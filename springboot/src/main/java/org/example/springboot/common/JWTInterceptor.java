package org.example.springboot.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot.entity.Account;
import org.example.springboot.exception.CustomerException;
import org.example.springboot.service.AdminService;
import org.example.springboot.service.UserService;
import org.springframework.web.servlet.HandlerInterceptor;

public class JWTInterceptor implements HandlerInterceptor {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

//    @Override
//    public boolean preHandle(HttpServletRequest request,
//                             HttpServletResponse response,
//                             Object handler) throws Exception {
//        // 1. 从请求头里拿 Token
//        String token = request.getHeader("token");
//        if(StrUtil.isEmpty(token)){
//            //如果没拿到，从参数里再拿一次
//            token = request.getParameter("token");
//        }
//
//        // 2. 认证 Token
//        if(StrUtil.isBlank(token)){
//            throw new CustomerException("401","您无权限操作");
//        }
//
//        Account account =null;
//        try {
//
//            // 拿到 token 的载荷数据
//            String audience = JWT.decode(token).getAudience().get(0);
//            String[] split = audience.split("-");
//            String userId = split[0];
//            String role = split[1];
//            //根据token解析出来的userid去对应的表查询用户信息
//            if("ADMIN".equals(role)){
//                account = adminService.selectById(userId);
//            }else if("USER".equals(role)){
//                account = userService.selectById(userId);
//            }
//
//        }catch (Exception e){
//            throw new CustomerException("401","您无权限操作");
//        }
//        if(account == null){
//            throw new CustomerException("401","您无权限操作");
//        }
//
//        try {
//            //验证签名
//            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
//            jwtVerifier.verify(token);
//
//        }catch (Exception e){
//            throw new CustomerException("401","您无权限操作");
//        }
//
//       return true;
//
//    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        // =========================
        // ✅ 1. 白名单（游客可访问）
        // =========================
        if (
                uri.contains("/login") ||
                        uri.contains("/register") ||

                        // 前台公开接口（重点！！）
                        uri.contains("/goods") ||
                        uri.contains("/notice") ||
                        uri.contains("/category") ||
                        uri.contains("/banner")||
                        uri.contains("/user/selectAll")
        ) {
            return true;
        }

        // =========================
        // ❗ 2. 获取 token
        // =========================
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            token = request.getParameter("token");
        }

        if (StrUtil.isBlank(token)) {
            throw new CustomerException("401", "请先登录");
        }

        Account account;
        String role;
        String userId;

        try {
            String audience = JWT.decode(token).getAudience().get(0);
            String[] split = audience.split("-");
            userId = split[0];
            role = split[1];

            if ("ADMIN".equals(role)) {
                account = adminService.selectById(userId);
            } else {
                account = userService.selectById(userId);
            }

        } catch (Exception e) {
            throw new CustomerException("401", "token无效");
        }

        if (account == null) {
            throw new CustomerException("401", "用户不存在");
        }

        // =========================
        // ❗ 3. 验证 token
        // =========================
        try {
            JWTVerifier jwtVerifier =
                    JWT.require(Algorithm.HMAC256(account.getPassword()))
                            .build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new CustomerException("401", "token验证失败");
        }

        // =========================
        // ❗ 4. 后台权限控制
        // =========================
        if (uri.startsWith("/admin") && !"ADMIN".equals(role)) {
            throw new CustomerException("403", "无权限访问后台");
        }

        return true;
    }

}
