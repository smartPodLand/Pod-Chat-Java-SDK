package podChat.mainmodel;

import java.util.ArrayList;

public class UserRoleVO {
        private long userId;
        private ArrayList<String> roles;
        private String roleOperation;

    public UserRoleVO() {
        roles = new ArrayList<>();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public String getRoleOperation() {
        return roleOperation;
    }

    public void setRoleOperation(String roleOperation) {
        this.roleOperation = roleOperation;
    }
}
