package br.com.company.logistics.project.common;

public class CannotDeleteDriverIdentityException extends RuntimeException {
    public CannotDeleteDriverIdentityException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CannotDeleteDriverIdentityException(final String message) {
        super(message);
    }
}
