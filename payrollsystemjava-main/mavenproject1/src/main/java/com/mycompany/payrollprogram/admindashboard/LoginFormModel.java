package com.mycompany.payrollprogram.admindashboard;

import java.util.Arrays;

public class LoginFormModel {

    private String username;
    private char[] password;

    public void setCredentials(String username, char[] password) {
        this.username = username == null ? "" : username.trim();
        clearPassword();
        this.password = password == null ? new char[0] : Arrays.copyOf(password, password.length);
    }

    public String getUsername() {
        return username;
    }

    public char[] copyPassword() {
        return password == null ? new char[0] : Arrays.copyOf(password, password.length);
    }

    public String getValidationMessage() {
        boolean missingUsername = username == null || username.isEmpty();
        boolean missingPassword = password == null || password.length == 0;

        if (missingUsername && missingPassword) {
            return "Enter your username and password.";
        }
        if (missingUsername) {
            return "Enter your username.";
        }
        if (missingPassword) {
            return "Enter your password.";
        }
        return null;
    }

    public void clearPassword() {
        if (password != null) {
            Arrays.fill(password, '\0');
            password = new char[0];
        }
    }
}
