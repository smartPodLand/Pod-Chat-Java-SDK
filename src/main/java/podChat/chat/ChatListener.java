package podChat.chat;


import podChat.mainmodel.ResultDeleteMessage;
import podChat.model.*;

public interface ChatListener {

    default void onError(String content, ErrorOutPut error) {
    }

    default void onGetContacts(String content, ChatResponse<ResultContact> response) {
    }

    default void onGetHistory(String content, ChatResponse<ResultHistory> history) {
    }

    default void onGetThread(String content, ChatResponse<ResultThreads> thread) {

    }

    default void onThreadInfoUpdated(String content, ChatResponse<ResultThread> response) {

    }

    default void onBlock(String content, ChatResponse<ResultBlock> response) {

    }

    default void onUnBlock(String content, ChatResponse<ResultBlock> response) {

    }

    default void onSeen(String content, ChatResponse<ResultMessage> response) {

    }

    default void onDeliver(String content, ChatResponse<ResultMessage> response) {

    }

    default void onSent(String content, ChatResponse<ResultMessage> response) {

    }

    default void onMuteThread(String content, ChatResponse<ResultMute> response) {

    }

    default void onUnmuteThread(String content, ChatResponse<ResultMute> response) {

    }

    default void onCreateThread(String content, ChatResponse<ResultThread> response) {

    }

    default void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> response) {

    }

    default void onEditedMessage(String content, ChatResponse<ResultNewMessage> response) {

    }

    default void onContactAdded(String content, ChatResponse<ResultAddContact> response) {

    }

    default void handleCallbackError(Throwable cause) throws Exception {

    }

    default void onRemoveContact(String content, ChatResponse<ResultRemoveContact> response) {

    }

    default void onRenameThread(String content, OutPutThread outPutThread) {

    }

    default void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {

    }

    default void onMapRouting(String content) {

    }

    default void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {

    }

    default void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> response) {

    }

    default void onUpdateContact(String content, ChatResponse<ResultUpdateContact> response) {

    }

    default void onUploadFile(String content, ChatResponse<ResultFile> response) {

    }

    default void onUploadImageFile(String content, ChatResponse<ResultImageFile> response) {

    }

    default void onSyncContact(String content, ChatResponse<Contacts> chatResponse) {

    }

    default void onSearchContact(String content, ChatResponse<ResultContact> response) {

    }

    default void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> response) {

    }

    default void onThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> response) {

    }

    default void onThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {

    }

    default void OnMapReverse(String json, ChatResponse<ResultMapReverse> response) {
    }

    default void onLastSeenUpdated(String content) {
    }

    default void onChatState(String state) {
    }

    default void onGetBlockList(String content, ChatResponse<ResultBlockList> response) {
    }

    default void onUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> response) {
    }

    default void OnDeliveredMessageList(String content, ChatResponse<ResultParticipant> response) {
    }

    default void OnSeenMessageList(String content, ChatResponse<ResultParticipant> response) {
    }

    default void OnRemovedFromThread(String content, ChatResponse<ResultThread> chatResponse) {

    }

    default void OnLogEvent(String log) {

    }

    default void OnClearHistory(String content, ChatResponse<ResultClearHistory> chatResponse) {

    }

    default void OnGetThreadAdmin(String content) {
    }

    default void OnSetRole(String content, ChatResponse<ResultSetAdmin> chatResponse) {
    }

    default void OnInteractMessage(String content, ChatResponse<ResultInteractMessage> chatResponse) {
    }
}
