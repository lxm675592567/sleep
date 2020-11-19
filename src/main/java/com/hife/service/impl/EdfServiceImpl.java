package com.hife.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hife.EDFUtils.EDFRecord;
import com.hife.dao.SleepMapper;
import com.hife.entity.SleepRecord;
import com.hife.service.EdfService;
import com.hife.util.EdfUtil;
import com.hife.util.SleepResultUtil;
import com.hife.util.SleepUtil;
import com.hife.util.StringUtil;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EdfServiceImpl implements EdfService {

    @Autowired
    private SleepMapper sleepMapper;

    @Override
    public String getWave(JSONObject jsonObject) throws ParseException {
        jsonObject.put("type","1"); //type 1json 2dat
        String result = EdfUtil.readJsonFile(jsonObject);
        return result;
    }

    @Override
    public String getDatWave(JSONObject jsonObject) throws ParseException {
        jsonObject.put("type","2"); //type 1json 2dat
        String result = EdfUtil.readJsonFile(jsonObject);
        return result;
    }

    @Override
    public List getFileName() {
        return  folderMethod2("D:\\项目\\新数据");
    }

    @Override
    public List getFileDatName() {
        return  folderMethod2("D:\\项目\\新数据\\dat");
    }

    @Override
    public String getDeepSleepWave(JSONObject jsonObject) throws ParseException {
        String result = EdfUtil.readJsonFile(jsonObject);
        return result;
    }

    @Override
    public JSONObject getSleepBasicValue(JSONObject jsonObject) throws ParseException, JSONException {
        //获取基本信息接口
        //需要传过个人信息,根据guid查询基本信息,根据filename获得基本数据
        String sleepId = jsonObject.getString("sleepId");
        SleepRecord sleepRecord = sleepMapper.getSleepId(sleepId);
        JSONObject readFile = SleepUtil.getReadFile(jsonObject);//获得原始数据
        List<EDFRecord> records = (List<EDFRecord>) readFile.get("records");
        JSONObject time = readFile.getJSONObject("time");
        List<Integer> rrList = (List<Integer>) readFile.get("rrList");
        JSONObject sleepResult = SleepResultUtil.getSleepResult(readFile);
        List<Integer> prListCopy = (List<Integer>) sleepResult.get("prListCopy");
        List<List<Object>> qxList = (List<List<Object>>) sleepResult.get("qxList");
        List<List<Integer>> listTime = (List<List<Integer>>) sleepResult.get("listTime");
        JSONObject jcszjs = SleepResultUtil.getJcszjs(records, time, rrList, prListCopy, listTime, qxList);
        jcszjs.put("sleepRecord",sleepRecord);
        jcszjs.put("BMI",sleepRecord.getBMI());
        jcszjs.put("spozx",readFile.getString("spozx"));
        return jcszjs;
    }

    @Override
    public JSONObject getSleepDataMap(JSONObject jsonObject) throws ParseException, JSONException {
        JSONObject readFile = SleepUtil.getReadFile(jsonObject);
        String type = jsonObject.getString("type");
        List<Integer> xyResult = (List<Integer>) readFile.get("xyResult");
        List<Integer> prResult = (List<Integer>) readFile.get("prResult");
        List<Integer> piResult = (List<Integer>) readFile.get("piResult");//搏动指数
        List<Integer> rrResult = (List<Integer>) readFile.get("rrResult");//呼吸
        List<Integer> pdrResult = (List<Integer>) readFile.get("pdrResult");//呼吸波
        JSONObject sleepResult = SleepResultUtil.getSleepResult(readFile);
        List<List<Object>> results = (List<List<Object>>)sleepResult.get("result");//结果
        List<List<Object>> xlResult = (List<List<Object>>) sleepResult.get("xlResult");//心率
        JSONObject smlxResult =  sleepResult.getJSONObject("smlxResult");
        JSONObject object = new JSONObject();
        if (StringUtil.stringIsNotNull(type)) {
            if (type.equals("xlResult")) {
                object.put("xlResult", xlResult); //心率
            } else if (type.equals("smlxResult")) {
                object.put("smlxResult", smlxResult); //睡眠类型
            } else if (type.equals("results")) {
                object.put("results", results);//结果
            } else if (type.equals("xyResult")) {
                object.put("xyResult", xyResult);//血氧
            } else if (type.equals("prResult")) {
                object.put("prResult", prResult);//脉率
            } else if (type.equals("piResult")) {
                object.put("piResult", piResult);//搏动指数
            } else if (type.equals("rrResult")) {
                object.put("rrResult", rrResult);//呼吸
            } else if (type.equals("pdrResult")) {
                object.put("pdrResult", pdrResult);//呼吸波
            }
        }else {
            object.put("xyResult",xyResult);object.put("prResult",prResult);object.put("piResult",piResult);object.put("rrResult",rrResult);object.put("pdrResult",pdrResult);object.put("results",results);object.put("xlResult",xlResult); object.put("smlxResult",smlxResult); //睡眠类型
        }

//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject1.put("smlxResult",smlxResult);
//        System.out.println("smlxResult"+smlxResult);

        return object;
    }

    @Override
    public long deleteSleepAdvice(String sleepId) {
        Document queryDoc = new Document("sleepId", sleepId);
        return this.sleepMapper.deleteSleepAdvice(queryDoc);
    }


    public static List folderMethod2(String path) {
        File file = new File(path);
        List<String> list = new ArrayList<>();
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        folderMethod2(file2.getAbsolutePath());
                    }
                    String temp[]= file2.getAbsolutePath().split("\\\\");
                    String fileNameNow=temp[temp.length-1];
                    list.add(fileNameNow);
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        return list;
    }

    public static int getSecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0,length-3));
        } else {
            return 0;
        }
    }








}
