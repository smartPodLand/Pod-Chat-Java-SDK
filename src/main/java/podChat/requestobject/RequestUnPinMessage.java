package podChat.requestobject;


public class RequestUnPinMessage extends GeneralRequestObject {
    private Long messageId;

    RequestUnPinMessage(Builder builder) {
        super(builder);
        this.messageId = builder.messageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }


    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private Long messageId;

        public Builder(Long messageId) {
            this.messageId = messageId;
        }

        public RequestUnPinMessage build() {
            return new RequestUnPinMessage(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
