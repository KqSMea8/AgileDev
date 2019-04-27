package com.baidu.spark.index.converter;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

import com.baidu.spark.model.QueryVO;
/**
 * 索引数据与对象的转换器
 * @author zhangjing_pe
 *
 * @param <I>可被索引的业务对象
 */
public interface IndexConverter<I> {
	/**
	 * 根据索引document对象获取业务对象
	 * @param document lucene document对象
	 * @return 业务对象
	 */
	public I getOriginObj(final Document document);
	
	/**
	 * 根据索引document对象列表获取业务对象列表
	 * @param documents 
	 * @return
	 */
	public List<I> getOriginObj(final List<Document> documents);
	/**
	 * 将业务对象转换为lucene的document对象
	 * @param indexable 业务对象
	 * @return lucene的document对象
	 */
	public Document getIndexableDocument(final I indexable);
	/**
	 * 获取业务对象的主键属性名。如，对于Card，getKeyFieldName返回“id”
	 * @return 主键属性的名称.
	 */
	public String getKeyFieldName();
	/**
	 * 获取业务对象的主键属性值。如对于card，getKeyFieldValue返回card.getId()的值
	 * @param obj 业务对象
	 * @return 主键属性的值
	 */
	public Object getKeyFieldValue(final I obj);
	/**
	 * 获取业务对象实例
	 * @return
	 */
	public I generatePojo();
	/**
	 * 根据查询vo，获取lucene query对象;
	 * vo格式要求形如[字段][操作符][值],
	 * 如果格式非法，则抛出SparkRuntimeException
	 * @param queryVO 查询vo
	 * @return lucene query对象
	 */
	public Query getQueryFromVo(final QueryVO queryVo);
	/**
	 * 根据查询vo，获取lucene的排序对象
	 * @param vo
	 * @return
	 */
	public List<SortField> getSortFromVo(final QueryVO queryVo);
	
}
