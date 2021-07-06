package chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerExecutors = new NioEventLoopGroup(8);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerExecutors)
                .channel(NioServerSocketChannel.class) // 设置服务器的通道
                .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
//                .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                .childHandler(new ChannelInitializer<SocketChannel>() { // 设置通道测试对象(匿名对象)
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //向pipeline加入解码器
                        pipeline.addLast("decoder", new StringDecoder());
                        //向pipeline加入编码器
                        pipeline.addLast("encoder", new StringEncoder());
                        // 给pipeline添加一个handler
                        socketChannel.pipeline().addLast(new ChatServerHandler());
                    }
                });

        ChannelFuture sync = serverBootstrap.bind(8989).sync();
        sync.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("服务器绑定8989端口成功");
            }
        });
    }
}