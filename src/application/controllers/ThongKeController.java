
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.Desktop;

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
            fc.setTitle("Lưu báo cáo PDF");
            fc.setInitialFileName("BaoCao_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"));
            
            File file = fc.showSaveDialog(null);
            if (file != null) {
                generateHTMLReport(file);
                // Mở file HTML trong browser mặc định
                Desktop.getDesktop().open(file);
                AlertUtil.info("Xuất báo cáo", "Đã tạo báo cáo thành công!\n\nMở tệp bằng trình duyệt, chọn File > In > Lưu dưới dạng PDF");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Lỗi", "Xuất báo cáo thất bại: " + e.getMessage());
        }
    }

    private void generateHTMLReport(File file) throws Exception {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"vi\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("<title>Báo cáo thống kê dự án</title>\n");
        html.append("<style>\n");
        html.append("@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap');\n");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("body { font-family: 'Roboto', 'Times New Roman', serif; line-height: 1.6; color: #333; background: #f5f5f5; }\n");
        html.append(".container { width: 210mm; height: 297mm; background: white; margin: 10mm auto; padding: 15mm; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n");
        html.append("h1 { text-align: center; color: #1a5490; margin: 20px 0; font-size: 28px; border-bottom: 3px solid #1a5490; padding-bottom: 10px; }\n");
        html.append("h2 { color: white; margin: 20px 0 10px 0; padding: 8px 12px; background: linear-gradient(135deg, #1a5490 0%, #2980b9 100%); font-size: 16px; }\n");
        html.append("p { text-align: center; color: #666; margin-bottom: 20px; }\n");
        html.append(".summary { background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%); padding: 15px; margin: 20px 0; border-radius: 5px; border-left: 4px solid #28a745; }\n");
        html.append(".summary p { text-align: left; margin: 6px 0; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin: 15px 0; }\n");
        html.append("th { background: linear-gradient(135deg, #1a5490 0%, #2980b9 100%); color: white; padding: 10px; text-align: left; font-weight: 500; }\n");
        html.append("td { padding: 8px 10px; border-bottom: 1px solid #ddd; }\n");
        html.append("tr:nth-child(even) { background: #f9f9f9; }\n");
        html.append("tr:hover { background: #f0f0f0; }\n");
        html.append(".footer { text-align: center; margin-top: 30px; padding-top: 15px; border-top: 1px solid #ddd; color: #999; font-size: 11px; }\n");
        html.append(".page-break { page-break-after: always; margin-top: 40px; }\n");
        html.append("@media print { body { background: white; } .container { box-shadow: none; margin: 0; width: 100%; height: auto; } }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class=\"container\">\n");
        html.append("<h1>📊 BÁO CÁO THỐNG KÊ DỰ ÁN</h1>\n");
        html.append("<p><em>Ngày xuất:</em> <strong>").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("</strong></p>\n");

        // Tổng quan
        html.append("<div class=\"summary\">\n");
        html.append("<h2>📋 TỔNG QUAN HỆ THỐNG</h2>\n");
        html.append("<p><strong>📁 Tổng số dự án:</strong> ").append(duAnDAO.getAll().size()).append(" dự án</p>\n");
        html.append("<p><strong>👥 Tổng số nhân viên:</strong> ").append(nhanVienDAO.getAll().size()).append(" nhân viên</p>\n");
        html.append("<p><strong>✓ Tổng số công việc:</strong> ").append(congViecDAO.getAll().size()).append(" công việc</p>\n");
        html.append("<p><strong>🔗 Tổng số phân công:</strong> ").append(phanCongDAO.getAll().size()).append(" phân công</p>\n");
        html.append("</div>\n");

        // Tiến độ các dự án
        html.append("<h2>📈 TIẾN ĐỘ CÁC DỰ ÁN</h2>\n");
        html.append("<table>\n");
        html.append("<thead><tr><th style=\"width: 40%;\">Tên dự án</th><th style=\"width: 20%;\">Tiến độ (%)</th><th style=\"width: 20%;\">Số CV</th><th style=\"width: 20%;\">Số NV</th></tr></thead>\n");
        html.append("<tbody>\n");
        List<DuAn> das = duAnDAO.getAll();
        List<application.models.CongViec> cvs = congViecDAO.getAll();
        List<application.models.PhanCong> pcs = phanCongDAO.getAll();
        for (DuAn da : das) {
            int tienDo = congViecDAO.calculateProgress(da.getMaDA());
            int soCV = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA()).count();
            int soNV = (int) pcs.stream().filter(pc -> pc.getMaDA() == da.getMaDA()).map(pc -> pc.getMaNV()).distinct().count();
            html.append("<tr><td>").append(escapeHtml(da.getTenDA())).append("</td><td>").append(tienDo).append("%</td><td>").append(soCV).append("</td><td>").append(soNV).append("</td></tr>\n");
        }
        html.append("</tbody>\n</table>\n");

        // Công việc hoàn thành
        html.append("<h2>✅ CÔNG VIỆC HOÀN THÀNH</h2>\n");
        html.append("<table>\n");
        html.append("<thead><tr><th style=\"width: 50%;\">Tên dự án</th><th style=\"width: 25%;\">Hoàn thành</th><th style=\"width: 25%;\">Tổng cộng</th></tr></thead>\n");
        html.append("<tbody>\n");
        for (DuAn da : das) {
            int tongCV = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA()).count();
            int cvHoanThanh = (int) cvs.stream().filter(cv -> cv.getMaDA() == da.getMaDA() && "Hoàn thành".equals(cv.getTrangThai())).count();
            html.append("<tr><td>").append(escapeHtml(da.getTenDA())).append("</td><td>").append(cvHoanThanh).append("</td><td>").append(tongCV).append("</td></tr>\n");
        }
        html.append("</tbody>\n</table>\n");

        html.append("<div class=\"page-break\"></div>\n");

        // Khối lượng theo nhân viên
        html.append("<h2>👤 KHỐI LƯỢNG CÔNG VIỆC THEO NHÂN VIÊN</h2>\n");
        html.append("<table>\n");
        html.append("<thead><tr><th style=\"width: 50%;\">Tên nhân viên</th><th style=\"width: 25%;\">Số CV</th><th style=\"width: 25%;\">Hoàn thành</th></tr></thead>\n");
        html.append("<tbody>\n");
        List<NhanVien> nvs = nhanVienDAO.getAll();
        for (NhanVien nv : nvs) {
            int soCV = (int) cvs.stream().filter(cv -> cv.getMaNV() == nv.getMaNV()).count();
            int cvHoanThanh = (int) cvs.stream().filter(cv -> cv.getMaNV() == nv.getMaNV() && "Hoàn thành".equals(cv.getTrangThai())).count();
            if (soCV > 0) {
                html.append("<tr><td>").append(escapeHtml(nv.getTenNV())).append("</td><td>").append(soCV).append("</td><td>").append(cvHoanThanh).append("</td></tr>\n");
            }
        }
        html.append("</tbody>\n</table>\n");

        // Trạng thái dự án
        html.append("<h2>🎯 TRẠNG THÁI DỰ ÁN</h2>\n");
        html.append("<table>\n");
        html.append("<thead><tr><th style=\"width: 70%;\">Trạng thái</th><th style=\"width: 30%;\">Số dự án</th></tr></thead>\n");
        html.append("<tbody>\n");
        Map<String, Long> statusCount = das.stream()
            .collect(Collectors.groupingBy(DuAn::getTrangThai, Collectors.counting()));
        for (Map.Entry<String, Long> entry : statusCount.entrySet()) {
            html.append("<tr><td>").append(escapeHtml(entry.getKey())).append("</td><td>").append(entry.getValue()).append("</td></tr>\n");
        }
        html.append("</tbody>\n</table>\n");

        html.append("<div class=\"page-break\"></div>\n");

        // Trạng thái công việc
        html.append("<h2>🔄 TRẠNG THÁI CÔNG VIỆC</h2>\n");
        html.append("<table>\n");
        html.append("<thead><tr><th style=\"width: 70%;\">Trạng thái</th><th style=\"width: 30%;\">Số công việc</th></tr></thead>\n");
        html.append("<tbody>\n");
        int chuaBD = (int) cvs.stream().filter(cv -> "Chưa bắt đầu".equals(cv.getTrangThai())).count();
        int dangLam = (int) cvs.stream().filter(cv -> "Đang làm".equals(cv.getTrangThai())).count();
        int hoanThanhCV = (int) cvs.stream().filter(cv -> "Hoàn thành".equals(cv.getTrangThai())).count();
        html.append("<tr><td>⏳ Chưa bắt đầu</td><td>").append(chuaBD).append("</td></tr>\n");
        html.append("<tr><td>⚙️ Đang làm</td><td>").append(dangLam).append("</td></tr>\n");
        html.append("<tr><td>✔️ Hoàn thành</td><td>").append(hoanThanhCV).append("</td></tr>\n");
        html.append("</tbody>\n</table>\n");

        html.append("<div class=\"footer\">\n");
        html.append("<p>Báo cáo được tạo tự động vào ngày ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("</p>\n");
        html.append("<p style=\"margin-top: 10px;\">© Hệ Thống Quản Lý Dự Án - Thống Kê Báo Cáo</p>\n");
        html.append("</div>\n");

        html.append("</div>\n");
        html.append("</body>\n</html>\n");

        // Ghi file HTML
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(html.toString());
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
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
