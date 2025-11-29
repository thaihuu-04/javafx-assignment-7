package application.dao;

import application.models.NhanVien;
import application.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getInt("MaNV"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setChucVu(rs.getString("ChucVu"));
                nv.setEmail(rs.getString("Email"));
                nv.setDienThoai(rs.getString("DienThoai"));
                list.add(nv);
            }
        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien(HoTen, ChucVu, Email, DienThoai) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getChucVu());
            ps.setString(3, nv.getEmail());
            ps.setString(4, nv.getDienThoai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET HoTen=?, ChucVu=?, Email=?, DienThoai=? WHERE MaNV=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getChucVu());
            ps.setString(3, nv.getEmail());
            ps.setString(4, nv.getDienThoai());
            ps.setInt(5, nv.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int maNV) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Xóa công việc liên quan trước
            String sqlCV = "DELETE FROM CongViec WHERE NguoiPhuTrach=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCV)) {
                ps.setInt(1, maNV);
                ps.executeUpdate();
            }
            
            // Xóa phân công liên quan
            String sqlPC = "DELETE FROM PhanCong WHERE MaNV=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlPC)) {
                ps.setInt(1, maNV);
                ps.executeUpdate();
            }
            
            // Xóa nhân viên
            String sql = "DELETE FROM NhanVien WHERE MaNV=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, maNV);
                int result = ps.executeUpdate();
                if (result > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    public List<NhanVien> search(String keyword) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE HoTen LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getInt("MaNV"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setChucVu(rs.getString("ChucVu"));
                nv.setEmail(rs.getString("Email"));
                nv.setDienThoai(rs.getString("DienThoai"));
                list.add(nv);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
