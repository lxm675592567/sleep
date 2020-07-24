package com.hife.util;

import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* 睡眠校准
* */
public class SleepCorrectUtil {

    public static JSONObject getCorrect(JSONObject json){
        JSONObject smlx = json.getJSONObject("smlx");
        List<List<Object>> qt = (List<List<Object>>) smlx.get("qt");
        List<List<Object>> xl = (List<List<Object>>) smlx.get("xl");
        List<List<Integer>> tdList = (List<List<Integer>>) json.get("tdList");
        Date date = json.getDate("date");
        long size = json.getLong("size");
        long ts = date.getTime();

        List<List<Object>> smList = new ArrayList<>();
        for (int i = 0; i < qt.size(); i++) {
            List<Object> list = qt.get(i);
            long qian = (long) list.get(0);
            long hou = (long) list.get(1);
            list.set(0,(qian-ts)/1000);
            list.set(1,(hou-ts)/1000);
            smList.add(list);
        }

        //获取最后时间,吧空白段删除
        List<List<Object>> smhxwlList = new ArrayList<>();
        List<List<Object>> smkbList = new ArrayList<>();//空白
        for (int i = smList.size()-1; i >= 0; i--) {
            List<Object> list = smList.get(i);
            String biaoshi = (String) list.get(3);
            int zhi = (int) list.get(2);
            if (biaoshi.equals("G")||zhi==100){
                smList.remove(i);
                smkbList.add(list);
            }else {
                if (!biaoshi.equals("F")){
                    break;
                }

            }
        }
        for (int i = smList.size()-1; i >= 0 ; i--) {
            List<Object> list = smList.get(i);
            String biaoshi = (String) list.get(3);
            if (biaoshi.equals("F")){
                smList.remove(i);
                smhxwlList.add(list);
            }
        }

        //前30分钟不能有快动,有则变为中睡 ,第一次深睡前不能有快动,有的话变中睡
        int kdsj = 1800;
        int kdtype = 0;
        for (int i = 0; i < smList.size(); i++) {
            List<Object> smzhi = smList.get(i);
            long smqian = (long) smzhi.get(0);
//            long smhou = (long) smzhi.get(1);
            String leixing = (String) smzhi.get(3);
            if (smqian<kdsj){
                if (leixing.equals("C")){//判断30分钟有无深睡,有的话则不需要深睡判断
                    kdtype = 1;
                }
                if (leixing.equals("B")){
                    smList.get(i).set(3,"D");
                }
            }
            if (kdtype==0 && smqian>kdsj){//此时说明30分钟后有深睡
                if (leixing.equals("C")){
                    break;
                }
                if (leixing.equals("B")){
                    smList.get(i).set(3,"D");
                }
            }
        }

        //将时间划分120分钟一段,如果最后一段大于100分钟则也划分,小于则归上一段
        //90分钟第一个深睡开始 最后一个快动结束
        int kaishi = 0;
        int jieshu = 0;
        for (int i = 0; i < smList.size(); i++) {
            String type = (String) smList.get(i).get(3);
            if (type.equals("C")){
                kaishi = i;
                break;
            }
        }
        for (int i = smList.size()-1; i >=0; i--) {
            String type = (String) smList.get(i).get(3);
            if (type.equals("B")){
                jieshu = i;
                break;
            }
        }

        int sj = 5400; //90分钟
        List<List<List<Object>>> sleephf = new ArrayList<>();//划分

        long zuida = (long) smList.get(jieshu).get(1)-(long) smList.get(kaishi).get(0);
        int chushu = (int) (zuida / sj);
        int yushu = (int) (zuida %  sj);
        int ystype = 0;//余数type 等于1不找中数
        if (yushu>2400){//余数大于40分钟
            chushu++;
            if (yushu<4200){//余数大于40分钟小于70分钟则求中数时不找他
                ystype = 1;
            }
        }
        List<List<Long>> qujian = new ArrayList<>();
        for (int i = 0; i < chushu; i++) {
            List<Long> list = new ArrayList<>();
            long ls = (long) smList.get(kaishi).get(0);
            list.add((i*sj+ls));
            if (i==chushu-1){
                list.add((long) smList.get(jieshu).get(1));
            }else {
                list.add((i*sj+sj+ls));
            }
            qujian.add(list);
        }

        for (int i = 0; i < qujian.size(); i++) {
            long qian = qujian.get(i).get(0);
            long hou = qujian.get(i).get(1);
            List<List<Object>> list = new ArrayList<>();
            for (int j = kaishi; j <= jieshu; j++) {
                List<Object> smzhi = smList.get(j);
                long smqian = (long) smzhi.get(0);
                long smhou = (long) smzhi.get(1);

                List<Object> smzhis = smList.get(j+1);
                long smqians = (long) smzhis.get(0);
                long smhous = (long) smzhis.get(1);
                if (smqian>=qian && smhou<=hou){
                    list.add(smzhi);
                    if (smqians<qian && smhous>hou){
                        break;
                    }
                }
            }
            sleephf.add(list);
        }

        /*
        * 一快动找值(每段递增),标准25%前后不超过5
        * 1先找到标准段落,1 将求得最大段落比值和最小段落比值/2得到标准比值 2 找到离标准比值最近的段落  3将次段落变为20-30
        * 如果不在,则找到最近的点进行+或者减,直到在范围内
        * 2找到标准值段之后,找到他的位置,在他前面的段落从标准往前递减,在他后面的段落从标准开始递增
        * 规则:
        * 1段落快动多:则需要减少,找到时间段最长的深睡,先判断左右是否为快动,如果是则删掉,如果不是找下一个,直到找到,删掉后,再判断是否达到标准,达到标准后结束
        *   1第一次删除,先删除左右,第二次删除最大左右快动,不管有无
        * 2段落快动少:则需要增加,找到时间段最长的快动,判断左右是否有中睡,如果有,则变为快动
        * */
        getksyd(sleephf,ystype);



        /*
         * 一深睡找值(每段递减),标准15%前后不超过5
         * 1先找到标准段落,1 将求得最大段落比值和最小段落比值/2得到标准比值 2 找到离标准比值最近的段落  3将次段落变为10-20
         * 如果不在,则找到最近的点进行+或者减,直到在范围内
         * 2找到标准值段之后,找到他的位置,在他前面的段落从标准往前递减,在他后面的段落从标准开始递增
         * 规则:
         * 1段落快动多:则需要减少,找到时间段最长的深睡,先判断左右是否为快动,如果是则删掉,如果不是找下一个,直到找到,删掉后,再判断是否达到标准,达到标准后结束
         *   1第一次删除,先删除左右,第二次删除最大左右快动,不管有无
         * 2段落快动少:则需要增加,找到时间段最长的快动,判断左右是否有中睡,如果有,则变为快动
        * */

        getSs(sleephf, ystype);



        /*
       * 三 浅睡
       * 1快动右边中睡变浅睡 2中睡有动变浅睡 3两个深睡之间不能有浅睡(出去清醒和快动)
       * */
        getQs(tdList, smList);



        /*
        * 形成最后结果
        * */
        JSONObject smlxNew = new JSONObject();
        getSmjg(size, ts, smList, smhxwlList, smkbList);

        smlxNew.put("xl",xl);
        smlxNew.put("qt",smList);

        return smlxNew;
    }

    private static void getSmjg(long size, long ts, List<List<Object>> smList, List<List<Object>> smhxwlList, List<List<Object>> smkbList) {
        for (int i = 0; i < smhxwlList.size(); i++) {
            List<Object> objects = smhxwlList.get(i);
            smList.add(objects);
        }

        for (int i = smkbList.size()-1; i >= 0; i--) {//最后空白不加
            List<Object> objects = smkbList.get(i);
            long hou = (long) objects.get(1);
            if (hou!=size){
                smList.add(objects);
            }
        }
        int sbz = 0;//深睡變中睡
        for (int i = 0; i < smList.size(); i++) {
            List<Object> list = smList.get(i);
            long qian =  (long) list.get(0);
            long hou =  (long) list.get(1);
            String type = (String) list.get(3);
            smList.get(i).set(0,ts + qian * 1000);
            smList.get(i).set(1,ts + hou * 1000);
            if (type.equals("F")){
                smList.get(i).set(2,30);
            }else {
                smList.get(i).set(2,60);
            }

            //第一次深睡变中,中变浅
            if (sbz == 0){
                if (type.equals("D")){
                    smList.get(i).set(3,"E");
                }
                if (type.equals("C")){
                    smList.get(i).set(3,"D");
                    sbz = 1;
                }
            }
        }
    }

    private static void getQs(List<List<Integer>> tdList, List<List<Object>> smList) {
        List<List<Object>> smssList = new ArrayList<>();//深睡
        //1快动右边中睡变浅睡
        for (int i = 0; i < smList.size()-1; i++) {
            String zuobiaoq = (String) smList.get(i).get(3);
            String zuobiaoh = (String) smList.get(i+1).get(3);
            if (zuobiaoq.equals("B") && !zuobiaoh.equals("B")){
                smList.get(i+1).set(3,"E");
            }
        }
        //2中睡有动变浅睡
        for (int i = 0; i < smList.size(); i++) {
            String zuobiaoq = (String) smList.get(i).get(3);
            if (zuobiaoq.equals("D")){
                long kai = (long) smList.get(i).get(0);
                long shi = (long) smList.get(i).get(1);
                int type = 0;
                for (int j = 0; j < tdList.size(); j++) {
                    int tdkai = (int) tdList.get(j).get(0);
                    int tdshi = (int) tdList.get(j).get(1);
                    if (tdkai>=kai && tdshi<=shi){
                        type++;
                    }
                }
                if (type>=1){ //改为浅睡
                    smList.get(i).set(3,"E");
             }
            }

            if (zuobiaoq.equals("C")){
                if (smList.get(i).size()<=4){
                    smList.get(i).add(i);
                }
                smssList.add(smList.get(i));
            }
        }
        //3两个深睡之间不能有浅睡(除去清醒和快动)
        for (int i = 0; i < smssList.size()-1; i++) {
            int qzuobiao = (int) smssList.get(i).get(4);
            int hzuobiao = (int) smssList.get(i+1).get(4);
            int type = 0;
            for (int j = qzuobiao+1; j < hzuobiao; j++) {
                String zuobiaoq = (String) smList.get(j).get(3);
                if (zuobiaoq.equals("A") || zuobiaoq.equals("B")){
                    type = 1;
                }
            }
            if (type == 0){
                for (int j = qzuobiao+1; j < hzuobiao; j++) {
                    String zuobiaoq = (String) smList.get(j).get(3);
                    if (zuobiaoq.equals("E") ){
                        smList.get(j).set(3,"D");
                    }
                }
            }
        }
    }

    private static void getSs(List<List<List<Object>>> sleephf, int ystype) {
        int biaozhun = 15;//标准值

        List<List<Object>> bzList = new ArrayList<>();//比值list
        long zdbz = 0;//最大比值
        long zxbz = 100;//最小比值
        for (List<List<Object>> lists : sleephf) {
            long zong = 0;//总长度值
            long sszong = 0;//深睡总值
            int kdbizhi = 0;
            int czkdzd = 0;//差值快动最大坐标
            int czsszd = 0;//差值深睡最大坐标
            for (int i = 0; i < lists.size(); i++) {
                List<Object> ls = lists.get(i);
                long qian = (long) ls.get(0);
                long hou = (long) ls.get(1);
                String type = (String) ls.get(3);
                long chazhi = hou-qian;
                zong = zong+chazhi;
                if (type.equals("C")){//深睡总数
                    sszong = sszong+chazhi;
                }
                if (chazhi>czkdzd && (type.equals("A")||type.equals("B"))){//清醒和快动
                    czkdzd = i;
                }
                if (chazhi>czsszd && (type.equals("C"))){//深睡
                    czsszd = i;
                }
            }

            double floor = (double) sszong / (double) zong;
            kdbizhi = (int) (floor*100);
            List<Object> linshis = new ArrayList<>();
            linshis.add(sszong);//深睡总值
            linshis.add(zong);//总长度值
            linshis.add(kdbizhi);//深睡和总长度比值
            linshis.add(czkdzd);//清醒快动最大坐标
            linshis.add(czsszd);//深睡最大坐标
            bzList.add(linshis);

            if (zdbz<kdbizhi){
                zdbz = kdbizhi;
            }
            if (zxbz>kdbizhi){
                zxbz = kdbizhi;
            }
        }
        int zhongzhi = (int) ((zdbz+zxbz)/2);

        //找到标准段落  1判断中值是否大于15+5 大于找小值,小于找大值 遍历三个段落
        int bztype = 0;
        if (zhongzhi>biaozhun+5){
            bztype = 1;//等于1是大值,找小值
        }else if (zhongzhi<biaozhun-5){
            bztype = 2;//等于2是小值,找大的
        }

        int xiangjin = 100;//相近最小
        int bzzb = 0; //标准坐标
        int size = bzList.size();
        if (ystype == 1){
            size--;
        }
        for (int i = 0; i < size; i++) {
            int bz = (int) bzList.get(i).get(2);
            int abs = Math.abs(zhongzhi - bz);
            if (bztype==1){
                if (bz<zhongzhi){
                    if (xiangjin>abs){
                        xiangjin = abs;
                        bzzb = i;
                    }
                }
            }else if (bztype==2){
                if (bz>zhongzhi){
                    if (xiangjin>abs){
                        xiangjin = abs;
                        bzzb = i;
                    }
                }
            }else {
                if (xiangjin>abs){
                    xiangjin = abs;
                    bzzb = i;
                }
            }
        }

        //对标准段落进行筛查,看是否在20-30之间,在则进行下一步,如果不在则根据增加或者减少进行取值
        int bzbz = (int) bzList.get(bzzb).get(2);//标准比值
        int bzkdzdbz = (int) bzList.get(bzzb).get(3);//标准快动最大坐标
        int bzsszdbz = (int) bzList.get(bzzb).get(4);//标准深睡最大坐标

        List<List<Object>> bzlists = sleephf.get(bzzb);

        //添加快动,熟睡单独数组
        List<List<Object>> kddandu = new ArrayList<>();
        List<List<Object>> ssdandu = new ArrayList<>();
        for (int i = 0; i < bzlists.size(); i++) {
            List<Object> bzlist = bzlists.get(i);
            String type = (String) bzlist.get(3);
            if (type.equals("B")){
                bzlist.add(i);
                kddandu.add(bzlist);
            }
            if (type.equals("C")){
                bzlist.add(i);
                ssdandu.add(bzlist);
            }
        }

        if (bzbz>biaozhun+5){//删掉快动周围
            //标准值15  坐标  标准坐标 最大值快动坐标 标准list
            getScssss(biaozhun, bzList, bzzb, bzkdzdbz, bzlists);
        }else if (bzbz<biaozhun-5){//增加深睡周围
            getZjkdss(biaozhun,bzList, bzzb, bzsszdbz, bzlists, ssdandu);
        }
        //以上标准段落校准完成,用标准段落坐标bzzb,往前倒叙递减,往后递增
        int bzbzNew = (int) bzList.get(bzzb).get(2);//更新后的标准段落比值
        for (int i = bzzb-1; i >= 0; i--) {
//            //第一次必须小于bzbzNew,比他小不变,比他大减少
//            List<List<Object>> lists = sleephf.get(i);
//            int bzNew = (int) bzList.get(i).get(2);//比值
//            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
//            int sszdbzNew = (int) bzList.get(i).get(4);//标准深睡最大坐标
//            if (bzNew>bzbzNew){
//                getScssss(bzbzNew-5, bzList, i, kdzdbzNew, lists);
//            }
//            bzbzNew = (int) bzList.get(i).get(2);
            //第一次必须小于bzbzNew,比他小不变,比他大减少
            List<List<Object>> lists = sleephf.get(i);
            int bzNew = (int) bzList.get(i).get(2);//比值
            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
            int sszdbzNew = (int) bzList.get(i).get(4);//标准深睡最大坐标
            List<List<Object>> ssdanduNew = new ArrayList<>();
            for (int j = 0; j < lists.size(); j++) {
                List<Object> bzlist = lists.get(j);
                String type = (String) bzlist.get(3);
                if (type.equals("C")){
                    bzlist.add(i);
                    ssdanduNew.add(bzlist);
                }
            }
            if (bzNew>bzbzNew){
                getZjkdss(bzbzNew+5,bzList, i, sszdbzNew, lists, ssdanduNew);
            }
            bzbzNew = (int) bzList.get(i).get(2);
        }
        bzbzNew = (int) bzList.get(bzzb).get(2);
        int kashizhi = bzzb+1;
        if (bzzb==bzList.size()-1){
            kashizhi--;
        }
        for (int i = bzzb+1; i < bzList.size(); i++) {
            //第一次必须大于bzbzNew,比他大不变,比他小增加
            List<List<Object>> lists = sleephf.get(i);

            List<List<Object>> ssdanduNew = new ArrayList<>();
            for (int j = 0; j < lists.size(); j++) {
                List<Object> bzlist = lists.get(j);
                String type = (String) bzlist.get(3);
                if (type.equals("C")){
                    bzlist.add(i);
                    ssdanduNew.add(bzlist);
                }
            }


            int bzNew = (int) bzList.get(i).get(2);//比值
            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
            int sszdbzNew = (int) bzList.get(i).get(4);//标准深睡最大坐标
            if (bzNew>bzbzNew){
                getScssss(bzbzNew-5, bzList, i, kdzdbzNew, lists);
            }
            bzbzNew = (int) bzList.get(i).get(2);
//            //第一次必须大于bzbzNew,比他大不变,比他小增加
//            List<List<Object>> lists = sleephf.get(i);
//
//            List<List<Object>> ssdanduNew = new ArrayList<>();
//            for (int j = 0; j < lists.size(); j++) {
//                List<Object> bzlist = lists.get(j);
//                String type = (String) bzlist.get(3);
//                if (type.equals("C")){
//                    bzlist.add(i);
//                    ssdanduNew.add(bzlist);
//                }
//            }
//
//
//            int bzNew = (int) bzList.get(i).get(2);//比值
//            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
//            int sszdbzNew = (int) bzList.get(i).get(4);//标准深睡最大坐标
//            if (bzNew>bzbzNew){
//                getZjkdss(bzbzNew+5,bzList, i, sszdbzNew, lists, ssdanduNew);
//            }
//            bzbzNew = (int) bzList.get(i).get(2);
        }
    }

    private static void getksyd(List<List<List<Object>>> sleephf,int ystype) {
        int biaozhun = 25;//标准值

        List<List<Object>> bzList = new ArrayList<>();//比值list
        long zdbz = 0;//最大比值
        long zxbz = 100;//最小比值
        for (List<List<Object>> lists : sleephf) {
            long zong = 0;//总长度值
            long kdzong = 0;//快动总值
            int kdbizhi = 0;
            int czkdzd = 0;//差值快动最大坐标
            int czsszd = 0;//差值深睡最大坐标
            for (int i = 0; i < lists.size(); i++) {
                List<Object> ls = lists.get(i);
                long qian = (long) ls.get(0);
                long hou = (long) ls.get(1);
                String type = (String) ls.get(3);
                long chazhi = hou-qian;
                zong = zong+chazhi;
                if (type.equals("A")||type.equals("B")){//清醒和快动总数
                    kdzong = kdzong+chazhi;
                }
                if (chazhi>czkdzd && (type.equals("A")||type.equals("B"))){//清醒和快动
                    czkdzd = i;
                }
                if (chazhi>czsszd && (type.equals("C"))){//深睡
                    czsszd = i;
                }
            }

            double floor = (double) kdzong / (double) zong;
            kdbizhi = (int) (floor*100);
            List<Object> linshis = new ArrayList<>();
            linshis.add(kdzong);//快动总值
            linshis.add(zong);//总长度值
            linshis.add(kdbizhi);//快动和总长度比值
            linshis.add(czkdzd);//清醒快动最大坐标
            linshis.add(czsszd);//深睡最大坐标
            bzList.add(linshis);

            if (zdbz<kdbizhi){
                zdbz = kdbizhi;
            }
            if (zxbz>kdbizhi){
                zxbz = kdbizhi;
            }
        }
        int zhongzhi = (int) ((zdbz+zxbz)/2);

        //找到标准段落  1判断中值是否大于25+5 大于找小值,小于找大值 遍历三个段落
        int bztype = 0;
        if (zhongzhi>biaozhun+5){
            bztype = 1;//等于1是大值,找小值
        }else if (zhongzhi<biaozhun-5){
            bztype = 2;//等于2是小值,找大的
        }

        int xiangjin = 100;
        int bzzb = 0; //标准坐标
        int size = bzList.size();
        if (ystype == 1){
            size--;
        }
        for (int i = 0; i < size; i++) {
            int bz = (int) bzList.get(i).get(2);
            int abs = Math.abs(zhongzhi - bz);
            if (bztype==1){
                if (bz<zhongzhi){
                    if (xiangjin>abs){
                        xiangjin = abs;
                        bzzb = i;
                    }
                }
            }else if (bztype==2){
                if (bz>zhongzhi){
                    if (xiangjin>abs){
                        xiangjin = abs;
                        bzzb = i;
                    }
                }
            }else {
                if (xiangjin>abs){
                    xiangjin = abs;
                    bzzb = i;
                }
            }
        }

        //对标准段落进行筛查,看是否在20-30之间,在则进行下一步,如果不在则根据增加或者减少进行取值
        int bzbz = (int) bzList.get(bzzb).get(2);//标准比值
        int bzkdzdbz = (int) bzList.get(bzzb).get(3);//标准快动最大坐标
        int bzsszdbz = (int) bzList.get(bzzb).get(4);//标准深睡最大坐标
//        bztype =0;
//        if (bzbz>biaozhun+5){
//            bztype = 1;//等于1是大值,找到深睡最大值左右,进行减少
//        }else if (bzbz<biaozhun+5){
//            bztype = 2;//等于2是小值,找到清醒左右,进行增加
//        }
//        List<List<Object>> bzlists = sleephf.get(bzzb);
//        for (int i = 0; i < bzlists.size(); i++) {
//            List<Object> bzlist = bzlists.get(i);
//            if (bztype==1){//找到深睡最大值坐标左右进行减少
//            }
//        }
        List<List<Object>> bzlists = sleephf.get(bzzb);

        //添加快动,熟睡单独数组
        List<List<Object>> kddandu = new ArrayList<>();
        List<List<Object>> ssdandu = new ArrayList<>();
        for (int i = 0; i < bzlists.size(); i++) {
            List<Object> bzlist = bzlists.get(i);
            String type = (String) bzlist.get(3);
            if (type.equals("B")){
                bzlist.add(i);
                kddandu.add(bzlist);
            }
            if (type.equals("C")){
                bzlist.add(i);
                ssdandu.add(bzlist);
            }
        }

        if (bzbz>biaozhun+5){//删掉深睡周围
                   //标准值25  坐标  标准坐标 最大值深睡坐标 标准list
            getScss(biaozhun, bzList, bzzb, bzsszdbz, bzlists);
        }else if (bzbz<biaozhun-5){//增加快动周围
            getZjkd(biaozhun,bzList, bzzb, bzkdzdbz, bzlists, kddandu);
        }
        //以上标准段落校准完成,用标准段落坐标bzzb,往前倒叙递减,往后递增
        int bzbzNew = (int) bzList.get(bzzb).get(2);//更新后的标准段落比值
        for (int i = bzzb-1; i >= 0; i--) {
            //第一次必须小于bzbzNew,比他小不变,比他大减少
            List<List<Object>> lists = sleephf.get(i);
            int bzNew = (int) bzList.get(i).get(2);//比值
            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
            int sszdbzNew = (int) bzList.get(i).get(4);//标准深睡最大坐标
            if (bzNew>bzbzNew){
                getScss(bzbzNew-5, bzList, i, sszdbzNew, lists);
            }
            bzbzNew = (int) bzList.get(i).get(2);
        }
        bzbzNew = (int) bzList.get(bzzb).get(2);
        int kashizhi = bzzb+1;
        if (bzzb==bzList.size()-1){
            kashizhi--;
        }
        for (int i = bzzb+1; i < bzList.size(); i++) {
            //第一次必须大于bzbzNew,比他大不变,比他小增加
            List<List<Object>> lists = sleephf.get(i);

            List<List<Object>> kddanduNew = new ArrayList<>();
            for (int j = 0; j < lists.size(); j++) {
                List<Object> bzlist = lists.get(j);
                String type = (String) bzlist.get(3);
                if (type.equals("B")){
                    bzlist.add(j);
                    kddanduNew.add(bzlist);
                }
            }


            int bzNew = (int) bzList.get(i).get(2);//比值
            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
            if (bzNew>bzbzNew){
                getZjkd(bzbzNew+5,bzList, i, kdzdbzNew, lists, kddanduNew);
            }
            bzbzNew = (int) bzList.get(i).get(2);
        }
    }
    private static void getZjkdss(int biaozhun,List<List<Object>> bzList, int bzzb, int bzkdzdbz, List<List<Object>> bzlists, List<List<Object>> kddandu) {
//        for (int i = bzkdzdbz+1; i < bzlists.size(); i++) {//往右(时间大)找,找到第一个停止
//            List<Object> bzlist = bzlists.get(i);
//            long qian = (long) bzlist.get(0);
//            long hou = (long) bzlist.get(1);
//            long chazhi = hou-qian;
//            String type = (String) bzlist.get(3);
//            if (type.equals("D")){//断是否是中睡D改为快动
//                bzlists.get(i).set(3,"B");
//                long kdz = (long) bzList.get(bzzb).get(0);//快动值
//                bzList.get(bzzb).set(0,kdz+chazhi);//更新快动值
//                break;
//            }
//        }
//        for (int i = bzkdzdbz-1; i >= 0; i--) {//往左(时间小)找
//            List<Object> bzlist = bzlists.get(i);
//            long qian = (long) bzlist.get(0);
//            long hou = (long) bzlist.get(1);
//            long chazhi = hou-qian;
//            String type = (String) bzlist.get(3);
//            if (type.equals("D")){//判断是否是中睡D改为快动
//                bzlists.get(i).set(3,"B");
//                long kdz = (long) bzList.get(bzzb).get(0);//快动值
//                bzList.get(bzzb).set(0,kdz+chazhi);//更新快动值
//                break;
//            }
//        }


        int kdzj = 7200;//快动最近
        int zjkdz = 0; //最近快动左
        int zjkdy = 0; //最近快动右
        for (int i = 0; i < kddandu.size()-1; i++) {
            long qhou = (long) kddandu.get(i).get(1);//前后值
            long hqou = (long) kddandu.get(i+1).get(0);//后前值
            int zuobiaoq = (int) kddandu.get(i).get(4);//坐标前
            int zuobiaoh = (int) kddandu.get(i+1).get(4);//坐标后
            int cha = (int) (hqou - qhou);
            if (kdzj>cha && cha>0){
                kdzj = cha;
                zjkdz = zuobiaoq;
                zjkdy = zuobiaoh;
            }
        }
        for (int i = zjkdz+1; i < zjkdy; i++) {
            bzlists.get(i).set(3, "C");
            List<Object> bzlist = bzlists.get(i);
            kddandu.add(bzlist);

            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou-qian;
            long kdz = (long) bzList.get(bzzb).get(0);//快动值
            bzList.get(bzzb).set(0,kdz+chazhi);//更新快动值
            break;
        }

        //修改完成后,对比是否比达到标准
        long bzqz = (long) bzList.get(bzzb).get(0);
        long bzhz = (long) bzList.get(bzzb).get(1);
        double floor = (double) bzqz / (double) bzhz;
        int bzbizhi = (int) (floor*100);//标准比值
        bzList.get(bzzb).set(2, bzbizhi);

        if (bzbizhi<biaozhun-5 ){
            getZjkdss(biaozhun,bzList, bzzb, bzkdzdbz, bzlists, kddandu);
        }
    }
    private static void getZjkd(int biaozhun,List<List<Object>> bzList, int bzzb, int bzkdzdbz, List<List<Object>> bzlists, List<List<Object>> kddandu) {
//        for (int i = bzkdzdbz+1; i < bzlists.size(); i++) {//往右(时间大)找,找到第一个停止
//            List<Object> bzlist = bzlists.get(i);
//            long qian = (long) bzlist.get(0);
//            long hou = (long) bzlist.get(1);
//            long chazhi = hou-qian;
//            String type = (String) bzlist.get(3);
//            if (type.equals("D")){//断是否是中睡D改为快动
//                bzlists.get(i).set(3,"B");
//                long kdz = (long) bzList.get(bzzb).get(0);//快动值
//                bzList.get(bzzb).set(0,kdz+chazhi);//更新快动值
//                break;
//            }
//        }
//        for (int i = bzkdzdbz-1; i >= 0; i--) {//往左(时间小)找
//            List<Object> bzlist = bzlists.get(i);
//            long qian = (long) bzlist.get(0);
//            long hou = (long) bzlist.get(1);
//            long chazhi = hou-qian;
//            String type = (String) bzlist.get(3);
//            if (type.equals("D")){//判断是否是中睡D改为快动
//                bzlists.get(i).set(3,"B");
//                long kdz = (long) bzList.get(bzzb).get(0);//快动值
//                bzList.get(bzzb).set(0,kdz+chazhi);//更新快动值
//                break;
//            }
//        }


        int kdzj = 7200;//快动最近
        int zjkdz = 0; //最近快动左
        int zjkdy = 0; //最近快动右
        for (int i = 0; i < kddandu.size()-1; i++) {
            long qhou = (long) kddandu.get(i).get(1);//前后值
            long hqou = (long) kddandu.get(i+1).get(0);//后前值
            int zuobiaoq = (int) kddandu.get(i).get(4);//坐标前
            int zuobiaoh = (int) kddandu.get(i+1).get(4);//坐标后
            int cha = (int) (hqou - qhou);
            if (kdzj>cha && cha>0){
                kdzj = cha;
                zjkdz = zuobiaoq;
                zjkdy = zuobiaoh;
            }
        }
        for (int i = zjkdz+1; i < zjkdy; i++) {
            bzlists.get(i).set(3, "B");
            List<Object> bzlist = bzlists.get(i);
            kddandu.add(bzlist);

            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou-qian;
            long kdz = (long) bzList.get(bzzb).get(0);//快动值
            bzList.get(bzzb).set(0,kdz+chazhi);//更新快动值
            break;
        }

        //修改完成后,对比是否比达到标准
        long bzqz = (long) bzList.get(bzzb).get(0);
        long bzhz = (long) bzList.get(bzzb).get(1);
        double floor = (double) bzqz / (double) bzhz;
        int bzbizhi = (int) (floor*100);//标准比值
        bzList.get(bzzb).set(2, bzbizhi);

        if (bzbizhi<biaozhun-5 ){
                getZjkd(biaozhun,bzList, bzzb, bzkdzdbz, bzlists, kddandu);
            }
    }
    private static void getScssss(int biaozhun, List<List<Object>> bzList, int bzzb, int bzsszdbz, List<List<Object>> bzlists) {
        for (int i = bzsszdbz + 1; i < bzlists.size(); i++) {//往右(时间大)找,找到第一个停止
            List<Object> bzlist = bzlists.get(i);
            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou - qian;
            String type = (String) bzlist.get(3);
            if (type.equals("C") ) {//判断是否是深睡是的话改为中睡D
                bzlists.get(i).set(3, "D");
                long kdz = (long) bzList.get(bzzb).get(0);//快动值
                bzList.get(bzzb).set(0, kdz - chazhi);//更新快动值
                break;
            }
        }
        for (int i = bzsszdbz - 1; i >= 0; i--) {//往左(时间小)找
            List<Object> bzlist = bzlists.get(i);
            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou - qian;
            String type = (String) bzlist.get(3);
            if (type.equals("C")) {//判断是否是快动是的话改为中睡D
                bzlists.get(i).set(3, "D");
                long kdz = (long) bzList.get(bzzb).get(0);//快动值
                bzList.get(bzzb).set(0, kdz - chazhi);//更新快动值
                break;
            }
        }


        //修改完成后,对比是否比达到标准
        long bzqz = (long) bzList.get(bzzb).get(0);
        long bzhz = (long) bzList.get(bzzb).get(1);
        double floor = (double) bzqz / (double) bzhz;
        int bzbizhi = (int) (floor*100);//标准比值
        bzList.get(bzzb).set(2, bzbizhi);

        if (bzbizhi>biaozhun+5 ){
            getScssss(biaozhun, bzList, bzzb, bzsszdbz, bzlists);
        }
    }
    private static void getScss(int biaozhun, List<List<Object>> bzList, int bzzb, int bzsszdbz, List<List<Object>> bzlists) {
        for (int i = bzsszdbz + 1; i < bzlists.size(); i++) {//往右(时间大)找,找到第一个停止
            List<Object> bzlist = bzlists.get(i);
            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou - qian;
            String type = (String) bzlist.get(3);
            if (type.equals("A") || type.equals("B")) {//判断是否是快动是的话改为中睡D
                bzlists.get(i).set(3, "D");
                long kdz = (long) bzList.get(bzzb).get(0);//快动值
                bzList.get(bzzb).set(0, kdz - chazhi);//更新快动值
                break;
            }
        }
        for (int i = bzsszdbz - 1; i >= 0; i--) {//往左(时间小)找
            List<Object> bzlist = bzlists.get(i);
            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou - qian;
            String type = (String) bzlist.get(3);
            if (type.equals("A") || type.equals("B")) {//判断是否是快动是的话改为中睡D
                bzlists.get(i).set(3, "D");
                long kdz = (long) bzList.get(bzzb).get(0);//快动值
                bzList.get(bzzb).set(0, kdz - chazhi);//更新快动值
                break;
            }
        }


        //修改完成后,对比是否比达到标准
        long bzqz = (long) bzList.get(bzzb).get(0);
        long bzhz = (long) bzList.get(bzzb).get(1);
        double floor = (double) bzqz / (double) bzhz;
        int bzbizhi = (int) (floor*100);//标准比值
        bzList.get(bzzb).set(2, bzbizhi);
//        if (bzbizhi>biaozhun ){//第一次未达目标,则相近值添加
//            int kdzj = 7200;//快动最近
//            int zjkdz = 0; //最近快动左
//            int zjkdy = 0; //最近快动右
//            for (int i = 0; i < ssdandu.size()-1; i++) {
//                long qhou = (long) ssdandu.get(i).get(1);//前后值
//                long hqou = (long) ssdandu.get(i+1).get(0);//后前值
//                int zuobiaoq = (int) ssdandu.get(i).get(4);//坐标前
//                int zuobiaoh = (int) ssdandu.get(i+1).get(4);//坐标后
//                int cha = (int) (hqou - qhou);
//                if (kdzj>cha && cha>0){
//                    kdzj = cha;
//                    zjkdz = zuobiaoq;
//                    zjkdy = zuobiaoh;
//                }
//            }
//            for (int i = zjkdz+1; i < zjkdy; i++) {
//                bzlists.get(i).set(3, "D");
//                List<Object> bzlist = bzlists.get(i);
//                ssdandu.add(bzlist);
//                break;
//            }
//        }

        if (bzbizhi>biaozhun+5 ){
            getScss(biaozhun, bzList, bzzb, bzsszdbz, bzlists);
        }
    }
}