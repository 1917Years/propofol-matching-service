package propofol.matchingservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundBoardMemberException extends NoSuchElementException {
    public NotFoundBoardMemberException() {
        super();
    }

    public NotFoundBoardMemberException(String s) {
        super(s);
    }
}
