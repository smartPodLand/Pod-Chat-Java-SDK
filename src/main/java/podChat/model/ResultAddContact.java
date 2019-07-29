package podChat.model;

import podChat.mainmodel.Contact;

public class ResultAddContact {

    private Contact contact;
    private long contentCount;

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
