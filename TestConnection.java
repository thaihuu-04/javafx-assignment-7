import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        String[] urls = {
            "jdbc:sqlserver://localhost:1433;databaseName=QuanLyDuAn;user=sa;password=admin123;encrypt=false;trustServerCertificate=true;",
            "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=QuanLyDuAn;user=sa;password=admin123;encrypt=false;trustServerCertificate=true;",
            "jdbc:sqlserver://localhost:1433;databaseName=QuanLyDuAn;integratedSecurity=true;encrypt=false;trustServerCertificate=true;",
            "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=QuanLyDuAn;integratedSecurity=true;encrypt=false;trustServerCertificate=true;"
        };
        
        for (int i = 0; i < urls.length; i++) {
            System.out.println("\n[Test " + (i+1) + "] Trying: " + urls[i].substring(0, 60) + "...");
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Connection conn = DriverManager.getConnection(urls[i]);
                System.out.println("✓ KẾT NỐI THÀNH CÔNG!");
                
                // Test query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM NguoiDung");
                System.out.println("✓ Tìm thấy bảng NguoiDung:");
                while (rs.next()) {
                    System.out.println("  - User: " + rs.getString("TenDangNhap") + " | Pass: " + rs.getString("MatKhau"));
                }
                conn.close();
                System.out.println("\n>>> DÙNG CONNECTION STRING NÀY! <<<");
                break;
            } catch (Exception e) {
                System.out.println("✗ Thất bại: " + e.getMessage().split("\\.")[0]);
            }
        }
    }
}
