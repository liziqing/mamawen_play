package exception;

/**
 * Created by Ronald on 2015/3/24.
 */
public class IMServerException extends Exception{
    public IMServerException() {
    }

    public IMServerException(String message) {
        super(message);
    }

    public IMServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IMServerException(Throwable cause) {
        super(cause);
    }

    public IMServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
