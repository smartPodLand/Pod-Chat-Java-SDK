package podChat.chat;


import podChat.mainmodel.ResultDeleteMessage;
import podChat.model.*;

public interface ChatListener {

    default void onError(ErrorOutPut error) {
    }

    default void onGetContacts(ChatResponse<ResultContact> response) {
    }

    default void onGetHistory(ChatResponse<ResultHistory> history) {
    }

    default void onGetThread(ChatResponse<ResultThreads> thread) {

    }

    default void onThreadInfoUpdated(ChatResponse<ResultThread> response) {

    }

    default void onBlock(ChatResponse<ResultBlock> response) {

    }

    default void onUnBlock(ChatResponse<ResultBlock> response) {

    }

    default void onSeen(ChatResponse<ResultMessage> response) {

    }

    default void onDeliver(ChatResponse<ResultMessage> response) {

    }

    default void onSent(ChatResponse<ResultMessage> response) {

    }

    default void onMuteThread(ChatResponse<ResultMute> response) {

    }

    default void onUnmuteThread(ChatResponse<ResultMute> response) {

    }

    default void onCreateThread(ChatResponse<ResultThread> response) {

    }

    default void onGetThreadParticipant(ChatResponse<ResultParticipant> response) {

    }

    default void onEditedMessage(ChatResponse<ResultNewMessage> response) {

    }

    default void onContactAdded(ChatResponse<ResultAddContact> response) {

    }

    default void handleCallbackError(Throwable cause) throws Exception {

    }

    default void onRemoveContact(ChatResponse<ResultRemoveContact> response) {

    }

    default void onRenameThread(OutPutThread outPutThread) {

    }


    default void onNewMessage(ChatResponse<ResultNewMessage> response) {

    }

    default void onDeleteMessage(ChatResponse<ResultDeleteMessage> response) {

    }

    default void onUpdateContact(ChatResponse<ResultUpdateContact> response) {

    }

    default void onUploadFile(ChatResponse<ResultFile> response) {

    }

    default void onUploadImageFile(ChatResponse<ResultImageFile> response) {

    }

    default void onSearchContact(ChatResponse<ResultContact> response) {

    }

    default void onThreadAddParticipant(ChatResponse<ResultAddParticipant> response) {

    }

    default void onThreadRemoveParticipant(ChatResponse<ResultParticipant> response) {

    }

    default void onThreadLeaveParticipant(ChatResponse<ResultLeaveThread> response) {

    }


    default void onLastSeenUpdated(String content) {
    }

    default void onChatState(String state) {
    }

    default void onGetBlockList(ChatResponse<ResultBlockList> response) {
    }

    default void onUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> response) {
    }

    default void OnDeliveredMessageList(ChatResponse<ResultParticipant> response) {
    }

    default void OnSeenMessageList(ChatResponse<ResultParticipant> response) {
    }

    default void OnRemovedFromThread(ChatResponse<ResultThread> chatResponse) {

    }

    default void OnLogEvent(String log) {

    }

    default void OnClearHistory(ChatResponse<ResultClearHistory> chatResponse) {

    }

    default void OnGetThreadAdmin(String content) {
    }

    default void OnSetRole(ChatResponse<ResultSetRole> chatResponse) {

    }

    default void OnRemoveRole(ChatResponse<ResultSetRole> chatResponse) {

    }


    default void OnInteractMessage(ChatResponse<ResultInteractMessage> chatResponse) {
    }

    default void onUserInfo(ChatResponse<ResultUserInfo> response) {

    }

    default void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response) {

    }

    default void onUpdateProfile(ChatResponse<ResultUpdateProfile> response) {

    }

    default void onPinThread(ChatResponse<ResultPinThread> response) {

    }

    default void onUnPinThread(ChatResponse<ResultPinThread> response) {

    }

    default void onPinMessage(ChatResponse<ResultPinMessage> response) {

    }

    default void onUnPinMessage(ChatResponse<ResultPinMessage> response) {

    }

    default void onIsNameAvailable(ChatResponse<ResultIsNameAvailable> response) {

    }

    default void onJoinThread(ChatResponse<ResultThread> response) {

    }

    default void onCountUnreadMessage(ChatResponse<ResultUnreadMessageCount> response) {

    }
}
