package com.xfma.juc;

import java.util.Random;

/**
 * AtomicInteger 使用CAS算法
 * CAS：compare-and-swap 比较和交换
 * 核心思想就是：CAS中使用了三个数来保证原子性；
 *      V:原来内存中的值
 *      A:交换的时候进行比较，再次获取内存中的值
 *      B:新值
 *    在执行操作的时候，会拿 V == A 进行比较，如果返回true，则进行替换内存中的值
 *    如果返回false，则进行新一轮的操作（失败重试），直到true为止
 *
 * 当然不使用AtomicInteger，在进行num++的时候使用同步锁，还是能解决问题，但是同步锁是悲观锁，比较重量级
 * CAS是乐观锁，失败重试机制
 *
 */

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
