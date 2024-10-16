package com.snorlaexes.raem.global.apiPayload.code.status;

import com.snorlaexes.raem.global.apiPayload.code.BaseErrorCode;
import com.snorlaexes.raem.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _WRONG_EXTENSION(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COMMON404", "잘못된 파일 형식입니다."),
    _WRONG_PARAM(HttpStatus.FORBIDDEN, "COMMON405", "잘못된 파라미터입니다."),

    // 유저 관련 응답
    _USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER400", "사용 중인 이메일입니다."),
    _USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER401", "해당 유저가 존재하지 않습니다."),
    _WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "USER402", "비밀번호가 틀렸습니다."),
    _WRONG_CODE(HttpStatus.BAD_REQUEST, "USER403", "인증번호가 다릅니다."),
    _CODE_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER404", "인증 정보가 존재하지 않습니다."),

    // 인가 관련 응답
    _ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401", "AccessToken이 만료되었습니다."),
    _AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH402", "이메일 또는 비밀번호가 틀렸습니다."),

    // 이메일 관련 응답
    _UNABLE_TO_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL501", "이메일 전송 중 문제가 발생했습니다."),

    // 수면 관련 응답
    _DATA_SIZE_TOO_SMALL(HttpStatus.BAD_REQUEST, "SLEEP601", "데이터 사이즈가 너무 작습니다."),
    _SLEEP_DATA_NOT_FOUND(HttpStatus.BAD_REQUEST, "SLEEP602", "수면 데이터가 없습니다."),
    _URL_NOT_FOUND(HttpStatus.BAD_REQUEST, "SLEEP603", "수면 데이터 파일을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}