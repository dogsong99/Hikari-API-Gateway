package com.dogsong.core.request;

import com.dogsong.common.constants.BasicConst;
import com.dogsong.common.utils.TimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.cookie.Cookie;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 网关请求类
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/7
 */
@Slf4j
public class GatewayRequest implements IGatewayRequest {

    /** 服务ID */
    @Getter
    private final String uniqueId;

    @Getter
    private final long beginTime;

    @Getter
    private final Charset charset;

    @Getter
    private final String clientIp;

    @Getter
    private final String host;

    @Getter
    private final String path;

    @Getter
    private final String uri;

    @Getter
    private final HttpMethod method;

    @Getter
    private final String contentType;

    @Getter
    private final HttpHeaders headers;

    @Getter
    private final QueryStringDecoder queryStringDecoder;

    @Getter
    private final FullHttpRequest fullHttpRequest;

    @Getter
    private String body;

    @Getter
    private Map<String, io.netty.handler.codec.http.cookie.Cookie> cookieMap;

    @Getter
    private Map<String, List<String>> postParameters;

    @Getter
    private String modifyScheme;

    @Getter
    private String modifyHost;

    @Getter
    private String modifyPath;

    /** 构建下游请求是的http请求构建器 */
    @Getter
    private final RequestBuilder requestBuilder;

    public GatewayRequest(String uniqueId, Charset charset,
                          String clientIp, String host, String path,
                          String uri, HttpMethod method, String contentType, HttpHeaders headers,
                          QueryStringDecoder queryStringDecoder, FullHttpRequest fullHttpRequest) {
        this.uniqueId = uniqueId;
        this.beginTime = TimeUtil.currentTimeMillis();
        this.charset = charset;
        this.clientIp = clientIp;
        this.host = host;
        this.uri = uri;
        this.method = method;
        this.contentType = contentType;
        this.headers = headers;
        this.fullHttpRequest = fullHttpRequest;
        this.queryStringDecoder = new QueryStringDecoder(uri,charset);
        this.path  = queryStringDecoder.path();

        this.modifyHost = host;
        this.modifyPath = path;
        this.modifyScheme = BasicConst.HTTP_PREFIX_SEPARATOR;

        this.requestBuilder = new RequestBuilder();
        this.requestBuilder.setMethod(getMethod().name());
        this.requestBuilder.setHeaders(getHeaders());
        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());

        ByteBuf contentBuffer = fullHttpRequest.content();
        if(Objects.nonNull(contentBuffer)){
            this.requestBuilder.setBody(contentBuffer.nioBuffer());
        }
    }


    /**
     * 修改域名
     *
     * @param host 域名
     */
    @Override
    public void setModifyHost(String host) {

    }

    /**
     * 获取域名
     */
    @Override
    public String getModifyHost() {
        return null;
    }

    /**
     * 设置路径
     *
     * @param path 路径
     */
    @Override
    public void setModifyPath(String path) {

    }

    /**
     * 获取路径
     */
    @Override
    public String getModifyPath() {
        return null;
    }

    /**
     * 添加请求头信息
     *
     * @param name  name
     * @param value value
     */
    @Override
    public void addHeader(CharSequence name, String value) {

    }

    /**
     * 设置请求头信息
     *
     * @param name  name
     * @param value value
     */
    @Override
    public void setHeader(CharSequence name, String value) {

    }

    /**
     * 添加 Get 请求参数
     *
     * @param name  name
     * @param value value
     */
    @Override
    public void addQueryParam(String name, String value) {

    }

    /**
     * 添加 Post 请求参数
     *
     * @param name  name
     * @param value value
     */
    @Override
    public void addFormParam(String name, String value) {

    }

    /**
     * 添加或者替换Cookie
     *
     * @param cookie cookie
     */
    @Override
    public void addOrReplaceCookie(Cookie cookie) {

    }

    /**
     * 设置请求超时时间
     *
     * @param requestTimeout 超时时间
     */
    @Override
    public void setRequestTimeout(int requestTimeout) {

    }

    /**
     * 获取最终的请求路径
     */
    @Override
    public String getFinalUrl() {
        return null;
    }

    /**
     * 构造最终的请求对象
     */
    @Override
    public Request build() {
        return null;
    }
}
