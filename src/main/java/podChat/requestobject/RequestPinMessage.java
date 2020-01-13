package podChat.requestobject;


public class RequestPinMessage extends GeneralRequestObject {
    private Long messageId;
    private Boolean notifyAll;

    RequestPinMessage(Builder builder) {
        super(builder);
        this.messageId = builder.messageId;
        this.notifyAll = builder.notifyAll;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Boolean getNotifyAll() {
        return notifyAll;
    }

    public void setNotifyAll(Boolean notifyAll) {
        this.notifyAll = notifyAll;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private Long messageId;
        private Boolean notifyAll;

        public Builder(Long messageId) {
            this.messageId = messageId;
        }

        public Builder notifyAll(Boolean notifyAll) {
            this.notifyAll = notifyAll;
            return this;
        }

        public RequestPinMessage build() {
            return new RequestPinMessage(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
