package br.com.company.logistics.project.common;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class Paginator<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long total;

    public <U> Paginator<U> map(final Function<? super T, ? extends U> converter) {
        return Paginator.of(content.stream().map(converter).collect(Collectors.toList()), page, size, total);
    }
}