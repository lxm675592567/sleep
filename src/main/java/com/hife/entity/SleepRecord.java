package com.hife.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 患者档案
 */
@EqualsAndHashCode()
@Data
@Accessors(chain = true)
@Document(collection = "sleep_record")
public class SleepRecord {
    /**
     * 主键
     */
    private String sleepId;

    /**
     * guid
     */
    private String guid;

    /**
     * 卡号
     */
    private String cardId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 地址
     */
    private String address;

    /**
     * 电话
     */
    private String phone;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 身高
     */
    private String height;

    /**
     * 体重
     */
    private String weight;

    /**
     * 体质指数
     */
    private String BMI;

    /**
     * dat路径
     */
    private List<String> datUrl;

    /**
     * dat路径
     */
    private String url;

    /**
     * 医护人员姓名
     */
    private String yhryName;

    /**
     * 技术员
     */
    private String technician;

    /**
     * 电话
     */
    private String yhryPhone;

    /**
     * 记录员
     */
    private String recorder;

    /**
     * 传真
     */
    private String fax;

    /**
     * 医生
     */
    private String doctor;

    /**
     * 生成报告日期
     */
    private String createTime;

    /**
     * 医生建议
     */
    private String doctorAdvice;

    /**
     * tenant_id
     */
    private String tenant_id;
}
