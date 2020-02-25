package exmaple;

import exception.ConnectionException;
import podChat.ProgressHandler;
import podChat.mainmodel.*;
import podChat.model.*;
import podChat.requestobject.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Khojasteh on 7/30/2019
 */
public interface ChatContract {

    interface view {

//        default void onRecivedNotification(Notification notification) {

//        }

        default void onLogEvent(String log) {
        }

        default void onGetThreadList(ChatResponse<ResultThreads> thread) {
        }

        default void onGetThreadHistory(ChatResponse<ResultHistory> history) {
        }

        default void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response) {
        }

        default void onGetContacts(ChatResponse<ResultContact> response) {
        }

        default void onGetThreadParticipant(ChatResponse<ResultParticipant> response) {
        }

        default void onSentMessage(ChatResponse<ResultMessage> chatResponse) {
        }

        default void onGetDeliverMessage(ChatResponse<ResultMessage> chatResponse) {
        }

        default void onGetSeenMessage(ChatResponse<ResultMessage> response) {
        }

        default void onEditMessage(ChatResponse<ResultNewMessage> response) {
        }

        default void onDeleteMessage(ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        }

        default void onCreateThread(ChatResponse<ResultThread> outPutThread) {
        }

        default void onMuteThread(ChatResponse<ResultMute> outPut) {
        }

        default void onUnMuteThread(ChatResponse<ResultMute> response) {
        }

        default void onRenameGroupThread() {
        }

        default void onAddContact(ChatResponse<ResultAddContact> chatResponse) {
        }

        default void onUpdateContact(ChatResponse<ResultUpdateContact> chatResponse) {
        }

        default void onUploadFile(ChatResponse<ResultFile> response) {
        }

        default void onUploadImageFile(ChatResponse<ResultImageFile> response) {
        }

        default void onRemoveContact(ChatResponse<ResultRemoveContact> response) {
        }

        default void onAddParticipant(ChatResponse<ResultAddParticipant> outPutAddParticipant) {
        }

        default void onRemoveParticipant(ChatResponse<ResultParticipant> response) {
        }

        default void onLeaveThread(ChatResponse<ResultLeaveThread> response) {
        }

        default void OnClearHistory(ChatResponse<ResultClearHistory> chatResponse) {
        }

        default void onBlock(ChatResponse<ResultBlock> outPutBlock) {
        }

        default void onUnblock(ChatResponse<ResultBlock> outPutBlock) {
        }

        default void onSearchContact(ChatResponse<ResultContact> chatResponse) {
        }

        default void onSetRole(ChatResponse<ResultSetRole> chatResponse) {
        }


        default void onRemoveRole(ChatResponse<ResultSetRole> chatResponse) {
        }

        default void onUserInfo() {

        }


        default void onSearchHisory() {
        }

        default void onState(String state) {
        }

        default void onGetBlockList(ChatResponse<ResultBlockList> outPutBlockList) {
        }

        default void onMapSearch() {
        }

//        default void onMapStaticImage(ChatResponse<ResultStaticMapImage> chatResponse) {
//        }

        default void onMapRouting() {
        }

        default void onMapReverse() {
        }

        default void onError(ErrorOutPut error) {
        }

        default void onSpam() {
        }

        default void onNewMessage(ChatResponse<ResultNewMessage> chatResponse) {
        }

        default void OnDeliveredMessageList(ChatResponse<ResultParticipant> response) {
        }

        default void OnSeenMessageList(ChatResponse<ResultParticipant> response) {
        }

        default void OnSetRole(ChatResponse<ResultSetRole> chatResponse) {
        }

        default void OnInteractMessage(ChatResponse<ResultInteractMessage> chatResponse) {
        }

        default void onPinThread(ChatResponse<ResultPinThread> chatResponse) {
        }

        default void onUnPinThread(ChatResponse<ResultPinThread> chatResponse) {
        }


        default void onPinMessage(ChatResponse<ResultPinMessage> chatResponse) {
        }

        default void onUnPinMessage(ChatResponse<ResultPinMessage> chatResponse) {
        }


    }

    interface controller {

        //  void sendLocationMessage(RequestLocationMessage request);

        void isDatabaseOpen();

        void uploadImage(RequestUploadImage requestUploadImage);

        void uploadFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler);

        void replyFileMessage(RequestReplyFileMessage requestReplyFileMessage, ProgressHandler.sendFileMessage handler);

        void uploadFile(RequestUploadFile requestUploadFile);


        //void retryUpload(RetryUpload retry, ProgressHandler.sendFileMessage handler);

        void resendMessage(String uniqueId);

        void cancelMessage(String uniqueId);

        void retryUpload(String uniqueId);

        void cancelUpload(String uniqueId);

        void seenMessageList(RequestSeenMessageList requestParam);

        void deliveredMessageList(RequestDeliveredMessageList requestParams);

        void createThreadWithMessage(RequestCreateThreadWithMessage threadRequest);

//        String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
//                , String metadata);


        void getThreads(RequestThread requestThread);

        void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,

                        long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, String typeCode);

        void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName);

        void setToke(String token);

        void connect(RequestConnect requestConnect) throws ConnectionException;

        void getUserInfo();

        void getHistory(History history, long threadId);

        void getHistory(RequestGetHistory request);

        void searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO);

        void getContact(Integer count, Long offset, String typeCode);

        void getContact(RequestGetContact request);

        void createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
                , String metaData, String typeCode);

        void createThread(RequestCreateThread requestCreateThread);

        void createThreadWithFileMessage(RequestCreateThreadWithFile requestCreateThreadWithMessage);

        void sendTextMessage(String textMessage, long threadId, Integer messageType, String metaData, String typeCode);

        void sendTextMessage(RequestMessage requestMessage);

        void replyMessage(String messageContent, long threadId, long messageId, String systemMetaData
                , Integer messageType, String typeCode);

        // void replyFileMessage(RequestReplyFileMessage request, ProgressHandler.sendFileMessage handler);

        void replyMessage(RequestReplyMessage request);

        void muteThread(int threadId);

        void muteThread(RequestMuteThread requestMuteThread);

        void renameThread(long threadId, String title);

        void unMuteThread(int threadId);

        void unMuteThread(RequestMuteThread requestMuteThread);

        void editMessage(int messageId, String messageContent, String metaData);

        void editMessage(RequestEditMessage request);

        void getThreadParticipant(int count, Long offset, long threadId);

        void getThreadParticipant(RequestThreadParticipant request);

        void getMentionedList(RequestGetMentionedList requestGetMentionedList);

        void getCurrentUserRoles(RequestCurrentUserRoles requestCurrentUserRoles);

        void get(int count, Long offset, long threadId);

        void addContact(RequestAddContact request);

        void removeContact(RequestRemoveContact requestRemoveContact);


        void searchContact(RequestSearchContact requestSearchContact);

        void block(Long contactId, Long userId, Long threadId);

        void block(RequestBlock request);

        void unBlock(Long blockId, Long userId, Long threadId, Long contactId);

        void unBlock(RequestUnBlock request);

        void spam(long threadId);

        void spam(RequestSpam requestSpam);

        void getBlockList(Long count, Long offset);

        // String sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData, Integer messageType, ProgressHandler.sendFileMessage handler);

        //  void sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler);

        //  void syncContact(Activity activity);

        void forwardMessage(long threadId, ArrayList<Long> messageIds);

        void forwardMessage(RequestForwardMessage request);

        void updateContact(RequestUpdateContact updateContact);

        //  void uploadImage(Activity activity, Uri fileUri);

        //   void uploadFile(Activity activity, Uri uri);

        void seenMessage(long messageId, long ownerId);

        void logOut();

        void removeParticipants(long threadId, List<Long> participantIds);

        void removeParticipants(RequestRemoveParticipants requestRemoveParticipants);

        void addParticipants(long threadId, List<Long> contactIds);

        void addParticipants(RequestAddParticipants requestAddParticipants);

        void leaveThread(long threadId);

        void leaveThread(RequestLeaveThread threadId);

        void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO);

        void updateThreadInfo(RequestThreadInfo request);

        void deleteMessage(RequestDeleteMessage deleteMessage);

        void deleteMultipleMessage(RequestDeleteMessage deleteMessage);

//        void uploadImageProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgress handler);
//
//        void uploadFileProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgressFile handler);

        void addAdmin(RequestSetAdmin requestSetAdmin);

        void removeAdmin(RequestSetAdmin requestSetAdmin);

        void addAuditor(RequestSetAuditor requestSetAuditor);

        void removeAuditor(RequestSetAuditor requestSetAuditor);

        void clearHistory(RequestClearHistory requestClearHistory);

        void getAdminList(RequestGetAdmin requestGetAdmin);

        String startSignalMessage(RequestSignalMsg requestSignalMsg);

        void stopSignalMessage(String uniqueId);

        void getBlockList(RequestBlockList request);

        void interactiveMessage(RequestInteract request);

        void pinThread(RequestPinThread request);

        void unPinThread(RequestPinThread request);

        void pinMessage(RequestPinMessage request);

        void unPinMessage(RequestPinMessage request);

        void updateProfile(RequestUpdateProfile request);
    }
}
