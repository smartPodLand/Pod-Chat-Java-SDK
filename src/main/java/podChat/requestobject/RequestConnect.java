package podChat.requestobject;


public class RequestConnect extends BaseRequestObject {
    private String queueServer;
    private String queuePort;
    private String queueInput;
    private String queueOutput;
    private String queueUserName;
    private String queuePassword;
    private String severName;
    private String token;
    private String ssoHost;
    private String platformHost;
    private String fileServer;


    public RequestConnect(Builder builder) {
        super(builder);
        this.queueServer = builder.queueServer;
        this.queuePort = builder.queuePort;
        this.queueInput = builder.queueInput;
        this.queueOutput = builder.queueOutput;
        this.queueUserName = builder.queueUserName;
        this.queuePassword = builder.queuePassword;
        this.fileServer = builder.fileServer;
        this.platformHost = builder.platformHost;
        this.severName = builder.severName;
        this.token = builder.token;
        this.ssoHost = builder.ssoHost;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private String queueServer;
        private String queuePort;
        private String queueInput;
        private String queueOutput;
        private String queueUserName;
        private String queuePassword;
        private String severName;
        private String token;
        private String ssoHost;
        private String platformHost;
        private String fileServer;
        private String typeCode;


        public Builder(String queueServer, String queuePort, String queueInput, String queueOutput,
                       String queueUserName, String queuePassword, String severName, String token,
                       String ssoHost, String platformHost, String fileServer) {
            this.queueServer = queueServer;
            this.queuePort = queuePort;
            this.queueInput = queueInput;
            this.queueOutput = queueOutput;
            this.queuePort = queuePort;
            this.queueUserName = queueUserName;
            this.queuePassword = queuePassword;
            this.fileServer = fileServer;
            this.platformHost = platformHost;
            this.severName = severName;
            this.token = token;
            this.ssoHost = ssoHost;
        }


        public RequestConnect build() {
            return new RequestConnect(this);
        }

        public Builder typeCode(String typeCode) {
            this.typeCode = typeCode;
            return this;
        }


        @Override
        protected Builder self() {
            return this;
        }
    }


    public String getQueueServer() {
        return queueServer;
    }

    public void setQueueServer(String queueServer) {
        this.queueServer = queueServer;
    }

    public String getQueuePort() {
        return queuePort;
    }

    public void setQueuePort(String queuePort) {
        this.queuePort = queuePort;
    }

    public String getQueueInput() {
        return queueInput;
    }

    public void setQueueInput(String queueInput) {
        this.queueInput = queueInput;
    }

    public String getQueueOutput() {
        return queueOutput;
    }

    public void setQueueOutput(String queueOutput) {
        this.queueOutput = queueOutput;
    }

    public String getQueueUserName() {
        return queueUserName;
    }

    public void setQueueUserName(String queueUserName) {
        this.queueUserName = queueUserName;
    }

    public String getQueuePassword() {
        return queuePassword;
    }

    public void setQueuePassword(String queuePassword) {
        this.queuePassword = queuePassword;
    }

    public String getSeverName() {
        return severName;
    }

    public void setSeverName(String severName) {
        this.severName = severName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSsoHost() {
        return ssoHost;
    }

    public void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }

    public String getPlatformHost() {
        return platformHost;
    }

    public void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }
}
