package application.controllers;

import application.models.NguoiDung;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;

public class MainController {

    @FXML private BorderPane mainPane;
    @FXML private Label lblUser;

    private NguoiDung currentUser;

    public void setCurrentUser(NguoiDung user) {
        this.currentUser = user;
        if (user != null) {
            lblUser.setText("Xin chào: " + user.getTenDangNhap() + " (" + user.getVaiTro() + ")");
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
    private void logout() {
        loadCenter("/application/views/login.fxml");
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
