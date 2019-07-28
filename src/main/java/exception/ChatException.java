package exception;

/**
 * Created By Khojasteh on 7/27/2019
 */
abstract class ChatException extends Exception {
    private String message ;
    private int code;

    public ChatException(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
