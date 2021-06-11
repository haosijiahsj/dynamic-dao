package com.husj.dynamicdao.model;

import com.husj.dynamicdao.annotations.mapping.Column;
import com.husj.dynamicdao.annotations.mapping.EnumType;
import com.husj.dynamicdao.annotations.mapping.IdColumn;
import com.husj.dynamicdao.annotations.mapping.IdType;
import com.husj.dynamicdao.annotations.mapping.Table;
import com.husj.dynamicdao.convert.StatusConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Data
@Table("entity")
public class EntityPo {

    @IdColumn
    private Integer id;

    @Column("name_")
    private String name;

    @Column
    private Integer sex;

    @Column
    private String tel;

    @Column
    private Boolean available;

    @Column("create_time")
    private LocalDateTime createTime;

    @Column(value = "status_", enumType = EnumType.STRING)
    private Status status;

}
