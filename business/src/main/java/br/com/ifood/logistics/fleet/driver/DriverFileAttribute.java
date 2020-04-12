package br.com.company.logistics.project.driver;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = {"name", "fileName"})
@RequiredArgsConstructor(staticName = "of")
public class DriverFileAttribute {

    @NonNull
    private final DriverAttributeName name;

    @NonNull
    private final String url;

    @NonNull
    private final String fileName;

}
