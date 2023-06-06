package com.dogsong.core.context;

import com.dogsong.common.config.Rule;
import io.netty.channel.ChannelHandlerContext;

/**
 * 网关核心上下文类
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/6
 */
public class GatewayContext extends BasicContext {


    private Rule rule;

    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        super(protocol, nettyCtx, keepAlive);
    }

}
