package podChat.mainmodel;

import java.io.Serializable;

public class BlockAcount implements Serializable {
    private long contactId;
    private long threadId;

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
}
