package application.controllers;

import application.dao.NhanVienDAO;
import application.models.NhanVien;
import application.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class NhanVienController {

    @FXML private TableView<NhanVien> tblNV;
    @FXML private TableColumn<NhanVien, Integer> colMa;
    @FXML private TableColumn<NhanVien, String> colTen;
    @FXML private TableColumn<NhanVien, String> colChucVu;
    @FXML private TableColumn<NhanVien, String> colEmail;

    @FXML private TextField txtTen, txtEmail, txtTim;
    @FXML private ComboBox<String> cboChucVu;

    private NhanVienDAO dao = new NhanVienDAO();

    @FXML
    public void initialize() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        cboChucVu.setItems(FXCollections.observableArrayList("Lập trình viên","Tester","PM","Designer"));

        load();
    }

    private void load() {
        List<NhanVien> list = dao.getAll();
        tblNV.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void add() {
        NhanVien nv = new NhanVien();
        nv.setHoTen(txtTen.getText());
        nv.setChucVu(cboChucVu.getValue());
        nv.setEmail(txtEmail.getText());
        boolean ok = dao.insert(nv);
        if (ok) { AlertUtil.info("Thêm", "Đã thêm nhân viên"); load(); }
        else AlertUtil.error("Lỗi", "Thêm thất bại");
    }

    @FXML
    private void update() {
        NhanVien sel = tblNV.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Chọn", "Vui lòng chọn nhân viên"); return; }
        sel.setHoTen(txtTen.getText());
        sel.setChucVu(cboChucVu.getValue());
        sel.setEmail(txtEmail.getText());
        if (dao.update(sel)) { AlertUtil.info("Cập nhật", "Đã lưu"); load(); }
        else AlertUtil.error("Lỗi", "Cập nhật thất bại");
    }

    @FXML
    private void delete() {
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
    private void onTableClick() {
        NhanVien sel = tblNV.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        txtTen.setText(sel.getHoTen());
        cboChucVu.setValue(sel.getChucVu());
        txtEmail.setText(sel.getEmail());
    }
}
