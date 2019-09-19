package com.xfma.nio.channel;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 通道（channel）：用于源节点寓目标节点的连接
 * 负责缓冲区数据的传输
 * channel本身布存储数据，需要配合缓冲区进行数据传输
 *
 * 本地操作
 *  FileChannel
 * 网络操作
 *  TCP
 *      SocketChannel
 *      ServerSocketChannel
 *  UDP
 *      DatagramChannel
 *
 * 直接缓冲区：直接使用操作系统的内存作为缓冲区
 * 间接缓冲区：使用JVM分配的内存作为缓冲区
 *
 * 直接缓冲区、间接缓冲区优缺点：
 *      直接缓冲区：优点：快，不给JVM增加压力；缺点：文件交给操作系统去管理了，JVM没有操作权
 *      间接缓冲区：优点：JVM直接操作文件，可控制文件；缺点：慢
 *
 *
 * @author mxf
 * @version 1.0
 */
@SuppressWarnings("all")
public class TestChannel {

    /**
     * 完成文件的复制
     */
    @Test
    public void testFileChannel() throws Exception{
        //创建输入输出流
        FileInputStream fis = new FileInputStream("1.jpeg");
        FileOutputStream fos = new FileOutputStream("2.jpeg");

        //获取管道
        FileChannel fisChannel = fis.getChannel();

        FileChannel fosChannel = fos.getChannel();

        //创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (fisChannel.read(buffer) != -1){
            //切换到读模式
            buffer.flip();
            //往外写
            fosChannel.write(buffer);
            //清空缓冲区，继续读
            buffer.clear();
        }

        fisChannel.close();
        fosChannel.close();
        fos.close();
        fis.close();

    }

    /**
     * 完成文件的复制
     * 通道之间数据传输
     * 直接缓冲区：使用操作系统的内存作为缓冲区
     */
    @Test
    public void testFileChannel2() throws Exception{
        //获取读操作的通道
        FileChannel inChannel = FileChannel.open(Paths.get("/xfma/rd_process_definition.zip"), StandardOpenOption.READ);
        //获写读操作的通道
        FileChannel outChannel = FileChannel.open(Paths.get("/xfma/rd_process_definition2.zip"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        inChannel.transferTo(0,inChannel.size(),outChannel);
        outChannel.close();
        inChannel.close();
    }

    /**
     * 分散读取和聚集写入
     * 分散读取：将channel中的数据以此分散到多个缓冲区中
     * 聚集写入：将多个缓冲区的数据聚集到多个channel中
     */
    @Test
    public void test3()throws Exception{
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt","rw");

        FileChannel channel = randomAccessFile.getChannel();
        ByteBuffer b1 = ByteBuffer.allocate(100);
        ByteBuffer b2 = ByteBuffer.allocate(1024);

        ByteBuffer[] bus = {b1,b2};
        //分散读
        channel.read(bus);

        for (ByteBuffer byteBuffer : bus) {
            //切换到读模式，将position置为0
            byteBuffer.flip();
        }

        System.out.println(new String(bus[0].array(),0,bus[0].limit()));
        System.out.println("-----------------------------");
        System.out.println(new String(bus[1].array(),0,bus[1].limit()));

        RandomAccessFile randomAccessFile2 = new RandomAccessFile("2.txt","rw");
        FileChannel channel1 = randomAccessFile2.getChannel();
        //聚集写
        channel1.write(bus);
    }
}
