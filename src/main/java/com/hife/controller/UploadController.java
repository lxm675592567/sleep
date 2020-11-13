package com.hife.controller;

import com.alibaba.fastjson.JSONObject;
import com.hife.entity.SleepRecord;
import com.hife.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("sleepAnalysis/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * @api {Post} sleepAnalysis/upload/getDat getDat
     * @apiGroup 上传
     * @apiDescription 上传获得dat保存路径
     * @apiParam {file} file dat传输
     * @apiSuccessExample  {String} 返回值示例
     * D:/file/dat/黄智.dat
     */
    @PostMapping("/getDat")
    @ResponseBody
    public String getDat(@RequestParam("file") MultipartFile file) throws ParseException {
        return uploadService.getDat(file);
    }

    /*
    * 上传 将医护信息,cardid,guid传过来进行保存
    * */
    /**
     * @api {Post} sleepAnalysis/upload/SaveDatValue SaveDatValue
     * @apiGroup 上传
     * @apiDescription 将医护信息,cardid,guid传过来进行保存
     * @apiParam {record} record //用户数据(参数可看getSleepRecode接口)
     * @apiParamExample {json} 传参示例
     * {
     *    "guid": "121212",
     *    "cardId": "1221",
     *    "name": "测试",
     *    "sex": "男",
     *    "birthday": "2010-04-01",
     *    "address": "丽水花园",
     *    "phone": "15069861111",
     *    "height": "160",
     *    "weight": "60",
     *    "datUrl": ["D:/file/dat/黄智.dat","D:/file/dat/李总6.4.dat"],
     *    "yhryName": "医护人员",
     *    "technician": "技术员",
     *    "yhryPhone": "13959592929",
     *    "recorder": "记录员",
     *    "fax": "150152",
     *    "doctor": "王医生"
     * }
     */
    @PostMapping("/SaveDatValue")
    public String SaveDatValue(@RequestBody SleepRecord record) throws Exception {
         return uploadService.SaveDatValue(record);
    }


    /**
     * @api {Get} http://10.10.10.54:5566/api/external/record/getRecodeByKey?key=20200627000001 getRecodeByKey
     * @apiGroup 档案
     * @apiDescription 平台上传获得档案信息
     * @apiParam {String} key cardId
     * @apiSuccessExample  {json} 返回值示例
     * {"success":true,"message":"操作成功","resultData":[{"createUser":null,"createDept":null,"tenantId":"","guid":"1276727791785107456","node":0,"cardId":"20200627000001","name":"小鱼6","sex":"男","idnumber":"","birthday":"2020-05-23","unionId":"","createTime":"2020-06-27 12:03:20","scenesId":"","nextCheckDate":"","ageDetail":{},"area":[],"areaMap":{},"address":"","province":"","city":"","country":"","weixinid":""}]}
     */
}
