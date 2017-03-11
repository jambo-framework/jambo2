package org.jambo2.examples.school.biz;

import org.apache.ibatis.annotations.Param;
import org.jambo2.examples.school.vo.SchoolVO;
import org.jambo2.jop.infrastructure.db.BaseDAO;

import java.util.List;

/**
 * 学校机构基本信息持久化层
 * <p>Description: </p>
 * @author jinbo
 */
public interface SchoolDAO extends BaseDAO {
    public void createTable();
}
