package podChat.requestobject;


public class RequestCountUnreadMessage extends GeneralRequestObject {

    private boolean mute;

    RequestCountUnreadMessage(Builder builder) {
        super(builder);
        this.mute = builder.uniqueName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private boolean mute;

        public Builder(String uniqueName) {
            this.uniqueName = uniqueName;
        }

        public RequestCountUnreadMessage build() {
            return new RequestCountUnreadMessage(this);
        }


        @Override
        protected Builder self() {
            return this;
        }


    }


}
