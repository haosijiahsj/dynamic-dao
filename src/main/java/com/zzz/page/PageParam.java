package com.zzz.page;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

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
        Preconditions.checkArgument(page > 0);
        Preconditions.checkArgument(size > 0);

        return new PageParam(page, size);
    }

}
