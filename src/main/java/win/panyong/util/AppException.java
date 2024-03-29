package win.panyong.util;

public class AppException extends RuntimeException {
    private Integer errorCode = 0;

    public AppException(String message) {
        super(message);
    }

    public AppException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
