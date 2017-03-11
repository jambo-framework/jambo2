package org.jambo2.examples.biz.ease;

import org.apache.commons.beanutils.BeanUtils;
import org.jambo2.examples.school.biz.School2BO;
import org.jambo2.examples.school.vo.School2VO;
import org.jambo2.examples.school.vo.SchoolVO;
import org.jambo2.jop.common.spring.SpringContextManager;
import org.jambo2.jop.infrastructure.db.BaseVO;
import org.jambo2.jop.infrastructure.db.DBQueryParam;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

/**
 * 基本数据操作范例2
 * 使用基类AbstractBO的抽象方法，减少mybatis的xml内CRUD的编写
 */
public class School2TestCase {
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
	public void testCRUD(){
		School2VO vo = new School2VO();
		vo.setSchoolId(32);
		vo.setSchoolName("test1");
		vo.setIntrodution("testCreate");

		int id = biz.create(vo);
		System.out.println("id:" + id);

		testQuery("testCreate");

		vo.setIntrodution("testUpdate");
		vo.setProvince("gz");
		biz.update(vo);

//		testQuery("testUpdate");

		School2VO school = (School2VO) biz.findByPK(14);
		System.out.println("finByPK name:"+school.getSchoolName());
		System.out.println("finByPK intro:"+school.getIntrodution());

		biz.delete(1);
//		testQuery("testDelete");
	}

	private void testQuery(String namae) {
		List<BaseVO> list;
//		list = biz.queryAll();
//		System.out.println(namae + " size:"+list.size());
//		for (BaseVO obj : list){
//			School2VO school = (School2VO) obj;
//			System.out.println(namae + "intro:"+school.getIntrodution());
//		}
	}

	@Test
	public void testQueryByPage() {
		School2VO vo = new School2VO();
		vo.setSchoolId(1);
		vo.setSchoolName("test1");
		vo.setIntrodution("testCreate");
		for (int i=0; i < 10; i++){
			vo.setSchoolId(i);
			biz.create(vo);
		}

		DBQueryParam param = new DBQueryParam();
		param.set_pagesize("5");
		//页码从0开始
		param.set_pageno("0");
		queryByPage(param);
		param.set_pageno("1");
		queryByPage(param);
	}

	private void queryByPage(DBQueryParam param) {
		System.out.println("queryByPage pageno:"+param.get_pageno());
		List<BaseVO> list = biz.queryByPage(param);
		for (BaseVO obj : list){
			School2VO school = (School2VO) obj;
			System.out.println("queryByPage id:"+school.getSchoolId());
			System.out.println("queryByPage intro:"+school.getIntrodution());
		}
	}

}
