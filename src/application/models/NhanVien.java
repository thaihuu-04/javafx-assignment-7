package application.models;

public class NhanVien {
    private int maNV;
    private String hoTen;
    private String chucVu;
    private String email;
    private String dienThoai;
    public NhanVien() {}
    public int getMaNV(){return maNV;} public void setMaNV(int v){maNV=v;}
    public String getHoTen(){return hoTen;} public void setHoTen(String v){hoTen=v;}
    public String getChucVu(){return chucVu;} public void setChucVu(String v){chucVu=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getDienThoai(){return dienThoai;} public void setDienThoai(String v){dienThoai=v;}
    @Override
    public String toString() {
        return hoTen;
    }

    public String getTenNV() {
        return hoTen;
    }
}
