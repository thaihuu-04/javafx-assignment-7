package application.dao;

import application.models.CongViec;
import application.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CongViecDAO {

    public List<CongViec> getAll() {
        List<CongViec> list = new ArrayList<>();
        String sql = "SELECT * FROM CongViec";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CongViec cv = new CongViec();
                cv.setMaCV(rs.getInt("MaCV"));
                cv.setMaDA(rs.getInt("MaDA"));
                cv.setMaNV(rs.getInt("NguoiPhuTrach")==0?0:rs.getInt("NguoiPhuTrach"));
                cv.setTenCV(rs.getString("TenCV"));
                cv.setMoTa(rs.getString("MoTa"));
                Date bd = rs.getDate("NgayBatDau");
                Date kt = rs.getDate("NgayKetThuc");
                if (bd != null) cv.setNgayBatDau(bd.toLocalDate());
                if (kt != null) cv.setNgayKetThuc(kt.toLocalDate());
                cv.setTienDo(rs.getInt("TienDo"));
                cv.setTrangThai(rs.getString("TrangThai"));
                list.add(cv);
            }
        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }

    public List<CongViec> getAllByProject(int maDA) {
        List<CongViec> list = new ArrayList<>();
        String sql = "SELECT * FROM CongViec WHERE MaDA=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDA);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CongViec cv = new CongViec();
                cv.setMaCV(rs.getInt("MaCV"));
                cv.setMaDA(rs.getInt("MaDA"));
                cv.setMaNV(rs.getInt("NguoiPhuTrach"));
                cv.setTenCV(rs.getString("TenCV"));
                cv.setMoTa(rs.getString("MoTa"));
                Date bd = rs.getDate("NgayBatDau");
                Date kt = rs.getDate("NgayKetThuc");
                if (bd != null) cv.setNgayBatDau(bd.toLocalDate());
                if (kt != null) cv.setNgayKetThuc(kt.toLocalDate());
                cv.setTienDo(rs.getInt("TienDo"));
                cv.setTrangThai(rs.getString("TrangThai"));
                list.add(cv);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(CongViec cv) {
        String sql = "INSERT INTO CongViec(TenCV, MaDA, NguoiPhuTrach, NgayBatDau, NgayKetThuc, TrangThai, TienDo, MoTa) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cv.getTenCV());
            ps.setInt(2, cv.getMaDA());
            ps.setInt(3, cv.getMaNV());
            if (cv.getNgayBatDau() != null) ps.setDate(4, Date.valueOf(cv.getNgayBatDau())); else ps.setNull(4, Types.DATE);
            if (cv.getNgayKetThuc() != null) ps.setDate(5, Date.valueOf(cv.getNgayKetThuc())); else ps.setNull(5, Types.DATE);
            ps.setString(6, cv.getTrangThai());
            ps.setInt(7, cv.getTienDo());
            ps.setString(8, cv.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(CongViec cv) {
        String sql = "UPDATE CongViec SET TenCV=?, MaDA=?, NguoiPhuTrach=?, NgayBatDau=?, NgayKetThuc=?, TrangThai=?, TienDo=?, MoTa=? WHERE MaCV=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cv.getTenCV());
            ps.setInt(2, cv.getMaDA());
            ps.setInt(3, cv.getMaNV());
            if (cv.getNgayBatDau() != null) ps.setDate(4, Date.valueOf(cv.getNgayBatDau())); else ps.setNull(4, Types.DATE);
            if (cv.getNgayKetThuc() != null) ps.setDate(5, Date.valueOf(cv.getNgayKetThuc())); else ps.setNull(5, Types.DATE);
            ps.setString(6, cv.getTrangThai());
            ps.setInt(7, cv.getTienDo());
            ps.setString(8, cv.getMoTa());
            ps.setInt(9, cv.getMaCV());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int maCV) {
        String sql = "DELETE FROM CongViec WHERE MaCV=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public List<CongViec> search(String keyword) {
        List<CongViec> list = new ArrayList<>();
        String sql = "SELECT * FROM CongViec WHERE TenCV LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CongViec cv = new CongViec();
                cv.setMaCV(rs.getInt("MaCV"));
                cv.setMaDA(rs.getInt("MaDA"));
                cv.setMaNV(rs.getInt("NguoiPhuTrach"));
                cv.setTenCV(rs.getString("TenCV"));
                cv.setMoTa(rs.getString("MoTa"));
                Date bd = rs.getDate("NgayBatDau");
                Date kt = rs.getDate("NgayKetThuc");
                if (bd != null) cv.setNgayBatDau(bd.toLocalDate());
                if (kt != null) cv.setNgayKetThuc(kt.toLocalDate());
                cv.setTienDo(rs.getInt("TienDo"));
                cv.setTrangThai(rs.getString("TrangThai"));
                list.add(cv);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int calculateProgress(int maDA) {
        String sql = "SELECT AVG(TienDo) AS AvgProgress FROM CongViec WHERE MaDA=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDA);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("AvgProgress");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
