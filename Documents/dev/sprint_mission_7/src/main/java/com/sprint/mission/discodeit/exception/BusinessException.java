//package com.sprint.mission.discodeit.exception;
//
//public class BusinessException extends RuntimeException {
//    private final StatusCode statusCode;
//
//    public BusinessException(StatusCode statusCode) {
//        super(statusCode.getMessage());
//        this.statusCode = statusCode;
//    }
//
//    public StatusCode getStatusCode() {
//        return statusCode;
//    }
//    public int getStatus() {
//        return statusCode.getHttpStatus().value();
//    }
//
//    public String getErrorMessage() {
//        return statusCode.getMessage();
//    }
//
//    public String getErrorCode() {
//        return statusCode.name();
//    }
//}
