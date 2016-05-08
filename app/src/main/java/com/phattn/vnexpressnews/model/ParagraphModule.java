package com.phattn.vnexpressnews.model;

import android.os.Parcel;

import org.jsoup.nodes.Element;

/**
 * The {@link ParagraphModule} as its name describes. It is used to handle a paragraph type.
 */
public class ParagraphModule extends Module {

    private String paragraph;

    public ParagraphModule() {
        super(PARAGRAPH_TYPE);
    }

    public ParagraphModule(String paragraph) {
        this();
        this.paragraph = paragraph;
    }

    public ParagraphModule(Element element) {
        this();
        this.paragraph = element.html();
    }

    public ParagraphModule(Parcel in) {
        super(in);
        paragraph = in.readString();
    }

    public static final Creator<ParagraphModule> CREATOR = new Creator<ParagraphModule>() {
        @Override
        public ParagraphModule createFromParcel(Parcel source) {
            return new ParagraphModule(source);
        }

        @Override
        public ParagraphModule[] newArray(int size) {
            return new ParagraphModule[size];
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
