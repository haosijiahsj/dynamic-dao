package com.zzz.page;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@Data
public class PageWrapper<T> implements Serializable {

    private static final long serialVersionUID = 7460406991379542895L;
    private int curPage;
    private int totalPages;
    private int size;
    private long totalRows;

    private List<T> content = new ArrayList<>();

}
