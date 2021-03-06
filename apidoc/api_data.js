define({ "api": [
  {
    "type": "Post",
    "url": "sleepAnalysis/upload/getDat",
    "title": "getDat",
    "group": "上传",
    "description": "<p>上传获得dat保存路径</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "file",
            "optional": false,
            "field": "file",
            "description": "<p>dat传输</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "返回值示例",
          "content": "D:/file/dat/黄智.dat",
          "type": "String"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/UploadController.java",
    "groupTitle": "上传",
    "name": "PostSleepanalysisUploadGetdat"
  },
  {
    "type": "Post",
    "url": "sleepAnalysis/upload/SaveDatValue",
    "title": "SaveDatValue",
    "group": "上传",
    "description": "<p>将医护信息,cardid,guid传过来进行保存</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "record",
            "optional": false,
            "field": "record",
            "description": "<p>//用户数据(参数可看getSleepRecode接口)</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "传参示例",
          "content": "{\n   \"guid\": \"121212\",\n   \"cardId\": \"1221\",\n   \"name\": \"测试\",\n   \"sex\": \"男\",\n   \"birthday\": \"2010-04-01\",\n   \"address\": \"丽水花园\",\n   \"phone\": \"15069861111\",\n   \"height\": \"160\",\n   \"weight\": \"60\",\n   \"datUrl\": [\"D:/file/dat/黄智.dat\",\"D:/file/dat/李总6.4.dat\"],\n   \"yhryName\": \"医护人员\",\n   \"technician\": \"技术员\",\n   \"yhryPhone\": \"13959592929\",\n   \"recorder\": \"记录员\",\n   \"fax\": \"150152\",\n   \"doctor\": \"王医生\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/UploadController.java",
    "groupTitle": "上传",
    "name": "PostSleepanalysisUploadSavedatvalue"
  },
  {
    "type": "Get",
    "url": "sleepAnalysis/sleep/deleteDoctorAdvice",
    "title": "deleteDoctorAdvice",
    "group": "医生建议",
    "description": "<p>删除医生建议接口</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "daId",
            "description": "<p>医生建议id</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "传参示例",
          "content": "http://10.10.10.90:8088/sleep/deleteDoctorAdvice?daId=1278970377971195904",
          "type": "json"
        }
      ]
    },
    "success": {
      "examples": [
        {
          "title": "返回值示例",
          "content": "{\"success\":true,\"message\":\"操作成功\",\"resultData\":0}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/SleepController.java",
    "groupTitle": "医生建议",
    "name": "GetSleepanalysisSleepDeletedoctoradvice"
  },
  {
    "type": "Post",
    "url": "sleepAnalysis/sleep/getDoctorAdvice",
    "title": "getDoctorAdvice",
    "group": "医生建议",
    "description": "<p>查询医生建议接口</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "daId",
            "description": "<p>医生建议id(可不填)</p>"
          },
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "pageNum",
            "description": "<p>当前页</p>"
          },
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "pageSize",
            "description": "<p>当前页数量</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "传参示例",
          "content": "{\"daId\": \"1278970038534561792\",\"pageNum\": \"1\",\"pageSize\": \"10\"}",
          "type": "json"
        }
      ]
    },
    "success": {
      "examples": [
        {
          "title": "返回值示例",
          "content": "{\"page\":{\"pageNum\":1,\"pageSize\":10,\"total\":1,\"pages\":1},\"list\":[{\"daId\":\"1278970038534561792\",\"title\":\"超重\",\"doctorAdvice\":\"需要减重（控制饮食，加强运动）。  BMI超标的减重，戒烟酒，慎用镇静安眠药，改变 睡姿（保持侧卧），口腔矫治器，耳鼻喉科会诊等。  需在改善睡眠质量，具体做法：在睡眠医生的指导下进行睡眠卫生学习，心理、物理、药物治疗等。睡前数小时（一般下午4点以后）避免使用兴奋性物质（咖啡，浓茶或    吸烟等）。睡前不要饮酒，酒精可干扰睡眠。规律的体育锻炼，但睡前应避免剧烈运动。睡前不要大吃大喝或进食不易消化的食物。睡前至少1小时内不做容易引起兴奋的脑力劳动或观看容易引起兴奋的书籍和影视节目。卧室环境应安静、舒适、光线及温度适宜，保持规律的作息时间。\"}]}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/SleepController.java",
    "groupTitle": "医生建议",
    "name": "PostSleepanalysisSleepGetdoctoradvice"
  },
  {
    "type": "Post",
    "url": "sleepAnalysis/sleep/saveOrEditDoctorAdvice",
    "title": "saveOrEditDoctorAdvice",
    "group": "医生建议",
    "description": "<p>保存修改医生建议接口</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "daId",
            "description": "<p>医生建议id(新增不填,修改填)</p>"
          },
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "title",
            "description": "<p>标题</p>"
          },
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "doctorAdvice",
            "description": "<p>医生建议</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "传参示例",
          "content": "{\"daId\": \"1278970377971195904\",\"title\": \"超级轻\",\"doctorAdvice\": \"不需要减重（控制饮食，加强运动）。  BMI超标的减重，戒烟酒，慎用镇静安眠药，改变 睡姿（保持侧卧），口腔矫治器，耳鼻喉科会诊等。  需在改善睡眠质量，具体做法：在睡眠医生的指导下进行睡眠卫生学习，心理、物理、药物治疗等。睡前数小时（一般下午4点以后）避免使用兴奋性物质（咖啡，浓茶或    吸烟等）。睡前不要饮酒，酒精可干扰睡眠。规律的体育锻炼，但睡前应避免剧烈运动。睡前不要大吃大喝或进食不易消化的食物。睡前至少1小时内不做容易引起兴奋的脑力劳动或观看容易引起兴奋的书籍和影视节目。卧室环境应安静、舒适、光线及温度适宜，保持规律的作息时间。\"}",
          "type": "json"
        }
      ]
    },
    "success": {
      "examples": [
        {
          "title": "返回值示例",
          "content": "{\"success\":true,\"message\":\"操作成功\",\"resultData\":{\"daId\":\"1278974832057802752\",\"title\":\"超级轻\",\"doctorAdvice\":\"不需要减重（控制饮食，加强运动）。  BMI超标的减重，戒烟酒，慎用镇静安眠药，改变 睡姿（保持侧卧），口腔矫治器，耳鼻喉科会诊等。  需在改善睡眠质量，具体做法：在睡眠医生的指导下进行睡眠卫生学习，心理、物理、药物治疗等。睡前数小时（一般下午4点以后）避免使用兴奋性物质（咖啡，浓茶或    吸烟等）。睡前不要饮酒，酒精可干扰睡眠。规律的体育锻炼，但睡前应避免剧烈运动。睡前不要大吃大喝或进食不易消化的食物。睡前至少1小时内不做容易引起兴奋的脑力劳动或观看容易引起兴奋的书籍和影视节目。卧室环境应安静、舒适、光线及温度适宜，保持规律的作息时间。\"}}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/SleepController.java",
    "groupTitle": "医生建议",
    "name": "PostSleepanalysisSleepSaveoreditdoctoradvice"
  },
  {
    "type": "Post",
    "url": "sleepAnalysis/edf/getSleepBasicValue",
    "title": "getSleepBasicValue",
    "group": "报告单",
    "description": "<p>获取报告单基本数据</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "sleepId",
            "description": "<p>用户id</p>"
          },
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "datUrl",
            "description": "<p>dat路径url</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "传参示例",
          "content": "{\"sleepId\": \"9f4e517c215a4969bb0a515f86fc0eb4\",\"datUrl\": \"D:/file/dat/黄智.dat\"}",
          "type": "json"
        }
      ]
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "sleepRecord",
            "description": "<p>包含客户信息和医护人员信息</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "smfx",
            "description": "<p>睡眠分析</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "BMI",
            "description": "<p>BMI</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "AHI",
            "description": "<p>AHI</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "ODI",
            "description": "<p>ODI</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "spozx",
            "description": "<p>持续时长&gt;=5分钟的最低血氧值</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "cxzt",
            "description": "<p>睡眠清醒持续状态</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "hxqkfx",
            "description": "<p>呼吸情况分析</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "xybhdfx",
            "description": "<p>血氧饱和度分析</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "mlfx",
            "description": "<p>脉率分析</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "smqx",
            "description": "<p>睡眠清醒图</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "arrayyj",
            "description": "<p>氧减持续时间次数图</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "arraymlzf",
            "description": "<p>脉率直方图</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "createTime",
            "description": "<p>开始记录时间</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "startSleep",
            "description": "<p>入睡时间</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "endSleep",
            "description": "<p>最后睡眠时刻</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "endTime",
            "description": "<p>记录结束时间</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "zjlsjSleep",
            "description": "<p>总记录时间</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "smsjSleep",
            "description": "<p>睡眠时间</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "smzsjSleep",
            "description": "<p>睡眠总时间</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "smxl",
            "description": "<p>睡眠效率</p>"
          },
          {
            "group": "Success 200",
            "type": "smfx",
            "optional": false,
            "field": "smjxsj",
            "description": "<p>睡眠觉醒时间</p>"
          },
          {
            "group": "Success 200",
            "type": "cxzt",
            "optional": false,
            "field": "qxSleep",
            "description": "<p>清醒</p>"
          },
          {
            "group": "Success 200",
            "type": "cxzt",
            "optional": false,
            "field": "qxzbSleep",
            "description": "<p>清醒占比</p>"
          },
          {
            "group": "Success 200",
            "type": "cxzt",
            "optional": false,
            "field": "smSleep",
            "description": "<p>睡眠</p>"
          },
          {
            "group": "Success 200",
            "type": "cxzt",
            "optional": false,
            "field": "smzbSleep",
            "description": "<p>睡眠占比</p>"
          },
          {
            "group": "Success 200",
            "type": "cxzt",
            "optional": false,
            "field": "smqfz",
            "description": "<p>睡眠潜伏期</p>"
          },
          {
            "group": "Success 200",
            "type": "cxzt",
            "optional": false,
            "field": "jxcsSleep",
            "description": "<p>觉醒次数</p>"
          },
          {
            "group": "Success 200",
            "type": "hxqkfx",
            "optional": false,
            "field": "hxsjzs",
            "description": "<p>呼吸事件总数</p>"
          },
          {
            "group": "Success 200",
            "type": "hxqkfx",
            "optional": false,
            "field": "oxygenFour",
            "description": "<p>氧减百分4事件持续时长</p>"
          },
          {
            "group": "Success 200",
            "type": "hxqkfx",
            "optional": false,
            "field": "oxygenThree",
            "description": "<p>氧减百分3事件</p>"
          },
          {
            "group": "Success 200",
            "type": "hxqkfx",
            "optional": false,
            "field": "hxzd",
            "description": "<p>呼吸最短</p>"
          },
          {
            "group": "Success 200",
            "type": "hxqkfx",
            "optional": false,
            "field": "hxzc",
            "description": "<p>呼吸最长</p>"
          },
          {
            "group": "Success 200",
            "type": "hxqkfx",
            "optional": false,
            "field": "hxpj",
            "description": "<p>呼吸平均(秒)</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "avgSpO2",
            "description": "<p>平均血氧值</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "mostSpO2",
            "description": "<p>最高血氧值</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "leastSpO2",
            "description": "<p>最低血氧值</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "ninetySpO2",
            "description": "<p>&lt;90总睡眠比值</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "ninetySmSpO2",
            "description": "<p>&lt;90总时间比值</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "nextFour",
            "description": "<p>百分四次数</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "nextThree",
            "description": "<p>百分三次数</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "OxygenFour",
            "description": "<p>百分四指数</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "OxygenThree",
            "description": "<p>百分三指数</p>"
          },
          {
            "group": "Success 200",
            "type": "xybhdfx",
            "optional": false,
            "field": "xybhd",
            "description": "<p>血氧饱和度</p>"
          },
          {
            "group": "Success 200",
            "type": "mlfx",
            "optional": false,
            "field": "avgHr",
            "description": "<p>平均脉率</p>"
          },
          {
            "group": "Success 200",
            "type": "mlfx",
            "optional": false,
            "field": "mostHr",
            "description": "<p>最快脉率</p>"
          },
          {
            "group": "Success 200",
            "type": "mlfx",
            "optional": false,
            "field": "leastHr",
            "description": "<p>最慢脉率</p>"
          },
          {
            "group": "Success 200",
            "type": "mlfx",
            "optional": false,
            "field": "avgValue",
            "description": "<p>平均脉率差</p>"
          },
          {
            "group": "Success 200",
            "type": "mlfx",
            "optional": false,
            "field": "arrayml",
            "description": "<p>脉率表格值</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "返回值示例",
          "content": "{\"cxzt\":{\"smzbSleep\":\"96.7\",\"qxzbSleep\":\"3.3\",\"jxcsSleep\":4,\"qxSleep\":12.0,\"smSleep\":352.0,\"smqfz\":2.0},\"mlfx\":{\"avgValue\":30.5,\"arrayml\":[{\"zhi\":\"0-30\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"31-60\",\"hr\":\"166.0\",\"sm\":\"166.0\",\"jx\":\"0.0\"},{\"zhi\":\"61-80\",\"hr\":\"170.8\",\"sm\":\"170.8\",\"jx\":\"0.0\"},{\"zhi\":\"81-90\",\"hr\":\"2.5\",\"sm\":\"2.5\",\"jx\":\"0.0\"},{\"zhi\":\"91-100\",\"hr\":\"0.4\",\"sm\":\"0.4\",\"jx\":\"0.0\"},{\"zhi\":\"101-110\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"111-120\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"121-130\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"131-140\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"141-150\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"151-160\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"161-170\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"171-180\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"181-190\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"191-200\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"zhi\":\"201>\",\"hr\":\"0.0\",\"sm\":\"0.0\",\"jx\":\"0.0\"}],\"avgHr\":63.5,\"leastHr\":46,\"mostHr\":96},\"arrayyj\":[[\"2020-06-18 23:58:25\",\"2020-06-18 23:58:54\",84,59,25],[\"2020-06-19 00:14:33\",\"2020-06-19 00:14:57\",1046,1027,19],[\"2020-06-19 00:37:57\",\"2020-06-19 00:38:25\",2447,2431,16],[\"2020-06-19 01:00:54\",\"2020-06-19 01:01:26\",3839,3808,31],[\"2020-06-19 01:51:34\",\"2020-06-19 01:52:31\",6900,6848,52],[\"2020-06-19 02:00:40\",\"2020-06-19 02:01:22\",7421,7394,27],[\"2020-06-19 02:04:53\",\"2020-06-19 02:05:39\",7688,7647,41],[\"2020-06-19 02:27:40\",\"2020-06-19 02:28:08\",9037,9014,23],[\"2020-06-19 02:32:48\",\"2020-06-19 02:33:14\",9344,9322,22],[\"2020-06-19 02:38:28\",\"2020-06-19 02:38:50\",9677,9662,15],[\"2020-06-19 03:03:08\",\"2020-06-19 03:03:58\",11189,11142,47],[\"2020-06-19 03:10:43\",\"2020-06-19 03:11:19\",11621,11597,24],[\"2020-06-19 03:11:28\",\"2020-06-19 03:11:49\",11658,11642,16],[\"2020-06-19 04:24:20\",\"2020-06-19 04:25:05\",16057,16014,43],[\"2020-06-19 04:41:04\",\"2020-06-19 04:41:27\",17038,17018,20],[\"2020-06-19 04:44:49\",\"2020-06-19 04:45:33\",17272,17243,29],[\"2020-06-19 04:45:44\",\"2020-06-19 04:46:08\",17313,17298,15],[\"2020-06-19 04:53:04\",\"2020-06-19 04:53:42\",17774,17738,36],[\"2020-06-19 04:54:45\",\"2020-06-19 04:55:18\",17869,17839,30],[\"2020-06-19 04:55:56\",\"2020-06-19 04:56:46\",17950,17910,40],[\"2020-06-19 04:57:35\",\"2020-06-19 04:58:07\",18033,18009,24],[\"2020-06-19 05:00:58\",\"2020-06-19 05:01:30\",18237,18212,25],[\"2020-06-19 05:48:20\",\"2020-06-19 05:48:44\",21076,21054,22],[\"2020-06-19 05:49:14\",\"2020-06-19 05:50:09\",21156,21108,48],[\"2020-06-19 05:59:24\",\"2020-06-19 06:00:06\",21757,21718,39],[\"2020-06-19 06:07:50\",\"2020-06-19 06:08:21\",22247,22224,23]],\"hxqkfx\":{\"AHI\":1,\"oxygenFour\":\"4.2\",\"hxsjzs\":1,\"hxzc\":10,\"hxzd\":10,\"hxpj\":10,\"oxygenThree\":\"8.7\"},\"smqx\":{\"smzbSleep\":\"96.7\",\"qxzbSleep\":\"3.3\"},\"arraymlzf\":[[96,8843],[97,8872],[98,1909],[92,6],[93,68],[94,258],[95,2314]],\"smfx\":{\"smjxsj\":749,\"endSleep\":\"2020-06-19 06:04:08\",\"zjlsjSleep\":\"06:11\",\"smzsjSleep\":\"05:52\",\"createTime\":\"2020-06-18 23:57:26\",\"smsjSleep\":364.0,\"startSleep\":\"2020-06-18 23:59:28\",\"endTime\":\"2020-06-19 06:08:38\",\"smxl\":\"94.9%\"},\"sleepRecord\":{\"sleepId\":\"9f4e517c215a4969bb0a515f86fc0eb4\",\"guid\":\"121212\",\"cardId\":\"1221\",\"name\":\"测试\",\"sex\":\"男\",\"birthday\":\"2010-04-01\",\"address\":\"丽水花园\",\"phone\":\"15069861111\",\"age\":10,\"height\":\"160\",\"weight\":\"60\",\"datUrl\":\"D:/file/dat/黄智.dat\",\"yhryName\":\"医护人员\",\"technician\":\"技术员\",\"yhryPhone\":\"13959592929\",\"recorder\":\"记录员\",\"fax\":\"150152\",\"doctor\":\"王医生\",\"createTime\":\"2020-07-02\",\"doctorAdvice\":\"需要减重（控制饮食，加强运动）。  BMI超标的减重，戒烟酒，慎用镇静安眠药，改变 睡姿（保持侧卧），口腔矫治器，耳鼻喉科会诊等。  需在改善睡眠质量，具体做法：在睡眠医生的指导下进行睡眠卫生学习，心理、物理、药物治疗等。睡前数小时（一般下午4点以后）避免使用兴奋性物质（咖啡，浓茶或    吸烟等）。睡前不要饮酒，酒精可干扰睡眠。规律的体育锻炼，但睡前应避免剧烈运动。睡前不要大吃大喝或进食不易消化的食物。睡前至少1小时内不做容易引起兴奋的脑力劳动或观看容易引起兴奋的书籍和影视节目。卧室环境应安静、舒适、光线及温度适宜，保持规律的作息时间。\",\"bmi\":\"23.4\"},\"xybhd\":{\"leastSpO2\":92,\"OxygenFour\":\"4.2\",\"OxygenThree\":\"8.7\",\"ninetySmSpO2\":0.0,\"nextThree\":54,\"mostSpO2\":98,\"avgSpO2\":99.8,\"nextFour\":26,\"ninetySpO2\":0.0,\"xybhd\":[{\"O2\":\"0.0\",\"zhi\":\"<92%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<90%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<88%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<85%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<80%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<75%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<70%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<65%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<60%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<55%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<50%\",\"sm\":\"0.0\",\"jx\":\"0.0\"},{\"O2\":\"0.0\",\"zhi\":\"<40%\",\"sm\":\"0.0\",\"jx\":\"0.0\"}]}}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/EdfController.java",
    "groupTitle": "报告单",
    "name": "PostSleepanalysisEdfGetsleepbasicvalue"
  },
  {
    "type": "Post",
    "url": "sleepAnalysis/edf/getSleepDataMap",
    "title": "getSleepDataMap",
    "group": "报告单",
    "description": "<p>获取报告单数据图标</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "sleepId",
            "description": "<p>用户id</p>"
          },
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "datUrl",
            "description": "<p>dat路径url</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "传参示例",
          "content": "{\"sleepId\": \"9f4e517c215a4969bb0a515f86fc0eb4\",\"datUrl\": \"D:/file/dat/黄智.dat\"}",
          "type": "json"
        }
      ]
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "xlResult",
            "description": "<p>心率</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "smlxResult",
            "description": "<p>睡眠类型(清醒A 快速眼动B 深睡C 中度深睡D 浅睡E 呼吸紊乱F 空白G)</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "results",
            "description": "<p>最终滤波结果</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "xyResult",
            "description": "<p>血氧</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "prResult",
            "description": "<p>脉率</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "piResult",
            "description": "<p>搏动指数</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "rrResult",
            "description": "<p>呼吸</p>"
          },
          {
            "group": "Success 200",
            "type": "json",
            "optional": false,
            "field": "pdrResult",
            "description": "<p>呼吸波</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "返回值示例",
          "content": "{\"xlResult\":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],\"piResult\":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],\"rrResult\":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],\"prResult\":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],\"pdrResult\":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],\"result\":[[1592495846000,81],[1592495847000,81],[1592495848000,81]],\"smlxResult\":[[2,122,100,\"A\"],[2,42,100,\"F\"],[122,922,100,\"E\"]]}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/EdfController.java",
    "groupTitle": "报告单",
    "name": "PostSleepanalysisEdfGetsleepdatamap"
  },
  {
    "type": "Get",
    "url": "http://10.10.10.54:5566/api/external/record/getRecodeByKey?key=20200627000001",
    "title": "getRecodeByKey",
    "group": "档案",
    "description": "<p>平台上传获得档案信息</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "key",
            "description": "<p>cardId</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "返回值示例",
          "content": "{\"success\":true,\"message\":\"操作成功\",\"resultData\":[{\"createUser\":null,\"createDept\":null,\"tenantId\":\"\",\"guid\":\"1276727791785107456\",\"node\":0,\"cardId\":\"20200627000001\",\"name\":\"小鱼6\",\"sex\":\"男\",\"idnumber\":\"\",\"birthday\":\"2020-05-23\",\"unionId\":\"\",\"createTime\":\"2020-06-27 12:03:20\",\"scenesId\":\"\",\"nextCheckDate\":\"\",\"ageDetail\":{},\"area\":[],\"areaMap\":{},\"address\":\"\",\"province\":\"\",\"city\":\"\",\"country\":\"\",\"weixinid\":\"\"}]}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/UploadController.java",
    "groupTitle": "档案",
    "name": "GetHttp101010545566ApiExternalRecordGetrecodebykeyKey20200627000001"
  },
  {
    "type": "Post",
    "url": "sleepAnalysis/sleep/getSleepRecode",
    "title": "getSleepRecode",
    "group": "用户接口",
    "description": "<p>获取用户报告单数据(查询接口)</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "cardId",
            "description": "<p>用户id(可传可不传)</p>"
          },
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "pageNum",
            "description": "<p>当前页</p>"
          },
          {
            "group": "Parameter",
            "type": "json",
            "optional": false,
            "field": "pageSize",
            "description": "<p>当前页展示数量</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "传参示例",
          "content": "{\n\t \"cardId\": \"1221\",\n\t \"pageNum\": \"1\",\n\t \"pageSize\": \"10\"\n}",
          "type": "String"
        }
      ]
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "sleepId",
            "description": "<p>主键id</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "guid",
            "description": "<p>guid</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "cardId",
            "description": "<p>卡号</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "name",
            "description": "<p>姓名</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "birthday",
            "description": "<p>生日</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "address",
            "description": "<p>地址</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "phone",
            "description": "<p>电话</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "age",
            "description": "<p>年龄</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "height",
            "description": "<p>身高</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "weight",
            "description": "<p>体重</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "BMI",
            "description": "<p>体质指数</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "datUrl",
            "description": "<p>dat路径</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "technician",
            "description": "<p>技术员</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "yhryPhone",
            "description": "<p>医护人员电话</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "recorder",
            "description": "<p>记录员</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "fax",
            "description": "<p>传真</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "doctor",
            "description": "<p>医生</p>"
          },
          {
            "group": "Success 200",
            "type": "SleepRecord",
            "optional": false,
            "field": "createTime",
            "description": "<p>生成报告日期</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "返回值示例",
          "content": "{\"page\":{\"pageNum\":1,\"pageSize\":10,\"total\":4,\"pages\":1},\"list\":[{\"sleepId\":\"6d9e0f28125b49b6af3f0194a5bb76d7\",\"guid\":\"121212\",\"cardId\":\"1221\",\"name\":\"测试\",\"sex\":\"男\",\"birthday\":\"2010-04-01\",\"address\":\"丽水花园\",\"phone\":\"15069861111\",\"age\":10,\"height\":\"160\",\"weight\":\"60\",\"datUrl\":null,\"url\":\"D:/file/dat/黄智.dat\",\"yhryName\":\"医护人员\",\"technician\":\"技术员\",\"yhryPhone\":\"13959592929\",\"recorder\":\"记录员\",\"fax\":\"150152\",\"doctor\":\"王医生\",\"createTime\":\"2020-07-03\",\"doctorAdvice\":null,\"bmi\":\"23.4\"},{\"sleepId\":\"2c9e208408154659bb890b165ba70b1e\",\"guid\":\"121212\",\"cardId\":\"1221\",\"name\":\"测试\",\"sex\":\"男\",\"birthday\":\"2010-04-01\",\"address\":\"丽水花园\",\"phone\":\"15069861111\",\"age\":10,\"height\":\"160\",\"weight\":\"60\",\"datUrl\":null,\"url\":\"D:/file/dat/李总6.4.dat\",\"yhryName\":\"医护人员\",\"technician\":\"技术员\",\"yhryPhone\":\"13959592929\",\"recorder\":\"记录员\",\"fax\":\"150152\",\"doctor\":\"王医生\",\"createTime\":\"2020-07-03\",\"doctorAdvice\":null,\"bmi\":\"23.4\"},{\"sleepId\":\"8ff67a2e536c466db4fb4ac52d2ace9c\",\"guid\":\"121212\",\"cardId\":\"1221\",\"name\":\"测试\",\"sex\":\"男\",\"birthday\":\"2010-04-01\",\"address\":\"丽水花园\",\"phone\":\"15069861111\",\"age\":10,\"height\":\"160\",\"weight\":\"60\",\"datUrl\":null,\"url\":\"D:/file/dat/黄智.dat\",\"yhryName\":\"医护人员\",\"technician\":\"技术员\",\"yhryPhone\":\"13959592929\",\"recorder\":\"记录员\",\"fax\":\"150152\",\"doctor\":\"王医生\",\"createTime\":\"2020-07-03\",\"doctorAdvice\":null,\"bmi\":\"23.4\"},{\"sleepId\":\"9d7324b68a6e40eabbc10e5895949104\",\"guid\":\"121212\",\"cardId\":\"1221\",\"name\":\"测试\",\"sex\":\"男\",\"birthday\":\"2010-04-01\",\"address\":\"丽水花园\",\"phone\":\"15069861111\",\"age\":10,\"height\":\"160\",\"weight\":\"60\",\"datUrl\":null,\"url\":\"D:/file/dat/李总6.4.dat\",\"yhryName\":\"医护人员\",\"technician\":\"技术员\",\"yhryPhone\":\"13959592929\",\"recorder\":\"记录员\",\"fax\":\"150152\",\"doctor\":\"王医生\",\"createTime\":\"2020-07-03\",\"doctorAdvice\":null,\"bmi\":\"23.4\"}]}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/hife/controller/SleepController.java",
    "groupTitle": "用户接口",
    "name": "PostSleepanalysisSleepGetsleeprecode"
  }
] });
