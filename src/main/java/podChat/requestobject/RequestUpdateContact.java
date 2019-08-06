package podChat.requestobject;


public class RequestUpdateContact extends GeneralRequestObject {

    private String firstName;
    private String lastName;
    private String cellphoneNumber;
    private String email;
    private long userId;

    private RequestUpdateContact(Builder builder) {
        super(builder);
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.cellphoneNumber = builder.cellphoneNumber;
        this.email = builder.email;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long userId;
        private String firstName;
        private String lastName;
        private String cellphoneNumber;
        private String email;

        public Builder(long userId, String firstName, String lastName, String cellphoneNumber, String email) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.cellphoneNumber = cellphoneNumber;
            this.email = email;
        }


        public RequestUpdateContact build() {
            return new RequestUpdateContact(this);
        }


        @Override
        protected Builder self() {
            return this;
        }
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


}
