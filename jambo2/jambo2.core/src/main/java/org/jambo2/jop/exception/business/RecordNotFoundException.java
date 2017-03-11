package org.jambo2.jop.exception.business;

import org.apache.commons.lang.ClassUtils;
import org.jambo2.jop.exception.BusinessException;
import org.jambo2.jop.exception.JOPException;

import java.io.Serializable;


/**
 * RecordNotFoundException，未找到对应记录时抛出。
 * <br> Description: class RecordNotFoundException
 * <br> Company: jambo2-framework,Guangzhou</br>
 * @author jinbo
 * @since 1.0
 * @version 1.0
 * 2017-1-23
 */
public class RecordNotFoundException extends BusinessException {
	public static final String ERROR_CODE_DB_RECORDNOTFOUND = "db.recordnotfound";
	/**
	 * @param errorCode  错误代码
	 */
	public RecordNotFoundException(String errorCode) {
		super (JOPException.toMessage(BusinessException.class, checkErrorCode(errorCode), ""));
		setErrorCode(errorCode);
	}
	
	public RecordNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecordNotFoundException(String errorCode, String[] msgParam, Throwable cause) {
		super (JOPException.toMessage(BusinessException.class, checkErrorCode(errorCode),msgParam), cause);
		setErrorCode(errorCode);
	}

	public RecordNotFoundException(String errorCode, String msgParam, Throwable cause) {
		super (JOPException.toMessage(BusinessException.class, checkErrorCode(errorCode), msgParam), cause);
		setErrorCode(errorCode);
	}

	protected static String checkErrorCode(String errorCode){
		return (errorCode==null)?ERROR_CODE_DB_RECORDNOTFOUND:errorCode;
	}

	public RecordNotFoundException(Class voClass, Serializable pk) {
		super (JOPException.toMessage(BusinessException.class, ERROR_CODE_DB_RECORDNOTFOUND, new String[]{pk.toString(), ClassUtils.getShortClassName(voClass)}), null);
		setErrorCode(ERROR_CODE_DB_RECORDNOTFOUND);
	}

}
