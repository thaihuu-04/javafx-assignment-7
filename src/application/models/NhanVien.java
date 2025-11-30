package application.models;

public class NhanVien {
    private int maNV;
    private String hoTen;
    private String chucVu;
    private String email;
    private String dienThoai;
    private String username;
    private String matKhau;
    private String vaiTro;
    public NhanVien() {}
    public int getMaNV(){return maNV;} public void setMaNV(int v){maNV=v;}
    public String getHoTen(){return hoTen;} public void setHoTen(String v){hoTen=v;}
    public String getChucVu(){return chucVu;} public void setChucVu(String v){chucVu=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getDienThoai(){return dienThoai;} public void setDienThoai(String v){dienThoai=v;}
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getMatKhau() { return matKhau; } public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getVaiTro() { return vaiTro; } public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    @Override
    public String toString() {
        return hoTen;
    }

    public String getTenNV() {
        return hoTen;
    }
}