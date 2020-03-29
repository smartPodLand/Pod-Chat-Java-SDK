package podChat.chat;

import podChat.mainmodel.ResultDeleteMessage;
import podChat.model.*;

public class ChatAdapter implements ChatListener {
    public ChatAdapter() {
        super();
    }

    @Override
    public void onError(ErrorOutPut error) {

    }

    @Override
    public void onGetContacts(ChatResponse<ResultContact> response) {

    }

    @Override
    public void onGetHistory(ChatResponse<ResultHistory> history) {

    }

    @Override
    public void onGetThread(ChatResponse<ResultThreads> thread) {

    }

    @Override
    public void onThreadInfoUpdated(ChatResponse<ResultThread> response) {

    }

    @Override
    public void onBlock(ChatResponse<ResultBlock> response) {

    }

    @Override
    public void onUnBlock(ChatResponse<ResultBlock> response) {

    }

    @Override
    public void onSeen(ChatResponse<ResultMessage> response) {

    }

    @Override
    public void onDeliver(ChatResponse<ResultMessage> response) {

    }

    @Override
    public void onSent(ChatResponse<ResultMessage> response) {

    }

    @Override
    public void onMuteThread(ChatResponse<ResultMute> response) {

    }

    @Override
    public void onUnmuteThread(ChatResponse<ResultMute> response) {

    }

    @Override
    public void onCreateThread(ChatResponse<ResultThread> response) {

    }

    @Override
    public void onGetThreadParticipant(ChatResponse<ResultParticipant> response) {

    }

    @Override
    public void onEditedMessage(ChatResponse<ResultNewMessage> response) {

    }

    @Override
    public void onContactAdded(ChatResponse<ResultAddContact> response) {

    }

    @Override
    public void handleCallbackError(Throwable cause) throws Exception {

    }

    @Override
    public void onRemoveContact(ChatResponse<ResultRemoveContact> response) {

    }

    @Override
    public void onRenameThread(OutPutThread outPutThread) {

    }

    @Override
    public void onNewMessage(ChatResponse<ResultNewMessage> response) {

    }

    @Override
    public void onDeleteMessage(ChatResponse<ResultDeleteMessage> response) {

    }

    @Override
    public void onUpdateContact(ChatResponse<ResultUpdateContact> response) {

    }

    @Override
    public void onUploadFile(ChatResponse<ResultFile> response) {

    }

    @Override
    public void onUploadImageFile(ChatResponse<ResultImageFile> response) {

    }

    @Override
    public void onSearchContact(ChatResponse<ResultContact> response) {

    }

    @Override
    public void onThreadAddParticipant(ChatResponse<ResultAddParticipant> response) {

    }

    @Override
    public void onThreadRemoveParticipant(ChatResponse<ResultParticipant> response) {

    }

    @Override
    public void onThreadLeaveParticipant(ChatResponse<ResultLeaveThread> response) {

    }

    @Override
    public void onLastSeenUpdated(String content) {

    }

    @Override
    public void onChatState(String state) {

    }

    @Override
    public void onGetBlockList(ChatResponse<ResultBlockList> response) {

    }

    @Override
    public void onUpdateThreadInfo(ChatResponse<ResultThread> response) {

    }

    @Override
    public void OnDeliveredMessageList(ChatResponse<ResultParticipant> response) {

    }

    @Override
    public void OnSeenMessageList(ChatResponse<ResultParticipant> response) {

    }

    @Override
    public void OnRemovedFromThread(ChatResponse<ResultThread> chatResponse) {

    }

    @Override
    public void OnLogEvent(String log) {

    }

    @Override
    public void OnClearHistory(ChatResponse<ResultClearHistory> chatResponse) {

    }

    @Override
    public void OnGetThreadAdmin(String content) {

    }

    @Override
    public void OnSetRole(ChatResponse<ResultSetRole> chatResponse) {

    }

    @Override
    public void OnRemoveRole(ChatResponse<ResultSetRole> chatResponse) {

    }

    @Override
    public void OnInteractMessage(ChatResponse<ResultInteractMessage> chatResponse) {

    }

    @Override
    public void onUserInfo(ChatResponse<ResultUserInfo> response) {

    }

    @Override
    public void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response) {

    }

    @Override
    public void onPinThread(ChatResponse<ResultPinThread> response) {

    }

    @Override
    public void onUnPinThread(ChatResponse<ResultPinThread> response) {

    }

    @Override
    public void onPinMessage(ChatResponse<ResultPinMessage> response) {

    }

    @Override
    public void onUnPinMessage(ChatResponse<ResultPinMessage> response) {

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
