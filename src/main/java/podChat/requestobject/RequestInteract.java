package podChat.requestobject;


public class RequestInteract extends GeneralRequestObject {
    private long messageId;
    private String content;
    private String uniqueId;
    private String systemMetadata;
    private String metadata;
    private long repliedTo;

    RequestInteract(Builder builder) {
        super(builder);
        this.messageId = builder.messageId;
        this.content = builder.content;
        this.uniqueId = builder.uniqueId;
        this.systemMetadata = builder.systemMetadata;
        this.metadata = builder.metadata;
        this.repliedTo = builder.repliedTo;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public long getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(long repliedTo) {
        this.repliedTo = repliedTo;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long messageId;
        private String content;
        private String uniqueId;
        private String systemMetadata;
        private String metadata;
        private long repliedTo;

        public Builder(long messageId, String content) {
            this.messageId = messageId;
            this.content = content;
        }

        public Builder uniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }


        public Builder systemMetadata(String systemMetadata) {
            this.systemMetadata = systemMetadata;
            return this;
        }


        public Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder repliedTo(long repliedTo) {
            this.repliedTo = repliedTo;
            return this;
        }

        public RequestInteract build() {
            return new RequestInteract(this);
        }


        @Override
        protected Builder self() {
            return this;
        }
    }
}
