package podChat.requestobject;

public class RequestReplyFileMessage extends GeneralRequestObject {

    private String messageContent;
    private long threadId;
    private long messageId;
    private String systemMetaData;
    private String filePath;
    private int messageType;
    private int xC;
    private int yC;
    private int hC;
    private int wC;


    RequestReplyFileMessage(Builder builder) {
        super(builder);
        this.systemMetaData = builder.systemMetaData;
        this.messageContent = builder.messageContent;
        this.threadId = builder.threadId;
        this.messageId = builder.messageId;
        this.filePath = builder.filePath;
        this.messageType = builder.messageType;
        this.xC = builder.xC;
        this.yC = builder.yC;
        this.hC = builder.hC;
        this.wC = builder.wC;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String messageContent;
        private long threadId;
        private long messageId;
        private String systemMetaData;
        private String filePath;
        private int messageType;
        private int xC;
        private int yC;
        private int hC;
        private int wC;


        public Builder(String messageContent, long threadId, long messageId, String filePath) {
            this.messageContent = messageContent;
            this.threadId = threadId;
            this.messageId = messageId;
            this.filePath = filePath;
        }

        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder systemMetaData(String systemMetaData) {
            this.systemMetaData = systemMetaData;
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

        public Builder wC(int wC) {
            this.wC = wC;
            return this;
        }

        public Builder hC(int hC) {
            this.hC = hC;
            return this;
        }

        public RequestReplyFileMessage build() {
            return new RequestReplyFileMessage(this);
        }


        @Override
        protected Builder self() {
            return this;
        }

    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSystemMetaData() {
        return systemMetaData;
    }

    public void setSystemMetaData(String systemMetaData) {
        this.systemMetaData = systemMetaData;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getxC() {
        return xC;
    }

    public RequestReplyFileMessage setxC(int xC) {
        this.xC = xC;
        return this;
    }

    public int getyC() {
        return yC;
    }

    public RequestReplyFileMessage setyC(int yC) {
        this.yC = yC;
        return this;
    }

    public int gethC() {
        return hC;
    }

    public RequestReplyFileMessage sethC(int hC) {
        this.hC = hC;
        return this;
    }

    public int getwC() {
        return wC;
    }

    public RequestReplyFileMessage setwC(int wC) {
        this.wC = wC;
        return this;
    }
}
