package com.husj.dynamicdao.page;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 转换器
     * @param pageWrapperP
     * @param targetClass
     * @param <V>
     * @param <P>
     * @return
     */
    public static <V, P> PageWrapper<V> convert(PageWrapper<P> pageWrapperP, Class<V> targetClass) {
        PageWrapper<V> pageWrapperV = new PageWrapper<>();
        BeanUtils.copyProperties(pageWrapperP, pageWrapperV, "content");
        List<V> listV = pageWrapperP.getContent().stream().map(p -> {
            try {
                V v = targetClass.newInstance();
                BeanUtils.copyProperties(p, v);
                return v;
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }

        }).collect(Collectors.toList());

        pageWrapperV.setContent(listV);

        return pageWrapperV;
    }

    /**
     * 转换器
     * @param targetClass
     * @param <V>
     * @return
     */
    public <V> PageWrapper<V> convert(Class<V> targetClass) {
        PageWrapper<V> pageWrapperV = new PageWrapper<>();
        BeanUtils.copyProperties(this, pageWrapperV, "content");
        List<V> listV = this.content.stream().map(p -> {
            try {
                V v = targetClass.newInstance();
                BeanUtils.copyProperties(p, v);
                return v;
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }

        }).collect(Collectors.toList());

        pageWrapperV.setContent(listV);

        return pageWrapperV;
    }

}
