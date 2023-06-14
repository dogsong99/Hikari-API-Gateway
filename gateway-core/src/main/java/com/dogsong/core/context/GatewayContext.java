package com.dogsong.core.context;

import com.dogsong.common.config.Rule;
import com.dogsong.common.utils.AssertUtil;
import com.dogsong.core.request.GatewayRequest;
import com.dogsong.core.response.GatewayResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 网关核心上下文类
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/6
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class GatewayContext extends BasicContext {

    private final GatewayRequest request;

    private GatewayResponse response;

    private final Rule rule;

    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive,
                          GatewayRequest request, Rule rule) {
        super(protocol, nettyCtx, keepAlive);
        this.request = request;
        this.rule = rule;
    }

    /**
     * 获取必要的上下文参数，如果没有则抛出IllegalArgumentException
     *
     * @param key key
     */
    public <T> T getRequiredAttribute(String key) {
        T value = getAttribute(key);
        AssertUtil.notNull(value, "required attribute '" + key + "' is missing !");
        return value;
    }

    /**
     * 获取指定key的上下文参数，如果没有则返回第二个参数的默认值
     *
     * @param key key
     * @param defaultValue defaultValue
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttributeOrDefault(String key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    /**
     * 根据过滤器id获取对应的过滤器配置信息
     *
     * @param filterId filterId
     */
    public Rule.FilterConfig getFilterConfig(String filterId) {
        return rule.getFilterConfig(filterId);
    }

    /**
     * 获取上下文中唯一的UniqueId
     */
    public String getUniqueId() {
        return request.getUniqueId();
    }

    /**
     * 重写覆盖父类：basicContext的该方法，主要用于真正的释放操作
     */
    @Override
    public void releaseRequest() {
        if(requestReleased.compareAndSet(false, true)) {
            ReferenceCountUtil.release(request.getFullHttpRequest());
        }
    }

    /**
     * 设置请求返回结果
     */
    @Override
    public void setResponse(Object response) {
        this.response = (GatewayResponse) response;
    }

}
