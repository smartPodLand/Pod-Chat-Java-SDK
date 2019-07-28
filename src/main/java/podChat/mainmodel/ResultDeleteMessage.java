package podChat.mainmodel;


import podChat.model.DeleteMessageContent;

public class ResultDeleteMessage {
    private DeleteMessageContent deletedMessage;

    public DeleteMessageContent getDeletedMessage() {
        return deletedMessage;
    }

    public void setDeletedMessage(DeleteMessageContent deleteMessageContent) {
        this.deletedMessage = deleteMessageContent;
    }
}