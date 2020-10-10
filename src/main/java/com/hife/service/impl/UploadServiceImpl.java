package com.hife.service.impl;

import com.hife.EDFUtils.EDFRecord;
import com.hife.dao.SleepMapper;
import com.hife.dao.UploadMapper;
import com.hife.entity.SleepRecord;
import com.hife.service.EdfService;
import com.hife.service.UploadService;
import com.hife.util.*;
import lombok.RequiredArgsConstructor;
import net.sf.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UploadServiceImpl implements UploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    private final UploadMapper uploadMapper;

    private final SleepMapper sleepMapper;

    private final EdfService edfService;

    private static String separator = "/";

    @Override
    public String getDat(MultipartFile file) throws ParseException {
        return update(file);
    }

    @Override
    public String SaveDatValue(SleepRecord record) throws ParseException, JSONException {
        List<String> datUrl = record.getDatUrl();
        for (String s : datUrl) {
            record.setSleepId(CommUtil.getGuid()); //生成主键
            record.setUrl(s);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(System.currentTimeMillis());
            record.setCreateTime(formatter.format(date));//生成日期
            if (StringUtil.stringIsNotNull(record.getBirthday())) {
                //Date parse = formatter.parse(record.getBirthday());
                int year = DateUtil.getAge(formatter.parse(record.getBirthday())).getYear();
                record.setAge(year);
            }
            if (StringUtil.stringIsNotNull(record.getHeight()) || StringUtil.stringIsNotNull(record.getWeight())) {
                double height = Double.valueOf(record.getHeight()) * 0.01;
                double weight = Double.valueOf(record.getWeight());
                DecimalFormat df = new DecimalFormat("#0.0");
                String bmi = df.format(weight / (height * height));
                if (height <= 0) {
                    bmi = "0.0";
                }
                record.setBMI(bmi);
            }
            record.setDatUrl(null);
            uploadMapper.SaveDatValue(record);
            updateData(record);
        }
        return "200";
    }

    public void updateData(SleepRecord record) throws ParseException, JSONException {
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        /*json.put("sleepId", "eeb7cec06a4445f1ba8faadfef94f531");
        json.put("datUrl", "D:/file/dat/黄智.dat");*/
        json.put("sleepId", record.getSleepId());
        json.put("datUrl", record.getUrl());
        com.alibaba.fastjson.JSONObject sleepBasicValue = edfService.getSleepBasicValue(json);
        com.alibaba.fastjson.JSONObject cszt = sleepBasicValue.getJSONObject("cszt");
        double smSleep = sleepBasicValue.getJSONObject("cxzt").getDouble("smSleep");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("smSleep",smSleep);
        net.sf.json.JSONObject eventData = new JSONObject();
        eventData.put("hzguid", record.getGuid());
        eventData.put("devicType", "ky.stl.intellwatch.sleep");
        eventData.put("bgdname", "智能手环");
        eventData.put("dataGuid", CommUtil.getGuid());
        eventData.put("data", jsonObject);
        //String key = "tenant_id", tenantId = data.has(key) ? data.getString(key) : SecureUtil.getTenantId();
        //System.out.println(eventData.toString());
        try {
            CommUtil.doPost(HttpclientUtil.get("file.upDevicData")+ "?tenantId=" + record.getTenant_id(), eventData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String update(MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        String fileName = file.getOriginalFilename();
        String filePath = HttpclientUtil.get("file.ImgUrl.post") + separator;
        File dest = new File(filePath + fileName);
        String pathUrl = HttpclientUtil.get("file.ImgUrl") + separator + fileName;
        String path = filePath + separator + fileName;
        try {
            file.transferTo(dest);
            LOGGER.info("上传成功");
            return pathUrl;
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        return pathUrl;
    }
}
