package com.hife.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分页参数类
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageParam implements Serializable {

    /**
     * 每页条数
     */
    private Integer pageSize;
    /**
     * 第多少页
     */
    private Integer pageNum;

    /**
     * 每页条数字段名
     */

    public static final String PAGE_SIZE = "pageSize";
    /**
     * 第几页字段名
     */
    public static final String PAGE_NUM = "pageNum";

}
