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

import java.util.List;
import java.util.ArrayList;

public class CongViecController {

    @FXML private ComboBox<DuAn> cboDuAn;
    @FXML private ComboBox<NhanVien> cboNhanVien;
    @FXML private ComboBox<String> cboTrangThai;

    @FXML private TableView<CongViec> tableCV;
    @FXML private TableColumn<CongViec, Integer> colMa;
    @FXML private TableColumn<CongViec, String> colTen;
    @FXML private TableColumn<CongViec, String> colDuAn;
    @FXML private TableColumn<CongViec, String> colNhanVien;
    @FXML private TableColumn<CongViec, String> colBD;
    @FXML private TableColumn<CongViec, String> colKT;
    @FXML private TableColumn<CongViec, String> colTrangThai;
    @FXML private TableColumn<CongViec, Integer> colTienDo;

    @FXML private TextField txtTenCV, txtSearchName;
    @FXML private DatePicker dpBD, dpKT;
    @FXML private Slider sliderTienDo;
    @FXML private ComboBox<NhanVien> cboSearchNhanVien;
    @FXML private ComboBox<String> cboSearchTrangThai;

    private DuAnDAO duAnDAO = new DuAnDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private CongViecDAO congViecDAO = new CongViecDAO();

    @FXML
    public void initialize() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maCV"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenCV"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTienDo.setCellValueFactory(new PropertyValueFactory<>("tienDo"));

        // Custom cell factories for lookups
        colDuAn.setCellValueFactory(cellData -> {
            int maDA = cellData.getValue().getMaDA();
            String tenDA = "N/A";
            for (DuAn da : duAnDAO.getAll()) {
                if (da.getMaDA() == maDA) {
                    tenDA = da.getTenDA();
                    break;
                }
            }
            return new javafx.beans.property.SimpleStringProperty(tenDA);
        });

        colNhanVien.setCellValueFactory(cellData -> {
            int maNV = cellData.getValue().getMaNV();
            String tenNV = "N/A";
            for (NhanVien nv : nhanVienDAO.getAll()) {
                if (nv.getMaNV() == maNV) {
                    tenNV = nv.getTenNV();
                    break;
                }
            }
            return new javafx.beans.property.SimpleStringProperty(tenNV);
        });

        colBD.setCellValueFactory(cellData -> {
            Object ngayBD = cellData.getValue().getNgayBatDau();
            String date = ngayBD != null ? ngayBD.toString() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(date);
        });

        colKT.setCellValueFactory(cellData -> {
            Object ngayKT = cellData.getValue().getNgayKetThuc();
            String date = ngayKT != null ? ngayKT.toString() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(date);
        });

        cboTrangThai.setItems(FXCollections.observableArrayList("Chưa bắt đầu","Đang làm","Hoàn thành"));
        cboDuAn.setItems(FXCollections.observableArrayList(duAnDAO.getAll()));
        cboNhanVien.setItems(FXCollections.observableArrayList(nhanVienDAO.getAll()));
        
        // Setup search filters
        cboSearchNhanVien.setItems(FXCollections.observableArrayList(nhanVienDAO.getAll()));
        cboSearchTrangThai.setItems(FXCollections.observableArrayList("Chưa bắt đầu","Đang làm","Hoàn thành"));

        // Add listener to cboTrangThai to automatically update progress
        cboTrangThai.setOnAction(event -> updateProgressByStatus());

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
            cv.setTrangThai(cboTrangThai.getValue());
            
            // Auto-set progress based on status
            if ("Chưa bắt đầu".equals(cboTrangThai.getValue())) {
                cv.setTienDo(0);
                sliderTienDo.setValue(0);
            } else if ("Hoàn thành".equals(cboTrangThai.getValue())) {
                cv.setTienDo(100);
                sliderTienDo.setValue(100);
            } else {
                cv.setTienDo((int)sliderTienDo.getValue());
            }

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
            sel.setTrangThai(cboTrangThai.getValue());
            sel.setMaNV(cboNhanVien.getValue().getMaNV());

            // Auto-set progress based on status
            if ("Chưa bắt đầu".equals(cboTrangThai.getValue())) {
                sel.setTienDo(0);
                sliderTienDo.setValue(0);
            } else if ("Hoàn thành".equals(cboTrangThai.getValue())) {
                sel.setTienDo(100);
                sliderTienDo.setValue(100);
            } else {
                sel.setTienDo((int)sliderTienDo.getValue());
            }

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
        String tenCV = txtSearchName.getText();
        NhanVien nhanVien = cboSearchNhanVien.getValue();
        String trangThai = cboSearchTrangThai.getValue();
        
        List<CongViec> all = congViecDAO.getAll();
        List<CongViec> results = new ArrayList<>();
        
        for (CongViec cv : all) {
            boolean matchName = tenCV == null || tenCV.isBlank() || cv.getTenCV().toLowerCase().contains(tenCV.toLowerCase());
            boolean matchNV = nhanVien == null || cv.getMaNV() == nhanVien.getMaNV();
            boolean matchStatus = trangThai == null || cv.getTrangThai().equals(trangThai);
            
            if (matchName && matchNV && matchStatus) {
                results.add(cv);
            }
        }
        
        tableCV.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    private void clearSearch() {
        txtSearchName.clear();
        cboSearchNhanVien.setValue(null);
        cboSearchTrangThai.setValue(null);
        loadByProject();
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

    private void updateProgressByStatus() {
        String status = cboTrangThai.getValue();
        if (status == null) return;
        
        if ("Chưa bắt đầu".equals(status)) {
            sliderTienDo.setValue(0);
        } else if ("Hoàn thành".equals(status)) {
            sliderTienDo.setValue(100);
        }
    }
}
