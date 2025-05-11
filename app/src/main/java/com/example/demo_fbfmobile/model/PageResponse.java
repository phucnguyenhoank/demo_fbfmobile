package com.example.demo_fbfmobile.model;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    // có thể map thêm các trường khác nếu cần

    public List<T> getContent() {
        return content;
    }
}

