package com.xfma.juc;

import java.util.Random;

/**
 * 模拟实现CAS算法
 * CAS缺点：一直在失败重试，占用太多CPU资源，高并发的情况下，服务器压力比较大
 *http://www.imooc.com/article/details/id/44189
 * @author mxf
 * @version 1.0
 */
public class TestCAS {

    public static void main(String[] args) {
        final CAS cas = new CAS();
        for (int i=0;i<100;i++){
            new Thread(new Runnable(){
                @Override
                public void run() {
                    int value = cas.getValue();
                    boolean b = cas.compareAndSet(value, (int) (Math.random() * 20));
                    System.out.println(b);
                }
            }).start();
        }
    }

}

class CAS{
    private int value;

    public synchronized int getValue(){
        return value;
    }

    /**
     * 比较并交换
     * 预估值和原值相等，则交换并返回新值，否则，不作处理，返回原来的值
     * @param v 预估值，比较时获取的旧值
     * @param n 新值
     * @return
     */
    public synchronized int compareAndSwap(int v,int n){
        if (this.value == v){
            this.value = n;
        }

        return this.value;
    }

    /**
     * 比较并设置值
     * @param v 预估值，比较时获取的旧值
     * @param n 新值
     * @return 交换是否成功
     */
    public synchronized boolean compareAndSet(int v,int n){
        return v == compareAndSwap(v,n);
    }
}
