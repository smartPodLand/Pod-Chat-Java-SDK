package podChat.mainmodel;


import podChat.model.ConversationSummery;

public class ForwardInfo {

    private long id;
    private Participant participant;
    private Long participantId;
    private ConversationSummery conversation;
    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ConversationSummery getConversation() {
        return conversation;
    }

    public void setConversation(ConversationSummery conversation) {
        this.conversation = conversation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParticipantId() {
        return participantId;
    }


    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
