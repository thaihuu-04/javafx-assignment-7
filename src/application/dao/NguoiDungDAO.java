package application.dao;

import application.models.NguoiDung;
import application.utils.DatabaseConnection;

import java.sql.*;

public class NguoiDungDAO {

    public NguoiDung login(String username, String password) {
    	System.out.println("===========================================================================");
        String sql = "SELECT * FROM NguoiDung WHERE TenDangNhap = ? AND MatKhau = ?";
        System.out.println("=======" + sql);
        try (
        		
        		Connection conn = DatabaseConnection.getConnection();
        		PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setString(1, username);
	            ps.setString(2, password);
	
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                NguoiDung nd = new NguoiDung();
	                nd.setTenDangNhap(rs.getString("TenDangNhap"));
	                System.out.println("===========================================================================");
	                System.out.println(rs.getString("TenDangNhap"));
	                nd.setMatKhau(rs.getString("MatKhau"));
	                System.out.println(rs.getString("MatKhau"));
	                nd.setVaiTro(rs.getString("VaiTro"));
	                System.out.println("===========================================================================");
	                return nd;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
