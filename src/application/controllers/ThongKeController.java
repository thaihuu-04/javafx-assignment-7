
package application.controllers;

import application.dao.CongViecDAO;
import application.dao.DuAnDAO;
import application.dao.NhanVienDAO;
import application.dao.PhanCongDAO;
import application.models.DuAn;
import application.models.NhanVien;
import application.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThongKeController {
    @FXML private Label lblTongDuAn, lblTongNhanVien, lblTongCongViec, lblTongPhanCong;
    
    // Tổng quát tab
    @FXML private TableView<ThongKeDuAn> tableThongKe;
    @FXML private TableColumn<ThongKeDuAn, String> colTenDuAn;
    @FXML private TableColumn<ThongKeDuAn, Integer> colTienDo;
    @FXML private TableColumn<ThongKeDuAn, Integer> colSoLuongCV;
    @FXML private TableColumn<ThongKeDuAn, Integer> colSoLuongNV;
    @FXML private BarChart<String, Number> barChartTienDo;
    @FXML private CategoryAxis xAxisTienDo;
    @FXML private NumberAxis yAxisTienDo;
    
    // Công việc hoàn thành tab
    @FXML private TableView<ThongKeCVHoanThanh> tableCVHoanThanh;
    @FXML private TableColumn<ThongKeCVHoanThanh, String> colDuAnCV;
    @FXML private TableColumn<ThongKeCVHoanThanh, Integer> colCVHoanThanh;
    @FXML private TableColumn<ThongKeCVHoanThanh, Integer> colTongCVDA;
    @FXML private BarChart<String, Number> barChartCV;
    @FXML private CategoryAxis xAxisCV;
    @FXML private NumberAxis yAxisCV;
    
    // Khối lượng theo nhân viên tab
    @FXML private TableView<ThongKeNhanVien> tableNhanVienCV;
    @FXML private TableColumn<ThongKeNhanVien, String> colTenNV;
    @FXML private TableColumn<ThongKeNhanVien, Integer> colSoCVNV;
    @FXML private TableColumn<ThongKeNhanVien, Integer> colSoCVHoanThanhNV;
    @FXML private BarChart<String, Number> barChartNhanVien;
    @FXML private CategoryAxis xAxisNV;
    @FXML private NumberAxis yAxisNV;
    
    // Trạng thái dự án tab
    @FXML private PieChart pieTrangThaiDA;
    @FXML private TableView<ThongKeTrangThaiDA> tableTrangThaiDA;
    @FXML private TableColumn<ThongKeTrangThaiDA, String> colTrangThaiDA;
    @FXML private TableColumn<ThongKeTrangThaiDA, Integer> colSoDATheoTrangThai;
    
    // Trạng thái công việc PieChart
    @FXML private PieChart pieTrangThaiCV;

    private DuAnDAO duAnDAO = new DuAnDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private CongViecDAO congViecDAO = new CongViecDAO();
    private PhanCongDAO phanCongDAO = new PhanCongDAO();

    @FXML
    public void initialize() {
        updateOverview();
        setupTables();
        loadAllData();
    }

    private void updateOverview() {
        lblTongDuAn.setText("Tổng dự án: " + duAnDAO.getAll().size());
        lblTongNhanVien.setText("Tổng nhân viên: " + nhanVienDAO.getAll().size());
        lblTongCongViec.setText("Tổng công việc: " + congViecDAO.getAll().size());
        lblTongPhanCong.setText("Tổng phân công: " + phanCongDAO.getAll().size());
    }

    private void setupTables() {
        // Tổng quát tab
        colTenDuAn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tenDuAn));
        colTienDo.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().tienDo).asObject());
        colSoLuongCV.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soLuongCV).asObject());
        colSoLuongNV.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soLuongNV).asObject());

        // Công việc hoàn thành tab
        colDuAnCV.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tenDuAn));
        colCVHoanThanh.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().cvHoanThanh).asObject());
        colTongCVDA.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().tongCV).asObject());

        // Nhân viên tab
        colTenNV.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tenNV));
        colSoCVNV.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soCV).asObject());
        colSoCVHoanThanhNV.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().cvHoanThanh).asObject());

        // Trạng thái dự án tab
        colTrangThaiDA.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().trangThai));
        colSoDATheoTrangThai.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soDA).asObject());
    }

    private void loadAllData() {
        loadTongQuatData();
        loadCVHoanThanhData();
        loadNhanVienData();
        loadTrangThaiDAData();
        loadTrangThaiCVData();
    }

    private void loadTongQuatData() {
        List<DuAn> das = duAnDAO.getAll();
        List<application.models.CongViec> cvs = congViecDAO.getAll();
        List<application.models.PhanCong> pcs = phanCongDAO.getAll();
        javafx.collections.ObservableList<ThongKeDuAn> list = FXCollections.observableArrayList();
        
        XYChart.Series<String, Number> seriesTienDo = new XYChart.Series<>();
        seriesTienDo.setName("Tiến độ (%)");
        
        for (DuAn da : das) {
            int tienDo = congViecDAO.calculateProgress(da.getMaDA());
            int soCV = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA()).count();
            int soNV = (int) pcs.stream().filter(pc -> pc.getMaDA() == da.getMaDA()).map(pc -> pc.getMaNV()).distinct().count();
            list.add(new ThongKeDuAn(da.getTenDA(), tienDo, soCV, soNV));
            seriesTienDo.getData().add(new XYChart.Data<>(da.getTenDA(), tienDo));
        }
        tableThongKe.setItems(list);
        barChartTienDo.getData().clear();
        barChartTienDo.getData().add(seriesTienDo);
    }

    private void loadCVHoanThanhData() {
        List<DuAn> das = duAnDAO.getAll();
        List<application.models.CongViec> cvs = congViecDAO.getAll();
        javafx.collections.ObservableList<ThongKeCVHoanThanh> list = FXCollections.observableArrayList();
        
        XYChart.Series<String, Number> seriesCVHoanThanh = new XYChart.Series<>();
        seriesCVHoanThanh.setName("Công việc hoàn thành");
        XYChart.Series<String, Number> seriesTongCV = new XYChart.Series<>();
        seriesTongCV.setName("Tổng công việc");
        
        for (DuAn da : das) {
            int tongCV = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA()).count();
            int cvHoanThanh = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA() && "Hoàn thành".equals(cv.getTrangThai())).count();
            list.add(new ThongKeCVHoanThanh(da.getTenDA(), cvHoanThanh, tongCV));
            seriesCVHoanThanh.getData().add(new XYChart.Data<>(da.getTenDA(), cvHoanThanh));
            seriesTongCV.getData().add(new XYChart.Data<>(da.getTenDA(), tongCV));
        }
        tableCVHoanThanh.setItems(list);
        barChartCV.getData().clear();
        barChartCV.getData().add(seriesCVHoanThanh);
        barChartCV.getData().add(seriesTongCV);
    }

    private void loadNhanVienData() {
        List<NhanVien> nvs = nhanVienDAO.getAll();
        List<application.models.CongViec> cvs = congViecDAO.getAll();
        javafx.collections.ObservableList<ThongKeNhanVien> list = FXCollections.observableArrayList();
        
        XYChart.Series<String, Number> seriesSoCV = new XYChart.Series<>();
        seriesSoCV.setName("Số công việc");
        XYChart.Series<String, Number> seriesCVHoanThanh = new XYChart.Series<>();
        seriesCVHoanThanh.setName("Công việc hoàn thành");
        
        for (NhanVien nv : nvs) {
            int soCV = (int) cvs.stream().filter(cv -> cv.getMaNV() == nv.getMaNV()).count();
            int cvHoanThanh = (int) cvs.stream().filter(cv -> cv.getMaNV() == nv.getMaNV() && "Hoàn thành".equals(cv.getTrangThai())).count();
            if (soCV > 0) {
                list.add(new ThongKeNhanVien(nv.getTenNV(), soCV, cvHoanThanh));
                seriesSoCV.getData().add(new XYChart.Data<>(nv.getTenNV(), soCV));
                seriesCVHoanThanh.getData().add(new XYChart.Data<>(nv.getTenNV(), cvHoanThanh));
            }
        }
        tableNhanVienCV.setItems(list);
        barChartNhanVien.getData().clear();
        barChartNhanVien.getData().add(seriesSoCV);
        barChartNhanVien.getData().add(seriesCVHoanThanh);
    }

    private void loadTrangThaiDAData() {
        List<DuAn> das = duAnDAO.getAll();
        javafx.collections.ObservableList<ThongKeTrangThaiDA> list = FXCollections.observableArrayList();
        
        Map<String, Long> statusCount = das.stream()
            .collect(Collectors.groupingBy(DuAn::getTrangThai, Collectors.counting()));
        
        javafx.collections.ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : statusCount.entrySet()) {
            list.add(new ThongKeTrangThaiDA(entry.getKey(), entry.getValue().intValue()));
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        tableTrangThaiDA.setItems(list);
        pieTrangThaiDA.setData(pieData);
    }

    private void loadTrangThaiCVData() {
        int chuaBD = (int) congViecDAO.getAll().stream().filter(cv -> "Chưa bắt đầu".equals(cv.getTrangThai())).count();
        int dangLam = (int) congViecDAO.getAll().stream().filter(cv -> "Đang làm".equals(cv.getTrangThai())).count();
        int hoanThanh = (int) congViecDAO.getAll().stream().filter(cv -> "Hoàn thành".equals(cv.getTrangThai())).count();
        PieChart.Data d1 = new PieChart.Data("Chưa bắt đầu", chuaBD);
        PieChart.Data d2 = new PieChart.Data("Đang làm", dangLam);
        PieChart.Data d3 = new PieChart.Data("Hoàn thành", hoanThanh);
        pieTrangThaiCV.setData(FXCollections.observableArrayList(d1, d2, d3));
    }

    @FXML
    private void exportPDF() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Lưu báo cáo");
            fc.setInitialFileName("BaoCao_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
            
            File file = fc.showSaveDialog(null);
            if (file != null) {
                generateReport(file);
                AlertUtil.info("Xuất báo cáo", "Đã xuất báo cáo thành công tại:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", "Xuất báo cáo thất bại: " + e.getMessage());
        }
    }

    private void generateReport(File file) throws Exception {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("BÁO CÁO THỐNG KÊ DỰ ÁN\n");
            writer.write("Ngày xuất: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n\n");

            // Tổng quan
            writer.write("=== TỔNG QUAN ===\n");
            writer.write("Tổng dự án," + duAnDAO.getAll().size() + "\n");
            writer.write("Tổng nhân viên," + nhanVienDAO.getAll().size() + "\n");
            writer.write("Tổng công việc," + congViecDAO.getAll().size() + "\n");
            writer.write("Tổng phân công," + phanCongDAO.getAll().size() + "\n\n");

            // Tiến độ các dự án
            writer.write("=== TIẾN ĐỘ CÁC DỰ ÁN ===\n");
            writer.write("Tên dự án,Tiến độ (%),Số công việc,Số nhân viên\n");
            List<DuAn> das = duAnDAO.getAll();
            List<application.models.CongViec> cvs = congViecDAO.getAll();
            List<application.models.PhanCong> pcs = phanCongDAO.getAll();
            for (DuAn da : das) {
                int tienDo = congViecDAO.calculateProgress(da.getMaDA());
                int soCV = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA()).count();
                int soNV = (int) pcs.stream().filter(pc -> pc.getMaDA() == da.getMaDA()).map(pc -> pc.getMaNV()).distinct().count();
                writer.write(da.getTenDA() + "," + tienDo + "," + soCV + "," + soNV + "\n");
            }
            writer.write("\n");

            // Công việc hoàn thành
            writer.write("=== CÔNG VIỆC HOÀN THÀNH ===\n");
            writer.write("Tên dự án,Công việc hoàn thành,Tổng công việc\n");
            for (DuAn da : das) {
                int tongCV = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA()).count();
                int cvHoanThanh = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA() && "Hoàn thành".equals(cv.getTrangThai())).count();
                writer.write(da.getTenDA() + "," + cvHoanThanh + "," + tongCV + "\n");
            }
            writer.write("\n");

            // Khối lượng theo nhân viên
            writer.write("=== KHỐI LƯỢNG CÔNG VIỆC THEO NHÂN VIÊN ===\n");
            writer.write("Tên nhân viên,Số công việc,Số công việc hoàn thành\n");
            List<NhanVien> nvs = nhanVienDAO.getAll();
            for (NhanVien nv : nvs) {
                int soCV = (int) cvs.stream().filter(cv -> cv.getMaNV() == nv.getMaNV()).count();
                int cvHoanThanh = (int) cvs.stream().filter(cv -> cv.getMaNV() == nv.getMaNV() && "Hoàn thành".equals(cv.getTrangThai())).count();
                if (soCV > 0) {
                    writer.write(nv.getTenNV() + "," + soCV + "," + cvHoanThanh + "\n");
                }
            }
            writer.write("\n");

            // Trạng thái dự án
            writer.write("=== TRẠNG THÁI DỰ ÁN ===\n");
            writer.write("Trạng thái,Số dự án\n");
            Map<String, Long> statusCount = das.stream()
                .collect(Collectors.groupingBy(DuAn::getTrangThai, Collectors.counting()));
            for (Map.Entry<String, Long> entry : statusCount.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
            writer.write("\n");

            // Trạng thái công việc
            writer.write("=== TRẠNG THÁI CÔNG VIỆC ===\n");
            writer.write("Trạng thái,Số công việc\n");
            int chuaBD = (int) cvs.stream().filter(cv -> "Chưa bắt đầu".equals(cv.getTrangThai())).count();
            int dangLam = (int) cvs.stream().filter(cv -> "Đang làm".equals(cv.getTrangThai())).count();
            int hoanThanhCV = (int) cvs.stream().filter(cv -> "Hoàn thành".equals(cv.getTrangThai())).count();
            writer.write("Chưa bắt đầu," + chuaBD + "\n");
            writer.write("Đang làm," + dangLam + "\n");
            writer.write("Hoàn thành," + hoanThanhCV + "\n");
        }
    }

    // Inner classes
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

    public static class ThongKeCVHoanThanh {
        public String tenDuAn;
        public int cvHoanThanh;
        public int tongCV;
        public ThongKeCVHoanThanh(String tenDuAn, int cvHoanThanh, int tongCV) {
            this.tenDuAn = tenDuAn;
            this.cvHoanThanh = cvHoanThanh;
            this.tongCV = tongCV;
        }
    }

    public static class ThongKeNhanVien {
        public String tenNV;
        public int soCV;
        public int cvHoanThanh;
        public ThongKeNhanVien(String tenNV, int soCV, int cvHoanThanh) {
            this.tenNV = tenNV;
            this.soCV = soCV;
            this.cvHoanThanh = cvHoanThanh;
        }
    }

    public static class ThongKeTrangThaiDA {
        public String trangThai;
        public int soDA;
        public ThongKeTrangThaiDA(String trangThai, int soDA) {
            this.trangThai = trangThai;
            this.soDA = soDA;
        }
    }
}
