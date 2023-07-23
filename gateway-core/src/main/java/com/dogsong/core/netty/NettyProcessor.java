package com.dogsong.core.netty;

import com.dogsong.core.context.HttpRequestWrapper;

/**
 * NettyProcessor
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/7/23
 */
public interface NettyProcessor {


    void process(HttpRequestWrapper wrapper);

}
