package br.com.company.logistics.project.common;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;

@ControllerAdvice
public class projectDriverAccountAdvice extends ResponseEntityExceptionHandler {

    public projectDriverAccountAdvice() {
    }

    @ExceptionHandler(DriverUuidInvalidException.class)
    public ResponseEntity<ErrorInfo> handleDriverUuidInvalidException(final HttpServletRequest request,
                                                                        final DriverUuidInvalidException exception) {
        logger.error("Handled error: " + exception.getClass().getSimpleName() + ", cause: " + exception.getMessage(),
            exception);
        return ResponseEntity.badRequest().body(new ErrorInfo(request.getRequestURL(), exception.getMessage()));
    }

    @ExceptionHandler(DriverAccountException.class)
    public ResponseEntity<ErrorInfo> handleDriverAccountException(final HttpServletRequest request,
                                                                  final DriverAccountException exception) {
        logger.error("Handled error: ", exception);
        return ResponseEntity.badRequest()
                .body(new ErrorInfo(request.getRequestURL(), exception.getLocalizedMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorInfo handleException(final HttpServletRequest req, final Exception exception) {
        logger.error("Uncaught exception: ", exception);
        return new ErrorInfo(req.getRequestURL(), exception.getLocalizedMessage());
    }

    @Getter
    static class ErrorInfo {
        private final String url;
        private final String message;

        ErrorInfo(final StringBuffer url, final String message) {
            this.url = url.toString();
            this.message = message;
        }
    }

}
