package podChat.model;

public class ResultPinMessage {

    private long messageId;
    private String text;
    private boolean notifyAll;
    private long time;
    private PinSender sender;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public PinSender getSender() {
        return sender;
    }

    public void setSender(PinSender sender) {
        this.sender = sender;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isNotifyAll() {
        return notifyAll;
    }

    public void setNotifyAll(boolean notifyAll) {
        this.notifyAll = notifyAll;
    }
}
