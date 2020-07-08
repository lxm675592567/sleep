package com.hife.util;

import com.alibaba.fastjson.JSONObject;
import com.hife.EDFUtils.EDFParser;
import com.hife.EDFUtils.EDFRecord;
import org.json.JSONArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EdfUtil {

    public static String readJsonFile(JSONObject json) throws ParseException {
        int filterNumber = json.getInteger("filterNumber");
        String filterName = json.getString("filterName");
        int a1 = json.getInteger("a1");int b1 = json.getInteger("b1");
        int a2 = json.getInteger("a2");int b2 = json.getInteger("b2");
        int a3 = json.getInteger("a3");int b3 = json.getInteger("b3");
        String type = json.getString("type");//type 1json 2dat

        //String path = "D:\\项目\\sleep\\sleep\\src\\main\\java\\com\\hife\\"+filterName;
        String path = "D:\\项目\\新数据\\dat\\"+filterName;

        EDFParser edf = new EDFParser(path);
        HashMap<String, String> header = edf.header;
        List<EDFRecord> records = edf.records;
        String startTime = header.get("记录的开始时间*").replace(".",":");//时分秒 14.50.52
        String startDate = header.get("记录的开始日期*").replace(".",":");//日期 11.01.20
        String[] strArr = startDate.split("\\:");
        String time = "20"+strArr[2]+"-"+strArr[1]+"-"+strArr[0];
        int miao = records.size() * 2; //睡眠总秒数
        int minute = miao / 60 % 60; //睡眠时间转换成分钟
        String createTime = time+" "+startTime;//开始时间 startDate=2020-01-11 15:09:52
        String endTime = getEndTime(createTime, minute);//结束时间
        List<Integer> PrList = new ArrayList<>();

        List<Long> integerList = new ArrayList<>();

        if(type.equals("2")){
            for (EDFRecord record : records) {
                short[] hr = record.HR;
                for (int i : hr) {
                    if (i<=200){
                        PrList.add(i);

                    }else {
                        PrList.add(0);
                    }
                }
            }
        }else {
            String jsonString = JsonUtil.readJsonFile("D:\\项目\\新数据\\"+filterName);
            net.sf.json.JSONObject dataMeaning = net.sf.json.JSONObject.fromObject(jsonString);
            net.sf.json.JSONArray jsonArray = dataMeaning.getJSONArray("data");
            for (int f = 0, size = jsonArray.size(); f < size; f++) {
                net.sf.json.JSONObject jsonObject = jsonArray.getJSONObject(f);
                PrList.add(jsonObject.getInt("heart_rate"));
                integerList.add(jsonObject.getLong("timestamp")*1000);
            }
        }

        List<Integer> prTwo = new ArrayList<>(PrList);
        List<Integer> prThree = new ArrayList<>();
        List<Integer> prtwoThree = new ArrayList<>();
        List<Integer> prThrees = new ArrayList<>();

        //时间数组
        List<Integer> timeOne = new ArrayList<>();
        List<Integer> timeTwo = new ArrayList<>();
        prThree.add(0,PrList.get(0)); timeOne.add(0,0);
        if (filterNumber >= 1) {
            int yushu = PrList.size()%(a1-1) <= 1 ? (PrList.size()/(a1-1)) : (PrList.size()/(a1-1))+1;
            int rint = (int) Math.rint(yushu);
            loop(PrList, a1, b1, prTwo, prThree, rint,timeOne);
        }


        ////getRemainder(a1, prTwo, prThree);
        if (filterNumber >= 2) {
            int yushuTwo = prThree.size() % (a2 - 1) <= 1 ? (prThree.size() / (a2 - 1)) : (prThree.size() / (a2 - 1)) + 1;
            int rintTwo = (int) Math.rint(yushuTwo);
            prtwoThree.add(0, prThree.get(0));
            timeTwo.add(0, 0);
            loop2(prThree, a2, b2, prTwo, prtwoThree, rintTwo, a1, timeOne, timeTwo);
        }
        if (filterNumber >= 3) {
            int yushuThree = prtwoThree.size()%(a3-1) <= 1 ? (prtwoThree.size()/(a3-1)) : (prtwoThree.size()/(a3-1))+1;
            int rintThree = (int) Math.rint(yushuThree);
            loop3(prtwoThree, a3, b3, prTwo, prThrees, rintThree, timeTwo, a2, a1);
        }
        Long times = 0L;
        String zero =null;
        //获取最终结果 [时间.值]
        if (filterNumber == 0){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(createTime);
            int secondTimestamp = getSecondTimestamp(date);
            JSONArray jsonArrays = new JSONArray();
            ArrayList<Object> object = new ArrayList<>();
            for (int i = 0; i < PrList.size(); i++) {
                long ts = date.getTime();
                ArrayList<Object> objects = new ArrayList<>();
                if (type.equals("2")){
                    times = ts+i*1000;
                }else {
                    times = integerList.get(i);
                }
                objects.add(times);
                if (prTwo.get(i) == 0){
                    objects.add(zero);
                }else {
                    objects.add(prTwo.get(i));
                }
                jsonArrays.put(objects);
                object.add(objects);
            }
            // System.out.println("object="+object.toString().replaceAll("\"",""));
            return object.toString().replaceAll("\"","");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(createTime);
        int secondTimestamp = getSecondTimestamp(date);
        JSONArray jsonArrays = new JSONArray();
        ArrayList<Object> object = new ArrayList<>();
        for (int i = 0; i < prTwo.size(); i++) {
            long ts = date.getTime();
            ArrayList<Object> objects = new ArrayList<>();
            if (type.equals("2")){
                times = ts+i*1000;
            }else {
                times = integerList.get(i);
            }
            // objects.add(integerList.get(i));
            objects.add(times);
            if (prTwo.get(i) == 0){
                objects.add(zero);
            }else {
                objects.add(prTwo.get(i));
            }
            jsonArrays.put(objects);
            object.add(objects);
        }
        // System.out.println("object="+object.toString());
        return object.toString();
    }

    public static int getSecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0,length-3));
        } else {
            return 0;
        }
    }

    private static void getRemainder(int a1, List<Integer> prTwo, List<Integer> prThree) {
        int integer = prTwo.size()/a1;
        int remainder = prTwo.size()%a1;
        if (remainder>0){
            int rest = 0;
            for(int i=0;i<remainder;i++) {
                rest = prTwo.get(integer*a1-1);
                prTwo.set(integer*a1+i,prTwo.get(integer*a1-1));
            }
            prThree.add(rest);
        }
    }

    private static void loop(List<Integer> prList, int a1, int b1, List<Integer> prTwo, List<Integer> prThree, int rint,List<Integer> timeOne) {
        int miao =0;
        for(int i=0;i<rint;i++){
            //从1中取值一直是从0开始
            List<Integer> integerr = new ArrayList<>();
            if(prList.size()<a1){
                for (int k=1;k<prList.size();k++){
                    int kl = i * a1+ k; //0 1 2   3 4 5    6
                    int ky = kl - i;
                    prTwo.set(ky,prTwo.get(ky-1));
                }
                return;
            }else {
                integerr = prList.subList(0, a1);
            }
            List<Integer> integers = new ArrayList<>();
            integers.addAll(integerr);
            String type = "0"; //1递增取最大值 2递减取最大值 3绝对值小于b 取第一值 4绝对值大于等于b 取绝对值最大值
            Boolean fun = fun(integers); //true 递增  flase 递减
            int saz = 0;
            int coordinate =0;
            int absolute = 0;
            if (fun){
                type = "1";
            }else if(reduce(integers)){
                type = "2";
            }else if(absoluteSmall(integers,b1)){
                type = "3";
            }else{
                for(int m=0;m<integers.size()-1;m++) {
                    //if(m<integers.size()-1 ) {
                    absolute = Math.abs(integers.get(m + 1) - integers.get(m));//后者减前者取得差值
                    // 先判断
                    if(absolute>saz){ //绝对值大于saz 则赋值给saz 坐标也给与C 取得最大值和坐标
                        saz = absolute;
                        coordinate = m;//坐标
                    }
                    //   }
                }
            }

            if(type.equals("1") || type.equals("2") ){
                int size = integers.size();
                Integer integer = integers.get(size-1); //自增最大值
                Integer integer1 = integers.get(0); //自增最小值

                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prList.remove(0);
                    }
                }
                for(int l=0;l<size;l++) {
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

                    if(l != size-1){ //判断是否为最大值,最大值原值不变
                        prTwo.set(i2,integer1);
                    }

                    //prTwo.size();
                    //将1中不需要数值删除
                    if(l==0){
                        prList.set(0,integer);
                    }
                }
                //添加三
                prThree.add(integers.get(integers.size()-1));
            }else if( type.equals("3")){
                Integer integer = integers.get(0); //首值
                Integer integer1 = integers.get(1); //次值
                int size = integers.size(); //a1长度
                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prList.remove(0); //删掉原始数组无用
                    }
                }
                for(int l=0;l<size;l++) {
                    int i1 = i * a1 + l;
                    int i2 = i1 - i;
                    if(l!= size-1){
                        prTwo.set(i2,integer); //全部等于首值 不确定i1
                    }
                    if(l==0 ){
                        // prList.set(i2,integer); 不确定
                        prList.set(0,integer);
                    }
                }
                prThree.add(integers.get(0));
            }else{
                Integer integer = integers.get(coordinate+1);//先获得差值减数值 也就是绑定者
                Integer integerOne = integers.get(0); //首值
                int size = integers.size();
                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prList.remove(0);
                    }
                }
                for(int l=0;l<size;l++) {
//                        int i1 = i * a1 + l;
//                        prTwo.set(i1,integer);
                    int i1 = i * a1 + l;
                    int i2 = i1 - i;
                    if(l!= size-1){
                        prTwo.set(i2,integerOne); //全部等于首值
                    }else {
                        prTwo.set(i2,integer); //最后值
                    }
                    if(l==0 ){
                        prList.set(0,integer);
                    }
                }
                prThree.add(prList.get(0));
            }
            miao = miao + (a1-1);
            timeOne.add(miao);
        }
    }

    private static void loop2(List<Integer> prThree, int a2, int b2, List<Integer> prTwo, List<Integer> prtwoThree, int rintTwo,int a1,List<Integer> timeOne,List<Integer> timeTwo) {
        int miao =0;
        int time = 0;
        for(int i=0;i<rintTwo;i++){
            //从1中取值一直是从0开始
            List<Integer> integerr = new ArrayList<>();
            if(prThree.size()<a2){
                for (int k=1;k<prThree.size();k++){
                    int kl = i * a2+ k; //0 1 2   3 4 5    6  1 3 1 =4
                    int ky = kl - i;
                    prTwo.set(ky,prTwo.get(ky-1));
                }
                return;
            }else {
                integerr = prThree.subList(0, a2);
            }
            //List<Integer> integerr = prThree.subList(0, a2);
            List<Integer> integers = new ArrayList<>();
            integers.addAll(integerr);
            String type = "0"; //1递增取最大值 2递减取最大值 3绝对值小于b 取第一值 4绝对值大于等于b 取绝对值最大值
            Boolean fun = fun(integers); //true 递增  flase 递减
            int coordinate =0;
            int absolute = 0;
            int saz = 0;
            if (fun){
                type = "1";
            }else if(reduce(integers)){
                type = "2";
            }else if(absoluteSmall(integers,b2)){
                type = "3";
            }else{
                for(int m=0;m<integers.size();m++) {
                    if(m<integers.size()-1 ) {
                        absolute = Math.abs(integers.get(m + 1) - integers.get(m));
                        // 先判断
                        if(absolute>saz){ //绝对值大于i1
                            saz = absolute;
                            coordinate = m;//坐标
                        }
                    }
                }
            }
            Integer timeInteger = 0;
            Integer timeIntegers = 0;
            Integer zuizhi = 0;
            if(type.equals("1") || type.equals("2")){
                int size = integers.size();
                Integer integer = integers.get(size-1);//自增最大值
                Integer integer1 = integers.get(0); //自增最小值
                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prThree.remove(0);
                    }
                }
                for(int l=0;l<size;l++) {
                    int i1 = i * a2 + l;
                    if(l==0){
                        prThree.set(0,integer);
                    }
                }
                Integer shouzhi = timeOne.get(time);
                time = time+a2-1; //获得时间下标值最大值
                if(time>timeOne.size()-1){
                    return;
                }
                zuizhi = timeOne.get(time);
                for(int l=shouzhi;l<zuizhi;l++) {
                    if(l != zuizhi){ //判断是否为最大值,最大值原值不变
                        prTwo.set(l,integer1);
                    }
                }
                //添加三
                prtwoThree.add(integers.get(integers.size()-1));

            }else if(type.equals("3")){
                Integer integer = integers.get(0);
                //自增最小值
                int size = integers.size();
                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prThree.remove(0);
                    }
                }
                for(int l=0;l<size;l++) {
                    int i1 = i * a2 + l;
                    //  prTwo.set(i1,integer);
                    if(l==0 ){
                        prThree.set(0,integer);
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
                time = time+a2-1; //获得时间下标值最大值
                if(time>timeOne.size()-1){
                    return;
                }
                zuizhi = timeOne.get(time);
                for (int l = shouzhi; l < zuizhi; l++) {
//                    int i1 = i * a2 + l;
                    prTwo.set(l, integer);
                }
                prtwoThree.add(integers.get(0));
            }else{
                Integer integer = integers.get(coordinate+1);
                Integer integerOne = integers.get(0);
                int size = integers.size();
                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prThree.remove(0);
                    }
                }
                for(int l=0;l<size;l++) {
                    int i1 = i * a2 + l;
                    // prTwo.set(i1,integer);
                    if(l==0 ){
                        prThree.set(0,integer);
                    }
                }

                Integer shouzhi = timeOne.get(time);
                time = time+(a2-1); //获得时间下标值最大值
                if(time>timeOne.size()-1){
                    return;
                }
                zuizhi = timeOne.get(time);
                for(int l=shouzhi;l<zuizhi;l++) {
//                    int i1 = i * a2 + l;
                    if(l!= zuizhi){
                        prTwo.set(l,integerOne); //全部等于首值
                    }else {
                        prTwo.set(l,integer); //最后值
                    }
                }
                prtwoThree.add(prThree.get(0));
            }
//            if(miao==0){
//                timeTwo.add(miao);
//                miao = zuizhi-1;
//                timeTwo.add(miao);
//            }else {
            miao = zuizhi-1;
            timeTwo.add(miao);
            //}

        }
    }
    private static void loop3(List<Integer> prtwoThree, int a3, int b3, List<Integer> prTwo, List<Integer> prThrees, int rintThree,List<Integer> timeTwo,int a2,int a1) {
        int time = 0;
        for(int i=0;i<rintThree;i++){
            //从1中取值一直是从0开始
            List<Integer> integerr = new ArrayList<>();
            if(prtwoThree.size()<a3){
                if(prtwoThree.size()==1){
                    return;
                }
                for (int k=1;k<prtwoThree.size();k++){
                    int kl = i * a2+ k; //0 1 2   3 4 5    6
                    int ky = kl - i;
                    prTwo.set(ky,prTwo.get(ky-1));
                }
                return;
            }else {
                integerr = prtwoThree.subList(0, a3);
            }

            List<Integer> integers = new ArrayList<>();
            integers.addAll(integerr);
            String type = "0"; //1递增取最大值 2递减取最大值 3绝对值小于b 取第一值 4绝对值大于等于b 取绝对值最大值
            Boolean fun = fun(integers); //true 递增  flase 递减
            int coordinate =0;
            int absolute = 0;
            int saz = 0;

            if (fun){
                type = "1";
            }else if(reduce(integers)){
                type = "2";
            }else if(absoluteSmall(integers,b3)){
                type = "3";
            }else{
                for(int m=0;m<integers.size();m++) {
                    if(m<integers.size()-1 ) {
                        absolute = Math.abs(integers.get(m + 1) - integers.get(m));
                        // 先判断
                        if(absolute>saz){ //绝对值大于i1
                            saz = absolute;
                            coordinate = m;//坐标
                        }
                    }
                }
            }
            Integer timeInteger = 0;
            Integer timeIntegers = 0;
            if(type.equals("1") ||type.equals("2")){
                int size = integers.size();
                Integer integer = integers.get(size-1);
                Integer integer1 = integers.get(0); //自增最小值
                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prtwoThree.remove(0);
                    }
                }
                for(int l=0;l<size;l++) {
                    int i1 = i * a3 + l;
                    if(l==0){
                        prtwoThree.set(0,integer);
                    }
                }

                Integer shouzhi = timeTwo.get(time);
                time = time+a3-1; //获得时间下标值最大值
                if(time>timeTwo.size()-1){
                    return;
                }
                Integer zuizhi = timeTwo.get(time);
                for(int l=shouzhi;l<zuizhi;l++) {
                    if(l != zuizhi) {
                        prTwo.set(l, integer1);
                    }
                }
                //添加三
                prThrees.add(integers.get(integers.size()-1));
            }else if( type.equals("3")){
                Integer integer = integers.get(0);
                int size = integers.size();
                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prtwoThree.remove(0);
                    }
                }
                for(int l=0;l<size;l++) {
                    int i1 = i * a3 + l;
                    //  prTwo.set(i1,integer);
                    if(l==0 ){
                        prtwoThree.set(0,integer);
                    }
                }
                Integer shouzhi = timeTwo.get(time);
                time = time+a3-1; //获得时间下标值最大值
                if(time>timeTwo.size()-1){
                    return;
                }
                Integer zuizhi = timeTwo.get(time);
                for (int l = shouzhi; l < zuizhi; l++) {
                    prTwo.set(l, integer);
                }

                prThrees.add(integers.get(0));
            }else{
                Integer integer = integers.get(coordinate+1);
                Integer integerOne = integers.get(0);
                int size = integers.size();
                for(int z=0;z<size;z++) {
                    if (z!=size-1){
                        prtwoThree.remove(0);
                    }
                }
                for(int l=0;l<size;l++) {
                    int i1 = i * a3 + l;
                    // prTwo.set(i1,integer);
                    if(l==0 ){
                        prtwoThree.set(0,integer);
                    }
                }

                Integer shouzhi = timeTwo.get(time);
                time = time+a3-1; //获得时间下标值最大值
                if(time>timeTwo.size()-1){
                    return;
                }
                Integer zuizhi = timeTwo.get(time);
                for(int l=shouzhi;l<zuizhi;l++) {
                    if(l!= zuizhi){
                        prTwo.set(l,integerOne); //全部等于首值
                    }else {
                        prTwo.set(l,integer); //最后值
                    }
                }
                prThrees.add(prtwoThree.get(0));
            }
        }
    }

    //绝对值取值
    public static Boolean absoluteSmall(List<Integer> integers,int b) {
        int i1 = 0;
        //int coordinate =0;
        for(int i=0;i<integers.size();i++) {
            if(i<integers.size()-1 ) {
                int absolute = Math.abs(integers.get(i + 1) - integers.get(i));
                // 先判断
                if(absolute>i1){ //绝对值大于i1
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
        if(i1<b){
            return true;
        }

        return false;
    }
    //增加
    public static Boolean fun(List<Integer> integers) {
        int tap = 0;int tbp = 0;
        for(int i=0;i<integers.size()-1;i++) {
            if(integers.get(i)>integers.get(i+1)) {
                return false;
            }else if(integers.get(i)==integers.get(i+1)){
                tap = -1;
            }else {
                tbp = 1;
            }
        }
        if(tap == -1 && tbp ==1){
            return false;
        }

        return true;
    }
    //减少
    public static Boolean reduce(List<Integer> integers) {
        int tap = 0;int tbp = 0;
        for(int i=0;i<integers.size()-1;i++) {
            if(integers.get(i)<integers.get(i+1)) {
                return false;
            }else if(integers.get(i)==integers.get(i+1)){
                tap = -1;
            }else {
                tbp = 1;
            }
        }
        if(tap == -1 && tbp ==1) {
            return false;
        }
        return true;
    }

    public static String getEndTime(String createTime,int minute) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(createTime);
        int time = date.getHours()* 60 + date.getMinutes()+minute;

        int hours = (int) Math.floor(time / 60);
        int minutes = time % 60;
        date.setHours(hours);
        date.setMinutes(minutes);
        String endTime = format.format(date);
        System.out.println("dateString="+endTime);
        return endTime;
    }
}
