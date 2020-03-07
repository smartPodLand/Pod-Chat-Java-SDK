package podChat.requestobject;

import podChat.mainmodel.Invitee;
import podChat.mainmodel.RequestThreadInnerMessage;

import java.util.List;

public class RequestCreatePublicGroupOrChannelThread extends RequestCreateThread {
    private String uniqueName;

    RequestCreatePublicGroupOrChannelThread(Builder builder) {
        super(builder);
        this.uniqueName = builder.uniqueName;

    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public static class Builder extends RequestCreateThread.Builder<Builder> {
        private String uniqueName;

        public Builder(int type, List<Invitee> invitees, String uniqueName) {
            super(type, invitees);
            this.uniqueName = uniqueName;
        }

        @Override
        protected Builder self() {
            super.self();
            return this;
        }

        public RequestCreatePublicGroupOrChannelThread build() {
            return new RequestCreatePublicGroupOrChannelThread(this);
        }
    }
}
