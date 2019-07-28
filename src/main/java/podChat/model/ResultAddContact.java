package podChat.model;


import podChat.mainmodel.Contact;

public class ResultAddContact {

    private podChat.mainmodel.Contact Contact;
    private long contentCount;

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public Contact getContact() {
        return Contact;
    }

    public void setContact(Contact contact) {
        Contact = contact;
    }
}
