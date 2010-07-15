package com.jsonengine.util;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserUtil {

    /**
     * ログインユーザのEmailを取得する
     *
     * @return
     */
    public static String userEmail() {
        return getUser() != null ? getUser().getEmail() : "";
    }

    /**
     * ログインユーザ情報を取得する
     *
     * @return
     */
    public static User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    /**
     * ログインユーザがadmin権限ありかを判定する
     *
     * @return
     */
    public static boolean isAdmin() {
        if (!isLogined()) {
            return false;
        }
        return UserServiceFactory.getUserService().isUserAdmin();
    }

    /**
     * ログイン状態かを判定する
     *
     * @return
     */
    public static boolean isLogined() {
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
