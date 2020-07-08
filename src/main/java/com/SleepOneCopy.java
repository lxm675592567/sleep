package com;

import com.hife.EDFUtils.EDFParser;
import com.hife.EDFUtils.EDFRecord;
import com.hife.util.JsonUtil;
import org.apache.commons.lang.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.*;

public class SleepOneCopy {

    public static void main(String[] args) throws ParseException, JSONException {
        String path = "D:\\项目\\sleep\\sleep\\src\\main\\java\\com\\hife\\刘6.3.dat";
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
//        for (int f = 0, size = jsonArray.size(); f < size; f++) {
//            net.sf.json.JSONObject jsonObject = jsonArray.getJSONObject(f);
//            PrList.add(jsonObject.getInt("heart_rate"));
//            integerList.add(jsonObject.getLong("timestamp") * 1000);
//        }

// List<Integer> PrList = Stream.of(100, 60, 80, 50, 0, 50, 45, 35, 25, 15, 8, 4, 80, 20, 0, 50, 24, 21, 25, 15, 100, 60, 80, 50, 0, 50, 45, 35, 25, 15, 8, 4, 80, 20, 0, 50, 24, 21, 25, 15).collect(Collectors.toList());
//        int array[]= new int[10];0

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

        System.out.println("RR++++"+RR);

        List<Integer> PrListCopy = new ArrayList<>(PrList);
       // System.out.println("PrList++" + PrList);
        // PrList.add(10); PrList.add(60);PrList.add(30);PrList.add(81);PrList.add(81);PrList.add(50);PrList.add(40);
        //PrList.add(3); PrList.add(1);PrList.add(2);PrList.add(5);PrList.add(4);PrList.add(6);PrList.add(7);
        //PrList.add(1); PrList.add(2);PrList.add(3);PrList.add(4);PrList.add(5);PrList.add(6);PrList.add(7);PrList.add(8);PrList.add(9);PrList.add(10);PrList.add(11);PrList.add(12);PrList.add(13);
        //PrList.add(13);PrList.add(12);PrList.add(11);PrList.add(10);PrList.add(9);;PrList.add(8);PrList.add(7);PrList.add(6);PrList.add(5);PrList.add(4);PrList.add(3);PrList.add(2); PrList.add(1);
        int a1 = 3;
        int a2 = 3;
        int a3 = 3;
        int b1 = 5;
        int b2 = 4;
        int b3 = 5;
        //数组1   132159123458423   1159123458423   59123458423
        //数组2   111999111444222
        //数组3   1(前3秒)

//        int yushu = PrList.size() - PrList.size() % a1;
//        PrList = PrList.subList(0, yushu);
        List<Integer> prTwo = new ArrayList<>(PrList);
        //prTwo.addAll(PrList);
        //List<Integer> prTwo = PrList;
        List<Integer> prThree = new ArrayList<>();
        List<Integer> prtwoThree = new ArrayList<>();
        List<Integer> prThrees = new ArrayList<>();
        //遍历取整
        int yushu = PrList.size() % (a1 - 1) <= 1 ? (PrList.size() / (a1 - 1)) : (PrList.size() / (a1 - 1)) + 1;
        int rint = (int) rint(yushu);
//        else if(reduce(integers)){
//            type = "3";
//        }
        //a1次循环

        Iterator<Integer> iterator = PrList.iterator();

        //时间数组
        List<Integer> timeOne = new ArrayList<>();
        List<Integer> timeTwo = new ArrayList<>();
        List<Integer> timeThree = new ArrayList<>();
        prThree.add(0, PrList.get(0));
        timeOne.add(0, 0);
        loop(PrList, a1, b1, prTwo, prThree, rint, timeOne);

        int yushuTwo = prThree.size() % (a2 - 1) <= 1 ? (prThree.size() / (a2 - 1)) : (prThree.size() / (a2 - 1)) + 1;
        int rintTwo = (int) rint(yushuTwo);
        prtwoThree.add(0, prThree.get(0));
        timeTwo.add(0, 0);
        loop2(prThree, a2, b2, prTwo, prtwoThree, rintTwo, a1, timeOne, timeTwo);
//        int rintThree = (int) Math.rint(prThree.size()/a3);
        int yushuThree = prtwoThree.size() % (a3 - 1) <= 1 ? (prtwoThree.size() / (a3 - 1)) : (prtwoThree.size() / (a3 - 1)) + 1;
        int rintThree = (int) rint(yushuThree);
        loop3(prtwoThree, a3, b3, prTwo, prThrees, rintThree, timeTwo, a2, timeThree);
//        getRemainder(a3, prTwo, prThrees);
        //list分割

        List<Integer> prThreesCopy = new ArrayList<>(prThrees);

        JSONObject jsonObject = new JSONObject();



        List<List<Integer>> slp = new ArrayList<>(); //存储数组

        for (int i = 0; i < prThreesCopy.size(); i++) {
            ArrayList<Integer> tes = new ArrayList<>();
            if(prThreesCopy.size()<=15*i+15){
                 break;
            }else {
                List<Integer> integers = prThreesCopy.subList(0, 15 * i + 15);
                slp.add(integers);
            }
        }
        //System.out.println(slp);
        int zhi = 0;
        int D =3; //几条
        int E =3; //几次

        int[] arr = new int[slp.size()]; //方差数组
        int[] arrTime = new int[slp.size()];
        int a = 0;
        for (List<Integer> integers : slp) {

            int[] array = new int[integers.size()];
            for(int i = 0; i < integers.size();i++){
                array[i] = integers.get(i);
            }
            //zhi = zhi+15;
            int i = Variance(array);//取出方差值
            arr[a] = i;
            arrTime[a] = timeThree.get(a+7);
//            System.out.println("标记++++"+zhi);
//            System.out.println("方差++++"+arr[a]);
            a = a+1;
        }

        int xjshouzhi = 0;
        int xjshouzhics = 0;
        int xjcs =1;
        int xjweizhi = 0;

        int ssshouzhi = 0;
        int ssshouzhics = 0;
        int sscs = 1;

        int type = 0;
        //int arr[] = {6, 5, 4, 3, 4 , 5, 6, 7};

        List<List<Integer>> list = new ArrayList<>();

        for (int i = 1; i < arr.length; i++) {
            List<Integer> integers = new ArrayList<>();
            //判断是否为下降 前值减后值>0
            if (i == 14) {
                System.out.println("");
            }
            if(arr[i-1]-arr[i]>0 ) {
                if (i > 1 && arr[i - 2] - arr[i - 1] > 0 && type !=0) {
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
            if(xjcs>=E) { //下降必须大于3
                if(arr[i-1]-arr[i]>0 && sscs== 1){
                    xjcs = xjcs+1; //如果为下降则+1 如2 3 4 5  循环完清零
                    continue;
                }
                if (arr[i-1]-arr[i]<0){
                    if (arr[i-2]-arr[i-1]<0){
                        sscs = sscs+1;
                    }else if (sscs == 1){
                        sscs = sscs+1;
                    }

                    if (i == arr.length-1){ //到结尾判断
                        ssshouzhi =  arr[i ]; //此时求得下标值
                        ssshouzhics = i ;
                    }
                }else {
                    if(sscs>=E){  //此时说明先下降三次后上升三次完成
                        ssshouzhi =  arr[i - 1]; //此时求得下标值
                        ssshouzhics = i - 1;
                        type = 0;
                        sscs = 1;
                        xjcs =1;
                        int sz = xjshouzhics*7-1;
                        int wz = ssshouzhics*7-1;
                        if (sz<0){
                            sz = 0;
                        }
                        integers.add(timeThree.get(sz));
                        integers.add(timeThree.get(wz));
                        list.add(integers);
                    }
//                        xjshouzhi = arr[i];
//                        xjshouzhics = i;
//                        sscs = 1;
//                        xjcs =1;
                }
            }
        }
//        int sz = xjshouzhics*7-1;
//        int wz = ssshouzhics*7-1;
//        if (ssshouzhics > 0){
//            System.out.println("初始时间1+++"+timeThree.get(sz));
//            System.out.println("结束时间+++"+timeThree.get(wz));
//        }

        int[] rrList = new int[3137];
        int rrs = 0;
        //5910 9047  3137
        int chu =5910;
        int zhong =9047;

        for (int i = chu; i < zhong; i++) {
            Integer integer = RR.get(i);
            rrList[rrs] = integer;
            rrs = rrs+1;
        }
        int variance = Variance(rrList);



        List<Integer> original = Arrays.asList(ArrayUtils.toObject(rrList)); //原始数组
        int F = 1;

        int m = 0;
        int thirtymiaoone = 30;
        int thirtymiaotwo = 30;
        int nx = variance;
        int leixinga = 0;
        int leixingb = 0;// 1 2
        int shou = 0;
        int wei =0;
        List<Integer> arrListone = new ArrayList(original);
        List<Integer> arrListtwo = new ArrayList(original);
        List<Integer> arrListcha = new ArrayList(original);
        while(true){
            if (leixinga != 1){
                   //n1a
                //thirtymiaoone = thirtymiaoone+30;
                if (m>0){
                    arrListone = new ArrayList(arrListcha);
                }

                for (int i = 0; i < arrListone.size(); i++) { //删除左边前30秒
                    if (i<thirtymiaoone){
                        arrListone.remove(0);
                    }
                }
                int[] intArrOne = arrListone.stream().mapToInt(Integer::valueOf).toArray();
                int na1 = Variance(intArrOne); //求得na1方差
                if (na1-nx<=F){
                    leixinga = 1;
                    shou = m;
                }
            }

            if (leixingb != 2) {
                //n1b
                 //thirtymiaotwo = thirtymiaotwo+30;
                if (m>0){
                    arrListtwo = new ArrayList(arrListcha);
                }

                for (int i = 0; i < arrListtwo.size(); i++) { //删除左边前30秒
                    if (i < thirtymiaoone) {
                        arrListtwo.remove(arrListtwo.size() - 1);
                    }
                }
                int[] intArrTwo = arrListtwo.stream().mapToInt(Integer::valueOf).toArray();
                int na2 = Variance(intArrTwo); //na2 右边数组方差
                if (na2 - nx <= F) {
                    leixingb = 2;
                    wei = m;
                }
            }

            if (leixinga==1 && leixingb==2){
                break;
            }

            for (int i = 0; i < arrListcha.size(); i++) {
                if (i<thirtymiaoone && leixinga != 1){
                    arrListcha.remove(0);
                }
                if (i<thirtymiaotwo && leixingb != 2){
                    if (arrListcha.size()>0){
                        arrListcha.remove(arrListcha.size()-1);
                    }

                }

            }
            int[] intArrCha = arrListcha.stream().mapToInt(Integer::valueOf).toArray();
            nx = Variance(intArrCha);  //最后形成的数组方差
            m++;
        }
        int size = original.size();
        int i = size - wei;
        System.out.println("首值+++"+shou);
        System.out.println("尾值+++"+i);

        long[] arrx = new long[3137];
        long[] arry = new long[3137];
        int kai = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(createTime);
        int secondTimestamp = getSecondTimestamp(date);

        List<List<Long>> objects3 = new ArrayList<>();
        for (int h = chu; h < zhong; h++) {
            ArrayList<Long> objects = new ArrayList<>();
            long ts = date.getTime();
           arrx[kai] = ts+h*1000;
           arry[kai] = PrListCopy.get(h);
           objects.add(1000l);
           objects.add(Long.valueOf(PrListCopy.get(h)));
            objects3.add(objects);
           kai++;
        }

        Map<String, Object> stringObjectMap = lineRegression(arrx, arry);
        long x1 = arrx[0];
        long x2 = arrx[arrx.length-1];
        Long pa = (Long) stringObjectMap.get("a");
        Long pb = (Long) stringObjectMap.get("b");
        long y1 = pa * x1 + pb;
        long y2 = pa * x2 + pb;



        List<List<Long>> objects = new ArrayList<>();
        List<Long> objects1 = new ArrayList<>();
        objects1.add(x1);
        objects1.add(y1);
        List<Long> objects2 = new ArrayList<>();
        objects2.add(x2);
        objects2.add(y2);
        objects.add(objects1);
        objects.add(objects2);
//        List<Integer> arrListone = new ArrayList(original);    //n1a
//        int thirtymiaoone = 30;
//        for (int i = 0; i < arrListone.size(); i++) { //删除左边前30秒
//            if (i<thirtymiaoone){
//                arrListone.remove(0);
//            }
//        }
//        int[] intArrOne = arrListone.stream().mapToInt(Integer::valueOf).toArray();
//        int na1 = Variance(intArrOne); //方差

//        List<Integer> arrListtwo = new ArrayList(original);   //n1b
//        int thirtymiaotwo = 30;
//
//        for (int i = 0; i < arrListtwo.size(); i++) { //删除左边前30秒
//            if (i<thirtymiaoone){
//                arrListtwo.remove(arrListtwo.size()-1);
//            }
//        }
//        int[] intArrTwo = arrListtwo.stream().mapToInt(Integer::valueOf).toArray();
//        int na2 = Variance(intArrTwo); //na2 右边数组方差
//
//        List<Integer> arrListcha = new ArrayList(original);
//        for (int i = 0; i < arrListcha.size(); i++) {
//            if (i<thirtymiaoone){
//                arrListcha.remove(0);
//            }
//            if (i<thirtymiaotwo){
//                arrListcha.remove(arrListcha.size()-1);
//            }
//
//        }
//        int[] intArrCha = arrListcha.stream().mapToInt(Integer::valueOf).toArray();
//        int n2 = Variance(intArrCha);  //最后形成的数组

        //System.out.println("c+++"+variance+"na1++++"+na1+"na2++++"+na2+"n2++++"+n2);


//        JSONArray jsonArrays = new JSONArray();
//        ArrayList<Object> object = new ArrayList<>();
//        for (int i = 0; i < PrListCopy.size(); i++) {
//            long ts = date.getTime();
//            ArrayList<Object> objectss = new ArrayList<>();
//            objects.add(integerList.get(i));
//            objects.add(PrList.get(i));
//            jsonArrays.put(objects);
//            object.add(objects);
//        }
//        System.out.println("object="+object.toString().replaceAll("\"",""));

        List<List<Long>> objects4 = new ArrayList<>();
        for (int h = 0; h < PrListCopy.size(); h++) {
            ArrayList<Long> objectss = new ArrayList<>();
            long ts = date.getTime();
            if (h>=chu && h<= zhong){
                objectss.add(Long.valueOf(ts + h * 1000));
                objectss.add(Long.valueOf(y1));
            }else {
                objectss.add(ts + h * 1000);
                objectss.add(Long.valueOf(PrListCopy.get(h)));
            }
            objects4.add(objectss);
            kai++;
        }


        System.out.println("list+++"+list);
        System.out.println("objects+++"+objects);
        System.out.println("stringObjectMap+++"+stringObjectMap);
        System.out.println("objects4+++"+objects4);
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

    private static void loop3(List<Integer> prtwoThree, int a3, int b3, List<Integer> prTwo, List<Integer> prThrees, int rintThree, List<Integer> timeTwo, int a2,List<Integer> timeThree) {
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
            time = time + (a3 - 1); //获得时间下标值最大值
            if (time > timeTwo.size() - 1) {
                return;
            }
            if (i==0){
                miao = 0;
                timeThree.add(miao);
            }
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
        System.out.println("dateString=" + endTime);
        return endTime;
    }

}
