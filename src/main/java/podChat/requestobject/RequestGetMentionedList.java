package podChat.requestobject;


public class RequestGetMentionedList extends BaseRequestObject {
    private long threadId;
    private boolean unreadMentioned;
    private boolean allMentioned;


    RequestGetMentionedList(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
        this.allMentioned = builder.allMentioned;
        this.unreadMentioned = builder.unreadMentioned;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public boolean isUnreadMentioned() {
        return unreadMentioned;
    }

    public void setUnreadMentioned(boolean unreadMentioned) {
        this.unreadMentioned = unreadMentioned;
    }

    public boolean isAllMentioned() {
        return allMentioned;
    }

    public void setAllMentioned(boolean allMentioned) {
        this.allMentioned = allMentioned;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private long threadId;
        private boolean unreadMentioned;
        private boolean allMentioned;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public Builder unreadMentioned(boolean unreadMentioned) {
            this.unreadMentioned = unreadMentioned;
            return this;
        }


        public Builder allMentioned(boolean allMentioned) {
            this.allMentioned = allMentioned;
            return this;
        }


        public RequestGetMentionedList build() {
            return new RequestGetMentionedList(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
