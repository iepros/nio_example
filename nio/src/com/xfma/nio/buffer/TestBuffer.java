package com.xfma.nio.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * nio buffer demo：
 *  Buffer：缓冲区 抽象类，nio中除了boolean没有对应的缓冲区实现，其他七大类型都有对应的实现类
 *
 *  获取办法：
 *      ByteBuffer.allocate()：这种办法获取的缓冲区底层是数组实现，具体内存由JVM分配
 *      ByteBuffer.allocateDirect()：由操作系统OS来分配内存，传说中的零拷贝问题就是通过这种方式实现的
 *
 *   Buffer中很重要的四个属性：
 *      mark：标记，标记当前position的问题，如果再次读取后，可通过reset()方法将position的值复原到mark的位置，方便重复读
 *      position：当前操作数据的位置
 *      limit：当前可操作的数据数量
 *      capacity：当前的容量
 *
 * @author mxf
 * @version 1.0
 */
public class TestBuffer {
    @Test
    public void testByteBuffer(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        System.out.println("----------allocate-----------");
        System.out.println(buffer.capacity());
        System.out.println(buffer.limit());
        System.out.println(buffer.position());

        String str = "abcde";
        buffer.put(str.getBytes());
        //

        System.out.println("----------put-----------");
        System.out.println(buffer.capacity());
        System.out.println(buffer.limit());
        System.out.println(buffer.position());

        buffer.flip();//切换到读取数据模式
        System.out.println("----------flip-----------");
        System.out.println(buffer.capacity());
        System.out.println(buffer.limit());
        System.out.println(buffer.position());

        //读取数据
        byte[] sdt = new byte[buffer.limit()];
        buffer.get(sdt,0,buffer.limit());
        System.out.println(new String(sdt,0,sdt.length));

        System.out.println("------------设置重复读，会将position位置设置为0，从头开始读--------------");
        buffer.rewind();//设置重复读，会将position位置设置为0，从头开始读
        buffer.get(sdt,0,buffer.limit());
        System.out.println(new String(sdt,0,sdt.length));

        buffer.clear();//清空缓冲区，重置position、limit；但是数据不会被清空，到被遗忘状态，仍然可以读数据
        System.out.println("------------清空缓冲区后读数据--------------");
        System.out.println((char) buffer.get());
    }

    /**
     * mark:标记，记录当前position位置，可以通过reset()恢复到mark的位置
     * mark <= position <= limit <= capacity
     */
    @Test
    public void testMark(){
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(str.getBytes());
        //切换到读模式
        buf.flip();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst,0,2);

        System.out.println(new String(dst,0,2));

        System.out.println(buf.position());

        //记录当前position位置
        buf.mark();
        buf.get(dst,buf.position(),2);

        System.out.println(new String(dst,2,2));

        System.out.println(buf.position());

        //position恢复到mark的位置
        buf.reset();
        System.out.println(buf.position());

        //查看缓冲区是否还有可以操作的数据
        if (buf.hasRemaining()){
            //返回可操作的数据数量
            System.out.println(buf.remaining());
        }

    }
}
