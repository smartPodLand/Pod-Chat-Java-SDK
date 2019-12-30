package podChat.requestobject;


public class RequestPinThread extends GeneralRequestObject {
    private long threadId;

    RequestPinThread(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long threadId;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public RequestPinThread build() {
            return new RequestPinThread(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
