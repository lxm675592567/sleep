package com.hife.service;

import com.alibaba.fastjson.JSONObject;
import com.hife.base.PageResult;
import com.hife.entity.DoctorAdvice;
import com.hife.entity.SleepRecord;

import java.text.ParseException;
import java.util.List;

public interface SleepService {

    PageResult<SleepRecord> getSleepRecode(JSONObject jsonObject);

    SleepRecord getSleepId(String sleepId);

    PageResult<DoctorAdvice> getDoctorAdvice(JSONObject jsonObject);
    //saveOrEditDoctorAdvice
    DoctorAdvice saveOrEditDoctorAdvice(DoctorAdvice doctorAdvice);

    long deleteDoctorAdvice(String daId);
}
