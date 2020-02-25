package podChat.requestobject;

/**
 * Created By F.Khojasteh on 2/23/2020
 */

public class RequestUpdateProfile extends GeneralRequestObject {

    private String bio;
    private String metadata;

    RequestUpdateProfile(Builder builder) {
        super(builder);
        this.bio = builder.bio;
        this.metadata = builder.metadata;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String bio;
        private String metadata;


        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }


        public RequestUpdateProfile build() {
            return new RequestUpdateProfile(this);
        }


        @Override
        protected Builder self() {
            return this;
        }
    }
}
