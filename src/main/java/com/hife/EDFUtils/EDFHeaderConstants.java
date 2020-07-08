package com.hife.EDFUtils;

class EDFHeaderConstants {
    public static String[] specs = { "包头", "患者标识*", "本地记录标识*", "记录的开始日期*", "记录的开始时间*", "页眉记录中的字节数", "保留", "数据记录的数量",
            "数据记录的持续时间*", "数据记录中的信号数目*", "标签", "传感器类型", "物理尺寸", "物理最小值", "物理最大值", "数字最小值", "数字最大值", "prefiltering",
            "NS*NR在每个数据记录中的样本", "NS*预留" };

    public static int[] lenghts = { 8, 80, 80, 8, 8, 8, 44, 8, 8, 4, 16, 80, 8, 8, 8, 8, 8, 80, 8, 32 };
    public static int total = 512;
}

