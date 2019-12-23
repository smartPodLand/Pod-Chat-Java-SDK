package podChat.mainmodel;

import java.util.List;

public class ParticipantVO {
    private Long id;
    private Long coreUserId;
    private Boolean sendEnable;
    private Boolean receiveEnable;
    private String firstName;
    private String lastName;
    private String name;
    private String cellphoneNumber;
    private String email;
    private String image;
    private Boolean myFriend;
    private Boolean online;
    private Long notSeenDuration;
    private Long contactId;
    private String contactName;
    private String contactFirstName;
    private String contactLastName;
    private Boolean blocked;
    private Boolean admin;
    private Boolean auditor;
    private String keyId ;
    private List<String> roles;
    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoreUserId() {
        return coreUserId;
    }

    public void setCoreUserId(Long coreUserId) {
        this.coreUserId = coreUserId;
    }

    public Boolean getSendEnable() {
        return sendEnable;
    }

    public void setSendEnable(Boolean sendEnable) {
        this.sendEnable = sendEnable;
    }

    public Boolean getReceiveEnable() {
        return receiveEnable;
    }

    public void setReceiveEnable(Boolean receiveEnable) {
        this.receiveEnable = receiveEnable;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getMyFriend() {
        return myFriend;
    }

    public void setMyFriend(Boolean myFriend) {
        this.myFriend = myFriend;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Long getNotSeenDuration() {
        return notSeenDuration;
    }

    public void setNotSeenDuration(Long notSeenDuration) {
        this.notSeenDuration = notSeenDuration;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getAuditor() {
        return auditor;
    }

    public void setAuditor(Boolean auditor) {
        this.auditor = auditor;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
