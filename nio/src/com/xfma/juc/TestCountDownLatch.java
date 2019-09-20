package com.xfma.juc;

import java.util.concurrent.CountDownLatch;

/**
 * 闭锁：当某些计算完成后再执行下面的操作
 *
 * 需求：多线程计算5000以内的偶数和，计算完成后，打印计算时间
 *
 * @author mxf
 * @version 1.0
 */
public class TestCountDownLatch {
    public static void main(String[] args) {
        sumNum(1);
    }

    public static void sumNum(int threadNum){
        final CountDownLatch latch = new CountDownLatch(threadNum);
        long [] result = new long[threadNum];
        long start = System.currentTimeMillis();
        for (int i=0;i<threadNum;i++){
            new Thread(new CountDownLatchDemo(latch,result,i)).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();

        long sum =0;
        for (int i=0;i<threadNum;i++){
            sum += result[i];
        }

        System.out.println("计算时间为："+ (end - start));
        System.out.println("sum："+ sum);
    }
}

class CountDownLatchDemo implements Runnable{

    private CountDownLatch latch;
    long [] result = null;
    int num ;


    private volatile long sum;

    public CountDownLatchDemo(CountDownLatch latch,long [] result,int num){
        this.latch = latch;
        this.result = result;
        this.num = num;
    }

    @Override
    public void run() {
        try {
            for (int i=1; i<= 100; i++){
                if (i %2 == 0){
                    sum += i;
                }
            }

            result[num] = sum;
        }finally {
            latch.countDown();
        }
    }
}
