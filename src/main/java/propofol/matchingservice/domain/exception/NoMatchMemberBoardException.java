package propofol.matchingservice.domain.exception;

public class NoMatchMemberBoardException extends IllegalStateException{
    public NoMatchMemberBoardException() {
        super();
    }

    public NoMatchMemberBoardException(String s) {
        super(s);
    }

    public NoMatchMemberBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchMemberBoardException(Throwable cause) {
        super(cause);
    }
}
