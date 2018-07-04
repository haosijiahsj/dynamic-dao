package com.zzz.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Data
@Entity
@Table(name = "entity")
public class EntityPo {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name_")
    private String name;

    @Column
    private Integer sex;

    @Column
    private String tel;

    @Column
    private Boolean available;

}
