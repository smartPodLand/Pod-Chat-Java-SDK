package podChat.cachemodel;

import podChat.model.ConversationSummery;

public class CacheForwardInfo {

    //This field is just for using cache
    private long id;

    private CacheParticipant participant;

    private Long participantId;


    private ConversationSummery conversation;

    private long conversationId;

    public CacheParticipant getParticipant() {
        return participant;
    }


    public void setParticipant(CacheParticipant participant) {
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

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }
}
