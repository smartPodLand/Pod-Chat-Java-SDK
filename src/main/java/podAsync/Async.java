package podAsync;

import com.google.gson.Gson;
import config.MainConfig;
import config.QueueConfig;
import exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.model.*;
import podChat.chat.Chat;
import podChat.util.ChatStateType;
import util.ActiveMq;
import util.IoAdapter;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public class Async implements IoAdapter {
    private static Logger logger = LogManager.getLogger(Async.class);

    private static ActiveMq activeMq;
    private static final String TAG = "Async" + " ";
    private static Async instance;
    private boolean isServerRegister;
    private boolean isDeviceRegister;
    private MessageWrapperVo messageWrapperVo;
    private static AsyncListenerManager asyncListenerManager;
    private static Gson gson;
    private String errorMessage;
    private String message;
    private String state;
    private String appId;
    private String peerId;
    private String deviceID;
    private String serverAddress;
    private String token;
    private String serverName;
    private String ssoHost;

    private Async() {

    }

    public static Async getInstance() {
        if (instance == null) {
            gson = new Gson();
            instance = new Async();
            asyncListenerManager = new AsyncListenerManager();

            QueueConfig queueConfig = new QueueConfig();
            queueConfig.setConfig();

            MainConfig mainConfig = new MainConfig();
            mainConfig.setConfig();
        }
        return instance;
    }

    /**
     * @param textMessage that received when socket send message to Async
     */

    @Override
    public void onReceiveMessage(String textMessage) throws IOException {
        int type = 0;

        ClientMessage clientMessage = gson.fromJson(textMessage, ClientMessage.class);

        if (clientMessage != null) {
            type = clientMessage.getType();
        }

        switch (type) {
            case AsyncMessageType.ACK:
                handleOnAck(clientMessage);
                break;
            case AsyncMessageType.DEVICE_REGISTER:
                handleOnDeviceRegister(activeMq, clientMessage);
                break;
            case AsyncMessageType.ERROR_MESSAGE:
                handleOnErrorMessage(clientMessage);
                break;
            case AsyncMessageType.MESSAGE_ACK_NEEDED:

                handleOnMessageAckNeeded(activeMq, clientMessage);
                break;
            case AsyncMessageType.MESSAGE_SENDER_ACK_NEEDED:
                handleOnMessageAckNeeded(activeMq, clientMessage);
                break;
            case AsyncMessageType.MESSAGE:
                handleOnMessage(clientMessage);
                break;
            case AsyncMessageType.PEER_REMOVED:
                break;
            case AsyncMessageType.PING:
                handleOnPing(activeMq, clientMessage);
                break;
            case AsyncMessageType.SERVER_REGISTER:
                handleOnServerRegister(textMessage);
                break;
        }
    }


    @Override
    public void onSendError() {

    }

    @Override
    public void onReceiveError() {

    }

    @Override
    public void onSessionCloseError() {
        asyncListenerManager.callOnDisconnected("session closed...");
    }


    public void connect(String socketServerAddress, final String appId, String serverName, String token, String ssoHost, String deviceID) throws ConnectionException {
        try {

            //  setAppId(appId);
            setServerAddress(socketServerAddress);
            setToken(token);
            setServerName(serverName);
            setSsoHost(ssoHost);

            activeMq = new ActiveMq(this);

            setState(AsyncConstant.ASYNC_STATE_OPEN);

            asyncListenerManager.callOnStateChanged(ChatStateType.OPEN);
            asyncListenerManager.callOnStateChanged(ChatStateType.ASYNC_READY);

        } catch (ConnectionException e) {
            throw e;
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());

        }
    }

    /**
     * @Param textContent
     * @Param messageType it could be 3, 4, 5
     * @Param []receiversId the Id's that we want to send
     */
    public void sendMessage(String textContent, int messageType, long[] receiversId) {
        try {
            // if (getState().equals("OPEN")) {
            Message message = new Message();
            message.setContent(textContent);
            message.setReceivers(receiversId);

            String jsonMessage = gson.toJson(message);
            String wrapperJsonString = getMessageWrapper(gson, jsonMessage, messageType);

            sendData(activeMq, wrapperJsonString);
            //    }
        } catch (Exception e) {
            asyncListenerManager.callOnError(e.getCause().getMessage());
            showErrorLog("Async: connect", e.getCause().getMessage());
        }
    }


    /**
     * First we checking the state of the socket then we send the message
     */
    public void sendMessage(String textContent, int messageType) {
        try {
            if (getState() != null) {
                if (getState().equals(AsyncConstant.ASYNC_STATE_OPEN)) {
                    long ttl = new Date().getTime();
                    Message message = new Message();
                    message.setContent(textContent);
                    message.setPriority(1);
                    message.setPeerName(getServerName());
                    message.setTtl(ttl);

                    String json = gson.toJson(message);

                    messageWrapperVo = new MessageWrapperVo();
                    messageWrapperVo.setContent(json);
                    messageWrapperVo.setType(messageType);

                    String json1 = gson.toJson(messageWrapperVo);

                    sendData(activeMq, json1);

                } else {
                    asyncListenerManager.callOnError("Socket is close");
                }
            } else {
                showErrorLog(TAG + "Socket Is Not Connected");
            }
        } catch (Exception e) {
            asyncListenerManager.callOnError(e.getCause().getMessage());
            showErrorLog("Async: connect", e.getCause().getMessage());
        }

    }

    /**
     * Add a listener to receive events on this Async.
     *
     * @param listener A listener to add.
     * @return {@code this} object.
     */
    public Async addListener(AsyncListener listener) {
        asyncListenerManager.addListener(listener);
        return this;
    }

    public Async addListeners(List<AsyncListener> listeners) {
        asyncListenerManager.addListeners(listeners);
        return this;
    }

    public Async removeListener(AsyncListener listener) {
        asyncListenerManager.removeListener(listener);
        return this;
    }

    /**
     * Connect webSocket to the Async
     *
     * @Param socketServerAddress
     * @Param appId
     */
    private void handleOnAck(ClientMessage clientMessage) throws IOException {
        setMessage(clientMessage.getContent());
        asyncListenerManager.callOnTextMessage(clientMessage.getContent());
    }

    /**
     * When socket closes by any reason
     * , server is still registered and we sent a lot of message but
     * they are still in the queue
     */
    private void handleOnDeviceRegister(ActiveMq activeMq, ClientMessage clientMessage) {
        try {
            isDeviceRegister = true;

            String peerId = clientMessage.getContent();

            setPeerId(peerId);

            if (!isServerRegister) {
                serverRegister(activeMq);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

    }

    private void serverRegister(ActiveMq activeMq) {
        if (activeMq != null) {
            try {
                RegistrationRequest registrationRequest = new RegistrationRequest();
                registrationRequest.setName(getServerName());

                String jsonRegistrationRequestVo = gson.toJson(registrationRequest);
                String jsonMessageWrapperVo = getMessageWrapper(gson, jsonRegistrationRequestVo, AsyncMessageType.SERVER_REGISTER);

                sendData(activeMq, jsonMessageWrapperVo);

            } catch (Exception e) {
                showErrorLog(TAG + e.getCause().getMessage());
            }
        } else {
            showErrorLog("WebSocket Is Null");
        }
    }

    private void sendData(ActiveMq activeMq, String jsonMessageWrapperVo) {
        if (activeMq != null) {
            try {

                if (jsonMessageWrapperVo != null) {
                    activeMq.sendMessage(jsonMessageWrapperVo);

                } else {
                    showErrorLog(TAG + "message is Null");
                }

            } catch (Exception e) {
                showErrorLog("Async: connect", e.getCause().getMessage());
            }
        }
    }

    private void handleOnErrorMessage(ClientMessage clientMessage) {
        showErrorLog(TAG + "OnErrorMessage", clientMessage.getContent());
        setErrorMessage(clientMessage.getContent());
    }

    private void handleOnMessage(ClientMessage clientMessage) {
        if (clientMessage != null) {
            try {
                setMessage(clientMessage.getContent());

                asyncListenerManager.callOnTextMessage(clientMessage.getContent());

            } catch (Exception e) {
                showErrorLog(e.getCause().getMessage());
            }
        } else {
            showErrorLog("Async: clientMessage Is Null");
        }
    }

    private void handleOnPing(ActiveMq activeMq, ClientMessage clientMessage) {
        try {
            if (clientMessage.getContent() != null) {
                setDeviceID(clientMessage.getContent());
                deviceRegister(activeMq);

            } else {
                showInfoLog("ASYNC_PING_RECEIVED");
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleOnServerRegister(String textMessage) {
        try {
            showInfoLog("SERVER_REGISTERED");
            showInfoLog("ASYNC_IS_READY", textMessage);

            asyncListenerManager.callOnStateChanged(ChatStateType.ASYNC_READY);

            isServerRegister = true;
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleOnMessageAckNeeded(ActiveMq activeMq, ClientMessage clientMessage) {
        try {

            if (activeMq != null) {
                handleOnMessage(clientMessage);

                Message messageSenderAckNeeded = new Message();
                messageSenderAckNeeded.setMessageId(clientMessage.getId());

                String jsonSenderAckNeeded = gson.toJson(messageSenderAckNeeded);
                String jsonSenderAckNeededWrapper = getMessageWrapper(gson, jsonSenderAckNeeded, AsyncMessageType.ACK);

                sendData(activeMq, jsonSenderAckNeededWrapper);

            } else {
                showErrorLog("WebSocket Is Null ");
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void deviceRegister(ActiveMq activeMq) {
        try {
            if (activeMq != null) {
                PeerInfo peerInfo = new PeerInfo();
                if (getPeerId() != null) {
                    peerInfo.setRefresh(true);
                } else {
                    peerInfo.setRenew(true);
                }
                peerInfo.setAppId(getAppId());
                peerInfo.setDeviceId(getDeviceId());

                String peerMessageJson = gson.toJson(peerInfo);
                String jsonPeerInfoWrapper = getMessageWrapper(gson, peerMessageJson, AsyncMessageType.DEVICE_REGISTER);

                sendData(activeMq, jsonPeerInfoWrapper);

                showInfoLog(TAG + "SEND_SERVER_REGISTER");
                showInfoLog(jsonPeerInfoWrapper);

            } else {
                showErrorLog("Queue Is Null ");
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private String getMessageWrapper(Gson gson, String json, int messageType) {
        try {
            messageWrapperVo = new MessageWrapperVo();
            messageWrapperVo.setContent(json);
            messageWrapperVo.setType(messageType);

            return gson.toJson(messageWrapperVo);

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return null;
    }

    private void setServerName(String serverName) {
        this.serverName = serverName;
    }

    private String getServerName() {
        return serverName;
    }

    private String getDeviceId() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getPeerId() {
        return peerId;
    }

    private void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    private String getAppId() {
        return appId;
    }

    private void setAppId(String appId) {
        this.appId = appId;
    }

    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
    }

    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private String getServerAddress() {
        return serverAddress;
    }

    private void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private void setToken(String token) {
        this.token = token;
    }

    private String getToken() {
        return token;
    }

    /**
     * Get the manager that manages registered listeners.
     */
    AsyncListenerManager getListenerManager() {
        return asyncListenerManager;
    }

    private void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }

    private String getSsoHost() {
        return ssoHost;
    }


    private void showInfoLog(String i, String json) {
        if (Chat.isLoggable) logger.info(i + "\n \n" + json);

    }

    private void showInfoLog(String json) {
        if (Chat.isLoggable) logger.info("\n \n" + json);
    }


    private void showErrorLog(String i, String json) {
        if (Chat.isLoggable) logger.error(i + "\n \n" + json);
    }

    private void showErrorLog(String e) {
        if (Chat.isLoggable) logger.error("\n \n" + e);

    }

    private void showErrorLog(Throwable throwable) {
        if (Chat.isLoggable) logger.error("\n \n" + throwable.getMessage());
    }
}

