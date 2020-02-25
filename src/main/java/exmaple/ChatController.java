package exmaple;

import exception.ConnectionException;
import podChat.ProgressHandler;
import podChat.chat.Chat;
import podChat.chat.ChatAdapter;
import podChat.chat.ChatListener;
import podChat.mainmodel.*;
import podChat.model.*;
import podChat.requestobject.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Khojasteh on 7/30/2019
 */
public class ChatController extends ChatAdapter implements ChatContract.controller {

    private Chat chat;
    private ChatContract.view view;

    public ChatController(ChatContract.view view) {
        chat = Chat.init(true);

        chat.addListener(this);
        chat.addListener(new ChatListener() {
            @Override
            public void onSent(ChatResponse<ResultMessage> response) {
            }
        });

        this.view = view;
    }


    @Override
    public void isDatabaseOpen() {

    }

    @Override
    public void uploadImage(RequestUploadImage requestUploadImage) {
        chat.uploadImage(requestUploadImage);
    }

    @Override
    public void uploadFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler) {
        chat.sendFileMessage(requestFileMessage, handler);
    }

    @Override
    public void replyFileMessage(RequestReplyFileMessage requestReplyFileMessage, ProgressHandler.sendFileMessage handler) {
        chat.replyFileMessage(requestReplyFileMessage, handler);
    }


    @Override
    public void uploadFile(RequestUploadFile requestUploadFile) {
        chat.uploadFile(requestUploadFile);
    }


    @Override
    public void resendMessage(String uniqueId) {

    }

    @Override
    public void cancelMessage(String uniqueId) {

    }

    @Override
    public void retryUpload(String uniqueId) {

    }

    @Override
    public void cancelUpload(String uniqueId) {

    }

    @Override
    public void seenMessageList(RequestSeenMessageList requestParam) {
        chat.getMessageSeenList(requestParam);
    }

    @Override
    public void deliveredMessageList(RequestDeliveredMessageList requestParams) {
        chat.getMessageDeliveredList(requestParams);
    }

    @Override
    public void createThreadWithMessage(RequestCreateThreadWithMessage threadRequest) {
        chat.createThreadWithMessage(threadRequest);
    }

    @Override
    public void getThreads(RequestThread requestThread) {
        chat.getThreads(requestThread);
    }

    @Override
    public void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,
                           long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, String typeCode) {
        chat.getThreads(count, offset, threadIds, threadName, creatorCoreUserId, partnerCoreUserId, partnerCoreContactId
                , typeCode);
    }

    @Override
    public void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName) {

    }

    @Override
    public void setToke(String token) {
        chat.setToken(token);
    }

    @Override
    public void connect(RequestConnect requestConnect) throws ConnectionException {
        chat.connect(requestConnect);

    }


    @Override
    public void getUserInfo() {
        chat.getUserInfo();
    }

    @Override
    public void getHistory(History history, long threadId) {

    }

    @Override
    public void getHistory(RequestGetHistory request) {
        chat.getHistory(request);
    }

    @Override
    public void searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO) {

    }

    @Override
    public void getContact(Integer count, Long offset, String typeCode) {
        chat.getContacts(count, offset, typeCode);

    }

    @Override
    public void getContact(RequestGetContact request) {
        chat.getContacts(request);

    }

    @Override
    public void createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image, String metaData, String typeCode) {
        chat.createThread(threadType, invitee, threadTitle, description, image, metaData, typeCode);
    }

    @Override
    public void createThread(RequestCreateThread requestCreateThread) {
        chat.createThread(requestCreateThread);
    }

    @Override
    public void createThreadWithFileMessage(RequestCreateThreadWithFile requestCreateThreadWithMessage) {
        chat.createThreadWithFileMessage(requestCreateThreadWithMessage);
    }

    @Override
    public void sendTextMessage(String textMessage, long threadId, Integer messageType, String metaData, String typeCode) {
        chat.sendTextMessage(textMessage, threadId, messageType, metaData, typeCode);
    }

    @Override
    public void sendTextMessage(RequestMessage requestMessage) {
        chat.sendTextMessage(requestMessage);
    }

    @Override
    public void replyMessage(String messageContent, long threadId, long messageId, String systemMetaData,
                             Integer messageType, String typeCode) {
        chat.replyMessage(messageContent, threadId, messageId, systemMetaData, messageType, typeCode);
    }


    @Override
    public void replyMessage(RequestReplyMessage request) {
        chat.replyMessage(request);
    }

    @Override
    public void muteThread(int threadId) {

    }

    @Override
    public void muteThread(RequestMuteThread requestMuteThread) {
        chat.muteThread(requestMuteThread);
    }

    @Override
    public void renameThread(long threadId, String title) {

    }

    @Override
    public void unMuteThread(int threadId) {

    }

    @Override
    public void unMuteThread(RequestMuteThread requestMuteThread) {
        chat.unMuteThread(requestMuteThread);
    }

    @Override
    public void editMessage(int messageId, String messageContent, String metaData) {
        chat.editMessage(messageId, messageContent, metaData);
    }

    @Override
    public void editMessage(RequestEditMessage request) {
        chat.editMessage(request);
    }


    @Override
    public void getThreadParticipant(int count, Long offset, long threadId) {

    }

    @Override
    public void getThreadParticipant(RequestThreadParticipant request) {
        chat.getThreadParticipants(request);
    }

    @Override
    public void getMentionedList(RequestGetMentionedList requestGetMentionedList) {
        chat.getMentionedList(requestGetMentionedList);
    }

    @Override
    public void getCurrentUserRoles(RequestCurrentUserRoles requestCurrentUserRoles) {
        chat.getCurrentUserRoles(requestCurrentUserRoles);
    }

    @Override
    public void get(int count, Long offset, long threadId) {

    }

    @Override
    public void addContact(RequestAddContact request) {
        chat.addContact(request);
    }

    @Override
    public void removeContact(RequestRemoveContact requestRemoveContact) {
        chat.removeContact(requestRemoveContact);
    }

    @Override
    public void searchContact(RequestSearchContact requestSearchContact) {
        chat.searchContact(requestSearchContact);
    }

    @Override
    public void block(Long contactId, Long userId, Long threadId) {

    }

    @Override
    public void block(RequestBlock request) {
        chat.block(request);
    }


    @Override
    public void unBlock(Long blockId, Long userId, Long threadId, Long contactId) {

    }

    @Override
    public void unBlock(RequestUnBlock request) {
        chat.unblock(request);
    }

    @Override
    public void spam(long threadId) {

    }

    @Override
    public void spam(RequestSpam requestSpam) {
        chat.spam(requestSpam);
    }

    @Override
    public void getBlockList(Long count, Long offset) {

    }

    @Override
    public void forwardMessage(long threadId, ArrayList<Long> messageIds) {

    }

    @Override
    public void forwardMessage(RequestForwardMessage request) {
        chat.forwardMessage(request);
    }

    @Override
    public void updateContact(RequestUpdateContact updateContact) {
        chat.updateContact(updateContact);
    }

    @Override
    public void seenMessage(long messageId, long ownerId) {
        chat.seenMessage(messageId, ownerId);
    }

    @Override
    public void logOut() {

    }

    @Override
    public void removeParticipants(long threadId, List<Long> participantIds) {

    }

    @Override
    public void removeParticipants(RequestRemoveParticipants requestRemoveParticipants) {
        chat.removeParticipants(requestRemoveParticipants);
    }

    @Override
    public void addParticipants(long threadId, List<Long> contactIds) {
        chat.addParticipants(threadId, contactIds);
    }

    @Override
    public void addParticipants(RequestAddParticipants requestAddParticipants) {
        chat.addParticipants(requestAddParticipants);
    }

    @Override
    public void leaveThread(long threadId) {

    }

    @Override
    public void leaveThread(RequestLeaveThread requestLeaveThread) {
        chat.leaveThread(requestLeaveThread);
    }

    @Override
    public void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO) {

    }

    @Override
    public void updateThreadInfo(RequestThreadInfo request) {

    }

    @Override
    public void deleteMessage(RequestDeleteMessage deleteMessage) {
        chat.deleteMessage(deleteMessage);
    }

    @Override
    public void deleteMultipleMessage(RequestDeleteMessage deleteMessage) {
        chat.deleteMultipleMessage(deleteMessage);
    }

    @Override
    public void addAdmin(RequestSetAdmin requestSetAdmin) {
        chat.addAdmin(requestSetAdmin);
    }

    @Override
    public void removeAdmin(RequestSetAdmin requestSetAdmin) {
        chat.removeAdmin(requestSetAdmin);
    }

    @Override
    public void addAuditor(RequestSetAuditor requestSetAuditor) {
        chat.addAuditor(requestSetAuditor);
    }

    @Override
    public void removeAuditor(RequestSetAuditor requestSetAuditor) {
        chat.removeAuditor(requestSetAuditor);
    }


    @Override
    public void clearHistory(RequestClearHistory requestClearHistory) {
        chat.clearHistory(requestClearHistory);
    }

    @Override
    public void getAdminList(RequestGetAdmin requestGetAdmin) {
        chat.getAdminList(requestGetAdmin);
    }

    @Override
    public String startSignalMessage(RequestSignalMsg requestSignalMsg) {
        return null;
    }

    @Override
    public void stopSignalMessage(String uniqueId) {

    }

    @Override
    public void getBlockList(RequestBlockList request) {
        chat.getBlockList(request);
    }

    @Override
    public void interactiveMessage(RequestInteract request) {
        chat.interactMessage(request);
    }

    @Override
    public void pinThread(RequestPinThread request) {
        chat.pinThread(request);
    }

    @Override
    public void unPinThread(RequestPinThread request) {
        chat.unPinThread(request);
    }

    @Override
    public void pinMessage(RequestPinMessage request) {
        chat.pinMessage(request);
    }

    @Override
    public void unPinMessage(RequestPinMessage request) {
        chat.unPinMessage(request);
    }

    @Override
    public void updateProfile(RequestUpdateProfile request) {
        chat.updateProfile(request);
    }


    //View
    @Override
    public void onDeliver(ChatResponse<ResultMessage> chatResponse) {
        super.onDeliver(chatResponse);
        view.onGetDeliverMessage(chatResponse);
    }

    @Override
    public void onGetThread(ChatResponse<ResultThreads> thread) {
        super.onGetThread(thread);
        view.onGetThreadList(thread);
    }

    @Override
    public void onThreadInfoUpdated(ChatResponse<ResultThread> response) {
    }

    @Override
    public void onGetContacts(ChatResponse<ResultContact> outPutContact) {
        super.onGetContacts(outPutContact);
        view.onGetContacts(outPutContact);
    }

    @Override
    public void onSeen(ChatResponse<ResultMessage> chatResponse) {
        super.onSeen(chatResponse);
        view.onGetSeenMessage(chatResponse);
    }

    @Override
    public void onSent(ChatResponse<ResultMessage> chatResponse) {
        super.onSent(chatResponse);
        view.onSentMessage(chatResponse);
    }


    @Override
    public void onCreateThread(ChatResponse<ResultThread> outPutThread) {
        super.onCreateThread(outPutThread);
        view.onCreateThread(outPutThread);
    }

    @Override
    public void onGetThreadParticipant(ChatResponse<ResultParticipant> outPutParticipant) {
        super.onGetThreadParticipant(outPutParticipant);
        view.onGetThreadParticipant(outPutParticipant);
    }

    @Override
    public void onEditedMessage(ChatResponse<ResultNewMessage> chatResponse) {
        super.onEditedMessage(chatResponse);
        view.onEditMessage(chatResponse);
    }

    @Override
    public void onGetHistory(ChatResponse<ResultHistory> history) {
        super.onGetHistory(history);
        view.onGetThreadHistory(history);
    }

    @Override
    public void onMuteThread(ChatResponse<ResultMute> outPutMute) {
        super.onMuteThread(outPutMute);
        view.onMuteThread(outPutMute);
    }

    @Override
    public void onUnmuteThread(ChatResponse<ResultMute> outPutMute) {
        super.onUnmuteThread(outPutMute);
        view.onUnMuteThread(outPutMute);
    }

    @Override
    public void onRenameThread(OutPutThread outPutThread) {
        super.onRenameThread(outPutThread);
        view.onRenameGroupThread();
    }

    @Override
    public void onContactAdded(ChatResponse<ResultAddContact> chatResponse) {
        super.onContactAdded(chatResponse);
        view.onAddContact(chatResponse);
    }

    @Override
    public void onUpdateContact(ChatResponse<ResultUpdateContact> chatResponse) {
        super.onUpdateContact(chatResponse);
        view.onUpdateContact(chatResponse);
    }

    @Override
    public void onUploadFile(ChatResponse<ResultFile> response) {
        super.onUploadFile(response);
        view.onUploadFile(response);
    }


    @Override
    public void onUploadImageFile(ChatResponse<ResultImageFile> chatResponse) {
        super.onUploadImageFile(chatResponse);
        view.onUploadImageFile(chatResponse);
    }

    @Override
    public void onRemoveContact(ChatResponse<ResultRemoveContact> response) {
        super.onRemoveContact(response);
        view.onRemoveContact(response);
    }

    @Override
    public void onThreadAddParticipant(ChatResponse<ResultAddParticipant> outPutAddParticipant) {
        super.onThreadAddParticipant(outPutAddParticipant);
        view.onAddParticipant(outPutAddParticipant);
    }

    @Override
    public void onThreadRemoveParticipant(ChatResponse<ResultParticipant> chatResponse) {
        super.onThreadRemoveParticipant(chatResponse);
        view.onRemoveParticipant(chatResponse);
    }


    @Override
    public void onDeleteMessage(ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        super.onDeleteMessage(outPutDeleteMessage);
        view.onDeleteMessage(outPutDeleteMessage);
    }

    @Override
    public void onThreadLeaveParticipant(ChatResponse<ResultLeaveThread> response) {
        super.onThreadLeaveParticipant(response);
        view.onLeaveThread(response);
    }

    @Override
    public void onChatState(String state) {
        view.onState(state);
    }

    @Override
    public void onNewMessage(ChatResponse<ResultNewMessage> chatResponse) {
        super.onNewMessage(chatResponse);

        ResultNewMessage result = chatResponse.getResult();
        MessageVO messageVO = result.getMessageVO();
        Participant participant = messageVO.getParticipant();
        long id = messageVO.getId();

        chat.seenMessage(id, participant.getId());

        view.onNewMessage(chatResponse);
    }

    @Override
    public void onBlock(ChatResponse<ResultBlock> outPutBlock) {
        super.onBlock(outPutBlock);
        view.onBlock(outPutBlock);
    }

    @Override
    public void onUnBlock(ChatResponse<ResultBlock> outPutBlock) {
        super.onUnBlock(outPutBlock);
        view.onUnblock(outPutBlock);
    }


    @Override
    public void onGetBlockList(ChatResponse<ResultBlockList> outPutBlockList) {
        super.onGetBlockList(outPutBlockList);
        view.onGetBlockList(outPutBlockList);
    }

    @Override
    public void OnSeenMessageList(ChatResponse<ResultParticipant> chatResponse) {
        view.OnSeenMessageList(chatResponse);
    }

    @Override
    public void onSearchContact(ChatResponse<ResultContact> chatResponse) {
        super.onSearchContact(chatResponse);
        view.onSearchContact(chatResponse);
    }

    @Override
    public void onError(ErrorOutPut error) {
        super.onError(error);
        view.onError(error);
    }

    @Override
    public void OnDeliveredMessageList(ChatResponse<ResultParticipant> chatResponse) {
        super.OnDeliveredMessageList(chatResponse);
        view.OnDeliveredMessageList(chatResponse);
    }

    @Override
    public void OnSetRole(ChatResponse<ResultSetRole> chatResponse) {
        super.OnSetRole(chatResponse);
        view.OnSetRole(chatResponse);
    }

    @Override
    public void OnInteractMessage(ChatResponse<ResultInteractMessage> chatResponse) {
        super.OnInteractMessage(chatResponse);
        view.OnInteractMessage(chatResponse);
    }

    @Override
    public void OnRemoveRole(ChatResponse<ResultSetRole> chatResponse) {
        super.OnRemoveRole(chatResponse);
        view.onRemoveRole(chatResponse);
    }

    @Override
    public void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response) {
        super.onGetCurrentUserRoles(response);
        view.onGetCurrentUserRoles(response);
    }
}

