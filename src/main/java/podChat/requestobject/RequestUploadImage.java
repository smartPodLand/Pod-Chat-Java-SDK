package podChat.requestobject;


public class RequestUploadImage {

    private String filePath;

    RequestUploadImage(Builder builder) {
        this.filePath = builder.filePath;

    }

    public static class Builder {
        private String filePath;

        public Builder(String filePath) {
            this.filePath = filePath;
        }

        public RequestUploadImage build() {
            return new RequestUploadImage(this);
        }
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
