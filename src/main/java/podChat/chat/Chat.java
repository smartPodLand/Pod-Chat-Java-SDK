package podChat.chat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import config.QueueConfigVO;
import exception.ConnectionException;
import io.sentry.Sentry;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.Async;
import podAsync.AsyncAdapter;
import podChat.ProgressHandler;
import podChat.localModel.LFileUpload;
import podChat.mainmodel.*;
import podChat.mainmodel.Thread;
import podChat.model.*;
import podChat.model.Error;
import podChat.model.MapLocation;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created By Khojasteh on 7/29/2019
 */
public class Chat extends AsyncAdapter {
    private static Logger logger = LogManager.getLogger(Chat.class);

    private static Async async;
    private String token;
    private String typeCode = "default";
    private String platformHost;
    private String fileServer;
    private static Chat instance;
    private static ChatListenerManager listenerManager;
    private long userId;
    private ContactApi contactApi;
    private static HashMap<String, Callback> messageCallbacks;
    private static HashMap<Long, ArrayList<Callback>> threadCallbacks;
    private static HashMap<Long, ArrayList<Callback>> sentMessageCallBacks;
    private static HashMap<String, ChatHandler> handlerSend;
    private long lastSentMessageTime;
    private boolean chatReady = false;
    private boolean asyncReady = false;
    private static final int TOKEN_ISSUER = 1;
    private long retryStepUserInfo = 1;
    private int signalIntervalTime;
    private int expireAmount;
    private static Gson gson;
    private boolean userInfoResponse = false;
    private long ttl;
    private String ssoHost;
    private QueueConfigVO queueConfigVO;

    public static boolean isLoggable;


    private Chat() {
    }

    /**
     * Initialize the Chat
     **/
    public synchronized static Chat init(boolean useSentry, boolean loggable) {
        if (instance == null) {
            //TODO
            if (useSentry) Sentry.init("");
            isLoggable = loggable;
            async = Async.getInstance();
            instance = new Chat();
            gson = new Gson();
            listenerManager = new ChatListenerManager();
            threadCallbacks = new HashMap<>();

        }
        return instance;
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
        long threadId = chatMessage != null ? chatMessage.getSubjectId() : 0;
        Callback callback = null;

        if (messageCallbacks.containsKey(messageUniqueId)) {
            callback = messageCallbacks.get(messageUniqueId);
        }

        if (chatMessage != null) {
            messageType = chatMessage.getType();
        }

        switch (messageType) {
            case ChatMessageType.ADD_PARTICIPANT:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.UNBLOCK:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.BLOCK:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.CHANGE_TYPE:
                break;
            case ChatMessageType.SENT:
                handleSent(chatMessage, messageUniqueId, threadId);
                break;
            case ChatMessageType.DELIVERY:
                handleDelivery(chatMessage, messageUniqueId, threadId);
                break;
            case ChatMessageType.SEEN:
                handleSeen(chatMessage, messageUniqueId, threadId);
                break;
            case ChatMessageType.ERROR:
                handleError(chatMessage);
                break;
            case ChatMessageType.FORWARD_MESSAGE:
                handleForwardMessage(chatMessage);
                break;
            case ChatMessageType.GET_CONTACTS:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.GET_HISTORY:
                /*Remove uniqueIds from waitQueue /***/
                if (callback == null) {
                    handleRemoveFromWaitQueue(chatMessage);
                } else {
                    handleResponseMessage(callback, chatMessage, messageUniqueId);
                }
                break;
            case ChatMessageType.GET_STATUS:
                break;
            case ChatMessageType.GET_THREADS:
                if (callback == null) {
                    handleGetThreads(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.INVITATION:
                if (callback == null) {
                    handleCreateThread(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.REMOVED_FROM_THREAD:
                handleRemoveFromThread(chatMessage);
                break;
            case ChatMessageType.LEAVE_THREAD:
                if (callback == null) {
                    handleOutPutLeaveThread(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.MESSAGE:
                handleNewMessage(chatMessage);
                break;
            case ChatMessageType.MUTE_THREAD:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.PING:
                // handleOnPing(chatMessage);
                break;
            case ChatMessageType.RELATION_INFO:
                break;
            case ChatMessageType.REMOVE_PARTICIPANT:
                if (callback == null) {
                    handleOutPutRemoveParticipant(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.RENAME:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.THREAD_PARTICIPANTS:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.UN_MUTE_THREAD:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.USER_INFO:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.USER_STATUS:
                break;
            case ChatMessageType.GET_BLOCKED:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.DELETE_MESSAGE:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.EDIT_MESSAGE:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.THREAD_INFO_UPDATED:
                handleThreadInfoUpdated(chatMessage);
                break;
            case ChatMessageType.LAST_SEEN_UPDATED:
                handleLastSeenUpdated(chatMessage);
                break;
            case ChatMessageType.UPDATE_THREAD_INFO:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.SPAM_PV_THREAD:
                break;
            case ChatMessageType.DELIVERED_MESSAGE_LIST:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.SEEN_MESSAGE_LIST:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case ChatMessageType.SET_RULE_TO_USER:

                handleSetRule(chatMessage);
                break;
            case ChatMessageType.CLEAR_HISTORY:
                handleClearHistory(chatMessage);
                break;

            case ChatMessageType.GET_THREAD_ADMINS:
                //TODO handle response
                listenerManager.callOnGetThreadAdmin(chatMessage.getContent());
                break;
        }
    }

    @Override
    public void onStateChanged(String state) throws IOException {
        showInfoLog("State change: " + state);

        super.onStateChanged(state);
        listenerManager.callOnChatState(state);

        switch (state) {
            case ChatStateType.OPEN:

                break;
            case ChatStateType.ASYNC_READY:
                asyncReady = true;
                retryOnGetUserInfo();
                break;
            case ChatStateType.CONNECTING:
                chatReady = false;
                TokenExecutor.stopThread();
                break;
            case ChatStateType.CLOSING:
                chatReady = false;
                TokenExecutor.stopThread();
                break;
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
            messageCallbacks = new HashMap<>();
            handlerSend = new HashMap<>();
            async.addListener(this);

            setPlatformHost(requestConnect.getPlatformHost());
            setToken(requestConnect.getToken());
            setSsoHost(requestConnect.getSsoHost());

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
    public String sendTextMessage(String textMessage, long threadId, Integer messageType, String jsonSystemMetadata, ChatHandler handler) {

        String asyncContentWaitQueue;
        String uniqueId = generateUniqueId();

        try {

            ChatMessage chatMessageQueue = new ChatMessage();
            chatMessageQueue.setContent(textMessage);
            chatMessageQueue.setType(ChatMessageType.MESSAGE);
            chatMessageQueue.setTokenIssuer("1");
            chatMessageQueue.setToken(getToken());

            if (jsonSystemMetadata != null) {
                chatMessageQueue.setSystemMetadata(jsonSystemMetadata);
            }

            chatMessageQueue.setUniqueId(uniqueId);
            //chatMessageQueue.setTime(1000);
            chatMessageQueue.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageQueue);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }
            if (!Util.isNullOrEmpty(messageType)) {
                jsonObject.addProperty("messageType", messageType);
            } else {
                jsonObject.remove("messageType");
            }

            asyncContentWaitQueue = jsonObject.toString();

            if (chatReady) {
                //TODO handler is not complete!

                if (handler != null) {
                    handler.onSent(uniqueId, threadId);
                    handler.onSentResult(null);
                    handlerSend.put(uniqueId, handler);
                }
//TODO check messageID

                setThreadCallbacks(threadId, uniqueId, 0);
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
     * @param requestAddAdmin You can add or remove someone as admin to some thread set
     *                        and roles to them
     *                        `setRoleOperation` could be `Add` or `remove`
     */
    public String setAdmin(RequestAddAdmin requestAddAdmin) {
        long threadId = requestAddAdmin.getThreadId();

        ArrayList<RequestRole> roles = requestAddAdmin.getRoles();

        String uniqueId = generateUniqueId();

        if (chatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();
            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setCheckThreadMembership(true);
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVO.setRoleOperation(requestRole.getRoleOperation());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(getToken());
            chatMessage.setType(ChatMessageType.SET_RULE_TO_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);

            setCallBacks(null, null, null, true, ChatMessageType.SET_RULE_TO_USER, null, uniqueId);
            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, 4, "SET_RULE_TO_USER");
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
    public String sendTextMessage(RequestMessage requestMessage, ChatHandler handler) {
        String textMessage = requestMessage.getTextMessage();
        long threadId = requestMessage.getThreadId();
        int messageType = requestMessage.getMessageType();
        String jsonMetaData = requestMessage.getJsonMetaData();

        return sendTextMessage(textMessage, threadId, messageType, jsonMetaData, handler);
    }


    /**
     * Get the list of threads or you can just pass the thread id that you want
     *
     * @param count  number of thread
     * @param offset specified offset you want
     */
    @Deprecated
    public String getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName, ChatHandler handler) {

        String uniqueId;
        count = count != null ? count : 50;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                ChatMessageContent chatMessageContent = new ChatMessageContent();

                Long offsets;
                if (offset != null) {
                    chatMessageContent.setOffset(offset);
                    offsets = offset;
                } else {
                    chatMessageContent.setOffset(0);
                    offsets = 0L;
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
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                setCallBacks(null, null, null, true, ChatMessageType.GET_THREADS, offsets, uniqueId);

                sendAsyncMessage(jsonObject.toString(), 3, "Get thread send");
                if (handler != null) {
                    handler.onGetThread(uniqueId);
                }
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
     * @param handler              Its not working yet
     * @param threadIds            List of thread ids that you want to get
     * @param threadName           Name of the thread that you want to get
     */
    @Deprecated
    public String getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,
                             long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, ChatHandler handler) {

        String uniqueId;
        count = count != null ? count : 50;
        uniqueId = generateUniqueId();
        try {

            if (chatReady) {
                ChatMessageContent chatMessageContent = new ChatMessageContent();

                Long offsets;
                if (offset != null) {
                    chatMessageContent.setOffset(offset);
                    offsets = offset;
                } else {
                    chatMessageContent.setOffset(0);
                    offsets = 0L;
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
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                setCallBacks(null, null, null, true, ChatMessageType.GET_THREADS, offsets, uniqueId);

                sendAsyncMessage(jsonObject.toString(), 3, "Get thread send");
                if (handler != null) {
                    handler.onGetThread(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }


    public String getThreads(RequestThread requestThread, ChatHandler handler) {
        ArrayList<Integer> threadIds = requestThread.getThreadIds();
        long offset = requestThread.getOffset();
        long creatorCoreUserId = requestThread.getCreatorCoreUserId();
        long partnerCoreContactId = requestThread.getPartnerCoreContactId();
        long partnerCoreUserId = requestThread.getPartnerCoreUserId();
        String threadName = requestThread.getThreadName();
        long count = requestThread.getCount();

        return getThreads((int) count, offset, threadIds, threadName, creatorCoreUserId, partnerCoreUserId, partnerCoreContactId, handler);
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
    public String getHistory(History history, long threadId, ChatHandler handler) {
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
            getHistoryMain(history, threadId, handler, uniqueId);
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
    public String getHistory(RequestGetHistory request, ChatHandler handler) {
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
                    .id(request.getId()).build();

            getHistoryMain(history, request.getThreadId(), handler, uniqueId);

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
            chatMessage.setTokenIssuer("1");
            chatMessage.setSubjectId(threadId);
            chatMessage.setUniqueId(uniqueId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");
            jsonObject.remove("contentCount");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, ChatMessageType.CLEAR_HISTORY, null, uniqueId);

            sendAsyncMessage(asyncContent, 4, "SEND_CLEAR_HISTORY");
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
     * @Param long userId
     * @Param long id         Id of the message
     * @Param String query
     * @Param long fromTime    Start Time of the messages
     * @Param long fromTimeNanos  Start Time of the messages in Nano second
     * @Param long toTime         End time of the messages
     * @Param long toTimeNanos    End time of the messages
     * @Param NosqlSearchMetadataCriteria metadataCriteria
     * ------ String field
     * ------ String is
     * ------ String has
     * ------ String gt
     * ------ String gte
     * ------ String lt
     * ------ String lte
     * ------ List<NosqlSearchMetadataCriteria> and
     * ------ List<NosqlSearchMetadataCriteria> or
     * ------ List<NosqlSearchMetadataCriteria> not
     **/
    public String searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            String content = gson.toJson(messageCriteriaVO);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setType(ChatMessageType.GET_HISTORY);
            chatMessage.setToken(getToken());
            chatMessage.setTokenIssuer("1");
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(messageCriteriaVO.getMessageThreadId());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, ChatMessageType.GET_HISTORY, messageCriteriaVO.getOffset(), uniqueId);

            sendAsyncMessage(asyncContent, 3, "SEND SEARCH0. HISTORY");

            if (handler != null) {
                handler.onSearchHistory(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }


    /**
     * Get all of the contacts of the user
     */
    public String getContacts(RequestGetContact request, ChatHandler handler) {

        Long offset = request.getOffset();
        Long count = request.getCount();

        return getContacts(count.intValue(), offset, handler);
    }

    /**
     * Get all of the contacts of the user
     */
    public String getContacts(Integer count, Long offset, ChatHandler handler) {
        return getContactMain(count, offset, false, handler);
    }

    /**
     * Add one contact to the contact list
     *
     * @param firstName       Notice: if just put fistName without lastName its ok.
     * @param lastName        last name of the contact
     * @param cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * @param email           email of the contact
     */
    public String addContact(String firstName, String lastName, String cellphoneNumber, String email) {

        typeCode = getTypeCode();

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

            if (Util.isNullOrEmpty(getTypeCode())) {
                addContactService = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber);

            } else {
                addContactService = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber, typeCode);

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
                    showErrorLog(response.body().getMessage());
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

        typeCode = getTypeCode();

        return addContact(firstName, lastName, cellphoneNumber, email);
    }

    /**
     * Remove contact with the user id
     *
     * @param userId id of the user that we want to remove from contact list
     */
    public String removeContact(long userId) {
        String uniqueId = generateUniqueId();

        Call<ContactRemove> removeContactObservable;

        if (chatReady) {

            if (Util.isNullOrEmpty(getTypeCode())) {
                removeContactObservable = contactApi.removeContact(getToken(), 1, userId);
            } else {
                removeContactObservable = contactApi.removeContact(getToken(), 1, userId, getTypeCode());
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
        return removeContact(userId);
    }


    /**
     * Update contacts
     * all of the params all required to update
     */
    public String updateContact(long userId, String firstName, String lastName, String cellphoneNumber, String email) {

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

            if (Util.isNullOrEmpty(getTypeCode())) {
                updateContactObservable = contactApi.updateContact(getToken(), 1, userId, firstName, lastName, email, uniqueId, cellphoneNumber);
            } else {
                updateContactObservable = contactApi.updateContact(getToken(), 1, userId, firstName, lastName, email, uniqueId, cellphoneNumber, getTypeCode());
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

        return updateContact(userId, firstName, lastName, cellphoneNumber, email);
    }

//TODO description

    /**
     * @param requestSearchContact
     * @return
     */
    public String searchContact(RequestSearchContact requestSearchContact) {
        String uniqueId = generateUniqueId();
        String type_code;

        if (requestSearchContact.getTypeCode() != null && !requestSearchContact.getTypeCode().isEmpty()) {
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
                    , generateUniqueId()
                    , offset
                    , size
                    , type_code
                    , requestSearchContact.getQuery()
                    , requestSearchContact.getCellphoneNumber());


            RetrofitUtil.request(searchContactCall, new ApiListener<SearchContactVO>() {
                @Override
                public void onSuccess(SearchContactVO searchContactVO) {
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
    public String deleteMessage(Long messageId, Boolean deleteForAll, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        if (chatReady) {

            deleteForAll = deleteForAll != null ? deleteForAll : false;

            BaseMessage baseMessage = new BaseMessage();

            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("deleteForAll", deleteForAll);


            baseMessage.setContent(contentObj.toString());
            baseMessage.setToken(getToken());
            baseMessage.setTokenIssuer("1");
            baseMessage.setType(ChatMessageType.DELETE_MESSAGE);
            baseMessage.setUniqueId(uniqueId);
            baseMessage.setSubjectId(messageId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(baseMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_DELETE_MESSAGE");
            setCallBacks(null, null, null, true, ChatMessageType.DELETE_MESSAGE, null, uniqueId);

            if (handler != null) {
                handler.onDeleteMessage(uniqueId);
            }

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
    public List<String> forwardMessage(long threadId, ArrayList<Long> messageIds) {
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
//            ObjectMapper mapper = new ObjectMapper();
            chatMessageForward.setSubjectId(threadId);

            threadCallbacks.put(threadId, callbacks);

            String jsonUniqueIds = Util.listToJson(uniqueIds, gson);

            chatMessageForward.setUniqueId(jsonUniqueIds);
            chatMessageForward.setContent(messageIds.toString());
            chatMessageForward.setToken(getToken());
            chatMessageForward.setTokenIssuer("1");
            chatMessageForward.setType(ChatMessageType.FORWARD_MESSAGE);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageForward);

            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
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
        return forwardMessage(request.getThreadId(), request.getMessageIds());
    }


    /**
     * @param messageIds
     * @param deleteForAll
     * @param handler
     */

    public List<String> deleteMultipleMessage(ArrayList<Long> messageIds, Boolean deleteForAll, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        List<String> uniqueIds = new ArrayList<>();

        if (chatReady) {

            deleteForAll = deleteForAll != null ? deleteForAll : false;

            BaseMessage baseMessage = new BaseMessage();


            for (Long id : messageIds) {

                String uniqueId1 = generateUniqueId();

                uniqueIds.add(uniqueId1);

                setCallBacks(null, null, null, true, ChatMessageType.DELETE_MESSAGE, null, uniqueId1);


            }

            JsonObject contentObj = new JsonObject();


            JsonElement messageIdsElement = gson.toJsonTree(messageIds, new TypeToken<List<Long>>() {
            }.getType());

            JsonElement uniqueIdsElement = gson.toJsonTree(uniqueIds, new TypeToken<List<String>>() {
            }.getType());


            contentObj.add("id", messageIdsElement.getAsJsonArray());
            contentObj.add("uniqueIds", uniqueIdsElement.getAsJsonArray());
            contentObj.addProperty("deleteForAll", deleteForAll);


            baseMessage.setContent(contentObj.toString());
            baseMessage.setToken(getToken());
            baseMessage.setTokenIssuer("1");
            baseMessage.setType(ChatMessageType.DELETE_MESSAGE);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(baseMessage);


            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            jsonObject.remove("subjectId");

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_DELETE_MESSAGE");


            if (handler != null) {
                handler.onDeleteMessage(uniqueId);
            }

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
    public List<String> deleteMultipleMessage(RequestDeleteMessage request, ChatHandler handler) {

        return deleteMultipleMessage(request.getMessageIds(), request.isDeleteForAll(), handler);
    }


    /**
     * DELETE MESSAGE IN THREAD
     * <p>
     * messageId    Id of the message that you want to be removed.
     * deleteForAll If you want to delete message for everyone you can set it true if u don't want
     * you can set it false or even null.
     */
    public String deleteMessage(RequestDeleteMessage request, ChatHandler handler) {
        if (request.getMessageIds().size() > 1) {
            return getErrorOutPut(ChatConstant.ERROR_NUMBER_MESSAGEID, ChatConstant.ERROR_CODE_NUMBER_MESSAGEID, null);

        }
        return deleteMessage(request.getMessageIds().get(0), request.isDeleteForAll(), handler);
    }

    /**
     * Get the participant list of specific thread
     * <p>
     *
     * @ param long threadId id of the thread we want to get the participant list
     * @ param long count number of the participant wanted to get
     * @ param long offset offset of the participant list
     */
    public String getThreadParticipants(RequestThreadParticipant request, ChatHandler handler) {

        long count = request.getCount();
        long offset = request.getOffset();
        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();

        return getThreadParticipantsMain((int) count, offset, threadId, typeCode, handler);
    }

    /**
     * Get the participant list of specific thread
     *
     * @param threadId id of the thread we want to ge the participant list
     */
    @Deprecated
    public String getThreadParticipants(Integer count, Long offset, long threadId, ChatHandler handler) {
        return getThreadParticipantsMain(count, offset, threadId, null, handler);
    }

    private String getThreadParticipantsMain(Integer count, Long offset, long threadId, String typeCode, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        offset = offset != null ? offset : 0;
        count = count != null ? count : 50;

        if (chatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setCount(count);
            chatMessageContent.setOffset(offset);

            String content = gson.toJson(chatMessageContent);

            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setContent(content);
            chatMessage.setType(ChatMessageType.THREAD_PARTICIPANTS);
            chatMessage.setTokenIssuer("1");
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

            jsonObject.remove("lastMessageId");
            jsonObject.remove("firstMessageId");
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, ChatMessageType.THREAD_PARTICIPANTS, offset, uniqueId);

            sendAsyncMessage(asyncContent, 3, "SEND_THREAD_PARTICIPANT");

            if (handler != null) {
                handler.onGetThreadParticipant(uniqueId);
            }

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;

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
            , String metadata, ChatHandler handler) {

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

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, ChatMessageType.INVITATION, null, uniqueId);

            sendAsyncMessage(asyncContent, 4, "SEND_CREATE_THREAD");

            if (handler != null) {
                handler.onCreateThread(uniqueId);
            }

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
    public ArrayList<String> createThreadWithMessage(RequestCreateThread threadRequest) {
        List<String> forwardUniqueIds;
        JsonObject innerMessageObj;
        JsonObject jsonObject;

        String threadUniqueId = generateUniqueId();

        ArrayList<String> uniqueIds = new ArrayList<>();
        uniqueIds.add(threadUniqueId);
        try {
            if (chatReady) {

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

                    setCallBacks(true, true, true, true, ChatMessageType.MESSAGE, null, newMsgUniqueId);
                }

                if (!Util.isNullOrEmptyNumber(threadRequest.getMessage().getForwardedMessageIds())) {

                    /** Its generated new unique id for each forward message*/
                    List<Long> messageIds = threadRequest.getMessage().getForwardedMessageIds();
                    forwardUniqueIds = new ArrayList<>();

                    for (long ids : messageIds) {
                        String frwMsgUniqueId = generateUniqueId();

                        forwardUniqueIds.add(frwMsgUniqueId);
                        uniqueIds.add(frwMsgUniqueId);

                        setCallBacks(true, true, true, true, ChatMessageType.MESSAGE, null, frwMsgUniqueId);
                    }
                    JsonElement element = gson.toJsonTree(forwardUniqueIds, new TypeToken<List<Long>>() {
                    }.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    innerMessageObj.add("forwardedUniqueIds", jsonArray);
                } else {
                    innerMessageObj.remove("forwardedUniqueIds");
                    innerMessageObj.remove("forwardedMessageIds");
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
                chatMessage.setTokenIssuer("1");

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                jsonObject.remove("repliedTo");
                jsonObject.remove("subjectId");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("contentCount");

                String typeCode = threadRequest.getTypeCode();

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.addProperty("typeCode", typeCode);
                }

                setCallBacks(null, null, null, true, ChatMessageType.INVITATION, null, threadUniqueId);

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
    public String updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler) {
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

                chatMessage.setTokenIssuer("1");
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

                setCallBacks(null, null, null, true, ChatMessageType.UPDATE_THREAD_INFO, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), 4, "SEND_UPDATE_THREAD_INFO");
                if (handler != null) {
                    handler.onUpdateThreadInfo(uniqueId);
                }
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

    public String updateThreadInfo(RequestThreadInfo request, ChatHandler handler) {
        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder().title(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .metadat(request.getMetadata())
                .build();
        return updateThreadInfo(request.getThreadId(), threadInfoVO, handler);
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     * <p>
     * messageContent content of the reply message
     * threadId       id of the thread
     * messageId      of the message that we want to reply
     * metaData       meta data of the message
     */
    public String replyMessage(RequestReplyMessage request, ChatHandler handler) {
        long threadId = request.getThreadId();
        long messageId = request.getMessageId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        int messageType = request.getMessageType();

        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, handler);
    }

    private String mainReplyMessage(String messageContent, long threadId, long messageId, String systemMetaData, Integer messageType, String metaData, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setRepliedTo(messageId);
        chatMessage.setSubjectId(threadId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setContent(messageContent);
        chatMessage.setMetadata(metaData);
        // chatMessage.setTime(1000);
        chatMessage.setType(ChatMessageType.MESSAGE);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(systemMetaData)) {
            jsonObject.remove("systemMetaData");
        } else {
            jsonObject.remove("systemMetaData");
            jsonObject.addProperty("systemMetaData", systemMetaData);
        }

        if (Util.isNullOrEmpty(getTypeCode())) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", getTypeCode());
        }

        if (Util.isNullOrEmpty(messageType)) {
            jsonObject.remove("messageType");
        } else {
            jsonObject.remove("messageType");
            jsonObject.addProperty("messageType", messageType);
        }

        String asyncContent = jsonObject.toString();

        if (chatReady) {

            setThreadCallbacks(threadId, uniqueId, messageId);

            sendAsyncMessage(asyncContent, 4, "SEND_REPLY_MESSAGE");

            if (handler != null) {
                handler.onReplyMessage(uniqueId);
            }

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
    public String replyMessage(String messageContent, long threadId, long messageId, String systemMetaData, Integer messageType, ChatHandler handler) {
        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, handler);
    }


    /**
     * In order to send seen message you have to call this method
     */
    public String seenMessage(long messageId, long ownerId, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            if (ownerId != getUserId()) {
                ChatMessage message = new ChatMessage();
                message.setType(ChatMessageType.SEEN);
                message.setContent(String.valueOf(messageId));
                message.setTokenIssuer("1");
                message.setToken(getToken());
                message.setUniqueId(uniqueId);
                // message.setTime(1000);

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
                if (handler != null) {
                    handler.onSeen(uniqueId);
                }
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * In order to send seen message you have to call {@link #seenMessage(long, long, ChatHandler)}
     */
    public String seenMessage(RequestSeenMessage request, ChatHandler handler) {
        long messageId = request.getMessageId();
        long ownerId = request.getOwnerId();

        return seenMessage(messageId, ownerId, handler);
    }

    /**
     * It Gets the information of the current user
     */
    public String getUserInfo(ChatHandler handler) {
        String uniqueId = generateUniqueId();
        try {
            if (asyncReady) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.USER_INFO);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");

                setCallBacks(null, null, null, true, ChatMessageType.USER_INFO, null, uniqueId);

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

                if (handler != null) {
                    handler.onGetUserInfo(uniqueId);
                }
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * It Mutes the thread so notification is set to off for that thread
     */
    public String muteThread(long threadId, ChatHandler handler) {
        String uniqueId;
        JsonObject jsonObject;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
//                long threadId = request.getThreadId();
//                String typeCode = request.getTypeCode();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");
                chatMessage.setSubjectId(threadId);


                chatMessage.setUniqueId(uniqueId);

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
                }

                setCallBacks(null, null, null, true, ChatMessageType.MUTE_THREAD, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), 4, "SEND_MUTE_THREAD");

                if (handler != null) {
                    handler.onMuteThread(uniqueId);
                }
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
    public String muteThread(RequestMuteThread request, ChatHandler handler) {
        return muteThread(request.getThreadId(), handler);
    }

    /**
     * It Un mutes the thread so notification is on for that thread
     */
    public String unMuteThread(RequestMuteThread request, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        JsonObject jsonObject = null;
        try {
            if (chatReady) {
                long threadId = request.getThreadId();
                String typeCode = request.getTypeCode();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UN_MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);

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

                setCallBacks(null, null, null, true, ChatMessageType.UN_MUTE_THREAD, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), 4, "SEND_UN_MUTE_THREAD");
                if (handler != null) {
                    handler.onUnMuteThread(uniqueId);
                }
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
    public String unMuteThread(long threadId, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UN_MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");
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

                setCallBacks(null, null, null, true, ChatMessageType.UN_MUTE_THREAD, null, uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_UN_MUTE_THREAD");
                if (handler != null) {
                    handler.onUnMuteThread(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

        return uploadImage(filePath, xC, yC, hC, wC);
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
                                    showInfoLog("RECEIVE_UPLOAD_FILE");
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
    public String sendFileMessage(String description, long threadId, String filePath, String systemMetaData, Integer messageType, int xC, int yC, int hC, int wC, ProgressHandler.sendFileMessage handler) {

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

                                String jsonMeta = createFileMetadata(finalFile, hashCode, fileId, mimeType, file_size, "");

                                listenerManager.callOnLogEvent(jsonMeta);

                                if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {
                                    mainReplyMessage(description, threadId, messageId, systemMetadata, messageType, jsonMeta, null);

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
        chatMessage.setTokenIssuer("1");
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
            setThreadCallbacks(threadId, uniqueId, 0);

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
                                    mainReplyMessage(description, threadId, messageId, systemMetaData, messageType, metaJson, null);

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
    public String editMessage(int messageId, String messageContent, String systemMetaData, ChatHandler handler) {
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
                chatMessage.setTokenIssuer("1");

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                jsonObject.remove("metadata");

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, ChatMessageType.EDIT_MESSAGE, null, uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_EDIT_MESSAGE");
                if (handler != null) {
                    handler.onEditMessage(uniqueId);
                }
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
    public String editMessage(RequestEditMessage request, ChatHandler handler) {
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
                chatMessage.setTokenIssuer("1");

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                setCallBacks(null, null, null, true, ChatMessageType.EDIT_MESSAGE, null, uniqueId);

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_EDIT_MESSAGE");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
            if (handler != null) {
                handler.onEditMessage(uniqueId);
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
    public String addParticipants(long threadId, List<Long> contactIds, ChatHandler handler) {
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
                addParticipant.setTokenIssuer("1");
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

                setCallBacks(null, null, null, true, ChatMessageType.ADD_PARTICIPANT, null, uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_ADD_PARTICIPANTS");
                if (handler != null) {
                    handler.onAddParticipants(uniqueId);
                }

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
    public String addParticipants(RequestAddParticipants request, ChatHandler handler) {

        String uniqueId = generateUniqueId();

        if (chatReady) {
            JsonArray contacts = new JsonArray();

            for (Long p : request.getContactIds()) {
                contacts.add(p);
            }
            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setTokenIssuer("1");
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
            if (Util.isNullOrEmpty(typeCode)) {
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            setCallBacks(null, null, null, true, ChatMessageType.ADD_PARTICIPANT, null, uniqueId);
            sendAsyncMessage(jsonObject.toString(), 4, "SEND_ADD_PARTICIPANTS");
            if (handler != null) {
                handler.onAddParticipants(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * @param participantIds List of PARTICIPANT IDs that gets from {@link #getThreadParticipants}
     * @param threadId       Id of the thread that we wants to remove their participant
     */
    public String removeParticipants(long threadId, List<Long> participantIds, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            RemoveParticipant removeParticipant = new RemoveParticipant();

            removeParticipant.setTokenIssuer("1");
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

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_REMOVE_PARTICIPANT");

            setCallBacks(null, null, null, true, ChatMessageType.REMOVE_PARTICIPANT, null, uniqueId);

            if (handler != null) {
                handler.onRemoveParticipants(uniqueId);
            }

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * participantIds List of PARTICIPANT IDs from Thread's Participants object
     * threadId       Id of the thread that we wants to remove their participant
     */
    public String removeParticipants(RequestRemoveParticipants request, ChatHandler handler) {

        List<Long> participantIds = request.getParticipantIds();
        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();

        return removeParticipants(threadId, participantIds, handler);
    }

    /**
     * It leaves the thread that you are in there
     */
    public String leaveThread(long threadId, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            RemoveParticipant removeParticipant = new RemoveParticipant();

            removeParticipant.setSubjectId(threadId);
            removeParticipant.setToken(getToken());
            removeParticipant.setTokenIssuer("1");
            removeParticipant.setUniqueId(uniqueId);
            removeParticipant.setType(ChatMessageType.LEAVE_THREAD);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(removeParticipant);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, ChatMessageType.LEAVE_THREAD, null, uniqueId);

            sendAsyncMessage(asyncContent, 4, "SEND_LEAVE_THREAD");

            if (handler != null) {
                handler.onLeaveThread(uniqueId);
            }
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
    public String leaveThread(RequestLeaveThread request, ChatHandler handler) {

        return leaveThread(request.getThreadId(), handler);
    }

    public String getMessageDeliveredList(RequestDeliveredMessageList requestParams) {
        return deliveredMessageList(requestParams);
    }

    public String getMessageSeenList(RequestSeenMessageList requestParams) {
        return seenMessageList(requestParams);
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


    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }


    /**
     * @param expireSecond participants and contacts have an expire date in cache and after expireSecond
     *                     they are going to delete from the cache/
     */
    public void setExpireAmount(int expireSecond) {
        this.expireAmount = expireSecond;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }


    public String getAdminList(RequestGetAdmin requestGetAdmin) {
        String uniqueId = generateUniqueId();

        long threadId = requestGetAdmin.getThreadId();

        if (chatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.GET_THREAD_ADMINS);
            chatMessage.setToken(getToken());
            chatMessage.setTokenIssuer("1");
            chatMessage.setSubjectId(threadId);
            chatMessage.setUniqueId(uniqueId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");
            jsonObject.remove("contentCount");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, ChatMessageType.CLEAR_HISTORY, null, uniqueId);

            sendAsyncMessage(asyncContent, 4, "SEND_GET_THREAD_ADMINS");
        }
        return uniqueId;
    }

    /**
     * This Function sends ping message to keep user connected to
     * chat server and updates its status
     * <p>
     * long lastSentMessageTimeout =
     * long lastSentMessageTime =
     */

    private void pingWithDelay() {
        long lastSentMessageTimeout = 9 * 1000;
        lastSentMessageTime = new Date().getTime();

        PingExecutor.getInstance().
                scheduleAtFixedRate(() -> checkForPing(lastSentMessageTimeout),
                        0, 20000,
                        TimeUnit.MILLISECONDS);
    }

    public void checkForPing(long lastSentMessageTimeout) {
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
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());

            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, 4, "CHAT PING");
        }
    }


    private void showInfoLog(String i, String json) {
        if (isLoggable) logger.info(i + "\n \n" + json);

        if (!Util.isNullOrEmpty(json)) {
            listenerManager.callOnLogEvent(json);
        }
    }

    private void showInfoLog(String json) {
        if (isLoggable) logger.info("\n \n" + json);
    }


    private void showErrorLog(String i, String json) {
        if (isLoggable) logger.error(i + "\n \n" + json);

        if (!Util.isNullOrEmpty(json)) {
            listenerManager.callOnLogEvent(json);
        }
    }

    private void showErrorLog(String e) {
        if (isLoggable) logger.error("\n \n" + e);

    }

    private void showErrorLog(Throwable throwable) {
        if (isLoggable) logger.error("\n \n" + throwable.getMessage());
    }

    private void handleError(ChatMessage chatMessage) {

        Error error = gson.fromJson(chatMessage.getContent(), Error.class);
        if (error.getCode() == 401) {
            PingExecutor.stopThread();
        } else if (error.getCode() == 21) {
            userInfoResponse = true;
            retryStepUserInfo = 1;
            chatReady = false;

            GetInfoExecutor.stopThread();

            String errorMessage = error.getMessage();
            long errorCode = error.getCode();
            getErrorOutPut(errorMessage, errorCode, chatMessage.getUniqueId());

            PingExecutor.stopThread();

            /*we are Changing the state of the chat because of the Client is not Authenticate*/
            listenerManager.callOnChatState("ASYNC_READY");

            return;
        }
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
        podChat.mainmodel.Thread thread = gson.fromJson(chatMessage.getContent(), podChat.mainmodel.Thread.class);
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
        podChat.mainmodel.Thread thread = new podChat.mainmodel.Thread();
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
    private void handleOnPing(ChatMessage chatMessage) {

        showInfoLog("RECEIVED_CHAT_PING", "");

        chatReady = true;

        GetInfoExecutor.stopThread();

        listenerManager.callOnChatState(ChatStateType.CHAT_READY);
        showInfoLog("** CLIENT_AUTHENTICATED_NOW", "");
        pingWithDelay();

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

//            if (!threadCallbacks.containsKey(chatMessage.getSubjectId())) {
//                setThreadCallbacks(chatMessage.getSubjectId(), chatMessage.getUniqueId());
//            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

    }

    //TODO Problem in message id in forwardMessage
    private void handleSent(ChatMessage chatMessage, String messageUniqueId, long threadId) {
        boolean found = false;
        try {

            if (threadCallbacks.containsKey(threadId)) {
                ArrayList<Callback> callbacks = threadCallbacks.get(threadId);

                for (Callback callback : callbacks) {

                    if (messageUniqueId.equals(callback.getUniqueId())) {
                        found = true;

                        int indexUnique = callbacks.indexOf(callback);

                        if (callbacks.get(indexUnique).isSent()) {

                            ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                            ResultMessage resultMessage = new ResultMessage();

                            chatResponse.setErrorCode(0);
                            chatResponse.setErrorMessage("");
                            chatResponse.setHasError(false);
                            chatResponse.setUniqueId(callbacks.get(indexUnique).getUniqueId());

                            resultMessage.setConversationId(chatMessage.getSubjectId());
                            resultMessage.setMessageId(Long.valueOf(chatMessage.getContent()));
                            chatResponse.setResult(resultMessage);

                            String json = gson.toJson(chatResponse);
                            listenerManager.callOnSentMessage(json, chatResponse);


//                            if (handlerSend.get(callback.getUniqueId()) != null) {
//                                handlerSend.get(callback.getUniqueId()).onSentResult(chatMessage.getContent());
//                            }

                            Callback callbackUpdateSent = new Callback();
                            callbackUpdateSent.setSent(false);
                            callbackUpdateSent.setDelivery(callback.isDelivery());
                            callbackUpdateSent.setSeen(callback.isSeen());
                            callbackUpdateSent.setUniqueId(callbacks.get(indexUnique).getUniqueId());
                            callbackUpdateSent.setMessageId(callbacks.get(indexUnique).getMessageId());


                            callbacks.set(indexUnique, callbackUpdateSent);

                            threadCallbacks.put(threadId, callbacks);

                            showInfoLog("RECEIVED_SENT_MESSAGE", json);
                        }
                        break;
                    }
                }
            }

            if (!found) {
                ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                ResultMessage resultMessage = new ResultMessage();

                chatResponse.setErrorCode(0);
                chatResponse.setErrorMessage("");
                chatResponse.setHasError(false);
                chatResponse.setUniqueId(chatMessage.getUniqueId());

                resultMessage.setConversationId(chatMessage.getSubjectId());
                resultMessage.setMessageId(Long.valueOf(chatMessage.getContent()));

                chatResponse.setResult(resultMessage);

                String json = gson.toJson(chatResponse);

                listenerManager.callOnSentMessage(json, chatResponse);

                showInfoLog("RECEIVED_SENT_MESSAGE", json);

                setThreadCallbacks(threadId, chatMessage.getUniqueId(), Long.parseLong(chatMessage.getContent()));
            }
        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleSeen(ChatMessage chatMessage, String messageUniqueId, long threadId) {

        if (threadCallbacks.containsKey(threadId)) {
            ArrayList<Callback> callbacks = threadCallbacks.get(threadId);

            for (Callback callback : callbacks) {

                if (messageUniqueId.equals(callback.getUniqueId())) {
                    int indexUnique = callbacks.indexOf(callback);

                    while (indexUnique > -1) {

                        if (callbacks.get(indexUnique).isSeen()) {
                            ResultMessage resultMessage = gson.fromJson(chatMessage.getContent(), ResultMessage.class);
                            resultMessage.setMessageId(callbacks.get(indexUnique).getMessageId());

                            if (callbacks.get(indexUnique).isDelivery()) {

                                ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                                chatResponse.setErrorMessage("");
                                chatResponse.setErrorCode(0);
                                chatResponse.setHasError(false);
                                chatResponse.setUniqueId(callbacks.get(indexUnique).getUniqueId());
                                chatResponse.setResult(resultMessage);

                                String json = gson.toJson(chatResponse);

                                listenerManager.callOnDeliveryMessage(json, chatResponse);

                                Callback callbackUpdateSent = new Callback();
                                callbackUpdateSent.setSent(callback.isSent());
                                callbackUpdateSent.setDelivery(false);
                                callbackUpdateSent.setSeen(callback.isSeen());
                                callbackUpdateSent.setUniqueId(callbacks.get(indexUnique).getUniqueId());
                                callbackUpdateSent.setMessageId(callbacks.get(indexUnique).getMessageId());

                                callbacks.set(indexUnique, callbackUpdateSent);
                                threadCallbacks.put(threadId, callbacks);
                                showInfoLog("RECEIVED_DELIVERED_MESSAGE", json);
                            }

                            ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                            chatResponse.setErrorMessage("");
                            chatResponse.setErrorCode(0);
                            chatResponse.setHasError(false);
                            chatResponse.setUniqueId(callbacks.get(indexUnique).getUniqueId());
                            chatResponse.setResult(resultMessage);

                            String json = gson.toJson(chatResponse);

                            listenerManager.callOnSeenMessage(json, chatResponse);

                            callbacks.remove(indexUnique);

                            threadCallbacks.put(threadId, callbacks);

                            showInfoLog("RECEIVED_SEEN_MESSAGE", json);

                        }
                        indexUnique--;
                    }
                    break;
                }
            }
        }
    }

    private void handleDelivery(ChatMessage chatMessage, String messageUniqueId, long threadId) {
        try {
            if (threadCallbacks.containsKey(threadId)) {
                ArrayList<Callback> callbacks = threadCallbacks.get(threadId);

                for (Callback callback : callbacks) {

                    if (messageUniqueId.equals(callback.getUniqueId())) {
                        int indexUnique = callbacks.indexOf(callback);

                        while (indexUnique > -1) {

                            if (callbacks.get(indexUnique).isDelivery()) {
                                ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                                ResultMessage resultMessage = gson.fromJson(chatMessage.getContent(), ResultMessage.class);
                                resultMessage.setMessageId(callbacks.get(indexUnique).getMessageId());

                                chatResponse.setErrorMessage("");
                                chatResponse.setErrorCode(0);
                                chatResponse.setHasError(false);
                                chatResponse.setUniqueId(callbacks.get(indexUnique).getUniqueId());

                                chatResponse.setResult(resultMessage);
                                String json = gson.toJson(chatResponse);

                                listenerManager.callOnDeliveryMessage(json, chatResponse);

                                Callback callbackUpdateSent = new Callback();
                                callbackUpdateSent.setSent(callback.isSent());
                                callbackUpdateSent.setDelivery(false);
                                callbackUpdateSent.setSeen(callback.isSeen());
                                callbackUpdateSent.setUniqueId(callbacks.get(indexUnique).getUniqueId());
                                callbackUpdateSent.setMessageId(callbacks.get(indexUnique).getMessageId());

                                callbacks.set(indexUnique, callbackUpdateSent);

                                threadCallbacks.put(threadId, callbacks);
                                showInfoLog("RECEIVED_DELIVERED_MESSAGE", json);
                            }
                            indexUnique--;
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
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

    private void handleResponseMessage(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        try {
            if (callback != null) {
                if (callback.getRequestType() >= 1) {
                    switch (callback.getRequestType()) {

                        case ChatMessageType.GET_HISTORY:

                            handleOutPutGetHistory(callback, chatMessage, messageUniqueId);
                            break;
                        case ChatMessageType.GET_CONTACTS:

                            handleGetContact(callback, chatMessage, messageUniqueId);
                            break;
                        case ChatMessageType.UPDATE_THREAD_INFO:

                            handleUpdateThreadInfo(chatMessage, messageUniqueId, callback);
                            break;
                        case ChatMessageType.GET_THREADS:
                            if (callback.isResult()) {
                                handleGetThreads(callback, chatMessage, messageUniqueId);
                            }
                            break;
                        case ChatMessageType.INVITATION:
                            if (callback.isResult()) {
                                handleCreateThread(callback, chatMessage, messageUniqueId);
                            }
                            break;
                        case ChatMessageType.MUTE_THREAD:

                            if (callback.isResult()) {
                                ChatResponse<ResultMute> chatResponse = new ChatResponse<>();
                                String muteThreadJson = reformatMuteThread(chatMessage, chatResponse);
                                listenerManager.callOnMuteThread(muteThreadJson, chatResponse);
                                messageCallbacks.remove(messageUniqueId);
                                showInfoLog("RECEIVE_MUTE_THREAD", muteThreadJson);
                            }
                            break;
                        case ChatMessageType.UN_MUTE_THREAD:

                            if (callback.isResult()) {
                                ChatResponse<ResultMute> chatResponse = new ChatResponse<>();
                                String unmuteThreadJson = reformatMuteThread(chatMessage, chatResponse);
                                listenerManager.callOnUnmuteThread(unmuteThreadJson, chatResponse);
                                messageCallbacks.remove(messageUniqueId);
                                showInfoLog("RECEIVE_UN_MUTE_THREAD", unmuteThreadJson);

                            }
                            break;
                        case ChatMessageType.EDIT_MESSAGE:

                            if (callback.isResult()) {
                                handleEditMessage(chatMessage, messageUniqueId);
                            }

                            break;
                        case ChatMessageType.USER_INFO:

                            handleOnGetUserInfo(chatMessage, messageUniqueId, callback);
                            break;
                        case ChatMessageType.THREAD_PARTICIPANTS:

                            if (callback.isResult()) {
                                ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(callback, chatMessage);

                                String jsonParticipant = gson.toJson(chatResponse);

                                listenerManager.callOnGetThreadParticipant(jsonParticipant, chatResponse);
                                messageCallbacks.remove(messageUniqueId);

                                showInfoLog("RECEIVE_PARTICIPANT", jsonParticipant);
                            }

                            break;
                        case ChatMessageType.ADD_PARTICIPANT:
                            if (callback.isResult()) {
                                handleAddParticipant(chatMessage, messageUniqueId);
                            }

                            break;
                        case ChatMessageType.REMOVE_PARTICIPANT:
                            if (callback.isResult()) {
                                handleOutPutRemoveParticipant(callback, chatMessage, messageUniqueId);
                            }

                            break;
                        case ChatMessageType.LEAVE_THREAD:
                            if (callback.isResult()) {
                                handleOutPutLeaveThread(callback, chatMessage, messageUniqueId);
                            }

                            break;
                        case ChatMessageType.RENAME:

                            break;
                        case ChatMessageType.DELETE_MESSAGE:
                            handleOutPutDeleteMsg(chatMessage);

                            break;
                        case ChatMessageType.BLOCK:
                            if (callback.isResult()) {
                                handleOutPutBlock(chatMessage, messageUniqueId);
                            }

                            break;
                        case ChatMessageType.UNBLOCK:
                            if (callback.isResult()) {
                                handleUnBlock(chatMessage, messageUniqueId);
                            }

                            break;
                        case ChatMessageType.GET_BLOCKED:
                            if (callback.isResult()) {
                                handleOutPutGetBlockList(chatMessage);
                            }

                            break;
                        case ChatMessageType.DELIVERED_MESSAGE_LIST:
                            if (callback.isResult()) {
                                handleOutPutDeliveredMessageList(chatMessage, callback);
                            }
                            break;
                        case ChatMessageType.SEEN_MESSAGE_LIST:
                            if (callback.isResult()) {
                                handleOutPutSeenMessageList(chatMessage, callback);
                            }
                            break;
                    }
                }
            }
        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleEditMessage(ChatMessage chatMessage, String messageUniqueId) {
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
        messageCallbacks.remove(messageUniqueId);
    }

    private void handleOutPutSeenMessageList(ChatMessage chatMessage, Callback callback) {
        try {
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());

            ResultParticipant resultParticipant = new ResultParticipant();

            List<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
            }.getType());
            resultParticipant.setParticipants(participants);
            resultParticipant.setContentCount(chatMessage.getContentCount());

            resultParticipant.setNextOffset(callback.getOffset() + participants.size());
            resultParticipant.setContentCount(chatMessage.getContentCount());
            if (participants.size() + callback.getOffset() < chatMessage.getContentCount()) {
                resultParticipant.setHasNext(true);
            } else {
                resultParticipant.setHasNext(false);
            }

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
    private void getHistoryMain(History history, long threadId, ChatHandler handler, String uniqueId) {
        long offsets = history.getOffset();
        long firstMessageId = history.getFirstMessageId();
        long lastMessageId = history.getLastMessageId();
        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();
        long id = history.getId();

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

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(jObj.toString());
        chatMessage.setType(ChatMessageType.GET_HISTORY);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
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
        String order;
        if (Util.isNullOrEmpty(history.getOrder())) {
            order = "asc";
        } else {
            order = history.getOrder();
        }

        setCallBacks(firstMessageId, lastMessageId, order, history.getCount(), history.getOffset(), uniqueId, id, true, query);
        if (handler != null) {
            handler.onGetHistory(uniqueId);
        }

        sendAsyncMessage(asyncContent, 3, "SEND GET THREAD HISTORY");
    }

    private void handleOutPutDeliveredMessageList(ChatMessage chatMessage, Callback callback) {
        try {
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());

            ResultParticipant resultParticipant = new ResultParticipant();

            List<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
            }.getType());
            resultParticipant.setParticipants(participants);
            resultParticipant.setContentCount(chatMessage.getContentCount());

            resultParticipant.setNextOffset(callback.getOffset() + participants.size());
            resultParticipant.setContentCount(chatMessage.getContentCount());
            if (participants.size() + callback.getOffset() < chatMessage.getContentCount()) {
                resultParticipant.setHasNext(true);
            } else {
                resultParticipant.setHasNext(false);
            }

            chatResponse.setResult(resultParticipant);
            String content = gson.toJson(chatResponse);
            listenerManager.callOnDeliveredMessageList(content, chatResponse);
            showInfoLog("RECEIVE_DELIVERED_MESSAGE_LIST", content);

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleGetContact(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultContact> chatResponse = reformatGetContactResponse(chatMessage, callback);
        String contactJson = gson.toJson(chatResponse);

        listenerManager.callOnGetContacts(contactJson, chatResponse);
        messageCallbacks.remove(messageUniqueId);

        showInfoLog("RECEIVE_GET_CONTACT", contactJson);

    }

    private void handleCreateThread(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultThread> chatResponse = reformatCreateThread(chatMessage);

        String inviteJson = gson.toJson(chatResponse);

        listenerManager.callOnCreateThread(inviteJson, chatResponse);
        messageCallbacks.remove(messageUniqueId);

        showInfoLog("RECEIVE_CREATE_THREAD", inviteJson);

    }

    private void handleGetThreads(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultThreads> chatResponse = reformatGetThreadsResponse(chatMessage, callback);
        String threadJson = gson.toJson(chatResponse);

        listenerManager.callOnGetThread(threadJson, chatResponse);
        messageCallbacks.remove(messageUniqueId);

        showInfoLog("RECEIVE_GET_THREAD", threadJson);

    }

    private void handleUpdateThreadInfo(ChatMessage chatMessage, String messageUniqueId, Callback callback) {

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        if (callback.isResult()) {

            podChat.mainmodel.Thread thread = gson.fromJson(chatMessage.getContent(), podChat.mainmodel.Thread.class);

            ResultThread resultThread = new ResultThread();

            resultThread.setThread(thread);
            chatResponse.setErrorCode(0);
            chatResponse.setErrorMessage("");
            chatResponse.setHasError(false);
            chatResponse.setUniqueId(chatMessage.getUniqueId());
            chatResponse.setResult(resultThread);

            String threadJson = gson.toJson(chatResponse);
            messageCallbacks.remove(messageUniqueId);

            listenerManager.callOnUpdateThreadInfo(threadJson, chatResponse);
            showInfoLog("RECEIVE_UPDATE_THREAD_INFO", threadJson);
        }
    }


    private void handleOnGetUserInfo(ChatMessage chatMessage, String messageUniqueId, Callback callback) {

        if (callback.isResult()) {
            GetInfoExecutor.stopThread();

            userInfoResponse = true;
            ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();

            UserInfo userInfo = gson.fromJson(chatMessage.getContent(), UserInfo.class);
            String userInfoJson = reformatUserInfo(chatMessage, chatResponse, userInfo);

            showInfoLog("RECEIVE_USER_INFO", userInfoJson);

            chatReady = true;

            listenerManager.callOnUserInfo(userInfoJson, chatResponse);
            messageCallbacks.remove(messageUniqueId);

            pingWithDelay();
        }
    }


    private void retryOnGetUserInfo() {

        GetInfoExecutor.getInstance().
                scheduleAtFixedRate(() -> checkForGetUserInfo(), 0,
                        10000, TimeUnit.MILLISECONDS);
    }

    private void checkForGetUserInfo() {
        if (userInfoResponse) {
            GetInfoExecutor.stopThread();
            userInfoResponse = false;
            retryStepUserInfo = 1;

        } else {
            if (retryStepUserInfo < 32) {
                retryStepUserInfo *= 2;

                getUserInfo(null);

                showInfoLog("getUserInfo " + " retry in " + retryStepUserInfo + " s ", "");
            } else {
                GetInfoExecutor.stopThread();

                getErrorOutPut(ChatConstant.ERROR_CANT_GET_USER_INFO, ChatConstant.ERROR_CODE_CANT_GET_USER_INFO, null);
            }
        }
    }

    private void handleOutPutLeaveThread(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

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

        if (callback != null) {
            messageCallbacks.remove(messageUniqueId);
        }

        showInfoLog("RECEIVE_LEAVE_THREAD", jsonThread);
    }

    private void handleAddParticipant(ChatMessage chatMessage, String messageUniqueId) {
        podChat.mainmodel.Thread thread = gson.fromJson(chatMessage.getContent(), podChat.mainmodel.Thread.class);


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

        messageCallbacks.remove(messageUniqueId);

        showInfoLog("RECEIVE_ADD_PARTICIPANT", jsonAddParticipant);
    }

    private void handleOutPutDeleteMsg(ChatMessage chatMessage) {

        ChatResponse<ResultDeleteMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        long messageId = Long.valueOf(chatMessage.getContent());

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
        messageCallbacks.remove(messageUniqueId);
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
        chatMessage.setTokenIssuer("1");
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

    private void handleSetRule(ChatMessage chatMessage) {
        ChatResponse<ResultSetAdmin> chatResponse = new ChatResponse<>();
        ResultSetAdmin resultSetAdmin = new ResultSetAdmin();
        ArrayList<Admin> admins;
        admins = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
        }.getType());
        resultSetAdmin.setAdmins(admins);
        chatResponse.setResult(resultSetAdmin);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String responseJson = gson.toJson(chatResponse);
        listenerManager.callonSetRuleToUser(responseJson, chatResponse);
        showInfoLog("RECEIVE_SET_RULE", responseJson);
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
        messageCallbacks.remove(messageUniqueId);
    }

    private void handleOutPutGetBlockList(ChatMessage chatMessage) {
        ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultBlockList resultBlockList = new ResultBlockList();

        List<Contact> contacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());
        resultBlockList.setContacts(contacts);
        chatResponse.setResult(resultBlockList);
        String jsonGetBlock = gson.toJson(chatResponse);
        listenerManager.callOnGetBlockList(jsonGetBlock, chatResponse);
        showInfoLog("RECEIVE_GET_BLOCK_LIST", jsonGetBlock);
    }

    private void handleOutPutRemoveParticipant(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(callback, chatMessage);

        String jsonRmParticipant = gson.toJson(chatResponse);

        listenerManager.callOnThreadRemoveParticipant(jsonRmParticipant, chatResponse);
        messageCallbacks.remove(messageUniqueId);

        showInfoLog("RECEIVE_REMOVE_PARTICIPANT", jsonRmParticipant);
    }

    private void handleOutPutGetHistory(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        List<MessageVO> messageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<MessageVO>>() {
        }.getType());

        ResultHistory resultHistory = new ResultHistory();

        ChatResponse<ResultHistory> chatResponse = new ChatResponse<>();

        resultHistory.setNextOffset(callback.getOffset() + messageVOS.size());
        resultHistory.setContentCount(chatMessage.getContentCount());
        if (messageVOS.size() + callback.getOffset() < chatMessage.getContentCount()) {
            resultHistory.setHasNext(true);
        } else {
            resultHistory.setHasNext(false);
        }

        resultHistory.setHistory(messageVOS);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setResult(resultHistory);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String json = gson.toJson(chatResponse);

        listenerManager.callOnGetThreadHistory(json, chatResponse);

        showInfoLog("RECEIVE_GET_HISTORY", json);

        messageCallbacks.remove(messageUniqueId);
    }

    private String getContactMain(Integer count, Long offset, boolean syncContact, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        count = count != null && count > 0 ? count : 50;
        offset = offset != null && offset >= 0 ? offset : 0;

        if (chatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setOffset(offset);

            JsonObject jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
            jObj.remove("lastMessageId");
            jObj.remove("firstMessageId");

            jObj.remove("count");
            jObj.addProperty("size", count);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(jObj.toString());
            chatMessage.setType(ChatMessageType.GET_CONTACTS);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            if (syncContact) {
                setCallBacks(null, null, null, false, ChatMessageType.GET_CONTACTS, offset, uniqueId);
            } else {
                setCallBacks(null, null, null, true, ChatMessageType.GET_CONTACTS, offset, uniqueId);
            }
            sendAsyncMessage(asyncContent, 3, "GET_CONTACT_SEND");
            if (handler != null) {
                handler.onGetContact(uniqueId);
            }
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
        if (Util.isNullOrEmpty(typeCode)) {
            typeCode = "default";
        }
        return typeCode;
    }


    private int getExpireAmount() {
        if (Util.isNullOrEmpty(expireAmount)) {
            expireAmount = 2 * 24 * 60 * 60;
        }
        return expireAmount;
    }

    /**
     * The replacement method is getMessageDeliveredList.
     */
    private String deliveredMessageList(RequestDeliveredMessageList requestParams) {

        String uniqueId;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                if (Util.isNullOrEmpty(requestParams.getCount())) {
                    requestParams.setCount(50);
                }

                JsonObject object = (JsonObject) gson.toJsonTree(requestParams);
                object.remove("typeCode");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setType(ChatMessageType.DELIVERED_MESSAGE_LIST);

                chatMessage.setContent(object.toString());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(requestParams.getTypeCode())) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.remove("typeCode");
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", requestParams.getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, ChatMessageType.DELIVERED_MESSAGE_LIST, requestParams.getOffset(), uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_DELIVERED_MESSAGE_LIST");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    //Get the list of the participants that saw the specific message
    /*
     * The replacement method is getMessageSeenList.
     * */
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
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setContent(object.toString());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(requestParams.getTypeCode())) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.remove("typeCode");
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", requestParams.getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, ChatMessageType.SEEN_MESSAGE_LIST, requestParams.getOffset(), uniqueId);

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
    private ChatResponse<ResultParticipant> reformatThreadParticipants(Callback callback, ChatMessage chatMessage) {

        ArrayList<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
        }.getType());


        ChatResponse<ResultParticipant> outPutParticipant = new ChatResponse<>();
        outPutParticipant.setErrorCode(0);
        outPutParticipant.setErrorMessage("");
        outPutParticipant.setHasError(false);
        outPutParticipant.setUniqueId(chatMessage.getUniqueId());

        ResultParticipant resultParticipant = new ResultParticipant();

        resultParticipant.setContentCount(chatMessage.getContentCount());

        if (callback != null) {
            if (participants.size() + callback.getOffset() < chatMessage.getContentCount()) {
                resultParticipant.setHasNext(true);
            } else {
                resultParticipant.setHasNext(false);
            }
            resultParticipant.setNextOffset(callback.getOffset() + participants.size());
        }

        resultParticipant.setParticipants(participants);
        outPutParticipant.setResult(resultParticipant);
        return outPutParticipant;
    }


    private void setThreadCallbacks(long threadId, String uniqueId, long messageId) {
        try {
            if (chatReady) {
                Callback callback = new Callback();
                callback.setDelivery(true);
                callback.setSeen(true);
                callback.setSent(true);
                callback.setUniqueId(uniqueId);
                callback.setMessageId(messageId);

                ArrayList<Callback> callbackList = threadCallbacks.get(threadId);
                if (!Util.isNullOrEmpty(callbackList)) {
                    callbackList.add(callback);
                    threadCallbacks.put(threadId, callbackList);
                } else {
                    ArrayList<Callback> callbacks = new ArrayList<>();
                    callbacks.add(callback);
                    threadCallbacks.put(threadId, callbacks);
                }
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void setCallBacks(Boolean delivery, Boolean sent, Boolean seen, Boolean result, int requestType, Long offset, String uniqueId) {
        try {
            if (chatReady || asyncReady) {
                delivery = delivery != null ? delivery : false;
                sent = sent != null ? sent : false;
                seen = seen != null ? seen : false;
                result = result != null ? result : false;
                offset = offset != null ? offset : 0;

                Callback callback = new Callback();

                callback.setDelivery(delivery);
                callback.setOffset(offset);
                callback.setSeen(seen);
                callback.setSent(sent);
                callback.setRequestType(requestType);
                callback.setResult(result);

                messageCallbacks.put(uniqueId, callback);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }


    private void setCallBacks(long firstMessageId, long lastMessageId, String order, long count
            , Long offset, String uniqueId, long msgId, boolean messageCriteriaVO, String query) {

        try {
            if (chatReady || asyncReady) {

                offset = offset != null ? offset : 0;

                Callback callback = new Callback();
                callback.setFirstMessageId(firstMessageId);
                callback.setLastMessageId(lastMessageId);
                callback.setOffset(offset);
                callback.setCount(count);
                callback.setOrder(order);
                callback.setMessageId(msgId);
                callback.setResult(true);
                callback.setQuery(query);
                callback.setMetadataCriteria(messageCriteriaVO);
                callback.setRequestType(ChatMessageType.GET_HISTORY);

                messageCallbacks.put(uniqueId, callback);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
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

            pingWithDelay();

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
        chatMessage.setTokenIssuer("1");

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
        message.setTokenIssuer("1");
        message.setToken(getToken());
        message.setUniqueId(generateUniqueId());
        //message.setTime(1000);

        return message;
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

    private String reformatMuteThread(ChatMessage chatMessage, ChatResponse<ResultMute> outPut) {
        ResultMute resultMute = new ResultMute();
        resultMute.setThreadId(Long.valueOf(chatMessage.getContent()));
        outPut.setResult(resultMute);
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

        podChat.mainmodel.Thread thread = gson.fromJson(chatMessage.getContent(), podChat.mainmodel.Thread.class);
        resultThread.setThread(thread);
        chatResponse.setResult(resultThread);

        resultThread.setThread(thread);
        return chatResponse;
    }

    /**
     * Reformat the get thread response
     */
    private ChatResponse<ResultThreads> reformatGetThreadsResponse(ChatMessage chatMessage, Callback callback) {
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

        if (callback != null) {

            if (threads.size() + callback.getOffset() < chatMessage.getContentCount()) {
                resultThreads.setHasNext(true);
            } else {
                resultThreads.setHasNext(false);
            }

            resultThreads.setNextOffset(callback.getOffset() + threads.size());
        }

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

    /**
     * It Removes messages from wait queue after the check-in of their existence.
     */
    private void handleRemoveFromWaitQueue(ChatMessage chatMessage) {

        try {
            List<MessageVO> messageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<MessageVO>>() {
            }.getType());

        } catch (Throwable throwable) {
            showErrorLog(throwable.getMessage());
        }
    }

    private ChatResponse<ResultContact> reformatGetContactResponse(ChatMessage chatMessage, Callback callback) {
        ResultContact resultContact = new ResultContact();

        ChatResponse<ResultContact> outPutContact = new ChatResponse<>();

        ArrayList<Contact> contacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());

        resultContact.setContacts(contacts);
        resultContact.setContentCount(chatMessage.getContentCount());

        if (contacts.size() + callback.getOffset() < chatMessage.getContentCount()) {
            resultContact.setHasNext(true);
        } else {
            resultContact.setHasNext(false);
        }

        resultContact.setNextOffset(callback.getOffset() + contacts.size());
        resultContact.setContentCount(chatMessage.getContentCount());

        outPutContact.setResult(resultContact);
        outPutContact.setErrorMessage("");
        outPutContact.setUniqueId(chatMessage.getUniqueId());

        return outPutContact;
    }

    public String getContentType(File file) throws IOException {
        return Files.probeContentType(file.toPath());
    }

    private String createFileMetadata(File file, String hashCode, long fileId, String mimeType, long fileSize, String filePath) {
        MetaDataFile metaDataFile = new MetaDataFile();
        FileMetaDataContent metaDataContent = new FileMetaDataContent();

        metaDataContent.setId(fileId);
        metaDataContent.setName(file.getName());
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
                mapLocation.setLatitude(Double.valueOf(latitude));
                mapLocation.setLongitude(Double.valueOf(longitude));
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

    private static synchronized String generateUniqueId() {
        return UUID.randomUUID().toString();
    }


    private String getToken() {
        return token;
    }

    private long getUserId() {
        return userId;
    }

    private String getSsoHost() {
        return ssoHost;
    }

    private void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }

    private void setUserId(long userId) {
        this.userId = userId;
    }

    private void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    private String getPlatformHost() {
        return platformHost;
    }

    private void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    private String getFileServer() {
        return fileServer;
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

    public void setToken(String token) {
        this.token = token;

//        if (asyncReady) {
//            PingExecutor.getInstance().schedule(() -> checkForSetToken()
//                    , retrySetToken * 1000, TimeUnit.MILLISECONDS);
//
//        }
    }


}