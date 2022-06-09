package top.withlevi.usercenter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.withlevi.usercenter.common.BaseResponse;
import top.withlevi.usercenter.common.ErrorCode;
import top.withlevi.usercenter.common.ResultUtil;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandle(BusinessException e){
        log.error("businessException: "+e.getMessage(),e);
        return ResultUtil.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler({RuntimeException.class})
    public BaseResponse runtimeExceptionHandler(BusinessException e){
        log.error("runtimeException", e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }

}
