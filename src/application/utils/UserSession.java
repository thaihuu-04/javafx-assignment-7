package application.utils;

public class UserSession {
    private static String tenDangNhap;
    private static String vaiTro;
    private static Integer maNV;

    public static void setSession(String tenDangNhap, String vaiTro, Integer maNV) {
        UserSession.tenDangNhap = tenDangNhap;
        UserSession.vaiTro = vaiTro;
        UserSession.maNV = maNV;
    }

    public static String getTenDangNhap() {
        return tenDangNhap;
    }

    public static String getVaiTro() {
        return vaiTro;
    }

    public static Integer getMaNV() {
        return maNV;
    }

    public static void clear() {
        tenDangNhap = null;
        vaiTro = null;
        maNV = null;
    }
}
