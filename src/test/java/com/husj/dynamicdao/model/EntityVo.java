package com.husj.dynamicdao.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Data
public class EntityVo {

    private Integer id;
    private String name;
    private Integer sex;
    private String tel;
    private Boolean available;
    private LocalDateTime createTime;
    private Status status;

}
