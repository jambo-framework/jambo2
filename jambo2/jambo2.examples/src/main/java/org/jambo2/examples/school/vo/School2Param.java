package org.jambo2.examples.school.vo;


import org.jambo2.jop.infrastructure.db.BaseVO;
import org.jambo2.jop.infrastructure.db.DBQueryParam;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 学校机构基本信息实体类
 * <p>Description: </p>
 * @author jinbo
 */
public class School2Param extends DBQueryParam {

	private int _sk_schoolId;
	
	private String _sk_schoolName;

	public String get_sk_schoolName() {
		return _sk_schoolName;
	}

	public void set_sk_schoolName(String _sk_schoolName) {
		this._sk_schoolName = _sk_schoolName;
	}

	public int get_sk_schoolId() {
		return _sk_schoolId;
	}

	public void set_sk_schoolId(int _sk_schoolId) {
		this._sk_schoolId = _sk_schoolId;
	}
}
