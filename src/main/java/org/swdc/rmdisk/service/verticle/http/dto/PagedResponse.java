package org.swdc.rmdisk.service.verticle.http.dto;

import java.util.List;

public class PagedResponse<T> {

    private int page;
    private int size;
    private long totalElements;
    private List<T> content;

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public List<T> getContent() {
        return content;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
