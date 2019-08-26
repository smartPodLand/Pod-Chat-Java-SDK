package podChat.localModel;

import podChat.ProgressHandler;

import java.io.File;

/**
 * Created By Khojasteh on 8/26/2019
 */
public class LFileUpload {
    private String description;
    private long threadId;
    private String filePath;
    private String mimeType;
    private String systemMetaData;
    private String uniqueId;
    private String typeCode;
    private Integer messageType;
    private long messageId;
    private long fileSize;
    private File file;
    private String methodName;
    private String center;
    private ProgressHandler.sendFileMessage handler;
    private int xC;
    private int yC;
    private int hC;
    private int wC;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSystemMetaData() {
        return systemMetaData;
    }

    public void setSystemMetaData(String systemMetaData) {
        this.systemMetaData = systemMetaData;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ProgressHandler.sendFileMessage getHandler() {
        return handler;
    }

    public void setHandler(ProgressHandler.sendFileMessage handler) {
        this.handler = handler;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public int getxC() {
        return xC;
    }

    public LFileUpload setxC(int xC) {
        this.xC = xC;
        return this;
    }

    public int getyC() {
        return yC;
    }

    public LFileUpload setyC(int yC) {
        this.yC = yC;
        return this;
    }

    public int gethC() {
        return hC;
    }

    public LFileUpload sethC(int hC) {
        this.hC = hC;
        return this;
    }

    public int getwC() {
        return wC;
    }

    public LFileUpload setwC(int wC) {
        this.wC = wC;
        return this;
    }
}
