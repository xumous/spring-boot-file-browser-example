// StuInfoMapper.java
package com.demo_249050433.mapper;

import com.demo_249050433.entity.StuInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 学生信息数据访问层接口
 * 提供与学生信息相关的数据库交互操作
 */
@Mapper
@Repository
public interface StuInfoMapper {

    /**
     * 根据学号查询学生信息
     *
     * @param stu_no 学号
     * @return 学生信息
     */
    @Select("SELECT * FROM stu_info WHERE stu_no = #{stu_no}")
    StuInfo getStuInfoByNo(@Param("stu_no") int stu_no);

    /**
     * 根据学号和密码查询学生信息（用于登录验证）
     *
     * @param stu_no  学号
     * @param stu_pwd 密码
     * @return 学生信息
     */
    @Select("SELECT * FROM stu_info WHERE stu_no = #{stu_no} AND stu_pwd = #{stu_pwd}")
    StuInfo getStuInfoByNoAndPwd(@Param("stu_no") int stu_no, @Param("stu_pwd") String stu_pwd);

    /**
     * 新增学生信息（用于注册）
     *
     * @param stuInfo 学生信息对象
     * @return 插入影响的行数
     */
    @Insert("INSERT INTO stu_info (stu_no, stu_name, stu_pwd, stu_telephone, stu_status) " +
            "VALUES (#{stu_no}, #{stu_name}, #{stu_pwd}, #{stu_telephone}, 1)")
    int insertStuInfo(StuInfo stuInfo);

    /**
     * 检查学号是否存在
     *
     * @param stu_no 学号
     * @return 存在的记录数
     */
    @Select("SELECT COUNT(*) FROM stu_info WHERE stu_no = #{stu_no}")
    int checkStuNoExists(@Param("stu_no") int stu_no);
}