package org.jambo2.jop.db;

import org.jambo2.examples.school.biz.School2BO;
import org.jambo2.examples.school.vo.School2Param;
import org.jambo2.examples.school.vo.School2VO;
import org.jambo2.jop.common.spring.SpringContextManager;
import org.jambo2.jop.infrastructure.sql.SQLHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * sql拼装测试
 */
public class SQLHelperTestCase {
	private School2BO biz;

	@BeforeClass
	public static void start() throws Exception {
		SpringContextManager.init("/spring/applicationContextTest.xml");
	}

	@Before()
	public void setUp() throws Exception {
		biz = (School2BO) SpringContextManager.getBean("school2BO");
	}

	@Test
	public void testQueryByPage() {
		School2Param param = new School2Param();
		param.set_pagesize("5");
		//页码从0开始
		param.set_pageno("1");

		SQLHelper helper = new SQLHelper();

		try {
			School2VO vo = new School2VO();
			String sql = helper.assembleSQL(param, vo);
			System.out.println(sql);
			System.out.println(helper.assembleCountSQL(param, vo, sql));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
