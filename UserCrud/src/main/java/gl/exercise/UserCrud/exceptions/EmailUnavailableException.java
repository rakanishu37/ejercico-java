package gl.exercise.UserCrud.exceptions;

public class EmailUnavailableException extends InvalidDataException {
    public EmailUnavailableException() {
        super("Email is unavailable");
    }
}
