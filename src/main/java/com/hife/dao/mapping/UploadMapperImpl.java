package com.hife.dao.mapping;

import com.hife.base.BaseMapper;
import com.hife.dao.UploadMapper;
import com.hife.entity.SleepRecord;
import org.springframework.stereotype.Repository;

@Repository
public class UploadMapperImpl extends BaseMapper implements UploadMapper {

    @Override
    public SleepRecord SaveDatValue(SleepRecord record) {
        return this.mongoTemplate.save(record);
    }
}
