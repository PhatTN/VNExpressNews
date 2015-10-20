package com.example.phat.vnexpressnews.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category implements Comparable<Category> {
    @JsonProperty(value = "category_id", required = true)
    private int categoryID;
    @JsonProperty(value = "catename", required = true)
    private String categoryName;
    @JsonProperty(value = "catecode", required = true)
    private String categoryCode;
    @JsonProperty(value = "parent_id", required = false)
    private int parentId;
    @JsonProperty(value = "full_parent", required = false)
    private int fullParent;
    @JsonProperty(value = "show_folder", required = false)
    private int showFolder;
    @JsonProperty(value = "display_order", required = false)
    private int displayOrder;

    public Category() {
    }

    public Category(@JsonProperty("category_id") int categoryID,
                    @JsonProperty("catename") String categoryName,
                    @JsonProperty("catecode") String categoryCode,
                    @JsonProperty("parent_id") int parentId,
                    @JsonProperty("full_parent") int fullParent,
                    @JsonProperty("show_folder") int showFolder,
                    @JsonProperty("display_order") int displayOrder) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
        this.parentId = parentId;
        this.fullParent = fullParent;
        this.showFolder = showFolder;
        this.displayOrder = displayOrder;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getFullParent() {
        return fullParent;
    }

    public void setFullParent(int fullParent) {
        this.fullParent = fullParent;
    }

    public int getShowFolder() {
        return showFolder;
    }

    public void setShowFolder(int showFolder) {
        this.showFolder = showFolder;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (categoryID != category.categoryID) return false;
        if (parentId != category.parentId) return false;
        if (fullParent != category.fullParent) return false;
        if (!categoryName.equals(category.categoryName)) return false;
        return categoryCode.equals(category.categoryCode);

    }

    @Override
    public int hashCode() {
        int result = categoryID;
        result = 31 * result + categoryName.hashCode();
        result = 31 * result + categoryCode.hashCode();
        result = 31 * result + parentId;
        result = 31 * result + fullParent;
        return result;
    }

    @Override
    public int compareTo(Category another) {
        return this.displayOrder - another.displayOrder; // ascending order
    }
}
