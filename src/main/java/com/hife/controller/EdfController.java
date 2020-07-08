package com.hife.controller;

import com.hife.EDFUtils.EDFParser;
import com.hife.EDFUtils.EDFRecord;
import com.alibaba.fastjson.JSONObject;
import com.hife.service.EdfService;
import com.hife.util.SleepResultUtil;
import com.hife.util.SleepResultUtils;
import com.hife.util.SleepUtil;
import io.swagger.annotations.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("sleepAnalysis/edf")
public class EdfController {

    @Autowired
    private EdfService edfService;

    @PostMapping(value = "/getWave")
    public String getWave(@RequestBody JSONObject jsonObject) throws ParseException {
        return edfService.getWave(jsonObject);
    }

    @PostMapping(value = "/getDatWave")
    public String getDatWave(@RequestBody JSONObject jsonObject) throws ParseException {
        return edfService.getDatWave(jsonObject);
    }

    @PostMapping(value = "/getDeepSleepWave")
    public String getDeepSleepWave(@RequestBody JSONObject jsonObject) throws ParseException {
        return edfService.getDeepSleepWave(jsonObject);
    }

    @GetMapping(value = "/getFileName")
    public List getFileName() {
         return edfService.getFileName();
    }

    @GetMapping(value = "/getFileDatName")
    public List getFileDatName() {
        return edfService.getFileDatName();
    }

    /**
     * @api {Post} sleepAnalysis/edf/getSleepBasicValue getSleepBasicValue
     * @apiGroup 报告单
     * @apiDescription 获取报告单基本数据
     * @apiParam {json} sleepId 用户id
     * @apiParam {json} datUrl dat路径url
     * @apiParamExample {json} 传参示例
     * {"sleepId": "9f4e517c215a4969bb0a515f86fc0eb4","datUrl": "D:/file/dat/黄智.dat"}
     * @apiSuccess {json} sleepRecord 包含客户信息和医护人员信息
     * @apiSuccess {json} smfx 睡眠分析
     * @apiSuccess {json} BMI BMI
     * @apiSuccess {json} AHI AHI
     * @apiSuccess {json} ODI ODI
     * @apiSuccess {json} spozx 持续时长>=5分钟的最低血氧值
     * @apiSuccess {json} cxzt 睡眠清醒持续状态
     * @apiSuccess {json} hxqkfx 呼吸情况分析
     * @apiSuccess {json} xybhdfx 血氧饱和度分析
     * @apiSuccess {json} mlfx 脉率分析
     * @apiSuccess {json} smqx 睡眠清醒图
     * @apiSuccess {json} arrayyj 氧减持续时间次数图
     * @apiSuccess {json} arraymlzf 脉率直方图
     * @apiSuccess {smfx} createTime 开始记录时间
     * @apiSuccess {smfx} startSleep 入睡时间
     * @apiSuccess {smfx} endSleep 最后睡眠时刻
     * @apiSuccess {smfx} endTime 记录结束时间
     * @apiSuccess {smfx} zjlsjSleep 总记录时间
     * @apiSuccess {smfx} smsjSleep 睡眠时间
     * @apiSuccess {smfx} smzsjSleep 睡眠总时间
     * @apiSuccess {smfx} smxl 睡眠效率
     * @apiSuccess {smfx} smjxsj 睡眠觉醒时间
     * @apiSuccess {cxzt} qxSleep 清醒
     * @apiSuccess {cxzt} qxzbSleep 清醒占比
     * @apiSuccess {cxzt} smSleep 睡眠
     * @apiSuccess {cxzt} smzbSleep 睡眠占比
     * @apiSuccess {cxzt} smqfz 睡眠潜伏期
     * @apiSuccess {cxzt} jxcsSleep 觉醒次数
     * @apiSuccess {hxqkfx} hxsjzs 呼吸事件总数
     * @apiSuccess {hxqkfx} oxygenFour 氧减百分4事件持续时长
     * @apiSuccess {hxqkfx} oxygenThree 氧减百分3事件
     * @apiSuccess {hxqkfx} hxzd 呼吸最短
     * @apiSuccess {hxqkfx} hxzc 呼吸最长
     * @apiSuccess {hxqkfx} hxpj 呼吸平均(秒)
     * @apiSuccess {xybhdfx} avgSpO2 平均血氧值
     * @apiSuccess {xybhdfx} mostSpO2 最高血氧值
     * @apiSuccess {xybhdfx} leastSpO2 最低血氧值
     * @apiSuccess {xybhdfx} ninetySpO2 <90总睡眠比值
     * @apiSuccess {xybhdfx} ninetySmSpO2 <90总时间比值
     * @apiSuccess {xybhdfx} nextFour 百分四次数
     * @apiSuccess {xybhdfx} nextThree 百分三次数
     * @apiSuccess {xybhdfx} OxygenFour 百分四指数
     * @apiSuccess {xybhdfx} OxygenThree 百分三指数
     * @apiSuccess {xybhdfx} xybhd 血氧饱和度
     * @apiSuccess {mlfx} avgHr 平均脉率
     * @apiSuccess {mlfx} mostHr 最快脉率
     * @apiSuccess {mlfx} leastHr 最慢脉率
     * @apiSuccess {mlfx} avgValue 平均脉率差
     * @apiSuccess {mlfx} arrayml 脉率表格值
     * @apiSuccessExample  {json} 返回值示例
     * {"cxzt":{"smzbSleep":"96.7","qxzbSleep":"3.3","jxcsSleep":4,"qxSleep":12.0,"smSleep":352.0,"smqfz":2.0},"mlfx":{"avgValue":30.5,"arrayml":[{"zhi":"0-30","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"31-60","hr":"166.0","sm":"166.0","jx":"0.0"},{"zhi":"61-80","hr":"170.8","sm":"170.8","jx":"0.0"},{"zhi":"81-90","hr":"2.5","sm":"2.5","jx":"0.0"},{"zhi":"91-100","hr":"0.4","sm":"0.4","jx":"0.0"},{"zhi":"101-110","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"111-120","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"121-130","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"131-140","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"141-150","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"151-160","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"161-170","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"171-180","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"181-190","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"191-200","hr":"0.0","sm":"0.0","jx":"0.0"},{"zhi":"201>","hr":"0.0","sm":"0.0","jx":"0.0"}],"avgHr":63.5,"leastHr":46,"mostHr":96},"arrayyj":[["2020-06-18 23:58:25","2020-06-18 23:58:54",84,59,25],["2020-06-19 00:14:33","2020-06-19 00:14:57",1046,1027,19],["2020-06-19 00:37:57","2020-06-19 00:38:25",2447,2431,16],["2020-06-19 01:00:54","2020-06-19 01:01:26",3839,3808,31],["2020-06-19 01:51:34","2020-06-19 01:52:31",6900,6848,52],["2020-06-19 02:00:40","2020-06-19 02:01:22",7421,7394,27],["2020-06-19 02:04:53","2020-06-19 02:05:39",7688,7647,41],["2020-06-19 02:27:40","2020-06-19 02:28:08",9037,9014,23],["2020-06-19 02:32:48","2020-06-19 02:33:14",9344,9322,22],["2020-06-19 02:38:28","2020-06-19 02:38:50",9677,9662,15],["2020-06-19 03:03:08","2020-06-19 03:03:58",11189,11142,47],["2020-06-19 03:10:43","2020-06-19 03:11:19",11621,11597,24],["2020-06-19 03:11:28","2020-06-19 03:11:49",11658,11642,16],["2020-06-19 04:24:20","2020-06-19 04:25:05",16057,16014,43],["2020-06-19 04:41:04","2020-06-19 04:41:27",17038,17018,20],["2020-06-19 04:44:49","2020-06-19 04:45:33",17272,17243,29],["2020-06-19 04:45:44","2020-06-19 04:46:08",17313,17298,15],["2020-06-19 04:53:04","2020-06-19 04:53:42",17774,17738,36],["2020-06-19 04:54:45","2020-06-19 04:55:18",17869,17839,30],["2020-06-19 04:55:56","2020-06-19 04:56:46",17950,17910,40],["2020-06-19 04:57:35","2020-06-19 04:58:07",18033,18009,24],["2020-06-19 05:00:58","2020-06-19 05:01:30",18237,18212,25],["2020-06-19 05:48:20","2020-06-19 05:48:44",21076,21054,22],["2020-06-19 05:49:14","2020-06-19 05:50:09",21156,21108,48],["2020-06-19 05:59:24","2020-06-19 06:00:06",21757,21718,39],["2020-06-19 06:07:50","2020-06-19 06:08:21",22247,22224,23]],"hxqkfx":{"AHI":1,"oxygenFour":"4.2","hxsjzs":1,"hxzc":10,"hxzd":10,"hxpj":10,"oxygenThree":"8.7"},"smqx":{"smzbSleep":"96.7","qxzbSleep":"3.3"},"arraymlzf":[[96,8843],[97,8872],[98,1909],[92,6],[93,68],[94,258],[95,2314]],"smfx":{"smjxsj":749,"endSleep":"2020-06-19 06:04:08","zjlsjSleep":"06:11","smzsjSleep":"05:52","createTime":"2020-06-18 23:57:26","smsjSleep":364.0,"startSleep":"2020-06-18 23:59:28","endTime":"2020-06-19 06:08:38","smxl":"94.9%"},"sleepRecord":{"sleepId":"9f4e517c215a4969bb0a515f86fc0eb4","guid":"121212","cardId":"1221","name":"测试","sex":"男","birthday":"2010-04-01","address":"丽水花园","phone":"15069861111","age":10,"height":"160","weight":"60","datUrl":"D:/file/dat/黄智.dat","yhryName":"医护人员","technician":"技术员","yhryPhone":"13959592929","recorder":"记录员","fax":"150152","doctor":"王医生","createTime":"2020-07-02","doctorAdvice":"需要减重（控制饮食，加强运动）。  BMI超标的减重，戒烟酒，慎用镇静安眠药，改变 睡姿（保持侧卧），口腔矫治器，耳鼻喉科会诊等。  需在改善睡眠质量，具体做法：在睡眠医生的指导下进行睡眠卫生学习，心理、物理、药物治疗等。睡前数小时（一般下午4点以后）避免使用兴奋性物质（咖啡，浓茶或    吸烟等）。睡前不要饮酒，酒精可干扰睡眠。规律的体育锻炼，但睡前应避免剧烈运动。睡前不要大吃大喝或进食不易消化的食物。睡前至少1小时内不做容易引起兴奋的脑力劳动或观看容易引起兴奋的书籍和影视节目。卧室环境应安静、舒适、光线及温度适宜，保持规律的作息时间。","bmi":"23.4"},"xybhd":{"leastSpO2":92,"OxygenFour":"4.2","OxygenThree":"8.7","ninetySmSpO2":0.0,"nextThree":54,"mostSpO2":98,"avgSpO2":99.8,"nextFour":26,"ninetySpO2":0.0,"xybhd":[{"O2":"0.0","zhi":"<92%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<90%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<88%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<85%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<80%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<75%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<70%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<65%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<60%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<55%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<50%","sm":"0.0","jx":"0.0"},{"O2":"0.0","zhi":"<40%","sm":"0.0","jx":"0.0"}]}}
     */
    @PostMapping(value = "/getSleepBasicValue")
    public JSONObject getSleepBasicValue(@RequestBody JSONObject jsonObject) throws ParseException, JSONException {
        return edfService.getSleepBasicValue(jsonObject);
    }


    /**
     * @api {Post} sleepAnalysis/edf/getSleepDataMap getSleepDataMap
     * @apiGroup 报告单
     * @apiDescription 获取报告单数据图标
     * @apiParam {json} sleepId 用户id
     * @apiParam {json} datUrl dat路径url
     * @apiParamExample {json} 传参示例
     * {"sleepId": "9f4e517c215a4969bb0a515f86fc0eb4","datUrl": "D:/file/dat/黄智.dat"}
     * @apiSuccess {json} xlResult 心率
     * @apiSuccess {json} smlxResult 睡眠类型(清醒A 快速眼动B 深睡C 中度深睡D 浅睡E 呼吸紊乱F 空白G)
     * @apiSuccess {json} results 最终滤波结果
     * @apiSuccess {json} xyResult 血氧
     * @apiSuccess {json} prResult 脉率
     * @apiSuccess {json} piResult 搏动指数
     * @apiSuccess {json} rrResult 呼吸
     * @apiSuccess {json} pdrResult 呼吸波
     * @apiSuccessExample  {json} 返回值示例
     *{"xlResult":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],"piResult":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],"rrResult":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],"prResult":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],"pdrResult":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],"result":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],"smlxResult":[[2,122,100,"A"],[2,42,100,"F"],[122,922,100,"E"]]}
     */
    @PostMapping(value = "/getSleepDataMap")
    public JSONObject getSleepDataMap(@RequestBody JSONObject jsonObject) throws ParseException, JSONException {
        return edfService.getSleepDataMap(jsonObject);
    }

    @PostMapping(value = "/getSleepDataMaps")
    public void getSleepDataMaps(@RequestBody JSONObject jsonObject) throws ParseException, JSONException {
        JSONObject readFile = SleepUtil.getReadFile(jsonObject);
        List<Integer> xyResult = (List<Integer>) readFile.get("xyResult");//血氧
        List<Integer> prResult = (List<Integer>) readFile.get("prResult");//脉率
        List<Integer> piResult = (List<Integer>) readFile.get("piResult");//搏动指数
        List<Integer> rrResult = (List<Integer>) readFile.get("rrResult");//呼吸
        List<Integer> pdrResult = (List<Integer>) readFile.get("pdrResult");//呼吸波
        JSONObject sleepResult = SleepResultUtils.getSleepResult(readFile);
        List<List<Object>> results = (List<List<Object>>)sleepResult.get("result");//结果
        List<List<Object>> xlResult = (List<List<Object>>) sleepResult.get("xlResult");//心率
        List<List<Object>> smlxResult = (List<List<Object>>) sleepResult.get("smlxResult");
        JSONObject object = new JSONObject();
        object.put("xyResult",xyResult);//血氧
        object.put("prResult",prResult);//脉率
        object.put("piResult",piResult);//搏动指数
        object.put("rrResult",rrResult);//呼吸
        object.put("pdrResult",pdrResult);//呼吸波

        object.put("results",results);//结果
        object.put("xlResult",xlResult); //心率
        object.put("smlxResult",smlxResult); //睡眠类型
        System.out.println("results"+results);
    }
}
