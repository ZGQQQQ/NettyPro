package com.atguigu.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception{
        //创建ServerSocketChannel 类似BIO的 ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //得到一个Selecor对象
		//注：这里得到的Selector的真实类型是WindowsSelectorImpl
        Selector selector = Selector.open();
        //绑定一个端口6666, 在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //把 serverSocketChannel 注册到  selector， 关心的 事件为 OP_ACCEPT（连接事件）
		//问题：serverSocketChannel注册到selector后，selector怎么管理serverSocketChannel？
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("注册后的selectionkey 数量=" + selector.keys().size()); // 1
        //循环等待客户端连接
        while (true) {
            //这里我们等待1秒，如果channel没有事件发生, 返回
            if(selector.select(5000) == 0) { //没有事件发生
                System.out.println("服务器等待了5秒，无连接");
                continue;
            }
            //如果selector.select返回的>0, 就获取到发生事件变化的selectionKey集合
            //1. 如果返回的>0， 表示已经获取到关注的事件
            //2. selector.selectedKeys() 返回的是关注事件的集合
            //3. 通过 selectionKeys 反向获取客户端通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selectionKeys 数量 = " + selectionKeys.size());
            //遍历 Set<SelectionKey>, 使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                //获取到SelectionKey
                SelectionKey key = keyIterator.next();
                //根据key对应的通道发生的事件做相应处理
                if(key.isAcceptable()) { //如果是 OP_ACCEPT, 有新的客户端连接
                    //给该客户端生成一个 SocketChannel
					//这里的accept方法不会阻塞，因为只有当客户端有连接时才会进入该if条件
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());
                    //将  SocketChannel 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将socketChannel 注册到selector, 关注事件为 OP_READ， 同时给socketChannel关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    //注：selector.keys()获取所有的channel
                    //注：selector.selectedKeys()只获取有事件发生的channel
                    System.out.println("客户端连接后 ，注册的selectionkey 数量=" + selector.keys().size()); //2,3,4..
                }
                if(key.isReadable()) {  //发生 OP_READ
                    //通过key 反向获取到对应channel
                    SocketChannel channel = (SocketChannel)key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    //将channel的数据读到buffer
                    channel.read(buffer);
                    System.out.println("form 客户端 " + new String(buffer.array()));
                }
                //手动从集合中删除当前的selectionKey, 防止重复操作
                keyIterator.remove();
            }
        }
    }
}
