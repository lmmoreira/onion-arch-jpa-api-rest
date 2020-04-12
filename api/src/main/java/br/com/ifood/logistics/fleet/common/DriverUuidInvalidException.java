package br.com.company.logistics.project.common;

public class DriverUuidInvalidException extends DriverAccountException {
    private static final long serialVersionUID = 1229313124420882588L;

    public DriverUuidInvalidException(final String message) {
        super(message);
    }
}