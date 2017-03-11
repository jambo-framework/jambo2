package org.jambo2.jop.infrastructure.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jambo2.jop.common.utils.PublicUtils;
import org.jambo2.jop.exception.JOPException;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 基础实体
 * @author jinbo
 *
 */
public class BaseVO implements Serializable{
	public static final Logger log = LogManager.getLogger(AbstractBO.class);
	private static final long serialVersionUID = 9999L;
	private static final String[] types1={"int","java.lang.String","boolean","char","float","double","long","short","byte"};
	private static final String[] types2={"Integer","java.lang.String","java.lang.Boolean","java.lang.Character","java.lang.Float","java.lang.Double","java.lang.Long","java.lang.Short","java.lang.Byte"};

	private String idname = null;
	private String idColumn = null;

	/**
	 * 获取POJO对应的表名
	 * 需要POJO中的属性定义@Table(name)
	 * @return
	 */
	public String tablename() {
		Table table = this.getClass().getAnnotation(Table.class);
		if(table != null)
			return table.name();
		else
			throw new JOPException("undefine POJO @Table, need Tablename(@Table)");
	}

	/**
	 * 获取POJO对应的主键名称
	 * 需要POJO中的属性定义@Id
	 */
	public String id() {
		if (idname == null) {
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Id.class)) {
					idname = field.getName();
					break;
				}
			}
			if (idname == null) throw new JOPException("undefine POJO @Id");
		}
		return idname;
	}

	public String getIdValue()  {
		try {
			return  BeanUtils.getProperty(this, id());
		} catch (Exception e) {
			log.catching(e);
		}
		return null;
	}

	/**
	 * 获取POJO对应的主键名称
	 * 需要POJO中的属性定义@Id
	 */
	public String getIdColumn() {
		if (idColumn == null) {
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Id.class)) {
					Column column = field.getAnnotation(Column.class);
					if ("".equals(column.name()))
						idColumn = field.getName();
					else idColumn = column.name();
					break;
				}
			}
			if (idColumn == null) throw new JOPException("undefine POJO @Id & @Column");
		}
		return idColumn;
	}

	/**
	 * 用于存放POJO的列信息
	 */
	private transient static Map<Class<? extends BaseVO>,List<String>> columnMap = new HashMap<Class<? extends BaseVO>, List<String>>();

	private boolean isNull(String fieldname) {
		try {
			Field field = this.getClass().getDeclaredField(fieldname);
			return isNull(field);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean isNull(Field field) {
		try {
			field.setAccessible(true);
			return field.get(this) == null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return false;
	}

	public Map getClumnAndValue(){
		HashMap map = new HashMap();

		Field[] fields = this.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class) ) {
				Column column = field.getAnnotation(Column.class);
				String value = reflectValue(this, field);
				if (!PublicUtils.isBlankString(value)){
					if ("".equals(column.name()))
						map.put(field.getName(), value);
					else map.put(column.name(), value);
				}
			}
		}
		System.out.println(map);
		return map;
	}

	public Map getIDAndValue() {
		HashMap map = new HashMap();
		try {
			map.put(getIdColumn(), getIdValue());
		} catch (Exception e) {
			log.catching(e);
		}
		System.out.println(map);
		return map;
	}

	/**
	 * 用于计算类定义
	 * 需要POJO中的属性定义@Column(name)
	 */
	public void caculationColumnList() {
		if(columnMap.containsKey(this.getClass()))
			return;

		Field[] fields = this.getClass().getDeclaredFields();
		List<String> columnList = new ArrayList<String>(fields.length);

		for(Field field : fields) {
			if(field.isAnnotationPresent(Column.class))
				columnList.add(field.getName());
		}

		columnMap.put(this.getClass(), columnList);
	}

	/**
	 * 获取用于WHERE的 有值字段表
	 * @return
	 */
	public List<WhereColumn> getWhereColumnsName() {
		Field[] fields = this.getClass().getDeclaredFields();
		List<WhereColumn> columnList = new ArrayList<WhereColumn>(fields.length);

		for(Field field : fields) {
			if(field.isAnnotationPresent(Column.class) && !isNull(field))
				columnList.add(new WhereColumn(field.getName(), field.getGenericType().equals(String.class)));
		}

		return columnList;
	}

	/**
	 * Where条件信息
	 */
	public class WhereColumn {
		public String name;
		public boolean isString;

		public WhereColumn(String name,boolean isString) {
			this.name = name;
			this.isString = isString;
		}
	}

	/**
	 * 用于获取Insert的字段累加
	 */
	public String getInsertColumnsName() {
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		for(String column : list) {
			if(isNull(column))
				continue;

			if(i++ != 0)
				sb.append(',');
			sb.append(column);
		}
		return sb.toString();
	}

	/**
	 * 用于获取Insert的字段映射累加
	 * @return
	 */
	public String getInsertColumnsDefine() {
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		for(String column : list) {
			if(isNull(column))
				continue;

			if(i++ != 0)
				sb.append(',');
			sb.append("#{").append(column).append('}');
		}
		return sb.toString();
	}

	/**
	 * 用于获取Update Set的字段累加
	 * @return
	 */
	public String getUpdateSet() {
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		for(String column : list) {
			if(isNull(column))
				continue;

			if(i++ != 0)
				sb.append(',');
			sb.append(column).append("=#{").append(column).append('}');
		}
		return sb.toString();
	}

	/**
	 * 转化POJO为JSON格式
	 * 需要org.json包支持,可以在json官网下载源码,或自己实现json编码
	 * @return
	 */
	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	/**
	 * 打印类字段信息
	 */
	@Override
	public String toString() {
		Field[] fields = this.getClass().getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for(Field f : fields) {
			if(Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers()))
				continue;
			Object value = null;
			try {
				f.setAccessible(true);
				value = f.get(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if(value != null)
				sb.append(f.getName()).append('=').append(value).append(',');
		}
		sb.append(']');

		return sb.toString();
	}

	public static void setProperties(Object obj, Map map){
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			String dbColumnname = null;
			if ("".equals(column.name()))
				dbColumnname = field.getName();
			else dbColumnname = column.name();

			try {
				Object value = map.get(dbColumnname);
				if (value != null) {
					BeanUtils.setProperty(obj, field.getName(), value);
				}
			} catch (Exception e) {
				log.catching(e);
			}
		}
	}

	public static String reflectValue(Object obj, Field field) {
		String result = "";
		if (field.getType() == Date.class){
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), obj.getClass());
				Method getMethod = pd.getReadMethod();//获得get方法
				Object v = getMethod.invoke(obj);//执行get方法返回一个Object

				if (v != null) {
					result = PublicUtils.utilDateToStr((Date) v);
				}
			} catch (Exception e) {
				log.catching(e);
			}
		} else {
			for (int i = 0; i < types1.length; i++) {
				if (field.getType().getName()
						.equalsIgnoreCase(types1[i]) || field.getType().getName().equalsIgnoreCase(types2[i])) {
					try {
						result = BeanUtils.getProperty(obj, field.getName());
					} catch (Exception e) {
						log.catching(e);
					}
				}
			}
		}
		return  result;
	}
}
