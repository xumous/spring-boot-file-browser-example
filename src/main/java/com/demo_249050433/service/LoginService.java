// LoginService.java
package com.demo_249050433.service;

import com.demo_249050433.entity.StuInfo;
import com.demo_249050433.mapper.StuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 登录注册服务层
 * 处理用户登录、注册相关的业务逻辑
 */
@Service
public class LoginService {

    @Autowired
    private StuInfoMapper stuInfoMapper;

    // 简单的内存存储用于保存登录状态（实际项目中应使用Redis等分布式缓存）
    private Map<String, Integer> tokenStorage = new HashMap<>();

    /**
     * 用户登录
     *
     * @param stu_no  学号
     * @param stu_pwd 密码
     * @return 登录结果，包含token和用户信息
     */
    public Map<String, Object> login(int stu_no, String stu_pwd) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 参数验证
            if (stu_pwd == null || stu_pwd.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "密码不能为空");
                return result;
            }

            // 查询用户信息
            StuInfo stuInfo = stuInfoMapper.getStuInfoByNoAndPwd(stu_no, stu_pwd);

            if (stuInfo != null) {
                // 生成token
                String token = UUID.randomUUID().toString().replace("-", "");

                // 存储token和用户ID的映射关系
                tokenStorage.put(token, stu_no);

                // 构造返回结果
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("stu_no", stuInfo.getStu_no());
                userInfo.put("stu_name", stuInfo.getStu_name());
                userInfo.put("stu_telephone", stuInfo.getStu_telephone());

                result.put("code", 200);
                result.put("message", "登录成功");
                result.put("token", token);
                result.put("userInfo", userInfo);
            } else {
                // 检查学号是否存在
                StuInfo stuInfoCheck = stuInfoMapper.getStuInfoByNo(stu_no);
                if (stuInfoCheck == null) {
                    result.put("code", 404);
                    result.put("message", "学号不存在");
                } else {
                    result.put("code", 401);
                    result.put("message", "密码错误");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 用户注册
     *
     * @param stu_no        学号
     * @param stu_pwd       密码
     * @param stu_name      姓名
     * @param stu_telephone 电话
     * @return 注册结果
     */
    @Transactional
    public Map<String, Object> register(int stu_no, String stu_pwd, String stu_name, String stu_telephone) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 参数验证
            if (stu_pwd == null || stu_pwd.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "密码不能为空");
                return result;
            }

            if (stu_name == null || stu_name.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "姓名不能为空");
                return result;
            }

            if (stu_telephone == null || stu_telephone.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "电话不能为空");
                return result;
            }

            // 检查学号是否已存在
            int exists = stuInfoMapper.checkStuNoExists(stu_no);
            if (exists > 0) {
                result.put("code", 400);
                result.put("message", "学号已存在");
                return result;
            }

            // 创建学生信息对象
            StuInfo stuInfo = new StuInfo();
            stuInfo.setStu_no(stu_no);
            stuInfo.setStu_pwd(stu_pwd);
            stuInfo.setStu_name(stu_name);
            stuInfo.setStu_telephone(stu_telephone);
            stuInfo.setStu_status(1); // 默认为1（激活状态）

            // 插入数据库
            int insertResult = stuInfoMapper.insertStuInfo(stuInfo);

            if (insertResult == 1) {
                result.put("code", 200);
                result.put("message", "注册成功");
            } else {
                result.put("code", 500);
                result.put("message", "注册失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 验证token是否有效
     *
     * @param token token
     * @return 用户ID（学号），如果无效则返回null
     */
    public Integer validateToken(String token) {
        return tokenStorage.get(token);
    }

    /**
     * 用户注销
     *
     * @param token token
     * @return 注销结果
     */
    public Map<String, Object> logout(String token) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (token != null && tokenStorage.containsKey(token)) {
                tokenStorage.remove(token);
                result.put("code", 200);
                result.put("message", "注销成功");
            } else {
                result.put("code", 400);
                result.put("message", "token无效或已过期");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
        }

        return result;
    }
}