package cn.com.hdect.rfid.printer;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DataConverter {

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24);
        return value;
    }

    public static byte[] intToBytes(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (0xFF & value >> i * 8);
        }
        return bytes;
    }


    public static byte[] reverse(byte[] value) {
        byte[] ret = new byte[value.length];
        for (int i = value.length; i > 0; i--) {
            ret[value.length - i] = value[i - 1];
        }
        return ret;
    }

    /**
     * byte数组转16进制字符串
     *
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param hex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            String subStr = hex.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    /**
     * 明文字符串编码为16进制字符串
     *
     * @param source
     * @return
     */
    public static String toHexStr(String source) {
        StringBuffer buffer = new StringBuffer();
        return source.chars().mapToObj(c -> Integer.toHexString(c).toUpperCase())
                .map(s -> s.length() == 2 ? "00" + s : s)
                .collect(Collectors.joining());
    }

    /**
     * 16进制字符串解码为明文字符串
     *
     * @param hexStr
     * @return
     */
    public static String hexStrToStr(String hexStr) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < hexStr.length(); i = i + 4) {
            String t = hexStr.substring(i, i + 4);
            char ch = (char) Integer.parseInt(t, 16);
            buffer.append(ch);
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(hexToBytes("24")));
        System.out.println(Arrays.toString(hexToBytes("1512")));
        System.out.println(bytesToInt(new byte[]{1,0,0,0}, 0));
        for (byte b : intToBytes(1)) {
            System.out.println(b);
        }
    }

}

