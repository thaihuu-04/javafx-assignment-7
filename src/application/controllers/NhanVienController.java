package application.controllers;

import application.dao.NhanVienDAO;
import application.models.NhanVien;
import application.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.ArrayList;

public class NhanVienController {

    @FXML private TableView<NhanVien> tblNV;
    @FXML private TableColumn<NhanVien, Integer> colMa;
    @FXML private TableColumn<NhanVien, String> colTen;
    @FXML private TableColumn<NhanVien, String> colChucVu;
    @FXML private TableColumn<NhanVien, String> colEmail;
    @FXML private TableColumn<NhanVien, String> colUsername;
    @FXML private TableColumn<NhanVien, String> colPassword;

    @FXML private TextField txtTen, txtEmail, txtTim;
    @FXML private ComboBox<String> cboChucVu;
    @FXML private ComboBox<String> cboLocChucVu;
    @FXML private TextField txtUsername, txtPassword;
    @FXML private Button btnThem, btnSua, btnXoa, btnShowPassword;

    private NhanVienDAO dao = new NhanVienDAO();

    @FXML
    public void initialize() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("matKhau"));

        cboChucVu.setItems(FXCollections.observableArrayList("Lập trình viên","Tester","PM","Designer","Admin"));
        cboLocChucVu.setItems(FXCollections.observableArrayList("Lập trình viên","Tester","PM","Designer"));
        
        String vaiTro = application.utils.UserSession.getVaiTro();
        Integer maNV = application.utils.UserSession.getMaNV();
        if (vaiTro != null && (vaiTro.equals("PM") || vaiTro.equals("Nhân Viên"))) {
            btnThem.setDisable(true);
            btnThem.setVisible(false);
            btnXoa.setDisable(true);
            btnXoa.setVisible(false);
            // Chỉ hiển thị thông tin của chính mình
            List<NhanVien> list = dao.getAll();
            for (NhanVien nv : list) {
                if (nv.getMaNV() == maNV) {
                    tblNV.setItems(FXCollections.observableArrayList(nv));
                    break;
                }
            }
        } else {
            load();
        }
    }

    private void load() {
        List<NhanVien> list = dao.getAll();
        tblNV.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void add() {
        String vaiTro = application.utils.UserSession.getVaiTro();
        if (vaiTro != null && !vaiTro.equals("Admin")) {
            AlertUtil.warning("Phân quyền", "Bạn không có quyền thêm nhân viên");
            return;
        }
        NhanVien nv = new NhanVien();
        nv.setHoTen(txtTen.getText());
        nv.setChucVu(cboChucVu.getValue());
        nv.setEmail(txtEmail.getText());
        nv.setUsername(txtUsername.getText());
        nv.setMatKhau(txtPassword.getText());
        boolean ok = dao.insert(nv);
        if (ok) { AlertUtil.info("Thêm", "Đã thêm nhân viên"); load(); }
        else AlertUtil.error("Lỗi", "Thêm thất bại");
    }

    @FXML
    private void update() {
        String vaiTro = application.utils.UserSession.getVaiTro();
        Integer maNV = application.utils.UserSession.getMaNV();
        NhanVien sel = tblNV.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Chọn", "Vui lòng chọn nhân viên"); return; }
        if (vaiTro != null && (vaiTro.equals("PM") || vaiTro.equals("Nhân Viên"))) {
            // Chỉ cho phép sửa thông tin của chính mình
            if (sel.getMaNV() != maNV) {
                AlertUtil.warning("Phân quyền", "Bạn chỉ được sửa thông tin của mình");
                return;
            }
        }
        sel.setHoTen(txtTen.getText());
        sel.setChucVu(cboChucVu.getValue());
        sel.setEmail(txtEmail.getText());
        sel.setUsername(txtUsername.getText());
        sel.setMatKhau(txtPassword.getText());
        if (dao.update(sel)) {
            AlertUtil.info("Cập nhật", "Đã lưu");
            if (vaiTro != null && (vaiTro.equals("PM") || vaiTro.equals("Nhân Viên"))) {
                // Chỉ hiển thị lại đúng user đang đăng nhập
                List<NhanVien> list = dao.getAll();
                for (NhanVien nv : list) {
                    if (nv.getMaNV() == maNV) {
                        tblNV.setItems(FXCollections.observableArrayList(nv));
                        break;
                    }
                }
            } else {
                load();
            }
        }
        else AlertUtil.error("Lỗi", "Cập nhật thất bại");
    }

    @FXML
    private void delete() {
        String vaiTro = application.utils.UserSession.getVaiTro();
        if (vaiTro != null && !vaiTro.equals("Admin")) {
            AlertUtil.warning("Phân quyền", "Bạn không có quyền xóa nhân viên");
            return;
        }
        NhanVien sel = tblNV.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Chọn", "Vui lòng chọn nhân viên"); return; }
        if (dao.delete(sel.getMaNV())) { AlertUtil.info("Xóa", "Đã xóa"); load(); }
        else AlertUtil.error("Lỗi", "Xóa thất bại");
    }

    @FXML
    private void search() {
        String q = txtTim.getText();
        tblNV.setItems(FXCollections.observableArrayList(dao.search(q)));
    }

    @FXML
    private void clearSearch() {
        txtTim.clear();
        load();
    }

    @FXML
    private void onTableClick() {
        NhanVien sel = tblNV.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        txtTen.setText(sel.getHoTen());
        cboChucVu.setValue(sel.getChucVu());
        txtEmail.setText(sel.getEmail());
    }

    @FXML
    private void filter() {
        String chucVu = cboLocChucVu.getValue();
        if (chucVu == null || chucVu.isEmpty()) {
            load();
            return;
        }
        
        List<NhanVien> allData = dao.getAll();
        List<NhanVien> filtered = new ArrayList<>();
        
        for (NhanVien nv : allData) {
            if (nv.getChucVu().equals(chucVu)) {
                filtered.add(nv);
            }
        }
        
        tblNV.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void clearFilter() {
        cboLocChucVu.setValue(null);
        load();
    }

    @FXML
    private void togglePasswordColumn() {
        boolean isVisible = colPassword.isVisible();
        colPassword.setVisible(!isVisible);
        btnShowPassword.setText(isVisible ? "Hiện mật khẩu" : "Ẩn mật khẩu");
    }
}