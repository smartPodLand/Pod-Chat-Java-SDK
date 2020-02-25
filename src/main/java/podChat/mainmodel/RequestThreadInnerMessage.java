package podChat.mainmodel;

import java.util.List;

public class RequestThreadInnerMessage {

    private String text;
    private String metadata;
    private String systemMetadata;
    private List<Long> forwardedMessageIds;

    public RequestThreadInnerMessage(Builder builder) {
        this.text = builder.text;
        this.metadata = builder.metadata;
        this.systemMetadata = builder.systemMetadata;
        this.forwardedMessageIds = builder.forwardedMessageIds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public List<Long> getForwardedMessageIds() {
        return forwardedMessageIds;
    }

    public void setForwardedMessageIds(List<Long> forwardedMessageIds) {
        this.forwardedMessageIds = forwardedMessageIds;
    }

    public static class Builder {
        private String text;
        private String metadata;
        private String systemMetadata;
        private List<Long> forwardedMessageIds;

        public Builder() {
        }

        public Builder(String text) {
            this.text = text;
        }


        public Builder message(String text) {
            this.text = text;
            return this;
        }

        public Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder systemMetadata(String systemMetadata) {
            this.systemMetadata = systemMetadata;
            return this;
        }

        public Builder forwardedMessageIds(List<Long> forwardedMessageIds) {
            this.forwardedMessageIds = forwardedMessageIds;
            return this;
        }

        public RequestThreadInnerMessage build() {
            return new RequestThreadInnerMessage(this);
        }

    }
}
