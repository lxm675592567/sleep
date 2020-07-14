package com.hife.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.hife.EDFUtils.EDFRecord;
import org.apache.commons.lang.ArrayUtils;
import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.Math.abs;

public class SleepResultUtil {

    public static JSONObject getSleepResult(JSONObject json) throws ParseException {
        List<Integer> prList = (List<Integer>) json.get("prList");
        List<Integer> RR = (List<Integer>) json.get("RR");
        List<Integer> PI = (List<Integer>) json.get("PI");
        String createTime = json.getJSONObject("time").getString("createTime");
        String endTime = json.getJSONObject("time").getString("endTime");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(createTime);
        //脉率复制list
        List<Integer> PrListCopy2 = new ArrayList<>(prList); //睡眠类型使用
        List<Integer> PrListCopy = new ArrayList<>(prList);
        List<Integer> PrListCopys = new ArrayList<>(prList); //目的保留为0值

        //此次循环 将数值为0的值赋予给前值的区间(只有区间并不赋值,目的为了在最后画图中将)
        List<List<Integer>> qujianlist = new ArrayList<>();
        //处理为0值 给前值
        zerochuli(prList, PrListCopy, qujianlist);


        int a1 = 5;int a2 = 1;int a3 = 1;
        int b1 = 100;int b2 = 4;int b3 = 5;

        int D = 10; //几条
        int E = 0; //下降上升几次 //参数E:第一次对比7个数一个方差 前值-后值<E
        //int F = 1; //呼吸波判定常量
        int R = 6; //爬坡算法左右找4
        int n = 0;//最小二乘法(pingjun)对比参数小于n
        int s = 120; //时间60秒
        int hx = 1;//呼吸方差=0
        int H = 300; //时间合并

        /*
         * 一 三次滤波完成取得slp一15条一组数组 获得timeThree时间区间数组
         * */
        List<List<Integer>> slp = new ArrayList<>(); //存储数组
        List<Integer> timeThree = getSlp(prList, a1, a2, a3, b1, b2, b3, D, slp);
        List<List<Integer>> huxiqujian = new ArrayList<>();

        /*
         * 二 获得方差数组组成arr方差 ,并合并为list1(相等合并第一次合并)
         * */
        int[] arr = new int[slp.size()];
        List<List<Integer>> list1 = huxifangchashuzu(RR, D, E, slp, timeThree, huxiqujian, arr,PI);

        /*
         * 三 hxlists为呼吸方差值数组 arrlist3为合并时间区间数组(相等合并第二次合并,取消<5合并)
         * */
        List<List<Integer>> hxlists = new ArrayList<>();
        List<List<Integer>> arrlist3 = new ArrayList<>();
        huxifangcha(hx, huxiqujian, list1, hxlists, arrlist3);

        /*
         * 四 用arrlist3的区间数组遍历心率,求的心率众数组array(区间+每段众数值) arrayhx为呼吸心率方差值,为呼吸紊乱做准备
         * */
        List<List<Integer>> array = new ArrayList(); //心率众数组
        List<List<Integer>> arrayhx = new ArrayList(); //呼吸心率方差组
        shijianzhongshujihe(prList, RR, PrListCopy, arrlist3, array, arrayhx,PI);

        //最终结果带0
        List<List<Long>> result = new ArrayList<>();
        bdresult(PrListCopy, date, array, result);



        /*呼吸紊乱
         * 1获得呼吸,心率方差组,根据心率获得 arrayhx
         * 2排序 将呼吸从大到小排序
         * 3将呼吸最大值,与心率对比 两方都大于常数50 ,则定为呼吸紊乱
         * 4将呼吸紊乱数组拿出
         * 5时间段表上呼吸方差值
         * 6画图
         * */
        int h = 50;
        int Q = 120;//呼吸紊乱常数
        List<List<Integer>> hxwl = new ArrayList<>();//呼吸紊乱前50结果
        List<List<Object>> resultshxxl = new ArrayList<>(); //呼吸紊乱原数据结果
        getHxwl(RR, date, arrayhx, h, Q, hxwl, resultshxxl,PI);

        /*
         * 五 爬坡算法 1先将array数组小于120秒合并给上一值 2爬坡最多6个
         * */
        getpapo(R, s, array,H);

        /*
         *六 爬坡算法完成后,获得区间原始数组 形成众数数组集合averageList 集合转数组pingjunshu
         * */
        List<Integer> averageList = new ArrayList();
        getpapohouhebing(date, PrListCopy, array, averageList);
        int[] pingjunshu = averageList.stream().mapToInt(Integer::valueOf).toArray();
        double pingjun = getpingjun(prList, PrListCopy, PrListCopys); //生成三分钟众数平均数-3

        /*
         * 七 最后一次合并相等 形成最终数组listTime
         * */
        List<List<Integer>> listTime = new ArrayList<>();
        getHebin(PrListCopy, n, array, averageList, pingjunshu, pingjun, listTime);
        listTime.get(0).set(0,0);
        //将最后通过listTime 形成最终数组
        List<List<Long>> results = new ArrayList<>();
        List<Integer> PrListCopy1 = new ArrayList<>(prList);
        List<List<Object>> results2 = new ArrayList<>();
        xingchengqujian(PrListCopy, qujianlist, date, listTime, results, PrListCopy1, results2);

        //熟睡 中度熟睡 浅睡 清醒
        //先形成波峰波谷区间数组
        List<List<List<Object>>> smresults = new ArrayList<>();
        //listTime添加坐标
        List<List<Integer>> listTimesm = new ArrayList<>(listTime);
        for (int i = 0; i < listTimesm.size(); i++) {
            listTimesm.get(i).add(i);
        }

        bofengguqujian(listTimesm, smresults);

        //形成睡眠类型数组
        List<List<Object>> shushui = new ArrayList<>();
        List<List<Object>> qingxing = new ArrayList<>();
        List<List<Object>> qianshui = new ArrayList<>();
        List<List<Object>> zhongdushushui = new ArrayList<>();
        Shuimianshuzu shuimianshuzu = new Shuimianshuzu(smresults, shushui, qingxing, qianshui, zhongdushushui).invoke();
        shushui = shuimianshuzu.getShushui();
        qingxing = shuimianshuzu.getQingxing();
        qianshui = shuimianshuzu.getQianshui();

        //getssqc(listTimesm, shushui, qingxing);//熟睡去掉前最后值

        //展示睡眠类型数据(测试)
        List<List<Long>> resultsshushui = new ArrayList<>();
        List<List<Long>> resultsqingxing = new ArrayList<>();
        List<List<Long>> resultszdshushui = new ArrayList<>();
        List<List<Long>> resultsqianshui = new ArrayList<>();
        quxianshuzu(PrListCopy2, date, shushui, qingxing, qianshui, zhongdushushui, resultsshushui, resultsqingxing, resultszdshushui, resultsqianshui);

        //一 获取初始熟睡
        int sssj = 300;int sszhi = 4;
        //List<List<List<Object>>> qingxingList = new ArrayList<>();
        List<List<List<Object>>> shushuiList = new ArrayList<>();
        getchushishushui(listTimesm, shushui, shushuiList,qingxing,sssj,sszhi);

        List<List<List<Object>>> shushuiNewList = new ArrayList<>();
        //int sscishu = 5;
        int sscishu = (int) Math.round((double) result.size()/5400);//总时间/90分钟
        getPaixu(shushuiList, shushuiNewList,sscishu);//按时间大小排序
        //二 获取清醒
        int qxcs = 3;
        List<List<Object>> qingxingList = new ArrayList<>();
        qingxingList = getQingXing(listTimesm, qingxing, shushuiNewList, qingxingList,qxcs);//找到高点后进行左右3次小于它对比

        //三 清醒5分钟内不允许有深睡,有的话则干掉深睡
        int qxsj = 300;
        //List<List<Object>> shushuiNew = new ArrayList<>(shushui);
        //getqxssNew(shushuiNew,qingxingList,qxsj);
        getqxss(shushuiList, qingxingList,qxsj);

        //四 深睡5分钟内不允许有深睡,有的话则干掉深睡
        int sssjs = 300;
        List<List<List<Object>>> ssNewList = new ArrayList<>();
        getPaixu(shushuiList, ssNewList,sscishu);//使用shushuiNewList提前排序按时间大小排序
        getssss(ssNewList,sssjs,shushuiList);

        //五 清醒到深睡之前超过40分钟,则找回一次清醒
        int qxsssj = 2400;
        qingxingList = getqxss60(listTimesm, qingxing, shushuiList, qingxingList,qxsssj);

        //六 中度熟睡 浅睡 谁近找谁
        //先遍历原始数组 熟睡1 清醒2 其他0  浅睡3 中4
        List<List<Object>> ssList = new ArrayList<>(); //熟睡list 去掉多重
        List<List<Object>> zdssList = new ArrayList<>(); //中度熟睡
        List<List<Object>> qsList = new ArrayList<>(); //浅睡

        getzhongqianshui(listTimesm, shushuiList, qingxingList, ssList, zdssList, qsList);

        //清醒 qingxingList  熟睡 ssList  浅睡qsList  中度熟睡 zdssList
        //七 求得方差众数 原始数组15个一组取得方差,然后再取得众数(中数) 心率和呼吸相加
        List<List<Integer>> tdList = new ArrayList<>(); //体动
        //int swxlzs = 0;int xlzd =0;int swhxzs = 0; int hxzd = 0; int zhh = 0;
        GetTiDong getTiDong = new GetTiDong(PI, PrListCopys, arrayhx, tdList).invoke();
        //int swxlzs = getTiDong.getSwxlzs();int swhxzs = getTiDong.getSwhxzs();int hxzd = getTiDong.getHxzd();

        //八 中度熟睡,超过三次体动,改为浅睡 清醒改为快速眼动
        getQianShui(zdssList, qsList, tdList);

        //九 判断清醒快速眼动 list中有值则为清醒 快速眼动值为5
        List<List<Object>> qxList = new ArrayList<>();//清醒
        List<List<Object>> ksydList = new ArrayList<>();//快速眼动
        getksyd(listTimesm, qingxingList, tdList, qxList, ksydList);

        //呼吸紊乱 体动中位数
        int hxwlzws = getHxwlzws(arrayhx);
        List<List<Integer>> hxwlList = new ArrayList<>(); //呼吸紊乱
        gethxwl(arrayhx, hxwlzws, hxwlList);

        //心率  原始15个取方差,前50个值
        List<List<Integer>> xlList = new ArrayList();
        getxinlv(PrListCopys, xlList);

        //睡眠类型最终结果
        List<List<Object>> smlxResult = new ArrayList<>();
//        List<List<Object>> smlx = getSmlx(qujianlist, ssList, zdssList, qsList, qxList, ksydList, hxwlList, smlxResult,date,xlList);
        JSONObject smlx = getSmlx(qujianlist, ssList, zdssList, qsList, qxList, ksydList, hxwlList, smlxResult,date,xlList);

        //心率最终结果
        List<List<Object>> xlResult = new ArrayList<>();
        getXlResult(PrListCopy, date, xlList, xlResult);

        JSONObject object = new JSONObject();
        object.put("prListCopy",PrListCopy);//原始值
        object.put("listTime",listTime);//原始值
        object.put("result",results2);//曲线图结果
        object.put("xlResult",xlResult);//心率
        object.put("smlxResult",smlx);//睡眠类型
//        object.put("ksydList",ksydList);//快速眼动
        object.put("qxList",qxList);//清醒
//        object.put("ssList",ssList);//熟睡
//        object.put("zdssList",zdssList);//中度熟睡
//        object.put("qsList",qsList);//浅睡
//        object.put("tdList",tdList);//体动
//        object.put("hxwlList",hxwlList);//呼吸紊乱

        return object;
    }

    private static int getHxwlzws(List<List<Integer>> arrayhx) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < arrayhx.size(); i++) {
            int integer = arrayhx.get(i).get(2);//呼吸-搏动指数
            list.add(integer);
        }
        int hxwlzws = (int) median(list);
        hxwlzws = (int) ((int)hxwlzws*1.5);
        return hxwlzws;
    }

    private static void getssqc(List<List<Integer>> listTimesm, List<List<Object>> shushui, List<List<Object>> qingxing) {
        for (int i = 0; i < shushui.size(); i++) {
            int size = (int) shushui.get(i).get(3);
            if (size==listTimesm.size()-1 || size ==0){
                qingxing.add(shushui.get(i));
                shushui.remove(i);
            }
        }
    }

    private static void getXlResult(List<Integer> prList, Date date, List<List<Integer>> xlList, List<List<Object>> xlResult) {
        for (int i = 0; i < prList.size(); i++) {
            List<Object> list = new ArrayList<>();
            long ts = date.getTime();
            for (int j = 0; j < xlList.size(); j++) {
                List<Integer> integers = xlList.get(j);
                Integer qian = integers.get(0); //区间前值
                Integer hou = integers.get(1); //区间后值
                if (i >= qian && i <= hou) {
                    list.add(ts + i * 1000);
                    if (prList.get(i) == 0) {
                        list.add(0);
                    } else {
                        list.add(integers.get(2));
                    }

                    break;
                } else if (j == xlList.size() - 1) {
                    list.add(ts + i * 1000);
                    list.add(0);
                }
            }
            xlResult.add(list);
        }
    }
    private static JSONObject getSmlx(List<List<Integer>> qujianlist, List<List<Object>> ssList, List<List<Object>> zdssList, List<List<Object>> qsList, List<List<Object>> qxList, List<List<Object>> ksydList, List<List<Integer>> hxwlList, List<List<Object>> smlxList, Date date ,List<List<Integer>> xlList) {
        //先将五值依次遍历
        //清醒A 快速眼动B 深睡C 中度深睡D 浅睡E 呼吸紊乱F 空白G
        List<List<Object>> xl = new ArrayList<>();
        List<List<Object>> qt = new ArrayList<>();
        long ts = date.getTime();
        for (int i = 0; i < qxList.size(); i++) {//清醒
            List<Object> list = new ArrayList<>();
            int qian = (int) qxList.get(i).get(0);
            int hou = (int)qxList.get(i).get(1) ;
            list.add(ts + qian * 1000);
            list.add(ts + hou * 1000);
            list.add(60);
            list.add("A");
            qt.add(list);
        }
        for (int i = 0; i < ksydList.size(); i++) {//快速眼动
            List<Object> list = new ArrayList<>();
            int qian =  (int) ksydList.get(i).get(0) ;
            int hou =  (int) ksydList.get(i).get(1) ;
            list.add(ts + qian * 1000);
            list.add(ts + hou * 1000);
            list.add(60);
            list.add("B");
            qt.add(list);
        }
        for (int i = 0; i < ssList.size(); i++) {//深睡
            List<Object> list = new ArrayList<>();
            int qian =  (int) ssList.get(i).get(0) ;
            int hou =  (int)ssList.get(i).get(1) ;
            list.add(ts + qian * 1000);
            list.add(ts + hou * 1000);
            list.add(60);
            list.add("C");
            qt.add(list);
        }
        for (int i = 0; i < zdssList.size(); i++) {//中度深睡
            List<Object> list = new ArrayList<>();
            int qian =  (int) zdssList.get(i).get(0) ;
            int hou =  (int)zdssList.get(i).get(1) ;
            list.add(ts + qian * 1000);
            list.add(ts + hou * 1000);
            list.add(60);
            list.add("D");
            qt.add(list);
        }
        for (int i = 0; i < qsList.size(); i++) {//浅睡
            List<Object> list = new ArrayList<>();
            int qian =  (int) qsList.get(i).get(0) ;
            int hou =  (int) qsList.get(i).get(1) ;
            list.add(ts + qian * 1000);
            list.add(ts + hou * 1000);
            list.add(60);
            list.add("E");
            qt.add(list);
        }
        for (int i = 0; i < hxwlList.size(); i++) {//呼吸紊乱
            List<Object> list = new ArrayList<>();
            int qian = (int) hxwlList.get(i).get(0) ;
            int hou = (int) hxwlList.get(i).get(1) ;
            list.add(ts + qian * 1000);
            list.add(ts + hou * 1000);
            list.add(30);
            list.add("F");
            qt.add(list);
        }
        for (int i = 0; i < qujianlist.size(); i++) {//空白
            List<Object> list = new ArrayList<>();
            int qian = (int) qujianlist.get(i).get(0) ;
            int hou = (int) qujianlist.get(i).get(1) ;
            list.add(ts + qian * 1000);
            list.add(ts + hou * 1000);
            list.add(60);
            list.add("G");
            qt.add(list);
        }
        for (int i = 0; i < xlList.size(); i++) {//快速眼动
            List<Object> list = new ArrayList<>();
            int qian =  (int) xlList.get(i).get(0) ;
            int hou =  (int) xlList.get(i).get(1) ;
            int zhi =  (int) xlList.get(i).get(2) ;
            list.add(ts + qian * 1000);
            list.add(ts + hou * 1000);
            list.add(zhi);
            list.add("H");
            xl.add(list);
        }

        List<List<Object>> arrayqtSort = new ArrayList(qt);
        arrayqtSort = arrayqtSort.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Long.valueOf((Long) o1.get(0)).compareTo(Long.valueOf((Long) o2.get(0)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
        List<List<Object>> arrayxlSort = new ArrayList(xl);
        arrayxlSort = arrayxlSort.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Long.valueOf((Long) o1.get(0)).compareTo(Long.valueOf((Long) o2.get(0)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
        JSONObject arraySort = new JSONObject();
        //心率合并
        List<List<Object>> arrayxlhb = new ArrayList();
        int xlty=1;int xlq=0;int xlh=0;int xlzs=0;
        for (int i = 1; i < arrayxlSort.size(); i++) {
            List<Object> list = new ArrayList();
            long qian = (Long)arrayxlSort.get(i - 1).get(1);
            long hou = (Long)arrayxlSort.get(i).get(0);

            int zhi = (int) arrayxlSort.get(i - 1).get(2);
            int hzhi = (int) arrayxlSort.get(i).get(2);
            if (qian==hou){
                if (xlty==1){
                    xlq = i-1;
                }
                xlzs = zhi+xlzs;
                xlty++;
            }else {
                if (xlty==1){
                    xlzs = zhi;
                }
                xlh = i-1;
                int pj = xlzs/xlty;
                list.add(xlq);list.add(xlh);list.add(pj);
                arrayxlhb.add(list);
                //System.out.println("xlq="+xlq+"  xlh="+xlh+"  xlty="+xlty+"  xlzs="+xlzs+"  pj="+pj);
                xlq = i;xlty = 1;xlzs = 0;
            }
            if (i==arrayxlSort.size()-1){
                xlh = i;
                if (xlty==1){
                    xlzs = hzhi;
                }
                int pj = xlzs/xlty;
                list.add(xlq);list.add(xlh);list.add(pj);
                arrayxlhb.add(list);
            }
        }

        for (int i = 0; i < arrayxlhb.size(); i++) {
            List<Object> list = arrayxlhb.get(i);
            for (int j = 0; j < list.size(); j++) {
                int qian = (int) list.get(0);
                int hou = (int) list.get(1);
                int zhi = (int) list.get(2);
                for (int k = qian; k <= hou; k++) {
                    arrayxlSort.get(k).set(2,zhi);
                }
            }
        }
        arraySort.put("xl",arrayxlSort);
        arraySort.put("qt",arrayqtSort);
        return arraySort;
    }


    public static JSONObject getJcszjs(List<EDFRecord> records, JSONObject time, List<Integer> RR, List<Integer> prListCopy, List<List<Integer>> listTime, List<List<Object>> qxList) throws JSONException, ParseException {
        String createTime = time.getString("createTime");
        String endTime = time.getString("endTime");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(createTime);

        qxList = qxList.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Integer.valueOf((Integer)o1.get(0)).compareTo(Integer.valueOf((Integer) o2.get(0)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
        /*
         * 睡眠计算
         * */
        int miao = records.size() * 2; //睡眠总秒数
        //开始入睡时间
        long startTimes = listTime.get(1).get(0);
        for (int i = 1; i < listTime.size(); i++) {
            Integer integer1 = listTime.get(i - 1).get(2);
            Integer integer = listTime.get(i).get(2);
            if (integer<integer1){
                startTimes = listTime.get(i).get(0);
                break;
            }
        }
        String startSleep = simpleDateFormat.format(new Date(date .getTime() + startTimes*1000));

        //结束入睡时间
        Integer qxend = (Integer) qxList.get(qxList.size() - 1).get(0);
        Integer ltend = listTime.get(listTime.size() - 1).get(0);
        long sendTimes = ltend;
        if (qxend!=ltend){
            sendTimes = listTime.get(listTime.size() - 1).get(1);
        }
        String endSleep = simpleDateFormat.format(new Date(date .getTime() + sendTimes*1000));

        //睡眠时间
        long smsj = sendTimes - startTimes;
        double smsjSleep = miaozhuanfen(smsj);

        //睡眠总时间
        long qxzsj =0; //清醒总时间
        long smjxsj =0;//睡眠觉醒时间
        int jxcsSleep = 0;//觉醒次数
        for (int i = 0; i < qxList.size(); i++) {
            int i1 = (int)qxList.get(i).get(1) - (int)qxList.get(i).get(0);
            if (i>0){
                smjxsj  = smjxsj+i1;
                jxcsSleep++;
            }
            qxzsj = qxzsj+i1;
        }
        int qxzh = (int) qxList.get(qxList.size() - 1).get(3);
        int shouqx = (int) qxList.get(0).get(1);
        if (qxzh==listTime.size()-1){
            int qxzhq = (int) qxList.get(qxList.size() - 1).get(0);
            int qxzhh = (int) qxList.get(qxList.size() - 1).get(1);
            smjxsj = smjxsj-(qxzhh-qxzhq);
            shouqx=shouqx+(qxzhh-qxzhq);
            jxcsSleep = jxcsSleep - 1; //觉醒次数
        }
        smjxsj = smjxsj/60;
        long smzsj = smsj - (qxzsj-shouqx);
        String smzsjSleep = miaozhuanfenmiao(smzsj);

        //总记录时间
        long size = prListCopy.size();
        String zjlsjSleep = miaozhuanfenmiao(size);

        //睡眠效率
        DecimalFormat df = new DecimalFormat("#0.0");
        String smxl = df.format((double)smzsj/(double)size*100)+"%";
        //double smxl = 5/1;

        //睡眠潜伏期 开始入睡时间 秒转分钟
        //String smqfz = df.format((double)startTimes/60);
        double smqfz = miaozhuanfen(startTimes);

        //睡眠
        double smSleep = miaozhuanfen(smzsj);

        //清醒 qxzsj
        double qxSleep = miaozhuanfen(qxzsj);

        //占比
        String smzbSleep = df.format((double)smSleep/(qxSleep+smSleep)*100);//睡眠
        String qxzbSleep = df.format((double)qxSleep/(qxSleep+smSleep)*100);//清醒

        /*
         * AHI计算
         * */
        List<List<Object>> AHIlist = new ArrayList<>();

        int ahisz= 1;
        int ahik = 0;
        int ahizuixiao = 1000; //最小值
        int ahizuida = 0; //最大值
        int ahizongshu = 0; //总数
        int ahipingjun = 0; //平均
        for (int i = 1; i < RR.size(); i++) {
            List<Object> list = new ArrayList<>();
            Integer integer = RR.get(i);
            Integer integer1 = RR.get(i-1);
            if (integer1 ==0){
                if (integer == 0){
                    if (ahisz == 1){
                        ahik = i;
                    }else if (i==RR.size()-1 && ahisz<90 && ahisz>=10){
                        list.add(ahik);
                        list.add(i);
                        list.add(ahisz);
                        AHIlist.add(list);
                    }
                    ahisz++;
                }else {
                    if (ahisz<90 && ahisz>=10){
                        list.add(ahik);
                        list.add(i);
                        list.add(ahisz);
                        AHIlist.add(list);
                    }
                    ahisz = 1;
                }
            }
        }

        for (int i = 0; i < AHIlist.size(); i++) {
            int ahi = (int) AHIlist.get(i).get(2);
            if (ahi<ahizuixiao){//最小值
                ahizuixiao = ahi;
            }
            if (ahi>ahizuida){
                ahizuida = ahi;
            }
            ahizongshu = ahizongshu+ ahi;
        }
        if(AHIlist.size()>0){
            ahipingjun = ahizongshu/AHIlist.size();
        }else {
            ahizuixiao = 0;
        }

        //小时
        int hour = miao / 3600;
        //AHI
        int AHI = AHIlist.size() / hour;

        /*
         * 氧减计算
         * */
        JSONObject getjibenshuzhi = getjibenshuzhi(records, createTime,qxList);
        String oxygenThree = getjibenshuzhi.getString("OxygenThree");//氧减百分3事件
        String oxygenFour = getjibenshuzhi.getString("OxygenFour");//氧减百分4事件

        //睡眠分析
        JSONObject smfxJson = new JSONObject();//睡眠分析
        smfxJson.put("createTime",createTime);//开始记录时间
        smfxJson.put("startSleep",startSleep);//入睡时间
        smfxJson.put("endSleep",endSleep);//最后睡眠时刻
        smfxJson.put("endTime",endTime);//记录结束时间
        smfxJson.put("zjlsjSleep",zjlsjSleep);//总记录时间
        smfxJson.put("smsjSleep",smsjSleep);//睡眠时间
        smfxJson.put("smzsjSleep",smzsjSleep);//睡眠总时间
        smfxJson.put("smxl",smxl);//睡眠效率
        smfxJson.put("smjxsj",smjxsj);//睡眠觉醒时间
        //睡眠清醒持续状态
        JSONObject cxztJson = new JSONObject();//睡眠/清醒持续状态
        cxztJson.put("qxSleep",qxSleep);//清醒
        cxztJson.put("qxzbSleep",qxzbSleep);//清醒占比
        cxztJson.put("smSleep",smSleep);//睡眠
        cxztJson.put("smzbSleep",smzbSleep);//睡眠占比
        cxztJson.put("smqfz",smqfz);//睡眠潜伏期
        cxztJson.put("jxcsSleep",jxcsSleep);//觉醒次数

        //呼吸情况分析
        JSONObject hxqkfxJson = new JSONObject();//呼吸情况分析
        hxqkfxJson.put("hxsjzs",AHIlist.size());//呼吸事件总数
        hxqkfxJson.put("oxygenFour",oxygenFour);//氧减百分4事件
        hxqkfxJson.put("oxygenThree",oxygenThree);//氧减百分3事件
        hxqkfxJson.put("AHI",AHI);//AHI
        hxqkfxJson.put("hxzd",ahizuixiao);//呼吸最短
        hxqkfxJson.put("hxzc",ahizuida);//呼吸最长
        hxqkfxJson.put("hxpj",ahipingjun);//呼吸平均(秒)

        //血氧饱和度分析
        JSONObject xybhdJson = getjibenshuzhi.getJSONObject("xybhdJson");
        //脉率分析
        JSONObject mlfxJson = getjibenshuzhi.getJSONObject("mlfxJson");

        //睡眠清醒图
        JSONObject smqxJson = new JSONObject();
        smqxJson.put("qxzbSleep",qxzbSleep);//清醒占比
        smqxJson.put("smzbSleep",smzbSleep);//睡眠占比

        //氧减持续时间次数图
        List<List<Object>> arrayyj = (List<List<Object>>)getjibenshuzhi.get("arrayyj"); //氧减持续时间次数图

        //脉率直方图
        List<List<Object>> arraymlzf = (List<List<Object>>)getjibenshuzhi.get("arraymlzf"); //脉率直方图

        JSONObject object = new JSONObject();
        object.put("smfx",smfxJson);//睡眠分析
        object.put("cxzt",cxztJson);//睡眠清醒持续状态
        object.put("hxqkfx",hxqkfxJson);//呼吸情况分析
        object.put("xybhdfx",xybhdJson);//血氧饱和度分析
        object.put("mlfx",mlfxJson);//脉率分析
        object.put("smqx",smqxJson);//睡眠清醒图
        object.put("arrayyj",arrayyj);//氧减持续时间次数图
        object.put("arraymlzf",arraymlzf);//脉率直方图
        object.put("AHI",AHI);//AHI
        object.put("ODI",oxygenThree);//ODI
        return object;
    }

    private static double miaozhuanfen(long smsj) {
        long minutes = smsj / 60;
        double remainingSeconds = smsj % 60 ;
//        remainingSeconds = Math.round(remainingSeconds*10)/10.0;
//        double smsjSleep = minutes+remainingSeconds;
        double smsjSleep =Math.round(minutes*10)/10.0;
        return smsjSleep;
    }

    private static String miaozhuanfenmiao(long time) {
        StringBuilder stringBuilder = new StringBuilder();
        long hour = time / 3600;
        long minute = time / 60 % 60;
//        Integer second = time % 60;
        if(hour<10){
            stringBuilder.append("0");
        }
        stringBuilder.append(hour);
        stringBuilder.append(":");
        if(minute < 10){
            stringBuilder.append("0");
        }
        stringBuilder.append(minute);
        return stringBuilder.toString();
    }

    private static JSONObject getjibenshuzhi(List<EDFRecord> records, String createTime, List<List<Object>> qxList) throws JSONException, ParseException {

        //血氧
        int totalSmsj = 0;    //总计总睡眠时间
        int totalJcsj = 0;    //总计总监测时间
        int sumSpO2 = 0;      //总数
        int mostSpO2 = 0;    //最多
        int leastSpO2 = 100;    //最少
        int ninetySpO2 = 0;   //小于百分之九十
        int ninetySmSpO2 = 0;
        int fourSecond =0;
        int threeSecond =0;
        int [] oxygenTime = {92,90,88,85,80,75,70,65,60,55,50,40};//睡眠判定值
        int [] spO2Time = new int[12];//睡眠时间秒
        int [] spO2jxTime = new int[12];//血氧觉醒时间
        float[] spTime = new float[12];
        //脉率
        int sumHr = 0;      //总数
        int mostHr = 0;    //最多
        int leastHr = 100;    //最少
        float Avgvalue = 0;   //脉率差

        int [] oxygenLeftTime = {0,31,61,81,91,101,111,121,131,141,151,161,171,181,191,201};//睡眠判定值左
        int [] oxygenrightTime = {30,60,80,90,100,110,120,130,140,150,160,170,180,190,200,1000};//睡眠判定值右
        int [] hrTime = new int[16];//睡眠时间秒
        int [] hrjxTime = new int[16];//睡眠觉醒时间秒
        float[] bpTime = new float[16]; //睡眠时间分
        int xysh=0;
        org.json.JSONObject xyjson = new org.json.JSONObject();

        ArrayList<Short> o2 = new ArrayList<>();

        int sjo2 = 0;
        int zeroO2 = 0;
        for (EDFRecord record : records) {
            short[] spO2 = record.SpO2;
            for (short i : spO2) {
                if(i<=100) {
                    sumSpO2 = sumSpO2 + i;
                    //最大值
                    if (i > mostSpO2) {
                        mostSpO2 = i;
                    }
                    //最小值
                    if (i < leastSpO2) {
                        leastSpO2 = i;
                    }
                    //90
                    if (i < 88) {
                        ninetySpO2 = ninetySpO2+1;
                    }
                    for (int k = 0; k < qxList.size(); k++) {
                        List<Object> objects = qxList.get(k);
                        int kai = (int) objects.get(0);
                        int shi = (int) objects.get(1);
                        if (sjo2<kai || sjo2>shi ){
                            if (i < 88) {
                                ninetySmSpO2 = ninetySmSpO2+1;
                            }
                        }
                    }
                    //睡眠时间比例图
                    for(int j=0;j<spO2Time.length;j++){
                        if(i<oxygenTime[j]){
                            spO2Time[j] = spO2Time[j]+1;
                        }
                        for (int k = 0; k < qxList.size(); k++) {
                            List<Object> objects = qxList.get(k);
                            int kai = (int) objects.get(0);
                            int shi = (int) objects.get(1);
                            if (sjo2>=kai && sjo2<=shi && i<oxygenTime[j]){
                                spO2jxTime[j] = spO2jxTime[j]+1;
                            }
                        }
                    }

                    if(i>=95&&i<=100){
                        xysh = xysh+1;
                        Double xy = Double.valueOf( xysh / sumSpO2);
                        String.format("%.2f", xy).toString();
                        xyjson.put("95xysh",xy);
                    }
                }else {
                    i=0;
                    zeroO2++;
                }
                o2.add(i);
                totalJcsj = totalJcsj+1;
                sjo2++;
            }
        }
        int chazhi = 0;
        for (int i = 0; i < qxList.size(); i++) {
            List<Object> objects = qxList.get(i);
            int kai = (int) objects.get(0);
            int shi = (int) objects.get(1);
            int cha = shi - kai;
            chazhi = chazhi + cha;
        }
        totalSmsj = totalJcsj - chazhi;
        //脉率
        ArrayList<Short> pr = new ArrayList<>();
        int sj = 0;
        int zeroxl = 0;
        for (EDFRecord record : records) {
            short[] hr = record.HR;
            for (short i : hr) {
                if (i < 200) {
                    sumHr += i;
                    //最大值
                    if (i > mostHr) {
                        mostHr = i;
                    }
                    //最小值
                    if (i < leastHr) {
                        leastHr = i;
                    }
                    //睡眠时间比例图
                    for (int j = 0; j < oxygenLeftTime.length; j++) {

                        if (i > oxygenLeftTime[j] && i < oxygenrightTime[j]) {
                            hrTime[j] = hrTime[j] + 1;
                        }

                        for (int k = 0; k < qxList.size(); k++) {
                            List<Object> objects = qxList.get(k);
                            int kai = (int) objects.get(0);
                            int shi = (int) objects.get(1);
                            if (sj>=kai && sj<=shi && i > oxygenLeftTime[j] && i < oxygenrightTime[j] ){
                                hrjxTime[j] = hrjxTime[j] + 1;
                            }
                        }
                    }
                }else {
                    zeroxl++;
                }
                pr.add(i);
                sj++;
            }

        }
        //脉率平均差
        float avgHr = new BigDecimal((float) sumHr/(totalJcsj-zeroxl)).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        for (EDFRecord record : records) {
            short[] hr = record.HR;
            for (short i : hr) {
                if (i < 200) {
                    //脉率差
                    Avgvalue = Math.abs(i - avgHr) + Avgvalue;
                }
            }
        }

        float avgSpO2= new BigDecimal((float)sumSpO2/(totalJcsj-zeroO2) ).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        //血氧<88(90)% 在总睡眠时间中比值:
        float ninetySMSpO2 = new BigDecimal((float)ninetySmSpO2/totalSmsj ).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        //血氧< 88(90)% 在总监测时间中比值:
        float ninetyJCSpO2 = new BigDecimal((float)ninetySpO2/totalJcsj ).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        //转换成小时
        float totalSmHours = new BigDecimal((float) o2.size() / 60/60).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        //short totalJcHours = (short) ((totalSmsj % ( 60 * 60 * 24)) / (60 * 60));
        //氧减4%指数
        float fourZsSpO2 = fourSecond/totalSmHours;
        //氧减3%指数
        float threeZsSpO2 = threeSecond/totalSmHours;
        int pingjunxuyang = (int) avgSpO2;
        JSONArray arrayxy = new JSONArray();
        for (int i = 0; i < spO2jxTime.length; i++) {
            JSONObject object = new JSONObject();
            DecimalFormat df = new DecimalFormat("#0.0");
            int jx = spO2jxTime[i];
            int O2 = spO2Time[i];
            int sm = O2-jx;
            String O2fen = df.format(Math.round((double)O2/(double)60));
            String jxfen = df.format(Math.round((double)jx/(double)60));
            String smfen = df.format(Math.round((double)sm/(double)60));
            object.put("zhi","<"+oxygenTime[i]+"%");
            object.put("O2",O2fen);
            object.put("sm",smfen);
            object.put("jx",jxfen);
            arrayxy.add(object);
        }

        JSONArray arrayml = new JSONArray();
        for (int i = 0; i < hrjxTime.length; i++) {
            JSONObject object = new JSONObject();
            DecimalFormat df = new DecimalFormat("#0.0");
            int jx = hrjxTime[i];
            int hr = hrTime[i];
            int sm = hr-jx;
            String hrfen = df.format((double)hr/(double)60);
            String jxfen = df.format((double)jx/(double)60);
            String smfen = df.format((double)sm/(double)60);
            String zhi = oxygenLeftTime[i]+"-"+oxygenrightTime[i];
            if (i==hrjxTime.length-1){
                zhi = oxygenLeftTime[i]+">";
            }
            object.put("zhi",zhi);
            object.put("hr",hrfen);
            object.put("sm",smfen);
            object.put("jx",jxfen);
            arrayml.add(object);
        }
        //睡眠时间
        for(int i=0;i<spO2Time.length;i++){
            if(spO2Time[i]>0) {
                float time=new BigDecimal((float) spO2Time[i]/60).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                spTime[i] =time;
            }
        }

        //脉率
        Avgvalue = new BigDecimal((float) Avgvalue/(totalJcsj-zeroxl)).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

        //脉率时间
        for(int i=0;i<hrTime.length;i++){
            if(hrTime[i]>0) {
                float time=new BigDecimal((float) hrTime[i]/60).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                bpTime[i] =time;
            }
        }
        ArrayList<Short> o2New = new ArrayList<>(o2);

        for (int i = o2.size() - 1; i >= 0; i--) {
            if (o2.get(i)==0){
                o2New.remove(i);
            }
        }

        int shou = 0; //首值
        int wei = 0; //尾值

        long shoumiao = 0; //首秒值
        long weimiao = 0; //尾秒值
        long pingjun =0;//平均数尾值
        long pingjundao =0;//平均数首值
        long weishushouzhi =0;//尾数首值

        long shoumiaowz = 0l; //首秒尾值
        int nextFour =0; //血氧下降4次数
        double biliFour = 0.04;
        int nextThree =0; //血氧下降3次数
        double biliThree = 0.03;
        List<List<Object>> arrayFour = new ArrayList<>();//血氧下降百分四
        List<List<Object>> arrayThree = new ArrayList<>();//血氧下降百分三

        nextFour = getxyxj(createTime, pingjunxuyang, o2New, shou, wei, nextFour, shoumiao, weimiao, pingjun, pingjundao, weishushouzhi, shoumiaowz, biliFour, arrayFour);//血氧下降百分4
        nextThree = getxyxj(createTime, pingjunxuyang, o2New, shou, wei, nextThree, shoumiao, weimiao, pingjun, pingjundao, weishushouzhi, shoumiaowz, biliThree, arrayThree);//血氧下降百分3

        List<List<Object>> arrayyj = new ArrayList<>(arrayFour);//氧减
        List<List<Object>> arraymlzf = new ArrayList<>();//脉率直方 脉率出现次数如45出现3次
        Map<Short, Integer> map = new HashMap<>();
        for (int i = 1; i < pr.size(); i++) {
            Short aShort = pr.get(i);
            if (aShort>300){
                continue;
            }
            if(map.containsKey(aShort)) {
                map.put(aShort, map.get(aShort).intValue()+1);
            }else {
                map.put(aShort, new Integer(1));
            }
        }
        Iterator<Short> iter = map.keySet().iterator();
        while(iter.hasNext()) {
            List<Object> mlzflist = new ArrayList<>();
            Short key = iter.next();
            if (key != 0){
                mlzflist.add(key);
                mlzflist.add(map.get(key));
                arraymlzf.add(mlzflist);
            }
        }

        DecimalFormat df = new DecimalFormat("#0.0");
        String OxygenFour = df.format((double)nextFour/(double)totalSmHours); //血氧下降百分4指数
        String OxygenThree = df.format((double)nextThree/(double)totalSmHours); //血氧下降百分3指数
        JSONObject object = new JSONObject();
        object.put("OxygenThree",OxygenThree);
        object.put("OxygenFour",OxygenFour);
        //血氧饱和度
        JSONObject xybhdJson = new JSONObject();//血氧饱和度
        xybhdJson.put("avgSpO2",avgSpO2);//平均血氧值
        xybhdJson.put("mostSpO2",mostSpO2);//最高血氧值
        xybhdJson.put("leastSpO2",leastSpO2);//最低血氧值
        xybhdJson.put("ninetySpO2",ninetyJCSpO2);//<90总睡眠比值
        xybhdJson.put("ninetySmSpO2", ninetySMSpO2);//<90总时间比值
        xybhdJson.put("nextFour",nextFour);//百分四次数
        xybhdJson.put("nextThree",nextThree);//百分三次数
        xybhdJson.put("OxygenFour",OxygenFour);//百分四指数
        xybhdJson.put("OxygenThree",OxygenThree);//百分三指数
        xybhdJson.put("xybhd",arrayxy);//血氧饱和度

        object.put("xybhdJson",xybhdJson);

        //脉率分析
        JSONObject mlfxJson = new JSONObject();//脉率分析
        mlfxJson.put("avgHr",avgHr);//平均脉率
        mlfxJson.put("mostHr",mostHr);//最快脉率
        mlfxJson.put("leastHr",leastHr);//最慢脉率
        mlfxJson.put("avgValue",Avgvalue);//平均脉率差
        mlfxJson.put("arrayml",arrayml);

        object.put("mlfxJson",mlfxJson);
        //arrayyj  arraymlzf
        object.put("arrayyj",arrayyj);
        object.put("arraymlzf",arraymlzf);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(createTime);
        int secondTimestamp = getSecondTimestamp(date);
        return object;
    }
    private static int getxyxj(String createTime, int pingjunxuyang, ArrayList<Short> o2New, int shou, int wei, int nextFour, long shoumiao, long weimiao, long pingjun, long pingjundao, long weishushouzhi, long shoumiaowz, double bili, List<List<Object>> array) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = sdf.parse(createTime);//时间转换data

        List<List<Integer>> sjszList = new ArrayList<>();
        for (int i = 0; i < o2New.size()-1; i++) {
            List<Integer> sjsz = new ArrayList<>();
            int cha = o2New.get(i) - o2New.get(i+1);
            ArrayList<Object> integers = new ArrayList<>();
            if (i==1){
                shou = o2New.get(i);
            }
            if(cha==0){
                weishushouzhi = i;
            }
            if(cha>=0){
                wei = o2New.get(i+1);
                weimiao = i+1l;
            }else{
                double v = shou * bili;
                if(shou-wei>v){
                    int a = Math.toIntExact(shoumiao);
                    for (int j = a; j < weimiao; j++) {
                        if(o2New.get(j)==pingjunxuyang){
                            pingjun = j;
                            continue;
                        }
                    }
                    int b = Math.toIntExact(weimiao);

                    for (int k = b; k >= shoumiao; k--) {
                        if(o2New.get(k)==pingjunxuyang){
                            pingjundao = k;
                            continue;
                        }
                    }

                    for (int j = a; j < weimiao; j++) {
                        if(o2New.get(j)==wei){
                            weishushouzhi = j;
                            break;
                        }
                    }

                    for (int k = b; k >= shoumiao; k--) {
                        if(o2New.get(k)==shou){
                            shoumiaowz = k;
                            continue;
                        }
                    }


                    if (shoumiao==weimiao){
                        break;
                    }
                    Date showDate = new Date(parse.getTime() + shoumiao*1000);
                    Date weiDate = new Date(parse.getTime() + weimiao*1000);
                    integers.add(sdf.format(showDate));
                    integers.add(sdf.format(weiDate));
                    integers.add(weishushouzhi);
                    integers.add(shoumiaowz);
//                    integers.add(weimiao-shoumiao);
//                    integers.add(weimiao-pingjun);
//                    integers.add(weishushouzhi-pingjundao); //尾数尾秒-平均首秒
                    integers.add(weishushouzhi-shoumiaowz); //尾数尾秒-首秒尾值
                    //integers.add(weimiao-pingjundao);
                    array.add(integers);
                    sjsz.add((int) shoumiao);
                    sjsz.add((int)weimiao);
                    sjszList.add(sjsz);
                    nextFour++;
                }
                shou = o2New.get(i+1);
                wei = o2New.get(i+2);
                shoumiao = i+1l; weimiao = i+2l;
            }
        }
        return nextFour;
    }


    private static void getxinlv(List<Integer> prListCopys, List<List<Integer>> xlList) {
        List<List<Integer>> shiwuxlfc = new ArrayList<>();  //先15个一组取得原始数据
        for (int i = 0; i < prListCopys.size(); i++) {
            if (prListCopys.size() <= 15 * i + 15) {
                break;
            } else {
                List<Integer> integers = prListCopys.subList(i * 15, 15 * i + 15);
                shiwuxlfc.add(integers);

            }
        }

        List<List<Integer>> arrayxl = new ArrayList();

        for (int i = 0; i < shiwuxlfc.size(); i++) {
            List<Integer> list = new ArrayList<>();
            List<Integer> integers = shiwuxlfc.get(i);
            int[] arrs =  integers.stream().mapToInt(Integer::valueOf).toArray();
            ModeUtil m = new ModeUtil();
            int c;
            m.number = new int[arrs.length];
            m.Mode(arrs, 0, arrs.length);
            int sa = m.number[0];
            list.add(i * 15);
            list.add(15 * i + 15);
            list.add(sa);
            arrayxl.add(list);
        }

        List<List<Integer>> xlSort = new ArrayList(arrayxl); //排序数组
        xlSort = xlSort.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Integer.valueOf(o1.get(2)).compareTo(Integer.valueOf(o2.get(2)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
        int xlsize = 0;
        if (xlSort.size()>50){
            xlsize = xlSort.size()-50;
        }

        for (int i = xlSort.size()-1; i >= xlsize; i--) {
            xlList.add(xlSort.get(i));
        }
    }

    private static void gethxwl(List<List<Integer>> arrayhx, int hxwlzs, List<List<Integer>> hxwlList) {
        for (int i = 0; i < arrayhx.size() ; i++) {
            List<Integer> integers = arrayhx.get(i);
            Integer hxz = integers.get(2); //呼吸值
            if (hxz > hxwlzs) {
                hxwlList.add(integers);
            }
        }
    }

    private static void getksyd(List<List<Integer>> listTimesm, List<List<Object>> qingxingList, List<List<Integer>> tdList, List<List<Object>> qxList, List<List<Object>> ksydList) {
        List<List<List<Object>>> ksydqjList = new ArrayList<>();//快速眼动区间list
        List<List<Object>> kydList = new ArrayList<>(qingxingList);//快速眼动
        for (int i = 0; i < qingxingList.size(); i++) {
            int kai = (int) qingxingList.get(i).get(0);
            int shi = (int) qingxingList.get(i).get(1);
            int size = (int) qingxingList.get(i).get(3);
            List<List<Object>> kdlist = new ArrayList<>();
            kdlist.add(qingxingList.get(i));
            if  (size==0 ||size ==listTimesm.size()-1 ){
                qxList.add(qingxingList.get(i));
                continue;
            }
            for (int j = 0; j < tdList.size(); j++) {
                List<Object> list = new ArrayList<>();
                int tdkai = (int) tdList.get(j).get(0);
                int tdshi = (int) tdList.get(j).get(1);
                if (tdkai>=kai && tdshi<=shi){
                    list.add(tdList.get(j).get(0));list.add(tdList.get(j).get(1));list.add(qingxingList.get(i).get(2));list.add(qingxingList.get(i).get(3));list.add(qingxingList.get(i).get(4));
                    qxList.add(list);
                    kdlist.add(list);

                }
            }
            ksydqjList.add(kdlist);
        }

        //遍历快速眼动,去掉第一段和最后一段为清醒
        //int ksydsize = kydList.size();
        for (int i = 0; i < kydList.size(); i++) {
            int size = (int) kydList.get(i).get(3);
            if (size==0 || size ==listTimesm.size()-1 ){
                kydList.remove(i);
                i--;
            }
        }

        //清醒整理
        for (int i = 0; i < ksydqjList.size(); i++) {
            List<List<Object>> lists = ksydqjList.get(i);
            if (lists.size()==1){
                ksydList.add(lists.get(0));
                continue;
            }
            List<Object> wanzheng = lists.get(0);
            int wzzuo = (int) wanzheng.get(0);
            int wzyou = (int) wanzheng.get(1);
            lists.remove(0);
            lists = lists.stream().sorted((o1, o2) -> {
                for (int j = 0; j < Math.min(o1.size(), o2.size()); j++) {
                    int c = Integer.valueOf((Integer) o1.get(0)).compareTo(Integer.valueOf((Integer) o2.get(0)));
                    if (c != 0) {
                        return c;
                    }
                }
                return Integer.compare(o1.size(), o2.size());
            }).collect(Collectors.toList());
            //排序完进行遍历
            for (int j = 0; j < lists.size(); j++) {
                //先判断左值是否相等
                List<Object> zhiList = lists.get(j);
                int zuo = (int) zhiList.get(0);
                int you = (int) zhiList.get(1);
                int zuoj = zuo;
                int yuoj = you;
                if (wzzuo==zuo && wzyou==you){
                    break;
                }
                if (lists.size()-1>j){
                    zuoj = (int)lists.get(j+1).get(0);
                }else {
                    zuoj = wzyou;
                }
                if (j>0){
                    yuoj = (int) lists.get(j-1).get(1);
                }else {
                    yuoj = wzzuo;
                }
                if (wzzuo==zuo){
                    wanzheng.set(0,you);
                    wanzheng.set(1,zuoj);
//                } else if (wzyuo==you){
//                    wanzheng.set(0,yuoj);
//                    wanzheng.set(1,zuo);
                }else {//当J==0 取
                    if (j==lists.size()-1){
                        List<Object> wanzhengnew = new ArrayList<>(wanzheng);
                        wanzhengnew.set(0,you);
                        wanzhengnew.set(1,zuoj);
                        ksydList.add(wanzhengnew);
                    }
                    wanzheng.set(0,yuoj);
                    wanzheng.set(1,zuo);
                }
                ksydList.add(wanzheng);
            }
        }
    }

    private static void getQianShui(List<List<Object>> zdssList, List<List<Object>> qsList, List<List<Integer>> tdList) {
        for (int i = 0; i < zdssList.size(); i++) {
            int kai = (int) zdssList.get(i).get(0);
            int shi = (int) zdssList.get(i).get(1);
            int type = 0;
            for (int j = 0; j < tdList.size(); j++) {
                int tdkai = (int) tdList.get(j).get(0);
                int tdshi = (int) tdList.get(j).get(1);
                if (tdkai>=kai && tdshi<=shi){
                    type++;
                }
            }
            if (type>=3){ //改为浅睡
                qsList.add(zdssList.get(i));
                zdssList.remove(i);
            }
        }
    }

    private static void getzhongqianshui(List<List<Integer>> listTimesm, List<List<List<Object>>> shushuiNewList, List<List<Object>> qingxingList, List<List<Object>> ssList, List<List<Object>> zdssList, List<List<Object>> qsList) {
        for (int i = 0; i < shushuiNewList.size(); i++) {
            List<List<Object>> lists = shushuiNewList.get(i);
            for (int j = 0; j < lists.size(); j++) {
                List list = (List) lists.get(j).get(0);
                ssList.add(list);
            }
        }
        int qxmz = 0;//清醒末位置
        for (int i = 0; i < listTimesm.size(); i++) {
            int zuobiao = listTimesm.get(i).get(3);
            int biaoshi = 0;
            for (int j = 0; j < qingxingList.size(); j++) {
                int qxzb = (int) qingxingList.get(j).get(3);
                if (qxzb == zuobiao){
                    listTimesm.get(i).add(2);
                    biaoshi =2;
                    if (zuobiao==listTimesm.size()-1){
                        qxmz =1;
                    }
                    break;
                }
            }
            for (int j = 0; j < ssList.size(); j++) {
                int sszb = (int) ssList.get(j).get(3);
                if (sszb == zuobiao){
                    listTimesm.get(i).add(1);
                    biaoshi =1;
                    break;
                }
            }
            if (biaoshi == 0){
                listTimesm.get(i).add(0);
            }
        }

        //遍历原始 如果值为0,则找最近
        for (int i = 0; i < listTimesm.size(); i++) {
            List<Object> list = new ArrayList<>();
            list.add(listTimesm.get(i).get(0));list.add(listTimesm.get(i).get(1));list.add(listTimesm.get(i).get(2));
            int leixing = listTimesm.get(i).get(4);
            int zhi = listTimesm.get(i).get(2);
            int zuozhi = 0;
            int youzhi = 0;
            if (leixing == 0){
                //先往左边找最近者 倒叙
                for (int j = i-1; j >= 0; j--) {
                    int zleixing = listTimesm.get(j).get(4);
                    if (zleixing == 1 || zleixing==2){
                        zuozhi = listTimesm.get(j).get(2);
                        break;
                    }
                }
                //找到左边值后,再找右边值
                for (int j = i+1; j < listTimesm.size(); j++) {
                    int yleixing = listTimesm.get(j).get(4);
                    if (yleixing == 1 || yleixing==2){
                        youzhi = listTimesm.get(j).get(2);
                        break;
                    }
                }
                if (qxmz == 0 && i==listTimesm.size()-1){
                    listTimesm.get(listTimesm.size()-1).set(4,3);
                    list.add(listTimesm.get(i).get(4));
                    qsList.add(list);
                    continue;
                }
                //得到左右值后,进行对比
                int youcha = abs(youzhi - zhi);
                int zuocha = abs(zuozhi - zhi);
                int daxiao = zuozhi - youzhi;
                if (daxiao>0){ //此时说明左边为波峰 右边为波谷
                    if (youcha<zuocha){
                        listTimesm.get(i).set(4,4);
                        list.add(listTimesm.get(i).get(4));
                        zdssList.add(list);
                    }else {
                        listTimesm.get(i).set(4,3);
                        list.add(listTimesm.get(i).get(4));
                        qsList.add(list);
                    }
                }else {//此时说明右边为波峰 左边为波谷
                    if (youcha<zuocha){
                        listTimesm.get(i).set(4,3);
                        list.add(listTimesm.get(i).get(4));
                        qsList.add(list);
                    }else {
                        listTimesm.get(i).set(4,4);
                        list.add(listTimesm.get(i).get(4));
                        zdssList.add(list);
                    }
                }
            }
        }

    }

    private static List<List<Object>> getqxss60(List<List<Integer>> listTimesm, List<List<Object>> qingxing, List<List<List<Object>>> shushuiNewList, List<List<Object>> qingxingList,int qxsssj) {
        int size = qingxingList.size();
        for (int i = 0; i < size; i++) {
            //1先找到qingxinglist原坐标 2遍历熟睡list 分左右 if<坐标为左 判断 用清醒前坐标-熟睡后坐标>3600 找回一次清醒
            int qingxingqzb = (int) qingxingList.get(i).get(0);
            int qingxinghzb = (int) qingxingList.get(i).get(1);
            int qingxingzuobiao = (int) qingxingList.get(i).get(3);
            int zuida = -1;
            int zuixiao = 100;
            for (int j = 0; j < shushuiNewList.size(); j++) {
                List shushuiqList = (List) shushuiNewList.get(j).get(0).get(0);
                List shushuihList = (List) shushuiNewList.get(j).get(shushuiNewList.get(j).size()-1).get(0);
                int shushuiqyzb = (int) shushuiqList.get(3); //最左边原坐标
                int shushuihyzb = (int) shushuihList.get(3); //最右边原左标

                //左边找坐标最大
                if (shushuihyzb<qingxingzuobiao && shushuihyzb!=0){
                    if (zuida<shushuihyzb){
                        zuida = shushuihyzb;
                    }
                }
                //右边找坐标最小
                if(shushuiqyzb>qingxingzuobiao && shushuihyzb!=listTimesm.size()-1) {
                    if (zuixiao>shushuihyzb){
                        zuixiao = shushuihyzb;
                    }
                }
            }
            //找到左右数组后,先判断左边是否大于60
            if (zuida>-1){
                int zuohou = listTimesm.get(zuida).get(1);
                int zuocha = Math.abs(zuohou - qingxingqzb);
                int zuozdz = 0; //左最大值
                int zuozb = 0; //左坐标
                if (zuocha>qxsssj){
                    //先找到大于zuohou小于qingxingqzb区间的清醒原数值
                    for (int j = 0; j < qingxing.size(); j++) {
                        int one = (int) qingxing.get(j).get(0);
                        int two = (int) qingxing.get(j).get(1);
                        int zhi = (int) qingxing.get(j).get(2);
                        int zuobiao = (int) qingxing.get(j).get(3);
                        if (one>zuohou && two<qingxingqzb){
                            zuozb = j;
                            break;
                        }
                    }
                    qingxingList.add(qingxing.get(zuozb));
                }
            }

            //右边
            if (zuixiao<100){
                int yuohou = listTimesm.get(zuixiao).get(0);
                int yuocha = Math.abs(yuohou - qingxinghzb);
                int yuozxz = 0; //左最小值
                int yuozb = 0; //左坐标
                if (yuocha>qxsssj){
                    //先找到大于zuohou小于qingxingqzb区间的清醒原数值
                    for (int j = 0; j < qingxing.size(); j++) {
                        int one = (int) qingxing.get(j).get(0);
                        int two = (int) qingxing.get(j).get(1);
                        int zhi = (int) qingxing.get(j).get(2);
                        int zuobiao = (int) qingxing.get(j).get(3);
                        if (one>qingxinghzb && two<yuohou){
                            yuozb = j;
                            break;
                        }
                    }
                    qingxingList.add(qingxing.get(yuozb));
                }
            }
        }
        qingxingList = qingxingList.stream().distinct().collect(Collectors.toList());
        return qingxingList;
    }

    private static void getssssNew(List<List<List<Object>>> shushuiNewList,int sssjs,List<List<Object>> shushuiNew) {
        //先获取深睡前5值,用前5值
    }
//    private static void getssss(List<List<List<Object>>> shushuiNewList,int sssjs,List<List<List<Object>>> shushui) {
////        for (int i = 0; i < shushuiNewList.size(); i++) {
////            List list = (List) shushuiNewList.get(i).get(0).get(0);
////            int ssNewzhi = (int) list.get(0);
////            for (int j = 0; j < shushui.size(); j++) {
////                List listss = (List) shushui.get(j).get(0).get(0);
////                int sszhi = (int) listss.get(0);
////                if (sszhi==ssNewzhi){
////                    shushui.remove(j);
////                    break;
////                }
////            }
////        }
//        for (int i = 0; i < shushui.size(); i++) {
//            //1先获取前后左标,并-300或+300(5分钟) 2遍历深睡
//            //1先找到qingxinglist原坐标 2遍历熟睡list 分左右 if<坐标为左 判断 用清醒前坐标-熟睡后坐标<300 删除熟睡
//            List shushuiList = (List) shushui.get(i).get(0).get(0);
//            List shushuisList = (List) shushui.get(i).get(shushui.get(i).size()-1).get(0);
//
//            int shusqzb = (int) shushuiList.get(0);
//            int shushzb = (int) shushuiList.get(1);
//            int shuszzhi = (int) shushuiList.get(2);
//            int shuszuobiao = (int) shushuiList.get(3);
//
//            int shusqzbs = (int) shushuisList.get(0);//右
//            int shushzbs = (int) shushuisList.get(1);
//            int shuszzhis = (int) shushuisList.get(2);
//            int shuszuobiaos = (int) shushuisList.get(3);
//            for (int j = 0; j < shushui.size(); j++) {
//                List shushuiqList = (List) shushui.get(j).get(0).get(0);
//                List shushuihList = (List) shushui.get(j).get(shushui.get(j).size()-1).get(0);
//                int shushuiqzb = (int) shushuiqList.get(0); //最左边前坐标
//                int shushuiqzbz = (int) shushuiqList.get(2); //最左边前zhi
//                int shushuiqyzb = (int) shushuiqList.get(3); //最左边原坐标
//                int shushuihzb = (int) shushuihList.get(1); //最右边后左标
//                int shushuihzbz = (int) shushuihList.get(1); //最右边后值
//                int shushuihyzb = (int) shushuihList.get(3); //最右边原左标
//                int qiancha = abs(shusqzb - shushuihzb);//查左边是否小于300 清醒左边左边-熟睡右边坐标
//                int houcha = Math.abs(shushuiqzb - shushzbs); //查右边是否小于300 熟睡左边左边-清醒右边坐标
//
//                if (shushuihyzb<shuszuobiao){
//                    if (qiancha<sssjs){
//                        if (shushuihzbz>shuszzhi){
//                            shushui.get(j).remove(shushui.get(j).size()-1);
//                        }else {
//                            shushui.get(j).remove(0);
//                        }
//
//                    }
//                }
//                if(shushuiqyzb>shuszuobiaos) {
//                    if (houcha < sssjs) {
//                        if (shushuiqzbz>shuszzhis){
//                            shushui.get(j).remove(0);
//                        }else {
//                            shushui.get(j).remove(shushui.get(j).size()-1);
//                        }
//
//                    }
//                }
//                if (shushui.get(j).size()==0){
//                    shushui.remove(j);
//                }
//            }
//        }

        //shushui.add(shushuiNewList);
//    }
    private static void getssss(List<List<List<Object>>> shushuiNewList,int sssjs,List<List<List<Object>>> shushui) {
        for (int i = 0; i < shushuiNewList.size(); i++) {
            //1先获取前后左标,并-300或+300(5分钟) 2遍历深睡
            //1先找到qingxinglist原坐标 2遍历熟睡list 分左右 if<坐标为左 判断 用清醒前坐标-熟睡后坐标<300 删除熟睡
            List shushuiList = (List) shushuiNewList.get(i).get(0).get(0);
            List shushuisList = (List) shushuiNewList.get(i).get(shushuiNewList.get(i).size()-1).get(0);

            int shusqzb = (int) shushuiList.get(0);
            int shushzb = (int) shushuiList.get(1);
            int shuszzhi = (int) shushuiList.get(2);
            int shuszuobiao = (int) shushuiList.get(3);

            int shusqzbs = (int) shushuisList.get(0);//右
            int shushzbs = (int) shushuisList.get(1);
            int shuszzhis = (int) shushuisList.get(2);
            int shuszuobiaos = (int) shushuisList.get(3);

            for (int j = 0; j < shushui.size(); j++) {
                List shushuiqList = (List) shushui.get(j).get(0).get(0);
                List shushuihList = (List) shushui.get(j).get(shushui.get(j).size()-1).get(0);
                int shushuiqzb = (int) shushuiqList.get(0); //最左边前坐标
                int shushuihzbs = (int) shushuiqList.get(1); //最左边后坐标
                int shushuiqzbz = (int) shushuiqList.get(2); //最左边前zhi
                int shushuiqyzb = (int) shushuiqList.get(3); //最左边原坐标
                int shushuiqzbs = (int) shushuihList.get(0); //最右边前左标
                int shushuihzb = (int) shushuihList.get(1); //最右边后左标
                int shushuihzbz = (int) shushuihList.get(2); //最右边后值
                int shushuihyzb = (int) shushuihList.get(3); //最右边原左标
                int qiancha = abs(shusqzb - shushuihzb);//查左边是否小于300 清醒左边左边-熟睡右边坐标
                int houcha = Math.abs(shushuiqzb - shushzbs); //查右边是否小于300 熟睡左边左边-清醒右边坐标

                if (shushuihyzb<shuszuobiao){
                    if (qiancha<sssjs){//时间是否小于5分钟
                        //如果现值大,则删除,如果小则不删,等下一轮
                        if(shuszzhis<shushuiqzbz){ //原最右跟现最左值对比 如果现大则删除
                            shushui.get(j).remove(shushui.get(j).size()-1);
                        }
//                        if (shushuihzbz>shuszzhi){
//                            shushui.get(j).remove(shushui.get(j).size()-1);
//                        }else {
//                            shushui.get(j).remove(0);
//                        }

                    }
                }
                if(shushuiqyzb>shuszuobiaos) {
                    if (houcha < sssjs) {//时间是否小于5分钟
                        if(shuszzhi<shushuihzbz){ //原最左跟现最右值对比 如果现大则删除
                            shushui.get(j).remove(0);
                        }
//                        if (shushuiqzbz>shuszzhis){
//                            shushui.get(j).remove(0);
//                        }else {
//                            shushui.get(j).remove(shushui.get(j).size()-1);
//                        }
                    }
                }
                if (shushui.get(j).size()==0){
                    shushui.remove(j);
                }
            }
        }

        //shushui.add(shushuiNewList);
    }
    private static void getqxssNew(List<List<Object>> shushui, List<List<Object>> qingxingList,int qxsj) {
        //先遍历清醒,再遍历深睡,看,看坐标在左边还是右边 ,左边的话就清醒前值-深睡后值  右边就深睡前值-清醒后值 如果小于300则删除改熟睡
        for (int i = 0; i < qingxingList.size(); i++) {
            int qingxingqzb = (int) qingxingList.get(i).get(0);//清醒前坐标
            int qingxinghzb = (int) qingxingList.get(i).get(1);//清醒后坐标
            int qingxingzuobiao = (int) qingxingList.get(i).get(3);//原始坐标
            for (int j = 0; j < shushui.size(); j++) {
                int shushuiqzb = (int) shushui.get(j).get(0);
                int shushuihzb = (int) shushui.get(j).get(1);
                int shushuizuobiao = (int) shushui.get(j).get(3);
                if (shushuizuobiao<qingxingzuobiao){//熟睡在左,清醒在右
                    int chazhi = qingxingqzb - shushuihzb;
                    if (chazhi<qxsj){
                        shushui.remove(j);
                    }
                }
                if (shushuizuobiao>qingxingzuobiao){//清醒在左,熟睡在右
                    int chazhi = shushuiqzb - qingxinghzb;
                    if (chazhi<qxsj){
                        shushui.remove(j);
                    }
                }
            }
        }
    }
    private static void getqxss(List<List<List<Object>>> shushuiNewList, List<List<Object>> qingxingList,int qxsj) {
        for (int i = 0; i < qingxingList.size(); i++) {
            //1先获取前后左标,并-300或+300(5分钟) 2遍历深睡
            //1先找到qingxinglist原坐标 2遍历熟睡list 分左右 if<坐标为左 判断 用清醒前坐标-熟睡后坐标<300 删除熟睡
            int qingxingqzb = (int) qingxingList.get(i).get(0);
            int qingxinghzb = (int) qingxingList.get(i).get(1);
            int qingxingzuobiao = (int) qingxingList.get(i).get(3);
            for (int j = 0; j < shushuiNewList.size(); j++) {
                List shushuiqList = (List) shushuiNewList.get(j).get(0).get(0);
                List shushuihList = (List) shushuiNewList.get(j).get(shushuiNewList.get(j).size()-1).get(0);
                int shushuiqzb = (int) shushuiqList.get(0); //最左边前坐标
                int shushuiqyzb = (int) shushuiqList.get(3); //最左边原坐标
                int shushuihzb = (int) shushuihList.get(1); //最右边后左标
                int shushuihyzb = (int) shushuihList.get(3); //最右边原左标
                int qiancha = abs(qingxingqzb - shushuihzb);//查左边是否小于300 清醒左边左边-熟睡右边坐标
                int houcha = Math.abs(shushuiqzb - qingxinghzb); //查右边是否小于300 熟睡左边左边-清醒右边坐标

                if (shushuihyzb<qingxingzuobiao){
                    if (qiancha<qxsj){
                        shushuiNewList.get(j).remove(shushuiNewList.get(j).size()-1);
                    }
                }
                if(shushuiqyzb>qingxingzuobiao) {
                    if (houcha < qxsj) {
                        shushuiNewList.get(j).remove(0);
                    }
                }
                if (shushuiNewList.get(j).size()==0){
                    shushuiNewList.remove(j);
                }
            }
        }
    }

    private static List<List<Object>> getQingXing(List<List<Integer>> listTimesm, List<List<Object>> qingxing, List<List<List<Object>>> shushuiNewList, List<List<Object>> qingxingList, int qxcs ) {
        for (int i = 0; i < shushuiNewList.size(); i++) {
            List zuizuoList = (List) shushuiNewList.get(i).get(0).get(0);
            List zuiyouList = (List) shushuiNewList.get(i).get(shushuiNewList.get(i).size()-1).get(0);
            //左
            int zuizuobiao = (int) zuizuoList.get(0);
            int zuiyuobiao = (int) zuiyouList.get(3);//得到最右边的原始数组坐标值
            //右
            int zuiyuohbiao = (int) zuiyouList.get(1);//得到最右边的后左标
            int zuizuoybiao = (int) zuizuoList.get(3);//最左原始左标

            //往左找 倒叙查询清醒数组,找到第一个
            for (int j = qingxing.size()-1; j >= 0; j--) {
                int qingxinghouzhi = (int) qingxing.get(j).get(1);
                int qingxingzhi = (int) qingxing.get(j).get(2);
                int qingxinghouzbzhi = (int) qingxing.get(j).get(3);
                //先判断右边
                int ytype = 0;
                if (qingxinghouzhi <= zuizuobiao && zuiyuobiao-qingxinghouzbzhi>=qxcs){
                    //将第一个清醒值取出左右对比 先对比右边 先判断右边是否>3
                    for (int k = qingxinghouzbzhi+1; k <= qingxinghouzbzhi+qxcs; k++) {//与右边最近三个比大小
                        int integer = listTimesm.get(k).get(2);
                        if (qingxingzhi>integer){
                            ytype++;
                        }
                    }
                    if (ytype<qxcs){
                        continue;
                    }
                }
                //再判断左边
                int ztype = 0;
                if (qingxinghouzbzhi>=qxcs && ytype>=qxcs){
                    for (int k = qingxinghouzbzhi-1; k >= qingxinghouzbzhi-qxcs; k--) {//与左边最近三个比大小
                        int integer = listTimesm.get(k).get(2);
                        if (qingxingzhi>integer){
                            ztype++;
                        }
                    }
                    if (ztype<qxcs){
                        continue;
                    }
                }
                if (ztype>=qxcs && ytype>=qxcs){
                    qingxingList.add(qingxing.get(j));
                    break;
                }
            }

            //往右边找
            for (int j = 0; j < qingxing.size(); j++) {
                int qingxingqianzhi = (int) qingxing.get(j).get(0);
                int qingxinghouzhi = (int) qingxing.get(j).get(1);
                int qingxingzhi = (int) qingxing.get(j).get(2);
                int qingxinghouzbzhi = (int) qingxing.get(j).get(3);

                //先判断左边
                int ztype = 0;
                if (qingxingqianzhi>zuiyuohbiao && qingxinghouzbzhi-zuizuoybiao>=qxcs){
                    for (int k = qingxinghouzbzhi-1; k >= qingxinghouzbzhi-qxcs; k--) {//与左边最近三个比大小
                        int integer = listTimesm.get(k).get(2);
                        if (qingxingzhi>integer){
                            ztype++;
                        }
                    }
                    if (ztype<qxcs){
                        continue;
                    }
                }

                //再判断右边
                int ytype = 0;
                if ((listTimesm.size()-1)-qingxinghouzbzhi>=qxcs && ztype>=qxcs){
                    //将第一个清醒值取出左右对比 先对比右边 先判断右边是否>3
                    for (int k = qingxinghouzbzhi+1; k <= qingxinghouzbzhi+qxcs; k++) {//与右边最近三个比大小
                        int integer = listTimesm.get(k).get(2);
                        if (qingxingzhi>integer){
                            ytype++;
                        }
                    }
                    if (ytype<qxcs){
                        continue;
                    }
                }
                if (ztype>=qxcs && ytype>=qxcs){
                    qingxingList.add(qingxing.get(j));
                    break;
                }
            }
        }
        if (qingxing.size()>0){
            List<Integer> integers = listTimesm.get(0);
            List<Object> qxLs = new ArrayList<>();
            qxLs.add(integers.get(0));qxLs.add(integers.get(1));qxLs.add(integers.get(2));qxLs.add(integers.get(3));
            qingxingList.add(qxLs);
//            qingxingList.add(qingxing.get(0));
            int qxzuobiao = (int) qingxing.get(qingxing.size() - 1).get(3);
            int zuobiao = listTimesm.size()-1;
            if (qxzuobiao==zuobiao){
                qingxingList.add(qingxing.get(qingxing.size()-1));
            }
            qingxingList = qingxingList.stream().distinct().collect(Collectors.toList());//去重
        }
        return qingxingList;
    }

    private static void getchushishushui(List<List<Integer>> listTimesm, List<List<Object>> shushui, List<List<List<Object>>> shushuiList,List<List<Object>> qingxing,int sssj,int sszhi ) {
        for (int i = 0; i < shushui.size(); i++) {
            List<List<Object>> list = new ArrayList<>();
            int zuobiao = (int) shushui.get(i).get(3);
            List<Integer> integers = listTimesm.get(zuobiao);

            if (zuobiao==1){
                List<Integer> integers2 = listTimesm.get(zuobiao+1);
                int yuocha = (int) listTimesm.get(zuobiao+1).get(1) - (int) listTimesm.get(zuobiao+1).get(0);
                int yuozhi = Math.abs((int) listTimesm.get(zuobiao+1).get(2) - (int) listTimesm.get(zuobiao).get(2));
                list.add(Collections.singletonList(integers));
                if (yuocha>sssj && yuozhi<sszhi){
                    list.add(Collections.singletonList(integers2));//+
                }
            }else if (zuobiao == listTimesm.size()-2){
                List<Integer> integers1 = listTimesm.get(zuobiao-1);
                int zuocha = (int) listTimesm.get(zuobiao-1).get(1) - (int) listTimesm.get(zuobiao-1).get(0);
                int zuozhi = Math.abs((int) listTimesm.get(i-1).get(2) - (int) listTimesm.get(zuobiao).get(2));
                if (zuocha>sssj && zuozhi<sszhi) {
                    list.add(Collections.singletonList(integers1));//-
                }
                list.add(Collections.singletonList(integers));
            }else {
                if (zuobiao==listTimesm.size()-1){
                    break;
                }
                List<Integer> integers1 = listTimesm.get(zuobiao-1);List<Integer> integers2 = listTimesm.get(zuobiao+1);
                int zuocha = (int) listTimesm.get(zuobiao-1).get(1) - (int) listTimesm.get(zuobiao-1).get(0);
                int zuozhi = Math.abs((int) listTimesm.get(zuobiao-1).get(2) - (int) listTimesm.get(zuobiao).get(2));
                int yuocha = (int) listTimesm.get(zuobiao+1).get(1) - (int) listTimesm.get(zuobiao+1).get(0);
                int yuozhi = Math.abs((int) listTimesm.get(zuobiao+1).get(2) - (int) listTimesm.get(zuobiao).get(2));
                if (zuocha>sssj && zuozhi<sszhi) {
                    list.add(Collections.singletonList(integers1));
                }
                list.add(Collections.singletonList(integers));
                if (yuocha>sssj && yuozhi<sszhi){
                    list.add(Collections.singletonList(integers2));
                }
            }
            shushuiList.add(list);
        }
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < shushuiList.size(); i++) {
            List<List<Object>> lists = shushuiList.get(i);
            for (int j = 0; j < lists.size(); j++) {
                List ls = (List) lists.get(j).get(0);
                int zuobiao = (int) ls.get(3);
                list.add(zuobiao);
                for (int k = 0; k < qingxing.size(); k++) {//深睡不允许有清醒
                    int qx = (int) qingxing.get(k).get(3);
                    if (qx==zuobiao){
                        shushuiList.get(i).remove(j);
                    }
                }
            }
        }
        List<Integer> listzb = new ArrayList<>();
        same( list,listzb);//得到重复数据坐标
        for (Integer integer : listzb) {
            for (int i = 0; i < shushuiList.size(); i++) {
                int type = 0;
                List<List<Object>> lists = shushuiList.get(i);
                for (int j = 0; j < lists.size(); j++) {
                    List ls = (List) lists.get(j).get(0);
                    int zuobiao = (int) ls.get(3);
                    if (zuobiao==integer){
                        shushuiList.get(i).remove(j);
                        type = 1;
                    }
                }
                if (type==1){
                    break;
                }

            }
        }
    }
    public static void same(List<Integer> list,List<Integer> listzb) {

        Map<Integer,Integer> map = new HashMap<>();
        // List<Integer> zb = new ArrayList<>();
        for(Integer str:list){
            Integer i = 1; //定义一个计数器，用来记录重复数据的个数
            if(map.get(str) != null){
                i=map.get(str)+1;
            }
            map.put(str,i);
        }
//        System.out.println("重复数据的个数："+map.toString());
        for(Integer s:map.keySet()){
            if(map.get(s) > 1){
                listzb.add(s);
            }
        }
    }

    private static JSONObject getfczws(List<List<Integer>> shiwuxlfc, int[] shiwufcarr) {
        int a = 0;
        int zuida = 0;
        List<Integer> list = new ArrayList<>();
        for (List<Integer> integers : shiwuxlfc) {
            int[] arrays = new int[integers.size()];
            for (int i = 0; i < integers.size(); i++) {
                arrays[i] = integers.get(i);
            }

            int i = Variance(arrays);//取出方差值
            if (i>zuida) {
                zuida=i;
            }
            shiwufcarr[a] = i;
            list.add(i);

            a = a + 1;
        }
        int swxlzs = (int) median(list);
        JSONObject json = new JSONObject();
        json.put("zs",swxlzs);
        json.put("zuida",zuida);
        return json;
    }

    private static List<Integer> getfc(List<List<Integer>> shiwuxlfc, int[] shiwufcarr) {
        int a = 0;
        List<Integer> list = new ArrayList<>();
        for (List<Integer> integers : shiwuxlfc) {
            int[] arrays = new int[integers.size()];
            for (int i = 0; i < integers.size(); i++) {
                arrays[i] = integers.get(i);
            }

            int i = Variance(arrays);//取出方差值
            shiwufcarr[a] = i;
            list.add(i);
            a = a + 1;
        }
        return list;
    }

    private static void getPaixu(List<List<List<Object>>> shushuiList, List<List<List<Object>>> shushuiNewList,int sscishu) {
        List<List<Integer>> dblists = new ArrayList<>();
        for (int i = 0; i < shushuiList.size(); i++) {
            List<Integer> dblist = new ArrayList<>();
            List kaiList = (List) shushuiList.get(i).get(0).get(0);
            List shiList = (List) shushuiList.get(i).get(shushuiList.get(i).size() - 1).get(0);
            int kai = (int) kaiList.get(0);
            int shi = (int) shiList.get(1);
            int cha = shi - kai;
            dblist.add(i);
            dblist.add(cha);
            dblists.add(dblist);
        }

        dblists = dblists.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Integer.valueOf((Integer) o1.get(1)).compareTo(Integer.valueOf((Integer) o2.get(1)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());

        for (int i = dblists.size() - 1; i >= 0; i--) {
            int zuobiao = dblists.get(i).get(0);
            List<List<Object>> lists = shushuiList.get(zuobiao);
            List<List<Object>> listcp = new ArrayList<>(lists);
                    shushuiNewList.add(listcp);
            if (i == dblists.size() - sscishu) {
                break;
            }
        }
    }


    private static void getfuzhi(List<EDFRecord> records, List<Integer> prList, List<Integer> RR, List<Integer> PI) {
        for (EDFRecord record : records) {
            short[] hr = record.HR;
            for (int i : hr) {
                if (i <= 250) {
                    prList.add(i);

                } else {
                    prList.add(0);
                }
            }
            short[] rr = record.PI;
            for (int i : rr) {
                if (i < 100) {
                    RR.add(i);

                } else {
                    RR.add(0);
                }
            }
            short[] pi = record.AccelarX;
            for (int i = 0; i < 2; i++) {
                int zhi = pi[i];
                PI.add(zhi);
            }

        }
        prList.set(0, 100);
    }

    private static String getTime(HashMap<String, String> header, List<EDFRecord> records) throws ParseException {
        String startTime = header.get("记录的开始时间*").replace(".", ":");//时分秒 14.50.52
        String startDate = header.get("记录的开始日期*").replace(".", ":");//日期 11.01.20
        String[] strArr = startDate.split("\\:");
        String time = "20" + strArr[2] + "-" + strArr[1] + "-" + strArr[0];
        int miao = records.size() * 2; //睡眠总秒数
        int minute = miao / 60 % 60; //睡眠时间转换成分钟
        String createTime = time + " " + startTime;//开始时间 startDate=2020-01-11 15:09:52
        String endTime = getEndTime(createTime, minute);//结束时间
        return createTime;
    }

    private static void getHebin(List<Integer> prListCopy, int n, List<List<Integer>> array, List<Integer> averageList, int[] pingjunshu, double pingjun, List<List<Integer>> listTime) {
        List<List<Integer>> resultList = new ArrayList<>();
        List<Integer> rsList = new ArrayList<>();
        //获取众数后再次对比<n求得最后区间数组
        //众数(pingjun)对比参数小于n
        int chazhi;
        int kaishipj = 0;
        int jieshupj = 0;
        int css = 0;
        for (int i = 1; i < pingjunshu.length; i++) {
            List<Integer> list = new ArrayList<>();
            chazhi = Math.abs(pingjunshu[i] - pingjunshu[i - 1]);
            if (chazhi <= n) {
                // kaishi = i-1;
                if (css == 0) {
                    kaishipj = i - 1;
                    css = 1;
                } else {
                    css++;
                }
            } else { //判断前值是否小于0 若小于0
                if (i > 1) {
                    if (Math.abs(pingjunshu[i - 1] - pingjunshu[i - 2]) <= n) {
                        //设置尾值
                        jieshupj = css + kaishipj;
                        if (i == pingjunshu.length - 1) {
                            jieshupj = pingjunshu.length - 1;
                        }
                        list.add(kaishipj);
                        list.add(jieshupj);
                        resultList.add(list);
                        css = 0;
                        continue;
                    } else {
                        list.add(i - 1);
                        list.add(i - 1);
                        resultList.add(list);
                        css = 0;
                        continue;
                    }
                } else {
                    list.add(i - 1);
                    list.add(i - 1);
                    css = 0;
                    resultList.add(list);
                }
            }
        }

        //取得区间(众数 需在找到真时间)
        //System.out.println(resultList);


        for (int i = 0; i < resultList.size(); i++) {
            List<Integer> ss = new ArrayList<>();
            List<Integer> integers = resultList.get(i);
            Integer one = integers.get(0);
            Integer two = integers.get(1);
            List<Integer> arrsl = array.get(one);
            Integer integer = arrsl.get(0);
            List<Integer> arrsw = array.get(two);
            Integer integer2 = arrsw.get(1);
            int zhi = pingjunshu[one];
            ss.add(integer);
            ss.add(integer2);
            ss.add(zhi);
            listTime.add(ss);
        }

        int integer = listTime.get(listTime.size() - 1).get(1);
        List<Integer> arrylist = new ArrayList<>();
        for (int i = integer; i < prListCopy.size(); i++) {
            arrylist.add(prListCopy.get(i));
        }
        int[] arrylistsz = arrylist.stream().mapToInt(Integer::valueOf).toArray();
        ModeUtil m = new ModeUtil();

        m.number = new int[arrylistsz.length];
        m.Mode(arrylistsz, 0, arrylistsz.length);
        int y = m.number[0];

        //for (int i = 0; i < listTime.size(); i++) {
        List<Integer> list = new ArrayList<>();
        if (y==0){
            listTime.get(listTime.size() - 1).set(1, prListCopy.size() - 1);
        }else {
            list.add(listTime.get(listTime.size() - 1).get(1));
            list.add(prListCopy.size()-1);
            list.add(y);
            listTime.add(list);
        }

        //}
    }

    private static double getpingjun(List<Integer> prList, List<Integer> prListCopy, List<Integer> prListCopys) {
        List<Integer> Prlistmiao = new ArrayList<>(prList);
        int zuixiao = 200;
        int zuida = 0;
        int fenzhong = 180;
        // List<Integer> Prlist35 = new ArrayList<>(PrList);
        for (int i = 121; i <= fenzhong + 1; i++) {
            Integer integer = prListCopy.get(i);
            Prlistmiao.add(integer);
            if (integer < zuixiao) {
                zuixiao = integer;
            }
            if (integer > zuida) {
                zuida = integer;
            }
        }
        int[] Prlistmiao240 = Prlistmiao.stream().mapToInt(Integer::valueOf).toArray();
        int[] ints = prListCopy.stream().mapToInt(Integer::valueOf).toArray();
        //众数
        ModeUtil m = new ModeUtil();
        int c;
        m.number = new int[ints.length];
        m.Mode(ints, 0, ints.length);
        int zhongshu = m.number[0];
        ModeUtil m1 = new ModeUtil();
        m1.number = new int[Prlistmiao240.length];
        m1.Mode(Prlistmiao240, 0, Prlistmiao240.length);
        int zhongshu4 = m1.number[0];

        //不算为0者
        for (int i = prListCopys.size() - 1; i >= 0; i--) {
            Integer integer = prListCopys.get(i);
            if (integer == 0) {
                prListCopys.remove(i);
            }
        }
        int[] notzero = prListCopys.stream().mapToInt(Integer::valueOf).toArray();
        int avenotzero = average(notzero, notzero.length);
        int avenot35 = average(Prlistmiao240, Prlistmiao240.length); //3-5分钟
        int average = average(ints, ints.length);

        return zhongshu4 - 3;
    }

    private static void getpapohouhebing(Date date, List<Integer> prListCopy, List<List<Integer>> array, List<Integer> averageList) {
        List<List<Long>> arrays = new ArrayList();
        List<List<Integer>> arrays1 = new ArrayList();
        List<List<Long>> arraystime = new ArrayList();
        for (int j = 0; j < array.size(); j++) {
            List<Long> list = new ArrayList<>();
            List<Integer> list2 = new ArrayList<>();
            List<Long> listtime = new ArrayList<>();
            long ts = date.getTime();
            for (int i = 0; i < prListCopy.size(); i++) {
                Integer kai = array.get(j).get(0);
                Integer shi = array.get(j).get(1);
                if (i >= kai && i < shi) {
                    list.add(Long.valueOf(prListCopy.get(i)));
                    list2.add(prListCopy.get(i));
                    listtime.add(Long.valueOf(ts + i * 1000));
                }
            }
            arrays.add(list);
            arrays1.add(list2);
            arraystime.add(listtime);
        }


        for (int i = 0; i < arrays.size(); i++) {
            List<Integer> arrylist = arrays1.get(i);

            int[] arrylistsz = arrylist.stream().mapToInt(Integer::valueOf).toArray();
            ModeUtil m = new ModeUtil();

            m.number = new int[arrylistsz.length];
            m.Mode(arrylistsz, 0, arrylistsz.length);
            int y = m.number[0];
            averageList.add((int) y);
        }
    }

    private static void getpapo(int r, int s, List<List<Integer>> array,int h) {

        List<List<Integer>> arraySort = new ArrayList(array); //排序数组

        arraySort = arraySort.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Integer.valueOf(o1.get(2)).compareTo(Integer.valueOf(o2.get(2)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());

        int xiao = 0;
        while (true) { //*需修改
            List<Integer> integers = arraySort.get(xiao);
            int zuixiaozhi = integers.get(2); //最小值
            int zuixiaozhizb = integers.get(3); //最小值坐标
            int chas = integers.get(1) - integers.get(0);

            //先往左边找4 找到后如果有合并则
            int zuotype = 0; //左边次数
            int xiajiangtypez = 0; //下降类型
            List<List<Integer>> zuolist = new ArrayList();
            for (int i = zuixiaozhizb; i > 0; i--) {
                Integer houzhi = array.get(i).get(2); //后值
                Integer qianzhi = array.get(i - 1).get(2); //前值
                //Integer xiajiangzhiz = array.get(i-2).get(2);
                Integer xiajiangzhiz = array.get(i - 1).get(2); //下降值
                if (i > 1) {
                    xiajiangzhiz = array.get(i - 2).get(2); //前值
                }

                if (qianzhi - houzhi >= 0) {//代表上升
                    if (zuotype == 0) {
                        zuolist.add(array.get(i));
                    }
                    zuolist.add(array.get(i - 1));
                    zuotype++;
                } else {
                    break;
                }
            }

            if (zuotype > r) {
                if (chas < h) {
                    //比较前后 谁近跟谁
                    if (zuixiaozhizb == 0) {//说明为0,则跟后面
                        array.get(0).set(1, array.get(1).get(1));
                        array.remove(1);
                        for (int i = 1; i < array.size(); i++) {
                            array.get(i).set(3, array.get(i).get(3) - 1);
                        }
                    } else if (zuixiaozhizb == array.size() - 1) { //
                        array.get(array.size() - 1).set(0, array.get(array.size() - 2).get(0));
                        array.remove(array.size() - 1);
                    } else {
                        //现取左,再取右
                        int tihuan = 0;
                        List<Integer> zuobian = array.get(zuixiaozhizb - 1);
                        List<Integer> youbian = array.get(zuixiaozhizb + 1);
                        Integer zuozhi = Math.abs(zuobian.get(2) - zuixiaozhi);
                        Integer youzhi = Math.abs(youbian.get(2) - zuixiaozhi);
                        if (zuozhi < youzhi) {
                            array.get(zuixiaozhizb - 1).set(1, array.get(zuixiaozhizb).get(1));
                            array.remove(zuixiaozhizb);
                            tihuan = zuixiaozhizb;
                        } else {
                            array.get(zuixiaozhizb).set(1, array.get(zuixiaozhizb + 1).get(1));
                            array.remove(zuixiaozhizb + 1);
                            tihuan = zuixiaozhizb + 1;
                        }
                        for (int i = tihuan; i < array.size(); i++) {
                            array.get(i).set(3, i);
                        }
                    }
                    arraySort = getSort(array);
                    if (xiao >= array.size() - 1) {
                        break;
                    }
                    continue;
                }
            }

            int biaozhun = 100;
            int hbzuobiaoz = 0; //合并左边坐标
            int hbzuobiaoy = 0; //右边坐
            int zuolisttype = 0;
            for (int l = 0; l <= zuotype - r; l++) {
                if (zuotype > r) { //如果次数>4 合并完一次 找一次然后再重新找 直到<4
                    for (int i = 0; i < zuolist.size() - 1; i++) {//zuolist 坐标为 最小值 最小值坐标-1 用-1 -最小
                        int cha = Math.abs(zuolist.get(i + 1).get(2) - zuolist.get(i).get(2)); //相邻两数组想减求差值
                        if (cha < biaozhun) {
                            biaozhun = cha;
                            hbzuobiaoz = zuolist.get(i + 1).get(3);
                            hbzuobiaoy = hbzuobiaoz;
                            zuolisttype = i;
                        }
                    }
                    //最终找到合并左边坐标hbzuobiaoz
                    for (int i = 1; i < array.size() - 1; i++) {
                        List<Integer> zuo = array.get(i);
                        Integer zuobiao = zuo.get(3);
                        if (zuobiao == hbzuobiaoz) { //找到合并坐标左 取get(0) 找到右取get(1) 然后删除右边坐标
                            array.get(i).set(1, array.get(i + 1).get(1));
                            array.remove(i + 1);
                            zuolist.remove(zuolisttype);
                            for (int j = 0; j < array.size(); j++) {
                                if (j >= i + 1) {
                                    array.get(j).set(3, array.get(j).get(3) - 1);
                                }
                            }
//                            array.get(i-1).set(1, array.get(i).get(1));
//                            array.remove(i);
//                            for (int j = 0; j < array.size(); j++) {
//                                if (j >= i ) {
//                                    array.get(j).set(3, array.get(j).get(3) - 1);
//                                }
//                            }
                            break;
                        }
                    }
                    arraySort = getSort(array);
                }
            }


            //往右边找4 找到后如果有合并则
            int youtype = 0; //右边次数
            int xiajiangtypey = 0; //下降类型
            List<List<Integer>> youlist = new ArrayList();
            for (int i = zuixiaozhizb; i < array.size() - 1; i++) {
                Integer houzhi = array.get(i + 1).get(2); //后值
                Integer qianzhi = array.get(i).get(2); //前值
                //Integer xiajiangzhiy = array.get(i+2).get(2); //下降值
                Integer xiajiangzhiy = array.get(i + 1).get(2);
                if (i < array.size() - 2) {
                    xiajiangzhiy = array.get(i + 2).get(2); //下降值
                }

                if (houzhi - qianzhi >= 0) { //用后值-前值 右减左
                    if (youtype == 0) {
                        youlist.add(array.get(i));
                    }
                    youlist.add(array.get(i + 1));

                    youtype++;
                } else {
                    break;
                }
            }


            if (youtype > r) {
                if (chas < h) {
                    //比较前后 谁近跟谁
                    if (zuixiaozhizb == 0) {//说明为0,则跟后面
                        array.get(0).set(1, array.get(1).get(1));
                        array.remove(1);
                        for (int i = 1; i < array.size(); i++) {
                            array.get(i).set(3, array.get(i).get(3) - 1);
                        }
                    } else if (zuixiaozhizb == array.size() - 1) { //
                        array.get(array.size() - 1).set(0, array.get(array.size() - 2).get(0));
                        array.remove(array.size() - 1);
                    } else {
                        //现取左,再取右
                        int tihuan = 0;
                        List<Integer> zuobian = array.get(zuixiaozhizb - 1);
                        List<Integer> youbian = array.get(zuixiaozhizb + 1);
                        Integer zuozhi = Math.abs(zuobian.get(2) - zuixiaozhi);
                        Integer youzhi = Math.abs(youbian.get(2) - zuixiaozhi);
                        if (zuozhi < youzhi) {
                            array.get(zuixiaozhizb - 1).set(1, array.get(zuixiaozhizb).get(1));
                            array.remove(zuixiaozhizb);
                            tihuan = zuixiaozhizb;
                        } else {
                            array.get(zuixiaozhizb).set(1, array.get(zuixiaozhizb + 1).get(1));
                            array.remove(zuixiaozhizb + 1);
                            tihuan = zuixiaozhizb + 1;
                        }
                        for (int i = tihuan; i < array.size(); i++) {
                            array.get(i).set(3, i);
                        }
                    }
                    arraySort = getSort(array);
                    if (xiao >= array.size() - 1) {
                        break;
                    }
                    continue;
                }
            }


            int biaozhuny = 100;
            int hbzuobiaozy = 0; //合并左边坐标
            int hbzuobiaoyy = 0; //右边坐标
            int youlisttype = 0;
            for (int l = 0; l <= youtype - r; l++) {
                if (youtype > r) { //如果次数>4 合并完一次 找一次然后再重新找 直到<4
                    for (int i = 0; i < youlist.size() - 1; i++) {
                        int cha = Math.abs(youlist.get(i + 1).get(2) - youlist.get(i).get(2)); //相邻两数组想减求差值 右减左
                        if (cha < biaozhuny) { //求最小值
                            biaozhuny = cha;
                            hbzuobiaozy = youlist.get(i).get(3);
                            hbzuobiaoyy = hbzuobiaozy + 1;
                            youlisttype = i+1;
                        }
                    }
                    //最终找到合并右边坐标hbzuobiaozy
                    for (int i = 1; i < array.size() - 1; i++) {
                        List<Integer> you = array.get(i);
                        Integer zuobiao = you.get(3);
                        if (zuobiao == hbzuobiaozy) { //找到合并坐标左 取get(0) 找到右取get(1) 然后删除右边坐标
                            array.get(i).set(1, array.get(i + 1).get(1));//将左坐标第一坐标设置为右坐标的1
                            array.remove(i + 1);
                            youlist.remove(youlisttype);
                            for (int j = 0; j < array.size(); j++) {
                                if (j >= i + 1) {
                                    array.get(j).set(3, array.get(j).get(3) - 1);
                                }
                            }
                            break;
                        }
                    }
                    arraySort = getSort(array);
                }
            }
            if (xiao >= array.size() - 1) {
                break;
            }
            xiao++;
        }

        for (int i = 0; i < array.size() - 1; i++) {
            array.get(i).set(1, array.get(i + 1).get(0));
        }

        int ty = 1;
        int shoutime = 0;
        int weitime = 0;
        int tytype = 0;
        int zuobiaozhi = 0;
        int time1 = 0;
        int time2 = 0;

        int tys = 0;
        while (true) {
            int time = array.get(tys).get(1) - array.get(tys).get(0);
            if (time < s) {
                if (tys == 0) {
                    array.get(1).set(0, array.get(0).get(0));
                    array.remove(tys);
                    for (int j = 0; j < array.size(); j++) {
                        array.get(j).set(3, j);
                    }
                } else if (tys == array.size() - 1) {
                    array.get(tys-1).set(1, array.get(tys).get(1));
                    array.get(tys-1).set(2, array.get(tys).get(2));
                    array.remove(tys);
                } else {
                    int qian = array.get(tys-1).get(2);
                    int xian = array.get(tys).get(2);
                    int hou = array.get(tys+1).get(2);
                    int qiancha = Math.abs(qian - xian);
                    int houcha = Math.abs(hou - xian);
                    int duibi = tys;//对比值
//                    if (qiancha<=houcha){//跟前值 删现值
//                        array.get(tys-1).set(1, array.get(tys).get(1));
//                        array.remove(tys);
//                    }else {
                    array.get(tys).set(1, array.get(tys+1).get(1));
                    duibi = tys+1;
                    array.remove(tys+1);
                    // }
                    for (int j = duibi; j < array.size(); j++) {
                        array.get(j).set(3, j);
                    }
                }
                tys --;
            }

            if (tys >= array.size() - 1) {
                break;
            }
            tys++;
        }
    }

    private static void getHxwl(List<Integer> RR, Date date, List<List<Integer>> arrayhx, int h, int q, List<List<Integer>> hxwl, List<List<Object>> resultshxxl,List<Integer> PI) {
        List<List<Integer>> sort = getSort1(arrayhx);
        for (int i = sort.size() - 1; i >= sort.size() - h && i >= 0; i--) {
            //for (int i = 0; i <  sort.size(); i++) {
            List<Integer> integers = sort.get(i);
            Integer hxz = integers.get(2); //呼吸值
            Integer xlz = integers.get(3); //心率值
            if (hxz + xlz > q) {
                hxwl.add(integers);
            }
        }


//        for (int i = 0; i < RR.size(); i++) {
//            List<Object> PrListCopyhxxl = new ArrayList<>();
//            long ts = date.getTime();
//            for (int j = 0; j < hxwl.size(); j++) {
//                List<Integer> integers = hxwl.get(j);
//                Integer qian = integers.get(0); //区间前值
//                Integer hou = integers.get(1); //区间后值
//                if (i >= qian && i <= hou) {
//                    PrListCopyhxxl.add(ts + i * 1000);
//                    if (PI.get(i) == 0) {
//                        PrListCopyhxxl.add(0);
//                    } else {
//                        PrListCopyhxxl.add(integers.get(2));
//                    }
//
//                    break;
//                } else if (j == hxwl.size() - 1) {
//                    PrListCopyhxxl.add(ts + i * 1000);
//                    PrListCopyhxxl.add(0);
//                }
//            }
//            resultshxxl.add(PrListCopyhxxl);
//        }
    }

    private static void shijianzhongshujihe(List<Integer> prList, List<Integer> RR, List<Integer> prListCopy, List<List<Integer>> arrlist3, List<List<Integer>> array, List<List<Integer>> arrayhx, List<Integer> PI) {
        long x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        int arrzb = 0;
        for (List<Integer> integers : arrlist3) {
            List<Integer> fanwei = new ArrayList();
            List<Integer> huxixinlv = new ArrayList();
            int zuo = integers.get(0);  //循环出的list 左边为初坐标 右边为结束坐标
            int you = integers.get(1);
            int zysize = you - zuo;

            int kai = 0;
            List<Integer> arrylisthx = new ArrayList<>(prList); //呼吸组
            List<Integer> arrylist1 = new ArrayList<>(prList);
            for (int h = zuo; h < you; h++) {
                if (h < 0) {
                    h = 0;
                }
                arrylist1.add(prListCopy.get(h));
                kai++;
                //呼吸
                //arrylisthx.add(RR.get(h));
                arrylisthx.add(PI.get(h));
            }

            //第三部通过众数获得数组
            int[] arrylist1sz = arrylist1.stream().mapToInt(Integer::valueOf).toArray();
            ModeUtil m = new ModeUtil();
            int c;
            m.number = new int[arrylist1sz.length];
            m.Mode(arrylist1sz, 0, arrylist1sz.length);
            int y = m.number[0];

            //形成数组 [[1591116380000, 49], [1591123995000, 49]]
            List<List<Long>> ercheng = new ArrayList<>();
            List<Long> objects1 = new ArrayList<>();
            objects1.add(x1);
            objects1.add(y1);
            List<Long> objects2 = new ArrayList<>();
            objects2.add(x2);
            objects2.add(y2);
            ercheng.add(objects1);
            ercheng.add(objects2);

            fanwei.add(zuo);//获得初坐标
            fanwei.add(you);
            fanwei.add((int) y);
            fanwei.add(arrzb);
            array.add(fanwei);
            arrzb++;

            //获得呼吸,心率方差组
            int[] arrylisthxfx = arrylisthx.stream().mapToInt(Integer::valueOf).toArray();
            int xinlvfc = Variance(arrylist1sz);
            int huxifc = Variance(arrylisthxfx); //huxixinlv
            huxixinlv.add(zuo);//获得初坐标
            huxixinlv.add(you);
            huxixinlv.add(huxifc);
            huxixinlv.add(xinlvfc);
            huxixinlv.add(arrzb);
            arrayhx.add(huxixinlv);
        }
    }

    private static void huxifangcha(int hx, List<List<Integer>> huxiqujian, List<List<Integer>> list1, List<List<Integer>> hxlists, List<List<Integer>> arrlist3) {
        int[] arrs = new int[huxiqujian.size()]; //呼吸方差数组
        for (int i = 0; i < huxiqujian.size(); i++) {
            List<Integer> integers = huxiqujian.get(i); //算出数组500多值

            // int a12 = 0;
            int[] array = new int[integers.size()];
            for (int k = 0; k < integers.size(); k++) {
                array[k] = integers.get(k);
            }

            int is = Variance(array);//取出方差值
            arrs[i] = is;
        }


        //求得呼吸方差后,遍历 对比==合并
        int hxkaishi = 0;
        int hxjieshu = 0;


        int hxcs = 0;
        for (int i = 1; i < arrs.length; i++) {
            int hxcha = Math.abs(arrs[i] - arrs[i - 1]);
            List<Integer> list = new ArrayList<>();

            if (hxcha < hx) {
                // hxkaishi = i-1;
                if (hxcs == 0 && i <= arrs.length - 2) {
                    if (Math.abs(arrs[i + 1] - arrs[i]) < hx) {
                        hxkaishi = i - 1;
                        //hxjieshu = i;
                        hxcs = 1;
                        // }else if ( Math.abs(arrs[i+1] - arrs[i]) != 0 ){
                    } else {
                        hxkaishi = i - 1;
                        hxjieshu = i;
                        list.add(hxkaishi);
                        list.add(hxjieshu);
                        hxlists.add(list);
                        hxcs = 0;
                    }
                } else if (hxcs == 0 && i == arrs.length - 1) {
                    hxkaishi = i - 1;
                    hxjieshu = i;
                    list.add(hxkaishi);
                    list.add(hxjieshu);
                    hxlists.add(list);
                    hxcs = 0;
                } else {
                    hxcs++;
                }
            }
        }


        //System.out.println(hxlists); //获得呼吸方差分组

        List<List<Integer>> arrlist2 = new ArrayList<>(list1); //呼吸方差对应时间


        for (int j = 0; j < arrlist2.size(); j++) {
            List<Integer> list = new ArrayList<>();
            List<Integer> integers = arrlist2.get(j);
            Integer integer = integers.get(0);
            if (integer < 0) {
                integer = 0;
            }
            if (hxlists.size() < 1) {
                list.add(integer);
                list.add(integers.get(1));
                arrlist3.add(list);
            }
            for (int i = 0; i < hxlists.size(); i++) {
                List<Integer> shijian = hxlists.get(i);
                Integer kaishisj = shijian.get(0);
                Integer jieshusj = shijian.get(1);
                //if (i==0){
                if (j == kaishisj) {
                    list.add(integer);
                    list.add(arrlist2.get(jieshusj).get(1));
                    arrlist3.add(list);
                    break;
                } else if (j > kaishisj && j <= jieshusj) {
//                        list.add(integers.get(1));
//                        list.add(arrlist2.get(jieshusj+1).get(1));
//                        arrlist3.add(list);
                    break;
                } else if (i == hxlists.size() - 1) {
                    list.add(integer);
                    list.add(integers.get(1));
                    arrlist3.add(list);
                    break;
                }

                //  }
            }
        }
        //呼吸频率形成的时间区间{{6,62},{}{}}
        for (int i = 0; i < arrlist3.size() - 1; i++) {
            arrlist3.get(i).set(1, arrlist3.get(i + 1).get(0));
        }
    }

    private static List<List<Integer>> huxifangchashuzu(List<Integer> RR, int d, int e, List<List<Integer>> slp, List<Integer> timeThree, List<List<Integer>> huxiqujian, int[] arr,List<Integer> PI) {
        int a = 0;
        for (List<Integer> integers : slp) {
            int[] array = new int[integers.size()];
            for (int i = 0; i < integers.size(); i++) {
                array[i] = integers.get(i);
            }

            int i = Variance(array);//取出方差值
            arr[a] = i;
            a = a + 1;
        }

        int xjshouzhi = 0;
        int xjshouzhics = 0;
        int xjcs = 1;
        int xjweizhi = 0;
        int ssshouzhi = 0;
        int ssshouzhics = 0;
        int sscs = 1;
        int type = 0;

        //求得arr方差数组
        int chazhi = 0;
        List<List<Integer>> lists = new ArrayList<>();
        int kaishi = 0;
        int jieshu = 0;
        int cs = 0;

        //参数E:第一次方差合并对比7个数一个方差 前值-后值<E
        for (int i = 1; i < arr.length; i++) {
            List<Integer> list = new ArrayList<>();
            chazhi = Math.abs(arr[i] - arr[i - 1]);
            if (chazhi <= e) {
                // kaishi = i-1;
                if (cs == 0) {
                    kaishi = i - 1;
                    cs = 1;
                } else {
                    cs++;
                }
            } else { //判断前值是否小于2 若小于2 则停止
                if (i > 1) {
                    if (Math.abs(arr[i - 1] - arr[i - 2]) <= e) {
                        //设置尾值
                        jieshu = cs + kaishi + 1;
                        list.add(kaishi);
                        if (i == arr.length - 1) {
                            jieshu = arr.length - 1;
                        }
                        list.add(jieshu);
                        lists.add(list);
                        cs = 0;
                    } else {
                        list.add(i - 1);
                        list.add(i);
                        lists.add(list);
                        cs = 0;
                    }
                } else {
                    list.add(i - 1);
                    list.add(i);
                    lists.add(list);
                    cs = 0;
                }
            }
        }
        //方差数组对比获得 区间坐标(差值的区间坐标,需转换成具体时间坐标)
        //System.out.println(lists);


        List<List<Integer>> list1 = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            List<Integer> list12 = new ArrayList<>();
            List<Integer> integers = lists.get(i);
            int sz = integers.get(0) * d;
            int wz = integers.get(1) * d;
            if (sz <= 0) {
                sz = 0;
            }
            Integer integer = timeThree.get(sz);
            Integer integer1 = timeThree.get(wz);
            list12.add(integer);
            list12.add(integer1);
            list1.add(list12);
        }
        //求得具体时间坐标 此时完成第一步获取7个方差 差值对比晒出来的坐标 接下来第二部寻找 对应呼吸频率
        //System.out.println(list1); //求得时间数组

        for (int i = 0; i < list1.size(); i++) {
            List<Integer> list22 = new ArrayList<>();
            List<Integer> integers = list1.get(i);
            Integer time1 = integers.get(0);
            Integer time2 = integers.get(1);
            for (int j = 0; j < PI.size(); j++) {
                if (j >= time1 && j <= time2) {
                    list22.add(PI.get(j));
                }
            }
            huxiqujian.add(list22);
        }
        return list1;
    }

    private static List<Integer> getSlp(List<Integer> prList, int a1, int a2, int a3, int b1, int b2, int b3, int d, List<List<Integer>> slp) {
        List<Integer> prTwo = new ArrayList<>(prList);
        List<Integer> prThree = new ArrayList<>();
        List<Integer> prtwoThree = new ArrayList<>();
        List<Integer> prThrees = new ArrayList<>();

        //遍历取整
        if (a1 <= 1) {
            a1 = 2;
        }
        int yushu = prList.size() % (a1 - 1) <= 1 ? (prList.size() / (a1 - 1)) : (prList.size() / (a1 - 1)) + 1;
        int rint = (int) rint(yushu);


        //时间数组
        List<Integer> timeOne = new ArrayList<>();
        List<Integer> timeTwo = new ArrayList<>();
        List<Integer> timeThree = new ArrayList<>();
        prThree.add(0, prList.get(0));
        timeOne.add(0, 0);
        loop(prList, a1, b1, prTwo, prThree, rint, timeOne);

        if (a2 <= 1) {
            a2 = 2;
        }
        int yushuTwo = prThree.size() % (a2 - 1) <= 1 ? (prThree.size() / (a2 - 1)) : (prThree.size() / (a2 - 1)) + 1;
        int rintTwo = (int) rint(yushuTwo);
        prtwoThree.add(0, prThree.get(0));
        timeTwo.add(0, 0);
        loop2(prThree, a2, b2, prTwo, prtwoThree, rintTwo, a1, timeOne, timeTwo);
        if (a3 <= 1) {
            a3 = 2;
        }
        int yushuThree = prtwoThree.size() % (a3 - 1) <= 1 ? (prtwoThree.size() / (a3 - 1)) : (prtwoThree.size() / (a3 - 1)) + 1;
        int rintThree = (int) rint(yushuThree);
        loop3(prtwoThree, a3, b3, prTwo, prThrees, rintThree, timeTwo, a2, timeThree);


        //遍历三遍完成
        List<Integer> prThreesCopy = new ArrayList<>(prThrees);

        //prThreesCopy循环以30条数据为一组形成集合
        for (int i = 0; i < prThreesCopy.size(); i++) {
            if (prThreesCopy.size() <= d * i + d) {
                break;
            } else {
                List<Integer> integers = prThreesCopy.subList(i * d, d * i + d);
                slp.add(integers);
            }
        }
        return timeThree;
    }

    private static void zerochuli(List<Integer> prList, List<Integer> prListCopy, List<List<Integer>> qujianlist) {
        int lingkai = 0;
        int lingwei = 0;
        for (int i = 1; i < prList.size(); i++) {
            List<Integer> list = new ArrayList<>();
            Integer integer1 = prList.get(i - 1);
            Integer integer = prList.get(i);
            if (integer1 == 0) {
                if (lingkai == 0) {
                    lingkai = i - 1;
                }
                if (integer != 0) {
                    lingwei = i - 1; //完成一次加入区间中
                    list.add(lingkai);
                    list.add(lingwei);
                    qujianlist.add(list);
                    lingkai = 0;
                } else if (i == prList.size() - 1 && integer == 0) {
                    lingwei = i;
                    list.add(lingkai);
                    list.add(lingwei);
                    qujianlist.add(list);
                    lingkai = 0;
                }
            }
        }
        //将为0的值给前值 list
        for (int i = 1; i < prList.size(); i++) {
            Integer integer = prList.get(i);
            if (integer == 0) {
//                PrList.set(i,PrList.get(i-1));  //给前值
//                PrListCopy.set(i,PrListCopy.get(i-1));
                prList.set(i, 100);  //给前值
                prListCopy.set(i, 100);
            }
        }
    }

    private static void xingchengqujian(List<Integer> prListCopy, List<List<Integer>> qujianlist, Date date, List<List<Integer>> listTime, List<List<Long>> results, List<Integer> prListCopy1, List<List<Object>> results2) {
        for (int i = 0; i < prListCopy.size(); i++) {
            ArrayList<Long> objects = new ArrayList<>();
            ArrayList<Object> objects2 = new ArrayList<>();
            int jun = prListCopy.get(i);
            for (int j = 0; j < listTime.size(); j++) {
                List<Integer> integers = listTime.get(j);
                Integer kaishijian = integers.get(0) - 1;
                Integer shishijian = integers.get(1);
                Integer junzhi = integers.get(2);
                if (i >= kaishijian && i <= shishijian) {
                    jun = junzhi;
                }
            }
            long ts = date.getTime();
            objects.add(ts + i * 1000);
            objects.add(Long.valueOf(jun));
            prListCopy1.add(jun);
            int size = qujianlist.size();
            if (size == 0) {
                objects2.add(ts + i * 1000);
                objects2.add(Long.valueOf(jun));
            } else {
                for (int j = 0; j < size; j++) {
                    List<Integer> integers = qujianlist.get(j);
                    Integer ks = integers.get(0);
                    Integer js = integers.get(1);
                    if (i >= ks && i <= js) {
                        objects2.add(ts + i * 1000);
                        objects2.add(null);
                        break;
                    } else if (j == qujianlist.size() - 1) {
                        objects2.add(ts + i * 1000);
                        objects2.add(Long.valueOf(jun));
                    }
                }
            }
            results2.add(objects2);
            results.add(objects);
        }
    }

    private static void quxianshuzu(List<Integer> prListCopy2, Date date, List<List<Object>> shushui, List<List<Object>> qingxing, List<List<Object>> qianshui, List<List<Object>> zhongdushushui, List<List<Long>> resultsshushui, List<List<Long>> resultsqingxing, List<List<Long>> resultszdshushui, List<List<Long>> resultsqianshui) {
        for (int i = 0; i < prListCopy2.size(); i++) {
            long ts = date.getTime();
            Integer integer = prListCopy2.get(i);
            List<Long> objects = new ArrayList<>();
            shuimianleixing(shushui, i, ts, integer, objects);
            resultsshushui.add(objects);

            //浅睡
            List<Long> objects1 = new ArrayList<>();
            shuimianleixing(qianshui, i, ts, integer, objects1);
            resultsqianshui.add(objects1);

            //清醒
            List<Long> objects2 = new ArrayList<>();
            shuimianleixing(qingxing, i, ts, integer, objects2);
            resultsqingxing.add(objects2);

            //中度熟睡
            List<Long> objects3 = new ArrayList<>();
            shuimianleixing(zhongdushushui, i, ts, integer, objects3);
            resultszdshushui.add(objects3);
        }
    }

    private static void bofengguqujian(List<List<Integer>> listTime, List<List<List<Object>>> smresults) {
        List<List<Object>> smresultsList = new ArrayList<>();
        int sstype = 1;
        int sskai = 0;
        int ssjieshu = 0;
        int xjtype = 1;
        int xjkai = 0;
        int xjjieshu = 0;
        for (int i = 1; i < listTime.size() - 1; i++) {
            List<Object> list = new ArrayList<>();
            List<Object> list11 = new ArrayList<>();
            List<Integer> integers1 = listTime.get(i - 1);
            List<Integer> integers2 = listTime.get(i);
            List<Integer> integers3 = listTime.get(i + 1);
            int cha = integers2.get(2) - integers1.get(2);

            if (cha > 0) {//上升 两种情况一种结束,一种后面还有
                if (sstype == 1) {
                    sskai = i - 1;
                }
                if (integers3.get(2) - integers2.get(2) > 0) {
                    sstype++;
                } else {
                    ssjieshu = sskai + sstype;
                    list.add(sskai);
                    list.add(ssjieshu);
                    sstype = 1;
                    smresultsList.add(list);
                    //i ++;
                }
            } else if (cha < 0) {//下降 两种情况一种结束,一种后面还有
                if (xjtype == 1) {
                    xjkai = i - 1;
                }
                if (integers3.get(2) - integers2.get(2) < 0) {
                    xjtype++;
                } else {
                    xjjieshu = xjkai + xjtype;
//                    if (){
//
//                    }
                    list.add(xjkai);
                    list.add(xjjieshu);
                    xjtype = 1;
                    smresultsList.add(list);
                    // i ++;
                }
            }
            if (i == listTime.size() - 2) {
                int jieshus = xjjieshu;
                if (ssjieshu > xjjieshu) {
                    jieshus = ssjieshu;
                }
                // list11.add(i-1);
                list11.add(jieshus);
                list11.add(listTime.size() - 1);
                xjtype = 1;
                smresultsList.add(list11);
            }
        }


        for (int j = 0; j < smresultsList.size(); j++) {
            List<List<Object>> smresults1 = new ArrayList<>();
            List<Object> objects = smresultsList.get(j);
            int chu = (int) objects.get(0);
            int shi = (int) objects.get(1);
            for (int i = 0; i < listTime.size(); i++) {
                if (i >= chu && i <= shi) {
                    smresults1.add(Collections.singletonList(listTime.get(i)));
                }
            }
            smresults.add(smresults1);
        }
    }

    private static void shuimianleixing(List<List<Object>> shuimian, int i, long ts, Integer integer, List<Long> objects) {
        for (int j = 0; j < shuimian.size(); j++) {
            List list = shuimian.get(j);
            //List list = (List)zhongdushushui.get(j).get(0);
            int kai = (int) list.get(0);
            int shi = (int) list.get(1);
            int jg = (int) list.get(2);
            if (i >= kai && i <= shi && integer != 0) {
                objects.add(ts + i * 1000);
                objects.add(Long.valueOf(jg));
                break;
            } else if (j == shuimian.size() - 1) {
                objects.add(ts + i * 1000);
                objects.add(null);
            }
        }
    }

    //中数
    private static double median(List<Integer> total) {
        double j = 0;
        //集合排序
        Collections.sort(total);
        int size = total.size();
        if (size % 2 == 1) {
            j = total.get((size - 1) / 2);
        } else {
            //加0.0是为了把int转成double类型，否则除以2会算错
            j = (total.get(size / 2 - 1) + total.get(size / 2) + 0.0) / 2;
        }
        return j;
    }

    //众数
    public static int majorityElement(int[] nums) {
        int zhongshu = nums[0];
        int count = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == zhongshu) {
                count++;
            } else {
                count--;
            }
            if (count == 0) {
                count = 1;
                zhongshu = nums[i];
            }
        }
        return zhongshu;
    }


    //平均值
    public static int average(int[] array, int size) {
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += array[i];
        }
        return (int) (sum / size);
    }

    private static List<List<Integer>> getSort(List<List<Integer>> array) {
        List<List<Integer>> arraySort;//坐标合并后,重新取值
        arraySort = new ArrayList(array);
        arraySort = arraySort.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Integer.valueOf(o1.get(2)).compareTo(Integer.valueOf(o2.get(2)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
        //System.out.println(arraySort);
        return arraySort;
    }

    private static List<List<Integer>> getSort1(List<List<Integer>> array) {
        List<List<Integer>> arraySort;//坐标合并后,重新取值
        arraySort = new ArrayList(array);
        arraySort = arraySort.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Integer.valueOf(o1.get(2) + o1.get(3)).compareTo(Integer.valueOf(o2.get(2) + o1.get(3)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());

        return arraySort;
    }

    private static void bdresult(List<Integer> prListCopy, Date date, List<List<Integer>> array, List<List<Long>> result) {
        List<Integer> PrListCopyTwo = new ArrayList<>(prListCopy);
        for (int i = 0; i < array.size(); i++) {
            List<Integer> integers = array.get(i);
            Integer chu = integers.get(0);
            Integer zhong = integers.get(1);
            Integer y = integers.get(2);
            for (int h = 0; h < PrListCopyTwo.size(); h++) {
                long ts = date.getTime();
                if (h >= chu && h <= zhong) {
                    PrListCopyTwo.set(h, y);
                }
            }
        }

        //形成数组 线
        for (int h = 0; h < prListCopy.size(); h++) {
            ArrayList<Long> objects = new ArrayList<>();
            long ts = date.getTime();
            objects.add(ts + h * 1000);
            objects.add(Long.valueOf(PrListCopyTwo.get(h)));
            result.add(objects);
        }
    }

    private static void rrsleep(List<Integer> RR, List<Integer> prListCopy, List<List<Integer>> list, Date date, List<List<Integer>> array, int F) {
        long x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        for (List<Integer> integers : list) {
            List<Integer> fanwei = new ArrayList();
            int zuo = integers.get(0);  //循环出的list 左边为初坐标 右边为结束坐标
            int you = integers.get(1);
            int zysize = you - zuo; //差值size
            int[] rrList = new int[zysize];
            int rrs = 0;
            //先将得到的值从呼吸率数组中遍历一遍放到数组中
            for (int i = zuo; i < you; i++) {
                Integer integer = RR.get(i);
                rrList[rrs] = integer;
                rrs = rrs + 1;
            }
            int variance = Variance(rrList); //呼吸率原始数组方差值

            List<Integer> original = Arrays.asList(ArrayUtils.toObject(rrList)); //原始数组转换成list

            int m = 0;
            int thirtymiaoone = 30;
            int thirtymiaotwo = 30;
            int leixinga = 0;
            int leixingb = 0;
            int nx = variance;
            int shou = 0;
            int wei = 0;
            List<Integer> arrListone = new ArrayList(original);
            List<Integer> arrListtwo = new ArrayList(original);
            List<Integer> arrListcha = new ArrayList(original);
            //遍历循环,左右减值最终取得 首值和尾值
            ArrayList<Integer> objects3 = new ArrayList<>();
            int nb1 = 0;
            int nb2 = 0;
            while (true) {
                if (leixinga != 1) {
                    //n1a
                    //thirtymiaoone = thirtymiaoone+30;
                    if (m > 0) {
                        arrListone = new ArrayList(arrListcha);
                    }

                    for (int i = 0; i < arrListone.size(); i++) { //删除左边前30秒
                        if (i < thirtymiaoone) {
                            arrListone.remove(0);
                        }
                    }
                    int[] intArrOne = arrListone.stream().mapToInt(Integer::valueOf).toArray();
                    int na1 = Variance(intArrOne); //求得na1方差

                    if (na1 < 7) {
                        leixinga = 1;
                        shou = m;
                        nb1 = na1;
                    }
                }

                if (leixingb != 2) {
                    //n1b
                    //thirtymiaotwo = thirtymiaotwo+30;
                    if (m > 0) {
                        arrListtwo = new ArrayList(arrListcha);
                    }

                    for (int i = 0; i < arrListtwo.size(); i++) { //删除左边前30秒
                        if (i < thirtymiaoone) {
                            arrListtwo.remove(arrListtwo.size() - 1);
                        }
                    }
                    int[] intArrTwo = arrListtwo.stream().mapToInt(Integer::valueOf).toArray();
                    int na2 = Variance(intArrTwo); //na2 右边数组方差
                    if (na2 < 6) {
                        leixingb = 2;
                        wei = m;
                        nb2 = na2;
                    }
                }

                if (leixinga == 1 && leixingb == 2) {
                    break;
                }

                for (int i = 0; i < arrListcha.size(); i++) {
                    if (i < thirtymiaoone && leixinga != 1) {
                        arrListcha.remove(0);
                    }
                    if (i < thirtymiaotwo && leixingb != 2) {
                        if (arrListcha.size() > 0) {
                            arrListcha.remove(arrListcha.size() - 1);
                        }

                    }

                }
                int[] intArrCha = arrListcha.stream().mapToInt(Integer::valueOf).toArray();
                nx = Variance(intArrCha);  //最后形成的数组方差
                m++;
            }

            int size = original.size();
            wei = size - (wei + 1) * 30; //取得坐标点.需要+zuo
            shou = (shou + 1) * 30; //取得坐标点.需要+zuo



            long[] arrx = new long[zysize];
            long[] arry = new long[zysize];
            int kai = 0;


            int secondTimestamp = getSecondTimestamp(date);
            //算出原数组值来拼成list
            List<List<Long>> yuan = new ArrayList<>();
            for (int h = zuo; h < you; h++) {
                ArrayList<Long> objects = new ArrayList<>();
                long ts = date.getTime();
                arrx[kai] = ts + h * 1000;
                //arrx[kai] = h;
                arry[kai] = prListCopy.get(h);
                objects.add(arrx[kai]);
                objects.add(Long.valueOf(prListCopy.get(h)));
                yuan.add(objects);
                kai++;
            }

            //通过最小二乘法获得数组
            Map<String, Object> stringObjectMap = lineRegression(arrx, arry); //最小二乘法
            x1 = arrx[0];
            x2 = arrx[arrx.length - 1];
            Long pa = (Long) stringObjectMap.get("a");
            Long pb = (Long) stringObjectMap.get("b");
            y1 = pa * x1 + pb;
            y2 = pa * x2 + pb;
            //形成数组 [[1591116380000, 49], [1591123995000, 49]]
            List<List<Long>> ercheng = new ArrayList<>();
            List<Long> objects1 = new ArrayList<>();
            objects1.add(x1);
            objects1.add(y1);
            List<Long> objects2 = new ArrayList<>();
            objects2.add(x2);
            objects2.add(y2);
            ercheng.add(objects1);
            ercheng.add(objects2);

            fanwei.add(zuo + shou);//获得初坐标
            fanwei.add(zuo + wei);
            fanwei.add((int) y1);
            array.add(fanwei);
        }
    }

    private static void deepsleep(List<Integer> timeThree, int e, int[] arr, int xjshouzhics, int xjcs, int sscs, int type, List<List<Integer>> list, int D) {
        int xjshouzhi;
        int ssshouzhi;
        int ssshouzhics;
        for (int i = 1; i < arr.length; i++) {
            List<Integer> integers = new ArrayList<>();
            //判断是否为下降 前值减后值>0
            if (arr[i - 1] - arr[i] > 0) {
                if (i > 1 && arr[i - 2] - arr[i - 1] > 0 && type != 0) {
                    xjcs = xjcs + 1;
                }

                if (type == 0)    //先赋予下降首值
                {
                    xjcs = xjcs + 1;
                    xjshouzhics = i - 1;
                    xjshouzhi = arr[i - 1];
                    type = 1;
                    continue;
                }
            }
            if (xjcs >= e) { //下降必须大于3
                if (arr[i - 1] - arr[i] > 0 && sscs == 1) {
                    xjcs = xjcs + 1; //如果为下降则+1 如2 3 4 5  循环完清零
                    continue;
                }
                if (arr[i - 1] - arr[i] < 0) {
                    if (arr[i - 2] - arr[i - 1] < 0) {
                        sscs = sscs + 1;
                    } else if (sscs == 1) {
                        sscs = sscs + 1;
                    }

                    if (i == arr.length - 1) { //到结尾判断
                        ssshouzhi = arr[i]; //此时求得下标值
                        ssshouzhics = i;
                    }
                } else {
                    if (sscs >= e) {  //此时说明先下降三次后上升三次完成
                        ssshouzhi = arr[i - 1]; //此时求得下标值
                        ssshouzhics = i - 1;
                        type = 0;
                        sscs = 1;
                        xjcs = 1;
                        int sz = xjshouzhics * (D - 1) - 1;
                        int wz = ssshouzhics * (D - 1) - 1;
                        if (sz < 0) {
                            sz = 0;
                        }
                        integers.add(timeThree.get(sz));
                        integers.add(timeThree.get(wz));
                        list.add(integers);
                    }
                }
            } else {
                xjcs = 0;
            }
        }
    }


    public static Map<String, Object> lineRegression(long[] X, long[] Y) {
        if (null == X || null == Y || 0 == X.length
                || 0 == Y.length || X.length != Y.length) {
            throw new RuntimeException();
        }

        // x平方差和
        long Sxx = varianceSum(X);
        // y平方差和
        Long Syy = varianceSum(Y);
        // xy协方差和
        long Sxy = covarianceSum(X, Y);

        Long xAvg = arraySum(X) / X.length;
        Long yAvg = arraySum(Y) / Y.length;

        Long a = Sxy / Sxx;
        Long b = yAvg - a * xAvg;

        // 相关系数
        double r = Sxy / sqrt(Sxx * Syy);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("a", a);
        result.put("b", b);
        result.put("r", r);

        return result;
    }

    /**
     * 计算方差和
     *
     * @param X
     * @return
     */
    private static long varianceSum(long[] X) {
        long xAvg = arraySum(X) / X.length;
        return arraySqSum(arrayMinus(X, xAvg));
    }

    /**
     * 计算协方差和
     *
     * @param X
     * @param Y
     * @return
     */
    private static long covarianceSum(long[] X, long[] Y) {
        long xAvg = arraySum(X) / X.length;
        long yAvg = arraySum(Y) / Y.length;
        return arrayMulSum(arrayMinus(X, xAvg), arrayMinus(Y, yAvg));
    }

    /**
     * 数组减常数
     *
     * @param X
     * @param x
     * @return
     */
    private static long[] arrayMinus(long[] X, long x) {
        int n = X.length;
        long[] result = new long[n];
        for (int i = 0; i < n; i++) {
            result[i] = X[i] - x;
        }

        return result;
    }

    /**
     * 数组求和
     *
     * @param X
     * @return
     */
    private static long arraySum(long[] X) {
        long s = 0;
        for (long x : X) {
            s = s + x;
        }
        return s;
    }

    /**
     * 数组平方求和
     *
     * @param X
     * @return
     */
    private static long arraySqSum(long[] X) {
        long s = 0;
        for (long x : X) {
            s = (long) (s + pow(x, 2));
            ;
        }
        return s;
    }

    /**
     * 数组对应元素相乘求和
     *
     * @param X
     * @return
     */
    private static long arrayMulSum(long[] X, long[] Y) {
        long s = 0;
        for (int i = 0; i < X.length; i++) {
            s = s + X[i] * Y[i];
        }
        return s;
    }


    public static int Variance(int[] x) {
        int m = x.length;
        long sum = 0;
        for (int i = 0; i < m; i++) {//求和
            sum += x[i];
        }
        long dAve = sum / m;//求平均值
        long dVar = 0;
        for (int i = 0; i < m; i++) {//求方差
            dVar += (x[i] - dAve) * (x[i] - dAve);
        }
        return (int) (dVar / m);
    }

    private static void intValue() {
    }


    public static int getSecondTimestamp(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0, length - 3));
        } else {
            return 0;
        }
    }

    private static void getRemainder(int a1, List<Integer> prTwo, List<Integer> prThree) {
        int integer = prTwo.size() / a1;
        int remainder = prTwo.size() % a1;
        if (remainder > 0) {
            int rest = 0;
            for (int i = 0; i < remainder; i++) {
                rest = prTwo.get(integer * a1 - 1);
                prTwo.set(integer * a1 + i, prTwo.get(integer * a1 - 1));
            }
            prThree.add(rest);
        }
    }

    private static void loop(List<Integer> prList, int a1, int b1, List<Integer> prTwo, List<Integer> prThree, int rint, List<Integer> timeOne) {
        int miao = 0;
        for (int i = 0; i < rint; i++) {
            //从1中取值一直是从0开始
            List<Integer> integerr = new ArrayList<>();
            if (prList.size() < a1) {
                for (int k = 0; k < prList.size(); k++) {
                    int kl = i * a1 + k; //0 1 2   3 4 5    6
                    int ky = kl - i;
                    prTwo.set(ky, prTwo.get(ky - 1));
                }
                return;
            } else {
                integerr = prList.subList(0, a1);
            }
            List<Integer> integers = new ArrayList<>();
            integers.addAll(integerr);
            String type = "0"; //1递增取最大值 2递减取最大值 3绝对值小于b 取第一值 4绝对值大于等于b 取绝对值最大值
            Boolean fun = fun(integers); //true 递增  flase 递减
            int saz = 0;
            int coordinate = 0;
            int absolute = 0;
            if (fun) {
                type = "1";
            } else if (reduce(integers)) {
                type = "2";
            } else if (absoluteSmall(integers, b1)) {
                type = "3";
            } else {
                for (int m = 0; m < integers.size() - 1; m++) {
                    //if(m<integers.size()-1 ) {
                    absolute = abs(integers.get(m + 1) - integers.get(m));//后者减前者取得差值
                    // 先判断
                    if (absolute > saz) { //绝对值大于saz 则赋值给saz 坐标也给与C 取得最大值和坐标
                        saz = absolute;
                        coordinate = m;//坐标
                    }
                    //   }
                }
            }

            if (type.equals("1") || type.equals("2")) {
                int size = integers.size();
                Integer integer = integers.get(size - 1); //自增最大值
                Integer integer1 = integers.get(0); //自增最小值

                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prList.remove(0);
                    }
                }
                for (int l = 0; l < size; l++) {
                    int i1 = i * a1 + l; //0 1 2   3 4 5    6
                    int i2 = i1 - i;//0 1 2   2 3 4    5
                    //将修改的值与2替换
//                        //i2 >= size *
//                        int i3 = 0;
//                        if(i == 0){
//                            i3 = size;
//                        }else {
//                            i3 = i * a1;
//                        }

                    if (l != size - 1) { //判断是否为最大值,最大值原值不变
                        prTwo.set(i2, integer1);
                    }

                    //prTwo.size();
                    //将1中不需要数值删除
                    if (l == 0) {
                        prList.set(0, integer);
                    }
                }
                //添加三
                prThree.add(integers.get(integers.size() - 1));
            } else if (type.equals("3")) {
                Integer integer = integers.get(0); //首值
                Integer integer1 = integers.get(1); //次值
                int size = integers.size(); //a1长度
                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prList.remove(0); //删掉原始数组无用
                    }
                }
                for (int l = 0; l < size; l++) {
                    int i1 = i * a1 + l;
                    int i2 = i1 - i;
                    if (l != size - 1) {
                        prTwo.set(i2, integer); //全部等于首值 不确定i1
                    }
                    if (l == 0) {
                        // prList.set(i2,integer); 不确定
                        prList.set(0, integer);
                    }
                }
                prThree.add(integers.get(0));
            } else {
                Integer integer = integers.get(coordinate + 1);//先获得差值减数值 也就是绑定者
                Integer integerOne = integers.get(0); //首值
                int size = integers.size();
                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prList.remove(0);
                    }
                }
                for (int l = 0; l < size; l++) {
//                        int i1 = i * a1 + l;
//                        prTwo.set(i1,integer);
                    int i1 = i * a1 + l;
                    int i2 = i1 - i;
                    if (l != size - 1) {
                        prTwo.set(i2, integerOne); //全部等于首值
                    } else {
                        prTwo.set(i2, integer); //最后值
                    }
                    if (l == 0) {
                        prList.set(0, integer);
                    }
                }
                prThree.add(prList.get(0));
            }
            miao = miao + (a1 - 1);
            timeOne.add(miao);
        }
    }

    private static void loop2(List<Integer> prThree, int a2, int b2, List<Integer> prTwo, List<Integer> prtwoThree, int rintTwo, int a1, List<Integer> timeOne, List<Integer> timeTwo) {
        int miao = 0;
        int time = 0;
        for (int i = 0; i < rintTwo; i++) {
            //从1中取值一直是从0开始
            List<Integer> integerr = new ArrayList<>();
            if (prThree.size() < a2) {
                for (int k = 0; k < prThree.size(); k++) {
                    int kl = i * a2 + k; //0 1 2   3 4 5    6
                    int ky = kl - i;
                    prTwo.set(ky, prTwo.get(ky - 1));
                }
                return;
            } else {
                integerr = prThree.subList(0, a2);
            }
            //List<Integer> integerr = prThree.subList(0, a2);
            List<Integer> integers = new ArrayList<>();
            integers.addAll(integerr);
            String type = "0"; //1递增取最大值 2递减取最大值 3绝对值小于b 取第一值 4绝对值大于等于b 取绝对值最大值
            Boolean fun = fun(integers); //true 递增  flase 递减
            int coordinate = 0;
            int absolute = 0;
            int saz = 0;
            if (fun) {
                type = "1";
            } else if (reduce(integers)) {
                type = "2";
            } else if (absoluteSmall(integers, b2)) {
                type = "3";
            } else {
                for (int m = 0; m < integers.size(); m++) {
                    if (m < integers.size() - 1) {
                        absolute = abs(integers.get(m + 1) - integers.get(m));
                        // 先判断
                        if (absolute > saz) { //绝对值大于i1
                            saz = absolute;
                            coordinate = m;//坐标
                        }
                    }
                }
            }
            Integer timeInteger = 0;
            Integer timeIntegers = 0;
            Integer zuizhi = 0;
            if (type.equals("1") || type.equals("2")) {
                int size = integers.size();
                Integer integer = integers.get(size - 1);//自增最大值
                Integer integer1 = integers.get(0); //自增最小值
                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prThree.remove(0);
                    }
                }
                for (int l = 0; l < size; l++) {
                    int i1 = i * a2 + l;
                    if (l == 0) {
                        prThree.set(0, integer);
                    }
                }
                Integer shouzhi = timeOne.get(time);
                time = time + a2 - 1; //获得时间下标值最大值
                if (time > timeOne.size() - 1) {
                    return;
                }
                zuizhi = timeOne.get(time);
                for (int l = shouzhi; l < zuizhi; l++) {
                    if (l != zuizhi) { //判断是否为最大值,最大值原值不变
                        prTwo.set(l, integer1);
                    }
                }
                //添加三
                prtwoThree.add(integers.get(integers.size() - 1));

            } else if (type.equals("3")) {
                Integer integer = integers.get(0);
                //自增最小值
                int size = integers.size();
                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prThree.remove(0);
                    }
                }
                for (int l = 0; l < size; l++) {
                    int i1 = i * a2 + l;
                    //  prTwo.set(i1,integer);
                    if (l == 0) {
                        prThree.set(0, integer);
                    }
                }

                int i2 = (i + 1) * a2;
//                if(i2 >= timeOne.size()){ //最大值
//                    prTwo.set(timeOne.size(),integer);
//                }else {

                int i1 = timeInteger - a2 * a1;
                if (i1 < 0) {
                    i1 = 0;
                }
                Integer shouzhi = timeOne.get(time);
                time = time + a2 - 1; //获得时间下标值最大值
                if (time > timeOne.size() - 1) {
                    return;
                }
                zuizhi = timeOne.get(time);
                for (int l = shouzhi; l < zuizhi; l++) {
//                    int i1 = i * a2 + l;
                    prTwo.set(l, integer);
                }
                prtwoThree.add(integers.get(0));
            } else {
                Integer integer = integers.get(coordinate + 1);
                Integer integerOne = integers.get(0);
                int size = integers.size();
                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prThree.remove(0);
                    }
                }
                for (int l = 0; l < size; l++) {
                    int i1 = i * a2 + l;
                    // prTwo.set(i1,integer);
                    if (l == 0) {
                        prThree.set(0, integer);
                    }
                }

                Integer shouzhi = timeOne.get(time);
                time = time + (a2 - 1); //获得时间下标值最大值
                if (time > timeOne.size() - 1) {
                    return;
                }
                zuizhi = timeOne.get(time);
                for (int l = shouzhi; l < zuizhi; l++) {
//                    int i1 = i * a2 + l;
                    if (l != zuizhi) {
                        prTwo.set(l, integerOne); //全部等于首值
                    } else {
                        prTwo.set(l, integer); //最后值
                    }
                }
                prtwoThree.add(prThree.get(0));
            }
//            if(miao==0){
//                timeTwo.add(miao);
//                miao = zuizhi-1;
//                timeTwo.add(miao);
//            }else {
            miao = zuizhi - 1;
            timeTwo.add(miao);
            //}

        }
    }

    private static void loop3(List<Integer> prtwoThree, int a3, int b3, List<Integer> prTwo, List<Integer> prThrees, int rintThree, List<Integer> timeTwo, int a2, List<Integer> timeThree) {
        int time = 0;
        int miao = 0;
        for (int i = 0; i < rintThree; i++) {
            //从1中取值一直是从0开始
            List<Integer> integerr = new ArrayList<>();
            if (prtwoThree.size() < a3) {
                if (prtwoThree.size() == 1) {
                    return;
                }
                for (int k = 0; k < prtwoThree.size(); k++) {
                    int kl = i * a2 + k; //0 1 2   3 4 5    6
                    int ky = kl - i;
                    prTwo.set(ky, prTwo.get(ky - 1));
                }
                return;
            } else {
                integerr = prtwoThree.subList(0, a3);
            }

            List<Integer> integers = new ArrayList<>();
            integers.addAll(integerr);
            String type = "0"; //1递增取最大值 2递减取最大值 3绝对值小于b 取第一值 4绝对值大于等于b 取绝对值最大值
            Boolean fun = fun(integers); //true 递增  flase 递减
            int coordinate = 0;
            int absolute = 0;
            int saz = 0;

            if (fun) {
                type = "1";
            } else if (reduce(integers)) {
                type = "2";
            } else if (absoluteSmall(integers, b3)) {
                type = "3";
            } else {
                for (int m = 0; m < integers.size(); m++) {
                    if (m < integers.size() - 1) {
                        absolute = abs(integers.get(m + 1) - integers.get(m));
                        // 先判断
                        if (absolute > saz) { //绝对值大于i1
                            saz = absolute;
                            coordinate = m;//坐标
                        }
                    }
                }
            }
            Integer timeInteger = 0;
            Integer timeIntegers = 0;
            Integer zuizhi = 0;
            if (type.equals("1") || type.equals("2")) {
                int size = integers.size();
                Integer integer = integers.get(size - 1);
                Integer integer1 = integers.get(0); //自增最小值
                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prtwoThree.remove(0);
                    }
                }
                for (int l = 0; l < size; l++) {
                    int i1 = i * a3 + l;
                    if (l == 0) {
                        prtwoThree.set(0, integer);
                    }
                }

                Integer shouzhi = timeTwo.get(time);
                time = time + a3 - 1; //获得时间下标值最大值
                if (time > timeTwo.size() - 1) {
                    return;
                }
                zuizhi = timeTwo.get(time);
                for (int l = shouzhi; l < zuizhi; l++) {
                    if (l != zuizhi) {
                        prTwo.set(l, integer1);
                    }
                }
                //添加三
                prThrees.add(integers.get(integers.size() - 1));
            } else if (type.equals("3")) {
                Integer integer = integers.get(0);
                int size = integers.size();
                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prtwoThree.remove(0);
                    }
                }
                for (int l = 0; l < size; l++) {
                    int i1 = i * a3 + l;
                    //  prTwo.set(i1,integer);
                    if (l == 0) {
                        prtwoThree.set(0, integer);
                    }
                }
                Integer shouzhi = timeTwo.get(time);
                time = time + a3 - 1; //获得时间下标值最大值
                if (time > timeTwo.size() - 1) {
                    return;
                }
                zuizhi = timeTwo.get(time);
                for (int l = shouzhi; l < zuizhi; l++) {
                    prTwo.set(l, integer);
                }

                prThrees.add(integers.get(0));
            } else {
                Integer integer = integers.get(coordinate + 1);
                Integer integerOne = integers.get(0);
                int size = integers.size();
                for (int z = 0; z < size; z++) {
                    if (z != size - 1) {
                        prtwoThree.remove(0);
                    }
                }
                for (int l = 0; l < size; l++) {
                    int i1 = i * a3 + l;
                    // prTwo.set(i1,integer);
                    if (l == 0) {
                        prtwoThree.set(0, integer);
                    }
                }

                Integer shouzhi = timeTwo.get(time);
                time = time + a3 - 1; //获得时间下标值最大值
                if (time > timeTwo.size() - 1) {
                    return;
                }
                zuizhi = timeTwo.get(time);
                for (int l = shouzhi; l < zuizhi; l++) {
                    if (l != zuizhi) {
                        prTwo.set(l, integerOne); //全部等于首值
                    } else {
                        prTwo.set(l, integer); //最后值
                    }
                }
                prThrees.add(prtwoThree.get(0));
            }

//            if (i==0){
//                miao = 0;
//                timeThree.add(miao);
//            }
            miao = zuizhi - 1;
            timeThree.add(miao);
        }
    }

    //绝对值取值
    public static Boolean absoluteSmall(List<Integer> integers, int b) {
        int i1 = 0;
        //int coordinate =0;
        for (int i = 0; i < integers.size(); i++) {
            if (i < integers.size() - 1) {
                int absolute = abs(integers.get(i + 1) - integers.get(i));
                // 先判断
                if (absolute > i1) { //绝对值大于i1
                    i1 = absolute;
                    //coordinate = i;//坐标
                }
//                    if (absolute<=b){ //绝对值小于b
//                    //absolute i1.getString();
//                    i1 = absolute;
//                }else{
//                    if(absolute>i1){ //绝对值大于i1
//                        i1 = absolute;
//                        coordinate = i;
//                    }
//                }

            }
        }
        if (i1 < b) {
            return true;
        }

        return false;
    }

    //增加
    public static Boolean fun(List<Integer> integers) {
        int tap = 0;
        int tbp = 0;
        for (int i = 0; i < integers.size() - 1; i++) {
            if (integers.get(i) > integers.get(i + 1)) {
                return false;
            } else if (integers.get(i) == integers.get(i + 1)) {
                tap = -1;
            } else {
                tbp = 1;
            }
        }
        if (tap == -1 && tbp == 1) {
            return false;
        }

        return true;
    }

    //减少
    public static Boolean reduce(List<Integer> integers) {
        int tap = 0;
        int tbp = 0;
        for (int i = 0; i < integers.size() - 1; i++) {
            if (integers.get(i) < integers.get(i + 1)) {
                return false;
            } else if (integers.get(i) == integers.get(i + 1)) {
                tap = -1;
            } else {
                tbp = 1;
            }
        }
        if (tap == -1 && tbp == 1) {
            return false;
        }
        return true;
    }

    public static String getEndTime(String createTime, int minute) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(createTime);
        String endTime = format.format(new Date(date.getTime() + minute * 1000));
        return endTime;
    }

    private static void rrsleeps(List<Integer> RR, List<Integer> prListCopy, List<List<Integer>> list, Date date, List<List<Integer>> array, int F) {
        long x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        for (List<Integer> integers : list) {
            List<Integer> fanwei = new ArrayList();
            int zuo = integers.get(0);  //循环出的list 左边为初坐标 右边为结束坐标
            int you = integers.get(1);
            int zysize = you - zuo; //差值size
            int[] rrList = new int[zysize];
            int rrs = 0;
            //先将得到的值从呼吸率数组中遍历一遍放到数组中
            for (int i = zuo; i < you; i++) {
                Integer integer = RR.get(i);
                rrList[rrs] = integer;
                rrs = rrs + 1;
            }
            int variance = Variance(rrList); //呼吸率原始数组方差值

            List<Integer> original = Arrays.asList(ArrayUtils.toObject(rrList)); //原始数组转换成list

            int m = 0;
            int thirtymiaoone = 30;
            int thirtymiaotwo = 30;
            int leixinga = 0;
            int leixingb = 0;
            int nx = variance;
            int shou = 0;
            int wei = 0;
            List<Integer> arrListone = new ArrayList(original);
            List<Integer> arrListtwo = new ArrayList(original);
            List<Integer> arrListcha = new ArrayList(original);
            //遍历循环,左右减值最终取得 首值和尾值
            ArrayList<Integer> objects3 = new ArrayList<>();
            while (true) {
                if (leixinga != 1) {
                    //n1a
                    //thirtymiaoone = thirtymiaoone+30;
                    if (m > 0) {
                        arrListone = new ArrayList(arrListcha);
                    }

                    for (int i = 0; i < arrListone.size(); i++) { //删除左边前30秒
                        if (i < thirtymiaoone) {
                            arrListone.remove(0);
                        }
                    }
                    int[] intArrOne = arrListone.stream().mapToInt(Integer::valueOf).toArray();
                    int na1 = Variance(intArrOne); //求得na1方差
                    shou = m;
                }

                if (leixingb != 2) {
                    //n1b
                    //thirtymiaotwo = thirtymiaotwo+30;
                    if (m > 0) {
                        arrListtwo = new ArrayList(arrListcha);
                    }

                    for (int i = 0; i < arrListtwo.size(); i++) { //删除左边前30秒
                        if (i < thirtymiaoone) {
                            arrListtwo.remove(arrListtwo.size() - 1);
                        }
                    }
                    int[] intArrTwo = arrListtwo.stream().mapToInt(Integer::valueOf).toArray();
                    int na2 = Variance(intArrTwo); //na2 右边数组方差
                    wei = m;

                }

                if (leixinga == 1 && leixingb == 2) {
                    break;
                }

                for (int i = 0; i < arrListcha.size(); i++) {
                    if (i < thirtymiaoone && leixinga != 1) {
                        arrListcha.remove(0);
                    }
                    if (i < thirtymiaotwo && leixingb != 2) {
                        if (arrListcha.size() > 0) {
                            arrListcha.remove(arrListcha.size() - 1);
                        }

                    }

                }
                int[] intArrCha = arrListcha.stream().mapToInt(Integer::valueOf).toArray();
                nx = Variance(intArrCha);  //最后形成的数组方差
                m++;
            }

            int size = original.size();
            wei = size - (wei + 1) * 30; //取得坐标点.需要+zuo
            shou = (shou + 1) * 30; //取得坐标点.需要+zuo


            long[] arrx = new long[zysize];
            long[] arry = new long[zysize];
            int kai = 0;


            int secondTimestamp = getSecondTimestamp(date);
            //算出原数组值来拼成list
            List<List<Long>> yuan = new ArrayList<>();
            for (int h = zuo; h < you; h++) {
                ArrayList<Long> objects = new ArrayList<>();
                long ts = date.getTime();
                arrx[kai] = ts + h * 1000;
                arry[kai] = prListCopy.get(h);
                objects.add(arrx[kai]);
                objects.add(Long.valueOf(prListCopy.get(h)));
                yuan.add(objects);
                kai++;
            }

            //通过最小二乘法获得数组
            Map<String, Object> stringObjectMap = lineRegression(arrx, arry); //最小二乘法
            x1 = arrx[0];
            x2 = arrx[arrx.length - 1];
            Long pa = (Long) stringObjectMap.get("a");
            Long pb = (Long) stringObjectMap.get("b");
            y1 = pa * x1 + pb;
            y2 = pa * x2 + pb;
            //形成数组 [[1591116380000, 49], [1591123995000, 49]]
            List<List<Long>> ercheng = new ArrayList<>();
            List<Long> objects1 = new ArrayList<>();
            objects1.add(x1);
            objects1.add(y1);
            List<Long> objects2 = new ArrayList<>();
            objects2.add(x2);
            objects2.add(y2);
            ercheng.add(objects1);
            ercheng.add(objects2);

            fanwei.add(zuo + shou);//获得初坐标
            fanwei.add(zuo + wei);
            fanwei.add((int) y1);
            array.add(fanwei);
        }
    }

    private static class Shuimianshuzu {
        private List<List<List<Object>>> smresults;
        private List<List<Object>> shushui;
        private List<List<Object>> qingxing;
        private List<List<Object>> qianshui;
        private List<List<Object>> zhongdushushui;

        public Shuimianshuzu(List<List<List<Object>>> smresults, List<List<Object>> shushui, List<List<Object>> qingxing, List<List<Object>> qianshui, List<List<Object>> zhongdushushui) {
            this.smresults = smresults;
            this.shushui = shushui;
            this.qingxing = qingxing;
            this.qianshui = qianshui;
            this.zhongdushushui = zhongdushushui;
        }

        public List<List<Object>> getShushui() {
            return shushui;
        }

        public List<List<Object>> getQingxing() {
            return qingxing;
        }

        public List<List<Object>> getQianshui() {
            return qianshui;
        }

        public Shuimianshuzu invoke() {
            for (int i = 0; i < smresults.size(); i++) {
                List<List<Object>> lists1 = smresults.get(i);
                List<Object> shouList = lists1.get(0);
                List<Object> weiList = lists1.get(lists1.size() - 1);
                List slist = (List) shouList.get(0);
                List wlist = (List) weiList.get(0);

                int shou = (int) slist.get(2);
                int wei = (int) wlist.get(2);
                int cha = wei - shou;
                int size = lists1.size();
                //1上升  1下降 0 1 多个

                int shouz = (int) slist.get(2); //首秒
                int weiz = (int) wlist.get(2); //尾秒

                if (cha > 0) {
                    qingxing.add(wlist);
                    if (i == 0) {
                        qingxing.add(slist);
                    } else {
                        shushui.add(slist);
                    }

                    if (size - 2 >= 1) {
                        for (int j = 1; j < size - 1; j++) {
                            List shoulist1 = (List) lists1.get(j).get(0);
                            int dangqianzhi = (int) shoulist1.get(2);
                            int shoucha = abs(dangqianzhi - shouz);
                            int weicha = abs(dangqianzhi - weiz);
                            if (shoucha < weicha) {
                                zhongdushushui.add(shoulist1);
                            } else {
                                qianshui.add(shoulist1);
                            }
                        }
                    }
                }

                if (cha < 0) {
                    qingxing.add(slist);
                    shushui.add(wlist);

                    if (size - 2 >= 1) {
                        for (int j = 1; j < size - 1; j++) {
                            List shoulist1 = (List) lists1.get(j).get(0);
                            int dangqianzhi = (int) shoulist1.get(2);
                            int shoucha = abs(dangqianzhi - shouz);
                            int weicha = abs(dangqianzhi - weiz);
                            if (shoucha > weicha) {
                                zhongdushushui.add(shoulist1);
                            } else {
                                qianshui.add(shoulist1);
                            }
                        }
                    }
                }
            }

            shushui = shushui.stream().distinct().collect(Collectors.toList());
            qingxing = qingxing.stream().distinct().collect(Collectors.toList());
            qianshui = qianshui.stream().distinct().collect(Collectors.toList());
            return this;
        }
    }

    private static class GetTiDong {
        private List<Integer> pi;
        private List<Integer> prListCopys;
        private List<List<Integer>> arrayhx;
        private List<List<Integer>> tdList;
        private int swxlzs;
        private int swhxzs;
        private int hxzd;
        //private List<Integer> shiwuxlList;

        public GetTiDong(List<Integer> PI, List<Integer> prListCopys, List<List<Integer>> arrayhx, List<List<Integer>> tdList) {
            pi = PI;
            this.prListCopys = prListCopys;
            this.arrayhx = arrayhx;
            this.tdList = tdList;
        }

        public int getSwxlzs() {
            return swxlzs;
        }

        public int getSwhxzs() {
            return swhxzs;
        }

        public int getHxzd() {
            return hxzd;
        }
        /*
         *之前:15个一组方差,单独取出呼吸和心率的最大方差和方差中位数,现在改成15个一组相加
         * */
        public GetTiDong invoke() {
            List<List<Integer>> shiwuxlfc = new ArrayList<>();  //先15个一组取得原始数据
            List<List<Integer>> shiwuhxfc = new ArrayList<>();  //呼吸
            for (int i = 0; i < prListCopys.size(); i++) {
                if (prListCopys.size() <= 15 * i + 15) {
                    break;
                } else {
                    List<Integer> integers = prListCopys.subList(i * 15, 15 * i + 15);
                    List<Integer> integershx = pi.subList(i * 15, 15 * i + 15);
                    shiwuxlfc.add(integers);
                    shiwuhxfc.add(integershx);
                }
            }
            int[] shiwuxlfcarr = new int[shiwuxlfc.size()];
            int[] shiwuhxfcarr = new int[shiwuhxfc.size()];
            // List<Integer> shiwuxlList = Arrays.asList(ArrayUtils.toObject(shiwuxlfcarr));

            //取出方差
            List<Integer> getxlfc = getfc(shiwuxlfc, shiwuxlfcarr);
            List<Integer> gethxfc = getfc(shiwuhxfc, shiwuhxfcarr);
            List<Integer> xlhx = new ArrayList<>();
            int zuida = 0;
            for (int i = 0; i < gethxfc.size(); i++) {
                int hx = gethxfc.get(i);
                int xl = getxlfc.get(i);
                int zongzhi = hx+xl;
                xlhx.add(zongzhi);
                if (zongzhi>zuida){
                    zuida = zongzhi;
                }
            }
            int swxlzs = (int) median(xlhx);//中位数
            int zhongweishu = (zuida-swxlzs)/2+swxlzs;


//            JSONObject xljson = getfczws(shiwuxlfc, shiwuxlfcarr);
//            //shiwuxlList = Arrays.asList(ArrayUtils.toObject(shiwuxlfcarr));
//            swxlzs = (int) xljson.get("zs");
//            int xlzd = (int) xljson.get("zuida");
            JSONObject hxjson = getfczws(shiwuhxfc, shiwuhxfcarr);//呼吸方差中数
            swhxzs = (int) hxjson.get("zs");
//            hxzd = (int) hxjson.get("zuida");
//            int zhh = swxlzs+swhxzs; //中数值
            //int zhongweishu = ((xlzd+hxzd)-zhh)/3+zhh;

            for (int i = 0; i < arrayhx.size() ; i++) {
                List<Integer> integers = arrayhx.get(i);
                Integer hxz = integers.get(2); //呼吸值
                Integer xlz = integers.get(3); //心率值
                if (hxz + xlz > zhongweishu) {
                    tdList.add(integers);
                }
            }
            return this;
        }
    }
}
