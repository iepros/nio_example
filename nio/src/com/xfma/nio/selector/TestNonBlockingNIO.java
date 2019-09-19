package com.xfma.nio.selector;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * NIO的完成网络通信的三个核心：
 * 1、通道（channel）：负责连接
 *      java.nio.channel.Channel 接口：
 *          |--SelectableChannel
 *              |--SocketChannel    TCP
 *              |--ServerSocketChannel  TCP
 *              |--DatagramChannel  UDP
 *
 *              |--Pipe.SinkChannel
 *              |--Pipe.SourceChannel
 *
 * 2、缓冲区（buffer）：负责数据的存取
 *
 * 3、选择器（selector）：是SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况
 *
 * @author mxf
 * @version 1.0
 */
public class TestNonBlockingNIO {

    @Test
    public void client()throws Exception{

        //1.获取一个SocketChannel
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));

        //2.设置为非阻塞通道
        socketChannel.configureBlocking(false);

        //3.获取一个缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String next = scanner.next();
            //4.往缓冲区写入当前时间
            buffer.put((new Date().toString() + "\n" + next).getBytes());
            buffer.flip();
            //5.往服务端写数据
            socketChannel.write(buffer);
            buffer.clear();
        }


        socketChannel.close();

    }

    @Test
    public void server()throws Exception{
        //1.获取一个ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2.设置为非阻塞通道
        serverSocketChannel.configureBlocking(false);

        //3.绑定9999端口
        ServerSocketChannel socketChannel = serverSocketChannel.bind(new InetSocketAddress(9999));

        //4.获取选择器
        Selector selector = Selector.open();
        //5.将通道注册到选择器上，并且制定“监听接收事件”
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //6.轮询式的获取选择器上已经“准备就绪”的事件
        while (selector.select()>0){
            //7.获取当前选择器中所有注册的“选择键”（已经准备就绪）
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            if (!selectionKeys.isEmpty()){
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    //8.获取准备“就绪”事件
                    SelectionKey selectionKey = iterator.next();

                    //判断是什么事件准备就绪
                    if (selectionKey.isAcceptable()){
                        SocketChannel accept = serverSocketChannel.accept();
                        //9.设置为非阻塞通道
                        accept.configureBlocking(false);
                        //10.将该通道注册到选择器上
                        accept.register(selector,SelectionKey.OP_READ);
                    }if (selectionKey.isReadable()){
                        //11.获取当前选择器上“读就绪”状态的通道
                        SocketChannel sc = (SocketChannel) selectionKey.channel();
                        //12.准备读数据
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = 0;
                        while ((len = sc.read(buffer)) != -1){
                            buffer.flip();
                            System.out.println(new String(buffer.array(),0,len));
                            buffer.clear();
                        }

                    }if (selectionKey.isWritable()){

                    }

                    //用完之后需要移除selectionKey
                    iterator.remove();
                }
            }
        }

    }
}
