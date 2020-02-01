package podChat.requestobject;

public class RequestMessage extends GeneralRequestObject {
    private String textMessage;
    private int messageType;
    private String jsonMetaData;
    private long threadId;

    RequestMessage(Builder builder) {
        super(builder);
        this.setTextMessage(builder.textMessage);
        this.setThreadId(builder.threadId);
        this.setMessageType(builder.messageType);
        this.setJsonMetaData(builder.jsonMetaData);
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getJsonMetaData() {
        return jsonMetaData;
    }

    public void setJsonMetaData(String jsonMetaData) {
        this.jsonMetaData = jsonMetaData;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String textMessage;
        private int messageType;
        private String jsonMetaData;
        private long threadId;

        public Builder(String textMessage, long threadId, int messageType) {
            this.textMessage = textMessage;
            this.threadId = threadId;
            this.messageType = messageType;
        }


        public Builder jsonMetaData(String jsonMetaData) {
            this.jsonMetaData = jsonMetaData;
            return this;
        }

        public RequestMessage build() {
            return new RequestMessage(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

    }


}
