package com.atguigu.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

	//public static List<Channel> channels = new ArrayList<Channel>();


	//定义一个channel组，管理所有的channel(一个channel对应一个客户端)
	//(GlobalEventExecutor.INSTANCE) 是全局的事件执行器，是一个单例
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	/**
	 * handlerAdded 表示连接建立，一旦连接，该方法第一个被执行
	 * 该方法的作用：
	 *        1-当有客户端来连接服务器时，提示channel组内的其他客户端有新的用户加入聊天；
	 *        2-当有客户端来连接服务器时，将当前客户端对应的channel加入到channel组channelGroup；
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		//将该客户加入聊天的信息推送给其它在线的客户端
		//writeAndFlush方法会将 channelGroup 中所有的channel遍历，并发送消息，我们不需要自己遍历channel组
		channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天" + sdf.format(new java.util.Date()) + " \n");
		//将客户端对应的channel加入到channel组
		channelGroup.add(channel);
	}

	/**
	 * 该方法的作用：当有客户端断开连接, 将断开连接客户离开信息推送给当前在线的客户
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开了\n");
		System.out.println("channelGroup size" + channelGroup.size());

	}

	/**
	 * 该方法的作用：当有客户端上线，在服务端提示某客户端上线
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + " 上线了~");
	}

	/**
	 * 该方法的作用：当有客户端下线，在服务端提示某客户端下线
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + " 离线了~");
	}

	/**
	 * 该方法的作用：当有客户端发送消息时，将消息转发给其他客户端
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		//获取到当前channel
		Channel channel = ctx.channel();
		//这时我们遍历channelGroup, 根据不同的情况，回送不同的消息
		channelGroup.forEach(ch -> {
			if (channel != ch) { //若不是消息发送者自己的channel,则发送如下消息
				ch.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息" + msg + "\n");
			} else {//若是自己的channel，则发送如下消息
				ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//关闭通道
		ctx.close();
	}
}
