package com.hife.dao.mapping;

import com.hife.base.BaseMapper;
import com.hife.base.PageParam;
import com.hife.base.PageResult;
import com.hife.dao.SleepMapper;
import com.hife.entity.DoctorAdvice;
import com.hife.entity.SleepRecord;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class SleepMapperImpl extends BaseMapper implements SleepMapper {
    @Override
    public List<SleepRecord> getSleepRecode(String key) {
        return this.mongoTemplate.find(
                new Query(
                        new Criteria().orOperator(where("cardId").regex(key))
                ),
                SleepRecord.class
        );
    }

    @Override
    public SleepRecord getSleepId(String sleepId) {
        return this.mongoTemplate.findOne(new Query(where("sleepId").is(sleepId)), SleepRecord.class);
    }

    @Override
    public PageResult<DoctorAdvice> getDoctorAdvice(Document queryDoc, PageParam pageParam) {
        return (PageResult<DoctorAdvice>) this.pageQuery(
                new BasicQuery(queryDoc),
                DoctorAdvice.class,
                Optional.ofNullable(pageParam.getPageSize()).orElse(5),
                Optional.ofNullable(pageParam.getPageNum()).orElse(1)
        );

    }

    @Override
    public PageResult<SleepRecord> findSleepPagination(Document queryDoc, PageParam pageParam) {
        return (PageResult<SleepRecord>) this.pageQuery(
                new BasicQuery(queryDoc),
                SleepRecord.class,
                Optional.ofNullable(pageParam.getPageSize()).orElse(5),
                Optional.ofNullable(pageParam.getPageNum()).orElse(1),
                Sort.Order.desc("createTime")
        );
    }

    @Override
    public DoctorAdvice saveDoctorAdvice(DoctorAdvice doctorAdvice) {
        return this.mongoTemplate.save(doctorAdvice);
    }

    @Override
    public long deleteDoctorAdvice(Document queryDoc) {
        return this.mongoTemplate.remove(new BasicQuery(queryDoc), DoctorAdvice.class).getDeletedCount();
    }

    @Override
    public long deleteSleepAdvice(Document queryDoc) {
        return this.mongoTemplate.remove(new BasicQuery(queryDoc), SleepRecord.class).getDeletedCount();
    }
}
