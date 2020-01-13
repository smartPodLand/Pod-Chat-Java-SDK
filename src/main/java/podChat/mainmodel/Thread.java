package podChat.mainmodel;

import java.util.List;

public class Thread {
    private Long id;
    private Long joinDate;
    private Participant inviter;
    private String title;
    private List<Participant> participants;
    private Long time;
    private String lastMessage;
    private String lastParticipantName;
    private Boolean group;
    private Long partner;
    private String lastParticipantImage;
    private Long unreadCount;
    private Long lastSeenMessageId;
    private Long lastSeenMessageTime;
    private Integer lastSeenMessageNanos;
    private MessageVO lastMessageVO;
    private PinMessageVO pinMessageVO;
    private Long partnerLastSeenMessageId;
    private Long partnerLastSeenMessageTime;
    private Integer partnerLastSeenMessageNanos;
    private Long partnerLastDeliveredMessageId;
    private Long partnerLastDeliveredMessageTime;
    private Integer partnerLastDeliveredMessageNanos;
    private Integer type;
    private String image;
    private String description;
    private String metadata;
    private Boolean mute;
    private Long participantCount;
    private Boolean canEditInfo;
    private Boolean canSpam;
    private Boolean admin;
    private Boolean mentioned;
    private Boolean pin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Long joinDate) {
        this.joinDate = joinDate;
    }

    public Participant getInviter() {
        return inviter;
    }

    public void setInviter(Participant inviter) {
        this.inviter = inviter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastParticipantName() {
        return lastParticipantName;
    }

    public void setLastParticipantName(String lastParticipantName) {
        this.lastParticipantName = lastParticipantName;
    }

    public Boolean getGroup() {
        return group;
    }

    public void setGroup(Boolean group) {
        this.group = group;
    }

    public Long getPartner() {
        return partner;
    }

    public void setPartner(Long partner) {
        this.partner = partner;
    }

    public String getLastParticipantImage() {
        return lastParticipantImage;
    }

    public void setLastParticipantImage(String lastParticipantImage) {
        this.lastParticipantImage = lastParticipantImage;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Long getLastSeenMessageId() {
        return lastSeenMessageId;
    }

    public void setLastSeenMessageId(Long lastSeenMessageId) {
        this.lastSeenMessageId = lastSeenMessageId;
    }

    public Long getLastSeenMessageTime() {
        return lastSeenMessageTime;
    }

    public void setLastSeenMessageTime(Long lastSeenMessageTime) {
        this.lastSeenMessageTime = lastSeenMessageTime;
    }

    public Integer getLastSeenMessageNanos() {
        return lastSeenMessageNanos;
    }

    public void setLastSeenMessageNanos(Integer lastSeenMessageNanos) {
        this.lastSeenMessageNanos = lastSeenMessageNanos;
    }

    public MessageVO getLastMessageVO() {
        return lastMessageVO;
    }

    public void setLastMessageVO(MessageVO lastMessageVO) {
        this.lastMessageVO = lastMessageVO;
    }

    public Long getPartnerLastSeenMessageId() {
        return partnerLastSeenMessageId;
    }

    public void setPartnerLastSeenMessageId(Long partnerLastSeenMessageId) {
        this.partnerLastSeenMessageId = partnerLastSeenMessageId;
    }

    public Long getPartnerLastSeenMessageTime() {
        return partnerLastSeenMessageTime;
    }

    public void setPartnerLastSeenMessageTime(Long partnerLastSeenMessageTime) {
        this.partnerLastSeenMessageTime = partnerLastSeenMessageTime;
    }

    public Integer getPartnerLastSeenMessageNanos() {
        return partnerLastSeenMessageNanos;
    }

    public void setPartnerLastSeenMessageNanos(Integer partnerLastSeenMessageNanos) {
        this.partnerLastSeenMessageNanos = partnerLastSeenMessageNanos;
    }

    public Long getPartnerLastDeliveredMessageId() {
        return partnerLastDeliveredMessageId;
    }

    public void setPartnerLastDeliveredMessageId(Long partnerLastDeliveredMessageId) {
        this.partnerLastDeliveredMessageId = partnerLastDeliveredMessageId;
    }

    public Long getPartnerLastDeliveredMessageTime() {
        return partnerLastDeliveredMessageTime;
    }

    public void setPartnerLastDeliveredMessageTime(Long partnerLastDeliveredMessageTime) {
        this.partnerLastDeliveredMessageTime = partnerLastDeliveredMessageTime;
    }

    public Integer getPartnerLastDeliveredMessageNanos() {
        return partnerLastDeliveredMessageNanos;
    }

    public void setPartnerLastDeliveredMessageNanos(Integer partnerLastDeliveredMessageNanos) {
        this.partnerLastDeliveredMessageNanos = partnerLastDeliveredMessageNanos;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Boolean getMute() {
        return mute;
    }

    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    public Long getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Long participantCount) {
        this.participantCount = participantCount;
    }

    public Boolean getCanEditInfo() {
        return canEditInfo;
    }

    public void setCanEditInfo(Boolean canEditInfo) {
        this.canEditInfo = canEditInfo;
    }

    public Boolean getCanSpam() {
        return canSpam;
    }

    public void setCanSpam(Boolean canSpam) {
        this.canSpam = canSpam;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getMentioned() {
        return mentioned;
    }

    public void setMentioned(Boolean mentioned) {
        this.mentioned = mentioned;
    }

    public Boolean getPin() {
        return pin;
    }

    public void setPin(Boolean pin) {
        this.pin = pin;
    }

    public PinMessageVO getPinMessageVO() {
        return pinMessageVO;
    }

    public void setPinMessageVO(PinMessageVO pinMessageVO) {
        this.pinMessageVO = pinMessageVO;
    }
}
