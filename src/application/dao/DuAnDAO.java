package application.dao;

import application.models.DuAn;
import application.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuAnDAO {

    public List<DuAn> getAll() {
        List<DuAn> list = new ArrayList<>();
        String sql = "SELECT * FROM DuAn";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DuAn da = new DuAn();
                da.setMaDA(rs.getInt("MaDA"));
                da.setTenDA(rs.getString("TenDA"));
                Date bd = rs.getDate("NgayBatDau");
                Date kt = rs.getDate("NgayKetThuc");
                if (bd != null) da.setNgayBatDau(bd.toLocalDate());
                if (kt != null) da.setNgayKetThuc(kt.toLocalDate());
                da.setTrangThai(rs.getString("TrangThai"));
                da.setNguoiQuanLy(rs.getString("NguoiQuanLy"));
                list.add(da);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(DuAn da) {
        String sql = "INSERT INTO DuAn(TenDA, MoTa, NgayBatDau, NgayKetThuc, TrangThai, NguoiQuanLy) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, da.getTenDA());
            ps.setString(2, da.getMoTa());
            if (da.getNgayBatDau() != null) ps.setDate(3, Date.valueOf(da.getNgayBatDau())); else ps.setNull(3, Types.DATE);
            if (da.getNgayKetThuc() != null) ps.setDate(4, Date.valueOf(da.getNgayKetThuc())); else ps.setNull(4, Types.DATE);
            ps.setString(5, da.getTrangThai());
            ps.setString(6, da.getNguoiQuanLy());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(DuAn da) {
        String sql = "UPDATE DuAn SET TenDA=?, MoTa=?, NgayBatDau=?, NgayKetThuc=?, TrangThai=?, NguoiQuanLy=? WHERE MaDA=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, da.getTenDA());
            ps.setString(2, da.getMoTa());
            if (da.getNgayBatDau() != null) ps.setDate(3, Date.valueOf(da.getNgayBatDau())); else ps.setNull(3, Types.DATE);
            if (da.getNgayKetThuc() != null) ps.setDate(4, Date.valueOf(da.getNgayKetThuc())); else ps.setNull(4, Types.DATE);
            ps.setString(5, da.getTrangThai());
            ps.setString(6, da.getNguoiQuanLy());
            ps.setInt(7, da.getMaDA());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maDA) {
        String sql = "DELETE FROM DuAn WHERE MaDA=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDA);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public java.util.List<DuAn> search(String keyword) {
        java.util.List<DuAn> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM DuAn WHERE TenDA LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DuAn da = new DuAn();
                da.setMaDA(rs.getInt("MaDA"));
                da.setTenDA(rs.getString("TenDA"));
                Date bd = rs.getDate("NgayBatDau");
                Date kt = rs.getDate("NgayKetThuc");
                if (bd != null) da.setNgayBatDau(bd.toLocalDate());
                if (kt != null) da.setNgayKetThuc(kt.toLocalDate());
                da.setTrangThai(rs.getString("TrangThai"));
                da.setNguoiQuanLy(rs.getString("NguoiQuanLy"));
                list.add(da);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
