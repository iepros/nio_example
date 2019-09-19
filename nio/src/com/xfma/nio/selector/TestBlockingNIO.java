package com.xfma.nio.selector;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class TestBlockingNIO {

    /**
     * NIO阻塞式IO客户端
     * 客户端往服务端发送一个文件
     */
    @Test
    public void iOClient() throws Exception{
        //1.创建一个SocketChannel
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9999));

        //2.创建一个文件channel
        FileChannel fileChannel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);

        //3.创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (fileChannel.read(buffer) != -1){
            //切换到读状态
            buffer.flip();
            //往服务端写数据
            socketChannel.write(buffer);
            buffer.clear();
        }

        socketChannel.shutdownOutput();

        //接收服务端返回数据
        int len = 0;
        while ((len = socketChannel.read(buffer)) != -1){
            buffer.flip();
            System.out.println(new String(buffer.array(),0,len));
            buffer.clear();
        }

        fileChannel.close();
        socketChannel.close();

    }

    /**
     * NIO阻塞式IO服务端
     */
    @Test
    public void iOServer()throws Exception{
        //1.创建一个ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2.绑定端口
        serverSocketChannel.bind(new InetSocketAddress(9999));

        //3.创建FileChannel
        FileChannel fileChannel = FileChannel.open(Paths.get("2.txt"),StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //4.监听客户端的连接
        SocketChannel socketChannel = serverSocketChannel.accept();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //5.接收客户端数据并保存到本地
        while (socketChannel.read(buffer) != -1){
            buffer.flip();
            //往本地写数据，保存文件
            fileChannel.write(buffer);
            buffer.clear();
        }

        //往客户端反馈
        buffer.put("服务器接收数据成功".getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        socketChannel.shutdownInput();
        socketChannel.close();
        fileChannel.close();
        serverSocketChannel.close();

    }
}
