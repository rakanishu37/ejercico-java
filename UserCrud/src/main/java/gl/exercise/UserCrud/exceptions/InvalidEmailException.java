package gl.exercise.UserCrud.exceptions;

public class InvalidEmailException extends InvalidDataException {
    public InvalidEmailException(String email) {
        super(String.format("Email %s does not have the correct format", email));
    }
}
