package exmaple;

import com.google.gson.Gson;
import exception.ConnectionException;
import podChat.ProgressHandler;
import podChat.chat.Chat;
import podChat.chat.ChatAdapter;
import podChat.chat.ChatHandler;
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
        chat = Chat.init(false, true);

        chat.addListener(this);
        chat.addListener(new ChatListener() {
            @Override
            public void onSent(String content, ChatResponse<ResultMessage> response) {
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

    }

    @Override
    public void deliveredMessageList(RequestDeliveredMessageList requestParams) {

    }

    @Override
    public void createThreadWithMessage(RequestCreateThread threadRequest) {
        ArrayList<String> uniqueId = chat.createThreadWithMessage(threadRequest);
        System.out.println(uniqueId);
    }

    @Override
    public String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
            , String metadata) {
        return chat.createThread(threadType, invitee, threadTitle, description, image, metadata, null);
    }

    @Override
    public void getThreads(RequestThread requestThread, ChatHandler handler) {
        chat.getThreads(requestThread, handler);
    }

    @Override
    public void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName, long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, ChatHandler handler) {
        chat.getThreads(count, offset, threadIds, threadName, creatorCoreUserId, partnerCoreUserId, partnerCoreContactId, handler);
    }

    @Override
    public void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName, ChatHandler handler) {

    }

    @Override
    public void setToke(String token) {

    }

    @Override
    public void connect(RequestConnect requestConnect) throws ConnectionException {
        chat.connect(requestConnect);

    }

    @Override
    public void mapSearch(String searchTerm, Double latitude, Double longitude) {

    }

    @Override
    public void mapRouting(String origin, String destination) {

    }

    @Override
    public void mapStaticImage(RequestMapStaticImage request) {

    }

    @Override
    public void mapReverse(RequestMapReverse request) {

    }

    @Override
    public void getUserInfo(ChatHandler handler) {

    }

    @Override
    public void getHistory(History history, long threadId, ChatHandler handler) {

    }

    @Override
    public void getHistory(RequestGetHistory request, ChatHandler handler) {
        chat.getHistory(request, handler);
    }

    @Override
    public void searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO, ChatHandler handler) {

    }

    @Override
    public void getContact(Integer count, Long offset, ChatHandler handler) {
        chat.getContacts(count, offset, handler);

    }

    @Override
    public void getContact(RequestGetContact request, ChatHandler handler) {
        chat.getContacts(request, handler);

    }


    @Override
    public void createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image, String metaData, ChatHandler handler) {

    }

    @Override
    public void sendTextMessage(String textMessage, long threadId, Integer messageType, String metaData, ChatHandler handler) {
        chat.sendTextMessage(textMessage, threadId, messageType, metaData, handler);
    }

    @Override
    public void sendTextMessage(RequestMessage requestMessage, ChatHandler handler) {
        chat.sendTextMessage(requestMessage, null);
    }

    @Override
    public void replyMessage(String messageContent, long threadId, long messageId, String systemMetaData, Integer messageType, ChatHandler handler) {
        chat.replyMessage(messageContent, threadId, messageId, systemMetaData, messageType, handler);
    }

    @Override
    public void replyMessage(RequestReplyMessage request, ChatHandler handler) {
        chat.replyMessage(request, handler);
    }

    @Override
    public void muteThread(int threadId, ChatHandler handler) {

    }

    @Override
    public void renameThread(long threadId, String title, ChatHandler handler) {

    }

    @Override
    public void unMuteThread(int threadId, ChatHandler handler) {

    }

    @Override
    public void editMessage(int messageId, String messageContent, String metaData, ChatHandler handler) {
        chat.editMessage(messageId, messageContent, metaData, handler);
    }

    @Override
    public void editMessage(RequestEditMessage request, ChatHandler handler) {
        chat.editMessage(request, handler);
    }


    @Override
    public void getThreadParticipant(int count, Long offset, long threadId, ChatHandler handler) {

    }

    @Override
    public void getThreadParticipant(RequestThreadParticipant request, ChatHandler handler) {
        chat.getThreadParticipants(request, handler);
    }

    @Override
    public void get(int count, Long offset, long threadId, ChatHandler handler) {

    }

    @Override
    public void addContact(String firstName, String lastName, String cellphoneNumber, String email) {
        chat.addContact(firstName, lastName, cellphoneNumber, email);
    }

    @Override
    public void addContact(RequestAddContact request) {
        chat.addContact(request);
    }

    @Override
    public void removeContact(long id) {

    }

    @Override
    public void removeContact(RequestRemoveContact requestRemoveContact) {
        chat.removeContact(requestRemoveContact);
    }

    @Override
    public void searchContact(SearchContact searchContact) {

    }

    @Override
    public void block(Long contactId, Long userId, Long threadId, ChatHandler handler) {

    }

    @Override
    public void unBlock(Long blockId, Long userId, Long threadId, Long contactId, ChatHandler handler) {

    }

    @Override
    public void unBlock(RequestUnBlock request, ChatHandler handler) {

    }

    @Override
    public void spam(long threadId) {

    }

    @Override
    public void getBlockList(Long count, Long offset, ChatHandler handler) {

    }

    @Override
    public void forwardMessage(long threadId, ArrayList<Long> messageIds) {

    }

    @Override
    public void forwardMessage(RequestForwardMessage request) {
        chat.forwardMessage(request);
    }

    @Override
    public void updateContact(int id, String firstName, String lastName, String cellphoneNumber, String email) {

    }

    @Override
    public void updateContact(RequestUpdateContact updateContact) {
        chat.updateContact(updateContact);
    }

    @Override
    public void seenMessage(int messageId, long ownerId, ChatHandler handler) {

    }

    @Override
    public void logOut() {

    }

    @Override
    public void removeParticipants(long threadId, List<Long> participantIds, ChatHandler handler) {

    }

    @Override
    public void removeParticipants(RequestRemoveParticipants requestRemoveParticipants, ChatHandler handler) {

    }

    @Override
    public void addParticipants(long threadId, List<Long> contactIds, ChatHandler handler) {
        chat.addParticipants(threadId, contactIds, handler);
    }

    @Override
    public void addParticipants(RequestAddParticipants requestAddParticipants, ChatHandler handler) {
        chat.addParticipants(requestAddParticipants, handler);
    }

    @Override
    public void leaveThread(long threadId, ChatHandler handler) {

    }

    @Override
    public void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler) {

    }

    @Override
    public void updateThreadInfo(RequestThreadInfo request, ChatHandler handler) {

    }

    @Override
    public void deleteMessage(RequestDeleteMessage deleteMessage, ChatHandler handler) {
        chat.deleteMessage(deleteMessage, handler);
    }

    @Override
    public void deleteMultipleMessage(RequestDeleteMessage deleteMessage, ChatHandler handler) {
        chat.deleteMultipleMessage(deleteMessage, handler);
    }

    @Override
    public void setAdmin(RequestAddAdmin requestAddAdmin) {

    }

    @Override
    public void clearHistory(RequestClearHistory requestClearHistory) {

    }

    @Override
    public void getAdminList(RequestGetAdmin requestGetAdmin) {

    }

    @Override
    public String startSignalMessage(RequestSignalMsg requestSignalMsg) {
        return null;
    }

    @Override
    public void stopSignalMessage(String uniqueId) {

    }

    //View
    @Override
    public void onDeliver(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onDeliver(content, chatResponse);
        view.onGetDeliverMessage(chatResponse);
    }

    @Override
    public void onGetThread(String content, ChatResponse<ResultThreads> thread) {
        super.onGetThread(content, thread);
        view.onGetThreadList(thread);
    }

    @Override
    public void onThreadInfoUpdated(String content, ChatResponse<ResultThread> response) {
    }

    @Override
    public void onGetContacts(String content, ChatResponse<ResultContact> outPutContact) {
        super.onGetContacts(content, outPutContact);
        view.onGetContacts(outPutContact);
    }

    @Override
    public void onSeen(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onSeen(content, chatResponse);
        view.onGetSeenMessage(chatResponse);
    }

    @Override
    public void onUserInfo(String content, ChatResponse<ResultUserInfo> outPutUserInfo) {

        view.onGetUserInfo(outPutUserInfo);
    }

    @Override
    public void onSent(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onSent(content, chatResponse);
        view.onSentMessage(chatResponse);
    }


    @Override
    public void onCreateThread(String content, ChatResponse<ResultThread> outPutThread) {
        super.onCreateThread(content, outPutThread);
        view.onCreateThread(outPutThread);
    }

    @Override
    public void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> outPutParticipant) {
        super.onGetThreadParticipant(content, outPutParticipant);
        view.onGetThreadParticipant();
    }

    @Override
    public void onEditedMessage(String content, ChatResponse<ResultNewMessage> chatResponse) {
        super.onEditedMessage(content, chatResponse);
        view.onEditMessage(chatResponse);
    }

    @Override
    public void onGetHistory(String content, ChatResponse<ResultHistory> history) {
        super.onGetHistory(content, history);
        view.onGetThreadHistory();
    }

    @Override
    public void onMuteThread(String content, ChatResponse<ResultMute> outPutMute) {
        super.onMuteThread(content, outPutMute);
        view.onMuteThread();
    }

    @Override
    public void onUnmuteThread(String content, ChatResponse<ResultMute> outPutMute) {
        super.onUnmuteThread(content, outPutMute);
        view.onUnMuteThread();
    }

    @Override
    public void onRenameThread(String content, OutPutThread outPutThread) {
        super.onRenameThread(content, outPutThread);
        view.onRenameGroupThread();
    }

    @Override
    public void onContactAdded(String content, ChatResponse<ResultAddContact> chatResponse) {
        super.onContactAdded(content, chatResponse);
        view.onAddContact(chatResponse);
    }

    @Override
    public void onUpdateContact(String content, ChatResponse<ResultUpdateContact> chatResponse) {
        super.onUpdateContact(content, chatResponse);
        view.onUpdateContact(chatResponse);
    }

    @Override
    public void onUploadFile(String content, ChatResponse<ResultFile> response) {
        super.onUploadFile(content, response);
        view.onUploadFile(response);
    }


    @Override
    public void onUploadImageFile(String content, ChatResponse<ResultImageFile> chatResponse) {
        super.onUploadImageFile(content, chatResponse);
        view.onUploadImageFile(chatResponse);
    }

    @Override
    public void onRemoveContact(String content, ChatResponse<ResultRemoveContact> response) {
        super.onRemoveContact(content, response);
        view.onRemoveContact(response);
    }

    @Override
    public void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> outPutAddParticipant) {
        super.onThreadAddParticipant(content, outPutAddParticipant);
        view.onAddParticipant(outPutAddParticipant);
    }

    @Override
    public void onThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> chatResponse) {
        super.onThreadRemoveParticipant(content, chatResponse);
        view.onRemoveParticipant();
    }


    @Override
    public void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        super.onDeleteMessage(content, outPutDeleteMessage);
        view.onDeleteMessage(outPutDeleteMessage);
    }

    @Override
    public void onThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {
        super.onThreadLeaveParticipant(content, response);
        view.onLeaveThread();
    }

    @Override
    public void onChatState(String state) {
        view.onState(state);
    }

    @Override
    public void onNewMessage(String content, ChatResponse<ResultNewMessage> chatResponse) {
        super.onNewMessage(content, chatResponse);
        Gson gson = new Gson();
        OutPutNewMessage outPutNewMessage = gson.fromJson(content, OutPutNewMessage.class);
        ResultNewMessage result = outPutNewMessage.getResult();
        MessageVO messageVO = result.getMessageVO();
        Participant participant = messageVO.getParticipant();

        long id = messageVO.getId();
        chat.seenMessage(id, participant.getId(), new ChatHandler() {
            @Override
            public void onSeen(String uniqueId) {
                super.onSeen(uniqueId);
            }
        });

        view.onNewMessage(chatResponse);
    }

    @Override
    public void onBlock(String content, ChatResponse<ResultBlock> outPutBlock) {
        super.onBlock(content, outPutBlock);
        view.onBlock();
    }

    @Override
    public void onUnBlock(String content, ChatResponse<ResultBlock> outPutBlock) {
        super.onUnBlock(content, outPutBlock);
        view.onUnblock();
    }

    @Override
    public void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {
        super.onMapSearch(content, outPutMapNeshan);
        view.onMapSearch();
    }

    @Override
    public void onMapRouting(String content) {
        view.onMapRouting();
    }

    @Override
    public void onGetBlockList(String content, ChatResponse<ResultBlockList> outPutBlockList) {
        super.onGetBlockList(content, outPutBlockList);
        view.ongetBlockList();
    }

    @Override
    public void OnSeenMessageList(String content, ChatResponse<ResultParticipant> chatResponse) {

    }

    @Override
    public void onSearchContact(String content, ChatResponse<ResultContact> chatResponse) {
        super.onSearchContact(content, chatResponse);
        view.onSearchContact();
    }

    @Override
    public void onError(String content, ErrorOutPut error) {
        super.onError(content, error);
        view.onError(error);
    }


}

