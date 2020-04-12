package br.com.company.logistics.project.common;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class PaginatorResponse<T> {
    private final List<T> content;
    private final Pageable pageable;
    private final long total;

    public static <T> PaginatorResponse<T> of(final Paginator<T> paginator) {
        return of(paginator.getContent(), Pageable.of(paginator.getPage(), paginator.getSize()), paginator.getTotal());
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Pageable {
        private final int page;
        private final int size;
    }
}
