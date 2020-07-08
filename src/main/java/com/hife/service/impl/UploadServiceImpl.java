package com.hife.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hife.dao.UploadMapper;
import com.hife.entity.SleepRecord;
import com.hife.service.UploadService;
import com.hife.util.CommUtil;
import com.hife.util.DateUtil;
import com.hife.util.GuidUtil;

import com.hife.util.StringUtil;
import org.apache.commons.lang.time.DateUtils;
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
public class UploadServiceImpl implements UploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    private UploadMapper uploadMapper;

    private static String separator = "/";

    @Override
    public String getDat(MultipartFile file) throws ParseException {
        return update(file);
    }

    @Override
    public String SaveDatValue(SleepRecord record) throws ParseException {
        List<String> datUrl = record.getDatUrl();
        for (String s : datUrl) {
            record.setSleepId(CommUtil.getGuid()); //生成主键
            record.setUrl(s);
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(System.currentTimeMillis());
            record.setCreateTime(formatter.format(date));//生成日期
            if (StringUtil.stringIsNotNull(record.getBirthday())){
                //Date parse = formatter.parse(record.getBirthday());
                int year = DateUtil.getAge(formatter.parse(record.getBirthday())).getYear();
                record.setAge(year);
            }
            if (StringUtil.stringIsNotNull(record.getHeight()) || StringUtil.stringIsNotNull(record.getWeight())){
                double height = Double.valueOf(record.getHeight())*0.01;  double weight = Double.valueOf(record.getWeight());
                DecimalFormat df = new DecimalFormat("#0.0");
                String bmi = df.format(weight/(height*height));
                record.setBMI(bmi);
            }
             record.setDatUrl(null);
             uploadMapper.SaveDatValue(record);
        }
        return "200";
    }

    private String update(MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        String fileName = file.getOriginalFilename();
        String filePath = "D:\\file\\dat\\";
        File dest = new File(filePath + fileName);
        //String path = filePath + fileName;
        String path = "D:/file/dat"  + separator + fileName ;
        try {
            file.transferTo(dest);
            LOGGER.info("上传成功");
            return path;
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        return path;
    }
}
