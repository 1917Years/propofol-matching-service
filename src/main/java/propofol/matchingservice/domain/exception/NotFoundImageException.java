package propofol.matchingservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundImageException extends NoSuchElementException {
    public NotFoundImageException() {
        super();
    }

    public NotFoundImageException(String s) {
        super(s);
    }
}
