package com.su.blog.aspect;

import com.alibaba.fastjson.JSON;
import com.su.blog.entity.User;
import com.su.blog.util.MD5Util;
import com.su.blog.util.Result.Result;
import com.su.blog.util.Result.Tips;
import com.su.blog.util.exception.AppRuntimeException;
import com.su.blog.util.redis.RedisKey;
import com.su.blog.util.redis.RedisOperator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//声明这是一个组件
@Component
//声明这是一个切面Bean
@Aspect
public class ControllerAspect {
    private Logger logger = LoggerFactory.getLogger(ControllerAspect.class);
    private long startTimeMillis;
    private long endTimeMillis;
    @Resource
    private RedisOperator redisOperator;

    //配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(* com.su.*.controller.*.*(..))" +
            "|| execution(* com.su.*.controller.*.*.*(..))" +
            "|| execution(* com.su.*.controller.*.*.*.*(..))")
    public void aspect() {
    }

    /*
     * 配置前置通知,使用在方法aspect()上注册的切入点
     * 同时接受JoinPoint切入点对象,可以没有该参数
     */
    @Before("aspect()")
    public void before(JoinPoint joinPoint) {
        if ("init".equals(joinPoint.getSignature().getName())) {
            return;
        }
        startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
        String params = getParams(joinPoint);
        logger.info("请求接口：【" + joinPoint.getSignature().getName() + "】");
        logger.info("请求参数：" + params);
    }

    //配置环绕通知,使用在方法aspect()上注册的切入点
    @Around("aspect()")
    public Object around(JoinPoint joinPoint) {
        Object o;
        try {
            o = ((ProceedingJoinPoint) joinPoint).proceed();
        } catch (AppRuntimeException e) {
            logger.error("AppRuntimeException：", e);
            o = Result.ResponseEntityError(e.getStatus(), e.getData());
        } catch (Throwable throwable) {
            o = Result.ResponseEntityError();
            logger.error("【" + joinPoint.getSignature().getName() + "】接口执行异常：", throwable);
        }
        try {
            if (o instanceof ResponseEntity) {
                ResponseEntity response = (ResponseEntity) o;
                endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
                long executeTimeMillis = endTimeMillis - startTimeMillis;

                logger.info("【" + joinPoint.getSignature().getName() + "】接口执行完毕,耗时：" + executeTimeMillis + "毫秒");
                String reqResult = JSON.toJSONString(response.getBody());
                logger.info("返回值：" + reqResult);

                //如果为登录接口,生成token返回并保存
                String params = getParams(joinPoint);
                if (params.contains("account") && params.contains("password") && response.getBody() instanceof User) {
                    User user = (User) response.getBody();
                    SimpleDateFormat dateShortFormat = new SimpleDateFormat("yyyyMMdd");
                    String token = Objects.requireNonNull(MD5Util.MD5(dateShortFormat.format(new Date()) + user.getUsername()));

                    //存放到缓存 todo token失效时间
                    Map<String, Object> userMap = objectToMap(user);
                    userMap.put("token", token);
                    String userInfo = JSON.toJSONString(userMap);
                    redisOperator.hset(RedisKey.USER_TOKEN.value(), token, userInfo);

                    //返回值也增加token
                    return new ResponseEntity(userMap, response.getStatusCode());
                }
                return response;
            }
        } catch (AppRuntimeException e) {
            logger.error("Exception：", e);
            return Result.ResponseEntityError(e.getStatus(), e.getData());
        } catch (Exception e) {
            logger.error("环绕切面错误Exception：", e);
            return Result.ResponseEntityError(HttpStatus.FORBIDDEN, Tips.ServerError);
        } catch (Throwable throwable) {
            logger.error("环绕切面错误Throwable：", throwable);
            return Result.ResponseEntityError(HttpStatus.FORBIDDEN, Tips.ServerError);
        }
        return o;
    }

    //配置抛出异常后通知,使用在方法aspect()上注册的切入点
    @AfterThrowing(pointcut = "aspect()", throwing = "ex")
    public ResponseEntity afterThrow(JoinPoint joinPoint, Exception ex) {

        //获取用户请求方法的参数并序列化为JSON格式字符串
        StringBuilder params = new StringBuilder();
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            for (int i = 0; i < joinPoint.getArgs().length; i++) {
                params.append(JSON.toJSON(joinPoint.getArgs()[i]) + ";");
            }
        }
        logger.error("异常代码:" + ex.getClass().getName() + "，异常信息:" + ex.getMessage());
        logger.error("异常方法:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
        logger.error("请求参数:" + params);
        return Result.ResponseEntityError(HttpStatus.FORBIDDEN);
    }

    //配置后置通知,使用在方法aspect()上注册的切入点
    @After("aspect()")
    public void after(JoinPoint joinPoint) {
        logger.info("after out successfully");
    }

    //配置后置返回通知,使用在方法aspect()上注册的切入点
    @AfterReturning("aspect()")
    public void afterReturn(JoinPoint joinPoint) {
    }

    private String getParams(JoinPoint joinPoint) {
        //获取用户请求方法的参数并序列化为JSON格式字符串
        StringBuilder params = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
            int argsLength = args.length;
            for (int i = 0; i < argsLength; i++) {
                if (!(args[i] instanceof HttpServletRequest) && !(args[i] instanceof HttpServletResponse) && !(args[i] instanceof MultipartFile[])) {
                    params.append(paramNames[i] + " ：" + JSON.toJSON(args[i]) + ";");
                }
            }
        }

        return params.toString();
    }

    private Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }

        return map;
    }


}
