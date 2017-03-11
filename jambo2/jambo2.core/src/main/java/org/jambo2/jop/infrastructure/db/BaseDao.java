package org.jambo2.jop.infrastructure.db;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 基础dao
 * @author jinbo
 */
public interface BaseDAO {
	/**
	 * 保存，基类只提供定义，需要业务类自己提供mybatis的sql定义
	 * @param vo 实体
	 * @return 返回保存后的id，默认返回整数型，需要其它类型的自己实现
	 */
	int create(BaseVO vo);
	
	/**
	 * 根据id删除实体，基类只提供定义，需要业务类自己提供mybatis的sql定义
	 * @param id 要删除的主键id
	 */
	void delete(int id);
	
	/**
	 * 更新实体，基类只提供定义，需要业务类自己提供mybatis的sql定义
	 * @param vo 实体
	 */
	void update(BaseVO vo);
	
	/**
	 * 查询所有，基类只提供定义，需要业务类自己提供mybatis的sql定义
	 * @return 返回list数组
	 */
	List<BaseVO> queryAll();

	/**
	 * 查询数据表中记录集合总数，基类只提供定义，需要业务类自己提供mybatis的sql定义
	 * @return 返回查询总条数
	 */
	int queryCount();

	/**
	 * 根据ID查询实体信息
	 * @param id 实体ID
	 * @return 返回base实体
	 */
	BaseVO findByPK(Integer id);

	//===============以下为通用数据操作实现====================
	/**
	 * 分页查询
	 * @param pageNo 页码
	 * @param pageSize 显示条数
	 * @param orderBy 排序字段
	 * @param order order 排序方式,asc or desc
	 * @return 返回list数组
	 */
	List<BaseVO> queryByPage(@Param("pageNo") String pageNo, @Param("pageSize") String pageSize, @Param("orderBy") String orderBy, @Param("asc") String order);

	/**
	 * 动态sql查询
	 * @param table 表名称
	 * @param fields list集合
	 * @param wheres 条件　都是key-value对应
	 * @param begin 开始位置
	 * @param end 结束位置
	 * @param order 排序方式,true:asc;fales:desc
	 * @return 返回查询结果
	 */
	@SuppressWarnings("rawtypes")
	List queryBySQL(@Param("table") String table, @Param("fields") List<String> fields, @Param("wheres") Map wheres, @Param("begin") Integer begin, @Param("end") Integer end, @Param("order") String order);

	/**
	 * 单项结果sql查询
	 * @param table 表名称
	 * @param wheres 条件　都是key-value对应
     */
	List<Map<String,Object>> findBySQL(@Param("table") String table, @Param("wheres") Map wheres);

	/**
	 * 总数
	 * @param table　表名称
	 * @param wheres　条件　都是key-value对应
	 * @return　总数
	 */
	int countBySQL(@Param("table") String table, @Param("wheres") Map wheres);
	
	/**
	 *动态SQL更新
	 * @param table 表名称
	 * @param fields list集合每个map都是key-value对应
	 * @param wheres 条件 都是key-value对应
	 */
	void updateBySQL(@Param("table") String table, @Param("fields") Map fields, @Param("wheres") Map wheres);
	
	/**
	 * 动态SQL删除
	 * @param table 表名称
    * @param wheres 條件 都是key-value对应
	 */
	
	void deleteBySQL(@Param("table") String table, @Param("wheres") Map wheres);
	
	/**
	 * 添加记录
	 * @param table 表名称
     * @param fields 编号
	 */
	void insertBySQL(@Param("table") String table, @Param("fields") Map fields);
	
	/**
	 * 根据id集合实现批量的删除
	 * @param ids id集合
	 */
	void delete(@Param("ids") String[] ids);
}
