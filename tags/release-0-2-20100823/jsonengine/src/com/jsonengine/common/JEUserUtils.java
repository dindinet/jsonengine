package com.jsonengine.common;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class JEUserUtils {

    /**
     * Returns the current user's email address.
     * 
     * @return email address
     */
    public static String userEmail() {
        return getUser() != null ? getUser().getEmail() : "";
    }

    /**
     * Returns User object for the current user.
     * 
     * @return
     */
    public static User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    /**
     * Returns true if the user has Administrator role.
     * 
     * @return true if the user is admin
     */
    public static boolean isAdmin() {
        if (!isLoggedIn()) {
            return false;
        }
        return UserServiceFactory.getUserService().isUserAdmin();
    }

    /**
     * Returns true if the user has logged in.
     * 
     * @return true if logged in
     */
    public static boolean isLoggedIn() {
        User user = getUser();
        if (user == null) {
            return false;
        }
        if (user.getEmail().length() == 0) {
            return false;
        }
        return true;
    }
}
