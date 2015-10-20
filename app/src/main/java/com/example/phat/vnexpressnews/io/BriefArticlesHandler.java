package com.example.phat.vnexpressnews.io;

import com.example.phat.vnexpressnews.exceptions.InternalServerErrorException;
import com.example.phat.vnexpressnews.exceptions.JacksonProcessingException;
import com.example.phat.vnexpressnews.model.BriefArticle;
import com.example.phat.vnexpressnews.util.JacksonUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGE;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

public class BriefArticlesHandler extends JSONHandler<Set<BriefArticle>> {
    private static final String TAG = makeLogTag(BriefArticlesHandler.class);

    public BriefArticlesHandler() {
        super(true); // set true to tell that this data should cache into DB
    }

    @Override
    public Set<BriefArticle> process(String jsonData)
            throws JacksonProcessingException, InternalServerErrorException {

        JsonNode rootNode = JacksonUtils.parse(jsonData); // try to parse the string to JsonNode type

        if (hasInternalServerError(rootNode)) {
            LOGE(TAG, "Something was wrong on server. Error code: " + mErrorCode);
            throw new InternalServerErrorException("Something was wrong on Server side. Error code: ", mErrorCode);
        }

        return parse(rootNode);
    }

    /**
     * Deserialize a {@link JsonNode} into {@link BriefArticle} set.
     * Check json format which is returned from server to understand this code clearly.
     */
    private Set<BriefArticle> parse(JsonNode rootNode) throws JacksonProcessingException {

        Iterator<JsonNode> data = rootNode.path(DATA).elements(); // unwrap data node and turn it into iterator
        Set<BriefArticle> articleSet = new LinkedHashSet<>(); // Needs to remain order of articles. So we use LinkedHashSet instead of HashSet.
        while (data.hasNext()) {
            Iterator<JsonNode> tempIterator = data.next().elements(); // unwrap each response part and turn it into iterator.

            while (tempIterator.hasNext()) {
                JsonParser parser = tempIterator.next().traverse();
                if (JacksonUtils.isStartArrayToken(parser)) { // Check in order to ensure least amount of errors occur whenever JSON format has been changed.
                    Collection<BriefArticle> tempCollection = JacksonUtils.parse(parser, List.class, BriefArticle.class);
                    articleSet.addAll(tempCollection);
                } else {
                    LOGD(TAG, "Maybe JSON format has been changed. Check SRS and update this code fit JSON format");
                }
            }
        }

        LOGI(TAG, articleSet.size() + " articles was returned");
        return articleSet;
    }

}
