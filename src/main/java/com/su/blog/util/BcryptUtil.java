//package com.su.blog.util;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
///**
// * @ClassName: BcryptUtil
// * @Description:
// * @Author: liuxiaoxiang
// * @Date: 2022/5/8 12:43
// * @Version:
// */
//public class BcryptUtil {
//
//    /**
//     * 加密
//     *
//     * @param password
//     * @return
//     */
//    public static String bcrypt(String password) {
//        // 创建密码加密的对象
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        // 密码加密
//        return passwordEncoder.encode(password);
//    }
//
//    /**
//     * 解密
//     *
//     * @param password       原始密码
//     * @param bcryptPassword 加密后的密码
//     * @return boolean true:一致，false:不一致
//     */
//    public static boolean comparePassword(String password, String bcryptPassword) {
//        // 创建密码加密的对象
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        // 校验这两个密码是否是同一个密码
//        // matches方法第一个参数是原密码，第二个参数是加密后的密码
//        return passwordEncoder.matches(password, bcryptPassword);
//    }
//}