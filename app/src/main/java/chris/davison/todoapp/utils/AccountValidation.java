package chris.davison.todoapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountValidation {
    public static String validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Email field is empty";
        }

        Pattern regexPattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
                "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = regexPattern.matcher(email);

        return matcher.find() ? "" : "Email is invalid";
    }

    public static String validatePassword(String password) {
        if(password == null || password.isEmpty()) {
            return "Password field is empty";
        }

        if (password.length() >= 8 && password.length() <= 20) {
            if (password.matches("[a-zA-Z0-9]+")) {
                return "";
            }
            else {
                return "Password should contain letters and numbers only";
            }
        }
        else {
            return "Password length should be 8 to 20 characters long";
        }
    }

    public static String validatePassword(String passwordOne, String passwordTwo) {
        if(passwordOne == null || passwordOne.isEmpty()) {
            return "Password field is empty";
        }

        if (passwordTwo == null || passwordTwo.isEmpty()) {
            return "Password confirmation field is empty";
        }

        if (passwordOne.length() >= 8 && passwordOne.length() <= 20) {
            if (passwordOne.matches("[a-zA-Z0-9]+")) {
                if (passwordOne.equals(passwordTwo)) {
                    return "";
                }
                else {
                    return "Passwords do not match";
                }
            }
            else {
                return "Password should contain letters and numbers only";
            }
        }
        else {
            return "Password length should be 8 to 20 characters long";
        }
    }
}