package com.dogsong.core.request;

import com.dogsong.common.constants.BasicConst;
import com.dogsong.common.utils.TimeUtil;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.cookie.Cookie;

import java.nio.charset.Charset;
import java.util.*;

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

    /** 请求进入网关时间 */
    @Getter
    private final long beginTime;

    /** 字符集 */
    @Getter
    private final Charset charset;

    /** 客户端的IP，主要用于做流控、黑白名单 */
    @Getter
    private final String clientIp;

    /** 请求的地址：ip:port */
    @Getter
    private final String host;

    /** 请求的路径 /XXX/XXX/XX */
    @Getter
    private final String path;

    /**
     * URI：统一资源标识符，/XXX/XXX/XXX?attr1=value&attr2=value2
     * URL：统一资源定位符，它只是URI的子集一个实现
     */
    @Getter
    private final String uri;

    /**
     * 请求方法 post/put/GET
     */
    @Getter
    private final HttpMethod method;

    /** 请求的格式 */
    @Getter
    private final String contentType;

    /** 请求头信息 */
    @Getter
    private final HttpHeaders headers;

    /** 参数解析器 */
    @Getter
    private final QueryStringDecoder queryStringDecoder;

    /** FullHttpRequest */
    @Getter
    private final FullHttpRequest fullHttpRequest;

    /** 请求体 */
    private String body;

    /** 请求Cookie */
    @Getter
    private Map<String, io.netty.handler.codec.http.cookie.Cookie> cookieMap;

    /** post请求定义的参数结合 */
    @Getter
    private Map<String, List<String>> postParameters;

    /**
     * 可修改的Scheme，默认是http://
     *
     */
    private String modifyScheme;

    /** 服务ID */
    private String modifyHost;

    /** 服务ID */
    private String modifyPath;

    /** 构建下游请求是的http请求构建器 */
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

    public String getBody() {
        if (StringUtils.isEmpty(body)) {
            body = fullHttpRequest.content().toString(charset);
        }
        return body;
    }

    /**
     * 获取Cookie
     *
     * @param name name
     */
    public io.netty.handler.codec.http.cookie.Cookie getCookie(String name) {
        if (cookieMap == null) {
            cookieMap = new HashMap<>();
            String cookieStr = getHeaders().get(HttpHeaderNames.COOKIE);
            Set<io.netty.handler.codec.http.cookie.Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            cookies.forEach(cookie -> cookieMap.put(name, cookie));
        }
        return cookieMap.get(name);
    }

    /**
     * 获取指定名词参数值
     *
     * @param name key
     */
    public List<String> getQueryParametersMultiples(String name) {
        return queryStringDecoder.parameters().get(name);
    }

    /**
     * 获取指定名词参数值
     *
     * @param name key
     */
    public List<String> getPostParametersMultiples(String name) {
        String body = getBody();
        if (isFormPost()) {
            if (postParameters == null) {
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body, false);
                postParameters = paramDecoder.parameters();
            }
            if (postParameters == null || postParameters.isEmpty()) {
                return Collections.emptyList();
            }
            return postParameters.get(name);
        } else if (isJsonPost()) {
            try {
                return Lists.newArrayList(JsonPath.read(body, name).toString());
            } catch (Exception e) {
                log.error("JsonPath解析失败，JsonPath:{},Body:{},",name,body,e);
            }
        }
        return Collections.emptyList();
    }

    public  boolean isFormPost(){
        return HttpMethod.POST.equals(method) &&
                (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()) ||
                        contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                );
    }

    public  boolean isJsonPost(){
        return HttpMethod.POST.equals(method) &&
                contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString());
    }

    /**
     * 修改域名
     *
     * @param modifyHost 域名
     */
    @Override
    public void setModifyHost(String modifyHost) {
        this.modifyHost = modifyHost;
    }

    /**
     * 获取域名
     */
    @Override
    public String getModifyHost() {
        return modifyHost;
    }

    /**
     * 设置路径
     *
     * @param modifyPath 路径
     */
    @Override
    public void setModifyPath(String modifyPath) {
        this.modifyPath = modifyPath;
    }

    /**
     * 获取路径
     */
    @Override
    public String getModifyPath() {
        return modifyPath;
    }

    /**
     * 添加请求头信息
     *
     * @param name  name
     * @param value value
     */
    @Override
    public void addHeader(CharSequence name, String value) {
        requestBuilder.addHeader(name, value);
    }

    /**
     * 设置请求头信息
     *
     * @param name  name
     * @param value value
     */
    @Override
    public void setHeader(CharSequence name, String value) {
        requestBuilder.setHeader(name, value);
    }

    /**
     * 添加 Get 请求参数
     *
     * @param name  name
     * @param value value
     */
    @Override
    public void addQueryParam(String name, String value) {
        requestBuilder.addQueryParam(name, value);
    }

    /**
     * 添加 Post 请求参数
     *
     * @param name  name
     * @param value value
     */
    @Override
    public void addFormParam(String name, String value) {
        if (isFormPost()){
            requestBuilder.addFormParam(name, value);
        }
    }

    /**
     * 添加或者替换Cookie
     *
     * @param cookie cookie
     */
    @Override
    public void addOrReplaceCookie(Cookie cookie) {
        requestBuilder.addOrReplaceCookie(cookie);
    }

    /**
     * 设置请求超时时间
     *
     * @param requestTimeout 超时时间
     */
    @Override
    public void setRequestTimeout(int requestTimeout) {
        requestBuilder.setRequestTimeout(requestTimeout);
    }

    /**
     * 获取最终的请求路径
     */
    @Override
    public String getFinalUrl() {
        return modifyScheme + modifyHost + modifyPath;
    }

    /**
     * 构造最终的请求对象
     */
    @Override
    public Request build() {
        requestBuilder.setUrl(getFinalUrl());
        return requestBuilder.build();
    }
}
