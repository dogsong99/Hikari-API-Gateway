package com.dogsong.core.context;

import com.dogsong.common.config.Rule;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * 核心上下文接口定义, 主要分3部分:
 * <p>
 * 1.上下文生命周期相关:
 *  1.1 定义状态
 *  1.2 状态流转方法
 *  1.3 判断状态方法
 *
 * 2. 获取 转换协议，请求对象，响应对象，异常
 *
 * 3.设置 响应对象，异常
 *
 * 主要用到的就是这些，后面有其它的再继续扩展
 * </p>
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/5/31
 */
public interface IContext {

    /**
     * 一个请求正在执行中的状态
     */
    int RUNNING = 0;

    /**
     * 标志请求结束, 写回Response
     * 比如运行完过滤器过程中，不一定哪一个出错，这时我们需要进行标记，告诉我们请求以及结束，需要返回客户端
     */
    int WRITTEN = 1;

    /**
     * 写回成功后,设置该标识,如果是Netty, ctx.WriteAndFlush(response)
     * 防止并发下的多次标记写回
     */
    int COMPLETED = 2;

    /**
     * 整个网关请求完毕,彻底结束
     */
    int TERMINATED = -1;


    // 设置运行状态

    /**
     * <B>概要说明：</B>设置上下文状态为正常运行状态<BR>
     */
    void setRunning();

    /**
     * <B>概要说明：</B>设置上下文状态为标记写回<BR>
     */
    void setWritten();

    /**
     * <B>概要说明：</B>设置上下文状态为写回结束<BR>
     */
    void setCompleted();

    /**
     * <B>概要说明：</B>设置上下文状态为最终结束<BR>
     */
    void setTerminated();

    /**
     * 判断运行状态
     */
    boolean isRunning();

    boolean isWritten();

    boolean isCompleted();

    boolean isTerminated();

    /**
     * <B>概要说明：</B>获取请求转换协议<BR>
     */
    String getProtocol();

    /**
     * 获取请求转换规则
     */
    Rule getRule();

    /**
     * <B>概要说明：</B>获取请求对象<BR>
     */
    Object getRequest();

    /**
     * 获取请求结果
     */
    Object getResponse();

    /**
     * 获取异常信息
     */
    Throwable getThrowable();

    /**
     * 获取上下文参数
     */
    <T> T getAttribute(String key);

    <T> T putAttribute(String key, T value);


    /**
     * 设置请求规则
     */
    void setRule();

    /**
     * 设置请求返回结果
     */
    void setResponse(Object response);

    /**
     * 设置异常信息
     */
    void setThrowable(Throwable throwable);

    /**
     * 设置上下文参数
     */
    void setAttribute(String key, Object obj);

    /**
     * 获取 netty 上下文参数
     */
    ChannelHandlerContext getNettyCtx();

    /**
     * 是否保持连接
     */
    boolean isKeepAlive();

    /**
     * 释放资源
     */
    void releaseRequest();

    /**
     * 设置回调函数
     */
    void setCompletedCallBack(Consumer<IContext> consumer);

    /**
     * 调用回调函数
     */
    void invokeCompletedCallBack(Consumer<IContext> consumer);

}
