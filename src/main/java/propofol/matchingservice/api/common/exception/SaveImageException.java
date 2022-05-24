package propofol.matchingservice.api.common.exception;

import java.io.IOException;

public class SaveImageException extends RuntimeException {
    public SaveImageException() {
        super();
    }

    public SaveImageException(String message) {
        super(message);
    }

    public SaveImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveImageException(Throwable cause) {
        super(cause);
    }
}
