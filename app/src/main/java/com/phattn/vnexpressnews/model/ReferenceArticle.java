package com.phattn.vnexpressnews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is lightweight version of article, services for reference purpose.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceArticle implements Comparable<ReferenceArticle> {
    @JsonProperty(value = "article_id", required = true)
    private int articleId;
    @JsonProperty(value = "article_type")
    private int articleType;
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "share_url", required = false)
    private String shareUrl;
    @JsonProperty(value = "thumbnail_url", required = true)
    private String thumbnailUrl;

    public ReferenceArticle() {}

    public ReferenceArticle(@JsonProperty("article_id") int articleId,
                            @JsonProperty("article_type") int articleType,
                            @JsonProperty("title") String title,
                            @JsonProperty("share_url") String shareUrl,
                            @JsonProperty("thumbnail_url") String thumbnailUrl) {
        this.articleId = articleId;
        this.articleType = articleType;
        this.title = title;
        this.shareUrl = shareUrl;
        this.thumbnailUrl = thumbnailUrl;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public int compareTo(ReferenceArticle another) {
        return this.articleId - another.articleId; // ascending order
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceArticle that = (ReferenceArticle) o;

        if (articleId != that.articleId) return false;
        if (articleType != that.articleType) return false;
        if (!title.equals(that.title)) return false;
        if (shareUrl != null ? !shareUrl.equals(that.shareUrl) : that.shareUrl != null)
            return false;
        return !(thumbnailUrl != null ? !thumbnailUrl.equals(that.thumbnailUrl) : that.thumbnailUrl != null);

    }

    @Override
    public int hashCode() {
        int result = articleId;
        result = 31 * result + articleType;
        result = 31 * result + title.hashCode();
        result = 31 * result + (shareUrl != null ? shareUrl.hashCode() : 0);
        result = 31 * result + (thumbnailUrl != null ? thumbnailUrl.hashCode() : 0);
        return result;
    }
}
