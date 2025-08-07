package com.bmcho.hightrafficboard.exception;

public class PasswordSecurityException extends BasicException {

  public PasswordSecurityException(ErrorCode errorCode) {
    super(errorCode);
  }

  public static class PasswordEncryptionException extends PasswordSecurityException {
    public PasswordEncryptionException() {
      super(ErrorCode.PASSWORD_ENCRYPTION_FAILED);
    }
  }
}
