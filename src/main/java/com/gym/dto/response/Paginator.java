package com.gym.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class Paginator<T> {
    private int pageSize;
    private int totalItems;
    private int currentPage;
    private int totalPages;
    private List<T> items;

    public Paginator(int totalItems, int currentPage, int pageSize, List<T> items) {
        this.totalItems = Math.max(totalItems, 0);
        this.pageSize = Math.max(pageSize, 1);
        this.totalPages = (int) Math.ceil((double) this.totalItems / this.pageSize);

        this.currentPage = Math.min(Math.max(currentPage, 1), Math.max(this.totalPages, 1));

        this.items = items;
    }

    public boolean isFirstPage() {
        return currentPage == 1;
    }

    public boolean isLastPage() {
        return currentPage == totalPages || totalPages == 0;
    }

    public boolean hasNextPage() {
        return currentPage < totalPages;
    }

    public boolean hasPreviousPage() {
        return currentPage > 1;
    }

    public int getNextPage() {
        return hasNextPage() ? currentPage + 1 : currentPage;
    }

    public int getPreviousPage() {
        return hasPreviousPage() ? currentPage - 1 : currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getStartItem() {
        return (currentPage - 1) * pageSize + 1;
    }

    public int getEndItem() {
        return Math.min(currentPage * pageSize, totalItems);
    }
}