package com.dogsong.core.request;

import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;

/**
 * 网关请求接口
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/6
 */
public interface IGatewayRequest {

    /**
     * 修改域名
     *
     * @param modifyHost 域名
     */
    void setModifyHost(String modifyHost);

    /**
     * 获取域名
     */
    String getModifyHost();

    /**
     * 设置路径
     *
     * @param path 路径
     */
    void setModifyPath(String modifyPath);

    /**
     * 获取路径
     */
    String getModifyPath();

    /**
     * 添加请求头信息
     *
     * @param name name
     * @param value value
     */
    void addHeader(CharSequence name, String value);

    /**
     * 设置请求头信息
     *
     * @param name name
     * @param value value
     */
    void setHeader(CharSequence name, String value);

    /**
     * 添加 Get 请求参数
     *
     * @param name name
     * @param value value
     */
    void addQueryParam(String name, String value);

    /**
     * 添加 Post 请求参数
     *
     * @param name name
     * @param value value
     */
    void addFormParam(String name, String value);

    /**
     * 添加或者替换Cookie
     *
     * @param cookie cookie
     */
    void addOrReplaceCookie(Cookie cookie);

    /**
     * 设置请求超时时间
     *
     * @param requestTimeout 超时时间
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * 获取最终的请求路径
     */
    String getFinalUrl();

    /**
     * 构造最终的请求对象
     */
    Request build();

}
