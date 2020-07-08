package com.hife.controller;

import com.alibaba.fastjson.JSONObject;
import com.hife.base.PageResult;
import com.hife.base.ResultVO;
import com.hife.entity.DoctorAdvice;
import com.hife.entity.SleepRecord;
import com.hife.service.SleepService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("sleepAnalysis/sleep")
public class SleepController {

    @Autowired
    private SleepService sleepService;

    /**
     * @api {Post} sleepAnalysis/sleep/getSleepRecode getSleepRecode
     * @apiGroup 用户接口
     * @apiDescription 获取用户报告单数据(查询接口)
     * @apiParam {json} cardId 用户id(可传可不传)
     * @apiParam {json} pageNum 当前页
     * @apiParam {json} pageSize 当前页展示数量
     * @apiParamExample {String} 传参示例
     * {
     * 	 "cardId": "1221",
     * 	 "pageNum": "1",
     * 	 "pageSize": "10"
     * }
     * @apiSuccess {SleepRecord} sleepId 主键id
     * @apiSuccess {SleepRecord} guid guid
     * @apiSuccess {SleepRecord} cardId 卡号
     * @apiSuccess {SleepRecord} name 姓名
     * @apiSuccess {SleepRecord} name 性别
     * @apiSuccess {SleepRecord} birthday 生日
     * @apiSuccess {SleepRecord} address 地址
     * @apiSuccess {SleepRecord} phone 电话
     * @apiSuccess {SleepRecord} age 年龄
     * @apiSuccess {SleepRecord} height 身高
     * @apiSuccess {SleepRecord} weight 体重
     * @apiSuccess {SleepRecord} BMI 体质指数
     * @apiSuccess {SleepRecord} datUrl dat路径
     * @apiSuccess {SleepRecord} datUrl 医护人员姓名
     * @apiSuccess {SleepRecord} technician 技术员
     * @apiSuccess {SleepRecord} yhryPhone 医护人员电话
     * @apiSuccess {SleepRecord} recorder 记录员
     * @apiSuccess {SleepRecord} fax 传真
     * @apiSuccess {SleepRecord} doctor 医生
     * @apiSuccess {SleepRecord} createTime 生成报告日期
     * @apiSuccessExample  {json} 返回值示例
     * {"page":{"pageNum":1,"pageSize":10,"total":4,"pages":1},"list":[{"sleepId":"6d9e0f28125b49b6af3f0194a5bb76d7","guid":"121212","cardId":"1221","name":"测试","sex":"男","birthday":"2010-04-01","address":"丽水花园","phone":"15069861111","age":10,"height":"160","weight":"60","datUrl":null,"url":"D:/file/dat/黄智.dat","yhryName":"医护人员","technician":"技术员","yhryPhone":"13959592929","recorder":"记录员","fax":"150152","doctor":"王医生","createTime":"2020-07-03","doctorAdvice":null,"bmi":"23.4"},{"sleepId":"2c9e208408154659bb890b165ba70b1e","guid":"121212","cardId":"1221","name":"测试","sex":"男","birthday":"2010-04-01","address":"丽水花园","phone":"15069861111","age":10,"height":"160","weight":"60","datUrl":null,"url":"D:/file/dat/李总6.4.dat","yhryName":"医护人员","technician":"技术员","yhryPhone":"13959592929","recorder":"记录员","fax":"150152","doctor":"王医生","createTime":"2020-07-03","doctorAdvice":null,"bmi":"23.4"},{"sleepId":"8ff67a2e536c466db4fb4ac52d2ace9c","guid":"121212","cardId":"1221","name":"测试","sex":"男","birthday":"2010-04-01","address":"丽水花园","phone":"15069861111","age":10,"height":"160","weight":"60","datUrl":null,"url":"D:/file/dat/黄智.dat","yhryName":"医护人员","technician":"技术员","yhryPhone":"13959592929","recorder":"记录员","fax":"150152","doctor":"王医生","createTime":"2020-07-03","doctorAdvice":null,"bmi":"23.4"},{"sleepId":"9d7324b68a6e40eabbc10e5895949104","guid":"121212","cardId":"1221","name":"测试","sex":"男","birthday":"2010-04-01","address":"丽水花园","phone":"15069861111","age":10,"height":"160","weight":"60","datUrl":null,"url":"D:/file/dat/李总6.4.dat","yhryName":"医护人员","technician":"技术员","yhryPhone":"13959592929","recorder":"记录员","fax":"150152","doctor":"王医生","createTime":"2020-07-03","doctorAdvice":null,"bmi":"23.4"}]}
     */
    @PostMapping(value = "/getSleepRecode")
    public PageResult<SleepRecord> getSleepRecode(@RequestBody JSONObject jsonObject) throws ParseException {
        return sleepService.getSleepRecode(jsonObject);
    }

    /**
     * @api {Post} sleepAnalysis/sleep/getDoctorAdvice getDoctorAdvice
     * @apiGroup 医生建议
     * @apiDescription 查询医生建议接口
     * @apiParam {json} daId 医生建议id(可不填)
     * @apiParam {json} pageNum 当前页
     * @apiParam {json} pageSize 当前页数量
     * @apiParamExample {json} 传参示例
     * {"daId": "1278970038534561792","pageNum": "1","pageSize": "10"}
     *  @apiSuccessExample  {json} 返回值示例
     *  {"page":{"pageNum":1,"pageSize":10,"total":1,"pages":1},"list":[{"daId":"1278970038534561792","title":"超重","doctorAdvice":"需要减重（控制饮食，加强运动）。  BMI超标的减重，戒烟酒，慎用镇静安眠药，改变 睡姿（保持侧卧），口腔矫治器，耳鼻喉科会诊等。  需在改善睡眠质量，具体做法：在睡眠医生的指导下进行睡眠卫生学习，心理、物理、药物治疗等。睡前数小时（一般下午4点以后）避免使用兴奋性物质（咖啡，浓茶或    吸烟等）。睡前不要饮酒，酒精可干扰睡眠。规律的体育锻炼，但睡前应避免剧烈运动。睡前不要大吃大喝或进食不易消化的食物。睡前至少1小时内不做容易引起兴奋的脑力劳动或观看容易引起兴奋的书籍和影视节目。卧室环境应安静、舒适、光线及温度适宜，保持规律的作息时间。"}]}
     */
    @PostMapping(value = "/getDoctorAdvice")
    public PageResult<DoctorAdvice> getDoctorAdvice(@RequestBody JSONObject jsonObject)  {
         return  sleepService.getDoctorAdvice(jsonObject);
    }


    /**
     * @api {Post} sleepAnalysis/sleep/saveOrEditDoctorAdvice saveOrEditDoctorAdvice
     * @apiGroup 医生建议
     * @apiDescription 保存修改医生建议接口
     * @apiParam {json} daId 医生建议id(新增不填,修改填)
     * @apiParam {json} title 标题
     * @apiParam {json} doctorAdvice 医生建议
     * @apiParamExample {json} 传参示例
     * {"daId": "1278970377971195904","title": "超级轻","doctorAdvice": "不需要减重（控制饮食，加强运动）。  BMI超标的减重，戒烟酒，慎用镇静安眠药，改变 睡姿（保持侧卧），口腔矫治器，耳鼻喉科会诊等。  需在改善睡眠质量，具体做法：在睡眠医生的指导下进行睡眠卫生学习，心理、物理、药物治疗等。睡前数小时（一般下午4点以后）避免使用兴奋性物质（咖啡，浓茶或    吸烟等）。睡前不要饮酒，酒精可干扰睡眠。规律的体育锻炼，但睡前应避免剧烈运动。睡前不要大吃大喝或进食不易消化的食物。睡前至少1小时内不做容易引起兴奋的脑力劳动或观看容易引起兴奋的书籍和影视节目。卧室环境应安静、舒适、光线及温度适宜，保持规律的作息时间。"}
     *  @apiSuccessExample  {json} 返回值示例
     * {"success":true,"message":"操作成功","resultData":{"daId":"1278974832057802752","title":"超级轻","doctorAdvice":"不需要减重（控制饮食，加强运动）。  BMI超标的减重，戒烟酒，慎用镇静安眠药，改变 睡姿（保持侧卧），口腔矫治器，耳鼻喉科会诊等。  需在改善睡眠质量，具体做法：在睡眠医生的指导下进行睡眠卫生学习，心理、物理、药物治疗等。睡前数小时（一般下午4点以后）避免使用兴奋性物质（咖啡，浓茶或    吸烟等）。睡前不要饮酒，酒精可干扰睡眠。规律的体育锻炼，但睡前应避免剧烈运动。睡前不要大吃大喝或进食不易消化的食物。睡前至少1小时内不做容易引起兴奋的脑力劳动或观看容易引起兴奋的书籍和影视节目。卧室环境应安静、舒适、光线及温度适宜，保持规律的作息时间。"}}
     */
    @PostMapping(value = "saveOrEditDoctorAdvice")
    public ResultVO<DoctorAdvice> saveOrEditDoctorAdvice(@RequestBody DoctorAdvice doctorAdvice) {
        return new ResultVO<>(this.sleepService.saveOrEditDoctorAdvice(doctorAdvice));
    }

    /**
     * @api {Get} sleepAnalysis/sleep/deleteDoctorAdvice deleteDoctorAdvice
     * @apiGroup 医生建议
     * @apiDescription 删除医生建议接口
     * @apiParam {String} daId 医生建议id
     * @apiParamExample {json} 传参示例
     * http://10.10.10.90:8088/sleep/deleteDoctorAdvice?daId=1278970377971195904
     *  @apiSuccessExample  {json} 返回值示例
     * {"success":true,"message":"操作成功","resultData":0}
     */
    @GetMapping(value = "deleteDoctorAdvice")
    public ResultVO<Long> deleteDoctorAdvice(@RequestParam(value = "daId") String daId) {
        return new ResultVO<>(this.sleepService.deleteDoctorAdvice(daId));
    }

}
