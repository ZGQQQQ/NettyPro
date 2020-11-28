package com.atguigu.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {
        //创建一个ByteBuf
        //说明
        //1. 创建一个对象，该对象包含一个数组arr , 真实类型是一个byte[10]
        //2. 在NIO中：在向缓冲区存完数据后要读取前要进行反转操作flip
		//   在netty中：在向ByteBuf存完后进行获取时不需要使用flip进行反转，因为ByteBuf底层维护了readerIndex(下一个读取的位置)和writerIndex(下一个写入的位置)
        //3. 通过 readerIndex 和  writerIndex 和  capacity， 将buffer分成三个区域
        //     0 --- readerIndex   已经读取的区域
        //     readerIndex --- writerIndex   可读的区域
        //     writerIndex --- capacity   可写的区域
        ByteBuf buffer = Unpooled.buffer(10);

        for(int i = 0; i < 10; i++) {
        	//向netty的缓冲区ByteBuf写入数据
            buffer.writeByte(i);
        }

        System.out.println("capacity=" + buffer.capacity());//10
/**
       //输出
       for(int i = 0; i<buffer.capacity(); i++) {
		   //注：buffer.getByte(i)这种读取方式不会使readerIndex发送改变
           System.out.println(buffer.getByte(i));
       }
*/

		//读取netty缓冲区ByteBuf的数据
        for(int i = 0; i < buffer.capacity(); i++) {
			//读取netty的缓冲区ByteBuf中的数据
            System.out.println(buffer.readByte());
        }
        System.out.println("执行完毕");
    }
}
