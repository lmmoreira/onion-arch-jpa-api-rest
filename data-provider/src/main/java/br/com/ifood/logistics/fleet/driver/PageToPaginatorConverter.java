package br.com.company.logistics.project.driver;

import org.springframework.data.domain.Page;

import br.com.company.logistics.project.common.Paginator;

public class PageToPaginatorConverter {

    private PageToPaginatorConverter() {
    }

    public static <T> Paginator<T> convert(final Page<T> page) {
        return Paginator.of(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }
}