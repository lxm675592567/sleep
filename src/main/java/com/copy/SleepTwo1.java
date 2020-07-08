package com.copy;

import com.hife.EDFUtils.EDFParser;
import com.hife.EDFUtils.EDFRecord;
import com.hife.util.JsonUtil;
import com.hife.util.ModeUtil;
import org.apache.commons.lang.ArrayUtils;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class SleepTwo1 {

    public static void main(String[] args) throws ParseException, JSONException {
        String path = "D:\\项目\\sleep\\sleep\\src\\main\\java\\com\\hife\\1.dat";
        EDFParser edf = new EDFParser(path);
        HashMap<String, String> header = edf.header;
        List<EDFRecord> records = edf.records;
        String startTime = header.get("记录的开始时间*").replace(".", ":");//时分秒 14.50.52
        String startDate = header.get("记录的开始日期*").replace(".", ":");//日期 11.01.20
        String[] strArr = startDate.split("\\:");
        String time = "20" + strArr[2] + "-" + strArr[1] + "-" + strArr[0];
        int miao = records.size() * 2; //睡眠总秒数
        int minute = miao / 60 % 60; //睡眠时间转换成分钟
        String createTime = time + " " + startTime;//开始时间 startDate=2020-01-11 15:09:52
        String endTime = getEndTime(createTime, minute);//结束时间
        List<Integer> PrList = new ArrayList<>();
        List<Integer> RR = new ArrayList<>(); //呼吸时间
        //String jsonString = JsonUtil.readJsonFile("D:\\data.json");

        String jsonString = JsonUtil.readJsonFile("D:\\项目\\新数据\\25.json");
        net.sf.json.JSONObject dataMeaning = net.sf.json.JSONObject.fromObject(jsonString);

        net.sf.json.JSONArray jsonArray = dataMeaning.getJSONArray("data");
        List<Long> integerList = new ArrayList<>();


        for (EDFRecord record : records) {
            short[] hr = record.HR;
            for (int i : hr) {
                if (i<=200){
                    PrList.add(i);

                }else {
                    PrList.add(0);
                }
            }
            short[] rr = record.PI;
            for (int i : rr) {
                if (i<100){
                    RR.add(i);

                }else {
                    RR.add(0);
                }
            }
        }



        List<Integer> PrListCopy = new ArrayList<>(PrList);

        int lingkai = 0;
        int lingwei = 0;
        List<List<Integer>> qujianlist = new ArrayList<>();
        for (int i = 1; i < PrList.size(); i++) {
            List<Integer> list = new ArrayList<>();
            Integer integer1 = PrList.get(i-1);
            Integer integer = PrList.get(i);

            if (integer1 ==0){
                if (lingkai == 0 ){
                    lingkai = i;
                }
                if (integer != 0){
                    lingwei = i-1; //完成一次加入区间中
                    list.add(lingkai);
                    list.add(lingwei);
                    qujianlist.add(list);
                    lingkai = 0;
                }
            }

        }

//        for (int i = 1; i < PrList.size(); i++) {
//            Integer integer = PrList.get(i);
//            if (integer == 0){
//                PrList.set(i,PrList.get(i-1));  //给前值
//                PrListCopy.set(i,PrListCopy.get(i-1));
//
//            }
//        }


        int a1 = 3;int a2 = 1;int a3 = 1;
        int b1 = 3;int b2 = 4;int b3 = 5;

        int D =30; //几条
        int E =1; //下降上升几次 //参数E:第一次对比7个数一个方差 前值-后值<E
        //int F = 1; //呼吸波判定常量
        int R =5; //爬坡算法左右找4
        int n = 1;////最小二乘法(pingjun)对比参数小于n
        int s = 60; //时间60秒
        int hx = 1;//呼吸方差=0
        List<Integer> prTwo = new ArrayList<>(PrList);

        List<Integer> prThree = new ArrayList<>();
        List<Integer> prtwoThree = new ArrayList<>();
        List<Integer> prThrees = new ArrayList<>();

        //遍历取整
        if (a1 <=1){
            a1 = 2;
        }
        int yushu = PrList.size() % (a1 - 1) <= 1 ? (PrList.size() / (a1 - 1)) : (PrList.size() / (a1 - 1)) + 1;
        int rint = (int) rint(yushu);


        //时间数组
        List<Integer> timeOne = new ArrayList<>();
        List<Integer> timeTwo = new ArrayList<>();
        List<Integer> timeThree = new ArrayList<>();
        prThree.add(0, PrList.get(0));
        timeOne.add(0, 0);
        loop(PrList, a1, b1, prTwo, prThree, rint, timeOne);

        if (a2 <=1){
            a2 = 2;
        }
        int yushuTwo = prThree.size() % (a2 - 1) <= 1 ? (prThree.size() / (a2 - 1)) : (prThree.size() / (a2 - 1)) + 1;
        int rintTwo = (int) rint(yushuTwo);
        prtwoThree.add(0, prThree.get(0));
        timeTwo.add(0, 0);
        loop2(prThree, a2, b2, prTwo, prtwoThree, rintTwo, a1, timeOne, timeTwo);
        if (a3 <=1){
            a3 = 2;
        }
        int yushuThree = prtwoThree.size() % (a3 - 1) <= 1 ? (prtwoThree.size() / (a3 - 1)) : (prtwoThree.size() / (a3 - 1)) + 1;
        int rintThree = (int) rint(yushuThree);
        loop3(prtwoThree, a3, b3, prTwo, prThrees, rintThree, timeTwo, a2, timeThree);


        List<Integer> prThreesCopy = new ArrayList<>(prThrees);


        List<List<Integer>> slp = new ArrayList<>(); //存储数组

        for (int i = 0; i < prThreesCopy.size(); i++) {
            if(prThreesCopy.size()<=D*i+D){
                 break;
            }else {
                List<Integer> integers = prThreesCopy.subList(i*D, D * i + D);
                slp.add(integers);
            }
        }

        int[] arr = new int[slp.size()]; //方差数组
        int[] arrTime = new int[slp.size()];
        int a = 0;
        for (List<Integer> integers : slp) {
            int[] array = new int[integers.size()];
            for(int i = 0; i < integers.size();i++){
                array[i] = integers.get(i);
            }
            int i = Variance(array);//取出方差值
            arr[a] = i;
            //arrTime[a] = timeThree.get(a+D);
            a = a+1;
        }

        int xjshouzhi = 0;int xjshouzhics = 0;int xjcs =1;int xjweizhi = 0;
        int ssshouzhi = 0;int ssshouzhics = 0;int sscs = 1;int type = 0;

        //求得arr方差数组
        int chazhi = 0;
        List<List<Integer>> lists = new ArrayList<>();
        int kaishi = 0;
        int jieshu = 0;
        int cs = 0;

//        for (int i = 1; i < arr.length; i++) {
//            List<Integer> list = new ArrayList<>();
//            chazhi =Math.abs(arr[i] - arr[i-1]) ;
//            if (chazhi <= E){
//                // kaishi = i-1;
//                if (cs == 0){
//                    kaishi = i-1;
//                    cs = 1;
//                }else{
//                    cs ++;
//                }
//            }else { //判断前值是否小于2 若小于2
//                if(i>1){
//                    if (Math.abs(arr[i-1] - arr[i-2]) <= E){
//                        //设置尾值
//                        jieshu = cs + kaishi;
//                        list.add(kaishi);
//                        list.add(jieshu);
//                        lists.add(list);
//                        cs = 0;
//                    }
//                }
//            }
//        }
        //参数E:第一次对比7个数一个方差 前值-后值<E
        for (int i = 1; i < arr.length; i++) {
            List<Integer> list = new ArrayList<>();
            chazhi =Math.abs(arr[i] - arr[i-1]) ;
            if (chazhi <= E){
                // kaishi = i-1;
                if (cs == 0){
                    kaishi = i-1;
                    cs = 1;
                }else{
                    cs ++;
                }
            }else { //判断前值是否小于2 若小于2
                if(i>1){
                    if (Math.abs(arr[i-1] - arr[i-2]) <= E){
                        //设置尾值
                        jieshu = cs + kaishi+1;
                        list.add(kaishi);
                        list.add(jieshu);
                        lists.add(list);
                        cs = 0;
                    } else {
                        list.add(i-1);
                        list.add(i);
                        lists.add(list);
                    }
                }else {
                    list.add(i-1);
                    list.add(i);
                    lists.add(list);
                }

            }
        }
     //方差数组对比获得 区间坐标(差值的区间坐标,需转换成具体时间坐标)
        System.out.println(lists);

//        for (int i = 0; i < arr.length; i++) {
//            for (int j = 0; j < lists.size(); j++) {
//                if (){
//
//                }
//
//            }
//
//        }


        List<List<Integer>> list1 = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            List<Integer> list12 = new ArrayList<>();
            List<Integer> integers = lists.get(i);
            int sz = integers.get(0)*D;
            int wz = integers.get(1)*D;
            if (sz<= 0){
                sz =0;
            }
            Integer integer = timeThree.get(sz);
            Integer integer1 = timeThree.get(wz);
            list12.add(integer);
            list12.add(integer1);
            list1.add(list12);
        }
        //求得具体时间坐标 此时完成第一步获取7个方差 差值对比晒出来的坐标 接下来第二部寻找 对应呼吸频率
       // System.out.println(list1); //求得时间数组

        List<List<Integer>> huxiqujian = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            List<Integer> list22 = new ArrayList<>();
            List<Integer> integers = list1.get(i);
            Integer time1 = integers.get(0);
            Integer time2 = integers.get(1);
            for (int j = 0; j < RR.size(); j++) {
                if (j>=time1 && j<=time2){
                    list22.add(RR.get(j));
                }
            }
            huxiqujian.add(list22);
        }

     //   System.out.println(huxiqujian); //求得呼吸频率 区间数组

        int[] arrs = new int[huxiqujian.size()]; //呼吸方差数组
        for (int i = 0; i < huxiqujian.size(); i++) {
            List<Integer> integers = huxiqujian.get(i); //算出数组500多值

           // int a12 = 0;
            int[] array = new int[integers.size()];
            for(int k = 0; k < integers.size();k++){
                array[k] = integers.get(k);
            }
            int is = Variance(array);//取出方差值
            arrs[i] = is;
            //arrTime[a] = timeThree.get(a+D);
        }
       // System.out.println(arrs); //呼吸方差


        //求得呼吸方差后,遍历 对比==合并
        int hxkaishi =0 ;
        int hxjieshu =0 ;

        List<List<Integer>> hxlists = new ArrayList<>();
        int hxcs = 0;
        for (int i = 1; i < arrs.length; i++) {
            int hxcha = Math.abs(arrs[i] - arrs[i-1]);
            List<Integer> list = new ArrayList<>();

            boolean b = arrs[i] < 0 && arrs[i - 1] < 0;

            if (b){
                hxkaishi = i - 1;
                hxjieshu = i;
                list.add(hxkaishi);
                list.add(hxjieshu);
                hxlists.add(list);
                hxcs = 0;
                continue;
            }
            if (hxcha < hx){
                // hxkaishi = i-1;
                if (hxcs == 0 && i <= arrs.length-2  ) {
                    if (Math.abs(arrs[i+1] - arrs[i]) < hx ){
                        hxkaishi = i - 1;
                        //hxjieshu = i;
                        hxcs = 1;
                        // }else if ( Math.abs(arrs[i+1] - arrs[i]) != 0 ){
                    }else{
                        hxkaishi = i - 1;
                        hxjieshu = i;
                        list.add(hxkaishi);
                        list.add(hxjieshu);
                        hxlists.add(list);
                        hxcs = 0;
                    }
                }else if ( hxcs == 0 && i == arrs.length-1  ){
                    hxkaishi = i - 1;
                    hxjieshu = i;
                    list.add(hxkaishi);
                    list.add(hxjieshu);
                    hxlists.add(list);
                    hxcs = 0;
                }else{
                    hxcs ++;
                }
            }else { //判断前值是否相等
                if(i>1){
                    if (Math.abs(arrs[i-1] - arrs[i-2]) < hx && hxcs== 0 ){
                        //设置尾值
                        hxjieshu = hxcs + hxkaishi;
                        list.add(hxkaishi);
                        list.add(hxjieshu);
                        hxlists.add(list);
                        hxcs = 0;
                    }
                }
            }
        }
//        for (int i = 1; i < arrs.length; i++) {
//            int hxcha = Math.abs(arrs[i] - arrs[i-1]);
//            List<Integer> list = new ArrayList<>();
//
//            boolean b = arrs[i] < 5 && arrs[i - 1] < 5;
//
//            if (hxcha < hx){
//                // hxkaishi = i-1;
//                if (hxcs == 0 && i <= arrs.length-2 || b ) {
//                    if (Math.abs(arrs[i+1] - arrs[i]) < hx || b){
//                        hxkaishi = i - 1;
//                        //hxjieshu = i;
//                        hxcs = 1;
//                   // }else if ( Math.abs(arrs[i+1] - arrs[i]) != 0 ){
//                    }else{
//                        hxkaishi = i - 1;
//                        hxjieshu = i;
//                        list.add(hxkaishi);
//                        list.add(hxjieshu);
//                        hxlists.add(list);
//                        hxcs = 0;
//                    }
//                }else if ( hxcs == 0 && i == arrs.length-1 || b ){
//                    hxkaishi = i - 1;
//                    hxjieshu = i;
//                    list.add(hxkaishi);
//                    list.add(hxjieshu);
//                    hxlists.add(list);
//                    hxcs = 0;
//                }else{
//                    hxcs ++;
//                }
//            }else { //判断前值是否相等
//                if(i>1){
//                    if (Math.abs(arrs[i-1] - arrs[i-2]) < hx && hxcs== 0 || b){
//                        //设置尾值
//                        hxjieshu = hxcs + hxkaishi;
//                        list.add(hxkaishi);
//                        list.add(hxjieshu);
//                        hxlists.add(list);
//                        hxcs = 0;
//                    }
//                }
//            }
//        }

        System.out.println(hxlists); //获得呼吸方差分组

        List<List<Integer>> arrlist2 = new ArrayList<>(list1); //呼吸方差对应时间
        List<List<Integer>> arrlist3 = new ArrayList<>();

            for (int j = 0; j < arrlist2.size(); j++) {
                List<Integer> list = new ArrayList<>();
                List<Integer> integers = arrlist2.get(j);
                Integer integer = integers.get(0);
                if (integer<0){
                    integer = 0;
                }
                if (hxlists.size()<1){
                    list.add(integer);
                    list.add(integers.get(1));
                    arrlist3.add(list);
                }
                for (int i = 0; i < hxlists.size(); i++) {
                    List<Integer> shijian = hxlists.get(i);
                    Integer kaishisj = shijian.get(0);
                    Integer jieshusj = shijian.get(1);
                //if (i==0){
                    if (j==kaishisj){
                        list.add(integer);
                        list.add(arrlist2.get(jieshusj).get(1));
                        arrlist3.add(list);
                        break;
                    }else if (j>kaishisj && j<=jieshusj){
                        break;
                    }else if (i == hxlists.size()-1){
                        list.add(integer);
                        list.add(integers.get(1));
                        arrlist3.add(list);
                        break;
                    }

              //  }
            }
        }
        //呼吸频率形成的时间区间{{6,62},{}{}}
        System.out.println(arrlist3);

        //用形成的时间区间获取原心率数据 求得最小二乘法
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(createTime);
        List<List<Integer>> array  = new ArrayList();
        long x1 =0, x2 = 0,y1=0,y2=0;
        int arrzb =0;
        List<List<Long>> yuan = new ArrayList<>();
        List<List<Long>> yuans = new ArrayList<>();
        for (int i = 0; i < PrListCopy.size(); i++) {
            List<Long> objects = new ArrayList<>();
            long ts = date.getTime();
            if (i == 0){
                i = 1;
            }
            objects.add(ts+i*1000);
            objects.add(Long.valueOf(PrListCopy.get(i)));
            yuans.add(objects);
        }

        for (List<Integer> integers : arrlist3) {
            List<Integer> fanwei = new ArrayList();
            int zuo = integers.get(0);  //循环出的list 左边为初坐标 右边为结束坐标
            int you = integers.get(1);
            int zysize = you -zuo;

            long[] arrx = new long[zysize];
            long[] arry = new long[zysize];
            int kai = 0;

            List<Integer> arrylist = new ArrayList<>(PrList);
            List<Integer> arrylist1 = new ArrayList<>(PrList);
            for (int h = zuo; h < you; h++) {
                if (h<0){
                    h = 0;
                }

                ArrayList<Long> objects = new ArrayList<>();
                long ts = date.getTime();
                arrx[kai] = ts+h*1000;
                arrylist1.add(PrListCopy.get(h));
                //arrx[kai] = h;
                arry[kai] = PrListCopy.get(h);
                objects.add(arrx[kai]);
                objects.add(Long.valueOf(PrListCopy.get(h)));
                yuan.add(objects);
                kai++;
            }


            //第三部通过最小二乘法获得数组

            double y = median(arrylist1);
            Map<String, Object> stringObjectMap = lineRegression(arrx, arry); //最小二乘法
            x1 = arrx[0]; x2 = arrx[arrx.length-1];
            Long pa = (Long) stringObjectMap.get("a");Long pb = (Long) stringObjectMap.get("b");
            y1 = pa * x1 + pb; y2 = pa * x2 + pb;
            //形成数组 [[1591116380000, 49], [1591123995000, 49]]
            List<List<Long>> ercheng = new ArrayList<>();
            List<Long> objects1 = new ArrayList<>();
            objects1.add(x1);objects1.add(y1);
            List<Long> objects2 = new ArrayList<>();
            objects2.add(x2);objects2.add(y2);
            ercheng.add(objects1);ercheng.add(objects2);

            fanwei.add(zuo);//获得初坐标
            fanwei.add(you);
            fanwei.add((int) y);
            fanwei.add(arrzb);
            array.add(fanwei);
            arrzb++;
        }
        List<List<Long>> result = new ArrayList<>();
        bdresult(PrListCopy, date, array,result);
       // System.out.println("result+++"+result);

        //此时进入第四步 爬坡算法

        //首先array数组,将时间小于等于100秒 合并给后面
        int ty = 1;
        int shoutime = 0;
        int weitime = 0;
        int tytype = 0;
        int zuobiaozhi = 0;
        while (true){
            int time1 = array.get(ty-1).get(1) - array.get(ty-1).get(0);
            int time2 = array.get(ty).get(1) - array.get(ty).get(0);

            //一种n1 <=s n2<=s 同时小 找下一个 ,同时大则不动,一个小一个大
            if (time1<=s){
                if (time2>s){ //如果下一个大于前一个,则跟随
                if (tytype>0){
                    weitime = ty;
                    //遍历 array  找到shou 和 wei  先找到首,设置值然后删掉shou+1,wei值 剩下坐标调整
                    array.get(shoutime).set(1,array.get(weitime).get(1)); //设置上一个时间
                    array.get(shoutime).set(2,array.get(weitime).get(2)); //设置上一个值为下一个
                    for (int i = 0; i < array.size(); i++) {
                        if (i>shoutime && i<=weitime ){
                            array.remove(shoutime+1);
                            for (int j = 0; j < array.size(); j++) {
                                if (j>=shoutime+1){
                                    array.get(j).set(3,array.get(j).get(3)-1);
                                }
                            }
                        }
                    }
                    tytype = 0;
                }else {
                    array.get(ty-1).set(1,array.get(ty).get(1)); //设置上一个时间
                    array.get(ty-1).set(2,array.get(ty).get(2)); //设置上一个值为下一个
                    array.remove(ty);//删掉下一个遍历 size+1
                    for (int i = 0; i < array.size(); i++) {
                        if (i>=ty){
                            array.get(i).set(3,array.get(i).get(3)-1);
                        }
                    }
                }
                    zuobiaozhi++;
                    ty = zuobiaozhi;
                }else if (tytype==0){
                    //如果两个都小,添加变量tou
                     shoutime = ty-1;
                     tytype ++;
                }else {
                    tytype ++;
                }
            }
//            int times = array.get(ty).get(1) - array.get(ty-1).get(0);
//            if (times<=s){
//                array.get(ty-1).set(1,array.get(ty).get(1));
//                array.get(ty-1).set(2,array.get(ty).get(2));
//                array.remove(ty);
//                for (int i = 0; i < array.size(); i++) {
//                    if (i>=ty){
//                        array.get(i).set(3,array.get(i).get(3)-1);
//                    }
//                }
//            }
            if (ty>=array.size()-1){
                break;
            }
            ty++;
        }
       System.out.println(array);



        List<List<Integer>> arraySort  = new ArrayList(array); //排序数组
        List<List<Integer>> arraySortCopy  = new ArrayList(); //排序数组

        arraySort = arraySort.stream().sorted((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = Integer.valueOf(o1.get(2)).compareTo(Integer.valueOf(o2.get(2)));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }).collect(Collectors.toList());
       // System.out.println(arraySort);

        int xiao =0;
        while (true){ //*需修改
            List<Integer> integers = arraySort.get(xiao);
            Integer zuixiaozhi = integers.get(2); //最小值
            Integer zuixiaozhizb = integers.get(3); //最小值坐标

            //先往左边找4 找到后如果有合并则
            int zuotype = 0; //左边次数
            int xiajiangtypez = 0; //下降类型
            List<List<Integer>> zuolist  = new ArrayList();
            for (int i = zuixiaozhizb; i >0; i--) {
                Integer houzhi = array.get(i).get(2); //后值
                Integer qianzhi = array.get(i-1).get(2); //前值
                //Integer xiajiangzhiz = array.get(i-2).get(2);
                Integer xiajiangzhiz = array.get(i-1).get(2); //下降值
                if (i>1){
                     xiajiangzhiz = array.get(i-2).get(2); //前值
                }

                if (qianzhi-houzhi>=0){//代表上升
                    if (zuotype==0){
                        zuolist.add(array.get(i));
                    }
                    zuolist.add(array.get(i-1));

                    zuotype++;
//                }else if (xiajiangzhiz-houzhi>=0 && xiajiangtypez<2){
//                    zuolist.add(array.get(i-1));
//                    xiajiangtypez++;
//                    zuotype++;
                }else {
                    break;
                }
            }
            int biaozhun = 100;
            int hbzuobiaoz = 0; //合并左边坐标
            int hbzuobiaoy = 0; //右边坐标
//            if (zuotype>=6){
//                System.out.println(zuotype);
//            }
            for (int l = 0; l <= zuotype-4; l++) {
                if (zuotype > R) { //如果次数>4 合并完一次 找一次然后再重新找 直到<4
                    for (int i = 0; i < zuolist.size() - 1; i++) {//zuolist 坐标为 最小值 最小值坐标-1 用-1 -最小
                        int cha = Math.abs(zuolist.get(i + 1).get(2) - zuolist.get(i).get(2)); //相邻两数组想减求差值
                        if (cha < biaozhun) {
                            biaozhun = cha;
                            hbzuobiaoz = zuolist.get(i + 1).get(3);
                            hbzuobiaoy = hbzuobiaoz;
                        }
                    }
                    //最终找到合并左边坐标hbzuobiaoz
                    for (int i = 1; i < array.size() - 1; i++) {
                        List<Integer> zuo = array.get(i);
                        Integer zuobiao = zuo.get(3);
                        if (zuobiao == hbzuobiaoz) { //找到合并坐标左 取get(0) 找到右取get(1) 然后删除右边坐标
                            array.get(i-1).set(1, array.get(i).get(1));
                           // array.get(i).set(1, array.get(i + 1).get(1));//将左坐标第一坐标设置为右坐标的1
//                        if (array.get(i).get(2)>array.get(i-1).get(2)){ //两个坐标对比 大取大,小取小,相等取中间
//                            array.get(i).set(2,array.get(i-1).get(1));
//                        }
                            array.remove(i);
                            for (int j = 0; j < array.size(); j++) {
                                if (j >= i ) {
                                    array.get(j).set(3, array.get(j).get(3) - 1);
                                }
                            }
                            break;
                        }
                    }
                    arraySort = getSort(array);
                }
            }


            //往右边找4 找到后如果有合并则
            int youtype = 0; //右边次数
            int xiajiangtypey = 0; //下降类型
            List<List<Integer>> youlist  = new ArrayList();
            for (int i = zuixiaozhizb; i <array.size()-1; i++) {
                Integer houzhi = array.get(i+1).get(2); //后值
                Integer qianzhi = array.get(i).get(2); //前值
                //Integer xiajiangzhiy = array.get(i+2).get(2); //下降值
                Integer xiajiangzhiy = array.get(i+1).get(2);
                if (i < array.size()-2 ){
                     xiajiangzhiy = array.get(i+2).get(2); //下降值
                }

                if (houzhi-qianzhi>=0){ //用后值-前值 右减左
                    if (youtype==0){
                        youlist.add(array.get(i));
                    }
                        youlist.add(array.get(i+1));

                    youtype++;
                    //下降判断
//                }else if (xiajiangzhiy-qianzhi>=0 && xiajiangtypey<2){
//                    if (i ==0){
//                        youlist.add(array.get(0));
//                    }else {
//                        youlist.add(array.get(i-1));
//                    }
//
//                    xiajiangtypey++;
//                    youtype++;
                }else {
                    break;
                }
            }
            int biaozhuny = 100;
            int hbzuobiaozy = 0; //合并左边坐标
            int hbzuobiaoyy = 0; //右边坐标
            for (int l = 0; l <= youtype-4; l++) {
            if (youtype>R){ //如果次数>4 合并完一次 找一次然后再重新找 直到<4
                for (int i = 0; i < youlist.size()-1; i++) {
                    int cha = Math.abs(youlist.get(i+1).get(2) - youlist.get(i).get(2)); //相邻两数组想减求差值 右减左
                    if (cha<biaozhuny){ //求最小值
                        biaozhuny = cha;
                        hbzuobiaozy = youlist.get(i).get(3);
                        hbzuobiaoyy = hbzuobiaozy +1;
                    }
                }
                //最终找到合并右边坐标hbzuobiaozy
                for (int i = 1; i < array.size()-1; i++) {
                    List<Integer> you = array.get(i);
                    Integer zuobiao = you.get(3);
                    if (zuobiao==hbzuobiaozy){ //找到合并坐标左 取get(0) 找到右取get(1) 然后删除右边坐标
                        array.get(i).set(1,array.get(i+1).get(1));//将左坐标第一坐标设置为右坐标的1
//                        if (array.get(i).get(2)>array.get(i-1).get(2)){ //两个坐标对比 大取大,小取小,相等取中间
//                            array.get(i).set(2,array.get(i-1).get(1));
//                        }
                        array.remove(i+1);
                        for (int j = 0; j < array.size(); j++) {
                            if (j>=i+1){
                                array.get(j).set(3,array.get(j).get(3)-1);
                            }
                        }
                        break;
                    }
                }
                arraySort = getSort(array);
             }
            }
//            System.out.println("zuo"+zuotype);
//            System.out.println("you"+youtype);
            if (xiao>=array.size()-1){
                break;
            }
            xiao++;
        }
        //爬坡算法完成,形成 区间 数值 数组(爬坡算法,将最小二乘法获得的数,找到区间,大于四减去)
        //System.out.println("");
        List<List<Long>> arrays  = new ArrayList();
        List<List<Integer>> arrays1  = new ArrayList();
        List<List<Long>> arraystime  = new ArrayList();
            for (int j = 0; j < array.size(); j++) {
                List<Long> list = new ArrayList<>();
                List<Integer> list2 = new ArrayList<>();
                List<Long> listtime = new ArrayList<>();
                long ts = date.getTime();
               for (int i = 0; i < PrListCopy.size(); i++) {
                Integer kai = array.get(j).get(0);
                Integer shi = array.get(j).get(1);
                if (i>=kai && i<shi){
                    list.add(Long.valueOf(PrListCopy.get(i)));
                    list2.add(PrListCopy.get(i));
                    listtime.add(Long.valueOf(ts + i * 1000));
                }
            }
            arrays.add(list);
            arrays1.add(list2);
           arraystime.add(listtime);
        }
        //arraystime获得对应的秒数,{{12131331000,3131313000.....}}目的求得最小二乘法
       // System.out.println(arraystime);

//        long[] arrx1 = new long[];
//        long[] arry1 = new long[zysize];
//        int kai = 0;
//
//
//        int secondTimestamp = getSecondTimestamp(date);
//        //算出原数组值来拼成list
//        List<List<Long>> yuan = new ArrayList<>();
//        for (int h = zuo; h < you; h++) {
//            ArrayList<Long> objects = new ArrayList<>();
//            long ts = date.getTime();
//            arrx[kai] = ts+h*1000;
//            //arrx[kai] = h;
//            arry[kai] = prListCopy.get(h);
//            objects.add(arrx[kai]);
//            objects.add(Long.valueOf(prListCopy.get(h)));
//            yuan.add(objects);
//            kai++;
//        }
        long[] arrx1 = new long[arrays.size()];
        long[] arry1 = new long[arrays.size()];



        List<Integer> averageList = new ArrayList();
        for (int i = 0; i < arrays.size(); i++) {
            List<Long> integers = arrays.get(i);
            List<Integer> arrylist = arrays1.get(i);
            long [] intArrCha = integers.stream().mapToLong(t->t.longValue()).toArray();
            List<Long> longs = arraystime.get(i);
            long[] x = longs.stream().mapToLong(t->t.longValue()).toArray();

            double y = median(arrylist);
//            Map<String, Object> stringObjectMap = lineRegression(x, intArrCha);
//            x1 = x[0]; x2 = x[x.length-1];
//            Long pa = (Long) stringObjectMap.get("a");Long pb = (Long) stringObjectMap.get("b");
//            y1 = pa * x1 + pb; y2 = pa * x2 + pb;
//            System.out.println("");
//            Integer average = average(intArrCha, intArrCha.length);
            averageList.add((int) y);
        }
        //System.out.println(averageList);

        int[] pingjunshu = averageList.stream().mapToInt(Integer::valueOf).toArray();
//        Integer pingjun = average(pingjunshu, pingjunshu.length);
//        PrListCopy.stream().mapToInt(Integer::valueOf).toArray();
//        Integer pingjuns = majorityElement(pingjunshu);


        List<Integer> Prlistmiao = new ArrayList<>(PrList);
        int zuixiao = 200;
        for (int i = 0; i <= 180; i++) {
            Integer integer = PrListCopy.get(i);
            Prlistmiao.add(integer);
            if (integer<zuixiao){
                zuixiao = integer;
            }
        }
        int[] Prlistmiaosz = averageList.stream().mapToInt(Integer::valueOf).toArray();
        int[] Prlistmiao240 = Prlistmiao.stream().mapToInt(Integer::valueOf).toArray();
        int[] ints = PrListCopy.stream().mapToInt(Integer::valueOf).toArray();
        //众数
        ModeUtil m=new ModeUtil();
        int c;
        m.number=new int[ints.length];
        m.Mode(ints,0,ints.length);
        System.out.println("当前有"+(m.t+1)+"个众数");
        for(c=m.t;c>=0;c--){
            System.out.println(m.number[c]+" "+m.sum);
        }
        int zhongshu = m.number[0];
        int d;
        ModeUtil m1=new ModeUtil();
        m1.number=new int[Prlistmiao240.length];
        m1.Mode(Prlistmiao240,0,Prlistmiao240.length);
        int zhongshu4 = m1.number[0];

        //double pingjun = median(Prlistmiao);
        double pingjun = median(PrListCopy);
        //double pingjun = zuixiao;

        List<List<Integer>> resultList = new ArrayList<>();
        //获取最小二乘法后再次对比<n求得最后区间数组
        //最小二乘法(pingjun)对比参数小于n
        int kaishipj =0;
        int jieshupj =0;
        int css =0;
        for (int i = 1; i < pingjunshu.length; i++) {
            List<Integer> list = new ArrayList<>();
            chazhi =Math.abs(pingjunshu[i] - pingjunshu[i-1]) ;
            if (chazhi <= n){
                // kaishi = i-1;
                if (css == 0){
                    kaishipj = i-1;
                    css = 1;
                }else{
                    css ++;
                }
            }else { //判断前值是否小于2 若小于2
                if(i>1){
                    if (Math.abs(pingjunshu[i-1] - pingjunshu[i-2]) <= n){
                        //设置尾值
                        jieshupj = css + kaishipj+1;
                        list.add(kaishipj);
                        list.add(jieshupj);
                        resultList.add(list);
                        css = 0;
                        continue;
                    } else {
                        list.add(i-1);
                        list.add(i-1);
                        resultList.add(list);
                        css = 0;
                        continue;
                    }
                }else {
                    list.add(i-1);
                    list.add(i-1);
                    css = 0;
                    resultList.add(list);
                }
            }
            if (i==pingjunshu.length-1){
                list.add(kaishipj);
                list.add(pingjunshu.length-1);
                css = 0;
                resultList.add(list);
            }
        }
        //取得区间(最小二乘区间 需在找到真时间)
      //  System.out.println(resultList);

        List<List<Integer>> listTime = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            List<Integer> ss = new ArrayList<>();
            List<Integer> integers = resultList.get(i);
            Integer one = integers.get(0);
            Integer two = integers.get(1);
            List<Integer> arrsl = array.get(one);
            Integer integer = arrsl.get(1);;
            if (i==0){
                 integer = arrsl.get(0);
            }
            List<Integer> arrsw = array.get(two);
            Integer integer2 = arrsw.get(1);
            ss.add(integer);
            ss.add(integer2);
            listTime.add(ss);
//            List<Integer> list12 = new ArrayList<>();
//            List<Integer> integers = resultList.get(i);
//            int sz = integers.get(0)*D;
//            int wz = integers.get(1)*D;
//            if (sz<= 0){
//                sz =0;
//            }
//            Integer integer = timeThree.get(sz);
//            Integer integer1 = timeThree.get(wz);
//            list12.add(integer);
//            list12.add(integer1);
//            listTime.add(list12);
        }

      //  System.out.println(listTime);

        //遍历resultList 将平均数赋值 将对应区间的平均数合并,目的求统一值
        List<Integer> rsList = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {

            List<Integer> rs = resultList.get(i);
            Integer integer1 = rs.get(0);
            Integer integer2 = rs.get(1);
            int zhong = 0;
            int kshij= 0;
            int jshij =0;
            for (int j = 1; j < averageList.size(); j++) {
                Integer shi = averageList.get(j);
                Integer kai = zhong;
                Integer p = averageList.get(j);
                if (j-1==integer1){
                     kai = averageList.get(j-1);
                }
                if (j-1>=integer1 && j-1<=integer2){
                    if (kai>=pingjun && shi>=pingjun){
                        zhong = kai > shi ? kai : shi;
                    }else if (kai<=pingjun && shi<=pingjun){
                        zhong = kai < shi ? kai : shi;
                    }else{
                       // zhong = (kai+shi)/2;
                        p= (kai+shi)/2;
                        zhong = p;
                        if (p>pingjun){
                            zhong = kai > shi ? kai : shi;
                        }else if (p<pingjun){
                            zhong = kai  < shi ? kai : shi;
                        }else {
                            zhong = kai;
                        }

                    }
                }else if (j-1>integer2){
                    break;
                }
            }
            rsList.add(zhong);

        }

        System.out.println(rsList);

        for (int i = 0; i < listTime.size(); i++) {
            List<Integer> listTimes = new ArrayList<>();
            listTime.get(i).add(rsList.get(i));
            if (i==listTime.size()-1){
                listTimes.add(listTime.get(i).get(0));
                listTimes.add(PrListCopy.size()-1);
                listTimes.add(listTime.get(i).get(2));
                listTime.add(listTimes);
                break;
            }
        }

       // System.out.println(listTime);
        //将最后求得的区间
        List<List<Long>> results = new ArrayList<>();
        //PrListCopy
        List<Integer> PrListCopy1 = new ArrayList<>(PrList);
        List<List<Object>> results2 = new ArrayList<>();
        for (int i = 0; i < PrListCopy.size(); i++) {
            ArrayList<Long> objects = new ArrayList<>();
            ArrayList<Object> objects2 = new ArrayList<>();
            int jun = PrListCopy.get(i);
            for (int j = 0; j < listTime.size(); j++) {
                List<Integer> integers = listTime.get(j);
                Integer kaishijian = integers.get(0)-1;
                Integer shishijian = integers.get(1);
                Integer junzhi = integers.get(2);
                if (i>=kaishijian && i<=shishijian){
                    jun = junzhi;
                }
            }
            long ts = date.getTime();
            objects.add(ts + i * 1000);
//            if (jun>pingjun){
//                jun = jun+10;
//            }else if (jun<pingjun){
//                jun = jun-10;
//            }
            objects.add(Long.valueOf(jun));
            PrListCopy1.add(jun);

            for (int j = 0; j < qujianlist.size(); j++) {
                List<Integer> integers = qujianlist.get(j);
                Integer ks = integers.get(0);
                Integer js = integers.get(1);
                if (i>=ks && i<=js){
                    objects2.add(ts + i * 1000);
                    objects2.add(null);
                    break;
                }
                if (j == qujianlist.size()-1){
                    objects2.add(ts + i * 1000);
                    objects2.add(Long.valueOf(jun));
                }
            }
            results2.add(objects2);

            results.add(objects);
        }
     //   System.out.println(PrListCopy1);
        System.out.println("全部众数="+zhongshu+"+3分钟众数="+zhongshu4);
        //System.out.println("results2+++"+results2);
        System.out.println("yuans+++"+yuans);
        //System.out.println("yuan+++"+yuan);
        //System.out.println("results+++"+results);
//        List<List<Integer>> list = new ArrayList<>();
//        //遍历获取list 睡眠下降上升范围
//        deepsleep(timeThree, E, arr, xjshouzhics, xjcs, sscs, type, list,D);
//
//
//
//        //获取呼吸波左右取值范围
//        rrsleep(RR, PrListCopy, list, date, array, F );
//        //rrsleeps(RR, PrListCopy, list, date, array, F );
//        //形成波动曲线
//        List<List<Long>> result = new ArrayList<>();
//        bdresult(PrListCopy, date, array,result);
//
//        System.out.println("list+++"+list);
//        System.out.println("result+++"+result);
    }

    //中数
    private static double median(List<Integer> total) {
        double j = 0;
        //集合排序
        Collections.sort(total);
        int size = total.size();
        if(size % 2 == 1){
            j = total.get((size-1)/2);
        }else {
            //加0.0是为了把int转成double类型，否则除以2会算错
            j = (total.get(size/2-1) + total.get(size/2) + 0.0)/2;
        }
        return j;
    }

    //众数
    public static int majorityElement(int[] nums) {
        int zhongshu=nums[0];
        int count=1;
        for(int i=1;i<nums.length;i++){
            if(nums[i]==zhongshu){
                count++;
            }else{
                count--;
            }if(count==0){
                count=1;
                zhongshu=nums[i];
            }
        }
        return zhongshu;
    }





    //平均值
    public static int average(int[] array,int size) {
        double sum = 0;
        for(int i = 0; i < size; i++) {
            sum += array[i];
        }
        return (int) (sum / size);
    }

    private static List<List<Integer>> getSort(List<List<Integer>> array) {
        List<List<Integer>> arraySort;//坐标合并后,重新取值
        arraySort  = new ArrayList(array);
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

    private static void bdresult(List<Integer> prListCopy, Date date, List<List<Integer>> array,List<List<Long>> result) {
        List<Integer> PrListCopyTwo = new ArrayList<>(prListCopy);
        for (int i = 0; i < array.size(); i++) {
            List<Integer> integers = array.get(i);
            Integer chu = integers.get(0);
            Integer zhong = integers.get(1);
            Integer y = integers.get(2);
            for (int h = 0; h < PrListCopyTwo.size(); h++) {
                long ts = date.getTime();
                if (h>=chu && h<= zhong){
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





    public static Map<String, Object> lineRegression(long[] X, long[] Y)
    {
        if(null == X || null == Y || 0 == X.length
                || 0 == Y.length || X.length != Y.length)
        {
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
     * @param X
     * @return
     */
    private static long varianceSum(long[] X)
    {
        long xAvg = arraySum(X) / X.length;
        return arraySqSum(arrayMinus(X, xAvg));
    }

    /**
     * 计算协方差和
     * @param X
     * @param Y
     * @return
     */
    private static long covarianceSum(long[] X, long[] Y)
    {
        long xAvg = arraySum(X) / X.length;
        long yAvg = arraySum(Y) / Y.length;
        return arrayMulSum(arrayMinus(X, xAvg), arrayMinus(Y, yAvg));
    }

    /**
     * 数组减常数
     * @param X
     * @param x
     * @return
     */
    private static long[] arrayMinus(long[] X, long x)
    {
        int n = X.length;
        long[] result = new long[n];
        for(int i = 0; i < n; i++)
        {
            result[i] = X[i] - x;
        }

        return result;
    }

    /**
     * 数组求和
     * @param X
     * @return
     */
    private static long arraySum(long[] X)
    {
        long s = 0 ;
        for( long x : X )
        {
            s = s + x ;
        }
        return s ;
    }

    /**
     * 数组平方求和
     * @param X
     * @return
     */
    private static long arraySqSum(long[] X)
    {
        long s = 0 ;
        for( long x : X )
        {
            s = (long) (s + pow(x, 2)); ;
        }
        return s ;
    }

    /**
     * 数组对应元素相乘求和
     * @param X
     * @return
     */
    private static long arrayMulSum(long[] X, long[] Y)
    {
        long s = 0 ;
        for( int i = 0 ; i < X.length ; i++ )
        {
            s = s + X[i] * Y[i] ;
        }
        return s ;
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

    private static void loop3(List<Integer> prtwoThree, int a3, int b3, List<Integer> prTwo, List<Integer> prThrees, int rintThree, List<Integer> timeTwo, int a2,List<Integer> timeThree) {
        int time = 0;
        int miao = 0;
        for (int i = 0; i < rintThree; i++) {
            //从1中取值一直是从0开始
          //  System.out.println("i==================="+i);
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
        int time = date.getHours() * 60 + date.getMinutes() + minute;

        int hours = (int) floor(time / 60);
        int minutes = time % 60;
        date.setHours(hours);
        date.setMinutes(minutes);
        String endTime = format.format(date);
       // System.out.println("dateString=" + endTime);
        return endTime;
    }

}
