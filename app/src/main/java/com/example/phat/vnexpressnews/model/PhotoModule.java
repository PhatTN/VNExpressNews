package com.example.phat.vnexpressnews.model;

import android.text.Html;
import android.text.Spanned;

import com.example.phat.vnexpressnews.util.TextUtils;

import org.jsoup.nodes.Element;

public class PhotoModule extends Module {
    private String photoUrl;
    private Spanned photoCaption;

    public PhotoModule() {
        super(PHOTO_TYPE);
    }

    public PhotoModule(String photoUrl, Spanned photoCaption) {
        this();
        this.photoUrl = photoUrl;
        this.photoCaption = TextUtils.trimTrailingWhiteSpace(photoCaption);
    }

    public PhotoModule(Element element) {
        this();
        photoUrl = getPhotoUrl(element);
        photoCaption = getPhotoCaption(element);
    }

    private String getPhotoUrl(Element element) {
        Element image = element.select("img").first();
        return image.attr("src");
    }

    private Spanned getPhotoCaption(Element element) {
        Element caption = element.select("p.Image").first();
        if (caption != null) {
            Spanned result = Html.fromHtml(caption.html());
            // Trims trailing whitespace before return
            return TextUtils.trimTrailingWhiteSpace(result);
        } else {
            return null;
        }
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Spanned getPhotoCaption() {
        return photoCaption;
    }

    public void setPhotoCaption(Spanned photoCaption) {
        this.photoCaption = photoCaption;
    }
}
