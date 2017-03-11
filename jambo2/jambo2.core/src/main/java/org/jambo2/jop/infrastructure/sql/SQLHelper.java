package org.jambo2.jop.infrastructure.sql;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jambo2.jop.infrastructure.config.CoreConfigInfo;
import org.jambo2.jop.infrastructure.db.BaseVO;
import org.jambo2.jop.infrastructure.db.DBQueryParam;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Title: SQLHelper
 * Description: 用DBQueryParam的属性来构造sql
 * Company: jambo-framework Tech Ltd.
 *
 * @author jinbo
 */
public class SQLHelper {
    private static Logger log = LogManager.getLogger(SQLHelper.class);

    public String assembleCountSQL(DBQueryParam param, BaseVO vo, String sql) throws Exception {
        StringBuffer countSQL = new StringBuffer("SELECT  ");
        countSQL.append(buildCountClause());
        countSQL.append(" FROM ").append(vo.tablename()).append(" this ");

        Map placeholders = new HashMap();
        List dateParamList = new ArrayList();
        StringBuffer whereClause = new StringBuffer(buildSQL(param, vo, "this",
                placeholders, dateParamList));
        if (whereClause.length() > 4) {
            countSQL = countSQL.append(" WHERE ").append(whereClause);
        }

        if (log.isDebugEnabled())
            log.debug("count SQL:" + countSQL.toString());
        return countSQL.toString();
    }

    public String assembleSQL(DBQueryParam param, BaseVO vo) throws Exception {
        int _pagesize = Integer.parseInt(CoreConfigInfo.DEFAULT_PAGE_SIZE);
        int _pageno = Integer.parseInt(CoreConfigInfo.DEFAULT_PAGE);
        String _orderby = null, _desc = null;
        try {
            _pagesize = Integer.parseInt(BeanUtils.getProperty(param,
                    "_pagesize"));
        } catch (Exception ex) {
            _pagesize = Integer.MAX_VALUE;
        }
        /** @todo 做一个查询量的限制，以后需要修改 */
        if (_pagesize > CoreConfigInfo.MAX_QUERY_COUNT) {
            _pagesize = CoreConfigInfo.MAX_QUERY_COUNT;
        }

        try {
            _pageno = Integer.parseInt(BeanUtils.getProperty(param,
                    "_pageno"));
        } catch (Exception ex) {
            _pageno = 1;
        }

        try {
            _orderby = BeanUtils.getProperty(param, "_orderby");
        } catch (Exception ex) {
            _orderby = "";
        }

        try {
            _desc = BeanUtils.getProperty(param, "_desc");
        } catch (Exception ex) {
            _desc = "";
        }
        if (_desc == null) {
            _desc = "0"; // 默认递增
        }

        Map placeholders = new HashMap();
        List dateParamList = new ArrayList();
        StringBuffer whereClause = new StringBuffer(buildSQL(param, vo, "this",
                placeholders, dateParamList));

        StringBuffer selectSQL = new StringBuffer(buildSelectFileds(param, vo));

        selectSQL.append(" FROM ").append(
                vo.tablename()).append(" this ");
        if (whereClause.toString().trim().length() > 0) {
            selectSQL = selectSQL.append(" WHERE ").append(whereClause);
        }

        if (_orderby != null && _orderby.trim().length() > 0
                && _desc != null) {
            selectSQL = selectSQL.append(" order by ").append(getNewOrderby(_orderby, _desc, "this."));
        }


//                if (_pagesize != 0) {
//                    query.setMaxResults(_pagesize);
//                    query.setFirstResult(_pagesize * (_pageno - 1));
//                }

        return selectSQL.toString();
    }

    /**
     * 以DBQueryParam中SelectFields指定的字段，构造查询的字段，空则查询全部
     *
     * @param param DBQueryParam
     */
    private String buildSelectFileds(Object param, BaseVO vo) {
        StringBuffer selectHQL = new StringBuffer("");
        try {
            DBQueryParam dpparam = (DBQueryParam) param;
            if (param != null && dpparam.getSelectFields() != null) {
                for (Object o : dpparam.getSelectFields()) {
                    if (selectHQL.length() > 0) selectHQL.append(",");
                    selectHQL.append(" this.").append(o);
                }
            }
            String head = "SELECT NEW " + vo.tablename() + "(";
            if (selectHQL.length() > 0) selectHQL.insert(0, head).append(")");
            else selectHQL.append("SELECT *");
        } catch (Exception e) {
            log.catching(e);
        }
        return selectHQL.toString();
    }

    private String buildCountClause() {
        return " count(*) ";
    }

    /**
     * 获取实体类的主键名称，支持多主键
     *
     */
    protected String[] getPKs() throws Exception {
//        Session session = currentSession();

//        ClassMetadata metadata = session.getSessionFactory().getClassMetadata(this.voClass);

//        if (metadata == null) {
//            throw new NullPointerException("voClass " + voClass.getName() + " not configed in hibernate config!");
//        }
//        Type type = metadata.getIdentifierType();

        String[] pkNames = null;
//        if (!(type instanceof CompositeType)) {
//            String pkName = metadata.getIdentifierPropertyName();
//            pkNames = new String[1];
//            pkNames[0] = pkName;
//
//        } else {
//            CompositeType aType = (CompositeType) type;
//            pkNames = aType.getPropertyNames();
//        }
        return pkNames;
    }

    protected Map getConditions(Object param) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {

        // 静态条件部分
        Map props = BeanUtils.describe(param);

        // new 动态条件部分 add by hekun
        if (param instanceof DBQueryParam) {
            DBQueryParam listVO = (DBQueryParam) param;
            Map queryConditions = listVO.getQueryConditions();

            if (queryConditions != null && queryConditions.size() > 0) {
                // 将静态条件加入动态条件中，重复的动态条件及其值将被覆盖。
                for (Iterator keys = props.keySet().iterator(); keys.hasNext(); ) {
                    String key = (String) keys.next();
                    Object value = props.get(key);
                    if (key.startsWith("_") && value != null)
                        queryConditions.put(key, value);
                }
                props = queryConditions;
            }
        }
        return props;
    }

    public String buildSQL(Object param, BaseVO vo, String prefix, Map placeholders,
                           List dateParamList) throws Exception {
        return buildSQL(param, vo, prefix, placeholders, dateParamList, 1);
    }

    public String buildSQL(Object param, BaseVO vo, String prefix, Map placeholders,
                           List dateParamList, int ph) throws Exception {

        if (param instanceof DBQueryParam) {
            DBQueryParam queryParam = (DBQueryParam) param;
            if (queryParam.isUseFixedParamOnly())
                return "";
        }

        if (prefix == null) {
            prefix = "";
        }

        if (prefix.trim().length() > 0) {
            prefix = prefix + ".";
        }

        StringBuffer whereClause = new StringBuffer();

        String key, field;
        Object value;

        Map props = getConditions(param);

        // 为查询条件排序，使用方法是设置ListVO的_firstitems属性，比如
        // listVO.set_firstitems("_ne_xxx,_se_yyy");
        // 那么生成的sql语句中关于_ne_xxx、_se_yyy的条件（假如有）就会排在前两位。
        // ----add by lwl
        List orderedKeys = getOrderedKeyset(props.keySet(), param);

        for (Iterator iter = orderedKeys.iterator(); iter.hasNext(); ) {
            key = (String) iter.next();
            // value = props.get(key)!=null ? String.valueOf( props.get(key) ) :
            // null;
            if (props.get(key) != null && props.get(key) instanceof Collection) {
                value = props.get(key);
            } else {
                value = props.get(key) != null ? props.get(key) : null;
            }

            // 忽略无值的参数
            if (value == null) {
                continue;
            }

            // 对于余下的检查是否符合规则
            try {
                field = key.substring(key.indexOf('_', 1) + 1);
            } catch (Exception ex) {
                continue;
            }

            // null条件的处理
            if (key.startsWith("_sn_") || key.startsWith("_dn_")
                    || key.startsWith("_nn_")) {
                whereClause = whereClause.append("(" + prefix + field
                        + " is null) and ");
            } else if (key.startsWith("_snn_") || key.startsWith("_dnn_")
                    || key.startsWith("_nnn_")) {
                whereClause = whereClause.append("(" + prefix + field
                        + " is not null) and ");
            } else {
                // 非 null条件的，参数必须有值
                if (value instanceof Collection) {
                    if (((Collection) value).size() <= 0) {
                        continue;
                    }
                } else {
                    if (value.toString().trim().length() <= 0) {
                        continue;
                    }
                }
                // 对日期类型做特殊处理，所以先保存下对应的参数名，例如k1，k2等
                // _dn _dnn _dl _dnm _de _dnl _dm _dne _din _dnin
                if (key.startsWith("_d")) {
                    if (key.startsWith("_dnl_") || key.startsWith("_dnm_")
                            || key.startsWith("_de_")
                            || key.startsWith("_dnn_")
                            || key.startsWith("_dl_") || key.startsWith("_dn_")
                            || key.startsWith("_dm_")
                            || key.startsWith("_dne_")
                            || key.startsWith("_din_")
                            || key.startsWith("_dnin_")) {
                        dateParamList.add("k" + ph);
                    }
                }
                // 只有向placeholders添加元素，才累加ph;
                // ----- modified by zhangsiwei
                if (key.startsWith("_sql_")) {
                    whereClause = whereClause.append("(" + value + ") and ");
                } else if (key.startsWith("_sl_") || key.startsWith("_dl_")
                        || key.startsWith("_nl_")) {
                    whereClause = whereClause.append("(" + prefix + field
                            + " < :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_snm_") || key.startsWith("_dnm_")
                        || key.startsWith("_nnm_")) {
                    whereClause = whereClause.append("(" + prefix + field
                            + " <= :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_se_") || key.startsWith("_de_")
                        || key.startsWith("_ne_")) {
                    whereClause = whereClause.append("(" + prefix + field
                            + " = :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_snl_") || key.startsWith("_dnl_")
                        || key.startsWith("_nnl_")) {
                    whereClause = whereClause.append("(" + prefix + field
                            + " >= :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_sm_") || key.startsWith("_dm_")
                        || key.startsWith("_nm_")) {
                    whereClause = whereClause.append("(" + prefix + field
                            + " > :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_sne_") || key.startsWith("_dne_")
                        || key.startsWith("_nne_")) {
                    whereClause = whereClause.append("(" + prefix + field
                            + " <> :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_sin_") || key.startsWith("_din_")
                        || key.startsWith("_nin_")) {
                    ph = buildSQLInOrNotInCondition(value, whereClause, prefix,
                            field, ph, placeholders, true);
                } else if (key.startsWith("_snin_") || key.startsWith("_dnin_")
                        || key.startsWith("_nnin_")) {
                    ph = buildSQLInOrNotInCondition(value, whereClause, prefix,
                            field, ph, placeholders, false);
                } else if (key.startsWith("_sei_")) {
                    whereClause = whereClause.append("( lower(" + prefix
                            + field + ") = lower( :k" + ph + ")) and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_snei_")) {
                    whereClause = whereClause.append("( lower(" + prefix
                            + field + ") <> lower( :k" + ph + ")) and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_sk_")) {
                    value = "%" + value + "%";
                    whereClause = whereClause.append("(" + prefix + field
                            + " like :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_ssw_")) {
                    value = value + "%";
                    whereClause = whereClause.append("(" + prefix + field
                            + " like :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_sew_")) {
                    value = "%" + value;
                    whereClause = whereClause.append("(" + prefix + field
                            + " like :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_snk_")) {
                    value = "%" + value + "%";
                    whereClause = whereClause.append("(" + prefix + field
                            + " not like :k" + ph + ") and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_ski_")) {
                    value = "%" + value + "%";
                    whereClause = whereClause.append("( lower(" + prefix
                            + field + ") like lower( :k" + ph + ")) and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                } else if (key.startsWith("_snki_")) {
                    value = "%" + value + "%";
                    whereClause = whereClause.append("( lower(" + prefix
                            + field + ") not like lower( :k" + ph + ")) and ");
                    placeholders.put(String.valueOf("k" + ph++), value);
                }
            }

            if (value != null && value.getClass() == String.class) {  //只对string类型做类型兼容型判断，避免给数值字段传入string的值
//				for this key
                PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(vo, field);
//				处理数值类型。
                Object ovalue = null;
                try {
                    if (pd != null && pd.getPropertyType() != String.class) {
                        ph--;
                        ovalue = placeholders.get(String.valueOf("k" + ph));
                        if (ovalue != null)
                            if (pd.getPropertyType() == Long.class) {

                                Long lvalue = Long.valueOf((String) ovalue);
                                placeholders.put(String.valueOf("k" + ph), lvalue);
                            } else if (pd.getPropertyType() == Integer.class) {

                                Integer lvalue = Integer.valueOf((String) ovalue);
                                placeholders.put(String.valueOf("k" + ph), lvalue);

                            } else if (pd.getPropertyType() == Short.class) {

                                Short lvalue = Short.valueOf((String) ovalue);
                                placeholders.put(String.valueOf("k" + ph), lvalue);

                            } else if (pd.getPropertyType() == Byte.class) {

                                Byte lvalue = Byte.valueOf((String) ovalue);
                                placeholders.put(String.valueOf("k" + ph), lvalue);
                            }
                        ph++;
                    }
                } catch (NumberFormatException t) {
                    throw new NumberFormatException("Field type not match：field type " + pd.getPropertyType() + ", field value:" + ovalue);
                }
            }
        }

        if (whereClause.length() > 4) {
            whereClause = whereClause.delete(whereClause.length() - 4,
                    whereClause.length() - 1);
        }

        return whereClause.toString();
    }

    protected List getOrderedKeyset(Set keys, Object param) throws Exception {
        List orderedKeyset = new ArrayList();
        if (keys.size() > 0) {
            String firstitems = BeanUtils.getProperty(param,
                    "_firstitems");
            String firstitemname = null;
            if (firstitems != null) {
                for (StringTokenizer st = new StringTokenizer(firstitems, ","); st
                        .hasMoreTokens(); ) {
                    firstitemname = st.nextToken();
                    if (keys.contains(firstitemname)) {
                        orderedKeyset.add(firstitemname);
                    }
                }
            }
            for (Iterator it = keys.iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                if (!orderedKeyset.contains(key))
                    orderedKeyset.add(key);
            }
        }
        return orderedKeyset;
    }

    /**
     * 修正多个orderby字段的bug
     *
     * @param _orderby
     * @param prefix
     */
    private String getNewOrderby(String _orderby,String _desc, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        String[] columns =
                StringUtils.split(_orderby, ",");
        String[] orderTypes =
                StringUtils.split(_desc, ",");

        String orderType = null;
        if(columns.length != orderTypes.length){
            orderType = orderTypes[0];
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < columns.length; i++) {
            sb.append(prefix).append(columns[i]);
            if(orderType != null){
                if("1".equals(orderType)){
                    sb.append(" desc");
                }else{
                    sb.append(" asc");
                }
            }else{
                if("1".equals(orderTypes[i])){
                    sb.append(" desc");
                }else{
                    sb.append(" asc");
                }
            }

            if (i != columns.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private int buildSQLInOrNotInCondition(Object value,
                                           StringBuffer whereClause, String prefix, String field, int ph,
                                           Map placeholders, boolean inOrNotIn) {
        Collection tmpList = (Collection) value;
        int tmpSize = tmpList.size();
        if (inOrNotIn) {
            whereClause = whereClause.append("( " + prefix + field + " in ( ");
        } else {
            whereClause = whereClause.append("( " + prefix + field
                    + " not in ( ");
        }
        Iterator iter = tmpList.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (i == tmpSize - 1) {
                whereClause.append(" :k" + ph + ")) and ");
            } else {
                whereClause.append(" :k" + ph + ",");
            }
            placeholders.put(String.valueOf("k" + ph++), obj);
            i++;
        }
        return ph;
    }

}
