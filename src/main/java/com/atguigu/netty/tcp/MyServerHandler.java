package com.atguigu.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<ByteBuf>{
    private int count;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //cause.printStackTrace();
        ctx.close();
    }

    /**
	 * 接收客户端发来的数据
	 * */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        //将buffer转成字符串
        String message = new String(buffer, Charset.forName("utf-8"));
		//将客户端发送的多条数据一次性接收，有可能出现粘包现象，服务端会一次性全部读取；也可能分几次读取
        System.out.println("服务器接收到的数据 " + message);
		//因为客户端分了多次向服务端发送数据报，这里输出看一下服务端收了几次
        System.out.println("服务器接收到消息量=" + (++this.count));
        //服务器回送数据给客户端, 回送一个随机id
        ByteBuf responseByteBuf = Unpooled.copiedBuffer("<"+UUID.randomUUID().toString() + ">", Charset.forName("utf-8"));
        ctx.writeAndFlush(responseByteBuf);
    }
}
