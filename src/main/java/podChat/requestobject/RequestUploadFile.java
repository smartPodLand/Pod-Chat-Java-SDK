package podChat.requestobject;

/**
 * Created By Khojasteh on 8/25/2019
 */
public class RequestUploadFile {
    private String filePath;

    RequestUploadFile(Builder builder) {
        this.filePath = builder.filePath;
    }

    public static class Builder {
        private String filePath;

        public Builder(String filePath) {
            this.filePath = filePath;
        }

        public RequestUploadFile build() {
            return new RequestUploadFile(this);
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
