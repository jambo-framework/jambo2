package org.jambo2.jop.infrastructure.db;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jambo2.jop.infrastructure.biz.CommonDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 基础业务实现
 *
 * @author jinbo
 */
public abstract class AbstractBO {
    public static final Logger log = LogManager.getLogger(AbstractBO.class);

    protected BaseVO bizVO = null;

    public abstract BaseDAO getDAO();
    public abstract DBAccessUser getUser();

    @Autowired
    protected CommonDAO commonDAO;

    public int create(BaseVO vo) {
        if (bizVO != null) {
            Map map = vo.getClumnAndValue();
            commonDAO.insertBySQL(vo.tablename(), map);

            //@todo 需要提供插入后的主键
            return Integer.parseInt(vo.getIdValue());
        } else {
            return getDAO().create(vo);
        }
    }

    public void delete(int id) {
        if (bizVO != null) {
            Map wheres = bizVO.getIDAndValue();
            commonDAO.deleteBySQL(bizVO.tablename(), wheres);
        } else
            getDAO().delete(id);
    }

    public void update(BaseVO vo) {
        if (bizVO != null) {
            Map values = bizVO.getClumnAndValue();
            Map wheres = bizVO.getIDAndValue();
            commonDAO.updateBySQL(vo.tablename(), values, wheres);
        } else
            getDAO().update(vo);
    }

    public List<BaseVO> queryAll(DBQueryParam param) {
        if (bizVO != null) {
//            Map map = vo.getClumnAndValue();
//            commonDAO.insertBySQL(vo.tablename(), map);
//            commonDAO.queryBySQL()
            return  null;
        } else
            return getDAO().queryAll();
    }

    public List<BaseVO> queryByPage(DBQueryParam param) {
        if (bizVO != null) {
            return  null;
        } else
            return getDAO().queryByPage(param.get_pageno(), param.get_pagesize(), param.get_orderby(), param.get_desc());
    }

    public int queryCount() {
        return getDAO().queryCount();
    }

    public BaseVO findByPK(int id) {
        if (bizVO != null) {
            BaseVO vo = null;
            Map wheres = new HashMap();
            wheres.put(bizVO.getIdColumn(), id);
            List<Map<String,Object>> list = commonDAO.findBySQL(bizVO.tablename(), wheres);

            try {
                if (list.size() > 0) {
                    Map map = list.get(0);//只处理第一个
                    vo = bizVO.getClass().newInstance();

                    BaseVO.setProperties(vo, map);
                }
            } catch (Exception e) {
                log.catching(e);
            }

            return vo;
        } else
            return getDAO().findByPK(id);
    }

    public List queryBySQL(String table, List<String> fields, Map wheres, Integer begin, Integer end) {
        return getDAO().queryBySQL(table, fields, wheres, begin, end, null);
    }

    public int countBySQL(String table, Map wheres) {
        return getDAO().countBySQL(table, wheres);
    }

    public List queryBySQL(String table, List<String> fields, Map wheres) {
        return getDAO().queryBySQL(table, fields, wheres, null, null, null);
    }

    public void updateBySQL(String table, Map fields, Map wheres) {
        getDAO().updateBySQL(table, fields, wheres);
    }

    public void deleteBySQL(String table, Map wheres) {
        getDAO().deleteBySQL(table, wheres);
    }

    public void insertBySQL(String table, Map fields) {
        getDAO().insertBySQL(table, fields);
    }

    public void delete(String[] ids) {
        getDAO().delete(ids);
    }
}
