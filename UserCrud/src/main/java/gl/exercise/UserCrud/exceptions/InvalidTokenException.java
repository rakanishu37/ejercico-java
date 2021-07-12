package gl.exercise.UserCrud.exceptions;

public class InvalidTokenException extends InvalidDataException{

    public InvalidTokenException() {
        super("Invalid token");
    }
}
