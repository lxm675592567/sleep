package com.modules.manage.dao;

import com.alibaba.fastjson.JSONObject;
import com.hife.base.PageParam;
import com.hife.base.PageResult;
import com.modules.manage.entity.WatchManage;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Update;

public interface ManageMapper {

    PageResult<WatchManage> getWatchManage(Document queryDoc, PageParam pageParam);

    WatchManage saveWatchManage(WatchManage watchManage);

    long revertWatchManage(String watchId,Update update);

    long deliveryWatchManage(String watchId,Update update);

    long deleteWatchManage(Document queryDoc);

    WatchManage amountSaveOrRevert(String watchId);

    WatchManage findWatchId(String watchId);
}
