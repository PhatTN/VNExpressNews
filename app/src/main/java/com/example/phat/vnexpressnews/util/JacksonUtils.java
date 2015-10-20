package com.example.phat.vnexpressnews.util;

import android.text.TextUtils;

import com.example.phat.vnexpressnews.exceptions.JacksonProcessingException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.IllegalFormatConversionException;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGE;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

public class JacksonUtils {

    public static final String TAG = makeLogTag(JacksonUtils.class);

    /**
     * Parse a json node into object
     */
    public static <T> T parse(JsonNode node, Class<T> clazz) throws JacksonProcessingException{
        ObjectMapper om = new ObjectMapper();
        try {

            if (node == null) {
                LOGD(TAG, "Json node must be not null.");
                throw new NullPointerException("Can not parse a null json node.");
            }

            if (clazz == null) {
                LOGD(TAG, "Class type must be not null.");
                throw new IllegalStateException("Can not parse a json node into unknown class type.");
            }

            return om.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            LOGD(TAG, "Something was wrong when trying to parse a json node into object.");
            throw new JacksonProcessingException(e.getMessage(), e.getLocation(), e.getCause());
        }
    }

    /**
     * Parse a json parser to collection
     * @param parser deserialize data from this parser
     * @param collectionType The class extends from {@link java.util.Collection}
     * @param clazz A class indicates generic specification for collection
     * @return a collection with data which is deserialized from json parser
     */
    public static <T> Collection<T> parse(JsonParser parser, Class<? extends Collection> collectionType, Class<T> clazz)
            throws JacksonProcessingException{
        ObjectMapper om = new ObjectMapper();
        try {

            if (parser == null) {
                LOGE(TAG, "Can not parse from null JsonParser instance");
                throw new NullPointerException("JsonParser instance is null");
            }

            if (collectionType == null || clazz == null) {
                LOGE(TAG, "Is second parameter null: " + (collectionType == null) + " or Is third parameter null: " + (clazz == null));
                throw new IllegalStateException("The collectionType parameter or clazz type parameter might not have been indicated. ");
            }

            return om.readValue(parser, TypeFactory.defaultInstance().constructCollectionType(collectionType, clazz));
        } catch (JsonProcessingException e) {
            LOGE(TAG, "The input JSON structure does not match structure expected for result type.");
            throw new JacksonProcessingException(e.getMessage(), e.getLocation(), e.getCause());
        } catch (IOException e) {
            throw new JacksonProcessingException(e.getMessage(), parser.getCurrentLocation(), e.getCause());
        }
    }

    /**
     * Check json parser token is currently a start array token
     */
    public static boolean isStartArrayToken(JsonParser parser) throws JacksonProcessingException{
        try {
            if (parser == null) {
                LOGE(TAG, "JsonParser instance must not null");
                throw new NullPointerException("JsonParser instance is null");
            }

            return parser.nextToken() == JsonToken.START_ARRAY;
        } catch (JsonParseException e) {
            LOGE(TAG, "Something was wrong when trying to get next token in parser stream. Location is: " + parser.getCurrentLocation());
            throw new JacksonProcessingException(e.getMessage(), e.getLocation(), e.getCause());
        } catch (IOException e) {
            throw new JacksonProcessingException(e.getMessage(), parser.getCurrentLocation(), e.getCause());
        }

    }

    /**
     * This method tries to parse a string into {@link JsonNode}
     * @param jsonData The json string which will be parsed to {@link JsonNode}
     * @return Parsed {@link JsonNode} if success
     * @throws JacksonProcessingException if have any problem
     */
    public static JsonNode parse(String jsonData) throws JacksonProcessingException{
        if (TextUtils.isEmpty(jsonData)) {
            LOGE(TAG, "Can not parse null or empty string");
            throw new JacksonProcessingException("Can not parse into json node from null or empty string");
        }

        try {
            return new ObjectMapper().readTree(jsonData);
        } catch (JsonProcessingException e) {
            LOGD(TAG, "Can not parse json string to JsonNode using tree mode." +
                    " Message: " + e.getMessage() + ". Location: " + e.getLocation());
            throw new JacksonProcessingException(e.getMessage(), e.getLocation(), e.getCause());
        } catch (IOException e) {
            LOGD(TAG, "IOException occurs when trying to parse json string to JsonNode." +
                    " Message: " + e.getMessage() + ". Cause: " + e.getCause());
            throw new JacksonProcessingException(e.getMessage(), e.getCause());
        }
    }
}
