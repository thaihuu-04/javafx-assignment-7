package application.controllers;

import application.dao.NguoiDungDAO;
import application.models.NguoiDung;
import application.utils.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();

        if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
            AlertUtil.error("Lỗi", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            NguoiDung nd = nguoiDungDAO.login(user, pass);
            if (nd == null) {
                AlertUtil.warning("Đăng nhập", "Sai tên đăng nhập hoặc mật khẩu.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/main.fxml"));
            Parent root = loader.load();

            MainController mainCtrl = loader.getController();
            mainCtrl.setCurrentUser(nd);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Quản lý Dự án CNTT");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", "Không thể đăng nhập: " + e.getMessage());
        }
    }
}
