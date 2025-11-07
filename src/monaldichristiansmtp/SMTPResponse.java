package monaldichristiansmtp;

public class SMTPResponse {
    private int code;
    private String message;
    private String rawResponse;

    public SMTPResponse(int code, String message, String rawResponse) {
        this.code = code;
        this.message = message;
        this.rawResponse = rawResponse;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    public boolean isError() {
        return code >= 400;
    }

    public boolean isIntermediate() {
        return code >= 300 && code < 400;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }
}