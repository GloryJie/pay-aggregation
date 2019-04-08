package com.gloryjie.pay.log.http.model;

import lombok.Data;

import java.util.List;

/**
 * @author jie
 * @since 2019/4/7
 */
@Data
public class PageInfo<T> {

    private Integer startPage;

    private Integer pageSize;

    private Long total;

    List<T> list;

}
