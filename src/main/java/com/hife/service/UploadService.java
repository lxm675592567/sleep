package com.hife.service;

import com.alibaba.fastjson.JSONObject;
import com.hife.entity.SleepRecord;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

public interface UploadService {

    String getDat(MultipartFile file) throws ParseException;

    String SaveDatValue(SleepRecord record) throws ParseException;
}
