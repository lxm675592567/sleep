package com.hife.util;

/*
 * 求得众数
 * */
public class ModeUtil {

    public int sum = 0;//当前最高重复次数
    public int[] number;//众数数组
    public int t = 0;//当前众数数组元素个数-1

    public int Partition(int[] a, int left, int right) {//划分函数，以a[left]为主元，将数组划分为比a[left]小和比a[left]大的两部分
        int i = left, j = right;
        int temp;
        do {
            do {
                i++;
                if (i >= right) break;//防止数组a只有一个元素时可能会发生循环判断条件的错误
            } while (a[i] < a[left]);
            do j--; while (a[j] > a[left]);
            if (i < j) {
                temp = a[i];
                a[i] = a[j];
                a[j] = temp;
            }
        } while (i < j);
        temp = a[j];
        a[j] = a[left];
        a[left] = temp;
        return j;
    }

    public int Count(int[] a, int x, int left, int right) {//统计从a[left]到a[right-1]中x出现的次数
        int count = 0;
        for (int i = left; i < right; i++) {
            if (a[i] == x)
                count++;
        }
        return count;
    }

    public void Mode(int a[], int left, int right) {
        if (left < right) {
            int q = Partition(a, left, right);//获得划分后的主元
            int times = Count(a, a[q], left, right);//统计该主元出现的次数
            if (sum < times) {//如果此前记录的重数比本次统计的出现次数小，则众数变为当前主元，众数个数变为1
                t = 0;
                sum = times;
                number[t] = a[q];
            } else if (sum == times) {//如果此前记录的重数与本次记录的频数相等，则众数数组+1
                number[++t] = a[q];
            }
            Mode(a, left, q - 1);
            Mode(a, q + 1, right);
        }
    }
}
