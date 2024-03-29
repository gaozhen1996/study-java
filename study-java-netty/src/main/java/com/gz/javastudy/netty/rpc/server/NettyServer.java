package com.gz.javastudy.netty.rpc.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.logging.Logger;


public class NettyServer {
    public static  String SERVER_PATH = "/netty";

    private static final Logger logger = Logger.getLogger(NettyServer.class.getName());

    public static void start() {
        logger.info("netty server 启动 ");
        /**
         * 新建两个线程组，boss线程组启动一条线程监听OP_ACCEPT事件，
         * worker线程组默认启动cpu核数*2的线程,监听客户端连接
         * 的OP_READ和OP_WRITE事件,处理IO事件
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // ServerBootstrap为netty服务启动辅助类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            // 设置TCP socket通道为NioServerSocketChannel，
            // 假如是UDP通信的话，则设置为DatagramChannel
            serverBootstrap.channel(NioServerSocketChannel.class);
            //设置TCP参数
            serverBootstrap.option(ChannelOption.SO_BACKLOG,128)
            /**
             * 当有客户端注册写事件时，初始化Handle
             * 并将Handler加入管道中
             */
            .childHandler(new ChannelInitializer<SocketChannel>(){
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 新增，前缀为4个字节大小的int类型作为长度的解码器
                    // 第一个参数是包最大大小，第二个参数是长度值偏移量，
                    // 由于编码时长度值在最前面，无偏移，此处设置为0
                    // 第三个参数长度值占用的字节数，
                    // 第四个参数是长度值的调节，假如请求包的大小是20个字节，
                    // 长度值没包含本身的话应该是20，假如长度值包含了本身就是24，需要调整4个字节
                    // 第五个参数,解析时候需要跳过的字节长,此处为4，跳过长度值字节数
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    // 把接收到的Bytebuf数据包转换成String
                    ch.pipeline().addLast(new StringDecoder());
                    /**
                     * 往worker线程的管道双向链表中添加处理类ServerHandler,
                     * 整个处理流向：
                     * HeadContext-channelRead读数据-->ServerHandler-
                     * channelRead读取数据做业务逻辑判断，
                     * 最后写回结果给客户端-->TailContext-write->HeadContext-write
                     */
                    ch.pipeline().addLast(new ServerHandler());
                    // 在消息体前面新增4个字节的长度值,第一个参数长度值字节数大小，
                    // 第二个参数长度值是否要包含长度值本身大小
                    ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                    // 把字符串消息转换成ByteBuf
                    ch.pipeline().addLast(new StringEncoder());
                    // 注意，解码器和编码器的顺序，
                    // 执行顺序正好相反，解码器执行顺序从上往下，
                    // 编码器执行顺序从下往上
                }
            });

            //同步绑定端口
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            //阻塞主线程，直到Socket通道关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 最终关闭线程组
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}

