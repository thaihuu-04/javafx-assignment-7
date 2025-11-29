package application.dao;

import application.models.PhanCong;
import application.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhanCongDAO {

    public List<PhanCong> getAll() {
        List<PhanCong> list = new ArrayList<>();
        String sql = "SELECT * FROM PhanCong";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhanCong pc = new PhanCong();
                pc.setMaPC(rs.getInt("MaPC"));
                pc.setMaDA(rs.getInt("MaDA"));
                pc.setMaNV(rs.getInt("MaNV"));
                pc.setVaiTro(rs.getString("VaiTro"));
                list.add(pc);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(PhanCong pc) {
        String sql = "INSERT INTO PhanCong(MaDA, MaNV, VaiTro) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pc.getMaDA());
            ps.setInt(2, pc.getMaNV());
            ps.setString(3, pc.getVaiTro());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int maPC) {
        String sql = "DELETE FROM PhanCong WHERE MaPC=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPC);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
