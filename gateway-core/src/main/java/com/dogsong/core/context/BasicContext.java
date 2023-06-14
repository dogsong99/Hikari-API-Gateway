package com.dogsong.core.context;

import com.dogsong.common.config.Rule;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 核心上下文基础类
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/6
 */
public abstract class BasicContext implements IContext {

    /** 转发协议, 例如http或dubbo等，因为是一开始就决定了，所以加上final */
    protected final String protocol;

    /** 上下文状态, 因为后面可能涉及到多线程共享，所以加上volatile关键字 */
    protected volatile int status  = IContext.RUNNING;

    /** Netty上下文 */
    protected final ChannelHandlerContext nettyCtx;

    /** 上下文参数集合 */
    protected final Map<String, Object> attributes = new HashMap<>();

    /** 请求过程中发生的异常 */
    protected Throwable throwable;

    /** 是否保持长连接 */
    protected final boolean keepAlive;

    /** 是否已经释放资源 */
    protected final AtomicBoolean requestReleased = new AtomicBoolean(false);

    /** 存放回调函数的集合 */
    protected List<Consumer<IContext>> completedCallbacks;

    public BasicContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        this.protocol = protocol;
        this.nettyCtx = nettyCtx;
        this.keepAlive = keepAlive;
    }


    /**
     * 设置运行状态
     */
    @Override
    public void setRunning() {
        status = IContext.RUNNING;
    }

    @Override
    public void setWritten() {
        status = IContext.WRITTEN;
    }

    @Override
    public void setCompleted() {
        status = IContext.COMPLETED;
    }

    @Override
    public void setTerminated() {
        status = IContext.TERMINATED;
    }

    /**
     * 判断运行状态
     */
    @Override
    public boolean isRunning() {
        return status == IContext.RUNNING;
    }

    @Override
    public boolean isWritten() {
        return status == IContext.WRITTEN;
    }

    @Override
    public boolean isCompleted() {
        return status == IContext.COMPLETED;
    }

    @Override
    public boolean isTerminated() {
        return status == IContext.TERMINATED;
    }

    /**
     * 获取请求转换协议
     */
    @Override
    public String getProtocol() {
        return this.protocol;
    }

    /**
     * 获取请求转换规则
     */
    @Override
    public Rule getRule() {
        return null;
    }

    /**
     * 获取请求对象
     */
    @Override
    public Object getRequest() {
        return null;
    }

    /**
     * 获取请求结果
     */
    @Override
    public Object getResponse() {
        return null;
    }

    /**
     * 获取异常信息
     */
    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    /**
     * 获取上下文参数
     *
     * @param key key
     */
    @Override
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    @Override
    public <T> T putAttribute(String key, T value) {
        return (T) attributes.put(key, value);
    }

    /**
     * 设置请求规则
     */
    @Override
    public void setRule() {

    }

    /**
     * 设置异常信息
     *
     * @param throwable throwable
     */
    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * 设置上下文参数
     *
     * @param key key
     * @param obj vel
     */
    @Override
    public void setAttribute(String key, Object obj) {
        attributes.put(key, obj);
    }

    /**
     * 获取 netty 上下文参数
     */
    @Override
    public ChannelHandlerContext getNettyCtx() {
        return nettyCtx;
    }

    /**
     * 是否保持连接
     */
    @Override
    public boolean isKeepAlive() {
        return keepAlive;
    }

    /**
     * 释放资源
     */
    @Override
    public void releaseRequest() {
        requestReleased.compareAndSet(false,true);
    }

    /**
     * 设置回调函数
     *
     * @param consumer
     */
    @Override
    public void setCompletedCallBack(Consumer<IContext> consumer) {
        if (completedCallbacks == null) {
            completedCallbacks = new ArrayList<>();
        }
        completedCallbacks.add(consumer);
    }

    /**
     * 调用回调函数
     *
     * @param consumer
     */
    @Override
    public void invokeCompletedCallBack(Consumer<IContext> consumer) {
        if (completedCallbacks == null) {
            return;
        }
        completedCallbacks.forEach(call -> call.accept(this));
    }
}
