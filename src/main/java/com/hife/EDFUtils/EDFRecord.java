package com.hife.EDFUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class EDFRecord {
    public String Ender;// 结束符?
    public short[] BreathWave;// =PDR
    public short[] SpO2;// 血氧
    public short[] HR;// 脉率 =PR
    public short[] PI;// 可能算法处理过
    public short[] AccelarX;

    public EDFRecord(byte[] datas) {
        if (datas.length != EDFRecordConstants.total)
            return;

       // System.out.println(bytesToHexString(datas));

        int begin = 0;
        int end = EDFRecordConstants.lenghts[0];
        BreathWave = BytesToShorts(Arrays.copyOfRange(datas, begin, end));
        //System.out.println("BreathWave:" + bytesToHexString(Arrays.copyOfRange(datas, begin, end)));

        begin = end;
        end += EDFRecordConstants.lenghts[1];
        SpO2 = BytesToShorts(Arrays.copyOfRange(datas, begin, end));
        //System.out.println("SpO2:" + bytesToHexString(Arrays.copyOfRange(datas, begin, end)));

        begin = end;
        end += EDFRecordConstants.lenghts[2];
        HR = BytesToShorts(Arrays.copyOfRange(datas, begin, end));
        //System.out.println("HR:" + bytesToHexString(Arrays.copyOfRange(datas, begin, end)));

        begin = end;
        end += EDFRecordConstants.lenghts[3];
        PI = BytesToShorts(Arrays.copyOfRange(datas, begin, end));
        //System.out.println("PI:" + bytesToHexString(Arrays.copyOfRange(datas, begin, end)));

        begin = end;
        end += EDFRecordConstants.lenghts[4];
        AccelarX = BytesToShorts(Arrays.copyOfRange(datas, begin, end));
        //System.out.println("AccelarX:" + bytesToHexString(Arrays.copyOfRange(datas, begin, end)));

        begin = end;
        end += EDFRecordConstants.lenghts[5];
        Ender = bytesToHexString(Arrays.copyOfRange(datas, begin, end));
        // Ender += " | " + hexStr2Str(Header);
        Ender += " | " + BytesToShortStr(Arrays.copyOfRange(datas, begin, end));
        //System.out.println("Ender:" + bytesToHexString(Arrays.copyOfRange(datas, begin, end)));
        //var test =
        short[] test = BytesToShorts(Arrays.copyOfRange(datas, begin, end));

    }

    private String BytesToShortStr(byte[] inlet) {
        ByteBuffer buffer = ByteBuffer.wrap(inlet);
        int limit = inlet.length / 2;
        short[] outlet = new short[limit];

        StringBuilder sb = new StringBuilder();
        buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < limit; ++i) {
            sb.append(buffer.getShort() + " ");
        }

        return sb.toString();
    }

    private short[] BytesToShorts(byte[] inlet) {
        ByteBuffer buffer = ByteBuffer.wrap(inlet);
        int limit = inlet.length / 2;
        short[] outlet = new short[limit];

        buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < limit; ++i) {
            outlet[i] = buffer.getShort();
        }

        return outlet;
    }

    private short BytesToShort(byte[] inlet) {
        ByteBuffer buffer = ByteBuffer.wrap(inlet);
        int limit = inlet.length / 2;

        buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
        short outlet = buffer.getShort();

        return outlet;
    }

    private String[] bytesToHexStrings(byte[] src) {
        String[] list = new String[src.length];
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                list[i] = "0" + hv;
            } else {
                list[i] = hv;
            }
        }
        return list;
    }

    private String bytesToHexString(byte[] src) {
        StringBuffer sb = new StringBuffer("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
            if (i != src.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
}