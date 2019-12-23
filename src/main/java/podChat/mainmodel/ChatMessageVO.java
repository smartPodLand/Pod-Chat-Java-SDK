package podChat.mainmodel;

import podChat.model.ReplyInfoVO;

public class ChatMessageVO {
    private Long id;
    private String uniqueId;
    private Long previousId;
    private String message;
    private int messageType;
    private boolean edited;
    private boolean editable;
    private boolean deletable;
    private ParticipantVO participant;
    private ConversationVO conversation;
    private Long time;
    private Integer timeNanos;
    private Boolean delivered;
    private Boolean seen;
    private String metadata;
    private String systemMetadata;
    private ReplyInfoVO replyInfoVO;
    private ForwardInfo forwardInfo;
    private boolean mentioned;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(Long previousId) {
        this.previousId = previousId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public ParticipantVO getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantVO participant) {
        this.participant = participant;
    }

    public ConversationVO getConversation() {
        return conversation;
    }

    public void setConversation(ConversationVO conversation) {
        this.conversation = conversation;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getTimeNanos() {
        return timeNanos;
    }

    public void setTimeNanos(Integer timeNanos) {
        this.timeNanos = timeNanos;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
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

    public boolean isMentioned() {
        return mentioned;
    }

    public void setMentioned(boolean mentioned) {
        this.mentioned = mentioned;
    }
}
