package podChat.requestobject;

import podChat.mainmodel.Invitee;

import java.util.List;

public class RequestCreateThread extends BaseRequestObject {

    private int type;
    private String ownerSsoId;
    private List<Invitee> invitees;
    private String title;
    private String description;
    private String image;
    private String metadata;

    RequestCreateThread(Builder<?> builder) {
        super(builder);
        this.type = builder.type;
        this.title = builder.title;
        this.invitees = builder.invitees;
        this.description = builder.description;
        this.image = builder.image;
        this.metadata = builder.metadata;
    }

    public String getOwnerSsoId() {
        return ownerSsoId;
    }

    public void setOwnerSsoId(String ownerSsoId) {
        this.ownerSsoId = ownerSsoId;
    }

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<Invitee> invitees) {
        this.invitees = invitees;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public static class Builder<T extends RequestCreateThread.Builder<T>> extends BaseRequestObject.Builder<Builder> {
        private final int type;
        private final List<Invitee> invitees;
        private String title;
        private String description;
        private String image;
        private String metadata;

        public Builder(int type, List<Invitee> invitees) {
            this.invitees = invitees;
            this.type = type;
        }

        public Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }


        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        @Override
        protected RequestCreateThread.Builder self() {
            return this;
        }


        public RequestCreateThread build() {
            return new RequestCreateThread(this);
        }
    }
}
