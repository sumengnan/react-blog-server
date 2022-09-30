package com.su.blog.util.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.su.blog.util.ParamUtil;
import com.su.blog.util.Result.Result;
import com.su.blog.util.Result.Tips;
import com.su.blog.util.redis.RedisKey;
import com.su.blog.util.redis.RedisOperator;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Component
public class AuthFilter implements Filter {
    ResponseEntity failResult = Result.ResponseEntityError(Tips.AuthError);
    @Resource
    private RedisOperator redisOperator;

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

        //打印日志追踪
        MDC.put("x_global_session_id", UUID.randomUUID().toString());
        MDC.put("request_type", request.getMethod());

        //放行跨域请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
        } else {
            String type = request.getHeader("Content-Type");
            if (!"GET".equals(request.getMethod()) && !"POST".equals(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!"GET".equalsIgnoreCase(request.getMethod()) && type != null && type.contains("application/json")) {
                // 防止流读取一次后就没有了, 所以需要将流继续写出去,之后使用requestWrapper
                request = new BodyReaderHttpServletRequestWrapper(request);
            }
            Map<String, Object> paramMap = ParamUtil.getParams(request);
            if (paramMap.isEmpty()) {
                filterChain.doFilter(request, response);
            } else {
                String token = request.getHeader("token");
                String requestKey = request.getHeader("requestKey");
                //判断是否为web端请求而来
                if (requestKey != null && "web".equals(requestKey)) {
                    if (token != null && !"".equals(token) && !"undefined".equals(token)) {
                        String userInfo = redisOperator.hget(RedisKey.USER_TOKEN.value(), token);
                        JSONObject user = JSONObject.parseObject(userInfo);
                        if (user == null || user.isEmpty()) {
                            send(request, response, failResult);
                        }
                    } else {
                        if (!"user/login".equals(requestURI)) {
                            send(request, response, failResult);
                        }
                    }
                }
                filterChain.doFilter(request, response);
            }

            //移除日志追踪
            MDC.remove("x_global_session_id");
            MDC.remove("request_type");
        }

    }

    @Override
    public void destroy() {

    }

    private void send(HttpServletRequest request, HttpServletResponse response, Object result) throws IOException {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(result));
        response.getWriter().flush();
        response.getWriter().close();
    }

}
