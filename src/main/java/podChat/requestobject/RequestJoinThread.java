package podChat.requestobject;


public class RequestJoinThread extends GeneralRequestObject {

    private String uniqueName;

    RequestJoinThread(Builder builder) {
        super(builder);
        this.uniqueName = builder.uniqueName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String uniqueName;

        public Builder(String uniqueName) {
            this.uniqueName = uniqueName;
        }

        public RequestJoinThread build() {
            return new RequestJoinThread(this);
        }


        @Override
        protected Builder self() {
            return this;
        }


    }


}
