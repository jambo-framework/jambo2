package org.jambo2.examples.school.biz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jambo2.examples.school.vo.SchoolVO;
import org.jambo2.jop.infrastructure.db.AbstractBO;
import org.jambo2.jop.infrastructure.db.BaseDAO;
import org.jambo2.jop.infrastructure.db.DBAccessUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 机构业务层实现类，AbstractBO
 * <p>Description: </p>
 * @author jinbo
 */
@Service("school1BO")
public class School1BO extends AbstractBO {
	public static final Logger log = LogManager.getLogger(School1BO.class);

	private SchoolDAO schoolDAO;

	@Autowired
	public void setSchoolDao(SchoolDAO schoolDAO) {
		this.schoolDAO = schoolDAO;
	}

	@Override
	public BaseDAO getDAO() {
		return schoolDAO;
	}

	@Override
	public DBAccessUser getUser() {
		return DBAccessUser.getInnerUser();
	}

	public void createTable(){
		schoolDAO.createTable();
	}
}
