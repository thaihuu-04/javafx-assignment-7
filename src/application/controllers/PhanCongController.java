package application.controllers;

import application.dao.DuAnDAO;
import application.dao.NhanVienDAO;
import application.dao.PhanCongDAO;
import application.models.DuAn;
import application.models.NhanVien;
import application.models.PhanCong;
import application.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class PhanCongController {

    @FXML private ComboBox<DuAn> cboDuAn;
    @FXML private ComboBox<NhanVien> cboNhanVien;
    @FXML private TableView<PhanCong> tbl;
    @FXML private TableColumn<PhanCong, Integer> colMaPC;
    @FXML private TableColumn<PhanCong, Integer> colMaNV;
    @FXML private TableColumn<PhanCong, Integer> colMaDA;
    @FXML private TableColumn<PhanCong, String> colTenNV;
    @FXML private TableColumn<PhanCong, String> colTenDA;

    private DuAnDAO duAnDAO = new DuAnDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private PhanCongDAO phanCongDAO = new PhanCongDAO();

    @FXML
    public void initialize() {
        colMaPC.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maPC"));
        colMaNV.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maNV"));
        colMaDA.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maDA"));
        colTenNV.setCellValueFactory(cellData -> {
            int maNV = cellData.getValue().getMaNV();
            NhanVien nv = nhanVienDAO.getAll().stream().filter(n -> n.getMaNV() == maNV).findFirst().orElse(null);
            return new javafx.beans.property.SimpleStringProperty(nv != null ? nv.getTenNV() : "");
        });
        colTenDA.setCellValueFactory(cellData -> {
            int maDA = cellData.getValue().getMaDA();
            DuAn da = duAnDAO.getAll().stream().filter(d -> d.getMaDA() == maDA).findFirst().orElse(null);
            return new javafx.beans.property.SimpleStringProperty(da != null ? da.getTenDA() : "");
        });
        List<DuAn> das = duAnDAO.getAll();
        List<NhanVien> nvs = nhanVienDAO.getAll();
        cboDuAn.setItems(FXCollections.observableArrayList(das));
        cboNhanVien.setItems(FXCollections.observableArrayList(nvs));
        load();
    }

    private void load() {
        tbl.setItems(FXCollections.observableArrayList(phanCongDAO.getAll()));
    }

    @FXML
    private void assign() {
        DuAn da = cboDuAn.getValue();
        NhanVien nv = cboNhanVien.getValue();
        if (da==null || nv==null) { AlertUtil.warning("Chọn","Vui lòng chọn dự án và nhân viên"); return; }
        PhanCong pc = new PhanCong();
        pc.setMaDA(da.getMaDA());
        pc.setMaNV(nv.getMaNV());
        if (phanCongDAO.insert(pc)) {
            AlertUtil.info("Phân công", "Đã phân công");
            load();
        } else AlertUtil.error("Lỗi", "Phân công thất bại");
    }

    @FXML
    private void unassign() {
        PhanCong sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Chọn","Vui lòng chọn phân công để huỷ"); return;}
        if (phanCongDAO.delete(sel.getMaPC())) {
            AlertUtil.info("Hủy", "Đã hủy phân công");
            load();
        } else AlertUtil.error("Lỗi", "Hủy thất bại");
    }
}
