package org.jambo2.jop.infrastructure.db;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.jambo2.jop.infrastructure.config.CoreConfigInfo;

import java.io.Serializable;

/**
 * <p>
 * Title: 基础数据访问用户对象
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: jambo2-framework Tech Ltd.
 * </p>
 * 
 * @author jinbo
 *
 */
public class DBAccessUser implements Serializable {

	private static final long serialVersionUID = 3628497740671279410L;

	private String oprcode;  //操作员编号
	private String dbFlag;  //数据所在地市ID(字母)
	private String ip;  //访问的来源IP

	public String getOprcode() {
		return oprcode;
	}

	public void setOprcode(String oprcode) {
		this.oprcode = oprcode;
	}

	public String getDbFlag() {
		return dbFlag;
	}

	public void setDbFlag(String dbFlag) {
		this.dbFlag = dbFlag;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String toString(){
		return ReflectionToStringBuilder.toString(this);
	}

    /**
     * 返回公共库用户对象，用于没登录用户的场景，opcode会指定为jop，IP会返回本机的IP
     */
	public static DBAccessUser getInnerUser() {
		DBAccessUser innerUser = new DBAccessUser();
		innerUser.setDbFlag(CoreConfigInfo.COMMON_DB_NAME);
		innerUser.setOprcode("jop");
		innerUser.setIp(CoreConfigInfo.LOCAL_IP_ADDDRESS);
		return innerUser;
	}
	
    /**
     * 2013-5-29 jinbo 增加使用登录user获取公共库user的方法
     * @param user 当前的User
     * @return dbFlag被转换为公共库的dbFlag
     */
	public static DBAccessUser getCommonUser(DBAccessUser user) throws Exception{
		DBAccessUser commUser = new DBAccessUser();
		commUser.setDbFlag(CoreConfigInfo.COMMON_DB_NAME);
		if (user != null){
			commUser.setOprcode(user.getOprcode());
			commUser.setIp(user.getIp());
		}
		return commUser;
	}
	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
}
