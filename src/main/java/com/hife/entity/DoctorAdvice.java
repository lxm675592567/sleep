package com.hife.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 医生建议
 */
@EqualsAndHashCode()
@Data
@Accessors(chain = true)
@Document(collection = "doctor_advice")
public class DoctorAdvice {

    /**
     * 医生建议Id
     */
    private String daId;

    /**
     * 标题
     */
    private String title;

//    /**
//     * 患者id
//     */
//    private String sleepId;

    /**
     * 医生建议
     */
    private String doctorAdvice;

}
