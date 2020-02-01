package podChat.requestobject;

import java.util.ArrayList;

public class RequestThread extends BaseRequestObject {

    private ArrayList<Integer> threadIds;
    private String threadName;
    private long creatorCoreUserId;
    private long partnerCoreUserId;
    private long partnerCoreContactId;
    private boolean New;

    RequestThread(Builder builder) {
        super(builder);
        this.threadIds = builder.threadIds;
        this.threadName = builder.threadName;
        this.creatorCoreUserId = builder.creatorCoreUserId;
        this.partnerCoreUserId = builder.partnerCoreUserId;
        this.partnerCoreContactId = builder.partnerCoreContactId;
        this.New = builder.New;
    }

    public ArrayList<Integer> getThreadIds() {
        return threadIds;
    }

    public void setThreadIds(ArrayList<Integer> threadIds) {
        this.threadIds = threadIds;
    }

    public long getCreatorCoreUserId() {
        return creatorCoreUserId;
    }

    public void setCreatorCoreUserId(long creatorCoreUserId) {
        this.creatorCoreUserId = creatorCoreUserId;
    }

    public long getPartnerCoreUserId() {
        return partnerCoreUserId;
    }

    public void setPartnerCoreUserId(long partnerCoreUserId) {
        this.partnerCoreUserId = partnerCoreUserId;
    }

    public long getPartnerCoreContactId() {
        return partnerCoreContactId;
    }

    public void setPartnerCoreContactId(long partnerCoreContactId) {
        this.partnerCoreContactId = partnerCoreContactId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isNew() {
        return New;
    }

    public void setNew(boolean aNew) {
        New = aNew;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private ArrayList<Integer> threadIds;
        private String threadName;
        private long creatorCoreUserId;
        private long partnerCoreUserId;
        private long partnerCoreContactId;
        private boolean New;


        public Builder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public Builder creatorCoreUserId(long creatorCoreUserId) {
            this.creatorCoreUserId = creatorCoreUserId;
            return this;
        }


        public Builder partnerCoreUserId(long partnerCoreUserId) {
            this.partnerCoreUserId = partnerCoreUserId;
            return this;
        }


        public Builder partnerCoreContactId(long partnerCoreContactId) {
            this.partnerCoreContactId = partnerCoreContactId;
            return this;
        }


        public Builder threadIds(ArrayList<Integer> threadIds) {
            this.threadIds = threadIds;
            return this;
        }

        public Builder New(boolean aNew) {
            this.New = aNew;
            return this;
        }

        public RequestThread build() {
            return new RequestThread(this);
        }


        @Override
        protected Builder self() {
            return this;
        }
    }
}
