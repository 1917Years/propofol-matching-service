package propofol.matchingservice.domain.exception;

public class MailSendException extends RuntimeException{
    public MailSendException(String message) {
        super(message);
    }
}
