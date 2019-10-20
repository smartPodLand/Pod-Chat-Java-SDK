package podChat.requestobject;

/**
 * Created By Khojasteh on 8/26/2019
 */
public class RequestFileMessage {
    private long threadId;
    private String filePath;
    private String systemMetadata;
    private int messageType;
    private String description;
    private int xC;
    private int yC;
    private int hC;
    private int wC;

    RequestFileMessage(Builder builder) {
        this.setThreadId(builder.threadId);
        this.setFilePath(builder.filePath);
        this.setSystemMetadata(builder.systemMetadata);
        this.setMessageType(builder.messageType);
        this.setDescription(builder.description);
        this.setxC(builder.xC);
        this.setyC(builder.yC);
        this.sethC(builder.hC);
        this.setwC(builder.wC);
    }


    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getxC() {
        return xC;
    }

    public RequestFileMessage setxC(int xC) {
        this.xC = xC;
        return this;
    }

    public int getyC() {
        return yC;
    }

    public RequestFileMessage setyC(int yC) {
        this.yC = yC;
        return this;
    }

    public int gethC() {
        return hC;
    }

    public RequestFileMessage sethC(int hC) {
        this.hC = hC;
        return this;
    }

    public int getwC() {
        return wC;
    }

    public RequestFileMessage setwC(int wC) {
        this.wC = wC;
        return this;
    }

    public static class Builder {
        private long threadId;
        private String filePath;
        private String systemMetadata;
        private int messageType;
        private String description;
        private int xC;
        private int yC;
        private int hC;
        private int wC;

        public Builder(long threadId, String filePath) {
            this.threadId = threadId;
            this.filePath = filePath;
        }

        public Builder systemMetadata(String systemMetadata) {
            this.systemMetadata = systemMetadata;
            return this;
        }

        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
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

        public RequestFileMessage build() {
            return new RequestFileMessage(this);
        }
    }
}
