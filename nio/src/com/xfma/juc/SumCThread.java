package com.xfma.juc;

import java.util.concurrent.CountDownLatch;

/**
 * TODO 请添加说明
 *
 * @author mxf
 * @version 1.0
 */
public class SumCThread implements Runnable{
    private long start;
    private long end;
    private long[] result;
    private CountDownLatch cdl;
    private int num;

    public SumCThread(CountDownLatch cdl, long[] result, long start, long end,
                      int num){
        this.result = result;
        this.start = start;
        this.end = end;
        this.cdl = cdl;
        this.num = num;
    }

    @Override
    public void run(){
        long sum = 0L;
        for(long i=start; i<end; i++){
            sum += i;
        }
        result[num] = sum;
        cdl.countDown();
    }



    //每个线程结果怎么返回  线程如何等待最终求值
    //使用CountDownLatch
    public static void countDownLatchSum(int N, int numThread) throws
            InterruptedException {
        long start1 = System.currentTimeMillis();
        CountDownLatch cdl = new CountDownLatch(numThread);
        long[] result = new long[numThread];
        long sum = 0L;
        for(int i=0; i<numThread; i++){
            new Thread(new SumCThread(cdl, result,i*N/numThread, (i+1)*N/numThread, i)).start();
        }
        cdl.await();
        for(int i=0; i<numThread; i++){
            sum += result[i];
        }
        //并行计算
        long end1 = System.currentTimeMillis();
        System.out.println("并行计算耗时：" + (end1 - start1) + " ms");
        System.out.println("并行计算的结果：" + sum);
    }

    public static void main(String[] args) {
        try {
            countDownLatchSum(10000000,10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
