package com.example.phat.vnexpressnews.model;

import android.text.Html;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides some methods to help to create right type {@link Module}, convert, parse
 * other type to {@link Module} type
 */
public class ModuleHelper {

    private static final String PHOTO_HTML_CLASS = "tplCaption";
    private static final String PARAGRAPH_HTML_CLASS = "normal";
    private static final String EXTRACT_PARAGRAPH_HTML_CLASS = "tbl_insert";
    private static final String DIV_TAG = "div";
    private static final String TABLE_TAG = "table";
    private static final String PARAGRAPH_TAG = "p";

    private static final int INVALID_TYPE = -1;
    private static final int PHOTO_TYPE = 0;
    private static final int PARAGRAPH_TYPE = 1;
    private static final int EXTRACT_PARAGRAPH_TYPE = 2;

    /**
     * Create a right type {@link Module} from {@link Element} type
     */
    public static Module createModule(Element element) {
        int type = getType(element);
        switch (type) {
            case PARAGRAPH_TYPE:
                return new ParagraphModule(element);
            case PHOTO_TYPE:
                return new PhotoModule(element);
            case EXTRACT_PARAGRAPH_TYPE:
                return new ExtractParagraphModule(element);
        }

        return null;
    }

    private static boolean isPhoto(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Element argument can not be null");
        }

        return element.tagName().equals(TABLE_TAG) && element.hasClass(PHOTO_HTML_CLASS);
    }

    private static boolean isParagraph(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Element argument can not be null");
        }

        return element.tagName().equals(PARAGRAPH_TAG);
    }

    private static boolean isExtractParagraph(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Element argument can not be null");
        }

        return element.tagName().equals(TABLE_TAG) && element.hasClass(EXTRACT_PARAGRAPH_HTML_CLASS);
    }

    /**
     * Gets type of {@link Module} from provided {@link Element}
     */
    public static int getType(Element element) {
        if (isPhoto(element)) return PHOTO_TYPE;
        if (isParagraph(element)) return PARAGRAPH_TYPE;
        if (isExtractParagraph(element)) return EXTRACT_PARAGRAPH_TYPE;
        return INVALID_TYPE;
    }

    /**
     * Converts a {@link Element[]} to {@link List<Module>}
     */
    public static List<Module> convertFrom(final Element[] elementList) {
        ArrayList<Module> result = new ArrayList<>(elementList.length);

        for (Element e : elementList) {
            Module module = createModule(e);
            if (module != null) {
                result.add(module);
            }
        }

        return result;
    }

    /**
     * Converts a {@link Photo[]} to {@link List<PhotoModule>}
     */
    public static Module[] convertFrom(final Photo[] photoArr) {
        Module[] result = new Module[photoArr.length];

        for (int i = 0; i < photoArr.length; i++) {
            result[i] = new PhotoModule(photoArr[i].getThumbnailUrl(),
                    Html.fromHtml(photoArr[i].getCaption()));
        }

        return result;
    }

}
