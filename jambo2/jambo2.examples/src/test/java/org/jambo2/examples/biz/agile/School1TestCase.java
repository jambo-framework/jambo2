package org.jambo2.examples.biz.agile;

import org.jambo2.examples.school.biz.School1BO;
import org.jambo2.examples.school.vo.SchoolVO;
import org.jambo2.jop.common.spring.SpringContextManager;
import org.jambo2.jop.infrastructure.db.BaseVO;
import org.jambo2.jop.infrastructure.db.DBQueryParam;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * 基本数据操作范例1
 * 使用基于注解的方式，基类AbstractBO的作用不大，主要工作在于编写mybatis的xml
 * 这种方式dao能使用的全部方法都需要在xml里有定义sql
 */
public class School1TestCase {
	private School1BO biz;

	@BeforeClass
	public static void start() throws Exception {
		SpringContextManager.init("/spring/applicationContextTest.xml");
	}

	@Before()
	public void setUp() throws Exception {
		biz = (School1BO) SpringContextManager.getBean("school1BO");
	}

//	@Rollback(false)
	public void testCreateTable(){
		biz.createTable();
		testCRUD();
	}

	@Test
	public void testCRUD(){
		SchoolVO vo = new SchoolVO();
		vo.setSchoolId(1);
		vo.setSchoolName("test1");
		vo.setIntrodution("testCreate");

		int id = biz.create(vo);
		System.out.println("id:" + id);

		testQuery("testCreate");

		vo.setIntrodution("testUpdate");
		vo.setProvince("gz");
		biz.update(vo);

		testQuery("testUpdate");

		SchoolVO school = (SchoolVO) biz.findByPK(1);
		System.out.println("finByPK name:"+school.getSchoolName());
		System.out.println("finByPK intro:"+school.getIntrodution());

		biz.delete(1);
		testQuery("testDelete");
	}

	private void testQuery(String namae) {
		List<BaseVO> list;
//		list = biz.queryAll();
//		System.out.println(namae + " size:"+list.size());
//		for (BaseVO obj : list){
//			SchoolVO school = (SchoolVO) obj;
//			System.out.println(namae + "intro:"+school.getIntrodution());
//		}
	}

	@Test
	public void testQueryByPage() {
		SchoolVO vo = new SchoolVO();
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
			SchoolVO school = (SchoolVO) obj;
			System.out.println("queryByPage id:"+school.getSchoolId());
			System.out.println("queryByPage intro:"+school.getIntrodution());
		}
	}

}
