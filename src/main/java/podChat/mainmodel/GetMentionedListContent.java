package podChat.mainmodel;

/**
 * Created By F.Khojasteh on 2/1/2020
 */

public class GetMentionedListContent {
    private boolean unreadMentioned;
    private boolean allMentioned;
    private long count;
    private long offset;

    public GetMentionedListContent(boolean unreadMentioned, boolean allMentioned, long count, long offset) {
        this.unreadMentioned = unreadMentioned;
        this.allMentioned = allMentioned;
        this.count = count;
        this.offset = offset;
    }

    public void setUnreadMentioned(boolean unreadMentioned) {
        this.unreadMentioned = unreadMentioned;
    }

    public void setAllMentioned(boolean allMentioned) {
        this.allMentioned = allMentioned;
    }
}
