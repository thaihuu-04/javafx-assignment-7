package application.controllers;

import application.models.NhanVien;
import application.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;

public class MainController {

    @FXML private BorderPane mainPane;
    @FXML private Label lblUser;
    @FXML private Button btnLogout;

    private NhanVien currentUser;

    public void setCurrentUser(NhanVien user) {
        this.currentUser = user;
        if (user != null) {
            lblUser.setText("Xin chào: " + user.getUsername() + " (" + user.getVaiTro() + ")");
        }
        showDuAn();
    }

    @FXML
    private void showDuAn() { loadCenter("/application/views/duan.fxml"); }

    @FXML
    private void showNhanVien() { loadCenter("/application/views/nhanvien.fxml"); }

    @FXML
    private void showCongViec() { loadCenter("/application/views/congviec.fxml"); }

    @FXML
    private void showPhanCong() { loadCenter("/application/views/phancong.fxml"); }

    @FXML
    private void showThongKe() { loadCenter("/application/views/thongke.fxml"); }

    @FXML
    public void logout() {
        UserSession.clear();
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/application/views/login.fxml"));
            mainPane.getScene().setRoot(login);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lblUser.setText("Guest");
    }

    private void loadCenter(String fxmlPath) {
        try {
            Parent p = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainPane.setCenter(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}