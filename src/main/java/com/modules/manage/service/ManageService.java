package com.modules.manage.service;

import com.alibaba.fastjson.JSONObject;
import com.hife.base.PageResult;
import com.modules.manage.entity.WatchManage;

public interface ManageService {

    PageResult<WatchManage> getWatchManage(JSONObject jsonObject);

    String saveWatchManage(JSONObject jsonObject);

    String revertWatchManage(JSONObject jsonObject);

    String deliveryWatchManage(JSONObject jsonObject);

    String deleteWatchManage(String watchId);

    long amountSaveOrRevert(JSONObject jsonObject);
}
