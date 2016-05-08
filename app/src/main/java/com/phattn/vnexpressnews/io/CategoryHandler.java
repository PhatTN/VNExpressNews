package com.phattn.vnexpressnews.io;

import com.phattn.vnexpressnews.exceptions.InternalServerErrorException;
import com.phattn.vnexpressnews.exceptions.JacksonProcessingException;
import com.phattn.vnexpressnews.model.Category;
import com.phattn.vnexpressnews.util.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static com.phattn.vnexpressnews.util.LogUtils.LOGE;
import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

public class CategoryHandler extends JSONHandler<List<Category>> {
    private static final String TAG = makeLogTag(CategoryHandler.class);

    public CategoryHandler() {
        super(SHOULD_BE_NOT_CACHED); // set false to indicate this should be not cached into DB
    }

    @Override
    public List<Category> process(String jsonData)
            throws JacksonProcessingException, InternalServerErrorException {

        JsonNode rootNode = JacksonUtils.parse(jsonData);

        if (hasInternalServerError(rootNode)) {
            LOGE(TAG, "Something was wrong on server. Error code: " + mErrorCode);
            throw new InternalServerErrorException("Something was wrong on Server side.", mErrorCode);
        }

        return parse(rootNode);
    }

    /**
     * Deserialize a {@link JsonNode} into {@link java.util.List<Category>}
     * @param node The {@link JsonNode} will be parsed
     * @return A parsed {@link List<Category>}
     * @throws JacksonProcessingException if have any problem occurs in processing
     */
    private List<Category> parse(JsonNode node) throws JacksonProcessingException {
        JsonNode categoryArrayNode = node.path(DATA); // unwrap data node

        if (!categoryArrayNode.isArray()) {
            return new ArrayList<>(0); // return empty array list when format is incorrect
        }
        // If it is array. We loop through it, parse each node into Category and add it to List
        List<Category> resultList = new ArrayList<>();
        for (JsonNode categoryNode : categoryArrayNode) {
            Category category = JacksonUtils.parse(categoryNode, Category.class);
            resultList.add(category);
        }

        return resultList;
    }
}
