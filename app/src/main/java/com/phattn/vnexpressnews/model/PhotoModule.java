package com.phattn.vnexpressnews.model;

import android.os.Parcel;

import org.jsoup.nodes.Element;

public class PhotoModule extends Module {
    private String photoUrl;
    private String photoCaption;

    public PhotoModule() {
        super(PHOTO_TYPE);
    }

    public PhotoModule(String photoUrl, String photoCaption) {
        this();
        this.photoUrl = photoUrl;
        this.photoCaption = photoCaption;
    }

    public PhotoModule(Element element) {
        this();
        photoUrl = getPhotoUrl(element);
        photoCaption = getPhotoCaption(element);
    }

    public PhotoModule(Parcel in) {
        super(in);
        photoUrl = in.readString();
        photoCaption = in.readString();
    }

    public static final Creator<PhotoModule> CREATOR = new Creator<PhotoModule>() {
        @Override
        public PhotoModule createFromParcel(Parcel source) {
            return new PhotoModule(source);
        }

        @Override
        public PhotoModule[] newArray(int size) {
            return new PhotoModule[size];
        }
    };

    private String getPhotoUrl(Element element) {
        Element image = element.select("img").first();
        return image.attr("src");
    }

    private String getPhotoCaption(Element element) {
        Element caption = element.select("p.Image").first();
        if (caption != null) {
            return caption.html();
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

    public String getPhotoCaption() {
        return photoCaption;
    }

    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(photoUrl);
        dest.writeString(photoCaption);
    }
}
