package podChat.requestobject;

public class RequestGetAdmin extends GeneralRequestObject {
    private long count;
    private long offset;
    private long threadId;

    RequestGetAdmin(Builder builder) {
        super(builder);
        this.count = builder.count;
        this.offset = builder.offset;
        this.threadId = builder.threadId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long count;
        private long offset;
        private long threadId;

        public Builder(long threadId) {
            this.threadId = threadId;
        }


        public Builder count(long count) {
            this.count = count;
            return this;
        }

        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        public RequestGetAdmin build() {
            return new RequestGetAdmin(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
