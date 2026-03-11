// H5LoginController.java
package com.demo_249050433.h5controller;

import com.demo_249050433.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * H5登录注册控制器
 * 为微信小程序前端提供登录注册相关的RESTful API接口
 */
@RestController
@RequestMapping("/login")
public class H5LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 用户登录接口
     *
     * @param params 参数对象，包含stu_no和stu_pwd
     * @return 登录结果（JSON格式）
     */
    @PostMapping("/h5_login")
    public Map<String, Object> h5Login(@RequestBody Map<String, Object> params) {
        System.out.println("用户登录 - params:" + params);

        Map<String, Object> result = new HashMap<>();
        try {
            // 获取参数
            Integer stu_no = null;
            String stu_pwd = null;

            try {
                stu_no = Integer.parseInt(params.get("stu_no").toString());
                stu_pwd = params.get("stu_pwd").toString();
            } catch (Exception e) {
                result.put("code", 400);
                result.put("message", "参数格式错误");
                return result;
            }

            // 调用登录服务
            Map<String, Object> loginResult = loginService.login(stu_no, stu_pwd);

            // 直接返回登录服务的结果
            result.putAll(loginResult);

            System.out.println("登录结果: " + loginResult.get("code") + " - " + loginResult.get("message"));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 用户注册接口
     *
     * @param params 参数对象，包含stu_no, stu_pwd, stu_name, stu_telephone
     * @return 注册结果（JSON格式）
     */
    @PostMapping("/h5_register")
    public Map<String, Object> h5Register(@RequestBody Map<String, Object> params) {
        System.out.println("用户注册 - params:" + params);

        Map<String, Object> result = new HashMap<>();
        try {
            // 获取参数
            Integer stu_no = null;
            String stu_pwd = null;
            String stu_name = null;
            String stu_telephone = null;

            try {
                stu_no = Integer.parseInt(params.get("stu_no").toString());
                stu_pwd = params.get("stu_pwd").toString();
                stu_name = params.get("stu_name").toString();
                stu_telephone = params.get("stu_telephone").toString();
            } catch (Exception e) {
                result.put("code", 400);
                result.put("message", "参数格式错误");
                return result;
            }

            // 调用注册服务
            Map<String, Object> registerResult = loginService.register(stu_no, stu_pwd, stu_name, stu_telephone);

            // 直接返回注册服务的结果
            result.putAll(registerResult);

            System.out.println("注册结果: " + registerResult.get("code") + " - " + registerResult.get("message"));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 验证token接口（用于保持登录状态）
     *
     * @param params 参数对象，包含token
     * @return 验证结果（JSON格式）
     */
    @PostMapping("/h5_validate_token")
    public Map<String, Object> h5ValidateToken(@RequestBody Map<String, Object> params) {
        System.out.println("验证token - params:" + params);

        Map<String, Object> result = new HashMap<>();
        try {
            String token = params.get("token").toString();

            // 验证token
            Integer stu_no = loginService.validateToken(token);

            if (stu_no != null) {
                result.put("code", 200);
                result.put("message", "token有效");
                result.put("stu_no", stu_no);
            } else {
                result.put("code", 401);
                result.put("message", "token无效或已过期");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 用户注销接口
     *
     * @param params 参数对象，包含token
     * @return 注销结果（JSON格式）
     */
    @PostMapping("/h5_logout")
    public Map<String, Object> h5Logout(@RequestBody Map<String, Object> params) {
        System.out.println("用户注销 - params:" + params);

        Map<String, Object> result = new HashMap<>();
        try {
            String token = params.get("token").toString();

            // 调用注销服务
            Map<String, Object> logoutResult = loginService.logout(token);

            // 直接返回注销服务的结果
            result.putAll(logoutResult);

            System.out.println("注销结果: " + logoutResult.get("code") + " - " + logoutResult.get("message"));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
        }

        return result;
    }
}