package com.hife.EDFUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EDFParser {

    private String path;
    public HashMap<String, String> header;
    public List<EDFRecord> records;
    private String[] Datas;

    public EDFParser(String input) {
        this.path = input;
        try {
            //var stream =
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path));

            this.readHeader(stream);

            int recStart = Integer.parseInt(((String) header.get("页眉记录中的字节数")).trim());
            stream.read(new byte[recStart - EDFHeaderConstants.total]);

            this.readRecords(stream);
            stream.close();

        } catch (Exception any) {
            System.out.println(any);
        }
    }

    private void readHeader(BufferedInputStream stream) throws IOException {
        int limit = EDFHeaderConstants.specs.length;
        byte[] buffer;
        String key, value;
        int i = 0;
        header = new HashMap<String, String>();

        for (i = 0; i < limit; ++i) {
            buffer = new byte[EDFHeaderConstants.lenghts[i]];

            stream.read(buffer);
            key = EDFHeaderConstants.specs[i];
            value = new String(buffer).trim();
            header.put(key, value);
        }
    }

    private void readRecords(BufferedInputStream stream) throws IOException {
        int readLength = 0;
        for (int i = 0; i < EDFRecordConstants.lenghts.length; i++) {
            readLength += EDFRecordConstants.lenghts[i];
        }
        records = new ArrayList<EDFRecord>();
        byte[] buffer = new byte[readLength];
        while (stream.read(buffer) != -1) {
            EDFRecord record = new EDFRecord(buffer);
            records.add(record);
        }
        // int numberOfsignals = 6;
        // int[] numberSamples = getNumberSamples();
        // String[] keys = getLabels();
        // int locStart = paramToInt("bytesheader");
        // for (int i = 0; i < numberOfsignals; i++) {
        // // 读取并后移
        // byte[] buffer;
        // buffer = Arrays.copyOfRange(datas, locStart, locStart + numberSamples[i] *
        // 2);
        // locStart += numberSamples[i] * 2;
        // // 解析为short数组
        // List<Short> value = new ArrayList<Short>();
        // for (int j = 0; j < buffer.length; j = j + 2) {
        // var s = (short) (((int) (buffer[j + 1] & 0xff) << 8) + ((int) (buffer[j] &
        // 0xff) << 0));
        // value.add(s);
        // }
        // records.put(keys[i], value.toArray());
        // }
    }

}
