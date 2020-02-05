package podChat.requestobject;


import podChat.util.Util;

public class RequestAddContact extends GeneralRequestObject {

    private String firstName;
    private String lastName;
    private String cellphoneNumber;
    private String email;
    private String userName;


    RequestAddContact(Builder builder) {
        super(builder);
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.cellphoneNumber = builder.cellphoneNumber;
        this.email = builder.email;
        this.userName = builder.userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String firstName;
        private String lastName;
        private String cellphoneNumber;
        private String email;
        private String userName;


        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder cellphoneNumber(String cellphoneNumber) {
            this.cellphoneNumber = cellphoneNumber;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }


        public RequestAddContact build() {

            if (Util.isNullOrEmpty(this.firstName)) {
                this.firstName = "";
            }
            if (Util.isNullOrEmpty(this.lastName)) {
                this.lastName = "";
            }
            if (Util.isNullOrEmpty(this.email)) {
                this.email = "";
            }
            if (Util.isNullOrEmpty(this.cellphoneNumber)) {
                this.cellphoneNumber = "";
            }
            if (Util.isNullOrEmpty(this.userName)) {
                this.userName = "";
            }

            return new RequestAddContact(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
