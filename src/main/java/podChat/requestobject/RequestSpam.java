package podChat.requestobject;


public class RequestSpam extends GeneralRequestObject {
    private long threadId;

    RequestSpam(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long threadId;

        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }


        public RequestSpam build() {
            return new RequestSpam(this);
        }


        @Override
        protected Builder self() {
            return this;
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
}
