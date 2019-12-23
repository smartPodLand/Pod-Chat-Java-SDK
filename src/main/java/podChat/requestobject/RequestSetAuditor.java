package podChat.requestobject;

import java.util.ArrayList;

public class RequestSetAuditor extends GeneralRequestObject {

    private long threadId;
    private ArrayList<RequestRole> roles;

    private RequestSetAuditor(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
        this.roles = builder.roles;
    }

    public ArrayList<RequestRole> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<RequestRole> roles) {
        this.roles = roles;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long threadId;
        private ArrayList<RequestRole> roles;

        public Builder(long threadId, ArrayList<RequestRole> roles) {
            this.threadId = threadId;
            this.roles = roles;
        }

        public RequestSetAuditor build() {
            return new RequestSetAuditor(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }


}
