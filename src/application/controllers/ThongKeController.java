
package application.controllers;

import application.dao.CongViecDAO;
import application.dao.DuAnDAO;
import application.dao.NhanVienDAO;
import application.dao.PhanCongDAO;
import application.models.DuAn;
import application.models.NhanVien;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.chart.PieChart;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;

public class ThongKeController {
    @FXML private Label lblTongDuAn, lblTongNhanVien, lblTongCongViec, lblTongPhanCong;
    @FXML private TableView<ThongKeDuAn> tableThongKe;
    @FXML private TableColumn<ThongKeDuAn, String> colTenDuAn;
    @FXML private TableColumn<ThongKeDuAn, Integer> colTienDo;
    @FXML private TableColumn<ThongKeDuAn, Integer> colSoLuongCV;
    @FXML private TableColumn<ThongKeDuAn, Integer> colSoLuongNV;
    @FXML private PieChart pieTrangThaiCV;

    private DuAnDAO duAnDAO = new DuAnDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private CongViecDAO congViecDAO = new CongViecDAO();
    private PhanCongDAO phanCongDAO = new PhanCongDAO();

    @FXML
    public void initialize() {
        updateOverview();
        setupTable();
        loadTableData();
        loadPieChart();
    }

    private void updateOverview() {
        lblTongDuAn.setText("Tổng dự án: " + duAnDAO.getAll().size());
        lblTongNhanVien.setText("Tổng nhân viên: " + nhanVienDAO.getAll().size());
        lblTongCongViec.setText("Tổng công việc: " + congViecDAO.getAll().size());
        lblTongPhanCong.setText("Tổng phân công: " + phanCongDAO.getAll().size());
    }

    private void setupTable() {
        colTenDuAn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tenDuAn));
        colTienDo.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().tienDo).asObject());
        colSoLuongCV.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soLuongCV).asObject());
        colSoLuongNV.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soLuongNV).asObject());
    }

    private void loadTableData() {
        List<DuAn> das = duAnDAO.getAll();
        List<NhanVien> nvs = nhanVienDAO.getAll();
        List<application.models.CongViec> cvs = congViecDAO.getAll();
        List<application.models.PhanCong> pcs = phanCongDAO.getAll();
        javafx.collections.ObservableList<ThongKeDuAn> list = FXCollections.observableArrayList();
        for (DuAn da : das) {
            int tienDo = congViecDAO.calculateProgress(da.getMaDA());
            int soCV = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA()).count();
            int soNV = (int) pcs.stream().filter(pc -> pc.getMaDA() == da.getMaDA()).map(pc -> pc.getMaNV()).distinct().count();
            list.add(new ThongKeDuAn(da.getTenDA(), tienDo, soCV, soNV));
        }
        tableThongKe.setItems(list);
    }

    private void loadPieChart() {
        int chuaBD = (int) congViecDAO.getAll().stream().filter(cv -> "Chưa bắt đầu".equals(cv.getTrangThai())).count();
        int dangLam = (int) congViecDAO.getAll().stream().filter(cv -> "Đang làm".equals(cv.getTrangThai())).count();
        int hoanThanh = (int) congViecDAO.getAll().stream().filter(cv -> "Hoàn thành".equals(cv.getTrangThai())).count();
        PieChart.Data d1 = new PieChart.Data("Chưa bắt đầu", chuaBD);
        PieChart.Data d2 = new PieChart.Data("Đang làm", dangLam);
        PieChart.Data d3 = new PieChart.Data("Hoàn thành", hoanThanh);
        pieTrangThaiCV.setData(FXCollections.observableArrayList(d1, d2, d3));
    }

    public static class ThongKeDuAn {
        public String tenDuAn;
        public int tienDo;
        public int soLuongCV;
        public int soLuongNV;
        public ThongKeDuAn(String tenDuAn, int tienDo, int soLuongCV, int soLuongNV) {
            this.tenDuAn = tenDuAn;
            this.tienDo = tienDo;
            this.soLuongCV = soLuongCV;
            this.soLuongNV = soLuongNV;
        }
    }
}
