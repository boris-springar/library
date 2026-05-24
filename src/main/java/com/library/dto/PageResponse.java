package com.library.dto;

import java.util.List;

// Generic wrapper for paginated responses
public record PageResponse<T>(List<T> content, int page, int size, long totalElements) {
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long total) {
        return new PageResponse<>(content, page, size, total);
    }
}