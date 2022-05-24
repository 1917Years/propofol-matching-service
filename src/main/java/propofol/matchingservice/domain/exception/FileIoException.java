package propofol.matchingservice.domain.exception;

public class FileIoException extends RuntimeException{
    public FileIoException(String message, Throwable cause) {
        super(message, cause);
    }
}
