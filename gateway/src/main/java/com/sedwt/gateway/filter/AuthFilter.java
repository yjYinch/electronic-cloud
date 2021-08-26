package com.sedwt.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sedwt.common.constant.Constants;
import com.sedwt.common.constant.ErrorCodeConstants;
import com.sedwt.common.entity.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static com.sedwt.common.constant.Constants.ACCESS_TOKEN;
import static com.sedwt.common.constant.Constants.DOWNLOAD_URL;
import static com.sedwt.common.constant.Constants.LOGIN_URL;
import static com.sedwt.common.constant.Constants.REGISTER_URL;
import static com.sedwt.common.constant.Constants.TOKEN;

/**
 * @author : yj zhang
 * @since : 2021/8/25 13:42
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);
    /**
     * 不需要过滤的接口：只允许登录和注册接口不用带token，其它接口必须要带token,以防止恶意的请求
     */
    private static final List<String> NOT_REQUIRED_AUTHENTICATE_URLS = Arrays.asList(LOGIN_URL, REGISTER_URL);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        // 过滤不需要token的路径
        if (NOT_REQUIRED_AUTHENTICATE_URLS.contains(path)) {
            LOGGER.info("请求路径path = {} 不需要过滤，直接转发下一级", path);
            return chain.filter(exchange);
        }
        //----------------------------token校验过程------------------------------------
        // 1. 获取token
        String token = request.getHeaders().getFirst(TOKEN);
        if (StringUtils.isBlank(token)) {
            return unAuthorizedResponse(exchange, "token为空");
        }
        // 2. 校验token
        String user = redisTemplate.opsForValue().get(ACCESS_TOKEN + token);
        if (StringUtils.isEmpty(user)) {
            return unAuthorizedResponse(exchange, "token不存在或已过期");
        }
        // 3. 根据token获取用户id
        String userId;
        String loginName;
        try {
            JSONObject jsonObject = JSON.parseObject(user);
            userId = jsonObject.getString("userId");
            loginName = jsonObject.getString("loginName");
        } catch (Exception e) {
            LOGGER.error("获取userId异常，原因：{}", e.getMessage());
            return unAuthorizedResponse(exchange, "token失效");
        }
        // 将userId、loginName设置到request header
        ServerHttpRequest mutableReq = exchange.getRequest()
                .mutate()
                .header(Constants.CURRENT_ID, userId)
                .header(Constants.CURRENT_USERNAME, loginName)
                .build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return chain.filter(mutableExchange);
    }


    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 设置鉴权失败响应
     * @param exchange
     * @param message
     * @return
     */
    private Mono<Void> unAuthorizedResponse(ServerWebExchange exchange, String message){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        try {
            response.bufferFactory().wrap(JSON.toJSONBytes(R.error(ErrorCodeConstants.AUTHENTICATION_FAILED, message)));
        } catch (Exception e) {
            LOGGER.error("响应异常，执行写入JSON失败", e);
        }
        return response.setComplete();
    }
}
