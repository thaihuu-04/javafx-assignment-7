package application.models;

import java.time.LocalDate;

public class DuAn {
    private int maDA;
    private String tenDA;
    private String moTa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String trangThai;
    private String nguoiQuanLy;

    public DuAn() {}
    public int getMaDA(){return maDA;} public void setMaDA(int v){maDA=v;}
    public String getTenDA(){return tenDA;} public void setTenDA(String v){tenDA=v;}
    public String getMoTa(){return moTa;} public void setMoTa(String v){moTa=v;}
    public java.time.LocalDate getNgayBatDau(){return ngayBatDau;} public void setNgayBatDau(java.time.LocalDate d){ngayBatDau=d;}
    public java.time.LocalDate getNgayKetThuc(){return ngayKetThuc;} public void setNgayKetThuc(java.time.LocalDate d){ngayKetThuc=d;}
    public String getTrangThai(){return trangThai;} public void setTrangThai(String v){trangThai=v;}
    public String getNguoiQuanLy(){return nguoiQuanLy;} public void setNguoiQuanLy(String v){nguoiQuanLy=v;}
    @Override
    public String toString() {
        return tenDA;
    }
}
