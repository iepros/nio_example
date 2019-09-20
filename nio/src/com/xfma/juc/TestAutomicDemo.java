package com.xfma.juc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 运行代码得知：新增后的最大值有可能小于9
 *      原因：volatile虽然可以保证内存的可见性，但是不具有原子性
 *              num++在JVM中优化后可以分解为三步：
 *                  1. num = 0;
 *                  2. temp = num +1;
 *                  3. num = temp;
 *            volatile可以保证一个变量的内存可见性，但是保证不了多个变量的可见性，所以此时变量应该使用并发包下的AtomicInteger
 *
 * @author mxf
 * @version 1.0
 */
public class TestAutomicDemo {
    public static void main(String[] args) {

//        AutomicDemo automicDemo = new AutomicDemo();
//
//        for (int i=0;i<10;i++){
//            new Thread(automicDemo).start();
//        }


        System.out.println("----------------------------------");

        AtomicIntegerTest atomicIntegerTest = new AtomicIntegerTest();

        for (int i=0;i<10;i++){
            new Thread(atomicIntegerTest).start();
        }
    }
}

/**
 * 线程安全
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
class AtomicIntegerTest implements Runnable{

    private AtomicInteger num = new AtomicInteger(0);
    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + ":" + num.incrementAndGet());
    }
}

/**
 * 会出现安全问题
 */
class AutomicDemo implements Runnable{

    private volatile int num = 0;

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + ":" + incrementNum());
    }

    public int incrementNum(){
        return num++;
    }
}
