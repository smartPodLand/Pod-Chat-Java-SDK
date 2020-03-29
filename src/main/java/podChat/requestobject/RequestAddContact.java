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

    public RequestAddContact() {
    }

    public static AddContactStep newBuilder() {

        return new Steps();
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

    public static interface AddContactStep {

        ActionStep firstName(String firstName);

        ActionStep lastName(String lastName);

        ActionStep firstNameLastName(String firstName, String lastName);


    }

    public static interface ActionStep {

        BuildStep phoneNumber(String cellPhoneNumber);

        BuildStep phoneNumberEmail(String cellPhoneNumber, String email);

        BuildStep phoneNumberEmailUserName(String cellPhoneNumber, String email, String username);

        BuildStep phoneNumberUserName(String cellPhoneNumber, String username);

        BuildStep email(String email);

        BuildStep emailUserName(String email, String username);

        BuildStep userName(String userName);


    }

    public static interface BuildStep {


        RequestAddContact build();

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

            return new RequestAddContact(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private static class Steps implements AddContactStep, ActionStep, BuildStep {
        private String firstName;
        private String lastName;
        private String cellphoneNumber;
        private String email;
        private String userName;


        @Override
        public ActionStep firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        @Override
        public ActionStep lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        @Override
        public ActionStep firstNameLastName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            return this;
        }

        @Override
        public BuildStep phoneNumber(String cellphoneNumber) {
            this.cellphoneNumber = cellphoneNumber;
            return this;
        }

        @Override
        public BuildStep phoneNumberEmail(String cellPhoneNumber, String email) {
            this.cellphoneNumber = cellPhoneNumber;
            this.email = email;
            return this;
        }

        @Override
        public BuildStep phoneNumberEmailUserName(String cellPhoneNumber, String email, String username) {
            this.cellphoneNumber = cellPhoneNumber;
            this.email = email;
            this.userName = username;
            return this;
        }

        @Override
        public BuildStep phoneNumberUserName(String cellPhoneNumber, String username) {
            this.cellphoneNumber = cellPhoneNumber;
            this.userName = username;
            return this;
        }

        @Override
        public BuildStep email(String email) {
            this.email = email;
            return this;
        }

        @Override
        public BuildStep emailUserName(String email, String username) {
            this.email = email;
            this.userName = username;
            return this;
        }

        @Override
        public BuildStep userName(String userName) {
            this.userName = userName;
            return this;
        }

        @Override
        public RequestAddContact build() {

            RequestAddContact request = new RequestAddContact();

            request.setUserName(userName != null ? userName : "");
            request.setLastName(lastName != null ? lastName : "");
            request.setFirstName(firstName != null ? firstName : "");
            request.setEmail(email != null ? email : "");
            request.setCellphoneNumber(cellphoneNumber != null ? cellphoneNumber : "");

            return request;
        }
    }
}
