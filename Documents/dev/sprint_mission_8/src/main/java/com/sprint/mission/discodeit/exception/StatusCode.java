//package com.sprint.mission.discodeit.exception;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//
//@Getter
//@RequiredArgsConstructor
//public enum StatusCode {
//    //성공 코드
//    USER_LIST_OK(HttpStatus.OK, "User 목록 조회 성공"),
//    READ_STATUS_OK(HttpStatus.OK,"Message 읽음 상태 목록 조회 성공"),
//    MESSAGE_LIST_OK(HttpStatus.OK, "Message 목록 조회 성공"),
//    LOGIN_OK(HttpStatus.OK, "로그인 성공"),
//    USER_UPDATE_OK(HttpStatus.OK, "User 정보가 성공적으로 수정됨"),
//    USER_STATUS_UPDATE_OK(HttpStatus.OK, "User 온라인 상태가 성공적으로 업데이트됨"),
//    MESSAGE_READ_STATUS_OK(HttpStatus.OK, "Message 읽음 상태가 성공적으로 수정됨"),
//    MESSAGE_UPDATE_OK(HttpStatus.OK, "Message가 성공적으로 수정됨"),
//    CHANNEL_UPDATE_OK(HttpStatus.OK, "Channel 정보가 성공적으로 수정됨"),
//    CHANNEL_LIST_OK(HttpStatus.OK, "Channel 목록 조회 성공"),
//    BINARY_CONTENT_LIST_OK(HttpStatus.OK, "첨부 파일 목록 조회 성공"),
//    BINARY_CONTENT_READ_OK(HttpStatus.OK, "첨부 파일 조회 성공"),
//
//    USER_CREATE(HttpStatus.CREATED, "User가 성공적으로 생성됨"),
//    MESSAGE_READ_STATUS_CREATE(HttpStatus.CREATED, "Message 읽음 상태가 성공적으로 생성됨"),
//    MESSAGE_CREATE(HttpStatus.CREATED, "Message가 성공적으로 생성됨"),
//    CHANNEL_PUBLIC_CREATE(HttpStatus.CREATED, "Public Channel이 성공적으로 생성됨"),
//    CHANNEL_PRIVATE_CREATE(HttpStatus.CREATED, "Private Channel이 성공적으로 생성됨"),
//
//    //예외 코드
//    USER_WITH_EMAIL_EXISTS(HttpStatus.BAD_REQUEST, "같은 email 또는 username을 사용하는 User가 이미 존재함"),
//    READ_STATUS_EXISTS(HttpStatus.BAD_REQUEST, "이미 읽음 상태가 존재함"),
//    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않음"),
//    CHANNEL_PRIVATE_UPDATE_FORBIDDEN(HttpStatus.BAD_REQUEST, "Private Channel은 수정할 수 없음"),
//
//    CHANNEL_OR_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel 또는 User를 찾을 수 없음"),
//    USER_WITH_USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없음"),
//    USER_WITH_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "User를 찾을 수 없음"),
//    USERSTATUS_WITH_USERID_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 User의 UserStatus를 찾을 수 없음"),
//    READSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "Message 읽음 상태를 찾을 수 없음"),
//    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Message를 찾을 수 없음"),
//    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel을 찾을 수 없음"),
//    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "첨부 파일을 찾을 수 없음");
//
//    private final HttpStatus httpStatus;
//    private final String message;
//}
