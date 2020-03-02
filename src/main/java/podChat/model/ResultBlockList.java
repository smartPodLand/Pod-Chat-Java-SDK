package podChat.model;


import java.util.List;

public class ResultBlockList {
    private List<BlockedUserVO> contacts;
    private long contentCount;

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

}
