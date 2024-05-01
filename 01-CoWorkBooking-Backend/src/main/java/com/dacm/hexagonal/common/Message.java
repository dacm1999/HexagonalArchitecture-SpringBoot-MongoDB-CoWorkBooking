package com.dacm.hexagonal.common;

public class Message {

    // Messages for user registration
    public static final String USERNAME_TAKEN= "Username is already taken";
    public static final String EMAIL_TAKEN = "Email is already taken";
    public static final String PASSWORD_LENGTH= "Password must be more than 5 characters";
    public static final String PASSWORD_MANDATORY= "Password must be mandatory";
    public static final String EMAIL_MANDATORY= "Email must be mandatory";
    public static final String USERNAME_MANDATORY= "Username must be mandatory";

    // Messages for user login
    public static final String LOGIN_INVALID_USERNAME= "Invalid username";
    public static final String LOGIN_INVALID_PASSWORD= "Invalid password";

    // Messages for JWT
    public static final String JWT_TOKEN_EXPIRED= "Jwt token expired";
    public static final String JWT_TOKEN_INVALID= "Jwt token invalid";
    public static final String JWT_TOKEN_ACCESS_DENIED= "Jwt token access denied";

    // Messages for update user
    public static final String USER_SAVE_SUCCESSFULLY= "user saved successfully";
    public static final String USER_UPDATE_SUCCESSFULLY= "user updated successfully";
    public static final String USER_DELETE_SUCCESSFULLY= "user deleted successfully";
    public static final String USER_NOT_FOUND= "user not found";
    public static final String USER_EMAIL_ALREADY_EXISTS= "user email already exists";
    public static final String USER_NAME_ALREADY_EXISTS= "user firstName already exists";

    // Messages for space
    public static final String SPACE_SAVE_SUCCESSFULLY= "space saved successfully";
    public static final String SPACE_UPDATE_SUCCESSFULLY= "space updated successfully";
    public static final String SPACE_DELETE_SUCCESSFULLY= "space deleted successfully";
    public static final String SPACE_NOT_FOUND= "space not found";
    public static final String SPACE_NOT_AVAILABLE = "space not available";
    public static final String SPACE_ID_ALREADY_EXISTS = "space id already exists";


    public static final String BOOKING_CREATED_SUCCESSFULLY= "booking saved successfully";
    public static final String BOOKING_UPDATE_SUCCESSFULLY= "booking updated successfully";
    public static final String BOOKING_ERROR = "Could not book";
    public static final String BOOKING_DELETE_SUCCESSFULLY= "booking deleted successfully";
    public static final String BOOKING_NOT_FOUND= "booking not found";
    public static final String BOOKING_ALREADY_EXISTS= "booking already exists";
    public static final String BOOKING_NAME_ALREADY_EXISTS= "booking firstName already exists";
    public static final String BOOKING_EMAIL_ALREADY_EXISTS= "booking email already exists";

    public static final String AuthErrorResponse= "An error occurred while processing your request";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";


}
