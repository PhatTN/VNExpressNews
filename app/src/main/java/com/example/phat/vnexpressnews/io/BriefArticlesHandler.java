package com.example.phat.vnexpressnews.io;

import com.example.phat.vnexpressnews.exceptions.InternalServerErrorException;
import com.example.phat.vnexpressnews.exceptions.JacksonProcessingException;
import com.example.phat.vnexpressnews.model.BriefArticle;
import com.example.phat.vnexpressnews.util.JacksonUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGE;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This class deserialize a json string into {@link java.util.Collection<BriefArticle>}.
 * Since a json string has many format depend on where it was returned.
 * So, when we instance a {@code BriefArticlesHandler}, we have to specify {@code HandlerType}
 * for it in order to it can deserialize json string correctly
 */
public class BriefArticlesHandler extends JSONHandler<Set<BriefArticle>> {
    private static final String TAG = makeLogTag(BriefArticlesHandler.class);

    private HandlerType mHandlerType;

    public enum HandlerType{
        TOP_NEWS, NEWS_WITH_CATEGORY
    }

    public BriefArticlesHandler(HandlerType handlerType) {
        super(SHOULD_BE_NOT_CACHED); // set true to tell that this data should cache into DB
        mHandlerType = handlerType;
    }

    @Override
    public Set<BriefArticle> process(String jsonData)
            throws JacksonProcessingException, InternalServerErrorException {

        if (mHandlerType == null) {
            LOGE(TAG, "Have not indicated handler type.");
            throw new IllegalStateException("Must set handler type before processing Json Data");
        }

        JsonNode rootNode = JacksonUtils.parse(jsonData); // try to parse the string to JsonNode type

        if (hasInternalServerError(rootNode)) {
            LOGE(TAG, "Something was wrong on server. Error code: " + mErrorCode);
            throw new InternalServerErrorException("Something was wrong on Server side.", mErrorCode);
        }

        return parse(rootNode);
    }

    /**
     * Deserialize a {@link JsonNode} into {@link BriefArticle} set.
     * Check json format which is returned from server to understand this code clearly.
     */
    private Set<BriefArticle> parse(JsonNode rootNode) throws JacksonProcessingException {
        // Needs to remain order of articles. So we use LinkedHashSet instead of HashSet.
        Set<BriefArticle> articleSet = new LinkedHashSet<>();
        BriefArticlesDeserializer mDeserializer;
        JsonNode dataNode = rootNode.path(DATA); // unwrap data node

        if (mHandlerType == HandlerType.TOP_NEWS) {
            mDeserializer = new TopNewsDeserializer();
        } else if (mHandlerType == HandlerType.NEWS_WITH_CATEGORY) {
            mDeserializer = new NewsWithCategoryDeserializer();
        } else {
            throw new IllegalStateException("Must set handler type before processing Json Data");
        }

        articleSet.addAll(mDeserializer.deserialize(dataNode));

        LOGI(TAG, articleSet.size() + " articles was returned");
        return articleSet;
    }

    public void setHandlerType(HandlerType handlerType) {
        mHandlerType = handlerType;
    }

    public HandlerType getHandlerType() {
        return mHandlerType;
    }

    private class TopNewsDeserializer implements BriefArticlesDeserializer {

        @Override
        public Set<BriefArticle> deserialize(JsonNode node) throws JacksonProcessingException{
            Set<BriefArticle> set = new LinkedHashSet<>();

            Iterator<JsonNode> data = node.elements();
            while (data.hasNext()) {
                Iterator<JsonNode> tempIterator = data.next().elements(); // unwrap each response part and turn it into iterator.

                while (tempIterator.hasNext()) {
                    JsonParser parser = tempIterator.next().traverse();
                    if (JacksonUtils.isStartArrayToken(parser)) { // Check in order to ensure least amount of errors occur whenever JSON format has been changed.
                        set.addAll(JacksonUtils.parse(parser, List.class, BriefArticle.class));
                    } else {
                        LOGD(TAG, "Maybe JSON format has been changed. Check SRS and update this code fit JSON format");
                    }
                }
            }
            return set;
        }
    }

    private class NewsWithCategoryDeserializer implements BriefArticlesDeserializer {

        @Override
        public Set<BriefArticle> deserialize(JsonNode node) throws JacksonProcessingException {
            Set<BriefArticle> set = new LinkedHashSet<>();

            Iterator<JsonNode> data = node.elements();
            while (data.hasNext()) {
                JsonParser parser = data.next().traverse();
                if (JacksonUtils.isStartArrayToken(parser)) {
                    set.addAll(JacksonUtils.parse(parser, List.class, BriefArticle.class));
                } else {
                    LOGD(TAG, "Maybe JSON format has been changed. Check SRS and update this code fit JSON format");
                }
            }
            return set;
        }
    }

    /**
     * Implements this interface to handle specific JsonNode format.
     */
    public interface BriefArticlesDeserializer {
        Set<BriefArticle> deserialize(JsonNode node) throws JacksonProcessingException;
    }
}
