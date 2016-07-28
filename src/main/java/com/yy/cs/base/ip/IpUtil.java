package com.yy.cs.base.ip;

public class IpUtil {
    public static byte[] atob(String ip) {
        String[] t = ip.split("\\.");
        byte[] b = new byte[] { 0, 0, 0, 0 };
        for (int i = 0; i < 4; i++) {
            int m = Integer.valueOf(t[i]) << (24 - (8 * i));
            m = m >> (24 - (8 * i));
            b[i] = (byte) (m & 0xff);
        }
        return b;
    }

    public static long atoi(String ip) {
        String[] t = ip.split("\\.");
        long ipNumbers = 0;
        for (int i = 0; i < 4; i++) {
            ipNumbers += Integer.valueOf(t[i]) << (24 - (8 * i));
        }
        return ipNumbers;
    }

    public static long atoiR(String ip) {
        String[] t = ip.split("\\.");
        long ipNumbers = 0;
        for (int i = 0; i < 4; i++) {
            ipNumbers += Integer.valueOf(t[i]) << (8 * i);
        }
        return ipNumbers | (1l << 32);
    }

    public static String itoa(long ipNumber) {
        StringBuilder sb = new StringBuilder();
        boolean reverse = (ipNumber >> 32 == 1) ? true : false;
        for (int i = 0; i < 4; i++) {
            int bit = reverse ? (8 * i) : (24 - 8 * i);
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
        System.out.println(itoa(t));
        t = atoiR("115.238.170.68");
        System.out.println(itoa(t));
    }
}
