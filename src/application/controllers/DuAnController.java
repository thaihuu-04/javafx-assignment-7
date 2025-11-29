package application.controllers;

import application.dao.DuAnDAO;
import application.dao.NhanVienDAO;
import application.models.DuAn;
import application.models.NhanVien;
import application.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class DuAnController {

    @FXML private TableView<DuAn> tableDuAn;
    @FXML private TableColumn<DuAn, Integer> colMa;
    @FXML private TableColumn<DuAn, String> colTen;
    @FXML private TableColumn<DuAn, LocalDate> colBD;
    @FXML private TableColumn<DuAn, LocalDate> colKT;
    @FXML private TableColumn<DuAn, String> colTT;
    @FXML private TableColumn<DuAn, String> colQL;
    @FXML private ComboBox<NhanVien> cboQuanLy;

    @FXML private TextField txtTenDA;
    @FXML private DatePicker dpBD, dpKT;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private TextField txtTim;

    private DuAnDAO duAnDAO = new DuAnDAO();
    private ObservableList<DuAn> data = FXCollections.observableArrayList();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private DuAn selectedDuAn = null;
    
    @FXML
    public void initialize() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maDA"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenDA"));
        colBD.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colKT.setCellValueFactory(new PropertyValueFactory<>("ngayKetThuc"));
        colTT.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colQL.setCellValueFactory(new PropertyValueFactory<>("nguoiQuanLy"));

        cboTrangThai.setItems(FXCollections.observableArrayList("Đang thực hiện","Hoàn thành","Tạm dừng"));
        cboQuanLy.setItems(FXCollections.observableArrayList(nhanVienDAO.getAll()));

        loadData();
        
        // Add listener for table row click
        tableDuAn.setOnMouseClicked(event -> onTableClick());
    }

    private void loadData() {
        data.clear();
        List<DuAn> list = duAnDAO.getAll();
        data.addAll(list);
        tableDuAn.setItems(data);
    }

    @FXML
    private void add() {
        try {
            // Reset form
            if (selectedDuAn != null) {
                AlertUtil.warning("Thêm", "Vui lòng xóa selection trước khi thêm mới");
                return;
            }
            
            DuAn da = new DuAn();
            da.setTenDA(txtTenDA.getText());
            da.setNgayBatDau(dpBD.getValue());
            da.setNgayKetThuc(dpKT.getValue());
            da.setTrangThai(cboTrangThai.getValue());
            
            NhanVien nv = cboQuanLy.getValue();
            if (nv != null) {
                da.setNguoiQuanLy(nv.getHoTen());
            }

            String v = duAnDAO.insert(da) ? "Thêm thành công" : "Thêm thất bại";
            AlertUtil.info("Thêm dự án", v);
            clearForm();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", e.getMessage());
        }
    }

    @FXML
    private void update() {
        if (selectedDuAn == null) { 
            AlertUtil.warning("Chọn", "Vui lòng chọn dự án để sửa"); 
            return; 
        }
        try {
            selectedDuAn.setTenDA(txtTenDA.getText());
            selectedDuAn.setNgayBatDau(dpBD.getValue());
            selectedDuAn.setNgayKetThuc(dpKT.getValue());
            selectedDuAn.setTrangThai(cboTrangThai.getValue());
            
            NhanVien nv = cboQuanLy.getValue();
            if (nv != null) {
                selectedDuAn.setNguoiQuanLy(nv.getHoTen());
            }
            
            boolean ok = duAnDAO.update(selectedDuAn);
            if (ok) AlertUtil.info("Cập nhật", "Đã cập nhật dự án");
            clearForm();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", e.getMessage());
        }
    }

    @FXML
    private void delete() {
        DuAn sel = tableDuAn.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Chọn", "Vui lòng chọn dự án để xoá"); return; }
        boolean ok = duAnDAO.delete(sel.getMaDA());
        if (ok) {
            AlertUtil.info("Xóa", "Đã xóa dự án");
            loadData();
        } else AlertUtil.error("Xóa", "Xóa thất bại");
    }

    @FXML
    private void search() {
        String q = txtTim.getText();
        if (q == null || q.isBlank()) { loadData(); return; }
        List<DuAn> results = duAnDAO.search(q);
        tableDuAn.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    private void clearSearch() {
        txtTim.clear();
        loadData();
    }

    @FXML
    private void onTableClick() {
        DuAn sel = tableDuAn.getSelectionModel().getSelectedItem();
        
        // If clicking on the same row that's already selected, deselect it
        if (selectedDuAn != null && sel != null && selectedDuAn.getMaDA() == sel.getMaDA()) {
            tableDuAn.getSelectionModel().clearSelection();
            selectedDuAn = null;
            return;
        }
        
        if (sel == null) return;
        
        selectedDuAn = sel;
        txtTenDA.setText(sel.getTenDA());
        dpBD.setValue(sel.getNgayBatDau());
        dpKT.setValue(sel.getNgayKetThuc());
        cboTrangThai.setValue(sel.getTrangThai());
        
        // Set NguoiQuanLy to ComboBox
        if (sel.getNguoiQuanLy() != null && !sel.getNguoiQuanLy().isEmpty()) {
            List<NhanVien> allNV = nhanVienDAO.getAll();
            for (NhanVien nv : allNV) {
                if (nv.getHoTen().equals(sel.getNguoiQuanLy())) {
                    cboQuanLy.setValue(nv);
                    break;
                }
            }
        }
    }
    
    private void clearForm() {
        selectedDuAn = null;
        txtTenDA.clear();
        dpBD.setValue(null);
        dpKT.setValue(null);
        cboTrangThai.setValue(null);
        cboQuanLy.setValue(null);
    }
}