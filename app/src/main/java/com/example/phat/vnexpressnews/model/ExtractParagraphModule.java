package com.example.phat.vnexpressnews.model;

import android.text.Html;
import android.text.Spanned;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A component of {@link Article}s content. It's used to provide more information of content
 *
 * An {@link ExtractParagraphModule} has same properties as {@link ParagraphModule},
 * but it has different behavior and will display to user differently
 */
public class ExtractParagraphModule extends Module {

    private Spanned paragraph;

    public ExtractParagraphModule() {
        super(EXTRACT_PARAGRAPH_TYPE);
    }

    public ExtractParagraphModule(Spanned paragraph) {
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
        paragraph = Html.fromHtml(builder.toString());
    }

    public Spanned getParagraph() {
        return paragraph;
    }

    public void setParagraph(Spanned paragraph) {
        this.paragraph = paragraph;
    }
}
