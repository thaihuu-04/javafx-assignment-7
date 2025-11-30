package application.controllers;

import application.dao.NhanVienDAO;
import application.models.NhanVien;
import application.utils.AlertUtil;
import application.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cboUserType;

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();
        if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
            AlertUtil.error("Lỗi", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }
        try {
            NhanVien nvLogin = null;
            for (NhanVien nv : nhanVienDAO.getAll()) {
                if (user.equals(nv.getUsername()) && pass.equals(nv.getMatKhau())) {
                    nvLogin = nv;
                    break;
                }
            }
            if (nvLogin == null) {
                AlertUtil.warning("Đăng nhập", "Sai tên đăng nhập hoặc mật khẩu.");
                return;
            }
            UserSession.setSession(nvLogin.getUsername(), nvLogin.getVaiTro(), nvLogin.getMaNV());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/main.fxml"));
            Parent root = loader.load();
            MainController mainCtrl = loader.getController();
            mainCtrl.setCurrentUser(nvLogin);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Quản lý Dự án CNTT");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", "Không thể đăng nhập: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        cboUserType.setItems(FXCollections.observableArrayList("Admin", "PM", "Nhân viên"));
        cboUserType.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldVal, String newVal) {
                if (newVal == null) return;
                switch (newVal) {
                    case "Admin":
                        txtUsername.setText("admin");
                        txtPassword.setText("admin123");
                        break;
                    case "PM":
                        txtUsername.setText("userpm");
                        txtPassword.setText("123456");
                        break;
                    case "Nhân viên":
                        txtUsername.setText("usernv");
                        txtPassword.setText("123456");
                        break;
                }
            }
        });
    }
}