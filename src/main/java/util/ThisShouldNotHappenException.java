package util;

/**
 * Created by root on 15.06.2017.
 */
public class ThisShouldNotHappenException extends RuntimeException {

    public ThisShouldNotHappenException() {
        super();
    }

    public ThisShouldNotHappenException(String message) {
        super(message);
    }

    public ThisShouldNotHappenException(Throwable cause) {
        super(cause);
    }

    public ThisShouldNotHappenException(String message, Throwable cause) {
        super(message, cause);
    }

}
