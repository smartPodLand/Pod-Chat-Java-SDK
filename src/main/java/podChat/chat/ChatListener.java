package podChat.chat;


import podChat.model.*;

public interface ChatListener {


    void onError(String content, ErrorOutPut OutPutError);

    default void onGetContacts(String content, OutPutContact outPutContact) {

    }

    default void onGetHistory(String content, OutPutHistory history) {

    }

    default void onGetThread(String content, OutPutThreads thread) {

    }

    default void onThreadInfoUpdated(String content) {

    }

    default void onBlock(String content, OutPutBlock outPutBlock) {

    }

    default void onUnBlock(String content, OutPutBlock outPutBlock) {

    }

    default void onSeen(String content) {

    }

    default void onDeliver(String content) {

    }

    default void onSent(String content) {

    }

    default void onMuteThread(String content, OutPutMute outPutMute) {

    }

    default void onUnmuteThread(String content, OutPutMute outPutUnMute) {

    }

    default void onUserInfo(String content, OutPutUserInfo outPutUserInfo) {

    }

    default void onCreateThread(String content, OutPutThread outPutThread) {

    }

    default void onGetThreadParticipant(String content, OutPutParticipant outPutParticipant) {

    }

    default void onEditedMessage(String content) {

    }

    default void onContactAdded(String content) {

    }

    default void handleCallbackError(Throwable cause) throws Exception {

    }

    default void onRemoveContact(String content) {

    }

    default void onRenameThread(String content, OutPutThread outPutThread) {

    }

    default void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {

    }

    default void onMapRouting(String content) {

    }

    default void onNewMessage(String content, OutPutNewMessage outPutNewMessage) {

    }

    default void onDeleteMessage(String content, OutPutDeleteMessage outPutDeleteMessage) {

    }

    default void onUpdateContact(String content) {

    }

    default void onUploadFile(String content) {

    }

    default void onUploadImageFile(String content, FileImageUpload fileImageUpload) {

    }

    default void onSyncContact(String content) {

    }

    default void onSearchContact(String content, ChatResponse<ResultContact> chatResponse) {

    }

    default void onThreadAddParticipant(String content, OutPutAddParticipant outPutAddParticipant) {

    }

    default void onThreadRemoveParticipant(String content, OutPutParticipant outPutParticipant) {

    }

    default void onThreadLeaveParticipant(String content, OutPutLeaveThread outPutLeaveThread) {

    }

    void onLastSeenUpdated(String content);

    void onChatState(String state);

    void onGetBlockList(String content, OutPutBlockList outPutBlockList);


}
