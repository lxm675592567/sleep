package com.hife.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hife.base.PageParam;
import com.hife.base.PageResult;
import com.hife.dao.SleepMapper;
import com.hife.entity.DoctorAdvice;
import com.hife.entity.SleepRecord;
import com.hife.service.SleepService;
import com.hife.util.GuidUtil;
import com.hife.util.StringUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SleepServiceImpl implements SleepService {

    @Autowired
    private SleepMapper sleepMapper;

    @Override
    public PageResult<SleepRecord> getSleepRecode(JSONObject jsonObject) {
        Document queryDoc = new Document();
        String cardId = jsonObject.getString("cardId");
        if (StringUtil.stringIsNotNull(cardId)) {
            queryDoc.put("cardId", cardId);
        }
        String phone = jsonObject.getString("phone");
        if (StringUtil.stringIsNotNull(phone)) {
            queryDoc.put("phone", phone);
        }
        String tenantId = jsonObject.getString("tenantId");
        if (StringUtil.stringIsNotNull(tenantId)) {
            queryDoc.put("tenant_id", tenantId);
        }
        PageParam pageParam = new PageParam(jsonObject.getInteger(PageParam.PAGE_SIZE),
                jsonObject.getInteger(PageParam.PAGE_NUM));

        PageResult<SleepRecord> sleepPagination = sleepMapper.findSleepPagination(queryDoc, pageParam);
        return sleepPagination;
    }

    @Override
    public SleepRecord getSleepId(String sleepId) {
        return sleepMapper.getSleepId(sleepId);
    }

    @Override
    public PageResult<DoctorAdvice> getDoctorAdvice(JSONObject jsonObject) {
        Document queryDoc = new Document();
        String daId = jsonObject.getString("daId");
        if (StringUtil.stringIsNotNull(daId)) {
            queryDoc.put("daId", daId);
        }

        PageParam pageParam = new PageParam(jsonObject.getInteger(PageParam.PAGE_SIZE),
                jsonObject.getInteger(PageParam.PAGE_NUM));
        return sleepMapper.getDoctorAdvice(queryDoc, pageParam);
    }

    @Override
    public DoctorAdvice saveOrEditDoctorAdvice(DoctorAdvice doctorAdvice) {
        if (StringUtil.stringIsNull(doctorAdvice.getDaId())) {
            doctorAdvice.setDaId(GuidUtil.generateGuid());
        } else {
            Document queryDoc = new Document("daId", doctorAdvice.getDaId());
            this.sleepMapper.deleteDoctorAdvice(queryDoc);
        }
        return this.sleepMapper.saveDoctorAdvice(doctorAdvice);
    }

    @Override
    public long deleteDoctorAdvice(String daId) {
        Document queryDoc = new Document("daId", daId);
        return this.sleepMapper.deleteDoctorAdvice(queryDoc);
    }


}
