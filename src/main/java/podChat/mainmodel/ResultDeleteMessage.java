package podChat.mainmodel;


public class ResultDeleteMessage {
    private long id;
    private int messageType;
    private boolean edited;
    private boolean editable;
    private boolean deletable;
    private boolean mentioned;
    private boolean pinned;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isMentioned() {
        return mentioned;
    }

    public void setMentioned(boolean mentioned) {
        this.mentioned = mentioned;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}