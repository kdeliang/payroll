package com.mycompany.payrollprogram.admindashboard;

import com.mycompany.payrollprogram.adminfeature.adminmain;
import com.mycompany.payrollprogram.database.Database;
import com.mycompany.payrollprogram.employeefeature.EmployeeMain;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class LoginController {

    private final LoginView view;
    private final LoginFormModel model;

    public LoginController(LoginView view, LoginFormModel model) {
        this.view = view;
        this.model = model;
        this.view.getSignInButton().addActionListener(event -> signIn());
    }

    private void signIn() {
        char[] password = view.getPassword();
        try {
            model.setCredentials(view.getUsername(), password);
        } finally {
            Arrays.fill(password, '\0');
        }

        String validationMessage = model.getValidationMessage();
        if (validationMessage != null) {
            model.clearPassword();
            view.showMessage(validationMessage, "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = model.getUsername();
        char[] submittedPassword = model.copyPassword();
        model.clearPassword();

        String role;
        try {
            role = Database.authenticate(username, submittedPassword);
        } catch (SQLException exception) {
            exception.printStackTrace();
            view.showMessage(
                    "Could not access the MySQL payroll database.\n"
                    + "Please check the console for details and try again.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            Arrays.fill(submittedPassword, '\0');
        }

        if (role == null) {
            view.showMessage("Invalid username or password.", "Sign in failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        view.dispose();
        if ("ADMIN".equals(role)) {
            new adminmain().setVisible(true);
        } else {
            new EmployeeMain().setVisible(true);
        }
    }
}
