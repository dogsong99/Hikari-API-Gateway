package com.dogsong.core.netty;

import com.dogsong.common.utils.RemotingUtil;
import com.dogsong.core.Config;
import com.dogsong.core.LifeCycle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * TODO
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/7/23
 */
@Slf4j
public class NettyHttpServer implements LifeCycle {

    private final Config config;

    private final NettyProcessor nettyProcessor;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroupBoss;

    private EventLoopGroup eventLoopGroupWorker;

    public NettyHttpServer(Config config, NettyProcessor nettyProcessor) {
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        init();
    }

    /**
     * 初始化
     */
    @Override
    public void init() {
        this.serverBootstrap = new ServerBootstrap();
        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(
                    config.getEventLoopGroupBossNum(), new DefaultThreadFactory("netty-boss-nio")
            );
            this.eventLoopGroupWorker = new EpollEventLoopGroup(
                    config.getEventLoopGroupWorkerNum(), new DefaultThreadFactory("netty-worker-nio")
            );
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(
                    config.getEventLoopGroupBossNum(), new DefaultThreadFactory("netty-boss-nio")
            );
            this.eventLoopGroupWorker = new NioEventLoopGroup(
                    config.getEventLoopGroupWorkerNum(), new DefaultThreadFactory("netty-worker-nio")
            );
        }

    }

    public boolean useEpoll() {
        return RemotingUtil.isLinuxPlatform() && Epoll.isAvailable();
    }

    /**
     * 启动
     */
    @Override
    public void start() {
        this.serverBootstrap.group(eventLoopGroupBoss, eventLoopGroupWorker)
                .channel(useEpoll()? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(config.getPort()))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(
                                new HttpServerCodec(),
                                new HttpObjectAggregator(config.getMaxContentLength()),
                                new NettyServerConnectManagerHandler(),
                                new NettyHttpServerHandler(nettyProcessor)
                        );
                    }
                });
        try {
            this.serverBootstrap.bind().sync();
            log.info("server startup on port {}", this.config.getPort());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭
     */
    @Override
    public void shutdown() {
        if (eventLoopGroupBoss != null) {
            eventLoopGroupBoss.shutdownGracefully();
        }
        if (eventLoopGroupWorker != null) {
            eventLoopGroupWorker.shutdownGracefully();
        }
    }
}
