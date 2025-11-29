package application.models;

import java.time.LocalDate;

public class CongViec {
    private int maCV;
    private int maDA;
    private int maNV;
    private String tenCV;
    private String moTa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private int tienDo;
    private String trangThai;

    public CongViec() {}
    public int getMaCV(){return maCV;} public void setMaCV(int v){maCV=v;}
    public int getMaDA(){return maDA;} public void setMaDA(int v){maDA=v;}
    public int getMaNV(){return maNV;} public void setMaNV(int v){maNV=v;}
    public String getTenCV(){return tenCV;} public void setTenCV(String v){tenCV=v;}
    public String getMoTa(){return moTa;} public void setMoTa(String v){moTa=v;}
    public java.time.LocalDate getNgayBatDau(){return ngayBatDau;} public void setNgayBatDau(java.time.LocalDate d){ngayBatDau=d;}
    public java.time.LocalDate getNgayKetThuc(){return ngayKetThuc;} public void setNgayKetThuc(java.time.LocalDate d){ngayKetThuc=d;}
    public int getTienDo(){return tienDo;} public void setTienDo(int v){tienDo=v;}
    public String getTrangThai(){return trangThai;} public void setTrangThai(String v){trangThai=v;}
}
