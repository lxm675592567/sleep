package com.modules.manage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@EqualsAndHashCode()
@Data
@Accessors(chain = true)
@Document(collection = "watch_manage")
public class WatchManage {

    /**
     * 手表编号
     */
    private String watchId;

    /**
     * 状态(0入库,1出库)
     */
    private String type;

    /**
     * 入库时间
     */
    private String startTime;

    /**
     * 出库时间
     */
    private String endTime;

    /**
     * 持有人
     */
    private String holder;

    /**
     * 操作员
     */
    private String name;

    /**
     * 持有人基本信息
     */
    private Map<String, Object> info;

}
