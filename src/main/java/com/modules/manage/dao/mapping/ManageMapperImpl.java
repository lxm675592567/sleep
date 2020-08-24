package com.modules.manage.dao.mapping;

import com.alibaba.fastjson.JSONObject;
import com.hife.base.BaseMapper;
import com.hife.base.PageParam;
import com.hife.base.PageResult;

import com.hife.entity.DoctorAdvice;
import com.modules.manage.dao.ManageMapper;
import com.modules.manage.entity.WatchManage;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class ManageMapperImpl extends BaseMapper implements ManageMapper {
    @Override
    public PageResult<WatchManage> getWatchManage(Document queryDoc, PageParam pageParam) {
         return (PageResult<WatchManage>) this.pageQuery(
                new BasicQuery(queryDoc),
                 WatchManage.class,
                Optional.ofNullable(pageParam.getPageSize()).orElse(5),
                Optional.ofNullable(pageParam.getPageNum()).orElse(1)
        );
    }

    @Override
    public WatchManage saveWatchManage(WatchManage watchManage) {
        return this.mongoTemplate.save(watchManage);
    }

    @Override
    public long revertWatchManage(String watchId, Update update) {
        return this.mongoTemplate.updateFirst(
                new Query(where("watchId").is(watchId)),
                update,
                WatchManage.class
        ).getModifiedCount();
    }

    @Override
    public long deliveryWatchManage(String watchId,Update update) {
        return this.mongoTemplate.updateFirst(
                new Query(where("watchId").is(watchId)),
                update,
                WatchManage.class
        ).getModifiedCount();
    }

    @Override
    public long deleteWatchManage(Document queryDoc) {
        return this.mongoTemplate.remove(new BasicQuery(queryDoc), WatchManage.class).getDeletedCount();
    }

    @Override
    public WatchManage amountSaveOrRevert(String watchId) {
        return  mongoTemplate.findOne(new Query(where("watchId").is(watchId)), WatchManage.class);
    }

    @Override
    public WatchManage findWatchId(String watchId) {
        return this.mongoTemplate.findOne(Query.query(where("watchId").is(watchId)), WatchManage.class);
    }
}
