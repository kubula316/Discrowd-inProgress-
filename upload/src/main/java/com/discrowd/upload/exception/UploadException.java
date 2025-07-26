package com.discrowd.upload.exception;

public class UploadException extends RuntimeException {
    private UploadError uploadError;

    public UploadException(UploadError uploadError){
        this.uploadError = uploadError;
    }

    public UploadError getStudentError() {
        return uploadError;
    }
}
