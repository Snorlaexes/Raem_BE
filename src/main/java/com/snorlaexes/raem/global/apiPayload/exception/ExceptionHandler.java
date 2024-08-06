package com.snorlaexes.raem.global.apiPayload.exception;

import com.snorlaexes.raem.global.apiPayload.code.BaseErrorCode;

public class ExceptionHandler extends GeneralException{
    public ExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
