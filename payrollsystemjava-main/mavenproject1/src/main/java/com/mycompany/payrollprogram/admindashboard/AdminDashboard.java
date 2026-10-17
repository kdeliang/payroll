package com.mycompany.payrollprogram.admindashboard;

import javax.swing.SwingUtilities;

public class AdminDashboard {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::showLogin);
    }

    /** Opens a fresh login window. Also used by the Log out buttons. */
    public static void showLogin() {
        LoginView view = new LoginView();
        LoginFormModel model = new LoginFormModel();
        new LoginController(view, model);
        view.setVisible(true);
    }
}
