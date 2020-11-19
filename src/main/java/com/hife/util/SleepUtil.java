package com.hife.util;

import com.alibaba.fastjson.JSONObject;
import com.hife.EDFUtils.EDFParser;
import com.hife.EDFUtils.EDFRecord;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/*
* 原始数据接收处理
* */
public class SleepUtil {

    public static JSONObject getReadFile(JSONObject json) throws ParseException {
        //String filterName = json.getString("filterName");
        String filterPath = json.getString("datUrl");
        String path = filterPath;

        String startTime = json.getString("startTime");
        String endTime = json.getString("endTime");

        EDFParser edf = new EDFParser(path);
        HashMap<String, String> header = edf.header;
        List<EDFRecord> records = edf.records;
        JSONObject time = getTime(header, records);

        //如果通过开始时间结束时间则截取 list = list.subList(0, 10);

        List<Integer> prList = new ArrayList<>();//心率原始数据
        List<Integer> SPOList = new ArrayList<>();//血氧原始数据
        List<Integer> rrList = new ArrayList<>(); //呼吸原始数据
        List<Integer> piList = new ArrayList<>(); //搏动指数原始数据
        List<Integer> PDRList = new ArrayList<>(); //呼吸波原始数据

        //此循环遍历原始数据并赋值PrList
        int kbzhi = getfuzhi(records, prList, rrList, piList, SPOList, PDRList);
        List<List<Long>> xyResult =  getyuanshi(time, SPOList, kbzhi);//血氧
        List<List<Long>> prResult = getyuanshi(time, prList, kbzhi);//脉率
        List<List<Long>> piResult = getyuanshi(time, piList, kbzhi);//搏动指数
        List<List<Long>> rrResult = getyuanshi(time, rrList, kbzhi);//呼吸
        List<List<Long>> pdrResult = getyuanshi(time, PDRList, kbzhi);//呼吸波

        //持续时长>=5分钟的最低血氧值
        String spozx = getZdxy(SPOList);

        JSONObject object = new JSONObject();
        object.put("prList",prList);//
        object.put("SPOList",SPOList);//
        object.put("piList",rrList);//搏动指数改为呼吸
        object.put("rrList",rrList);//呼吸
        object.put("xyResult",xyResult);//血氧
        object.put("prResult",prResult);//脉率
        object.put("piResult",piResult);//搏动指数
        object.put("rrResult",rrResult);//呼吸
        object.put("pdrResult",pdrResult);//呼吸波
        object.put("time",time);
        object.put("records",records);
        object.put("spozx",spozx);//持续时长>=5分钟的最低血氧值
        object.put("kbzhi",kbzhi);//最后空白前坐标
        return object;
    }

    private static String getZdxy(List<Integer> SPOList) {
        int spotype = 0; //>300
        String spozx = "";
        int zhi = 100;
        for (int i = 1; i < SPOList.size(); i++) {
            int shangzhi = SPOList.get(i-1);
            int benzhi = SPOList.get(i);
            if (benzhi==shangzhi){
                spotype++;
            }else {
                if (spotype>=300){
                    if (shangzhi<zhi && shangzhi!=0){
                        zhi=shangzhi;
                    }
                }
                spotype=0;
            }
        }
        spozx= String.valueOf(zhi);
        if (zhi == 100){
            spozx="";
        }
        return spozx;
    }

    private static List<List<Long>> getyuanshi(JSONObject time, List<Integer> prList,int kbzhi) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(time.getString("createTime"));
        List<List<Long>> list = new ArrayList<>();
        for (int i = 0; i <=kbzhi; i++) {
            List<Long> objects = new ArrayList<>();
            long ts = date.getTime();
            if (i == 0){
                i = 1;
            }
            objects.add(ts+i*1000);
            objects.add(Long.valueOf(prList.get(i)));
            list.add(objects);
        }
        return list;
    }

    private static int getfuzhi(List<EDFRecord> records, List<Integer> prList, List<Integer> RR, List<Integer> PI,List<Integer> SPOList,List<Integer> PDRList) {
        int pdrtype = 0;
        for (EDFRecord record : records) {
            short[] hr = record.HR;
            for (int i : hr) {
                if (i <= 250) {
                    prList.add(i);
                } else {
                    prList.add(0);
                }
            }
            short[] rr = record.PI;
            for (int i : rr) {
                if (i < 100) {
                    RR.add(i);
                } else {
                    RR.add(0);
                }
            }
            short[] pi = record.AccelarX;
            for (int i = 0; i < 2; i++) {
                int zhi = pi[i];
                if (zhi <= 100) {
                    PI.add(zhi);
                } else {
                    PI.add(0);
                }
            }
            short[] spO2 = record.SpO2;
            for (int i : spO2) {
                if (i < 100) {
                    SPOList.add(i);
                } else {
                    SPOList.add(0);
                }
            }
            short[] PDR = record.BreathWave;
            for (int i : PDR) {
                if (pdrtype >= (records.size())*2) {
                    break;
                }
                PDRList.add(i);

                pdrtype++;
            }

        }
        prList.set(0, 100);
        int kbzhi = 0;
        for (int i = prList.size()-1; i >= 0; i--) {
            int integer = prList.get(i);
            if (integer!=0){
                kbzhi = i+1;
                break;
            }
        }
        return kbzhi;
    }

    private static JSONObject getTime(HashMap<String, String> header, List<EDFRecord> records) throws ParseException {
        String startTime = header.get("记录的开始时间*").replace(".", ":");//时分秒 14.50.52
        String startDate = header.get("记录的开始日期*").replace(".", ":");//日期 11.01.20
        String[] strArr = startDate.split("\\:");
        String time = "20" + strArr[2] + "-" + strArr[1] + "-" + strArr[0];
        int miao = records.size() * 2; //睡眠总秒数
        int minute = miao / 60 % 60; //睡眠时间转换成分钟
        String createTime = time + " " + startTime;//开始时间 startDate=2020-01-11 15:09:52
        String endTime = getEndTime(createTime, miao);//结束时间
        JSONObject object = new JSONObject();
        object.put("createTime",createTime);
        object.put("endTime",endTime);
        return object;
    }

    public static String getEndTime(String createTime, int minute) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(createTime);
        String endTime =format.format(new Date(date .getTime() + minute*1000));
        return endTime;
    }
}
