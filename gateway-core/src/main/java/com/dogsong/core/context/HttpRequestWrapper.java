package com.dogsong.core.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

/**
 * HttpRequestWrapper
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/7/23
 */
@Data
public class HttpRequestWrapper {

    private FullHttpRequest request;

    private ChannelHandlerContext ctx;

}
