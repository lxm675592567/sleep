package com.modules.manage.controller;

import com.alibaba.fastjson.JSONObject;
import com.hife.base.PageResult;
import com.hife.base.ResultVO;
import com.modules.manage.entity.WatchManage;
import com.modules.manage.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sleepAnalysis/watch/manage")
public class ManageController {

    @Autowired
    private ManageService manageService;

    /**
     * @api {Post} sleepAnalysis/watch/manage/getWatchManage getWatchManage
     * @apiGroup 手表管理
     * @apiDescription 查询手表接口
     * @apiParam {json} watchId 手表编号
     * @apiParam {json} type 状态(0入库,1出库)
     * @apiParam {json} storageTime 入库时间区间
     * @apiParam {json} deliveryTime 出库时间区间
     * @apiParam {json} pageNum 当前页
     * @apiParam {json} pageSize 当前页展示数量
     * @apiParamExample {json} 传参示例
     * {"watchId": "svsw1523","type": "0","storageTime": ["2020-08-01", "2020-08-06"],"deliveryTime": ["2020-08-01", "2020-08-06"],"pageNum": "1","pageSize": "10"}
     *  @apiSuccess {json} watchId 手表编号
     *  @apiSuccess {json} type 状态(0入库,1出库)
     *  @apiSuccess {json} startTime 入库时间
     *  @apiSuccess {json} endTime 出库时间
     *  @apiSuccess {json} holder 持有人
     *  @apiSuccess {json} name 操作员
     *  @apiSuccess {json} info 持有人基本信息
     *  @apiSuccessExample  {json} 返回值示例
     *  {"page":{"pageNum":1,"pageSize":5,"total":2,"pages":1},"list":[{"watchId":"svsw1523","type":"0","startTime":"2020-08-04","endTime":null,"holder":null,"name":"王维","info":null},{"watchId":"svsw15233","type":"0","startTime":"2020-08-04","endTime":null,"holder":null,"name":"王维","info":null}]}
     */
    @PostMapping(value = "/getWatchManage")
    public PageResult<WatchManage> getWatchManage(@RequestBody JSONObject jsonObject)  {
        return  manageService.getWatchManage(jsonObject);
    }

    /**
     * @api {Post} sleepAnalysis/watch/manage/saveWatchManage saveWatchManage
     * @apiGroup 手表管理
     * @apiDescription 添加手表接口
     * @apiParam {json} name 操作员
     * @apiParam {json} watchIdList 手表编号数组
     * @apiParamExample {json} 传参示例
     * {"name": "王维",   "watchIdList": ["svsw1523","svsw15233"]}
     *  @apiSuccess {json} resultData 200成功
     *  @apiSuccessExample  {json} 返回值示例
     *  {"success":true,"message":"操作成功","resultData":"200"}
     */
    @PostMapping(value = "saveWatchManage")
    public ResultVO<String> saveWatchManage(@RequestBody JSONObject jsonObject) {
        return new ResultVO<>(this.manageService.saveWatchManage(jsonObject));
    }

    /**
     * @api {Post} sleepAnalysis/watch/manage/revertWatchManage revertWatchManage
     * @apiGroup 手表管理
     * @apiDescription 归还手表接口
     * @apiParam {json} name 操作员
     * @apiParam {json} watchIdList 手表编号数组
     * @apiParamExample {json} 传参示例
     * {"name": "王维",   "watchIdList": ["svsw1523","svsw15233"]}
     *  @apiSuccess {json} resultData 200成功
     *  @apiSuccessExample  {json} 返回值示例
     *  {"success":true,"message":"操作成功","resultData":"200"}
     */
    @PostMapping(value = "revertWatchManage")//归还(在出库后)
    public ResultVO<String> revertWatchManage(@RequestBody JSONObject jsonObject) {
        return new ResultVO<>(this.manageService.revertWatchManage(jsonObject));
    }


    /**
     * @api {Post} sleepAnalysis/watch/manage/deliveryWatchManage deliveryWatchManage
     * @apiGroup 手表管理
     * @apiDescription 出库接口
     * @apiParam {json} watchId 手表编号
     * @apiParam {json} info 给与人员基本信息
     * @apiParam {json} holder 持有人
     * @apiParam {json} name 操作员
     * @apiParamExample {json} 传参示例
     * {"watchId": "svsw1523","holder": "李白","name": "王维",   "info": {"name": "王维","card": "2121"}}
     * @apiSuccess {json} resultData 200成功
     *  @apiSuccessExample  {json} 返回值示例
     *  {"success":true,"message":"操作成功","resultData":"200"}
     */
    @PostMapping(value = "deliveryWatchManage")//出库
    public ResultVO<String> deliveryWatchManage(@RequestBody JSONObject jsonObject) {
        return new ResultVO<>(this.manageService.deliveryWatchManage(jsonObject));
    }

    /**
     * @api {Get} sleepAnalysis/watch/manage/deleteWatchManage deleteWatchManage
     * @apiGroup 手表管理
     * @apiDescription 删除接口
     * @apiParam {json} watchId 手表编号
     * @apiParamExample {json} 传参示例
     *  http://10.10.10.90:8088/watch/manage/deleteWatchManage?watchId="svsw1523"
     *  @apiSuccess {json} resultData 200成功
     *  @apiSuccessExample  {json} 返回值示例
     *   {"success":true,"message":"操作成功","resultData":"200"}
     */
    @GetMapping(value = "deleteWatchManage")
    public ResultVO<String> deleteWatchManage(@RequestParam(value = "watchId") String watchId) {
        return new ResultVO<>(this.manageService.deleteWatchManage(watchId));
    }

    /**
     * @api {Post} sleepAnalysis/watch/manage/amountSaveOrRevert amountSaveOrRevert
     * @apiGroup 手表管理
     * @apiDescription 批量判断接口
     * @apiParam {json} watchId 手表编号
     * @apiParamExample {json} 传参示例
     * {"watchId": "svsw1523"}
     * @apiSuccess {json} resultData 0没有值 1有值
     *  @apiSuccessExample  {json} 返回值示例
     *  {"success":true,"message":"操作成功","resultData":0}
     */
    @PostMapping(value = "amountSaveOrRevert")//批量判断
    public ResultVO<Long> amountSaveOrRevert(@RequestBody JSONObject jsonObject) {
        return new ResultVO<>(this.manageService.amountSaveOrRevert(jsonObject));
    }
}
