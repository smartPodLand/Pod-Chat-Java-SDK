package podChat.requestobject;


import java.util.List;

public class RequestConnect extends BaseRequestObject {
    private List<String> uris;
    private String queueInput;
    private String queueOutput;
    private String queueUserName;
    private String queuePassword;
    private String severName;
    private String token;
    private String ssoHost;
    private String platformHost;
    private String fileServer;
    private Long chatId;


    public RequestConnect(Builder builder) {
        super(builder);
        this.uris = builder.uris;
        this.queueInput = builder.queueInput;
        this.queueOutput = builder.queueOutput;
        this.queueUserName = builder.queueUserName;
        this.queuePassword = builder.queuePassword;
        this.fileServer = builder.fileServer;
        this.platformHost = builder.platformHost;
        this.severName = builder.severName;
        this.token = builder.token;
        this.ssoHost = builder.ssoHost;
        this.chatId = builder.chatId;
    }


    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
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

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private List<String> uris;
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
        private Long chatId;


        public Builder(List<String> uris, String queueInput, String queueOutput,
                       String queueUserName, String queuePassword, String severName, String token,
                       String ssoHost, String platformHost, String fileServer, Long chatId) {
            this.queueInput = queueInput;
            this.queueOutput = queueOutput;
            this.uris = uris;
            this.queueUserName = queueUserName;
            this.queuePassword = queuePassword;
            this.fileServer = fileServer;
            this.platformHost = platformHost;
            this.severName = severName;
            this.token = token;
            this.ssoHost = ssoHost;
            this.chatId = chatId;
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
}
