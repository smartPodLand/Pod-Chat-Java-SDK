package podChat.requestobject;


public class RequestUnreadMessageCount extends GeneralRequestObject {

    private Boolean mute;

    RequestUnreadMessageCount(Builder builder) {
        super(builder);
        this.mute = builder.mute;
    }

    public Boolean getMute() {
        return mute;
    }

    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private Boolean mute;

        public Builder mute(Boolean mute) {
            this.mute = mute;
            return this;
        }

        public RequestUnreadMessageCount build() {
            return new RequestUnreadMessageCount(this);
        }


        @Override
        protected Builder self() {
            return this;
        }


    }


}
