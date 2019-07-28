package podChat.mainmodel;


public class LastMessageVO {
    private long id;
    private String uniqueId;
    private String message;
    private boolean edited;
    private boolean editable;
    private long time;
    private Participant participant;
    private Long participantId;
    private ReplyInfoVO replyInfoVO;
    private Long replyInfoVOId;
    private ForwardInfo forwardInfo;
    private Long forwardInfoId;

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
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

    public ReplyInfoVO getReplyInfoVO() {
        return replyInfoVO;
    }

    public void setReplyInfoVO(ReplyInfoVO replyInfoVO) {
        this.replyInfoVO = replyInfoVO;
    }

    public ForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(ForwardInfo forwardInfo) {
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
}
