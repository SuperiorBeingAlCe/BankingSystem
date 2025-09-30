package com.banking.Banking.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	
	// Genel hata kodları
	  SOMETHING_WENT_WRONG("0001", "Bir hata oluştu."),
	    INVALID_REQUEST("0002", "Geçersiz istek."),
	    UNAUTHORIZED("0003", "Yetkisiz erişim."),
	    INTERNAL_SERVER_ERROR("0004", "Sunucu hatası oluştu."),
	    VALIDATION_FAILED("0005", "Doğrulama hatası."),
	    
	 // Kullanıcıya özel hata kodları (UserManager ile direkt ilgili)
	    USER_NOT_FOUND("1001", "Kullanıcı bulunamadı."),
	    USERNAME_ALREADY_EXISTS("1002", "Bu kullanıcı adı zaten kayıtlı."),
	    EMAIL_ALREADY_EXISTS("1003", "Bu e-posta adresi zaten kayıtlı."),
	    USER_CREATION_FAILED("1004", "Kullanıcı oluşturulurken hata oluştu."),
	    USER_UPDATE_FAILED("1005", "Kullanıcı güncellenirken hata oluştu."),
	    USER_DELETION_FAILED("1006", "Kullanıcı silinirken hata oluştu.");
	
	
	   private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

