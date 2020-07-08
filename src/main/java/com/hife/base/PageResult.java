package com.hife.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class PageResult<T> implements Serializable {

    private Page page;
    private List<T> list;

    @Data
    @Accessors(chain = true)
    public static class Page {
        private Integer pageNum;

        private Integer pageSize;

        private Long total;

        private Integer pages;
    }
}
