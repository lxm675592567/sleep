package com.hife.dao;

import com.hife.base.PageParam;
import com.hife.base.PageResult;
import com.hife.entity.DoctorAdvice;
import com.hife.entity.SleepRecord;
import org.bson.Document;

import java.util.List;

public interface SleepMapper {

    List<SleepRecord> getSleepRecode(String key);

    SleepRecord getSleepId(String sleepId);

    PageResult<DoctorAdvice>  getDoctorAdvice(Document queryDoc, PageParam pageParam);

    PageResult<SleepRecord> findSleepPagination(Document queryDoc, PageParam pageParam);

    DoctorAdvice saveDoctorAdvice(DoctorAdvice doctorAdvice);

    long deleteDoctorAdvice(Document queryDoc);

    long deleteSleepAdvice(Document queryDoc);
}
