package podChat.model;

public class OutPutThread{
    private ResultThread result;
    private boolean hasError;
    private String errorMessage;
    private long errorCode;

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }
    public ResultThread getResult() {
        return result;
    }

    public void setResult(ResultThread result) {
        this.result = result;
    }
}
