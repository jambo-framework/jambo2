package org.jambo2.examples.school.vo;


import org.jambo2.jop.infrastructure.db.BaseVO;

import java.util.Date;

/**
 * 学校机构基本信息实体类
 * <p>Description: </p>
 * @author jinbo
 */
public class SchoolVO extends BaseVO {

	/**
	 * 机构ID(主键)
	 */
	private int schoolId;
	
	/**
	 * 机构名称
	 */
	private String schoolName;
	
	/**
	 * 所属省份
	 */
	private String province;
	
	/**
	 * 所属地市
	 */
	private String city;
	
	/**
	 * 地址
	 */
	private String address;
	
	/**
	 * 简介
	 */
	private String introdution;

	/**
	 * 地区
	 */
	private String district;


	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIntrodution() {
		return introdution;
	}

	public void setIntrodution(String introdution) {
		this.introdution = introdution;
	}

}
