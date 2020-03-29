package podChat.chat;


import podChat.mainmodel.ResultDeleteMessage;
import podChat.model.*;

import java.util.ArrayList;
import java.util.List;

public class ChatListenerManager {
    private final List<ChatListener> mListeners = new ArrayList<>();
    private boolean mSyncNeeded = true;
    private List<ChatListener> mCopiedListeners;

    public ChatListenerManager() {
    }

    public void addListener(ChatListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            mListeners.add(listener);
            mSyncNeeded = true;
        }
    }

    public void addListeners(List<ChatListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (ChatListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                mListeners.add(listener);
                mSyncNeeded = true;
            }
        }
    }

    public void removeListener(ChatListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            if (mListeners.remove(listener)) {
                mSyncNeeded = true;
            }
        }
    }

    public void removeListeners(List<ChatListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (ChatListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                if (mListeners.remove(listener)) {
                    mSyncNeeded = true;
                }
            }
        }
    }

    public void clearListeners() {
        synchronized (mListeners) {
            if (mListeners.size() == 0) {
                return;
            }

            mListeners.clear();
            mSyncNeeded = true;
        }
    }

    private List<ChatListener> getSynchronizedListeners() {
        synchronized (mListeners) {
            if (!mSyncNeeded) {
                return mCopiedListeners;
            }

            // Copy mListeners to copiedListeners.
            List<ChatListener> copiedListeners = new ArrayList<>(mListeners.size());

            for (ChatListener listener : mListeners) {
                copiedListeners.add(listener);
            }

            // Synchronize.
            mCopiedListeners = copiedListeners;
            mSyncNeeded = false;

            return copiedListeners;
        }
    }

    public void callOnGetThread(ChatResponse<ResultThreads> thread) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetThread(thread);

            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetThreadHistory(ChatResponse<ResultHistory> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetHistory(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetContacts(ChatResponse<ResultContact> outPutContact) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetContacts(outPutContact);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSentMessage(ChatResponse<ResultMessage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSent(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSeenMessage(ChatResponse<ResultMessage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSeen(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeliveryMessage(ChatResponse<ResultMessage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onDeliver(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnError(ErrorOutPut errorOutPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onError(errorOutPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetThreadParticipant(ChatResponse<ResultParticipant> outPutParticipant) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetThreadParticipant(outPutParticipant);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnClearHistory(ChatResponse<ResultClearHistory> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnClearHistory(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnGetThreadAdmin(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnGetThreadAdmin(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    private void callHandleCallbackError(ChatListener listener, Throwable cause) {
        try {
            listener.handleCallbackError(cause);
        } catch (Throwable t) {
        }
    }

    public void callOnEditedMessage(ChatResponse<ResultNewMessage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onEditedMessage(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnAddContact(ChatResponse<ResultAddContact> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onContactAdded(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemoveContact(ChatResponse<ResultRemoveContact> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRemoveContact(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnMuteThread(ChatResponse<ResultMute> outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onMuteThread(outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnmuteThread(ChatResponse<ResultMute> outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnmuteThread(outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnPinThread(ChatResponse<ResultPinThread> outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onPinThread(outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnPinThread(ChatResponse<ResultPinThread> outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnPinThread(outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnPinMessage(ChatResponse<ResultPinMessage> outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onPinMessage(outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnPinMessage(ChatResponse<ResultPinMessage> outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnPinMessage(outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCreateThread(ChatResponse<ResultThread> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCreateThread(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUpdateContact(ChatResponse<ResultUpdateContact> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUpdateContact(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnNewMessage(ChatResponse<ResultNewMessage> outPutNewMessage) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onNewMessage(outPutNewMessage);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUploadImageFile(ChatResponse<ResultImageFile> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUploadImageFile(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUploadFile(ChatResponse<ResultFile> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUploadFile(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnThreadAddParticipant(ChatResponse<ResultAddParticipant> outPutAddParticipant) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadAddParticipant(outPutAddParticipant);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadRemoveParticipant(ChatResponse<ResultParticipant> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadRemoveParticipant(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadLeaveParticipant(ChatResponse<ResultLeaveThread> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadLeaveParticipant(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeleteMessage(ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onDeleteMessage(outPutDeleteMessage);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadInfoUpdated(ChatResponse<ResultThread> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadInfoUpdated(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnLastSeenUpdated(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onLastSeenUpdated(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnChatState(String state) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onChatState(state);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnBlock(ChatResponse<ResultBlock> outPutBlock) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onBlock(outPutBlock);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnBlock(ChatResponse<ResultBlock> outPutBlock) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnBlock(outPutBlock);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetBlockList(ChatResponse<ResultBlockList> outPutBlockList) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetBlockList(outPutBlockList);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSearchContact(ChatResponse<ResultContact> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSearchContact(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemovedFromThread(ChatResponse<ResultThread> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnRemovedFromThread(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUpdateThreadInfo(ChatResponse<ResultThread> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUpdateThreadInfo(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeliveredMessageList(ChatResponse<ResultParticipant> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnDeliveredMessageList(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSeenMessageList(ChatResponse<ResultParticipant> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnSeenMessageList(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnLogEvent(String logEvent) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnLogEvent(logEvent);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSetRoleToUser(ChatResponse<ResultSetRole> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnSetRole(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemoveRoleFromUser(ChatResponse<ResultSetRole> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnRemoveRole(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnInteractMessage(ChatResponse<ResultInteractMessage> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnInteractMessage(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUserInfo(ChatResponse<ResultUserInfo> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUserInfo(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetCurrentUserRoles(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUpdateProfile(ChatResponse<ResultUpdateProfile> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUpdateProfile(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnIsNameAvailable(ChatResponse<ResultIsNameAvailable> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onIsNameAvailable(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnJoinThread(ChatResponse<ResultThread> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onJoinThread(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCountUnreadMessage(ChatResponse<ResultUnreadMessageCount> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCountUnreadMessage(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


}
