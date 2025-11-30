package application.controllers;

import application.dao.CongViecDAO;
import application.dao.DuAnDAO;
import application.dao.NhanVienDAO;
import application.models.CongViec;
import application.models.DuAn;
import application.models.NhanVien;
import application.utils.AlertUtil;
import application.utils.UserSession;
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
    @FXML private Button btnThem, btnSua, btnXoa;

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

        // Lấy vai trò từ UserSession
        String vaiTro = UserSession.getVaiTro();
        String tenDangNhap = UserSession.getTenDangNhap();
        Integer maNV = UserSession.getMaNV();
        if (vaiTro != null && vaiTro.equals("PM")) {
            // Lấy danh sách dự án mà PM quản lý
            List<DuAn> duAnPM = duAnDAO.getAllByManager(tenDangNhap);
            cboDuAn.setItems(FXCollections.observableArrayList(duAnPM));
            // Lấy danh sách nhân viên liên quan đến các dự án PM quản lý
            List<NhanVien> nhanVienDuAn = new ArrayList<>();
            for (DuAn da : duAnPM) {
                List<NhanVien> nvs = nhanVienDAO.getByProject(da.getMaDA());
                for (NhanVien nv : nvs) {
                    if (!nhanVienDuAn.contains(nv)) nhanVienDuAn.add(nv);
                }
            }
            cboNhanVien.setItems(FXCollections.observableArrayList(nhanVienDuAn));
            cboSearchNhanVien.setItems(FXCollections.observableArrayList(nhanVienDuAn));
            // Chỉ hiển thị công việc thuộc dự án PM quản lý
            List<CongViec> allCV = congViecDAO.getAll();
            List<Integer> maDAList = new ArrayList<>();
            for (DuAn da : duAnPM) maDAList.add(da.getMaDA());
            List<CongViec> filteredCV = new ArrayList<>();
            for (CongViec cv : allCV) {
                if (maDAList.contains(cv.getMaDA())) filteredCV.add(cv);
            }
            tableCV.setItems(FXCollections.observableArrayList(filteredCV));
        }
        // Phân quyền hiển thị nút chức năng
        if (vaiTro == null) return;
        switch (vaiTro) {
            case "Admin":
                btnThem.setVisible(true);
                btnSua.setVisible(true);
                btnXoa.setVisible(true);
                break;
            case "PM":
                btnThem.setVisible(true);
                btnSua.setVisible(true);
                btnXoa.setVisible(true);
                break;
            case "Nhân Viên":
                btnThem.setVisible(false);
                btnSua.setVisible(false);
                btnXoa.setVisible(false);
                break;
        }
        // Lọc dữ liệu hiển thị theo quyền
        loadAllByRole();

        loadAll();

        if (vaiTro != null && vaiTro.equals("Nhân Viên")) {
            cboDuAn.setDisable(true);
            cboNhanVien.setDisable(true);
            txtTenCV.setDisable(true);
            dpBD.setDisable(true);
            dpKT.setDisable(true);
            cboTrangThai.setDisable(true);
            sliderTienDo.setDisable(true);
        }
    }

    private void loadAll() {
        List<CongViec> all = congViecDAO.getAll();
        tableCV.setItems(FXCollections.observableArrayList(all));
    }

    private void loadAllByRole() {
        String vaiTro = UserSession.getVaiTro();
        Integer maNV = UserSession.getMaNV();
        String tenDangNhap = UserSession.getTenDangNhap();
        List<CongViec> all = congViecDAO.getAll();
        List<CongViec> filtered = new ArrayList<>();
        if (vaiTro == null) return;
        switch (vaiTro) {
            case "Admin":
                filtered = all;
                break;
            case "PM":
                // Lấy danh sách dự án mà PM quản lý
                List<DuAn> duAnPM = duAnDAO.getAllByManager(tenDangNhap);
                List<Integer> maDAList = new ArrayList<>();
                for (DuAn da : duAnPM) maDAList.add(da.getMaDA());
                for (CongViec cv : all) {
                    if (maDAList.contains(cv.getMaDA())) filtered.add(cv);
                }
                break;
            case "Nhân viên":
                if (maNV != null) {
                    for (CongViec cv : all) {
                        if (cv.getMaNV() == maNV.intValue()) filtered.add(cv);
                    }
                }
                break;
        }
        tableCV.setItems(FXCollections.observableArrayList(filtered));
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
        // Kiểm tra quyền trước khi thêm
        String vaiTro = UserSession.getVaiTro();
        if (!"Admin".equals(vaiTro) && !"PM".equals(vaiTro)) {
            AlertUtil.warning("Phân quyền", "Bạn không có quyền thêm công việc");
            return;
        }
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
        // Kiểm tra quyền trước khi sửa
        String vaiTro = UserSession.getVaiTro();
        if (!"Admin".equals(vaiTro) && !"PM".equals(vaiTro)) {
            AlertUtil.warning("Phân quyền", "Bạn không có quyền sửa công việc");
            return;
        }
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
        // Kiểm tra quyền trước khi xóa
        String vaiTro = UserSession.getVaiTro();
        if (!"Admin".equals(vaiTro) && !"PM".equals(vaiTro)) {
            AlertUtil.warning("Phân quyền", "Bạn không có quyền xóa công việc");
            return;
        }
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