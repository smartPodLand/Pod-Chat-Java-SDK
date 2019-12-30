package podChat.requestobject;


public class RequestUploadImage extends RequestUploadFile {
    private int xC;
    private int yC;
    private int hC;
    private int wC;


    RequestUploadImage(Builder builder) {
        super(builder);
        this.xC = builder.xC;
        this.yC = builder.yC;
        this.hC = builder.hC;
        this.wC = builder.wC;

    }

    public int getxC() {
        return xC;
    }

    public RequestUploadImage setxC(int xC) {
        this.xC = xC;
        return this;
    }

    public int getyC() {
        return yC;
    }

    public RequestUploadImage setyC(int yC) {
        this.yC = yC;
        return this;
    }

    public int gethC() {
        return hC;
    }

    public RequestUploadImage sethC(int hC) {
        this.hC = hC;
        return this;
    }

    public int getwC() {
        return wC;
    }

    public RequestUploadImage setwC(int wC) {
        this.wC = wC;
        return this;
    }

    public static class Builder extends RequestUploadFile.Builder<Builder> {
        private int xC;
        private int yC;
        private int hC;
        private int wC;

        public Builder(String filePath) {
            super(filePath);
        }

        public Builder xC(int xC) {
            this.xC = xC;
            return this;
        }

        public Builder yC(int yC) {
            this.yC = yC;
            return this;
        }

        public Builder hC(int hC) {
            this.hC = hC;
            return this;
        }

        public Builder wC(int wC) {
            this.wC = wC;
            return this;
        }

        public RequestUploadImage build() {
            return new RequestUploadImage(this);
        }

    }

}
