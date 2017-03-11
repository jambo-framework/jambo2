package org.jambo2.jop.exception.system;

import org.jambo2.jop.exception.JOPException;

/**
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: jambo2-framework Tech Ltd.</p>
 *
 * @author JinBo
 *
 * @version 1.0
 */
public class DAOException extends JOPException {
	private static String errorCode = "biz.common";
	
    public DAOException() {
        super(errorCode);
    }

    public DAOException(String message) {
        super(message);
    }

}
