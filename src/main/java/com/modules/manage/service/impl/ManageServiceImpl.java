package com.modules.manage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hife.base.PageParam;
import com.hife.base.PageResult;
import com.hife.util.DateUtil;
import com.hife.util.StringUtil;
import com.modules.manage.dao.ManageMapper;
import com.modules.manage.entity.WatchManage;
import com.modules.manage.service.ManageService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private ManageMapper manageMapper;

    @Override
    public PageResult<WatchManage> getWatchManage(JSONObject jsonObject) {
        Document queryDoc = new Document();
        String watchId = jsonObject.getString("watchId");//编号
        if (StringUtil.stringIsNotNull(watchId)) {
            queryDoc.put("watchId", watchId);
        }
        String type = jsonObject.getString("type");//状态
        if (StringUtil.stringIsNotNull(type)) {
            queryDoc.put("type", type);
        }
        //入库时间区间
        JSONArray storageTime = jsonObject.getJSONArray("storageTime");
        if (storageTime != null && storageTime.size() > 1) {
            queryDoc.append("startTime", DateUtil.parseDateRanges(storageTime));
        }
        //出库时间区间
        JSONArray deliveryTime = jsonObject.getJSONArray("deliveryTime");
        if (deliveryTime != null && deliveryTime.size() > 1) {
            queryDoc.append("endTime", DateUtil.parseDateRanges(deliveryTime));
        }

        PageParam pageParam = new PageParam(jsonObject.getInteger(PageParam.PAGE_SIZE),
                jsonObject.getInteger(PageParam.PAGE_NUM));
        return manageMapper.getWatchManage(queryDoc, pageParam);
    }

    @Override
    public String saveWatchManage(JSONObject jsonObject) {
        String dateStr = DateUtil.getDateStr(new Date(), DateUtil.DATE_FMT);
        String name = jsonObject.getString("name");//操作员
        JSONArray watchIdList = jsonObject.getJSONArray("watchValue");
        /*JSONObject watchIdList1 = jsonObject.getJSONObject("watchValue");
        Map watchIdList2 = (Map) jsonObject.get("watchValue");
        for (String s : watchIdList1.keySet()) {
            System.out.println(s);
        }*/
        for (Object mapList : watchIdList) {
            Map map = (Map) mapList;
            String watchId = (String) map.get("value");
            WatchManage watchList = manageMapper.findWatchId(watchId);
            if (!Objects.isNull(watchList)){
                continue;
            }
            WatchManage watchManage = new WatchManage();
            watchManage.setWatchId((String) watchId).setType("0").setStartTime(dateStr).setName(name);
            manageMapper.saveWatchManage(watchManage);
        }
        return "200";
    }

    //归还
    @Override
    public String revertWatchManage(JSONObject jsonObject) {
        String dateStr = DateUtil.getDateStr(new Date(), DateUtil.DATE_FMT);
        JSONArray watchIdList = jsonObject.getJSONArray("watchValue");
        String name = jsonObject.getString("name");
        if (name.isEmpty() || Objects.isNull(watchIdList)){
            return "500";
        }
        for (Object mapList : watchIdList) {
            Map map = (Map) mapList;
            String watchId = (String) map.get("value");
            Update update = Update.update("info", null)
                    .set("type", "0").set("startTime", dateStr).set("endTime", "").set("holder", "").set("name", name);
            manageMapper.revertWatchManage((String) watchId, update);
        }
        return "200";
    }

    //出库
    @Override
    public String deliveryWatchManage(JSONObject jsonObject) {
        String dateStr = DateUtil.getDateStr(new Date(), DateUtil.DATE_FMT);
        String watchId = jsonObject.getString("watchId");
        JSONObject info = jsonObject.getJSONObject("info");
        String holder = jsonObject.getString("holder");
        String name = jsonObject.getString("name");

        Update update = Update.update("info", info)
                .set("type", "1").set("endTime", dateStr).set("holder", holder).set("name", name);
        long l = manageMapper.deliveryWatchManage(watchId, update);
        if (l!=1){
            return "500";
        }
        return "200";
    }

    @Override
    public String deleteWatchManage(String watchId) {
        Document queryDoc = new Document("watchId", watchId);
        long l = this.manageMapper.deleteWatchManage(queryDoc);
        if (l!=1){
            return "500";
        }
        return "200";
    }

    @Override
    public long amountSaveOrRevert(JSONObject jsonObject) {
        String watchId = jsonObject.getString("watchId");
        WatchManage watchManage = manageMapper.amountSaveOrRevert(watchId);

        if (Objects.isNull(watchManage)) {
            return 0;//0是没有值
        }
        return 1;//1是有值
    }
}
