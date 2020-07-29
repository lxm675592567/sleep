package com.hife.util;

import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 睡眠校准
 * */
public class SleepCorrectUtil {

    public static JSONObject getCorrect(JSONObject json) {
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
            list.set(0, (qian - ts) / 1000);
            list.set(1, (hou - ts) / 1000);
            smList.add(list);
        }


        //获取最后时间,吧空白段删除
        List<List<Object>> smhxwlList = new ArrayList<>();
        List<List<Object>> smkbList = new ArrayList<>();//空白
        for (int i = smList.size() - 1; i >= 0; i--) {
            List<Object> list = smList.get(i);
            String biaoshi = (String) list.get(3);
            int zhi = (int) list.get(2);
            if (biaoshi.equals("G") || zhi == 100) {
                smList.remove(i);
                smkbList.add(list);
            } else {
                if (!biaoshi.equals("F")) {
                    break;
                }

            }
        }
        for (int i = smList.size() - 1; i >= 0; i--) {
            List<Object> list = smList.get(i);
            String biaoshi = (String) list.get(3);
            if (biaoshi.equals("F")) {
                smList.remove(i);
                smhxwlList.add(list);
            }
        }

        //特殊
        if (smList.size() <= 2) {
            /*
             * 形成最后结果
             * */
            JSONObject smlxNew = new JSONObject();
            List<List<Object>> smjg = getSmjg(size, ts, smList, smhxwlList, smkbList);

            smlxNew.put("xl", xl);
            smlxNew.put("qt", smjg);

            return smlxNew;
        }

        //前30分钟不能有快动,有则变为中睡 ,第一次深睡前不能有快动,有的话变中睡
        int kdsj = 1800;
        int kdtype = 0;
        for (int i = 0; i < smList.size(); i++) {
            List<Object> smzhi = smList.get(i);
            long smqian = (long) smzhi.get(0);
//            long smhou = (long) smzhi.get(1);
            String leixing = (String) smzhi.get(3);
            if (smqian < kdsj) {
                if (leixing.equals("C")) {//判断30分钟有无深睡,有的话则不需要深睡判断
                    kdtype = 1;
                }
                if (leixing.equals("B")) {
                    smList.get(i).set(3, "D");
                }
            }
            if (kdtype == 0 && smqian > kdsj) {//此时说明30分钟后有深睡
                if (leixing.equals("C")) {
                    break;
                }
                if (leixing.equals("B")) {
                    smList.get(i).set(3, "D");
                }
            }
        }

        //将时间划分120分钟一段,如果最后一段大于100分钟则也划分,小于则归上一段
        //90分钟第一个深睡开始 最后一个快动结束
        int kaishi = 0;
        int jieshu = 0;
        for (int i = 0; i < smList.size(); i++) {
            String type = (String) smList.get(i).get(3);
            if (type.equals("C")) {
                kaishi = i;
                break;
            }
            if (i == smList.size() - 1) {
                kaishi = 0;
            }
        }
        for (int i = smList.size() - 1; i >= 0; i--) {
            String type = (String) smList.get(i).get(3);
            if (type.equals("B")) {
                jieshu = i;
                break;
            }
            if (i == 0) {
                jieshu = smList.size() - 1;
            }
        }

        int sj = 5400; //90分钟5400
        List<List<List<Object>>> sleephf = new ArrayList<>();//划分

        long zuida = (long) smList.get(jieshu).get(1) - (long) smList.get(kaishi).get(0);
        int chushu = (int) (zuida / sj);
        if (jieshu < chushu) {
            chushu = jieshu + 1;
        }
        int yushu = (int) (zuida % sj);
        int ystype = 0;//余数type 等于1不找中数
        if (yushu > 2400) {//余数大于40分钟
            chushu++;
            if (yushu < 4200) {//余数大于40分钟小于70分钟则求中数时不找他
                ystype = 1;
            }
        }
        List<List<Long>> qujian = new ArrayList<>();
        for (int i = 0; i < chushu; i++) {
            List<Long> list = new ArrayList<>();
            long ls = (long) smList.get(kaishi).get(0);
            list.add((i * sj + ls));
            if (i == chushu - 1) {
                list.add((long) smList.get(jieshu).get(1));
            } else {
                list.add((i * sj + sj + ls));
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
                List<Object> smzhis = smList.get(j);
                if (smList.size() != jieshu + 1) {
                    smzhis = smList.get(j + 1);
                }
                long smqians = (long) smzhis.get(0);
                long smhous = (long) smzhis.get(1);
                if (smqian >= qian && smhou <= hou) {
                    list.add(smzhi);
                    if (smqians < qian && smhous > hou) {
                        break;
                    }
                } else if (smqian <= qian && smhou >= hou) {
                    list.add(smzhi);
                }
            }
            sleephf.add(list);
        }

        for (int i = 0; i < sleephf.size() - 1; i++) {
            List<List<Object>> lists = sleephf.get(i);
            if (lists.size() == 1) {
                List<Object> objects = lists.get(0);
                sleephf.get(i + 1).add(0, objects);
                sleephf.get(i + 1).stream().sorted((o1, o2) -> {
                    for (int j = 0; j < Math.min(o1.size(), o2.size()); j++) {
                        int c = Long.valueOf((Long) o1.get(0)).compareTo(Long.valueOf((Long) o2.get(0)));
                        if (c != 0) {
                            return c;
                        }
                    }
                    return Integer.compare(o1.size(), o2.size());
                }).collect(Collectors.toList());
                sleephf.remove(i);
            }

        }

        for (int i = 0; i < sleephf.size(); i++) {
            List<List<Object>> lists = sleephf.get(i);
            for (int j = 0; j < lists.size(); j++) {
                sleephf.get(i).get(j).add(j);
            }
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
        getksyd(sleephf, ystype);



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
        List<List<Object>> smjg = getSmjg(size, ts, smList, smhxwlList, smkbList);

        smlxNew.put("xl", xl);
        smlxNew.put("qt", smjg);

        return smlxNew;
    }

    private static List<List<Object>> getSmjg(long size, long ts, List<List<Object>> smList, List<List<Object>> smhxwlList, List<List<Object>> smkbList) {
        String typesize = (String) smList.get(smList.size() - 1).get(3);
        if (typesize.equals("B")) {
            smList.get(smList.size() - 1).set(3, "A");
        } else if (!typesize.equals("A")) {
            smList.get(smList.size() - 1).set(3, "E");
        }
        /*合并
         *一先获得清醒集合
         * 二清醒集合左右判断小于10分钟并且中间不能有深睡合并一数组中
         *
         * */
        List<List<Object>> arraySort = getKdLists(smList);


        //呼吸紊乱
        for (int i = 0; i < smhxwlList.size(); i++) {
            List<Object> objects = smhxwlList.get(i);
            arraySort.add(objects);
        }

        for (int i = smkbList.size() - 1; i >= 0; i--) {//最后空白不加
            List<Object> objects = smkbList.get(i);
            long hou = (long) objects.get(1);
            if (hou != size) {
                arraySort.add(objects);
            }
        }
        int sbz = 0;//深睡變中睡
        for (int i = 0; i < arraySort.size(); i++) {
            List<Object> list = arraySort.get(i);
            long qian = (long) list.get(0);
            long hou = (long) list.get(1);
            String type = (String) list.get(3);
            arraySort.get(i).set(0, ts + qian * 1000);
            arraySort.get(i).set(1, ts + hou * 1000);
            if (type.equals("F")) {
                arraySort.get(i).set(2, 30);
            } else {
                arraySort.get(i).set(2, 60);
            }

            //第一次深睡变中,中变浅
            if (sbz == 0) {
                if (type.equals("D")) {
                    arraySort.get(i).set(3, "E");
                }
                if (type.equals("C")) {
                    arraySort.get(i).set(3, "D");
                    if (i==1){
                        arraySort.get(i).set(3, "E");
                    }
                    sbz = 1;
                }
            }

        }
        return arraySort;
    }

    private static List<List<Object>> getKdLists(List<List<Object>> smList) {
        /*合并
         *一先获得清醒集合
         * 二清醒集合左右判断小于10分钟并且中间不能有深睡合并一数组中
         *
         * */
        List<List<Object>> kuaidongLs = new ArrayList<>();//一先获得清醒集合
        for (int i = 0; i < smList.size(); i++) {
            List<Object> list = smList.get(i);
            String type = (String) list.get(3);
            if (type.equals("B")) {
                kuaidongLs.add(smList.get(i));
            }
            if (list.size() > 4) { //处理没有用的坐标并添加新坐标
                smList.get(i).remove(4);
            }
            smList.get(i).add(i);
        }
        List<List<Integer>> kdjh = new ArrayList<>();//快动集合
        for (int i = 0; i < kuaidongLs.size() - 1; i++) {
            List<Object> listFront = kuaidongLs.get(i);
            long qianFront = (long) listFront.get(0);
            long houFront = (long) listFront.get(1);
            String typeq = (String) listFront.get(3);
            int zbq = (int) listFront.get(4); //原始坐标前
            List<Object> listBehind = kuaidongLs.get(i + 1);
            long qianBehind = (long) listBehind.get(0);
            long houBehind = (long) listBehind.get(1);
            String typeh = (String) listBehind.get(3);
            int zbh = (int) listBehind.get(4); //原始坐标后

            int cha = (int) (qianBehind - houFront);
            if (cha > 600) { //时间差大于10分钟取消
                continue;
            }
            int sstype = 0;//等于0通过
            for (int j = zbq + 1; j < zbh; j++) {//找到该区间内是否有深睡,如果遍历完没有,则添加2个快动到新集合中
                List<Object> lists = smList.get(j);
                String type = (String) lists.get(3);
                if (!type.equals("E")) {
                    sstype = 1;
                    continue;
                }
            }
            if (sstype == 0) {
                List<Integer> list = new ArrayList<>();
                if (kdjh.size() > 0) {
                    int kdjhsize = (int) kdjh.get(kdjh.size() - 1).get(kdjh.get(kdjh.size() - 1).size() - 1);
                    if (kdjhsize == zbq) {//前值=后值合并
//                        kdjh.get(kdjh.size() - 1).set(1,zbh);
                        kdjh.get(kdjh.size() - 1).add(zbh);
                        //list.add(zbh);
                        //kdjh.add(list);
                        continue;
                    }
                }
                list.add(zbq);
                list.add(zbh);
                kdjh.add(list);
            }
        }


        //优化,把中间有其他的去掉
        for (int i = kdjh.size() - 1; i >= 0; i--) {
            List<Integer> integers = kdjh.get(i);
            int bzs = 0;
            for (int j = 0; j < integers.size() - 1; j++) {
                int one = integers.get(j);
                int two = integers.get(j + 1);
                int cha = two - one;
                if (cha == 1) {
                    continue;
                } else {
                    bzs = 1;
                }
            }
            if (bzs == 0) {
                kdjh.remove(i);
            }
        }

        //目前得到合并的快动区间,找到中间区间
        for (int i = 0; i < kdjh.size(); i++) {
            List<Integer> integers = kdjh.get(i);
            int shou = integers.get(0);
            int wei = integers.get(integers.size() - 1);
            /*
             * 找到区间总时间,找到快动总时间 ,总时间相等不管,如果不相等,差值除以二,首时间+差值首时间
             * */
            int shousize = 0;
            int weisize = 0;
            for (int j = 0; j < smList.size(); j++) {
                int sz = (int) smList.get(j).get(4);
                if (sz == shou) {
                    shousize = j;
                }
                if (sz == wei) {
                    weisize = j;
                    break;
                }
            }
            long shousj = (long) smList.get(shousize).get(0); //首时间
            long weisj = (long) smList.get(weisize).get(1); //尾时间
            int zsj = (int) (weisj - shousj);//总时间

            int kdzsj = 0;//快动总时间
            for (int j = 0; j < integers.size(); j++) {
                int zb = integers.get(j);
                for (int k = 0; k < smList.size(); k++) {
                    int sz = (int) smList.get(k).get(4);
                    if (sz == zb) {
                        zb = k;
                        break;
                    }
                }
                long kai = (long) smList.get(zb).get(0);
                long shi = (long) smList.get(zb).get(1);
                int cha = (int) (shi - kai);
                kdzsj = kdzsj + cha;
            }

            int chazhi = zsj - kdzsj;//总时间-快动总时间
            int shoucha = 0;
            int weicha = 0;
            long kdshou = 0;//快动首
            long kdwei = 0;//快动尾
            if (chazhi > 0) {
                shoucha = chazhi / 2;
                weicha = chazhi - shoucha;
                kdshou = shousj + shoucha;
                kdwei = weisj - weicha;
            } else {
                break;
            }

            for (int j = wei; j >= shou; j--) {
                for (int k = 0; k < smList.size(); k++) {
                    int sz = (int) smList.get(k).get(4);
                    if (sz >= shou && sz <= wei) {
                        smList.remove(k);
                    }
                }
            }

            //顺序 浅睡 快动 浅睡
            List<Object> qsq = new ArrayList<>(); //浅睡前
            qsq.add(shousj);qsq.add(kdshou);qsq.add(60);qsq.add("E");qsq.add(-1);
            List<Object> kd = new ArrayList<>(); //快动
            kd.add(kdshou);kd.add(kdwei);kd.add(60);kd.add("B");kd.add(-1);
            List<Object> qsh = new ArrayList<>(); //浅睡后
            qsh.add(kdwei);qsh.add(weisj);qsh.add(60);qsh.add("E");qsh.add(-1);
            smList.add(qsq);
            smList.add(kd);
            smList.add(qsh);

        }

        List<List<Object>> arraySort = new ArrayList(smList);
        arraySort = arraySort.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Long.valueOf((Long) o1.get(0)).compareTo(Long.valueOf((Long) o2.get(0)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
        return arraySort;
    }

    private static void getQs(List<List<Integer>> tdList, List<List<Object>> smList) {
        List<List<Object>> smssList = new ArrayList<>();//深睡
        //1快动右边中睡变浅睡
        for (int i = 0; i < smList.size() - 1; i++) {
            String zuobiaoq = (String) smList.get(i).get(3);
            String zuobiaoh = (String) smList.get(i + 1).get(3);
            if (zuobiaoq.equals("B") && !zuobiaoh.equals("B")) {
                smList.get(i + 1).set(3, "E");
            }
        }
        //2中睡有动变浅睡
        for (int i = 0; i < smList.size(); i++) {
            String zuobiaoq = (String) smList.get(i).get(3);
            if (zuobiaoq.equals("D")) {
                long kai = (long) smList.get(i).get(0);
                long shi = (long) smList.get(i).get(1);
                int type = 0;
                for (int j = 0; j < tdList.size(); j++) {
                    int tdkai = (int) tdList.get(j).get(0);
                    int tdshi = (int) tdList.get(j).get(1);
                    if (tdkai >= kai && tdshi <= shi) {
                        type++;
                    }
                }
                int cha = (int) (shi - kai);
                int cishu = cha / 300; //时间除以5分钟
                if (type > cishu) { //改为浅睡
                    smList.get(i).set(3, "E");
                }
            }

            if (zuobiaoq.equals("C")) {
                if (smList.get(i).size() <= 4) {
                    smList.get(i).add(i);
                }
                smssList.add(smList.get(i));
            }
        }
        //3两个深睡之间不能有浅睡(除去清醒和快动)
        for (int i = 0; i < smssList.size() - 1; i++) {
            int qzuobiao = (int) smssList.get(i).get(4);
            int hzuobiao = (int) smssList.get(i + 1).get(4);
            int type = 0;
            for (int j = qzuobiao + 1; j < hzuobiao; j++) {
                String zuobiaoq = (String) smList.get(j).get(3);
                if (zuobiaoq.equals("A") || zuobiaoq.equals("B")) {
                    type = 1;
                }
            }
            if (type == 0) {
                for (int j = qzuobiao + 1; j < hzuobiao; j++) {
                    String zuobiaoq = (String) smList.get(j).get(3);
                    if (zuobiaoq.equals("E")) {
                        smList.get(j).set(3, "D");
                    }
                }
            }
        }
    }

    private static void getSs(List<List<List<Object>>> sleephf, int ystype) {
        int biaozhun = 20;//标准值

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
                long chazhi = hou - qian;
                zong = zong + chazhi;
                if (type.equals("C")) {//深睡总数
                    sszong = sszong + chazhi;
                }
                if (chazhi > czkdzd && (type.equals("A") || type.equals("B"))) {//清醒和快动
                    czkdzd = i;
                }
                if (chazhi > czsszd && (type.equals("C"))) {//深睡
                    czsszd = i;
                }
            }

            double floor = (double) sszong / (double) zong;
            kdbizhi = (int) (floor * 100);
            List<Object> linshis = new ArrayList<>();
            linshis.add(sszong);//深睡总值
            linshis.add(zong);//总长度值
            linshis.add(kdbizhi);//深睡和总长度比值
            linshis.add(czkdzd);//清醒快动最大坐标
            linshis.add(czsszd);//深睡最大坐标
            bzList.add(linshis);

            if (zdbz < kdbizhi) {
                zdbz = kdbizhi;
            }
            if (zxbz > kdbizhi) {
                zxbz = kdbizhi;
            }
        }
        int zhongzhi = (int) ((zdbz + zxbz) / 2);

        //找到标准段落  1判断中值是否大于15+5 大于找小值,小于找大值 遍历三个段落
        int bztype = 0;
        if (zhongzhi > biaozhun + 5) {
            bztype = 1;//等于1是大值,找小值
        } else if (zhongzhi < biaozhun - 5) {
            bztype = 2;//等于2是小值,找大的
        }

        int xiangjin = 100;//相近最小
        int bzzb = 0; //标准坐标
        int size = bzList.size();
        if (ystype == 1) {
            size--;
        }
        for (int i = 0; i < size; i++) {
            int bz = (int) bzList.get(i).get(2);
            int abs = Math.abs(zhongzhi - bz);
            if (bztype == 1) {
                if (bz < zhongzhi) {
                    if (xiangjin > abs) {
                        xiangjin = abs;
                        bzzb = i;
                    }
                }
            } else if (bztype == 2) {
                if (bz > zhongzhi) {
                    if (xiangjin > abs) {
                        xiangjin = abs;
                        bzzb = i;
                    }
                }
            } else {
                if (xiangjin > abs) {
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
            //bzlist.add(i);
            if (type.equals("B")) {
                kddandu.add(bzlist);
            }
            if (type.equals("C")) {
                ssdandu.add(bzlist);
            }
        }
        int dgxh = 0; //递归循环次数小于50
        if (bzbz > biaozhun + 5) {//删掉快动周围
            //标准值15  坐标  标准坐标 最大值快动坐标 标准list
            getScssss(biaozhun, bzList, bzzb, bzkdzdbz, bzlists,dgxh);
        } else if (bzbz < biaozhun - 5) {//增加深睡周围
            getZjkdss(biaozhun, bzList, bzzb, bzsszdbz, bzlists, ssdandu,dgxh);
        }

        //以上标准段落校准完成,用标准段落坐标bzzb,往前倒叙递减,往后递增
        int bzbzNew = (int) bzList.get(bzzb).get(2);//更新后的标准段落比值
        for (int i = bzzb - 1; i >= 0; i--) {
            //第一次必须小于bzbzNew,比他小不变,比他大减少
            List<List<Object>> lists = sleephf.get(i);
            int bzNew = (int) bzList.get(i).get(2);//比值
            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
            int sszdbzNew = (int) bzList.get(i).get(4);//标准深睡最大坐标
            List<List<Object>> ssdanduNew = new ArrayList<>();
            for (int j = 0; j < lists.size(); j++) {
                List<Object> bzlist = lists.get(j);
                String type = (String) bzlist.get(3);
                // bzlist.add(i);
                if (type.equals("C")) {
                    ssdanduNew.add(bzlist);
                }
            }
            if (bzNew < bzbzNew) {
                getZjkdss(bzbzNew + 5, bzList, i, sszdbzNew, lists, ssdanduNew,dgxh);
            }
            bzbzNew = (int) bzList.get(i).get(2);
        }
        bzbzNew = (int) bzList.get(bzzb).get(2);
        int kashizhi = bzzb + 1;
        if (bzzb == bzList.size() - 1) {
            kashizhi--;
        }
        for (int i = bzzb + 1; i < bzList.size(); i++) {
            //第一次必须大于bzbzNew,比他大不变,比他小增加
            List<List<Object>> lists = sleephf.get(i);

            List<List<Object>> ssdanduNew = new ArrayList<>();
            for (int j = 0; j < lists.size(); j++) {
                List<Object> bzlist = lists.get(j);
                String type = (String) bzlist.get(3);
                // bzlist.add(i);
                if (type.equals("C")) {
                    ssdanduNew.add(bzlist);
                }
            }


            int bzNew = (int) bzList.get(i).get(2);//比值
            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
            int sszdbzNew = (int) bzList.get(i).get(4);//标准深睡最大坐标
            if (bzNew > bzbzNew) {
                getScssss(bzbzNew - 5, bzList, i, kdzdbzNew, lists,dgxh);
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

    private static void getksyd(List<List<List<Object>>> sleephf, int ystype) {
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
                long chazhi = hou - qian;
                zong = zong + chazhi;
                if (type.equals("A") || type.equals("B")) {//清醒和快动总数
                    kdzong = kdzong + chazhi;
                }
                if (chazhi > czkdzd && (type.equals("A") || type.equals("B"))) {//清醒和快动
                    czkdzd = i;
                }
                if (chazhi > czsszd && (type.equals("C"))) {//深睡
                    czsszd = i;
                }
            }

            double floor = (double) kdzong / (double) zong;
            kdbizhi = (int) (floor * 100);
            List<Object> linshis = new ArrayList<>();
            linshis.add(kdzong);//快动总值
            linshis.add(zong);//总长度值
            linshis.add(kdbizhi);//快动和总长度比值
            linshis.add(czkdzd);//清醒快动最大坐标
            linshis.add(czsszd);//深睡最大坐标
            bzList.add(linshis);

            if (zdbz < kdbizhi) {
                zdbz = kdbizhi;
            }
            if (zxbz > kdbizhi) {
                zxbz = kdbizhi;
            }
        }
        int zhongzhi = (int) ((zdbz + zxbz) / 2);

        //找到标准段落  1判断中值是否大于25+5 大于找小值,小于找大值 遍历三个段落
        int bztype = 0;
        if (zhongzhi > biaozhun + 5) {
            bztype = 1;//等于1是大值,找小值
        } else if (zhongzhi < biaozhun - 5) {
            bztype = 2;//等于2是小值,找大的
        }

        int xiangjin = 100;
        int bzzb = 0; //标准坐标
        int size = bzList.size();
        if (ystype == 1) {
            size--;
        }
        for (int i = 0; i < size; i++) {
            int bz = (int) bzList.get(i).get(2);
            int abs = Math.abs(zhongzhi - bz);
            if (bztype == 1) {
                if (bz < zhongzhi) {
                    if (xiangjin > abs) {
                        xiangjin = abs;
                        bzzb = i;
                    }
                }
            } else if (bztype == 2) {
                if (bz > zhongzhi) {
                    if (xiangjin > abs) {
                        xiangjin = abs;
                        bzzb = i;
                    }
                }
            } else {
                if (xiangjin > abs) {
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
            //bzlist.add(i);
            if (type.equals("B")) {
//                bzlist.add(i);
                kddandu.add(bzlist);
            }
            if (type.equals("C")) {
//                bzlist.add(i);
                ssdandu.add(bzlist);
            }
        }
        int dgxh = 0; //递归循环次数不能多于50
        if (bzbz > biaozhun + 5) {//删掉深睡周围
            //标准值25  坐标  标准坐标 最大值深睡坐标 标准list
            getScss(biaozhun, bzList, bzzb, bzsszdbz, bzlists,dgxh);
        } else if (bzbz < biaozhun - 5) {//增加快动周围
            getZjkd(biaozhun, bzList, bzzb, bzkdzdbz, bzlists, kddandu,dgxh);
        }
        //以上标准段落校准完成,用标准段落坐标bzzb,往前倒叙递减,往后递增
        int bzbzNew = (int) bzList.get(bzzb).get(2);//更新后的标准段落比值
        for (int i = bzzb - 1; i >= 0; i--) {
            //第一次必须小于bzbzNew,比他小不变,比他大减少
            List<List<Object>> lists = sleephf.get(i);
            int bzNew = (int) bzList.get(i).get(2);//比值
            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
            int sszdbzNew = (int) bzList.get(i).get(4);//标准深睡最大坐标
            if (bzNew > bzbzNew) {
                getScss(bzbzNew - 5, bzList, i, sszdbzNew, lists,dgxh);
            }
            bzbzNew = (int) bzList.get(i).get(2);
        }
        bzbzNew = (int) bzList.get(bzzb).get(2);
        int kashizhi = bzzb + 1;
        if (bzzb == bzList.size() - 1) {
            kashizhi--;
        }
        for (int i = bzzb + 1; i < bzList.size(); i++) {
            //第一次必须大于bzbzNew,比他大不变,比他小增加
            List<List<Object>> lists = sleephf.get(i);

            List<List<Object>> kddanduNew = new ArrayList<>();
            for (int j = 0; j < lists.size(); j++) {
                List<Object> bzlist = lists.get(j);
                String type = (String) bzlist.get(3);
                // bzlist.add(j);
                if (type.equals("B")) {
                    kddanduNew.add(bzlist);
                }
            }


            int bzNew = (int) bzList.get(i).get(2);//比值
            int kdzdbzNew = (int) bzList.get(i).get(3);//标准快动最大坐标
            if (bzNew < bzbzNew) {
                getZjkd(bzbzNew + 5, bzList, i, kdzdbzNew, lists, kddanduNew,dgxh);
            }
//            if (bzNew>bzbzNew){
//                getZjkd(bzbzNew+5,bzList, i, kdzdbzNew, lists, kddanduNew);
//            }
            bzbzNew = (int) bzList.get(i).get(2);
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

    private static void getZjkdss(int biaozhun, List<List<Object>> bzList, int bzzb, int bzkdzdbz, List<List<Object>> bzlists, List<List<Object>> kddandu,int dgxh) {
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

        /*
         * 只有一条快动
         * */
        if (kddandu.size() == 1) {
            for (int i = bzkdzdbz + 1; i < bzlists.size(); i++) {//往右(时间大)找,找到第一个停止
                List<Object> bzlist = bzlists.get(i);
                long qian = (long) bzlist.get(0);
                long hou = (long) bzlist.get(1);
                long chazhi = hou - qian;
                String type = (String) bzlist.get(3);
                if (type.equals("D")) {//断是否是中睡D改为快动
                    bzlists.get(i).set(3, "B");
                    long kdz = (long) bzList.get(bzzb).get(0);//快动值
                    bzList.get(bzzb).set(0, kdz + chazhi);//更新快动值
                    break;
                }
            }
            for (int i = bzkdzdbz - 1; i >= 0; i--) {//往左(时间小)找
                List<Object> bzlist = bzlists.get(i);
                long qian = (long) bzlist.get(0);
                long hou = (long) bzlist.get(1);
                long chazhi = hou - qian;
                String type = (String) bzlist.get(3);
                if (type.equals("D")) {//判断是否是中睡D改为快动
                    bzlists.get(i).set(3, "B");
                    long kdz = (long) bzList.get(bzzb).get(0);//快动值
                    bzList.get(bzzb).set(0, kdz + chazhi);//更新快动值
                    break;
                }
            }
        }
        /*
         * 最近快动
         * */
        int kdzj = 7200;//快动最近
        int zjkdz = 0; //最近快动左
        int zjkdy = 0; //最近快动右
        for (int i = 0; i < kddandu.size() - 1; i++) {
            long qhou = (long) kddandu.get(i).get(1);//前后值
            long hqou = (long) kddandu.get(i + 1).get(0);//后前值
            int zuobiaoq = (int) kddandu.get(i).get(4);//坐标前
            int zuobiaoh = (int) kddandu.get(i + 1).get(4);//坐标后
            int cha = (int) (hqou - qhou);
            if (kdzj > cha && cha > 0) {
                kdzj = cha;
                zjkdz = zuobiaoq;
                zjkdy = zuobiaoh;
            }
        }
        for (int i = zjkdz + 1; i < zjkdy; i++) {
            bzlists.get(i).set(3, "C");
            List<Object> bzlist = bzlists.get(i);
            kddandu.add(bzlist);

            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou - qian;
            long kdz = (long) bzList.get(bzzb).get(0);//快动值
            bzList.get(bzzb).set(0, kdz + chazhi);//更新快动值
            break;
        }

        //修改完成后,对比是否比达到标准
        long bzqz = (long) bzList.get(bzzb).get(0);
        long bzhz = (long) bzList.get(bzzb).get(1);
        double floor = (double) bzqz / (double) bzhz;
        int bzbizhi = (int) (floor * 100);//标准比值
        bzList.get(bzzb).set(2, bzbizhi);

        if (bzbizhi < biaozhun - 5 && dgxh<50) {
            dgxh++;
            getZjkdss(biaozhun, bzList, bzzb, bzkdzdbz, bzlists, kddandu,dgxh);
        }
        dgxh = 0;
    }

    private static void getZjkd(int biaozhun, List<List<Object>> bzList, int bzzb, int bzkdzdbz, List<List<Object>> bzlists, List<List<Object>> kddandu,int dgxh) {
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
        /*
         * 只有一条快动
         * */
        if (kddandu.size() <= 1) {
            for (int i = bzkdzdbz + 1; i < bzlists.size(); i++) {//往右(时间大)找,找到第一个停止
                List<Object> bzlist = bzlists.get(i);
                long qian = (long) bzlist.get(0);
                long hou = (long) bzlist.get(1);
                long chazhi = hou - qian;
                String type = (String) bzlist.get(3);
                if (type.equals("D")) {//断是否是中睡D改为快动
                    bzlists.get(i).set(3, "B");
                    long kdz = (long) bzList.get(bzzb).get(0);//快动值
                    bzList.get(bzzb).set(0, kdz + chazhi);//更新快动值
                    break;
                }
            }
            for (int i = bzkdzdbz - 1; i >= 0; i--) {//往左(时间小)找
                List<Object> bzlist = bzlists.get(i);
                long qian = (long) bzlist.get(0);
                long hou = (long) bzlist.get(1);
                long chazhi = hou - qian;
                String type = (String) bzlist.get(3);
                if (type.equals("D")) {//判断是否是中睡D改为快动
                    bzlists.get(i).set(3, "B");
                    long kdz = (long) bzList.get(bzzb).get(0);//快动值
                    bzList.get(bzzb).set(0, kdz + chazhi);//更新快动值
                    break;
                }
            }
        }
        /*
         * 最近快动
         * */
        for (int i = 0; i < kddandu.size() - 1; i++) {
            long qhou = (long) kddandu.get(i).get(1);//前后值
            long hqou = (long) kddandu.get(i + 1).get(0);//后前值
            int zuobiaoq = (int) kddandu.get(i).get(4);//坐标前
            int zuobiaoh = (int) kddandu.get(i + 1).get(4);//坐标后
            int cha = (int) (hqou - qhou);
            if (kdzj > cha && cha > 0) {
                kdzj = cha;
                zjkdz = zuobiaoq;
                zjkdy = zuobiaoh;
            }
        }
        for (int i = zjkdz + 1; i < zjkdy; i++) {
            bzlists.get(i).set(3, "B");
            List<Object> bzlist = bzlists.get(i);
            kddandu.add(bzlist);

            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou - qian;
            long kdz = (long) bzList.get(bzzb).get(0);//快动值
            bzList.get(bzzb).set(0, kdz + chazhi);//更新快动值
            break;
        }

        //修改完成后,对比是否比达到标准
        long bzqz = (long) bzList.get(bzzb).get(0);
        long bzhz = (long) bzList.get(bzzb).get(1);
        double floor = (double) bzqz / (double) bzhz;
        int bzbizhi = (int) (floor * 100);//标准比值
        bzList.get(bzzb).set(2, bzbizhi);

        if (bzbizhi < biaozhun - 5 && dgxh<50) {
            dgxh++;
            getZjkd(biaozhun, bzList, bzzb, bzkdzdbz, bzlists, kddandu,dgxh);
        }
        dgxh=0;
    }

    private static void getScssss(int biaozhun, List<List<Object>> bzList, int bzzb, int bzsszdbz, List<List<Object>> bzlists,int dgxh) {
        for (int i = bzsszdbz + 1; i < bzlists.size(); i++) {//往右(时间大)找,找到第一个停止
            List<Object> bzlist = bzlists.get(i);
            long qian = (long) bzlist.get(0);
            long hou = (long) bzlist.get(1);
            long chazhi = hou - qian;
            String type = (String) bzlist.get(3);
            if (type.equals("C")) {//判断是否是深睡是的话改为中睡D
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
        int bzbizhi = (int) (floor * 100);//标准比值
        bzList.get(bzzb).set(2, bzbizhi);

        if (bzbizhi > biaozhun + 5 && dgxh<50) {
            dgxh++;
            getScssss(biaozhun, bzList, bzzb, bzsszdbz, bzlists,dgxh);
        }
        dgxh=0;
    }

    private static void getScss(int biaozhun, List<List<Object>> bzList, int bzzb, int bzsszdbz, List<List<Object>> bzlists,int dgxh) {
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
        int bzbizhi = (int) (floor * 100);//标准比值
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

        if (bzbizhi > biaozhun + 5 && dgxh<50) {
            dgxh++;
            getScss(biaozhun, bzList, bzzb, bzsszdbz, bzlists,dgxh);
        }
        dgxh = 0;
    }
}
