package com.atguigu.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * 这里的泛型TextWebSocketFrame，表示一个文本帧(frame)
 * 客户端与服务端以TextWebSocketFrame的形式就行数据交互
 */
public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		//输出客户端发送过来的消息
		System.out.println("服务器收到消息 " + msg.text());
		//向客户端回复消息
		ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间" + LocalDateTime.now() + " " + msg.text()));
	}

	/**
	 * 当web客户端连接后，会触发该方法
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		//channel的id是唯一的；LongText也是唯一的；
		System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
        //ShortText不是唯一的
		System.out.println("handlerAdded 被调用" + ctx.channel().id().asShortText());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("handlerRemoved 被调用" + ctx.channel().id().asLongText());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("异常发生 " + cause.getMessage());
		ctx.close(); //关闭连接
	}
}
