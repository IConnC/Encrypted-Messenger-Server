package xyz.iconc.dev.server.apiObjects;

public enum UserRoles {
    NORMAL("normal"),
    ADMIN("admin");


    private String userRoleName;

    private UserRoles(String roleName) {
        userRoleName = roleName;
    }

    public String getName() {
        return userRoleName;
    }

    public UserRoles getFromString(String roleName) {
        for (UserRoles userRole : UserRoles.values()) {
            if (userRole.getName().equals(roleName)) return userRole;
        }
        return null;
    }
}
