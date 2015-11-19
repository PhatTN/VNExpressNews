package com.example.phat.vnexpressnews.model;

import android.text.Html;
import android.text.Spanned;

import org.jsoup.nodes.Element;

/**
 * The {@link ParagraphModule} as its name describes. It is used to handle a paragraph type.
 */
public class ParagraphModule extends Module {

    private Spanned paragraph;

    public ParagraphModule() {
        super(PARAGRAPH_TYPE);
    }

    public ParagraphModule(Spanned paragraph) {
        this();
        this.paragraph = paragraph;
    }

    public ParagraphModule(Element element) {
        this();
        this.paragraph = Html.fromHtml(element.html());
    }

    public Spanned getParagraph() {
        return paragraph;
    }

    public void setParagraph(Spanned paragraph) {
        this.paragraph = paragraph;
    }
}
