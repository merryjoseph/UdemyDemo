package com.example.PasswordEmailSecurityDemo.Security;

import com.example.PasswordEmailSecurityDemo.SpringApplicationContext;

public class SecurityConstants {
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 3600000; // for password reset token 1 hour
    public static final long EXPIRATION_TIME = 864000000;// for user token 10 days in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String VERIFICATION_EMAIL_URL = "/users/verify";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String RESET_PASSWORD_URL = "/users/resetpassword";

    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }

}
