package propofol.matchingservice.domain.exception;

public class NotFoundFileException extends RuntimeException{
    public NotFoundFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
