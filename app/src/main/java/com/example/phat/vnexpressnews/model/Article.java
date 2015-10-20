package com.example.phat.vnexpressnews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Article extends BriefArticle {
    @JsonProperty(value = "site_id", required = false)
    private int siteId;
    private List<ReferenceArticle> listReference;
    @JsonProperty(value = "content", required = true)
    private String content;
    @JsonProperty(value = "photos", required = false)
    private List<Photo> photos;
    @JsonProperty(value = "mode_view", required = false)
    private int modeView;
    @JsonProperty(value = "cate_parent", required = true)
    private Category categoryParent;
    @JsonProperty(value = "list_video", required = false)
    private List<Video> videos;

    public Article() {
    }

    public Article(@JsonProperty("article_id") int articleId,
                    @JsonProperty("article_type") int articleType,
                    @JsonProperty("original_cate") int originalCategory,
                    @JsonProperty("title") String title,
                    @JsonProperty("lead") String lead,
                    @JsonProperty("share_url") String shareUrl,
                    @JsonProperty("thumbnail_url") String thumbnailUrl,
                    @JsonProperty("privacy") int privacy,
                    @JsonProperty("total_page") int totalPage,
                    @JsonProperty("total_comment") int totalComment,
                    @JsonProperty("publish_time") long publishTime,
                    @JsonProperty("site_id") int siteId,
                    @JsonProperty("content") String content,
                    @JsonProperty("photos") List<Photo> photos,
                    @JsonProperty("mode_view") int modeView,
                    @JsonProperty("cate_parent") Category categoryParent,
                    @JsonProperty("list_video") List<Video> videos) {

        super(articleId, articleType, originalCategory, title, lead, shareUrl, thumbnailUrl, privacy, totalPage, totalComment, publishTime);
        this.siteId = siteId;
        this.content = content;
        this.photos = photos;
        this.modeView = modeView;
        this.categoryParent = categoryParent;
        this.videos = videos;
    }

    // This version of constructor include list references
    public Article(int articleId,
                   int articleType,
                   int originalCategory,
                   String title,
                   String lead,
                   String shareUrl,
                   String thumbnailUrl,
                   int privacy,
                   int totalPage,
                   int totalComment,
                   long publishTime,
                   int siteId,
                   String content,
                   List<Photo> photos,
                   int modeView,
                   Category categoryParent,
                   List<Video> videos,
                   List<ReferenceArticle> listReference) {

        this(articleId, articleType, originalCategory, title, lead, shareUrl, thumbnailUrl,
                privacy, totalPage, totalComment, publishTime, siteId, content, photos, modeView,
                categoryParent, videos);
        this.listReference = listReference;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public List<ReferenceArticle> getListReference() {
        return listReference;
    }

    public void setListReference(List<ReferenceArticle> listReference) {
        this.listReference = listReference;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public int getModeView() {
        return modeView;
    }

    public void setModeView(int modeView) {
        this.modeView = modeView;
    }

    public Category getCategoryParent() {
        return categoryParent;
    }

    public void setCategoryParent(Category categoryParent) {
        this.categoryParent = categoryParent;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
