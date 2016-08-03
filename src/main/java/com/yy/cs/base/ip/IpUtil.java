package com.yy.cs.base.ip;

public class IpUtil {

    private static int getBit(int i, boolean r) {
        return r ? (8 * i) : (24 - 8 * i);
    }

    public static byte[] atob(String ip) {
        return atob(ip, false);
    }

    public static byte[] atob(String ip, boolean reverse) {
        String[] t = ip.split("\\.");
        byte[] b = new byte[] { 0, 0, 0, 0 };
        for (int i = 0; i < 4; i++) {
            int bit = getBit(i, reverse);
            int m = Integer.valueOf(t[i]) << bit;
            m = m >> (24 - (8 * i));
            b[i] = (byte) (m & 0xff);
        }
        return b;
    }

    public static long atoi(String ip, boolean reverse) {
        String[] t = ip.split("\\.");
        long ipNumbers = 0;
        for (int i = 0; i < 4; i++) {
            int bit = getBit(i,reverse);
            ipNumbers += Integer.valueOf(t[i]) << bit;
        }
        return ipNumbers;
    }

    public static long atoi(String ip) {
        return atoi(ip, false);
    }

    public static String itoa(long ipNumber) {
        return itoa(ipNumber, false);
    }

    public static String itoa(long ipNumber, boolean reverse) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int bit = getBit(i,reverse);
            long s = (ipNumber & (0xff << bit)) >> bit;
            s = s & 0x00ff;
            sb.append(s).append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String numberHex(long num) {
        StringBuilder sb = new StringBuilder();
        if (num == 0) {
            return "0x0";
        }
        for (int i = 0; i < 4; i++) {
            long s = (num & (0xff << (24 - (8 * i)))) >> (24 - (8 * i));
            s = s & 0x00ff;
            String hex = Integer.toHexString((int) s);
            if (hex.length() < 2) {
                sb.append("0").append(hex);
            } else {
                sb.append(hex);
            }
        }
        return "0x" + sb.toString();
    }

    public static String rangeIpToSectionIp(String ip1, String ip2) {
        long iIp1 = IpUtil.atoi(ip1);
        long iIp2 = IpUtil.atoi(ip2);
        long s = iIp1 ^ iIp2;
        int section = 32;
        for (int i = 0; i < 32; i++) {
            long m = s & (1 << (31 - i));
            if (m != 0) {
                section = i;
                break;
            }
        }
        return ip1 + "/" + section;
    }

    public static void main(String[] args) {
        long t = 5447020147l;
        System.out.println(itoa(t, true));
        t = atoi("115.238.170.68", true);
        System.out.println(itoa(t, true));
    }
}
