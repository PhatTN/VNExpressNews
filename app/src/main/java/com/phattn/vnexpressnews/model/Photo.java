package com.phattn.vnexpressnews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo implements Comparable<Photo> {
    @JsonProperty(value = "photo_id", required = true)
    private int photoId;
    @JsonProperty(value = "thumbnail_url", required = true)
    private String thumbnailUrl;
    @JsonProperty(value = "caption", required = true)
    private String caption;

    public Photo() {}

    public Photo(@JsonProperty("photo_id") int photoId,
                 @JsonProperty("thumbnail_url") String thumbnailUrl,
                 @JsonProperty("caption") String caption) {
        this.photoId = photoId;
        this.thumbnailUrl = thumbnailUrl;
        this.caption = caption;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public int compareTo(Photo another) {
        return this.photoId - another.photoId; // ascending order
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (photoId != photo.photoId) return false;
        if (thumbnailUrl != null ? !thumbnailUrl.equals(photo.thumbnailUrl) : photo.thumbnailUrl != null)
            return false;
        return !(caption != null ? !caption.equals(photo.caption) : photo.caption != null);

    }

    @Override
    public int hashCode() {
        int result = photoId;
        result = 31 * result + (thumbnailUrl != null ? thumbnailUrl.hashCode() : 0);
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        return result;
    }
}
