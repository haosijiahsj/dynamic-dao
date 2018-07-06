package com.zzz.exceptions;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class DynamicDaoException extends RuntimeException {

    private static final long serialVersionUID = -690044399733699352L;

    public DynamicDaoException() {
        super();
    }

    public DynamicDaoException(String message) {
        super(message);
    }

    public DynamicDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynamicDaoException(Throwable cause) {
        super(cause);
    }

}
