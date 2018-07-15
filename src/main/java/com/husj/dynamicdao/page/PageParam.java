package com.husj.dynamicdao.page;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageParam implements Serializable {

    private static final long serialVersionUID = 4640289454631847877L;

    private int page;
    private int size;

    public static PageParam of(int page, int size) {
        Assert.isTrue(page > 0, "'page' must greater than 0 !");
        Assert.isTrue(size > 0, "'size' must greater than 0 !");

        return new PageParam(page, size);
    }

}
