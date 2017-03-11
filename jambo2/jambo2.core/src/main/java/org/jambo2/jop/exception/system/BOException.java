package org.jambo2.jop.exception.system;

import org.jambo2.jop.exception.JOPException;

/**
 * <p>Title: GDIBOSS</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: jambo2-framework Tech Ltd.</p>
 *
 * @author jinbo
 *
 * @version 1.0
 */
public class BOException extends JOPException {
	public static final String ERROR_CODE_CONTROL_COMMON = "business.common";
	/**
	 * @param errorCode  错误代码
	 */
	public BOException(String errorCode) {
		super (toMessage(JOPException.class, checkErrorCode(errorCode), ""));
		setErrorCode(errorCode);
	}
	
	public BOException(String message, Throwable cause) {
		super(message, cause);
	}

	protected static String checkErrorCode(String errorCode){
		return (errorCode==null)?ERROR_CODE_CONTROL_COMMON:errorCode;
	}

}
