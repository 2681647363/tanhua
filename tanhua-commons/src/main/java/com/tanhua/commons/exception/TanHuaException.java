package com.tanhua.commons.exception;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 */
@Data
@NoArgsConstructor
public class TanHuaException extends RuntimeException {
    private Object errData;

    public TanHuaException(String errMessage){
        super(errMessage);
    }

    public TanHuaException(Object data){
        super();
        this.errData = data;
    }

}