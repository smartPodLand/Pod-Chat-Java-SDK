package podChat.chat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import config.QueueConfigVO;
import exception.ConnectionException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import podAsync.Async;
import podAsync.AsyncAdapter;
import podChat.ProgressHandler;
import podChat.localModel.LFileUpload;
import podChat.localModel.SetRuleVO;
import podChat.mainmodel.Thread;
import podChat.mainmodel.*;
import podChat.model.Error;
import podChat.model.MapLocation;
import podChat.model.*;
import podChat.networking.api.ContactApi;
import podChat.networking.api.FileApi;
import podChat.networking.retrofithelper.ApiListener;
import podChat.networking.retrofithelper.RetrofitHelperFileServer;
import podChat.networking.retrofithelper.RetrofitHelperPlatformHost;
import podChat.networking.retrofithelper.RetrofitUtil;
import podChat.requestobject.*;
import podChat.util.*;
import retrofit2.Call;
import retrofit2.Response;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created By Khojasteh on 7/29/2019
 */
public class Chat extends AsyncAdapter {
    private static final int TOKEN_ISSUER = 1;
    public static boolean isLoggable;
    private static Logger logger = LogManager.getLogger(Chat.class);
    private static Async async;
    private static Chat instance;
    private static ChatListenerManager listenerManager;
    private static Gson gson;
    private String token;
    private String typeCode = "default";
    private String platformHost;
    private String fileServer;
    private long userId;
    private ContactApi contactApi;
    private long lastSentMessageTime;
    private boolean chatReady = false;
    private boolean asyncReady = false;
    private int signalIntervalTime;
    private int expireAmount;
    private long ttl;
    private String ssoHost;
    private QueueConfigVO queueConfigVO;


    private Chat() {
    }

    /**
     * Initialize the Chat
     **/
    public synchronized static Chat init(boolean loggable) {
        if (instance == null) {
            isLoggable = loggable;
            async = Async.getInstance();
            instance = new Chat();
            gson = new Gson();
            listenerManager = new ChatListenerManager();
        }
        return instance;
    }

    private static synchronized String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * First we check the message type and then we set the
     * the callback for it.
     * Here its showed the raw log.
     */
    @Override
    public void onReceivedMessage(String textMessage) throws IOException {
        super.onReceivedMessage(textMessage);
        int messageType = 0;

        ChatMessage chatMessage = gson.fromJson(textMessage, ChatMessage.class);

        String messageUniqueId = chatMessage != null ? chatMessage.getUniqueId() : null;

        if (chatMessage != null) {
            messageType = chatMessage.getType();
        }

        switch (messageType) {
            case ChatMessageType.CHANGE_TYPE:
                break;

            case ChatMessageType.SENT:
                handleSent(chatMessage);
                break;

            case ChatMessageType.DELIVERY:
                handleDelivery(chatMessage);
                break;

            case ChatMessageType.SEEN:
                handleSeen(chatMessage);
                break;

            case ChatMessageType.ERROR:
                handleError(chatMessage);
                break;

            case ChatMessageType.FORWARD_MESSAGE:
                handleForwardMessage(chatMessage);
                break;


            case ChatMessageType.RELATION_INFO:
            case ChatMessageType.GET_STATUS:
            case ChatMessageType.USER_STATUS:
            case ChatMessageType.SPAM_PV_THREAD:
                break;

            case ChatMessageType.GET_THREADS:
                handleGetThreads(chatMessage);
//                handleResponseMessage(chatMessage, messageUniqueId);
                break;

            case ChatMessageType.REMOVED_FROM_THREAD:
                handleRemoveFromThread(chatMessage);
                break;

            case ChatMessageType.LEAVE_THREAD:
                handleOutPutLeaveThread(chatMessage);
                break;

            case ChatMessageType.MESSAGE:
                handleNewMessage(chatMessage);
                break;

            case ChatMessageType.PING:
                handleOnPing();
                break;

            case ChatMessageType.REMOVE_PARTICIPANT:
                handleOutPutRemoveParticipant(chatMessage);
                break;

            case ChatMessageType.RENAME:
            case ChatMessageType.THREAD_PARTICIPANTS:
            case ChatMessageType.UN_MUTE_THREAD:
            case ChatMessageType.MUTE_THREAD:
            case ChatMessageType.UNPIN_THREAD:
            case ChatMessageType.PIN_THREAD:
            case ChatMessageType.PIN_MESSAGE:
            case ChatMessageType.UNPIN_MESSAGE:
            case ChatMessageType.USER_INFO:
            case ChatMessageType.DELETE_MESSAGE:
            case ChatMessageType.EDIT_MESSAGE:
            case ChatMessageType.UPDATE_THREAD_INFO:
            case ChatMessageType.DELIVERED_MESSAGE_LIST:
            case ChatMessageType.SEEN_MESSAGE_LIST:
            case ChatMessageType.BLOCK:
            case ChatMessageType.UNBLOCK:
            case ChatMessageType.GET_BLOCKED:
            case ChatMessageType.ADD_PARTICIPANT:
            case ChatMessageType.GET_CONTACTS:
            case ChatMessageType.INVITATION:
            case ChatMessageType.GET_HISTORY:
                handleResponseMessage(chatMessage, messageUniqueId);
                break;

            case ChatMessageType.THREAD_INFO_UPDATED:
                handleThreadInfoUpdated(chatMessage);
                break;

            case ChatMessageType.LAST_SEEN_UPDATED:
                handleLastSeenUpdated(chatMessage);
                break;

            case ChatMessageType.SET_ROLE_TO_USER:
                handleSetRole(chatMessage);
                break;

            case ChatMessageType.REMOVE_ROLE_FROM_USER:
                handleRemoveRole(chatMessage);
                break;

            case ChatMessageType.CLEAR_HISTORY:
                handleClearHistory(chatMessage);
                break;

            case ChatMessageType.INTERACT_MESSAGE:
                handleInteractiveMessage(chatMessage);
                break;
        }
    }

    @Override
    public void onStateChanged(String state) throws IOException {
        showInfoLog("STATE: " + state);

        super.onStateChanged(state);
        listenerManager.callOnChatState(state);

        switch (state) {
            case ChatStateType.OPEN:
                break;

            case ChatStateType.ASYNC_READY:
                asyncReady = true;
                chatReady = true;
                pingWithDelay();
                break;

            case ChatStateType.CLOSING:
            case ChatStateType.CONNECTING:
            case ChatStateType.CLOSED:
                chatReady = false;
                TokenExecutor.stopThread();
                break;
        }
    }

    /**
     * @param requestConnect socketAddress        {**REQUIRED**}  Address of the socket
     *                       platformHost         {**REQUIRED**}  Address of the platform host
     *                       token                {**REQUIRED**}  Token for Authentication
     *                       fileServer           {**REQUIRED**}  Address of the file server
     *                       ssoHost              {**REQUIRED**}  Address of the SSO Host
     *                       queueServer          {**REQUIRED**}  Address of the queue server
     *                       queuePort            {**REQUIRED**}  The queue port
     *                       queueInput           {**REQUIRED**}  Name of the input queue
     *                       queueOutput          {**REQUIRED**}  Name of the output queue
     *                       queueUserName        {**REQUIRED**}
     *                       queuePassword        {**REQUIRED**}
     *                       queueReconnectTime   {**REQUIRED**}
     * @throws ConnectionException
     */

    public void connect(RequestConnect requestConnect) throws ConnectionException {
        try {

            async.addListener(this);

            setPlatformHost(requestConnect.getPlatformHost());
            setToken(requestConnect.getToken());
            setSsoHost(requestConnect.getSsoHost());
            setUserId(requestConnect.getChatId());

            if (!Util.isNullOrEmpty(requestConnect.getTypeCode()))
                setTypeCode(requestConnect.getTypeCode());
            else
                setTypeCode(typeCode);

            setFileServer(requestConnect.getFileServer());
            this.queueConfigVO = new QueueConfigVO(requestConnect.getQueueServer(), requestConnect.getQueuePort(), requestConnect.getQueueInput(), requestConnect.getQueueOutput(), requestConnect.getQueueUserName(), requestConnect.getQueuePassword());

            contactApi = RetrofitHelperPlatformHost.getInstance(getPlatformHost()).create(ContactApi.class);

            gson = new Gson();

            async.connect(queueConfigVO, requestConnect.getSeverName(), token, ssoHost);

        } catch (ConnectionException e) {
            throw e;
        } catch (Throwable throwable) {
            showErrorLog(throwable.getMessage());

            listenerManager.callOnLogEvent(throwable.getMessage());

        }
    }

    /**
     * Send text message to the thread
     * All of the messages first send to Message Queue(Cache) and then send to chat server
     *
     * @param textMessage        String that we want to sent to the thread
     * @param threadId           Id of the destination thread
     * @param jsonSystemMetadata It should be Json,if you don't have metaData you can set it to "null"
     */
    public String sendTextMessage(String textMessage, long threadId, Integer messageType, String jsonSystemMetadata, String typeCode) {

        String asyncContentWaitQueue;
        String uniqueId = generateUniqueId();

        try {

            ChatMessage chatMessageQueue = new ChatMessage();
            chatMessageQueue.setContent(textMessage);
            chatMessageQueue.setType(ChatMessageType.MESSAGE);
            chatMessageQueue.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessageQueue.setToken(getToken());

            if (jsonSystemMetadata != null) {
                chatMessageQueue.setSystemMetadata(jsonSystemMetadata);
            }

            chatMessageQueue.setUniqueId(uniqueId);
            chatMessageQueue.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageQueue);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            if (!Util.isNullOrEmpty(messageType)) {
                jsonObject.addProperty("messageType", messageType);
            } else {
                jsonObject.remove("messageType");
            }

            asyncContentWaitQueue = jsonObject.toString();

            if (chatReady) {
                sendAsyncMessage(asyncContentWaitQueue, 4, "SEND_TEXT_MESSAGE");

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);

                showErrorLog(jsonError);
            }
        } catch (Throwable throwable) {
            showErrorLog(throwable.getMessage());
        }
        return uniqueId;
    }

    /**
     * Its sent message but it gets Object as an attribute
     *
     * @param requestMessage this object has :
     *                       String textMessage {text of the message}
     *                       int messageType {type of the message}
     *                       String jsonMetaData {metadata of the message}
     *                       long threadId {The id of a thread that its wanted to send  }
     */
    public String sendTextMessage(RequestMessage requestMessage) {
        String textMessage = requestMessage.getTextMessage();
        long threadId = requestMessage.getThreadId();
        int messageType = requestMessage.getMessageType();
        String jsonMetaData = requestMessage.getJsonMetaData();
        String typeCode = requestMessage.getTypeCode();

        return sendTextMessage(textMessage, threadId, messageType, jsonMetaData, typeCode);
    }

    /**
     * Get the list of threads or you can just pass the thread id that you want
     *
     * @param count  number of thread
     * @param offset specified offset you want
     */
    @Deprecated
    public String getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName) {

        String uniqueId;
        count = count != null ? count : 50;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                ChatMessageContent chatMessageContent = new ChatMessageContent();

                if (offset != null) {
                    chatMessageContent.setOffset(offset);
                } else {
                    chatMessageContent.setOffset(0);
                }

                chatMessageContent.setCount(count);
                if (threadName != null) {
                    chatMessageContent.setName(threadName);
                }
                JsonObject jObj;

                if (threadIds != null && threadIds.size() > 0) {
                    chatMessageContent.setThreadIds(threadIds);
                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);

                } else {
                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
                    jObj.remove("threadIds");
                }

                jObj.remove("lastMessageId");
                jObj.remove("firstMessageId");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jObj.toString());
                chatMessage.setType(ChatMessageType.GET_THREADS);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                sendAsyncMessage(jsonObject.toString(), 3, "Get thread send");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * Get the list of threads or you can just pass the thread id that you want
     *
     * @param creatorCoreUserId    if it sets to '0' its considered as it was'nt set
     * @param partnerCoreUserId    if it sets to '0' its considered as it was'nt set -
     *                             it gets threads of p2p not groups
     * @param partnerCoreContactId if it sets to '0' its considered as it was'nt set-
     *                             it gets threads of p2p not groups
     * @param count                Count of the list
     * @param offset               Offset of the list
     * @param threadIds            List of thread ids that you want to get
     * @param threadName           Name of the thread that you want to get
     */
    @Deprecated
    public String getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,
                             long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, String typeCode) {

        String uniqueId;
        count = count != null ? count : 50;
        uniqueId = generateUniqueId();
        try {

            if (chatReady) {
                ChatMessageContent chatMessageContent = new ChatMessageContent();

                if (offset != null) {
                    chatMessageContent.setOffset(offset);
                } else {
                    chatMessageContent.setOffset(0);
                }

                chatMessageContent.setCount(count);
                if (threadName != null) {
                    chatMessageContent.setName(threadName);
                }
                JsonObject jObj;

                if (threadIds != null && threadIds.size() > 0) {
                    chatMessageContent.setThreadIds(threadIds);
                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);

                } else {
                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
                    jObj.remove("threadIds");
                }


                if (creatorCoreUserId > 0) {
                    jObj.addProperty("creatorCoreUserId", creatorCoreUserId);
                }
                if (partnerCoreUserId > 0) {
                    jObj.addProperty("partnerCoreUserId", partnerCoreUserId);
                }
                if (partnerCoreContactId > 0) {
                    jObj.addProperty("partnerCoreContactId", partnerCoreContactId);
                }

                jObj.remove("lastMessageId");
                jObj.remove("firstMessageId");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jObj.toString());
                chatMessage.setType(ChatMessageType.GET_THREADS);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), 3, "Get thread send");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    public String getThreads(RequestThread requestThread) {
        ArrayList<Integer> threadIds = requestThread.getThreadIds();
        long offset = requestThread.getOffset();
        long creatorCoreUserId = requestThread.getCreatorCoreUserId();
        long partnerCoreContactId = requestThread.getPartnerCoreContactId();
        long partnerCoreUserId = requestThread.getPartnerCoreUserId();
        String threadName = requestThread.getThreadName();
        long count = requestThread.getCount();
        String typeCode = requestThread.getTypeCode();

        return getThreads((int) count, offset, threadIds, threadName, creatorCoreUserId, partnerCoreUserId
                , partnerCoreContactId, typeCode);
    }

    /**
     * Get history of the thread
     * <p>
     * count    count of the messages
     * order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * and order must be lower case
     * lastMessageId
     * FirstMessageId
     *
     * @param threadId Id of the thread that we want to get the history
     */
    @Deprecated
    public String getHistory(History history, long threadId, String typeCode) {
        String uniqueId;
        uniqueId = generateUniqueId();

        if (history.getCount() != 0) {
            history.setCount(history.getCount());
        } else {
            history.setCount(50);
        }

        if (history.getOffset() != 0) {
            history.setOffset(history.getOffset());
        } else {
            history.setOffset(0);
        }

        if (chatReady) {
            getHistoryMain(history, threadId, uniqueId, typeCode);
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Gets history of the thread
     * <p>
     *
     * @Param count    count of the messages
     * @Param order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * lastMessageId
     * FirstMessageId
     * @Param long threadId   Id of the thread
     * @Param long fromTime    Start Time of the messages
     * @Param long fromTimeNanos  Start Time of the messages in Nano second
     * @Param long toTime         End time of the messages
     * @Param long toTimeNanos    End time of the messages
     * @Param @Deprecated long firstMessageId
     * @Param @Deprecated long lastMessageId
     * <p>
     * <p>
     * threadId Id of the thread that we want to get the history
     */
    public String getHistory(RequestGetHistory request) {
        String uniqueId = generateUniqueId();

        if (chatReady) {

            History history = new History.Builder()
                    .count(request.getCount())
                    .firstMessageId(request.getFirstMessageId())
                    .lastMessageId(request.getLastMessageId())
                    .offset(request.getOffset())
                    .fromTime(request.getFromTime())
                    .fromTimeNanos(request.getFromTimeNanos())
                    .toTime(request.getToTime())
                    .toTimeNanos(request.getToTimeNanos())
                    .order(request.getOrder())
                    .id(request.getId())
                    .uniqueIds(request.getUniqueIds())
                    .build();

            getHistoryMain(history, request.getThreadId(), uniqueId, request.getTypeCode());

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * It clears all messages in the thread
     *
     * @param requestClearHistory threadId  The id of the thread in which you want to clear all messages
     * @return
     */
    public String clearHistory(RequestClearHistory requestClearHistory) {
        String uniqueId = generateUniqueId();
        long threadId = requestClearHistory.getThreadId();

        if (chatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.CLEAR_HISTORY);
            chatMessage.setToken(getToken());
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setSubjectId(threadId);
            chatMessage.setUniqueId(uniqueId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");
            jsonObject.remove("contentCount");

            String typeCode = requestClearHistory.getTypeCode();

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_CLEAR_HISTORY");
        }
        return uniqueId;
    }

    /**
     * Get all of the contacts of the user
     */
    public String getContacts(RequestGetContact request) {

        Long offset = request.getOffset();
        Long count = request.getCount();
        String typeCode = request.getTypeCode();

        return getContacts(count.intValue(), offset, typeCode);
    }

    /**
     * Get all of the contacts of the user
     */
    public String getContacts(Integer count, Long offset, String typeCode) {
        return getContactMain(count, offset, typeCode);
    }

    /**
     * Add one contact to the contact list
     *
     * @param firstName       Notice: if just put fistName without lastName its ok.
     * @param lastName        last name of the contact
     * @param cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * @param email           email of the contact
     */
    public String addContact(String firstName, String lastName, String cellphoneNumber, String email, String typeCode) {

        if (Util.isNullOrEmpty(firstName)) {
            firstName = "";
        }
        if (Util.isNullOrEmpty(lastName)) {
            lastName = "";
        }
        if (Util.isNullOrEmpty(email)) {
            email = "";
        }
        if (Util.isNullOrEmpty(cellphoneNumber)) {
            cellphoneNumber = "";
        }

        String uniqueId = generateUniqueId();

        Call<Contacts> addContactService;

        if (chatReady) {

            if (!Util.isNullOrEmpty(typeCode)) {
                addContactService = contactApi.addContact(getToken(), TOKEN_ISSUER, firstName, lastName, email, uniqueId, cellphoneNumber, typeCode);

            } else {
                addContactService = contactApi.addContact(getToken(), TOKEN_ISSUER, firstName, lastName, email, uniqueId, cellphoneNumber, getTypeCode());
            }
            showInfoLog("ADD_CONTACT");

            RetrofitUtil.request(addContactService, new ApiListener<Contacts>() {
                @Override
                public void onSuccess(Contacts contacts) {
                    if (!contacts.getHasError()) {

                        ChatResponse<ResultAddContact> chatResponse = Util.getReformatOutPutAddContact(contacts, uniqueId);

                        String contactsJson = gson.toJson(chatResponse);

                        listenerManager.callOnAddContact(contactsJson, chatResponse);

                        showInfoLog("RECEIVED_ADD_CONTACT", contactsJson);

                    } else {
                        getErrorOutPut(contacts.getMessage(), contacts.getErrorCode(), uniqueId);
                    }

                }

                @Override
                public void onError(Throwable throwable) {
                    showErrorLog(throwable.getMessage());
                }

                @Override
                public void onServerError(Response<Contacts> response) {
                    if (response.body() != null) {
                        showErrorLog(response.body().getMessage());
                    } else {
                        showErrorLog(response.raw().toString());
                    }
                }
            });

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Add one contact to the contact list
     * <p>
     * firstName       Notice: if just put fistName without lastName its ok.
     * lastName        last name of the contact
     * cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * email           email of the contact
     */
    public String addContact(RequestAddContact request) {

        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String email = request.getEmail();
        String cellphoneNumber = request.getCellphoneNumber();
        String typeCode = request.getTypeCode();

        return addContact(firstName, lastName, cellphoneNumber, email, typeCode);
    }

    /**
     * Remove contact with the user id
     *
     * @param userId id of the user that we want to remove from contact list
     */
    public String removeContact(long userId, String typeCode) {
        String uniqueId = generateUniqueId();

        Call<ContactRemove> removeContactObservable;

        if (chatReady) {

            if (!Util.isNullOrEmpty(typeCode)) {
                removeContactObservable = contactApi.removeContact(getToken(), TOKEN_ISSUER, userId, typeCode);
            } else {
                removeContactObservable = contactApi.removeContact(getToken(), TOKEN_ISSUER, userId, getTypeCode());
            }

            RetrofitUtil.request(removeContactObservable, new ApiListener<ContactRemove>() {
                @Override
                public void onSuccess(ContactRemove contactRemove) {

                    if (contactRemove != null) {

                        if (!contactRemove.getHasError()) {

                            ChatResponse<ResultRemoveContact> chatResponse = new ChatResponse<>();
                            chatResponse.setUniqueId(uniqueId);
                            ResultRemoveContact resultRemoveContact = new ResultRemoveContact();
                            resultRemoveContact.setResult(contactRemove.isResult());

                            chatResponse.setResult(resultRemoveContact);

                            String json = gson.toJson(chatResponse);

                            listenerManager.callOnRemoveContact(json, chatResponse);

                            showInfoLog("RECEIVED_REMOVE_CONTACT", json);
                        } else {
                            getErrorOutPut(contactRemove.getErrorMessage(), contactRemove.getErrorCode(), uniqueId);
                        }
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    showErrorLog(throwable.getMessage());
                }

                @Override
                public void onServerError(Response<ContactRemove> response) {
                    showErrorLog(response.body().getErrorMessage());
                }
            });
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Remove contact with the user id
     * <p>
     * userId id of the user that we want to remove from contact list
     */
    public String removeContact(RequestRemoveContact request) {
        long userId = request.getUserId();
        String typeCode = request.getTypeCode();
        return removeContact(userId, typeCode);
    }

    /**
     * Update contacts
     * all of the params all required to update
     */
    public String updateContact(long userId, String firstName, String lastName, String cellphoneNumber, String email, String typeCode) {

        String uniqueId = generateUniqueId();

        if (Util.isNullOrEmpty(firstName)) {
            firstName = "";
        }

        if (Util.isNullOrEmpty(lastName)) {
            lastName = "";
        }

        if (Util.isNullOrEmpty(cellphoneNumber)) {
            cellphoneNumber = "";
        }

        if (Util.isNullOrEmpty(email)) {
            email = "";
        }

        if (chatReady) {
            Call<UpdateContact> updateContactObservable;

            if (!Util.isNullOrEmpty(typeCode)) {
                updateContactObservable = contactApi.updateContact(getToken(), TOKEN_ISSUER, userId, firstName, lastName, email, uniqueId, cellphoneNumber, typeCode);
            } else {
                updateContactObservable = contactApi.updateContact(getToken(), TOKEN_ISSUER, userId, firstName, lastName, email, uniqueId, cellphoneNumber, getTypeCode());
            }

            RetrofitUtil.request(updateContactObservable, new ApiListener<UpdateContact>() {
                @Override
                public void onSuccess(UpdateContact updateContact) {

                    if (updateContact != null) {

                        if (!updateContact.getHasError()) {

                            ChatResponse<ResultUpdateContact> chatResponse = new ChatResponse<>();
                            chatResponse.setUniqueId(uniqueId);

                            ResultUpdateContact resultUpdateContact = new ResultUpdateContact();

                            if (!Util.isNullOrEmpty(updateContact.getCount())) {
                                resultUpdateContact.setContentCount(updateContact.getCount());
                            }
                            resultUpdateContact.setContacts(updateContact.getResult());
                            chatResponse.setResult(resultUpdateContact);

                            String json = gson.toJson(chatResponse);

                            listenerManager.callOnUpdateContact(json, chatResponse);

                            showInfoLog("RECEIVE_UPDATE_CONTACT", json);

                        } else {
                            String errorMsg = updateContact.getMessage();
                            int errorCodeMsg = updateContact.getErrorCode();

                            errorMsg = errorMsg != null ? errorMsg : "";

                            getErrorOutPut(errorMsg, errorCodeMsg, uniqueId);
                        }
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    showErrorLog(throwable.getMessage());
                }

                @Override
                public void onServerError(Response<UpdateContact> response) {
                    showErrorLog(response.body().getMessage());
                }
            });

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

//TODO description

    /**
     * Update contacts
     * all of the params all required
     */
    public String updateContact(RequestUpdateContact request) {
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String email = request.getEmail();
        String cellphoneNumber = request.getCellphoneNumber();
        long userId = request.getUserId();
        String typeCode = request.getTypeCode();

        return updateContact(userId, firstName, lastName, cellphoneNumber, email, typeCode);
    }

    /**
     * @param requestSearchContact
     * @return
     */
    public String searchContact(RequestSearchContact requestSearchContact) {
        String uniqueId = generateUniqueId();
        String type_code;

        if (!Util.isNullOrEmpty(requestSearchContact.getTypeCode())) {
            type_code = requestSearchContact.getTypeCode();
        } else {
            type_code = getTypeCode();
        }

        String offset = (requestSearchContact.getOffset() == null) ? "0" : requestSearchContact.getOffset();
        String size = (requestSearchContact.getSize() == null) ? "50" : requestSearchContact.getSize();

        if (chatReady) {

            Call<SearchContactVO> searchContactCall = contactApi.searchContact(getToken(), TOKEN_ISSUER,
                    requestSearchContact.getId()
                    , requestSearchContact.getFirstName()
                    , requestSearchContact.getLastName()
                    , requestSearchContact.getEmail()
                    , offset
                    , size
                    , type_code
                    , requestSearchContact.getQuery()
                    , requestSearchContact.getCellphoneNumber());


            RetrofitUtil.request(searchContactCall, new ApiListener<SearchContactVO>() {
                @Override
                public void onSuccess(SearchContactVO searchContactVO) {
                    if (searchContactVO.getHasError())
                        getErrorOutPut(searchContactVO.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);

                    else {
                        ArrayList<Contact> contacts = new ArrayList<>(searchContactVO.getResult());

                        ResultContact resultContacts = new ResultContact();
                        resultContacts.setContacts(contacts);

                        ChatResponse<ResultContact> chatResponse = new ChatResponse<>();
                        chatResponse.setUniqueId(uniqueId);
                        chatResponse.setResult(resultContacts);

                        String content = gson.toJson(chatResponse);

                        listenerManager.callOnSearchContact(content, chatResponse);
                        listenerManager.callOnLogEvent(content);

                        showInfoLog("RECEIVE_SEARCH_CONTACT");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    showErrorLog(throwable.getMessage());
                }

                @Override
                public void onServerError(Response<SearchContactVO> response) {
                    String message = response.body().getMessage() != null ? response.body().getMessage() : "";
                    int errorCode = response.body().getErrorCode() != null ? response.body().getErrorCode() : 0;
                    getErrorOutPut(message, errorCode, uniqueId);
                }
            });

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * It deletes message from the thread.
     *
     * @param messageId    Id of the message that you want to be removed.
     * @param deleteForAll If you want to delete message for everyone you can set it true if u don't want
     *                     you can set it false or even null.
     */
    public String deleteMessage(Long messageId, Boolean deleteForAll, String typeCode) {
        String uniqueId = generateUniqueId();

        if (chatReady) {

            deleteForAll = deleteForAll != null ? deleteForAll : false;

            BaseMessage baseMessage = new BaseMessage();

            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("deleteForAll", deleteForAll);


            baseMessage.setContent(contentObj.toString());
            baseMessage.setToken(getToken());
            baseMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            baseMessage.setType(ChatMessageType.DELETE_MESSAGE);
            baseMessage.setUniqueId(uniqueId);
            baseMessage.setSubjectId(messageId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(baseMessage);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_DELETE_MESSAGE");
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * forward message
     *
     * @param threadId   destination thread id
     * @param messageIds Array of message ids that we want to forward them
     */
    public List<String> forwardMessage(long threadId, ArrayList<Long> messageIds, String typeCode) {
        ArrayList<String> uniqueIds = new ArrayList<>();
        ArrayList<Callback> callbacks = new ArrayList<>();

        for (long messageId : messageIds) {
            String uniqueId = generateUniqueId();
            uniqueIds.add(uniqueId);
            Callback callback = new Callback();
            callback.setDelivery(true);
            callback.setSeen(true);
            callback.setSent(true);
            callback.setUniqueId(uniqueId);
            callbacks.add(callback);
        }

        if (chatReady) {
            ChatMessageForward chatMessageForward = new ChatMessageForward();
            chatMessageForward.setSubjectId(threadId);

            String jsonUniqueIds = Util.listToJson(uniqueIds, gson);

            chatMessageForward.setUniqueId(jsonUniqueIds);
            chatMessageForward.setContent(messageIds.toString());
            chatMessageForward.setToken(getToken());
            chatMessageForward.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessageForward.setType(ChatMessageType.FORWARD_MESSAGE);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageForward);

            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_FORWARD_MESSAGE");

        } else {
            if (Util.isNullOrEmpty(uniqueIds)) {
                for (String uniqueId : uniqueIds) {
                    getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                }
            }
        }
        return uniqueIds;
    }

    /**
     * forward message
     * <p>
     * threadId   destination thread id
     * messageIds Array of message ids that we want to forward them
     */
    public List<String> forwardMessage(RequestForwardMessage request) {
        return forwardMessage(request.getThreadId(), request.getMessageIds(), request.getTypeCode());
    }

    /**
     * @param messageIds
     * @param deleteForAll
     */

    public List<String> deleteMultipleMessage(ArrayList<Long> messageIds, Boolean deleteForAll, String typeCode) {
        String uniqueId = generateUniqueId();

        List<String> uniqueIds = new ArrayList<>();

        if (chatReady) {

            deleteForAll = deleteForAll != null ? deleteForAll : false;

            BaseMessage baseMessage = new BaseMessage();


            for (Long id : messageIds) {

                String uniqueId1 = generateUniqueId();

                uniqueIds.add(uniqueId1);
            }

            JsonObject contentObj = new JsonObject();


            JsonElement messageIdsElement = gson.toJsonTree(messageIds, new TypeToken<List<Long>>() {
            }.getType());

            JsonElement uniqueIdsElement = gson.toJsonTree(uniqueIds, new TypeToken<List<String>>() {
            }.getType());


            contentObj.add("ids", messageIdsElement.getAsJsonArray());
            contentObj.add("uniqueIds", uniqueIdsElement.getAsJsonArray());
            contentObj.addProperty("deleteForAll", deleteForAll);


            baseMessage.setContent(contentObj.toString());
            baseMessage.setToken(getToken());
            baseMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            baseMessage.setType(ChatMessageType.DELETE_MESSAGE);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(baseMessage);


            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }


            jsonObject.remove("subjectId");

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_DELETE_MESSAGE");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueIds;
    }

    /**
     * DELETE MESSAGES IN THREAD
     * <p>
     * messageId    Id of the messages that you want to be removed.
     * deleteForAll If you want to delete messages for everyone you can set it true if u don't want
     * you can set it false or even null.
     */
    public List<String> deleteMultipleMessage(RequestDeleteMessage request) {

        return deleteMultipleMessage(request.getMessageIds(), request.isDeleteForAll(), request.getTypeCode());
    }

    /**
     * DELETE MESSAGE IN THREAD
     * <p>
     * messageId    Id of the message that you want to be removed.
     * deleteForAll If you want to delete message for everyone you can set it true if u don't want
     * you can set it false or even null.
     */
    public String deleteMessage(RequestDeleteMessage request) {
        if (request.getMessageIds().size() > 1) {
            return getErrorOutPut(ChatConstant.ERROR_NUMBER_MESSAGE_ID, ChatConstant.ERROR_CODE_NUMBER_MESSAGEID, null);

        }
        return deleteMessage(request.getMessageIds().get(0), request.isDeleteForAll(), request.getTypeCode());
    }

    /**
     * Get the participant list of specific thread
     * <p>
     *
     * @ param long threadId id of the thread we want to get the participant list
     * @ param long count number of the participant wanted to get
     * @ param long offset offset of the participant list
     */
    public String getThreadParticipants(RequestThreadParticipant request) {

        long count = request.getCount();
        long offset = request.getOffset();
        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();

        return getThreadParticipantsMain((int) count, offset, threadId, typeCode, false);
    }

    /**
     * Get the participant list of specific thread
     *
     * @param threadId id of the thread we want to ge the participant list
     */
    @Deprecated
    public String getThreadParticipants(Integer count, Long offset, long threadId) {
        return getThreadParticipantsMain(count, offset, threadId, null, false);
    }

    private String getThreadParticipantsMain(Integer count, Long offset, long threadId, String typeCode, boolean admin) {
        String uniqueId = generateUniqueId();

        offset = offset != null ? offset : 0;
        count = count != null ? count : 50;

        if (chatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setCount(count);
            chatMessageContent.setOffset(offset);
            chatMessageContent.setAdmin(admin);

            String content = gson.toJson(chatMessageContent);

            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setContent(content);
            chatMessage.setType(ChatMessageType.THREAD_PARTICIPANTS);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            jsonObject.remove("lastMessageId");
            jsonObject.remove("firstMessageId");
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 3, "SEND_THREAD_PARTICIPANT");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;

    }

    public String createThread(RequestCreateThread requestCreateThread) {
        int threadType = requestCreateThread.getType();
        Invitee[] invitee = (Invitee[]) requestCreateThread.getInvitees().toArray();
        String threadTitle = requestCreateThread.getTitle();
        String description = requestCreateThread.getDescription();
        String image = requestCreateThread.getImage();
        String metadata = requestCreateThread.getMetadata();
        String typeCode = requestCreateThread.getTypeCode();

        return createThread(threadType, invitee, threadTitle, description, image, metadata, typeCode);
    }

    /**
     * Create the thread to p to p/channel/group. The list below is showing all of the threads type
     * int NORMAL = 0;
     * int OWNER_GROUP = 1;
     * int PUBLIC_GROUP = 2;
     * int CHANNEL_GROUP = 4;
     * int TO_BE_USER_ID = 5;
     * <p>
     * int CHANNEL = 8;
     */
    public String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
            , String metadata, String typeCode) {

        String uniqueId = generateUniqueId();

        if (chatReady) {
            List<Invitee> invitees = new ArrayList<>(Arrays.asList(invitee));

            ChatThread chatThread = new ChatThread();
            chatThread.setType(threadType);
            chatThread.setInvitees(invitees);
            chatThread.setTitle(threadTitle);

            JsonObject chatThreadObject = (JsonObject) gson.toJsonTree(chatThread);

            if (Util.isNullOrEmpty(description)) {
                chatThreadObject.remove("description");
            } else {
                chatThreadObject.remove("description");
                chatThreadObject.addProperty("description", description);
            }

            if (Util.isNullOrEmpty(image)) {
                chatThreadObject.remove("image");
            } else {
                chatThreadObject.remove("image");
                chatThreadObject.addProperty("image", image);
            }

            if (Util.isNullOrEmpty(metadata)) {
                chatThreadObject.remove("metadata");

            } else {
                chatThreadObject.remove("metadata");
                chatThreadObject.addProperty("metadata", metadata);
            }
            String contentThreadChat = chatThreadObject.toString();

            ChatMessage chatMessage = getChatMessage(contentThreadChat, uniqueId, getTypeCode());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_CREATE_THREAD");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * Create the thread with message is just for  p to p.( Thread Type is int NORMAL = 0)
     *
     * @return The first UniqueId is for create thread and the rest of them are for new message or forward messages
     * Its have three kind of Unique Ids. One of them is for message. One of them for Create Thread
     * and the others for Forward Message or Messages.
     * <p>
     * int type  Type of the Thread (You can have Thread Type from ThreadType.class)
     * String ownerSsoId  [Optional]
     * List<Invitee> invitees  you can add your invite list here
     * String title  [Optional] title of the group thread
     * <p>
     * RequestThreadInnerMessage message{  object of the inner message
     * <p>
     * -------------  String text  text of the message
     * -------------  int type  type of the message  [Optional]
     * -------------  String metadata  [Optional]
     * -------------  String systemMetadata  [Optional]
     * -------------  List<Long> forwardedMessageIds  [Optional]
     * }
     */
    public ArrayList<String> createThreadWithMessage(RequestCreateThreadWithMessage threadRequest) {
        List<String> forwardUniqueIds;
        JsonObject innerMessageObj = null;
        JsonObject jsonObject;

        String threadUniqueId = generateUniqueId();

        ArrayList<String> uniqueIds = new ArrayList<>();
        uniqueIds.add(threadUniqueId);
        try {
            if (chatReady) {

                if (threadRequest.getMessage() != null) {
                    RequestThreadInnerMessage innerMessage = threadRequest.getMessage();
                    innerMessageObj = (JsonObject) gson.toJsonTree(innerMessage);

                    if (Util.isNullOrEmpty(threadRequest.getMessage().getType())) {
                        innerMessageObj.remove("type");
                    }

                    if (Util.isNullOrEmpty(threadRequest.getMessage().getText())) {
                        innerMessageObj.remove("message");
                    } else {
                        String newMsgUniqueId = generateUniqueId();

                        innerMessageObj.addProperty("uniqueId", newMsgUniqueId);
                        uniqueIds.add(newMsgUniqueId);
                    }

                    if (!Util.isNullOrEmptyNumber(threadRequest.getMessage().getForwardedMessageIds())) {

                        /** Its generated new unique id for each forward message*/
                        List<Long> messageIds = threadRequest.getMessage().getForwardedMessageIds();
                        forwardUniqueIds = new ArrayList<>();

                        for (long ids : messageIds) {
                            String frwMsgUniqueId = generateUniqueId();

                            forwardUniqueIds.add(frwMsgUniqueId);
                            uniqueIds.add(frwMsgUniqueId);
                        }
                        JsonElement element = gson.toJsonTree(forwardUniqueIds, new TypeToken<List<Long>>() {
                        }.getType());

                        JsonArray jsonArray = element.getAsJsonArray();
                        innerMessageObj.add("forwardedUniqueIds", jsonArray);
                    } else {
                        innerMessageObj.remove("forwardedUniqueIds");
                        innerMessageObj.remove("forwardedMessageIds");
                    }
                }

                JsonObject jsonObjectCreateThread = (JsonObject) gson.toJsonTree(threadRequest);

                jsonObjectCreateThread.remove("count");
                jsonObjectCreateThread.remove("offset");
                jsonObjectCreateThread.add("message", innerMessageObj);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jsonObjectCreateThread.toString());
                chatMessage.setType(ChatMessageType.INVITATION);
                chatMessage.setUniqueId(threadUniqueId);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));


                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                jsonObject.remove("repliedTo");
                jsonObject.remove("subjectId");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("contentCount");

                String typeCode = threadRequest.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_CREATE_THREAD_WITH_MESSAGE");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueIds;
    }

    /**
     * It updates the information of the thread like
     * image;
     * name;
     * description;
     * metadata;
     */
    public String updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO) {
        String uniqueId;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                JsonObject jObj = new JsonObject();

                jObj.addProperty("name", threadInfoVO.getTitle());
                jObj.addProperty("description", threadInfoVO.getDescription());
                jObj.addProperty("metadata", threadInfoVO.getMetadata());
                jObj.addProperty("image", threadInfoVO.getImage());

                String content = jObj.toString();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(content);

                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setToken(getToken());
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setType(ChatMessageType.UPDATE_THREAD_INFO);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                }

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_UPDATE_THREAD_INFO");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }

    /**
     * It updates the information of the thread like
     * image;
     * name;
     * description;
     * metadata;
     */

    public String updateThreadInfo(RequestThreadInfo request) {
        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder().title(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .metadat(request.getMetadata())
                .build();
        return updateThreadInfo(request.getThreadId(), threadInfoVO);
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     * <p>
     * messageContent content of the reply message
     * threadId       id of the thread
     * messageId      of the message that we want to reply
     * metaData       meta data of the message
     */
    public String replyMessage(RequestReplyMessage request) {
        long threadId = request.getThreadId();
        long messageId = request.getMessageId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        int messageType = request.getMessageType();
        String typeCode = request.getTypeCode();

        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, typeCode);
    }

    private String mainReplyMessage(String messageContent, long threadId, long messageId, String systemMetaData,
                                    Integer messageType, String metaData, String typeCode) {
        String uniqueId;
        uniqueId = generateUniqueId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setRepliedTo(messageId);
        chatMessage.setSubjectId(threadId);
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        chatMessage.setToken(getToken());
        chatMessage.setContent(messageContent);
        chatMessage.setMetadata(metaData);
        chatMessage.setType(ChatMessageType.MESSAGE);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(systemMetaData)) {
            jsonObject.remove("systemMetaData");
        } else {
            jsonObject.remove("systemMetaData");
            jsonObject.addProperty("systemMetaData", systemMetaData);
        }

        if (!Util.isNullOrEmpty(typeCode)) {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", typeCode);
        } else if (!Util.isNullOrEmpty(getTypeCode())) {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", getTypeCode());
        } else {
            jsonObject.remove("typeCode");
        }

        if (Util.isNullOrEmpty(messageType)) {
            jsonObject.remove("messageType");
        } else {
            jsonObject.remove("messageType");
            jsonObject.addProperty("messageType", messageType);
        }

        String asyncContent = jsonObject.toString();

        if (chatReady) {
            sendAsyncMessage(asyncContent, 4, "SEND_REPLY_MESSAGE");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     *
     * @param messageContent content of the reply message
     * @param threadId       id of the thread
     * @param messageId      of the message that we want to reply
     * @param systemMetaData meta data of the message
     */
    public String replyMessage(String messageContent, long threadId, long messageId, String systemMetaData
            , Integer messageType, String typeCode) {
        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, typeCode);
    }

    /**
     * In order to send seen message you have to call this method
     */
    public String seenMessage(long messageId, long ownerId) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            if (ownerId != getUserId()) {
                ChatMessage message = new ChatMessage();
                message.setType(ChatMessageType.SEEN);
                message.setContent(String.valueOf(messageId));
                message.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                message.setToken(getToken());
                message.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(message);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, 4, "SEND_SEEN_MESSAGE");

            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * In order to send seen message you have to call {@link #seenMessage(long, long)}
     */
    public String seenMessage(RequestSeenMessage request) {
        long messageId = request.getMessageId();
        long ownerId = request.getOwnerId();

        return seenMessage(messageId, ownerId);
    }

    /**
     * It Gets the information of the current user
     */
    public String getUserInfo() {
        String uniqueId = generateUniqueId();
        try {
            if (asyncReady) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.USER_INFO);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                showInfoLog("SEND_USER_INFO", asyncContent);

                async.sendMessage(asyncContent, 3);

            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * It uploads image to the server just by pass image path
     *
     * @param requestUploadImage filePath   the path of image File
     *                           xC   the X coordinate of the upper-left corner of the specified rectangular region
     *                           yC   the Y coordinate of the upper-left corner of the specified rectangular region
     *                           wC   the width of the specified rectangular region
     *                           hC   the height of the specified rectangular region
     * @return
     */
    public String uploadImage(RequestUploadImage requestUploadImage) {
        String filePath = requestUploadImage.getFilePath();
        int xC = requestUploadImage.getxC();
        int yC = requestUploadImage.getyC();
        int hC = requestUploadImage.gethC();
        int wC = requestUploadImage.getwC();

        if (filePath.endsWith(".gif")) {
            return uploadFile(filePath);
        } else {
            return uploadImage(filePath, xC, yC, hC, wC);
        }
    }

    /**
     * @param filePath the path of image File
     * @param xC       the X coordinate of the upper-left corner of the specified rectangular region
     * @param yC       the Y coordinate of the upper-left corner of the specified rectangular region
     * @param hC       the height of the specified rectangular region
     * @param wC       the width of the specified rectangular region
     * @return
     */
    @Deprecated
    public String uploadImage(String filePath, int xC, int yC, int hC, int wC) {
        String uniqueId = generateUniqueId();

        if (chatReady) {

            try {
                if (fileServer != null && filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);

                    if (file.exists()) {

                        String mimeType = getContentType(file);

                        if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {
                            FileApi fileApi;
                            RequestBody requestFile;

                            if (!Util.isNullOrEmpty(hC) && !Util.isNullOrEmpty(wC)) {
                                BufferedImage originalImage = ImageIO.read(file);

                                BufferedImage subImage = originalImage.getSubimage(xC, yC, wC, hC);

                                File outputFile = File.createTempFile("test", null);

                                ImageIO.write(subImage, mimeType.substring(mimeType.indexOf("/") + 1), outputFile);

                                fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);

                                requestFile = RequestBody.create(MediaType.parse("image/*"), outputFile);
                            } else {

                                fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);

                                requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                            }

                            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                            Call<FileImageUpload> fileImageUploadCall = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);
                            String finalUniqueId = uniqueId;
                            String finalUniqueId1 = uniqueId;

                            RetrofitUtil.request(fileImageUploadCall, new ApiListener<FileImageUpload>() {

                                @Override
                                public void onSuccess(FileImageUpload fileImageUpload) {
                                    boolean hasError = fileImageUpload.isHasError();

                                    if (hasError) {
                                        String errorMessage = fileImageUpload.getMessage();
                                        int errorCode = fileImageUpload.getErrorCode();
                                        String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId1);

                                        showErrorLog(jsonError);
                                    } else {
                                        ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
                                        ResultImageFile resultImageFile = new ResultImageFile();
                                        chatResponse.setUniqueId(finalUniqueId);

                                        resultImageFile.setId(fileImageUpload.getResult().getId());
                                        resultImageFile.setHashCode(fileImageUpload.getResult().getHashCode());
                                        resultImageFile.setName(fileImageUpload.getResult().getName());
                                        resultImageFile.setHeight(fileImageUpload.getResult().getHeight());
                                        resultImageFile.setWidth(fileImageUpload.getResult().getWidth());
                                        resultImageFile.setActualHeight(fileImageUpload.getResult().getActualHeight());
                                        resultImageFile.setActualWidth(fileImageUpload.getResult().getActualWidth());

                                        chatResponse.setResult(resultImageFile);

                                        String imageJson = gson.toJson(chatResponse);

                                        listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                                        showInfoLog("RECEIVE_UPLOAD_IMAGE");

                                        listenerManager.callOnLogEvent(imageJson);
                                    }

                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    showErrorLog(throwable.getMessage());
                                }

                                @Override
                                public void onServerError(Response<FileImageUpload> response) {
                                    showErrorLog(response.body().getMessage());
                                }
                            });
                        } else {
                            String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);

                            showErrorLog(jsonError);
                            uniqueId = null;
                        }
                    }

                } else {
                    showErrorLog("FileServer url Is null");

                    uniqueId = null;
                }
            } catch (Exception e) {
                showErrorLog(e.getCause().getMessage());
                getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                uniqueId = null;
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * It uploads file to file server
     */
    @Deprecated
    public String uploadFile(String path) {
        String uniqueId = generateUniqueId();
        if (chatReady) {
            try {
                if (getFileServer() != null && path != null && !path.isEmpty()) {

                    File file = new File(path);
                    String mimeType = getContentType(file);

                    if (file.exists()) {

                        long fileSize = file.length();
                        FileApi fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);

                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        Call<FileUpload> fileUploadCall = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);

                        String finalUniqueId = uniqueId;
                        RetrofitUtil.request(fileUploadCall, new ApiListener<FileUpload>() {

                            @Override
                            public void onSuccess(FileUpload fileUpload) {
                                boolean hasError = fileUpload.isHasError();
                                if (hasError) {
                                    String errorMessage = fileUpload.getMessage();
                                    int errorCode = fileUpload.getErrorCode();
                                    String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                    showErrorLog(jsonError);
                                } else {
                                    ResultFile result = fileUpload.getResult();

                                    ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                                    result.setSize(fileSize);
                                    chatResponse.setUniqueId(finalUniqueId);
                                    chatResponse.setResult(result);
                                    String json = gson.toJson(chatResponse);

                                    listenerManager.callOnUploadFile(json, chatResponse);
                                    showInfoLog("RECEIVE_UPLOAD_FILE" + json);
                                    listenerManager.callOnLogEvent(json);
                                }

                            }

                            @Override
                            public void onError(Throwable throwable) {
                                showErrorLog(throwable.getMessage());
                            }

                            @Override
                            public void onServerError(Response<FileUpload> response) {
                                showErrorLog(response.body().getMessage());
                            }
                        });
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                        showErrorLog(jsonError);
                        uniqueId = null;
                    }

                } else {
                    showErrorLog("File is not Exist");
                    return null;
                }

            } catch (Exception e) {
                showErrorLog(e.getCause().getMessage());
                return null;
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    public String uploadFile(RequestUploadFile requestUploadFile) {
        return uploadFile(requestUploadFile.getFilePath());
    }

    /**
     * This method first check the type of the file and then choose the right
     * server and send that
     *
     * @param description    Its the description that you want to send with file in the thread
     * @param filePath       Path of the file that you want to send to thread
     * @param threadId       Id of the thread that you want to send file
     * @param systemMetaData [optional]
     * @param xC             The X coordinate of the upper-left corner of the specified rectangular region [optional - for image file]
     * @param yC             The Y coordinate of the upper-left corner of the specified rectangular region[optional - for image file]
     * @param hC             The height of the specified rectangular region[optional -  for image file]
     * @param wC             The width of the specified rectangular region[optional - for image file]
     * @param handler        It is for send file message with progress
     * @return
     */
    public String sendFileMessage(String description,
                                  long threadId,
                                  String filePath,
                                  String systemMetaData,
                                  Integer messageType,
                                  int xC,
                                  int yC,
                                  int hC,
                                  int wC,
                                  ProgressHandler.sendFileMessage handler) {

        String uniqueId = generateUniqueId();

        LFileUpload lFileUpload = new LFileUpload();
        lFileUpload.setDescription(description);
        lFileUpload.setFilePath(filePath);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);
        lFileUpload.setThreadId(threadId);
        lFileUpload.setUniqueId(uniqueId);
        lFileUpload.setSystemMetaData(systemMetaData);
        lFileUpload.setxC(xC);
        lFileUpload.setyC(yC);
        lFileUpload.sethC(hC);
        lFileUpload.setwC(wC);

        try {
            if (filePath != null) {
                File file = new File(filePath);
                String mimeType = getContentType(file);
                lFileUpload.setMimeType(mimeType);

                if (FileUtils.isImage(mimeType)) {
                    uploadImageFileMessage(lFileUpload);
                } else {
                    uploadFileMessage(lFileUpload);
                }
                return uniqueId;

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_INVALID_FILE_URI
                        , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);

                ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);

                listenerManager.callOnLogEvent(jsonError);

                if (handler != null) {
                    handler.onError(jsonError, error);
                }
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());

            return null;
        }
        return uniqueId;
    }

    /**
     * @param requestFileMessage description    Its the description that you want to send with file in the thread
     *                           filePath       Path of the file that you want to send to thread
     *                           threadId       Id of the thread that you want to send file
     *                           systemMetaData [optional]
     *                           xC             The X coordinate of the upper-left corner of the specified rectangular region [optional - for image file]
     *                           yC             The Y coordinate of the upper-left corner of the specified rectangular region[optional - for image file]
     *                           hC             The height of the specified rectangular region[optional -  for image file]
     *                           wC             The width of the specified rectangular region[optional - for image file]
     * @param handler
     * @return
     */
    public String sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler) {
        long threadId = requestFileMessage.getThreadId();
        String filePath = requestFileMessage.getFilePath();
        String description = requestFileMessage.getDescription();
        int messageType = requestFileMessage.getMessageType();
        String systemMetadata = requestFileMessage.getSystemMetadata();
        int xC = requestFileMessage.getxC();
        int yC = requestFileMessage.getyC();
        int hC = requestFileMessage.gethC();
        int wC = requestFileMessage.getwC();

        return sendFileMessage(description, threadId, filePath, systemMetadata, messageType, xC, yC, hC, wC, handler);
    }

    private void uploadFileMessage(LFileUpload lFileUpload) {

        String filePath = lFileUpload.getFilePath();

        try {
            if (Util.isNullOrEmpty(filePath)) {
                filePath = "";
            }
            File file = new File(filePath);
            long file_size;

            if (file.exists() || file.isFile()) {
                file_size = file.length();

                lFileUpload.setFileSize(file_size);
                lFileUpload.setFile(file);

                mainUploadFileMessage(lFileUpload);


            } else {
                showErrorLog("File Is Not Exist");
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void mainUploadFileMessage(LFileUpload lFileUpload) {

        String description = lFileUpload.getDescription();
        Integer messageType = lFileUpload.getMessageType();
        long threadId = lFileUpload.getThreadId();
        String uniqueId = lFileUpload.getUniqueId();
        String systemMetadata = lFileUpload.getSystemMetaData();
        long messageId = lFileUpload.getMessageId();
        String mimeType = lFileUpload.getMimeType();
        File file = lFileUpload.getFile();
        long file_size = lFileUpload.getFileSize();
        String typeCode = lFileUpload.getTypeCode();

        String methodName = lFileUpload.getMethodName();

        if (chatReady) {
            if (getFileServer() != null) {
                FileApi fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);

                RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                Call<FileUpload> fileUploadCall = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);
                File finalFile = file;

                RetrofitUtil.request(fileUploadCall, new ApiListener<FileUpload>() {

                    @Override
                    public void onSuccess(FileUpload fileUpload) {
                        boolean error = fileUpload.isHasError();

                        if (error) {
                            String errorMessage = fileUpload.getMessage();

                            showErrorLog(errorMessage);

                        } else {
                            ResultFile result = fileUpload.getResult();

                            if (result != null) {
                                long fileId = result.getId();
                                String hashCode = result.getHashCode();

                                ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                                chatResponse.setResult(result);
                                chatResponse.setUniqueId(uniqueId);
                                result.setSize(file_size);
                                String json = gson.toJson(chatResponse);

                                listenerManager.callOnUploadFile(json, chatResponse);

                                showInfoLog("RECEIVE_UPLOAD_FILE");

                                listenerManager.callOnLogEvent(json);

                                String jsonMeta = createFileMetadata(finalFile.getName(),
                                        hashCode,
                                        fileId,
                                        mimeType,
                                        file_size,
                                        "");

                                listenerManager.callOnLogEvent(jsonMeta);

                                if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {
                                    mainReplyMessage(description, threadId, messageId, systemMetadata, messageType, jsonMeta, typeCode);

                                    showInfoLog("SEND_REPLY_FILE_MESSAGE");

                                } else {
                                    sendTextMessageWithFile(description, threadId, jsonMeta, systemMetadata, uniqueId, typeCode, messageType);
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showErrorLog(throwable.getMessage());
                    }

                    @Override
                    public void onServerError(Response<FileUpload> response) {
                        showErrorLog(response.body().getMessage());
                    }
                });
            } else {
                showErrorLog("FileServer url Is null");
            }

        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            listenerManager.callOnLogEvent(jsonError);
        }
    }

    private void sendTextMessageWithFile(String description, long threadId, String metaData, String systemMetadata, String uniqueId, String typeCode, Integer messageType) {

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setContent(description);
        chatMessage.setType(ChatMessageType.MESSAGE);
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        chatMessage.setToken(getToken());
        chatMessage.setMetadata(metaData);

        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(getTypeCode())) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", getTypeCode());
        }

        jsonObject.remove("repliedTo");

        String asyncContent = jsonObject.toString();


        if (chatReady) {
            sendAsyncMessage(asyncContent, 4, "SEND_TXT_MSG_WITH_FILE");
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
    }

    /**
     * description    description of the message
     * threadId       id of the thread its wanted to send in
     * fileUri        uri of the file
     * mimeType       mime type of the file
     * systemMetaData metadata of the message
     * messageType    type of a message
     * messageId      id of a message
     * methodName     METHOD_REPLY_MSG or other
     * handler        description of the interface methods are :
     * bytesSent        - Bytes sent since the last time this callback was called.
     * totalBytesSent   - Total number of bytes sent so far.
     * totalBytesToSend - Total bytes to send.
     */
    private void uploadImageFileMessage(LFileUpload lFileUpload) {

        String description = lFileUpload.getDescription();
        String filePath = lFileUpload.getFilePath();
        Integer messageType = lFileUpload.getMessageType();
        String uniqueId = lFileUpload.getUniqueId();
        String systemMetaData = lFileUpload.getSystemMetaData();
        String center = lFileUpload.getCenter();


        systemMetaData = systemMetaData != null ? systemMetaData : "";
        description = description != null ? description : "";
        messageType = messageType != null ? messageType : 0;

        if (Util.isNullOrEmpty(filePath)) {
            filePath = "";
        }
        File file = new File(filePath);

        if (file.exists()) {
            long fileSize = file.length();

            lFileUpload.setFile(file);
            lFileUpload.setFileSize(fileSize);
            lFileUpload.setSystemMetaData(systemMetaData);
            lFileUpload.setDescription(description);
            lFileUpload.setMessageType(messageType);
            lFileUpload.setCenter(center);

            mainUploadImageFileMsg(lFileUpload);

        } else {
            showErrorLog("File Is Not Exist");
        }

    }

    private void mainUploadImageFileMsg(LFileUpload fileUpload) {

        String description = fileUpload.getDescription();

        ProgressHandler.sendFileMessage handler = fileUpload.getHandler();

        Integer messageType = fileUpload.getMessageType();
        long threadId = fileUpload.getThreadId();
        String uniqueId = fileUpload.getUniqueId();
        String systemMetaData = fileUpload.getSystemMetaData();
        long messageId = fileUpload.getMessageId();
        String mimeType = fileUpload.getMimeType();
        String methodName = fileUpload.getMethodName();
        long fileSize = fileUpload.getFileSize();
        String center = fileUpload.getCenter();
        String typeCode = fileUpload.getTypeCode();

        File file = fileUpload.getFile();
        try {
            if (chatReady) {

                if (fileServer != null) {
                    FileApi fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);

                    RequestBody requestFile;

                    if (!Util.isNullOrEmpty(fileUpload.gethC()) && !Util.isNullOrEmpty(fileUpload.getwC())) {
                        BufferedImage originalImage = ImageIO.read(file);

                        BufferedImage subImage = originalImage.getSubimage(fileUpload.getxC(), fileUpload.getyC(), fileUpload.getwC(), fileUpload.gethC());

                        File outputFile = File.createTempFile("test", null);

                        ImageIO.write(subImage, mimeType.substring(mimeType.indexOf("/") + 1), outputFile);


                        requestFile = RequestBody.create(MediaType.parse("image/*"), outputFile);
                    } else {


                        requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    }

                    MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                    Call<FileImageUpload> fileImageUploadCall = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);

                    RetrofitUtil.request(fileImageUploadCall, new ApiListener<FileImageUpload>() {

                        @Override
                        public void onSuccess(FileImageUpload fileImageUpload) {
                            boolean hasError = fileImageUpload.isHasError();
                            if (hasError) {
                                String errorMessage = fileImageUpload.getMessage();
                                int errorCode = fileImageUpload.getErrorCode();
                                String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);

                                listenerManager.callOnLogEvent(jsonError);

                                ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);

                                if (handler != null) {
                                    handler.onError(jsonError, error);
                                }
                            } else {

                                ResultImageFile result = fileImageUpload.getResult();
                                long imageId = result.getId();
                                String hashCode = result.getHashCode();

                                ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
                                ResultImageFile resultImageFile = new ResultImageFile();
                                chatResponse.setUniqueId(uniqueId);
                                resultImageFile.setId(result.getId());
                                resultImageFile.setHashCode(result.getHashCode());
                                resultImageFile.setName(result.getName());
                                resultImageFile.setHeight(result.getHeight());
                                resultImageFile.setWidth(result.getWidth());
                                resultImageFile.setActualHeight(result.getActualHeight());
                                resultImageFile.setActualWidth(result.getActualWidth());

                                chatResponse.setResult(resultImageFile);

                                String imageJson = gson.toJson(chatResponse);

                                listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                                showInfoLog("RECEIVE_UPLOAD_IMAGE");

                                listenerManager.callOnLogEvent(imageJson);

                                String metaJson;
                                if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_LOCATION_MSG)) {
                                    metaJson = createImageMetadata(file, hashCode, imageId, result.getActualHeight()
                                            , result.getActualWidth(), mimeType, fileSize, null, true, center);

                                } else {
                                    metaJson = createImageMetadata(file, hashCode, imageId, result.getActualHeight()
                                            , result.getActualWidth(), mimeType, fileSize, null, false, null);
                                }

                                // send to handler
                                if (handler != null) {
                                    handler.onFinishImage(imageJson, chatResponse);
                                }

                                if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {
                                    mainReplyMessage(description, threadId, messageId, systemMetaData, messageType, metaJson, typeCode);

                                    showInfoLog("SEND_REPLY_FILE_MESSAGE");

                                    listenerManager.callOnLogEvent(metaJson);
                                } else {
                                    sendTextMessageWithFile(description, threadId, metaJson, systemMetaData, uniqueId, typeCode, messageType);
                                }

                                listenerManager.callOnLogEvent(metaJson);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            showErrorLog(throwable.getMessage());
                        }

                        @Override
                        public void onServerError(Response<FileImageUpload> response) {
                            showErrorLog(response.body().getMessage());
                        }
                    });

                } else {
                    showErrorLog("FileServer url Is null");
                }

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);

                listenerManager.callOnLogEvent(jsonError);
            }
        } catch (IOException e) {
            showErrorLog(e);
            getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);

        }
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     * <p>
     * messageContent content of the reply message
     * threadId       id of the thread
     * messageId      of the message that we want to reply
     * metaData       meta data of the message
     */
    public String replyFileMessage(RequestReplyFileMessage request, ProgressHandler.sendFileMessage handler) {
        String uniqueId = generateUniqueId();
        long threadId = request.getThreadId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        String filePath = request.getFilePath();
        long messageId = request.getMessageId();
        int messageType = request.getMessageType();
        String methodName = ChatConstant.METHOD_REPLY_MSG;
        String typeCode;

        if (!Util.isNullOrEmpty(request.getTypeCode())) {
            typeCode = request.getTypeCode();
        } else {
            typeCode = getTypeCode();
        }

        LFileUpload lFileUpload = new LFileUpload();
        lFileUpload.setDescription(messageContent);
        lFileUpload.setFilePath(filePath);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);
        lFileUpload.setMessageId(messageId);
        lFileUpload.setMethodName(methodName);
        lFileUpload.setThreadId(threadId);
        lFileUpload.setUniqueId(uniqueId);
        lFileUpload.setSystemMetaData(systemMetaData);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);
        lFileUpload.setxC(request.getxC());
        lFileUpload.setyC(request.getyC());
        lFileUpload.setwC(request.getwC());
        lFileUpload.sethC(request.gethC());
        lFileUpload.setTypeCode(typeCode);

        try {
            if (filePath != null) {
                File file = new File(filePath);
                String mimeType = getContentType(file);

                lFileUpload.setMimeType(mimeType);

                if (FileUtils.isImage(mimeType)) {
                    uploadImageFileMessage(lFileUpload);
                } else {
                    uploadFileMessage(lFileUpload);
                }

                return uniqueId;

            } else {
                getErrorOutPut(ChatConstant.ERROR_INVALID_URI, ChatConstant.ERROR_CODE_INVALID_URI, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());

            return null;
        }
        return uniqueId;
    }

    /**
     * Message can be edit when you pass the message id and the edited
     * content in order to edit your Message.
     */
    @Deprecated
    public String editMessage(int messageId, String messageContent, String systemMetaData) {
        String uniqueId = generateUniqueId();
        try {
            if (chatReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.EDIT_MESSAGE);
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(messageId);
                chatMessage.setContent(messageContent);
                chatMessage.setSystemMetadata(systemMetaData);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                jsonObject.remove("metadata");

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, 4, "SEND_EDIT_MESSAGE");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * Message can be edit when you pass the message id and the edited
     * content in order to edit your Message.
     */
    public String editMessage(RequestEditMessage request) {
        String uniqueId = generateUniqueId();
        try {
            JsonObject jsonObject;
            if (chatReady) {

                String messageContent = request.getMessageContent();
                long messageId = request.getMessageId();
                String metaData = request.getMetaData();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.EDIT_MESSAGE);
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(messageId);
                chatMessage.setContent(messageContent);
                chatMessage.setSystemMetadata(metaData);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (!Util.isNullOrEmpty(request.getTypeCode())) {
                    jsonObject.addProperty("typeCode", request.getTypeCode());
                } else if (!Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_EDIT_MESSAGE");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * @param contactIds List of CONTACT IDs
     * @param threadId   Id of the thread that you are {*NOTICE*}admin of that and you want to
     *                   add someone as a participant.
     */
    @Deprecated
    public String addParticipants(long threadId, List<Long> contactIds) {
        String uniqueId = generateUniqueId();
        try {

            if (chatReady) {
                AddParticipant addParticipant = new AddParticipant();
                addParticipant.setSubjectId(threadId);
                addParticipant.setUniqueId(uniqueId);
                JsonArray contacts = new JsonArray();
                for (Long p : contactIds) {
                    contacts.add(p);
                }
                addParticipant.setContent(contacts.toString());
                addParticipant.setSubjectId(threadId);
                addParticipant.setToken(getToken());
                addParticipant.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                addParticipant.setUniqueId(uniqueId);
                addParticipant.setType(ChatMessageType.ADD_PARTICIPANT);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(addParticipant);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, 4, "SEND_ADD_PARTICIPANTS");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }


        } catch (Throwable t) {
            showErrorLog(t.getCause().getMessage());
        }

        return uniqueId;
    }

    /**
     * contactIds  List of CONTACT IDs
     * threadId   Id of the thread that you are {*NOTICE*}admin of that and you are going to
     * add someone as a participant.
     */
    public String addParticipants(RequestAddParticipants request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {
            JsonArray contacts = new JsonArray();

            for (Long p : request.getContactIds()) {
                contacts.add(p);
            }
            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(getToken());
            chatMessage.setContent(contacts.toString());
            chatMessage.setSubjectId(request.getThreadId());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setType(ChatMessageType.ADD_PARTICIPANT);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            String typeCode = request.getTypeCode();

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            sendAsyncMessage(jsonObject.toString(), 4, "SEND_ADD_PARTICIPANTS");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * @param participantIds List of PARTICIPANT IDs that gets from {@link #getThreadParticipants}
     * @param threadId       Id of the thread that we wants to remove their participant
     */
    public String removeParticipants(long threadId, List<Long> participantIds, String typeCode) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            RemoveParticipant removeParticipant = new RemoveParticipant();

            removeParticipant.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            removeParticipant.setType(ChatMessageType.REMOVE_PARTICIPANT);
            removeParticipant.setSubjectId(threadId);
            removeParticipant.setToken(getToken());
            removeParticipant.setUniqueId(uniqueId);

            JsonArray contacts = new JsonArray();

            for (Long p : participantIds) {
                contacts.add(p);
            }
            removeParticipant.setContent(contacts.toString());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(removeParticipant);

            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_REMOVE_PARTICIPANT");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * participantIds List of PARTICIPANT IDs from Thread's Participants object
     * threadId       Id of the thread that we wants to remove their participant
     */
    public String removeParticipants(RequestRemoveParticipants request) {

        List<Long> participantIds = request.getParticipantIds();
        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();

        return removeParticipants(threadId, participantIds, typeCode);
    }

    /**
     * It leaves the thread that you are in there
     */
    public String leaveThread(long threadId, String typeCode) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            RemoveParticipant removeParticipant = new RemoveParticipant();

            removeParticipant.setSubjectId(threadId);
            removeParticipant.setToken(getToken());
            removeParticipant.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            removeParticipant.setUniqueId(uniqueId);
            removeParticipant.setType(ChatMessageType.LEAVE_THREAD);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(removeParticipant);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_LEAVE_THREAD");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * leaves the thread
     *
     * @ param threadId id of the thread
     */
    public String leaveThread(RequestLeaveThread request) {

        return leaveThread(request.getThreadId(), request.getTypeCode());
    }

    /**
     * @param requestSetAuditor You can add auditor role to someone in a thread using user id.
     */
    public String addAuditor(RequestSetAuditor requestSetAuditor) {
        SetRuleVO setRuleVO = new SetRuleVO();
        BeanUtils.copyProperties(requestSetAuditor, setRuleVO);
        setRuleVO.setTypeCode(requestSetAuditor.getTypeCode());

        return setRole(setRuleVO);
    }

    /**
     * @param requestSetAdmin You can add admin role to someone in a thread using user id.
     */
    public String addAdmin(RequestSetAdmin requestSetAdmin) {
        SetRuleVO setRuleVO = new SetRuleVO();
        BeanUtils.copyProperties(requestSetAdmin, setRuleVO);
        setRuleVO.setTypeCode(requestSetAdmin.getTypeCode());

        return setRole(setRuleVO);

    }

    /**
     * @param setRuleVO You can add some roles to someone in a thread using user id.
     */
    private String setRole(SetRuleVO setRuleVO) {
        long threadId = setRuleVO.getThreadId();
        ArrayList<RequestRole> roles = setRuleVO.getRoles();
        String uniqueId = generateUniqueId();


        if (chatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();

            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(getToken());
            chatMessage.setType(ChatMessageType.SET_ROLE_TO_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);

            if (!Util.isNullOrEmpty(setRuleVO.getTypeCode())) {
                chatMessage.setTypeCode(setRuleVO.getTypeCode());
            } else {
                chatMessage.setTypeCode(getTypeCode());
            }

            String asyncContent = gson.toJson(chatMessage);

            sendAsyncMessage(asyncContent, 4, "SET_RULE_TO_USER");
        }
        return uniqueId;
    }

    /**
     * @param requestSetAuditor You can add auditor role to someone in a thread using user id.
     */
    public String removeAuditor(RequestSetAuditor requestSetAuditor) {
        SetRuleVO setRuleVO = new SetRuleVO();

        BeanUtils.copyProperties(requestSetAuditor, setRuleVO);
        setRuleVO.setTypeCode(requestSetAuditor.getTypeCode());

        return removeRole(setRuleVO);
    }

    /**
     * @param requestSetAdmin You can add admin role to someone in a thread using user id.
     */
    public String removeAdmin(RequestSetAdmin requestSetAdmin) {
        SetRuleVO setRuleVO = new SetRuleVO();

        BeanUtils.copyProperties(requestSetAdmin, setRuleVO);
        setRuleVO.setTypeCode(requestSetAdmin.getTypeCode());

        return removeRole(setRuleVO);

    }


    /**
     * @param setRuleVO You can remove some roles from someone in a thread using user id.
     */
    private String removeRole(SetRuleVO setRuleVO) {
        long threadId = setRuleVO.getThreadId();
        ArrayList<RequestRole> roles = setRuleVO.getRoles();
        String uniqueId = generateUniqueId();


        if (chatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();

            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(getToken());
            chatMessage.setType(ChatMessageType.REMOVE_ROLE_FROM_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);

            if (!Util.isNullOrEmpty(setRuleVO.getTypeCode())) {
                chatMessage.setTypeCode(setRuleVO.getTypeCode());
            } else {
                chatMessage.setTypeCode(getTypeCode());
            }

            String asyncContent = gson.toJson(chatMessage);

            sendAsyncMessage(asyncContent, 4, "REMOVE_RULE_FROM_USER");
        }
        return uniqueId;
    }

    /**
     * It blocks the thread
     *
     * @param contactId id of the contact
     * @param threadId  id of the thread
     * @param userId    id of the user
     */
    public String block(Long contactId, Long userId, Long threadId, String typeCode) {
        String uniqueId = generateUniqueId();

        if (chatReady) {

            JsonObject contentObject = new JsonObject();
            if (!Util.isNullOrEmpty(contactId)) {
                contentObject.addProperty("contactId", contactId);
            }
            if (!Util.isNullOrEmpty(userId)) {
                contentObject.addProperty("userId", userId);
            }
            if (!Util.isNullOrEmpty(threadId)) {
                contentObject.addProperty("threadId", threadId);
            }

            String json = contentObject.toString();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(json);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setType(ChatMessageType.BLOCK);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_BLOCK");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * It blocks the thread
     *
     * @ param contactId id of the contact
     * @ param threadId  id of the thread
     * @ param userId    id of the user
     */
    public String block(RequestBlock request) {
        Long contactId = request.getContactId();
        long threadId = request.getThreadId();
        long userId = request.getUserId();
        String typeCode = request.getTypeCode();

        return block(contactId, userId, threadId, typeCode);
    }

    /**
     * It unblocks thread with three way
     *
     * @param blockId   the id that came from getBlockList
     * @param userId
     * @param threadId  Id of the thread
     * @param contactId Id of the contact
     */
    public String unblock(Long blockId, Long userId, Long threadId, Long contactId, String typeCode) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            ChatMessage chatMessage = new ChatMessage();

            JsonObject contentObject = new JsonObject();
            if (!Util.isNullOrEmpty(contactId)) {
                contentObject.addProperty("contactId", contactId);
            }
            if (!Util.isNullOrEmpty(userId)) {
                contentObject.addProperty("userId", userId);
            }
            if (!Util.isNullOrEmpty(threadId)) {
                contentObject.addProperty("threadId", threadId);
            }

            String jsonContent = contentObject.toString();


            chatMessage.setContent(jsonContent);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setType(ChatMessageType.UNBLOCK);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (Util.isNullOrEmpty(blockId)) {
                jsonObject.remove("subjectId");
            } else {
                jsonObject.remove("subjectId");
                jsonObject.addProperty("subjectId", blockId);
            }

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_UN_BLOCK");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * It unblocks thread with three way
     *
     * @ param blockId it can be found in the response of getBlockList
     * @ param userId Id of the user
     * @ param threadId Id of the thread
     * @ param contactId Id of the contact
     */
    public String unblock(RequestUnBlock request) {
        long contactId = request.getContactId();
        long threadId = request.getThreadId();
        long userId = request.getUserId();
        long blockId = request.getBlockId();
        String typeCode = request.getTypeCode();

        return unblock(blockId, userId, threadId, contactId, typeCode);
    }

    /**
     * It gets the list of the contacts that is on block list
     */
    public String getBlockList(Long count, Long offset, String typeCode) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            ChatMessageContent chatMessageContent = new ChatMessageContent();
            if (offset != null) {
                chatMessageContent.setOffset(offset);
            }
            if (count != null) {
                chatMessageContent.setCount(count);
            } else {
                chatMessageContent.setCount(50);
            }

            String json = gson.toJson(chatMessageContent);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(json);
            chatMessage.setType(ChatMessageType.GET_BLOCKED);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTypeCode(!Util.isNullOrEmpty(typeCode) ? typeCode : getTypeCode());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_BLOCK_List");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * It gets the list of the contacts that is on block list
     */
    public String getBlockList(RequestBlockList request) {
        return getBlockList(request.getCount(), request.getOffset(), request.getTypeCode());
    }

    public String getMessageDeliveredList(RequestDeliveredMessageList requestParams) {
        return deliveredMessageList(requestParams);
    }

    /**
     * Get the list of the participants that saw the specific message
     *
     * @param requestParams
     * @return
     */
    public String getMessageSeenList(RequestSeenMessageList requestParams) {
        return seenMessageList(requestParams);
    }

    /**
     * It Mutes the thread so notification is set to off for that thread
     */
    public String muteThread(long threadId, String typeCode) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;

        try {
            if (chatReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(threadId);
                chatMessage.setTypeCode(!Util.isNullOrEmpty(typeCode) ? typeCode : getTypeCode());


                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");


                sendAsyncMessage(jsonObject.toString(), 4, "SEND_MUTE_THREAD");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }

    /**
     * Mute the thread so notification is off for that thread
     */
    public String muteThread(RequestMuteThread request) {
        return muteThread(request.getThreadId(), request.getTypeCode());
    }

    /**
     * It Un mutes the thread so notification is on for that thread
     */
    public String unMuteThread(RequestMuteThread request) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;
        try {
            if (chatReady) {
                long threadId = request.getThreadId();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UN_MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setTypeCode(!Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : getTypeCode());

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");


                sendAsyncMessage(jsonObject.toString(), 4, "SEND_UN_MUTE_THREAD");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * Un mute the thread so notification is on for that thread
     */
    @Deprecated
    public String unMuteThread(long threadId) {
        String uniqueId = generateUniqueId();

        try {
            if (chatReady) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UN_MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, 4, "SEND_UN_MUTE_THREAD");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * It is possible to pin at most 5 thread
     *
     * @param requestPinThread
     * @return
     */
    public String pinThread(RequestPinThread requestPinThread) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;

        try {
            if (chatReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.PIN_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(requestPinThread.getThreadId());
                chatMessage.setTypeCode(!Util.isNullOrEmpty(requestPinThread.getTypeCode()) ? requestPinThread.getTypeCode() : getTypeCode());


                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_PIN_THREAD");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }


    /**
     * Unpin the thread already pined with thread id
     *
     * @param requestPinThread
     * @return
     */
    public String unPinThread(RequestPinThread requestPinThread) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;
        try {
            if (chatReady) {
                long threadId = requestPinThread.getThreadId();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UNPIN_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setTypeCode(!Util.isNullOrEmpty(requestPinThread.getTypeCode()) ? requestPinThread.getTypeCode() : getTypeCode());

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");


                sendAsyncMessage(jsonObject.toString(), 4, "SEND_UN_PIN_THREAD");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }


    /**
     * Pin a message only in a channel or group; pin message requester should have writer role
     *
     * @param requestPinMessage
     * @return
     */
    public String pinMessage(RequestPinMessage requestPinMessage) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;

        try {
            if (chatReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.PIN_MESSAGE);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(requestPinMessage.getMessageId());
                chatMessage.setTypeCode(!Util.isNullOrEmpty(requestPinMessage.getTypeCode()) ? requestPinMessage.getTypeCode() : getTypeCode());

                PinMessage pinMessage = new PinMessage();
                pinMessage.setNotifyAll(requestPinMessage.getNotifyAll());

                chatMessage.setContent(gson.toJson(pinMessage));

                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");


                sendAsyncMessage(jsonObject.toString(), 4, "SEND_PIN_MESSAGE");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }


    /**
     * Unpin the message already pined with message id
     *
     * @param requestPinMessage
     * @return
     */
    public String unPinMessage(RequestPinMessage requestPinMessage) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;
        try {
            if (chatReady) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UNPIN_MESSAGE);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(requestPinMessage.getMessageId());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setTypeCode(!Util.isNullOrEmpty(requestPinMessage.getTypeCode()) ? requestPinMessage.getTypeCode() : getTypeCode());

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_UN_PIN_MESSAGE");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }


    public String getAdminList(RequestGetAdmin requestGetAdmin) {
        int count = (int) requestGetAdmin.getCount();
        long offset = requestGetAdmin.getOffset();
        long threadId = requestGetAdmin.getThreadId();
        String typeCode = requestGetAdmin.getTypeCode();

        return getThreadParticipantsMain(count, offset, threadId, typeCode, true);
    }

    /**
     * If someone that is not in your contacts send message to you
     * their spam is false
     */
    @Deprecated
    public String spam(long threadId) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.SPAM_PV_THREAD);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_REPORT_SPAM");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * If someone that is not in your contact list tries to send message to you
     * their spam value is true and you can call this method in order to set that to false
     *
     * @ param long threadId Id of the thread
     */
    public String spam(RequestSpam request) {
        String uniqueId = generateUniqueId();

        JsonObject jsonObject;
        try {

            if (chatReady) {
                long threadId = request.getThreadId();
                String typeCode = request.getTypeCode();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.SPAM_PV_THREAD);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(threadId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.addProperty("typeCode", request.getTypeCode());
                }

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_REPORT_SPAM");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }


    public String interactMessage(RequestInteract request) {
        String uniqueId = generateUniqueId();

        try {
            if (chatReady) {
                ChatMessage chatMessage = new ChatMessage();

                chatMessage.setType(ChatMessageType.INTERACT_MESSAGE);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setContent(request.getContent());
                chatMessage.setSystemMetadata(request.getSystemMetadata());
                chatMessage.setMetadata(request.getMetadata());
                chatMessage.setRepliedTo(request.getRepliedTo());
                chatMessage.setSubjectId(request.getMessageId());


                if (!Util.isNullOrEmpty(request.getTypeCode())) {
                    chatMessage.setTypeCode(request.getTypeCode());
                } else {
                    chatMessage.setTypeCode(getTypeCode());
                }

                sendAsyncMessage(gson.toJson(chatMessage), 4, "SEND_INTERACT_MESSAGE");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }

    public ArrayList<String> createThreadWithFileMessage(RequestCreateThreadWithFile request) {
        ArrayList<String> uniqueIds = new ArrayList<>();

        String threadUniqId = generateUniqueId();
        uniqueIds.add(threadUniqId);

        String newMsgUniqueId = generateUniqueId();

        List<String> forwardUniqueIds = generateForwardingMessageId(request);

        if (!Util.isNullOrEmpty(forwardUniqueIds)) {
            uniqueIds.addAll(forwardUniqueIds);
        }

        if (chatReady) {
            if (request.getFile() != null && request.getFile() instanceof RequestUploadImage) {

                uploadImage((RequestUploadImage) request.getFile(), threadUniqId, metaData -> {
                    RequestThreadInnerMessage requestThreadInnerMessage;

                    if (request.getMessage() == null) {
                        requestThreadInnerMessage = new RequestThreadInnerMessage
                                .Builder()
                                .metadata(metaData)
                                .build();
                    } else {
                        requestThreadInnerMessage = request.getMessage();
                        requestThreadInnerMessage.setMetadata(metaData);
                    }


                    request.setMessage(requestThreadInnerMessage);
                    request.setFile(null);

                    createThreadWithMessage(request, threadUniqId, newMsgUniqueId, forwardUniqueIds);

                });

            } else if (request.getFile() != null) {

                uploadFile(request.getFile().getFilePath(), threadUniqId, metaData -> {
                    RequestThreadInnerMessage requestThreadInnerMessage;


                    if (request.getMessage() == null) {
                        requestThreadInnerMessage = new RequestThreadInnerMessage
                                .Builder()
                                .metadata(metaData)
                                .build();
                    } else {
                        requestThreadInnerMessage = request.getMessage();
                        requestThreadInnerMessage.setMetadata(metaData);
                    }


                    request.setMessage(requestThreadInnerMessage);
                    request.setFile(null);

                    createThreadWithMessage(request, threadUniqId, newMsgUniqueId, forwardUniqueIds);

                });
            }

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqId);
        }

        return uniqueIds;
    }

    private List<String> generateForwardingMessageId(RequestCreateThreadWithMessage request) {
        List<String> forwardUniqueIds = null;

        if (request.getMessage() != null && !Util.isNullOrEmptyNumber(request.getMessage().getForwardedMessageIds())) {
            List<Long> messageIds = request.getMessage().getForwardedMessageIds();
            forwardUniqueIds = new ArrayList<>();

            for (long ids : messageIds) {
                String frwMsgUniqueId = generateUniqueId();

                System.out.println(frwMsgUniqueId);

                forwardUniqueIds.add(frwMsgUniqueId);
            }
        }
        return forwardUniqueIds;
    }

    private void createThreadWithMessage(RequestCreateThreadWithMessage threadRequest,
                                         String threadUniqueId,
                                         String messageUniqueId,
                                         List<String> forwardUniqueIds) {
        JsonObject innerMessageObj = null;
        JsonObject jsonObject;

        try {
            if (chatReady) {

                if (threadRequest.getMessage() != null) {
                    RequestThreadInnerMessage innerMessage = threadRequest.getMessage();
                    innerMessageObj = (JsonObject) gson.toJsonTree(innerMessage);

                    if (Util.isNullOrEmpty(threadRequest.getMessage().getType())) {
                        innerMessageObj.remove("type");
                    }

                    if (Util.isNullOrEmpty(threadRequest.getMessage().getText())) {
                        innerMessageObj.remove("message");
                    }

                    innerMessageObj.addProperty("uniqueId", messageUniqueId);


                    if (!Util.isNullOrEmptyNumber(threadRequest.getMessage().getForwardedMessageIds())) {

                        /** Its generated new unique id for each forward message*/
                        JsonElement element = gson.toJsonTree(forwardUniqueIds, new TypeToken<List<Long>>() {
                        }.getType());

                        JsonArray jsonArray = element.getAsJsonArray();
                        innerMessageObj.add("forwardedUniqueIds", jsonArray);
                    } else {
                        innerMessageObj.remove("forwardedUniqueIds");
                        innerMessageObj.remove("forwardedMessageIds");
                    }
                }

                JsonObject jsonObjectCreateThread = (JsonObject) gson.toJsonTree(threadRequest);

                jsonObjectCreateThread.remove("count");
                jsonObjectCreateThread.remove("offset");
                jsonObjectCreateThread.add("message", innerMessageObj);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jsonObjectCreateThread.toString());
                chatMessage.setType(ChatMessageType.INVITATION);
                chatMessage.setUniqueId(threadUniqueId);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));


                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                jsonObject.remove("repliedTo");
                jsonObject.remove("subjectId");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("contentCount");

                String typeCode = threadRequest.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_CREATE_THREAD_WITH_MESSAGE");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
    }


    private void uploadFile(String path, String finalUniqueId, UploadFileListener uploadFileListener) {
        try {
            if (getFileServer() != null && !Util.isNullOrEmpty(path)) {

                File file = new File(path);
                String mimeType = getContentType(file);

                if (file.exists()) {

                    long fileSize = file.length();
                    FileApi fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);

                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                    RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    Call<FileUpload> fileUploadCall = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);

                    RetrofitUtil.request(fileUploadCall, new ApiListener<FileUpload>() {

                        @Override
                        public void onSuccess(FileUpload fileUpload) {
                            boolean hasError = fileUpload.isHasError();
                            if (hasError) {
                                String errorMessage = fileUpload.getMessage();
                                int errorCode = fileUpload.getErrorCode();
                                String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                showErrorLog(jsonError);

                            } else {
                                ResultFile resultFile = fileUpload.getResult();

                                showInfoLog("RECEIVE_UPLOAD_FILE", gson.toJson(resultFile));

                                MetaDataFile metaDataFile = new MetaDataFile();
                                FileMetaDataContent metaDataContent = new FileMetaDataContent();
                                metaDataContent.setHashCode(resultFile.getHashCode());
                                metaDataContent.setId(resultFile.getId());
                                metaDataFile.setFile(metaDataContent);


                                uploadFileListener.fileUploaded(gson.toJson(metaDataFile));
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            showErrorLog(throwable.getMessage());
                        }

                        @Override
                        public void onServerError(Response<FileUpload> response) {
                            showErrorLog(response.body().getMessage());
                        }
                    });
                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                    showErrorLog(jsonError);
                }

            } else {
                showErrorLog("File is not Exist");
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }


    private void uploadImage(RequestUploadImage requestUploadImage, String finalUniqueId, UploadFileListener uploadFileListener) {

        String filePath = requestUploadImage.getFilePath();
        int xC = requestUploadImage.getxC();
        int yC = requestUploadImage.getyC();
        int hC = requestUploadImage.gethC();
        int wC = requestUploadImage.getwC();

        if (filePath.endsWith(".gif")) {
            uploadFile(filePath, finalUniqueId, uploadFileListener);
        } else {
            uploadImage(filePath, xC, yC, hC, wC, finalUniqueId, uploadFileListener);
        }

    }

    public void uploadImage(String filePath,
                            int xC,
                            int yC,
                            int hC,
                            int wC,
                            String finalUniqueId,
                            UploadFileListener uploadFileListener) {

        try {
            if (fileServer != null && filePath != null && !filePath.isEmpty()) {
                File file = new File(filePath);

                if (file.exists()) {

                    String mimeType = getContentType(file);

                    if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {
                        FileApi fileApi;
                        RequestBody requestFile;

                        if (!Util.isNullOrEmpty(hC) && !Util.isNullOrEmpty(wC)) {
                            BufferedImage originalImage = ImageIO.read(file);

                            BufferedImage subImage = originalImage.getSubimage(xC, yC, wC, hC);

                            File outputFile = File.createTempFile("test", null);

                            ImageIO.write(subImage, mimeType.substring(mimeType.indexOf("/") + 1), outputFile);

                            fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);

                            requestFile = RequestBody.create(MediaType.parse("image/*"), outputFile);
                        } else {

                            fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);

                            requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                        }

                        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                        Call<FileImageUpload> fileImageUploadCall = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);

                        RetrofitUtil.request(fileImageUploadCall, new ApiListener<FileImageUpload>() {

                            @Override
                            public void onSuccess(FileImageUpload fileImageUpload) {
                                boolean hasError = fileImageUpload.isHasError();

                                if (hasError) {
                                    String errorMessage = fileImageUpload.getMessage();
                                    int errorCode = fileImageUpload.getErrorCode();
                                    String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);

                                    showErrorLog(jsonError);
                                } else {
                                    ResultImageFile resultImageFile = new ResultImageFile();

                                    resultImageFile.setId(fileImageUpload.getResult().getId());
                                    resultImageFile.setHashCode(fileImageUpload.getResult().getHashCode());
                                    resultImageFile.setName(fileImageUpload.getResult().getName());
                                    resultImageFile.setHeight(fileImageUpload.getResult().getHeight());
                                    resultImageFile.setWidth(fileImageUpload.getResult().getWidth());
                                    resultImageFile.setActualHeight(fileImageUpload.getResult().getActualHeight());
                                    resultImageFile.setActualWidth(fileImageUpload.getResult().getActualWidth());

                                    showInfoLog("RECEIVE_UPLOAD_IMAGE", gson.toJson(fileImageUpload));

                                    uploadFileListener.fileUploaded(gson.toJson(resultImageFile));
                                }

                            }

                            @Override
                            public void onError(Throwable throwable) {
                                showErrorLog(throwable.getMessage());
                            }

                            @Override
                            public void onServerError(Response<FileImageUpload> response) {
                                showErrorLog(response.body().getMessage());
                            }
                        });
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                        showErrorLog(jsonError);

                    }
                }

            } else {
                showErrorLog("FileServer url Is null");
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
            getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, finalUniqueId);
        }

    }


    /**
     * Add a listener to receive events on this Chat.
     *
     * @param listener A listener to add.
     * @return {@code this} object.
     */
    public Chat addListener(ChatListener listener) {
        listenerManager.addListener(listener);
        return this;
    }

    public Chat addListeners(List<ChatListener> listeners) {
        listenerManager.addListeners(listeners);
        return this;
    }

    public Chat removeListener(ChatListener listener) {
        listenerManager.removeListener(listener);
        return this;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    private void pingWithDelay() {
        long lastSentMessageTimeout = 9 * 1000;
        lastSentMessageTime = new Date().getTime();

        PingExecutor.getInstance().
                scheduleAtFixedRate(() -> checkForPing(lastSentMessageTimeout),
                        0, 20000,
                        TimeUnit.MILLISECONDS);
    }

    private void checkForPing(long lastSentMessageTimeout) {
        long currentTime = new Date().getTime();
        if (currentTime - lastSentMessageTime > lastSentMessageTimeout) {
            ping();
        }
    }

    /**
     * Ping for staying chat alive
     */
    private void ping() {
        if (chatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.PING);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(getToken());

            String asyncContent = gson.toJson(chatMessage);

            sendAsyncMessage(asyncContent, 4, "CHAT PING");
        }
    }

    private void showInfoLog(String tag, String json) {
        if (isLoggable) logger.log(Level.INFO, "{} \n \n {} ", tag, json);

        if (!Util.isNullOrEmpty(json)) {
            listenerManager.callOnLogEvent(json);
        }
    }

    private void showInfoLog(String json) {
        if (isLoggable) logger.log(Level.INFO, "\n \n {}", json);
    }

    private void showErrorLog(String tag, String json) {
        if (isLoggable) logger.log(Level.ERROR, " {} \n \n {} ", tag, json);

        if (!Util.isNullOrEmpty(json)) {
            listenerManager.callOnLogEvent(json);
        }
    }

    private void showErrorLog(String e) {
        if (isLoggable) logger.log(Level.ERROR, "\n \n {} ", e);

    }

    private void showErrorLog(Throwable throwable) {
        if (isLoggable) logger.log(Level.ERROR, "\n \n {} ", throwable.getMessage());
    }

    private void handleError(ChatMessage chatMessage) {

        Error error = gson.fromJson(chatMessage.getContent(), Error.class);

        String errorMessage = error.getMessage();
        long errorCode = error.getCode();

        getErrorOutPut(errorMessage, errorCode, chatMessage.getUniqueId());
    }

    private void handleLastSeenUpdated(ChatMessage chatMessage) {
        showInfoLog("LAST_SEEN_UPDATED", "");
        showInfoLog(chatMessage.getContent(), "");

        listenerManager.callOnLastSeenUpdated(chatMessage.getContent());
    }

    private void handleThreadInfoUpdated(ChatMessage chatMessage) {
        ResultThread resultThread = new ResultThread();
        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);
        resultThread.setThread(thread);

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setResult(resultThread);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        listenerManager.callOnThreadInfoUpdated(chatMessage.getContent(), chatResponse);

        showInfoLog("THREAD_INFO_UPDATED", chatMessage.getContent());
    }

    private void handleRemoveFromThread(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        ResultThread resultThread = new ResultThread();
        Thread thread = new Thread();
        thread.setId(chatMessage.getSubjectId());
        resultThread.setThread(thread);
        String content = gson.toJson(chatResponse);

        showInfoLog("RECEIVED_REMOVED_FROM_THREAD", content);

        listenerManager.callOnRemovedFromThread(content, chatResponse);
    }

    /**
     * After the set Token, we send ping for checking client Authenticated or not
     * the (boolean)checkToken is for that reason
     */
    private void handleOnPing() {
        showInfoLog("RECEIVED_CHAT_PING", "");
    }

    /**
     * When the new message arrived we send the deliver message to the sender user.
     */
    private void handleNewMessage(ChatMessage chatMessage) {

        try {
            MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

            ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());
            chatResponse.setHasError(false);
            chatResponse.setErrorCode(0);
            chatResponse.setErrorMessage("");

            ResultNewMessage resultNewMessage = new ResultNewMessage();
            resultNewMessage.setMessageVO(messageVO);
            resultNewMessage.setThreadId(chatMessage.getSubjectId());

            chatResponse.setResult(resultNewMessage);

            String json = gson.toJson(chatResponse);

            long ownerId = 0;

            if (messageVO != null) {
                ownerId = messageVO.getParticipant().getId();
            }
            showInfoLog("RECEIVED_NEW_MESSAGE", json);

            if (ownerId != getUserId()) {
                ChatMessage message = getChatMessage(messageVO);

                String asyncContent = gson.toJson(message);
                async.sendMessage(asyncContent, 4);

                showInfoLog("SEND_DELIVERY_MESSAGE");

                listenerManager.callOnLogEvent(asyncContent);
            }

            listenerManager.callOnNewMessage(json, chatResponse);

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

    }

    //TODO Problem in message id in forwardMessage
    private void handleSent(ChatMessage chatMessage) {

        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setConversationId(chatMessage.getSubjectId());
        resultMessage.setMessageId(Long.parseLong(chatMessage.getContent()));

        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(resultMessage);

        String json = gson.toJson(chatResponse);

        listenerManager.callOnSentMessage(json, chatResponse);

        showInfoLog("RECEIVED_SENT_MESSAGE", json);
    }

    private void handleSeen(ChatMessage chatMessage) {
        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(gson.fromJson(chatMessage.getContent(), ResultMessage.class));

        String json = gson.toJson(chatResponse);

        listenerManager.callOnSeenMessage(json, chatResponse);

        showInfoLog("RECEIVED_SEEN_MESSAGE", json);


    }

    private void handleDelivery(ChatMessage chatMessage) {
        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(gson.fromJson(chatMessage.getContent(), ResultMessage.class));

        String json = gson.toJson(chatResponse);

        listenerManager.callOnDeliveryMessage(json, chatResponse);

        showInfoLog("RECEIVED_DELIVERED_MESSAGE", json);
    }

    private void handleForwardMessage(ChatMessage chatMessage) {
        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        ResultNewMessage resultMessage = new ResultNewMessage();

        resultMessage.setThreadId(chatMessage.getSubjectId());
        resultMessage.setMessageVO(messageVO);

        chatResponse.setResult(resultMessage);

        String json = gson.toJson(chatResponse);

        long ownerId = 0;
        if (messageVO != null) {
            ownerId = messageVO.getParticipant().getId();
        }
        showInfoLog("RECEIVED_FORWARD_MESSAGE", json);

        listenerManager.callOnNewMessage(json, chatResponse);

        if (ownerId != getUserId()) {
            ChatMessage message = getChatMessage(messageVO);

            String asyncContent = gson.toJson(message);
            showInfoLog("SEND_DELIVERY_MESSAGE", asyncContent);

            async.sendMessage(asyncContent, 4);
        }
    }

    private void handleResponseMessage(ChatMessage chatMessage, String messageUniqueId) {

        try {
            switch (chatMessage.getType()) {

                case ChatMessageType.GET_HISTORY:
                    handleOutPutGetHistory(chatMessage);
                    break;

                case ChatMessageType.GET_CONTACTS:
                    handleGetContact(chatMessage, messageUniqueId);
                    break;

                case ChatMessageType.UPDATE_THREAD_INFO:

                    handleUpdateThreadInfo(chatMessage);
                    break;
                case ChatMessageType.INVITATION:
                    handleCreateThread(chatMessage);
                    break;

                case ChatMessageType.MUTE_THREAD:
                    handleMuteThread(chatMessage);
                    break;

                case ChatMessageType.UN_MUTE_THREAD:
                    handleUnMuteThread(chatMessage);
                    break;

                case ChatMessageType.PIN_THREAD:
                    handlePinThread(chatMessage);
                    break;

                case ChatMessageType.UNPIN_THREAD:
                    handleUnpinThread(chatMessage);
                    break;

                case ChatMessageType.EDIT_MESSAGE:
                    handleEditMessage(chatMessage);
                    break;

                case ChatMessageType.USER_INFO:
                    handleOnUserInfo(chatMessage);
                    break;

                case ChatMessageType.THREAD_PARTICIPANTS:

                    ChatResponse<ResultParticipant> resultParticipantChatResponse = reformatThreadParticipants(chatMessage);

                    String jsonParticipant = gson.toJson(resultParticipantChatResponse);

                    listenerManager.callOnGetThreadParticipant(jsonParticipant, resultParticipantChatResponse);


                    showInfoLog("RECEIVE_PARTICIPANT", jsonParticipant);
                    break;

                case ChatMessageType.ADD_PARTICIPANT:
                    handleAddParticipant(chatMessage);
                    break;

                case ChatMessageType.LEAVE_THREAD:
                    handleOutPutLeaveThread(chatMessage);
                    break;

                case ChatMessageType.RENAME:
                    break;

                case ChatMessageType.DELETE_MESSAGE:
                    handleOutPutDeleteMsg(chatMessage);
                    break;

                case ChatMessageType.BLOCK:
                    handleOutPutBlock(chatMessage, messageUniqueId);
                    break;

                case ChatMessageType.UNBLOCK:
                    handleUnBlock(chatMessage, messageUniqueId);
                    break;

                case ChatMessageType.GET_BLOCKED:
                    handleOutPutGetBlockList(chatMessage);
                    break;

                case ChatMessageType.DELIVERED_MESSAGE_LIST:
                    handleOutPutDeliveredMessageList(chatMessage);
                    break;

                case ChatMessageType.SEEN_MESSAGE_LIST:
                    handleOutPutSeenMessageList(chatMessage);
                    break;

                case ChatMessageType.PIN_MESSAGE:
                    handlePinMessage(chatMessage);
                    break;

                case ChatMessageType.UNPIN_MESSAGE:
                    handleUnpinMessage(chatMessage);
                    break;


            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleUnpinThread(ChatMessage chatMessage) {
        ChatResponse<ResultPinThread> resultUnPinChatResponse = new ChatResponse<>();
        String unpinThreadJson = reformatPinThread(chatMessage, resultUnPinChatResponse);

        listenerManager.callOnUnPinThread(unpinThreadJson, resultUnPinChatResponse);

        showInfoLog("RECEIVE_UN_PIN_THREAD", unpinThreadJson);
    }

    private void handlePinThread(ChatMessage chatMessage) {
        ChatResponse<ResultPinThread> resultPinChatResponse = new ChatResponse<>();
        String pingThreadJson = reformatPinThread(chatMessage, resultPinChatResponse);

        listenerManager.callOnPinThread(pingThreadJson, resultPinChatResponse);

        showInfoLog("RECEIVE_PIN_THREAD", pingThreadJson);
    }


    private void handleUnpinMessage(ChatMessage chatMessage) {
        ChatResponse<ResultPinMessage> resultUnPinChatResponse = new ChatResponse<>();
        String unpinThreadJson = reformatPinMessage(chatMessage, resultUnPinChatResponse);

        listenerManager.callOnUnPinMessage(unpinThreadJson, resultUnPinChatResponse);

        showInfoLog("RECEIVE_UN_PIN_MESSAGE", unpinThreadJson);
    }

    private void handlePinMessage(ChatMessage chatMessage) {
        ChatResponse<ResultPinMessage> resultPinChatResponse = new ChatResponse<>();
        String pingThreadJson = reformatPinMessage(chatMessage, resultPinChatResponse);

        listenerManager.callOnPinMessage(pingThreadJson, resultPinChatResponse);

        showInfoLog("RECEIVE_PIN_MESSAGE", pingThreadJson);
    }

    private void handleUnMuteThread(ChatMessage chatMessage) {
        ChatResponse<ResultMute> chatResponseTemp = new ChatResponse<>();
        String unmuteThreadJson = reformatMuteThread(chatMessage, chatResponseTemp);

        listenerManager.callOnUnmuteThread(unmuteThreadJson, chatResponseTemp);

        showInfoLog("RECEIVE_UN_MUTE_THREAD", unmuteThreadJson);
    }

    private void handleMuteThread(ChatMessage chatMessage) {
        ChatResponse<ResultMute> chatResponse = new ChatResponse<>();
        String muteThreadJson = reformatMuteThread(chatMessage, chatResponse);

        listenerManager.callOnMuteThread(muteThreadJson, chatResponse);

        showInfoLog("RECEIVE_MUTE_THREAD", muteThreadJson);
    }

    private void handleEditMessage(ChatMessage chatMessage) {
        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        ResultNewMessage newMessage = new ResultNewMessage();
        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

        newMessage.setMessageVO(messageVO);
        newMessage.setThreadId(chatMessage.getSubjectId());
        chatResponse.setResult(newMessage);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String content = gson.toJson(chatResponse);
        showInfoLog("RECEIVE_EDIT_MESSAGE", content);

        listenerManager.callOnEditedMessage(content, chatResponse);
    }

    private void handleOutPutSeenMessageList(ChatMessage chatMessage) {
        try {
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());

            ResultParticipant resultParticipant = new ResultParticipant();

            List<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
            }.getType());
            resultParticipant.setParticipants(participants);
            resultParticipant.setContentCount(chatMessage.getContentCount());

            chatResponse.setResult(resultParticipant);
            String content = gson.toJson(chatResponse);

            listenerManager.callOnSeenMessageList(content, chatResponse);

            showInfoLog("RECEIVE_SEEN_MESSAGE_LIST", content);

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    /**
     * order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * order should be set with lower case
     */
    private void getHistoryMain(History history, long threadId, String uniqueId, String typeCode) {
        long count = history.getCount() > 0 ? history.getCount() : 50;
        long offsets = history.getOffset() > 0 ? history.getOffset() : 0;
        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();


        history.setCount(count);
        history.setOffset(offsets);
        String query = history.getQuery();

        JsonObject jObj = (JsonObject) gson.toJsonTree(history);
        if (history.getLastMessageId() == 0) {
            jObj.remove("lastMessageId");
        }

        if (history.getFirstMessageId() == 0) {
            jObj.remove("firstMessageId");
        }

        if (history.getId() <= 0) {
            jObj.remove("id");
        }

        if (Util.isNullOrEmpty(query)) {
            jObj.remove("query");
        }

        if (Util.isNullOrEmpty(fromTime)) {
            jObj.remove("fromTime");
        }

        if (Util.isNullOrEmpty(fromTimeNanos)) {
            jObj.remove("fromTimeNanos");
        }

        if (Util.isNullOrEmpty(toTime)) {
            jObj.remove("toTime");
        }

        if (Util.isNullOrEmpty(toTimeNanos)) {
            jObj.remove("toTimeNanos");
        }

        if (Util.isNullOrEmpty(history.getUniqueIds())) {
            jObj.remove("uniqueIds");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(jObj.toString());
        chatMessage.setType(ChatMessageType.GET_HISTORY);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (!Util.isNullOrEmpty(typeCode)) {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", typeCode);
        } else if (!Util.isNullOrEmpty(getTypeCode())) {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", getTypeCode());
        } else {
            jsonObject.remove("typeCode");
        }


        String asyncContent = jsonObject.toString();

        sendAsyncMessage(asyncContent, 3, "SEND GET THREAD HISTORY");
    }

    private void handleOutPutDeliveredMessageList(ChatMessage chatMessage) {
        try {
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());

            ResultParticipant resultParticipant = new ResultParticipant();

            List<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
            }.getType());
            resultParticipant.setParticipants(participants);
            resultParticipant.setContentCount(chatMessage.getContentCount());

            resultParticipant.setContentCount(chatMessage.getContentCount());


            chatResponse.setResult(resultParticipant);
            String content = gson.toJson(chatResponse);

            listenerManager.callOnDeliveredMessageList(content, chatResponse);

            showInfoLog("RECEIVE_DELIVERED_MESSAGE_LIST", content);

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleGetContact(ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultContact> chatResponse = reformatGetContactResponse(chatMessage);
        String contactJson = gson.toJson(chatResponse);

        listenerManager.callOnGetContacts(contactJson, chatResponse);

        showInfoLog("RECEIVE_GET_CONTACT", contactJson);

    }

    private void handleCreateThread(ChatMessage chatMessage) {

        ChatResponse<ResultThread> chatResponse = reformatCreateThread(chatMessage);

        String inviteJson = gson.toJson(chatResponse);

        listenerManager.callOnCreateThread(inviteJson, chatResponse);

        showInfoLog("RECEIVE_CREATE_THREAD", inviteJson);

    }

    private void handleGetThreads(ChatMessage chatMessage) {

        ChatResponse<ResultThreads> chatResponse = reformatGetThreadsResponse(chatMessage);
        String threadJson = gson.toJson(chatResponse);

        listenerManager.callOnGetThread(threadJson, chatResponse);

        showInfoLog("RECEIVE_GET_THREAD", threadJson);

    }

    private void handleUpdateThreadInfo(ChatMessage chatMessage) {

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();

        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);

        ResultThread resultThread = new ResultThread();

        resultThread.setThread(thread);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(resultThread);

        String threadJson = gson.toJson(chatResponse);

        listenerManager.callOnUpdateThreadInfo(threadJson, chatResponse);
        showInfoLog("RECEIVE_UPDATE_THREAD_INFO", threadJson);
    }


    private void handleOutPutLeaveThread(ChatMessage chatMessage) {

        ChatResponse<ResultLeaveThread> chatResponse = new ChatResponse<>();

        ResultLeaveThread leaveThread = gson.fromJson(chatMessage.getContent(), ResultLeaveThread.class);

        long threadId = chatMessage.getSubjectId();

        leaveThread.setThreadId(threadId);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(leaveThread);

        String jsonThread = gson.toJson(chatResponse);

        listenerManager.callOnThreadLeaveParticipant(jsonThread, chatResponse);

        showInfoLog("RECEIVE_LEAVE_THREAD", jsonThread);
    }

    private void handleAddParticipant(ChatMessage chatMessage) {
        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);


        ChatResponse<ResultAddParticipant> chatResponse = new ChatResponse<>();

        ResultAddParticipant resultAddParticipant = new ResultAddParticipant();
        resultAddParticipant.setThread(thread);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        chatResponse.setHasError(false);
        chatResponse.setResult(resultAddParticipant);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String jsonAddParticipant = gson.toJson(chatResponse);

        listenerManager.callOnThreadAddParticipant(jsonAddParticipant, chatResponse);

        showInfoLog("RECEIVE_ADD_PARTICIPANT", jsonAddParticipant);
    }

    private void handleOutPutDeleteMsg(ChatMessage chatMessage) {

        ChatResponse<ResultDeleteMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        long messageId = Long.parseLong(chatMessage.getContent());

        ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();

        DeleteMessageContent deleteMessage = new DeleteMessageContent();
        deleteMessage.setId(messageId);

        resultDeleteMessage.setDeletedMessage(deleteMessage);

        chatResponse.setResult(resultDeleteMessage);

        String jsonDeleteMsg = gson.toJson(chatResponse);

        listenerManager.callOnDeleteMessage(jsonDeleteMsg, chatResponse);

        showInfoLog("RECEIVE_DELETE_MESSAGE", jsonDeleteMsg);
    }

    private void handleOutPutBlock(ChatMessage chatMessage, String messageUniqueId) {

        Contact contact = gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<ResultBlock> chatResponse = new ChatResponse<>();
        ResultBlock resultBlock = new ResultBlock();
        resultBlock.setContact(contact);
        chatResponse.setResult(resultBlock);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String jsonBlock = gson.toJson(chatResponse);

        listenerManager.callOnBlock(jsonBlock, chatResponse);

        showInfoLog("RECEIVE_BLOCK", jsonBlock);

    }

    private void signalMessage(RequestSignalMsg requestSignalMsg) {

        String uniqueId = generateUniqueId();
        int signalType = requestSignalMsg.getSignalType();
        long threadId = requestSignalMsg.getThreadId();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", signalType);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(jsonObject.toString());
        chatMessage.setType(ChatMessageType.SIGNAL_MESSAGE);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObjectCht = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(getTypeCode())) {
            jsonObjectCht.remove("typeCode");
        } else {
            jsonObjectCht.remove("typeCode");
            jsonObjectCht.addProperty("typeCode", getTypeCode());
        }
        String asyncContent = jsonObjectCht.toString();
        sendAsyncMessage(asyncContent, 3, "SEND SIGNAL_TYPE " + signalType);
    }

    private void handleClearHistory(ChatMessage chatMessage) {
        ChatResponse<ResultClearHistory> chatResponseClrHistory = new ChatResponse<>();

        ResultClearHistory resultClearHistory = new ResultClearHistory();
        long clrHistoryThreadId = gson.fromJson(chatMessage.getContent(), Long.class);
        resultClearHistory.setThreadId(clrHistoryThreadId);

        chatResponseClrHistory.setResult(resultClearHistory);
        chatResponseClrHistory.setUniqueId(chatMessage.getUniqueId());

        String jsonClrHistory = gson.toJson(chatResponseClrHistory);

        listenerManager.callOnClearHistory(jsonClrHistory, chatResponseClrHistory);

        showInfoLog("RECEIVE_CLEAR_HISTORY", jsonClrHistory);
    }

    private void handleInteractiveMessage(ChatMessage chatMessage) {
        ChatResponse<ResultInteractMessage> responseInteractMessage = new ChatResponse<>();

        ResultInteractMessage resultInteractMessage = gson.fromJson(chatMessage.getContent(), ResultInteractMessage.class);
        responseInteractMessage.setResult(resultInteractMessage);

        String jsonResponse = gson.toJson(responseInteractMessage);

        listenerManager.callOnInteractMessage(jsonResponse, responseInteractMessage);

        showInfoLog("RECEIVE_INTERACT_MESSAGE", jsonResponse);
    }

    private void handleSetRole(ChatMessage chatMessage) {
        ChatResponse<ResultSetRole> chatResponse = new ChatResponse<>();
        ResultSetRole resultSetRole = new ResultSetRole();

        ArrayList<Admin> admins = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
        }.getType());

        resultSetRole.setAdmins(admins);

        chatResponse.setResult(resultSetRole);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String responseJson = gson.toJson(chatResponse);

        listenerManager.callOnSetRoleToUser(responseJson, chatResponse);

        showInfoLog("RECEIVE_SET_RULE", responseJson);
    }

    private void handleRemoveRole(ChatMessage chatMessage) {
        ChatResponse<ResultSetRole> chatResponse = new ChatResponse<>();
        ResultSetRole resultSetRole = new ResultSetRole();

        ArrayList<Admin> admins = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
        }.getType());

        resultSetRole.setAdmins(admins);

        chatResponse.setResult(resultSetRole);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String responseJson = gson.toJson(chatResponse);

        listenerManager.callOnRemoveRoleFromUser(responseJson, chatResponse);

        showInfoLog("RECEIVE_REMOVE_RULE", responseJson);
    }

    private void handleUnBlock(ChatMessage chatMessage, String messageUniqueId) {

        Contact contact = gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<ResultBlock> chatResponse = new ChatResponse<>();

        ResultBlock resultBlock = new ResultBlock();
        resultBlock.setContact(contact);

        chatResponse.setResult(resultBlock);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String jsonUnBlock = gson.toJson(chatResponse);

        listenerManager.callOnUnBlock(jsonUnBlock, chatResponse);

        showInfoLog("RECEIVE_UN_BLOCK", jsonUnBlock);
    }

    private void handleOutPutGetBlockList(ChatMessage chatMessage) {
        ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultBlockList resultBlockList = new ResultBlockList();

        List<Contact> contacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());

        resultBlockList.setContentCount(chatMessage.getContentCount());


        resultBlockList.setContentCount(chatMessage.getContentCount());
        resultBlockList.setContacts(contacts);

        chatResponse.setResult(resultBlockList);

        String jsonGetBlock = gson.toJson(chatResponse);

        listenerManager.callOnGetBlockList(jsonGetBlock, chatResponse);

        showInfoLog("RECEIVE_GET_BLOCK_LIST", jsonGetBlock);
    }

    private void handleOutPutRemoveParticipant(ChatMessage chatMessage) {

        ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(chatMessage);

        String jsonRmParticipant = gson.toJson(chatResponse);

        listenerManager.callOnThreadRemoveParticipant(jsonRmParticipant, chatResponse);

        showInfoLog("RECEIVE_REMOVE_PARTICIPANT", jsonRmParticipant);
    }

    private void handleOnUserInfo(ChatMessage chatMessage) {

        ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();
        UserInfo userInfo = gson.fromJson(chatMessage.getContent(), UserInfo.class);
        String userInfoJson = reformatUserInfo(chatMessage, chatResponse, userInfo);
        listenerManager.callOnUserInfo(userInfoJson, chatResponse);

        showInfoLog("RECEIVE_USER_INFO", userInfoJson);
    }

    private String reformatUserInfo(ChatMessage chatMessage, ChatResponse<ResultUserInfo> outPutUserInfo, UserInfo userInfo) {

        ResultUserInfo result = new ResultUserInfo();

        setUserId(userInfo.getId());
        result.setUser(userInfo);
        outPutUserInfo.setErrorCode(0);
        outPutUserInfo.setErrorMessage("");
        outPutUserInfo.setHasError(false);
        outPutUserInfo.setResult(result);
        outPutUserInfo.setUniqueId(chatMessage.getUniqueId());

        return gson.toJson(outPutUserInfo);
    }

    private void handleOutPutGetHistory(ChatMessage chatMessage) {

        List<MessageVO> messageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<MessageVO>>() {
        }.getType());

        ResultHistory resultHistory = new ResultHistory();

        ChatResponse<ResultHistory> chatResponse = new ChatResponse<>();

        resultHistory.setContentCount(chatMessage.getContentCount());

        resultHistory.setHistory(messageVOS);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setResult(resultHistory);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String json = gson.toJson(chatResponse);

        listenerManager.callOnGetThreadHistory(json, chatResponse);

        showInfoLog("RECEIVE_GET_HISTORY", json);
    }

    private String getContactMain(Integer count, Long offset, String typeCode) {
        String uniqueId = generateUniqueId();

        count = count != null && count > 0 ? count : 50;
        offset = offset != null && offset >= 0 ? offset : 0;

        if (chatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setOffset(offset);

            JsonObject jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
            jObj.remove("lastMessageId");
            jObj.remove("firstMessageId");
            jObj.remove("new");

            jObj.remove("count");
            jObj.addProperty("size", count);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(jObj.toString());
            chatMessage.setType(ChatMessageType.GET_CONTACTS);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");
            jsonObject.remove("subjectId");

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 3, "GET_CONTACT_SEND");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;

    }

    private String getErrorOutPut(String errorMessage, long errorCode, String uniqueId) {
        ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);
        String jsonError = gson.toJson(error);

        listenerManager.callOnError(jsonError, error);
        listenerManager.callOnLogEvent(jsonError);


        showErrorLog("ErrorMessage :" + errorMessage + " *Code* " + errorCode + " *uniqueId* " + uniqueId);

        return jsonError;
    }

    private String getTypeCode() {
        return this.typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    private int getExpireAmount() {
        if (Util.isNullOrEmpty(expireAmount)) {
            expireAmount = 2 * 24 * 60 * 60;
        }
        return expireAmount;
    }

    /**
     * @param expireSecond participants and contacts have an expire date in cache and after expireSecond
     *                     they are going to delete from the cache/
     */
    public void setExpireAmount(int expireSecond) {
        this.expireAmount = expireSecond;
    }

    /**
     * The replacement method is getMessageDeliveredList.
     */
    private String deliveredMessageList(RequestDeliveredMessageList requestParams) {
        String uniqueId = generateUniqueId();

        try {
            if (chatReady) {
                if (Util.isNullOrEmpty(requestParams.getCount())) {
                    requestParams.setCount(50);
                }

                JsonObject object = (JsonObject) gson.toJsonTree(requestParams);
                object.remove("typeCode");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setType(ChatMessageType.DELIVERED_MESSAGE_LIST);

                chatMessage.setContent(object.toString());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                String typeCode = requestParams.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, 4, "SEND_DELIVERED_MESSAGE_LIST");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * The replacement method is getMessageSeenList.
     */
    private String seenMessageList(RequestSeenMessageList requestParams) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            try {

                if (Util.isNullOrEmpty(requestParams.getCount())) {
                    requestParams.setCount(50);
                }

                JsonObject object = (JsonObject) gson.toJsonTree(requestParams);
                object.remove("typeCode");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.SEEN_MESSAGE_LIST);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setContent(object.toString());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                String typeCode = requestParams.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, 4, "SEND_SEEN_MESSAGE_LIST");

            } catch (Throwable e) {
                showErrorLog(e.getCause().getMessage());
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    //TODO ChatContract cache
    //TODO Check if participant is null!!!
    private ChatResponse<ResultParticipant> reformatThreadParticipants(ChatMessage chatMessage) {

        ArrayList<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
        }.getType());


        ChatResponse<ResultParticipant> outPutParticipant = new ChatResponse<>();
        outPutParticipant.setErrorCode(0);
        outPutParticipant.setErrorMessage("");
        outPutParticipant.setHasError(false);
        outPutParticipant.setUniqueId(chatMessage.getUniqueId());

        ResultParticipant resultParticipant = new ResultParticipant();

        resultParticipant.setContentCount(chatMessage.getContentCount());

        resultParticipant.setParticipants(participants);
        outPutParticipant.setResult(resultParticipant);
        return outPutParticipant;
    }

    private void sendAsyncMessage(String asyncContent, int asyncMsgType, String logMessage) {
        if (chatReady) {
            showInfoLog(logMessage, asyncContent);

            try {
                async.sendMessage(asyncContent, asyncMsgType);

            } catch (Exception e) {
                showErrorLog(e.getMessage());
                return;
            }

//            pingWithDelay();

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, null);
        }
    }

    private ChatMessage getChatMessage(String contentThreadChat, String uniqueId, String typeCode) {
        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setContent(contentThreadChat);
        chatMessage.setType(ChatMessageType.INVITATION);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

        if (typeCode != null && !typeCode.isEmpty()) {
            chatMessage.setTypeCode(typeCode);
        } else {
            chatMessage.setTypeCode(getTypeCode());
        }
        return chatMessage;
    }

    /**
     * Get the manager that manages registered listeners.
     */
    ChatListenerManager getListenerManager() {
        return listenerManager;
    }

    private ChatMessage getChatMessage(MessageVO jsonMessage) {
        ChatMessage message = new ChatMessage();

        message.setType(ChatMessageType.DELIVERY);
        message.setContent(String.valueOf(jsonMessage.getId()));
        message.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        message.setToken(getToken());
        message.setUniqueId(generateUniqueId());
        //message.setTime(1000);

        return message;
    }

   /* private String reformatUserInfo(ChatMessage chatMessage, ChatResponse<ResultUserInfo> outPutUserInfo, UserInfo userInfo) {

        ResultUserInfo result = new ResultUserInfo();

        setUserId(userInfo.getId());
        result.setUser(userInfo);
        outPutUserInfo.setErrorCode(0);
        outPutUserInfo.setErrorMessage("");
        outPutUserInfo.setHasError(false);
        outPutUserInfo.setResult(result);
        outPutUserInfo.setUniqueId(chatMessage.getUniqueId());

        return gson.toJson(outPutUserInfo);
    }*/

    private String reformatMuteThread(ChatMessage chatMessage, ChatResponse<ResultMute> outPut) {
        ResultMute resultMute = new ResultMute();
        resultMute.setThreadId(Long.parseLong(chatMessage.getContent()));
        outPut.setResult(resultMute);
        outPut.setHasError(false);
        outPut.setErrorMessage("");
        outPut.setUniqueId(chatMessage.getUniqueId());
        gson.toJson(outPut);
        return gson.toJson(outPut);
    }


    private String reformatPinThread(ChatMessage chatMessage, ChatResponse<ResultPinThread> outPut) {
        ResultPinThread requestPinThread = new ResultPinThread();
        requestPinThread.setThreadId(Long.parseLong(chatMessage.getContent()));
        outPut.setResult(requestPinThread);
        outPut.setHasError(false);
        outPut.setErrorMessage("");
        outPut.setUniqueId(chatMessage.getUniqueId());
        gson.toJson(outPut);
        return gson.toJson(outPut);
    }

    private String reformatPinMessage(ChatMessage chatMessage, ChatResponse<ResultPinMessage> outPut) {

        ResultPinMessage resultPinMessage = gson.fromJson(chatMessage.getContent(), ResultPinMessage.class);

        outPut.setResult(resultPinMessage);
        outPut.setHasError(false);
        outPut.setErrorMessage("");
        outPut.setUniqueId(chatMessage.getUniqueId());
        gson.toJson(outPut);
        return gson.toJson(outPut);
    }


    private ChatResponse<ResultThread> reformatCreateThread(ChatMessage chatMessage) {

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultThread resultThread = new ResultThread();

        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);
        resultThread.setThread(thread);
        chatResponse.setResult(resultThread);

        resultThread.setThread(thread);
        return chatResponse;
    }

    /**
     * Reformat the get thread response
     */
    private ChatResponse<ResultThreads> reformatGetThreadsResponse(ChatMessage chatMessage) {
        ChatResponse<ResultThreads> outPutThreads = new ChatResponse<>();
        ArrayList<Thread> threads = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Thread>>() {
        }.getType());

        ResultThreads resultThreads = new ResultThreads();
        resultThreads.setThreads(threads);
        resultThreads.setContentCount(chatMessage.getContentCount());
        outPutThreads.setErrorCode(0);
        outPutThreads.setErrorMessage("");
        outPutThreads.setHasError(false);
        outPutThreads.setUniqueId(chatMessage.getUniqueId());

        outPutThreads.setResult(resultThreads);
        return outPutThreads;
    }

    private String reformatError(boolean hasError, ChatMessage chatMessage, OutPutHistory outPut) {
        Error error = gson.fromJson(chatMessage.getContent(), Error.class);
        showErrorLog("RECEIVED_ERROR", chatMessage.getContent());
        showErrorLog("ErrorMessage", error.getMessage());
        showErrorLog("ErrorCode", String.valueOf(error.getCode()));
        outPut.setHasError(hasError);
        outPut.setErrorMessage(error.getMessage());
        outPut.setErrorCode(error.getCode());

        return gson.toJson(outPut);
    }

    private ChatResponse<ResultContact> reformatGetContactResponse(ChatMessage chatMessage) {
        ResultContact resultContact = new ResultContact();

        ChatResponse<ResultContact> outPutContact = new ChatResponse<>();

        ArrayList<Contact> contacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());

        resultContact.setContacts(contacts);
        resultContact.setContentCount(chatMessage.getContentCount());

        resultContact.setContentCount(chatMessage.getContentCount());

        outPutContact.setResult(resultContact);
        outPutContact.setErrorMessage("");
        outPutContact.setUniqueId(chatMessage.getUniqueId());

        return outPutContact;
    }

    public String getContentType(File file) throws IOException {
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        return fileTypeMap.getContentType(file.getName());
    }

    private String createFileMetadata(String fileName,
                                      String hashCode,
                                      long fileId,
                                      String mimeType,
                                      long fileSize,
                                      String filePath) {

        MetaDataFile metaDataFile = new MetaDataFile();
        FileMetaDataContent metaDataContent = new FileMetaDataContent();

        metaDataContent.setId(fileId);
        metaDataContent.setName(fileName);
        metaDataContent.setMimeType(mimeType);
        metaDataContent.setSize(fileSize);

        if (hashCode != null) {
            metaDataContent.setHashCode(hashCode);
            metaDataContent.setLink(getFile(fileId, hashCode, true));

        } else {
            metaDataContent.setLink(filePath);
        }

        metaDataFile.setFile(metaDataContent);

        return gson.toJson(metaDataFile);
    }

    private String createImageMetadata(File fileUri, String hashCode, long imageId, int actualHeight, int actualWidth, String mimeType
            , long fileSize, String path, boolean isLocation, String center) {

        String originalName = fileUri.getName();
        FileImageMetaData fileMetaData = new FileImageMetaData();


        if (originalName.contains(".")) {
            String editedName = originalName.substring(0, originalName.lastIndexOf('.'));
            fileMetaData.setName(editedName);
        }
        fileMetaData.setHashCode(hashCode);
        fileMetaData.setId(imageId);
        fileMetaData.setOriginalName(originalName);
        fileMetaData.setActualHeight(actualHeight);
        fileMetaData.setActualWidth(actualWidth);
        fileMetaData.setMimeType(mimeType);
        fileMetaData.setSize(fileSize);
        if (!Util.isNullOrEmpty(hashCode)) {
            fileMetaData.setLink(getImage(imageId, hashCode, false));
        } else {
            fileMetaData.setLink(path);
        }
        if (isLocation) {
            MetadataLocationFile locationFile = new MetadataLocationFile();
            MapLocation mapLocation = new MapLocation();

            if (center.contains(",")) {
                String latitude = center.substring(0, center.lastIndexOf(','));
                String longitude = center.substring(center.lastIndexOf(',') + 1, center.length());
                mapLocation.setLatitude(Double.parseDouble(latitude));
                mapLocation.setLongitude(Double.parseDouble(longitude));
            }

            locationFile.setLocation(mapLocation);
            locationFile.setFile(fileMetaData);
            return gson.toJson(locationFile);

        } else {
            MetaDataImageFile metaData = new MetaDataImageFile();
            metaData.setFile(fileMetaData);
            return gson.toJson(metaData);
        }
    }

    /**
     * This method generate url that you can use to get your file
     */
    public String getFile(long fileId, String hashCode, boolean downloadable) {
        return getFileServer() + "/nzh/file/" + "?fileId=" + fileId + "&downloadable=" + downloadable + "&hashCode=" + hashCode;
    }

    /**
     * This method generate url based on your input params that you can use to get your image
     */
    public String getImage(long imageId, String hashCode, boolean downloadable) {
        String url;
        if (downloadable) {
            url = getFileServer() + "/nzh/image/" + "?imageId=" + imageId + "&downloadable=" + downloadable + "&hashCode=" + hashCode;
        } else {
            url = getFileServer() + "/nzh/image/" + "?imageId=" + imageId + "&hashCode=" + hashCode;
        }
        return url;
    }

    private String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private long getUserId() {
        return userId;
    }

    private void setUserId(long userId) {
        this.userId = userId;
    }

    private String getSsoHost() {
        return ssoHost;
    }

    private void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }

    private String getPlatformHost() {
        return platformHost;
    }

    private void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    private String getFileServer() {
        return fileServer;
    }

    private void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    private int getSignalIntervalTime() {
        return signalIntervalTime;
    }

    public void setSignalIntervalTime(int signalIntervalTime) {
        this.signalIntervalTime = signalIntervalTime;
    }

    public interface GetThreadHandler {
        void onGetThread();
    }

    public interface SendTextMessageHandler {
        void onSent(String uniqueId, long threadId);

        void onSentResult(String content);

    }


}