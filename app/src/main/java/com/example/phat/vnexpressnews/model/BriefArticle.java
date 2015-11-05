package com.example.phat.vnexpressnews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BriefArticle {

    @JsonProperty(value = "article_id", required = true)
    private int articleId;
    @JsonProperty(value = "article_type")
    private int articleType;
    @JsonProperty(value = "original_cate")
    private int originalCategory;
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "lead", required = true)
    private String lead;
    @JsonProperty(value = "share_url")
    private String shareUrl;
    @JsonProperty(value = "thumbnail_url", required = true)
    private String thumbnailUrl;
    @JsonProperty(value = "privacy")
    private int privacy;
    @JsonProperty(value = "total_page")
    private int totalPage;
    @JsonProperty(value = "total_comment", required = true)
    private int totalComment;
    @JsonProperty(value = "publish_time", required = true)
    private long publishTime;

    public BriefArticle() {
    }

    public BriefArticle(@JsonProperty("article_id") int articleId,
                        @JsonProperty("article_type") int articleType,
                        @JsonProperty("original_cate") int originalCategory,
                        @JsonProperty("title") String title,
                        @JsonProperty("lead") String lead,
                        @JsonProperty("share_url") String shareUrl,
                        @JsonProperty("thumbnail_url") String thumbnailUrl,
                        @JsonProperty("privacy") int privacy,
                        @JsonProperty("total_page") int totalPage,
                        @JsonProperty("total_comment") int totalComment,
                        @JsonProperty("publish_time") long publishTime) {
        this.articleId = articleId;
        this.articleType = articleType;
        this.originalCategory = originalCategory;
        this.title = title;
        this.lead = lead;
        this.shareUrl = shareUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.privacy = privacy;
        this.totalPage = totalPage;
        this.totalComment = totalComment;
        this.publishTime = publishTime;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getArticleType() {
        return articleType;
    }

    public void setArticleType(int articleType) {
        this.articleType = articleType;
    }

    public int getOriginalCategory() {
        return originalCategory;
    }

    public void setOriginalCategory(int originalCategory) {
        this.originalCategory = originalCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BriefArticle that = (BriefArticle) o;

        return articleId == that.articleId && title.equals(that.title);

    }

    @Override
    public int hashCode() {
        int result = articleId;
        result = 31 * result + title.hashCode();
        return result;
    }
}
