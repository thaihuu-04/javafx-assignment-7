package application.controllers;

import application.dao.DuAnDAO;
import application.models.DuAn;
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

    @FXML private TextField txtTenDA;
    @FXML private DatePicker dpBD, dpKT;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private TextField txtTim;

    private DuAnDAO duAnDAO = new DuAnDAO();
    private ObservableList<DuAn> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maDA"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenDA"));
        colBD.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colKT.setCellValueFactory(new PropertyValueFactory<>("ngayKetThuc"));
        colTT.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colQL.setCellValueFactory(new PropertyValueFactory<>("nguoiQuanLy"));

        cboTrangThai.setItems(FXCollections.observableArrayList("Đang thực hiện","Hoàn thành","Tạm dừng"));

        loadData();
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
            DuAn da = new DuAn();
            da.setTenDA(txtTenDA.getText());
            da.setNgayBatDau(dpBD.getValue());
            da.setNgayKetThuc(dpKT.getValue());
            da.setTrangThai(cboTrangThai.getValue());

            String v = duAnDAO.insert(da) ? "Thêm thành công" : "Thêm thất bại";
            AlertUtil.info("Thêm dự án", v);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", e.getMessage());
        }
    }

    @FXML
    private void update() {
        DuAn sel = tableDuAn.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Chọn", "Vui lòng chọn dự án để sửa"); return; }
        try {
            sel.setTenDA(txtTenDA.getText());
            sel.setNgayBatDau(dpBD.getValue());
            sel.setNgayKetThuc(dpKT.getValue());
            sel.setTrangThai(cboTrangThai.getValue());
            boolean ok = duAnDAO.update(sel);
            if (ok) AlertUtil.info("Cập nhật", "Đã cập nhật dự án");
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
    private void onTableClick() {
        DuAn sel = tableDuAn.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        txtTenDA.setText(sel.getTenDA());
        dpBD.setValue(sel.getNgayBatDau());
        dpKT.setValue(sel.getNgayKetThuc());
        cboTrangThai.setValue(sel.getTrangThai());
    }
}
