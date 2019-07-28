package podAsync.model;


public class AsyncMessageType {

    public static final int PING = 0;
    public static final int SERVER_REGISTER = 1;
    public static final int DEVICE_REGISTER = 2;
    public static final int MESSAGE = 3;
    public static final int MESSAGE_ACK_NEEDED = 4;
    public static final int MESSAGE_SENDER_ACK_NEEDED = 5;
    public static final int ACK = 6;
    public static final int PEER_REMOVED = -3;
    public static final int ERROR_MESSAGE = -99;
}
