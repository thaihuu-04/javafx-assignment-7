package application.controllers;

import application.dao.CongViecDAO;
import application.dao.DuAnDAO;
import application.dao.NhanVienDAO;
import application.models.CongViec;
import application.models.DuAn;
import application.models.NhanVien;
import application.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class CongViecController {

    @FXML private ComboBox<DuAn> cboDuAn;
    @FXML private ComboBox<NhanVien> cboNhanVien;
    @FXML private ComboBox<String> cboTrangThai;

    @FXML private TableView<CongViec> tableCV;
    @FXML private TableColumn<CongViec, Integer> colMa;
    @FXML private TableColumn<CongViec, String> colTen;
    @FXML private TableColumn<CongViec, String> colTrangThai;
    @FXML private TableColumn<CongViec, Integer> colTienDo;

    @FXML private TextField txtTenCV, txtSearch;
    @FXML private DatePicker dpBD, dpKT;
    @FXML private Slider sliderTienDo;

    private DuAnDAO duAnDAO = new DuAnDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private CongViecDAO congViecDAO = new CongViecDAO();

    @FXML
    public void initialize() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maCV"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenCV"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTienDo.setCellValueFactory(new PropertyValueFactory<>("tienDo"));

        cboTrangThai.setItems(FXCollections.observableArrayList("Chưa bắt đầu","Đang làm","Hoàn thành"));
        cboDuAn.setItems(FXCollections.observableArrayList(duAnDAO.getAll()));
        cboNhanVien.setItems(FXCollections.observableArrayList(nhanVienDAO.getAll()));

        loadAll();
    }

    private void loadAll() {
        List<CongViec> all = congViecDAO.getAll();
        tableCV.setItems(FXCollections.observableArrayList(all));
    }

    @FXML
    private void loadByProject() {
        DuAn da = cboDuAn.getValue();
        if (da == null) { AlertUtil.warning("Chọn", "Vui lòng chọn dự án"); return; }
        List<CongViec> list = congViecDAO.getAllByProject(da.getMaDA());
        tableCV.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void add() {
        try {
            CongViec cv = new CongViec();
            DuAn da = cboDuAn.getValue();
            NhanVien nv = cboNhanVien.getValue();
            if (da == null || nv == null) { AlertUtil.warning("Dữ liệu", "Chọn dự án và nhân viên"); return; }

            cv.setMaDA(da.getMaDA());
            cv.setMaNV(nv.getMaNV());
            cv.setTenCV(txtTenCV.getText());
            cv.setNgayBatDau(dpBD.getValue());
            cv.setNgayKetThuc(dpKT.getValue());
            cv.setTienDo((int)sliderTienDo.getValue());
            cv.setTrangThai(cboTrangThai.getValue());

            if (congViecDAO.insert(cv)) {
                AlertUtil.info("Thêm", "Đã thêm công việc");
                loadByProject();
            } else AlertUtil.error("Lỗi", "Thêm thất bại");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", e.getMessage());
        }
    }

    @FXML
    private void update() {
        CongViec sel = tableCV.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Chọn", "Vui lòng chọn công việc"); return; }
        try {
            sel.setTenCV(txtTenCV.getText());
            sel.setNgayBatDau(dpBD.getValue());
            sel.setNgayKetThuc(dpKT.getValue());
            sel.setTienDo((int)sliderTienDo.getValue());
            sel.setTrangThai(cboTrangThai.getValue());
            sel.setMaNV(cboNhanVien.getValue().getMaNV());

            if (congViecDAO.update(sel)) {
                AlertUtil.info("Cập nhật", "Đã cập nhật");
                loadByProject();
            } else AlertUtil.error("Lỗi", "Cập nhật thất bại");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", e.getMessage());
        }
    }

    @FXML
    private void delete() {
        CongViec sel = tableCV.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Chọn", "Vui lòng chọn công việc để xoá"); return; }
        if (congViecDAO.delete(sel.getMaCV())) {
            AlertUtil.info("Xóa", "Đã xóa");
            loadByProject();
        } else AlertUtil.error("Lỗi", "Xóa thất bại");
    }

    @FXML
    private void search() {
        String q = txtSearch.getText();
        if (q == null || q.isBlank()) loadByProject();
        else tableCV.setItems(FXCollections.observableArrayList(congViecDAO.search(q)));
    }

    @FXML
    private void onTableClick() {
        CongViec sel = tableCV.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        txtTenCV.setText(sel.getTenCV());
        dpBD.setValue(sel.getNgayBatDau());
        dpKT.setValue(sel.getNgayKetThuc());
        sliderTienDo.setValue(sel.getTienDo());
        cboTrangThai.setValue(sel.getTrangThai());
        nhanVienDAO.getAll().stream()
            .filter(n->n.getMaNV()==sel.getMaNV())
            .findFirst().ifPresent(n->cboNhanVien.setValue(n));
    }
}
