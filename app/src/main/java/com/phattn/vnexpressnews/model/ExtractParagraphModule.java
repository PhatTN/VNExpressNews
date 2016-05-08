package com.phattn.vnexpressnews.model;

import android.os.Parcel;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A component of {@link Article}s content. It's used to provide more information of content
 *
 * An {@link ExtractParagraphModule} has same properties as {@link ParagraphModule},
 * but it has different behavior and will display to user differently
 */
public class ExtractParagraphModule extends Module {

    private String paragraph;

    public ExtractParagraphModule() {
        super(EXTRACT_PARAGRAPH_TYPE);
    }

    public ExtractParagraphModule(String paragraph) {
        this();
        this.paragraph = paragraph;
    }

    public ExtractParagraphModule(Element element) {
        this();
        StringBuilder builder = new StringBuilder();
        Elements elements = element.select("tbody>tr");
        int size = elements.size();
        for (int i = 0; i < size; i++) {
            builder.append(elements.get(i).toString());
            if (i < size - 1) { // Do not add linebreak to last element
                builder.append("\n\n");
            }
        }
        paragraph = builder.toString();
    }

    protected ExtractParagraphModule(Parcel in) {
        super(in);
        paragraph = in.readString();
    }

    public static final Creator<ExtractParagraphModule> CREATOR = new Creator<ExtractParagraphModule>() {
        @Override
        public ExtractParagraphModule createFromParcel(Parcel in) {
            return new ExtractParagraphModule(in);
        }

        @Override
        public ExtractParagraphModule[] newArray(int size) {
            return new ExtractParagraphModule[size];
        }
    };

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(paragraph);
    }
}
