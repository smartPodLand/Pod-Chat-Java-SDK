package podChat.requestobject;

/**
 * Created By Khojasteh on 8/25/2019
 */
public class RequestUploadFile {
    private String filePath;

    protected RequestUploadFile(Builder<?> builder) {
        filePath = builder.filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static class Builder<T extends Builder<T>> {

        private String filePath;

        public Builder(String filePath) {
            this.filePath = filePath;
        }

    /*    public T RequestUploadFile(StringBuilder filePath) {
            filePath = filePath;
            return (T) this;
        }*/

        public RequestUploadFile build() {
            return new RequestUploadFile(this);
        }
    }
}
