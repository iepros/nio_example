package com.xfma.juc;

/**
 * volatile:当多个线程操作共享变量是，可以保证数据可见性，相较于synchronized是一种较为轻量级的同步策略
 * 但是volatile不具 备互斥性和原子性
 * synchronized:互斥所，一个线程进入到同步代码块，另一个线程需要等上个线程释放了锁  才能进入同步代码块
 * 变量使用volatile修饰，一个线程修改了变量的值，另一个线程立刻能被看到，保证共享变量的值再多线程中是同步的
 * 原理：共享内存，volatile修饰的共享变量在主内存中，线程操作变量，直接操作的主内存数据，而不是拷贝一份到本地线程的私有变量，改完后再刷进主内存
 *
 * @author mxf
 * @version 1.0
 */
public class TestVolatile {

    public static void main(String[] args) {
        ThreadDemo demo = new ThreadDemo();
        new Thread(demo).start();

        while (true){
            if (demo.isFlag()){
                System.out.println("--------------------");
                break;
            }
        }

    }
}

class ThreadDemo implements Runnable{

    private volatile boolean flag = false;

    @Override
    public void run() {

        try {
            Thread.sleep(500);
        }catch (Exception e){

        }

        flag = true;

        System.out.println("-----------" + isFlag() + "-----------");
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
