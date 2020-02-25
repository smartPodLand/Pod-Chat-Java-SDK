package podChat.model;


import java.util.List;

public class ResultBlockList {
    private List<BlockedUserVO> contacts;
    private long contentCount;
    private boolean hasNext;
    private long nextOffset;

    public List<BlockedUserVO> getContacts() {
        return contacts;
    }

    public void setContacts(List<BlockedUserVO> contacts) {
        this.contacts = contacts;
    }

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(long nextOffset) {
        this.nextOffset = nextOffset;
    }
}
