package gl.exercise.UserCrud.exceptions;

public class InvalidPasswordException extends InvalidDataException {
    public InvalidPasswordException() {
        super("Password not have the correct format, it should have at least an Uppercase, two numbers");
    }
}
