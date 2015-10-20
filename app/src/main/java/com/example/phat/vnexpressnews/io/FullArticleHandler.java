package com.example.phat.vnexpressnews.io;


import com.example.phat.vnexpressnews.exceptions.InternalServerErrorException;
import com.example.phat.vnexpressnews.exceptions.JacksonProcessingException;
import com.example.phat.vnexpressnews.model.Article;
import com.example.phat.vnexpressnews.model.ReferenceArticle;
import com.example.phat.vnexpressnews.util.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGE;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

public class FullArticleHandler extends JSONHandler<Article> {
    private static final String TAG = makeLogTag(FullArticleHandler.class);

    public FullArticleHandler() {
        super(SHOULD_BE_NOT_CACHED); // set false to indicate this should be not cached into DB
    }

    @Override
    public Article process(String jsonData)
            throws JacksonProcessingException, InternalServerErrorException {

        JsonNode rootNode = JacksonUtils.parse(jsonData); // try to parse the string into JsonNode type

        if (hasInternalServerError(rootNode)) {
            LOGE(TAG, "Something was wrong on server. Error code: " + mErrorCode);
            throw new InternalServerErrorException("Something was wrong on Server side.", mErrorCode);
        }
        return parse(rootNode);
    }

    /**
     * Deserialize a {@link JsonNode} into {@link Article}.
     * @param node The {@link JsonNode} will be parsed
     * @return A parsed {@link Article}
     * @throws JacksonProcessingException if have any problem occurs in processing
     */
    private Article parse(JsonNode node) throws JacksonProcessingException {
        final String LIST_REFERENCE = "list_reference";
        final String ARTICLE = "article";


        JsonNode articleNode = node.path(DATA); // unwrap data node

        Article article = JacksonUtils.parse(articleNode, Article.class);

        if (article == null) {
            LOGI(TAG, "Failure when trying to parse JsonNode type to a Article type.");
            return null;
        }

        LOGI(TAG, "Success when trying to parse JsonNode type to a Article type." +
                " Article title is: " + article.getTitle());

        // Because list_reference node is difficult in order to auto parsed by Jackson
        // So we'll do it by hand
        // First, we unwrap list_reference node, continue unwrap article node inside list_reference node and turn it into iterator
        JsonNode listReferenceArticleNode = articleNode.path(LIST_REFERENCE).path(ARTICLE);

        // Second, checks if it is array. Continue parsing it
        if (listReferenceArticleNode.isArray()) {
            List<ReferenceArticle> referenceArticles = new ArrayList<>();
            // Loop through array. Parse each node into ReferenceArticle and add to list
            for (JsonNode aListReferenceArticleNode : listReferenceArticleNode) {
                ReferenceArticle ra = JacksonUtils.parse(aListReferenceArticleNode, ReferenceArticle.class);
                referenceArticles.add(ra);
            }
            // Finally, set reference article list to article
            if (referenceArticles.size() > 0) {
                article.setListReference(referenceArticles);
            }
        }

        return article;
    }
}
