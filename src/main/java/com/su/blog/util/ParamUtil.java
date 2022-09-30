package com.su.blog.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamUtil {

    protected static Logger logger = LoggerFactory.getLogger(new ParamUtil().getClass());

    public static Map<String, Object> format(String json) {
        List<Integer> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        JSONArray jsonArray = JSONArray.parseArray(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(Integer.parseInt(jsonArray.get(i).toString()));
            builder.append(jsonArray.get(i).toString()).append(",");
        }
        map.put("format", builder.deleteCharAt(builder.length() - 1));
        map.put("list", list);
        return map;
    }


    /**
     * 判断参数是否存在为空的
     *
     * @param objects
     * @return 存在：true；不存在：false
     */
    public static boolean isOneEmpty(Object... objects) {
        boolean flag = false;
        for (Object o : objects) {
            if (o == null) {
                flag = true;
                break;
            }
            if (o instanceof String) {
                if ("".equals(o)) {
                    flag = true;
                    break;
                }
            } else if (o instanceof Integer) {
                if (Integer.parseInt(String.valueOf(o)) < 0) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 判断参数是否全部为空
     *
     * @param objects
     * @return 是：true；不是：false
     */
    public static boolean isAllEmpty(Object... objects) {
        int count = objects.length;
        for (Object o : objects) {
            if (o == null) {
                count = count - 1;
            }
            if (o instanceof String) {
                if ("".equals(o)) {
                    count = count - 1;
                }
            } else if (o instanceof Integer) {
                if (Integer.parseInt(String.valueOf(o)) <= 0) {
                    count = count - 1;
                }
            }
        }
        return count == 0;

    }


    public static boolean isEmpty(String value) {
        if (value == null || "".equals(value)) {
            return true;
        } else if (value.trim() == null || "".equals(value.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断参数是否为int型
     *
     * @param objects
     * @return 不是：true；是：false
     */
    public static boolean isNotInteger(Object... objects) {
        boolean flag = false;
        for (Object o : objects) {
            try {
                Integer.parseInt(String.valueOf(o));
            } catch (Exception e) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断参数是否为日期格式
     *
     * @param objects
     * @return 不是：true；是：false
     */
    public static boolean isNotDate(Object... objects) {
        boolean flag = false;
        for (Object o : objects) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.parse(String.valueOf(o));
            } catch (Exception e) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断参数是否为时间格式
     *
     * @param objects
     * @return 不是：true；是：false
     */
    public static boolean isNotDateTime(Object... objects) {
        boolean flag = false;
        for (Object o : objects) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.parse(String.valueOf(o));
            } catch (Exception e) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static Map getPageRows(String page, String pageSize) {
        Map map = new HashMap();
        int page_int = Integer.parseInt(page);
        int pageSize_int = Integer.parseInt(pageSize);

        int startNum = (page_int - 1) * pageSize_int;
        int endNum = page_int * pageSize_int;

        map.put("startNum", startNum);
        map.put("endNum", endNum);
        return map;
    }

    /**
     * 获取request参数
     *
     * @param request http请求信息
     * @return 参数map
     */
    public static Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, Object> params = new HashMap<>();
        String type = request.getHeader("Content-Type");
        if ("GET".equalsIgnoreCase(request.getMethod()) || !type.contains("application/json")) {
            parameters = request.getParameterMap();
            int len;
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                len = entry.getValue().length;
                if (len == 1) {
                    params.put(entry.getKey(), entry.getValue()[0]);
                } else if (len > 1) {
                    params.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            try {
                ServletInputStream inputStream = request.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line + "\n");
                }
                String strContent = content.toString();
                params = (Map<String, Object>) JSON.parse(strContent);
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(ParamUtil.class);
                logger.error("getParams异常", e);
            }
        }
        return params;
    }

    /**
     * 校验特殊字符
     *
     * @return true:有特殊字符
     */
    public static boolean isSpecialChar(String str) {
//         Pattern regex = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]{0,20}");
//        Pattern regex = Pattern.compile("^[\\u4e00-\\u9fa5_a-zA-Z0-9]{0,20}+$");
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 校验特殊字符
     *
     * @return true:格式正确
     */
    public static boolean isMobileFormat(String mobile) {
        String regex = "^(1[3-9]\\d{9}$)";
//        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
        if (mobile.length() == 11) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(mobile);
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }
}
