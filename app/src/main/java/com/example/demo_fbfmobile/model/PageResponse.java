package com.example.demo_fbfmobile.model;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private Page page; // Thêm trường page

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}