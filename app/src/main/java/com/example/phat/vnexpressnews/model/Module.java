package com.example.phat.vnexpressnews.model;

/**
 * An {@link Article} has many type. It has different content format in each type (Articles content
 * formats in HTML). It's hard to handle whole content. So, we split the content in to many small piece
 * based on HTML tag and format. Each piece will be called is module. And we can handle each module
 * more easier
 */
public class Module {
    public static final int PARAGRAPH_TYPE = 0;
    public static final int EXTRACT_PARAGRAPH_TYPE = 1;
    public static final int PHOTO_TYPE = 2;

    private int moduleType;

    public Module(int moduleType) {
        this.moduleType = moduleType;
    }

    public int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }
}
