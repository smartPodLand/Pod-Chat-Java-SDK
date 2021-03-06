package config;

/**
 * Created By Khojasteh on 8/24/2019
 */
public class QueueConfigVO {

    private String queueServer;
    private String queuePort;
    private String queueInput;
    private String queueOutput;
    private String queueUserName;
    private String queuePassword;
    private int queueReconnectTime;

    public QueueConfigVO(String queueServer, String queuePort, String queueInput, String queueOutput, String queueUserName, String queuePassword) {
        this.queueServer = queueServer;
        this.queuePort = queuePort;
        this.queueInput = queueInput;
        this.queueOutput = queueOutput;
        this.queueUserName = queueUserName;
        this.queuePassword = queuePassword;
        this.queueReconnectTime = 20000;
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

    public int getQueueReconnectTime() {
        return queueReconnectTime;
    }

    public void setQueueReconnectTime(int queueReconnectTime) {
        this.queueReconnectTime = queueReconnectTime;
    }

}
