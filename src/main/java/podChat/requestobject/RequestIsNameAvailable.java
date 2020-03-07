package podChat.requestobject;


public class RequestIsNameAvailable extends GeneralRequestObject {
    private String  uniqueName;

    RequestIsNameAvailable(Builder builder) {
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

        public RequestIsNameAvailable build() {
            return new RequestIsNameAvailable(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
