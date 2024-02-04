package ariefsyaifu.dto.error;

public class ErrorOas {
    public String message;

    public static ErrorOas valueOf(String message) {
        ErrorOas oas = new ErrorOas();
        oas.message = message;
        return oas;
    }
}
