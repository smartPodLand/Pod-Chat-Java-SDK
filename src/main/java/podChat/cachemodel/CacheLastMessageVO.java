package podChat.cachemodel;
public class CacheLastMessageVO {

    private long id;
    private String uniqueId;
    private String message;
    private boolean edited;
    private boolean editable;
    private boolean delivered;
    private boolean seen;
    private boolean deletable;
    private long time;

    private CacheParticipant participant;

    private Long participantId;

    private CacheReplyInfoVO replyInfoVO;

    private Long replyInfoVOId;

    private CacheForwardInfo forwardInfo;

    private Long forwardInfoId;

    public CacheParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(CacheParticipant participant) {
        this.participant = participant;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CacheReplyInfoVO getReplyInfoVO() {
        return replyInfoVO;
    }

    public void setReplyInfoVO(CacheReplyInfoVO replyInfoVO) {
        this.replyInfoVO = replyInfoVO;
    }

    public CacheForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(CacheForwardInfo forwardInfo) {
        this.forwardInfo = forwardInfo;
    }


    public Long getParticipantId() {
        return participantId;
    }


    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }


    public Long getReplyInfoVOId() {
        return replyInfoVOId;
    }


    public void setReplyInfoVOId(Long replyInfoVOId) {
        this.replyInfoVOId = replyInfoVOId;
    }

    public Long getForwardInfoId() {
        return forwardInfoId;
    }

    public void setForwardInfoId(Long forwardInfoId) {
        this.forwardInfoId = forwardInfoId;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
}
