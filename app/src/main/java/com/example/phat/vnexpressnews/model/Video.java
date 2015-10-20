package com.example.phat.vnexpressnews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Video implements Comparable<Video> {
    @JsonProperty(value = "video_id", required = true)
    private int videoId;
    @JsonProperty(value = "url", required = true)
    private String url;
    @JsonProperty(value = "caption", required = true)
    private String caption;
    @JsonProperty(value = "description", required = true)
    private String description;
    @JsonProperty(value = "thumbnail_url", required = true)
    private String thumbnailUrl;
    @JsonProperty(value = "size_format",required = false)
    private Map<String, String> sizeFormat; // Map<Video_Definition, Link>

    public Video() {
    }

    public Video(@JsonProperty("video_id") int videoId,
                 @JsonProperty("url") String url,
                 @JsonProperty("caption") String caption,
                 @JsonProperty("description") String description,
                 @JsonProperty("thumbnail_url") String thumbnailUrl,
                 @JsonProperty("size_format") Map<String, String> sizeFormat) {
        this.videoId = videoId;
        this.url = url;
        this.caption = caption;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.sizeFormat = sizeFormat;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Map<String, String> getSizeFormat() {
        return sizeFormat;
    }

    public void setSizeFormat(Map<String, String> sizeFormat) {
        this.sizeFormat = sizeFormat;
    }

    @Override
    public int compareTo(Video another) {
        return this.videoId - another.videoId; // ascending order
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Video video = (Video) o;

        if (videoId != video.videoId) return false;
        if (!url.equals(video.url)) return false;
        if (caption != null ? !caption.equals(video.caption) : video.caption != null) return false;
        if (description != null ? !description.equals(video.description) : video.description != null)
            return false;
        return !(thumbnailUrl != null ? !thumbnailUrl.equals(video.thumbnailUrl) : video.thumbnailUrl != null);

    }

    @Override
    public int hashCode() {
        int result = videoId;
        result = 31 * result + url.hashCode();
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (thumbnailUrl != null ? thumbnailUrl.hashCode() : 0);
        return result;
    }
}
