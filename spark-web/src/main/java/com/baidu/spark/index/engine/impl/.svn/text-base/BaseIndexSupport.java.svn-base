package com.baidu.spark.index.engine.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.index.converter.IndexConverter;
import com.baidu.spark.index.engine.IndexEngine;
import com.baidu.spark.model.QueryConditionVO;
import com.baidu.spark.model.QuerySortVO;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.util.LuceneUtils;

/**
 * 索引服务的基类
 * 
 * @author zhangjing_pe
 * @param <I>
 *            索引对象的类型
 */
public abstract class BaseIndexSupport<I> implements IndexEngine<I> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/** 索引数据的基础路径 */
	private String baseIndexPath;
	/** 本类型索引对象的索引数据路径 */
	private String indexPath;
	/** 索引与对象转换器 */
	private IndexConverter<I> converter = null;
	/** 最大查询条目数设置 */
	private static final Integer MAX_QUERY_SIZE = 5120;
	
	private static final Integer BOOLEAN_MAX_CLAUSE_COUNT = 5120;
	/**索引文件存放地址 */
	protected File indexPathFile = null;

	public BaseIndexSupport() {
		BooleanQuery.setMaxClauseCount(BOOLEAN_MAX_CLAUSE_COUNT);
	}
	/**
	 * 创建索引增量或重建索引
	 * 
	 * @param isAdd
	 *            boolean 是否是增量路径，true 删除所有索引，false 不删除索引
	 * @return IndexWriter
	 * @throws IndexException
	 */
	public IndexWriter getIndexWriter(boolean isAdd) throws IndexException {
		File indexPathFile = getIndexPathFile();
		boolean newFlag = indexPathFile.exists() ? isAdd : true;
		IndexWriter writer = null;
			try {
				writer = new IndexWriter(
						FSDirectory.open(indexPathFile),
						LuceneUtils.analyzer, newFlag,
						IndexWriter.MaxFieldLength.UNLIMITED);
			} catch (CorruptIndexException e) {
				logger.error("createIndex error",e);
				throw new IndexException(e);
			} catch (LockObtainFailedException e) {
				logger.error("createIndex error",e);
				throw new IndexException(e);
			} catch (IOException e) {
				logger.error("createIndex error",e);
				throw new IndexException(e);
			}
		
		return writer;
	}

	/**
	 * 创建对应的document对象
	 * 
	 * @param indexable
	 *            I 需要建索引的对象
	 * @return Document
	 */
	protected Document createDocument(I indexable) {
		return getConverter().getIndexableDocument(indexable);
	}

	/**
	 * 打开索引reader
	 * 
	 * @return IndexReader
	 * @throws IndexException
	 * @deprecated 使用search做查询
	 */

	public IndexReader getIndexReader() throws IndexException {
			try {
				return IndexReader.open(FSDirectory.open(getIndexPathFile()));
			} catch (CorruptIndexException e) {
				logger.error("getIndexReader error",e);
				throw new IndexException(e);
			} catch (IOException e) {
				logger.error("getIndexReader error",e);
				throw new IndexException(e);
			}
	}

	@Override
	public synchronized void addIndex(I obj) throws IndexException {
		Document doc = createDocument(obj);
		IndexWriter indexWriter = null;
		try {
			indexWriter = getIndexWriter(false);
			indexWriter.addDocument(doc);
			// sys out debug info
			// indexWriter.setInfoStream(System.out);
		} catch (CorruptIndexException e) {
			logger.error("getIndexReader error",e);
			throw new IndexException(e);
		} catch (IOException e) {
			logger.error("getIndexReader error",e);
			throw new IndexException(e);
		} finally {
			if (indexWriter != null){
				try {
					indexWriter.close();
				} catch (CorruptIndexException ex1) {
					logger.error("close writer error",ex1);
				} catch (IOException ex1) {
					logger.error("close writer error",ex1);
				}
			}
		}
	}

	@Override
	public synchronized void batchAddIndex(Collection<I> objs,
			boolean isCreate, boolean isOptimize) throws IndexException {
		
		IndexWriter indexWriter = null;
		try {
			indexWriter = getIndexWriter(false);
			indexWriter.setMergeFactor(100);
			for (I obj : objs) {
				Document doc = createDocument(obj);
				if(!isCreate){
					deleteIndex(obj, indexWriter);
				}
				indexWriter.addDocument(doc);
			}
			if (isOptimize) {
				indexWriter.optimize();
			}
		} catch (CorruptIndexException e) {
			logger.error("batchAddIndex error",e);
			throw new IndexException(e);
		} catch (IOException e) {
			logger.error("batchAddIndex error",e);
			throw new IndexException(e);
		} finally {
			if (indexWriter != null){
				try {
					indexWriter.close();
				} catch (CorruptIndexException ex1) {
					logger.error("close writer error",ex1);
				} catch (IOException ex1) {
					logger.error("close writer error",ex1);
				}
			}
		}
	}

	@Override
	public synchronized void deleteIndex(I obj) throws IndexException {
		String indexIdName = getConverter().getKeyFieldName();
		Object value = getConverter().getKeyFieldValue(obj);
		deleteIndexByField(indexIdName, value);
	}
	
	private void deleteIndex(I obj,IndexWriter indexWriter) throws IndexException{
		String indexIdName = getConverter().getKeyFieldName();
		Object value = getConverter().getKeyFieldValue(obj);
		deleteIndexByField(indexIdName, value,indexWriter);
	}
	
	@Override
	public synchronized void deleteIndexByField(String fieldName,
			Object fieldValue) throws IndexException {
		IndexWriter indexWriter = null;
		try {
			indexWriter = getIndexWriter(false);
			if (indexWriter == null)
				return;
			deleteIndexByField(fieldName, fieldValue, indexWriter);
		}finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (CorruptIndexException ex1) {
					logger.error("close writer error",ex1);
				} catch (IOException ex1) {
					logger.error("close writer error",ex1);
				}
			}
		}
	}
	
	/**
	 * @param fieldName
	 * @param fieldValue
	 * @param indexWriter
	 * @throws IndexException
	 */
	private void deleteIndexByField(String fieldName, Object fieldValue,
			IndexWriter indexWriter) throws IndexException {
		Query query = null;
		if (fieldValue instanceof Number) {
			query = LuceneUtils.getNumericEqualsQuery(fieldName, fieldValue);
		} else {
			query = LuceneUtils.getStringEqualsQuery(fieldName, fieldValue);
		}
		if (query == null) {
			return;
		}
		try {
			indexWriter.deleteDocuments(query);
		} catch (CorruptIndexException e) {
			logger.error("deleteIndexByField error",e);
			throw new IndexException(e);
		} catch (IOException e) {
			logger.error("deleteIndexByField error",e);
			throw new IndexException(e);
		} 
	}

	@Override
	public synchronized void updateIndex(I obj) throws IndexException {
		deleteIndex(obj);
		addIndex(obj);
	}

	protected List<I> findIndexByFieldValues(Map<String, Object> fieldWithValue,
			Filter filter, Sort sort, Pagination<I>... pageBeans)
			throws IndexException {
		List<I> retValues = null;
		BooleanQuery boolQuery = new BooleanQuery();
		for (Map.Entry<String, Object> entry : fieldWithValue.entrySet()) {
			String fieldName = (String) entry.getKey();
			Object value = entry.getValue();
			Query query = null;
			if (value instanceof Number) {
				query = LuceneUtils.getNumericEqualsQuery(fieldName, value);

			} else {
				query = LuceneUtils.getStringEqualsQuery(fieldName, value);
			}

			boolQuery.add(query, BooleanClause.Occur.MUST);
		}
		retValues = findIndexByQuery(filter, sort, boolQuery, pageBeans);

		return retValues;
	}

	@Override
	public synchronized void clearAllIndex() throws IndexException {
		IndexWriter indexWriter = null;
		try {
			indexWriter = getIndexWriter(false);
			if (indexWriter != null) {
				indexWriter.deleteAll();
			}
		} catch (CorruptIndexException e) {
			logger.error("clearAllIndex error",e);
			throw new IndexException(e);
		} catch (IOException e) {
			logger.error("clearAllIndex error",e);
			throw new IndexException(e);
		} finally {
			if (indexWriter != null){
				try {
					indexWriter.close();
				} catch (CorruptIndexException ex1) {
					logger.error("close writer error",ex1);
				} catch (IOException ex1) {
					logger.error("close writer error",ex1);
				}
			}
		}
	}
	/**
	 * 根据query和filter返回lucene的document对象
	 * @param filter
	 * @param sort
	 * @param query
	 * @param pageBean
	 * @return
	 * @throws IndexException
	 */
	protected List<Document> findDocumentByQuery(Filter filter, Sort sort, Query query,
			Pagination<I>... pageBean) throws IndexException {
		Assert.notNull(query);
		Assert.isTrue(sort == null || (sort.getSort() != null
				&& sort.getSort().length > 0),
				"lucene sort should have field if not null");
		IndexSearcher searcher = null;
		TopDocs hits = null;
		try {
			searcher = new IndexSearcher(FSDirectory.open(getIndexPathFile()));
			if (sort == null) {
				if (filter == null) {
					hits = searcher.search(query, MAX_QUERY_SIZE);
				} else {
					hits = searcher.search(query, filter, MAX_QUERY_SIZE);
				}
			} else {
				hits = searcher.search(query, filter, MAX_QUERY_SIZE, sort);
			}
			Pagination<I> pb = pageBean != null && pageBean.length > 0 ? pageBean[0]
					: null;
			int start = 0;
			int end = hits.scoreDocs.length;
			if (pb != null) {
				pb.setTotal(hits.scoreDocs.length);
				int actualPageNo = pb.getPage()>1?pb.getPage():1;
				start = (actualPageNo - 1) * pb.getSize();
				end = Math.min(hits.scoreDocs.length, start + pb.getSize());
			}
			
			List<Document> documents = new ArrayList<Document>();
			while (start < end) {
				int docId = hits.scoreDocs[start].doc;
				Document doc = searcher.doc(docId);
				start++;
				documents.add(doc);
			}
			return documents;
		} catch (CorruptIndexException e) {
			logger.error("findIndexByQuery error",e);
			throw new IndexException(e);
		} catch (IOException e) {
			logger.error("findIndexByQuery error",e);
			throw new IndexException(e);
		}finally {
			if (searcher != null){
				try {
					searcher.close();
				} catch (IOException ex1) {
					logger.error("close searcher error",ex1);
				}
			}
		}
	}

	/**
	 * 根据query和filter进行排序和分页
	 * @param filter 过滤器
	 * @param sort 排序器。若排序规则为空，则此对象应设置为null。否则lucene抛出异常
	 * @param query query不能为null，否则查询到的结果为空 。可使用 {@link org.apache.lucene.search.MatchAllDocsQuery}
	 * @param pageBean
	 * @return
	 * @throws IndexException
	 */
	@SuppressWarnings("unchecked")
	protected List<I> findIndexByQuery(Filter filter, Sort sort, Query query,
			Pagination<I>... pageBean) throws IndexException {
		Pagination<I> pb = pageBean != null && pageBean.length > 0 ? pageBean[0]: null;
		List<Document> documentList = findDocumentByQuery(filter, sort, query, pb);
		List<I> retValues = new ArrayList<I>(documentList.size());
		retValues = getObjectFromDocument(documentList);
		if (pb != null) {
			pb.setResults(retValues);
		}
		return retValues;
	}
	
	@Override
	public List<I> findIndexByQueryVO(List<QueryConditionVO> queryConditionVOList,List<QuerySortVO> sortList) throws IndexException{
		return findIndexByQueryVO(queryConditionVOList, sortList,null);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<I> findIndexByQueryVO(List<QueryConditionVO> queryConditionVOList,List<QuerySortVO> sortList, Pagination<I> pageBean) throws IndexException{
		List<I> ret = null;
		QueryVO queryVo = new QueryVO(queryConditionVOList, sortList);
		Query query = getConverter().getQueryFromVo(queryVo);
		Sort sort = new Sort();
		if (query != null) {
			logger.debug("query:{}", query.toString());
		}
		if (sort != null) {
			logger.debug("sort:{}", sort.toString());
		}
		sort.setSort(getConverter().getSortFromVo(queryVo).toArray(new SortField[]{}));
		if(pageBean == null){
			ret = findIndexByQuery(null, sort, query);	
		}else{
			ret = findIndexByQuery(null, sort, query, pageBean);
		}
			
		return ret;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<I> findIndexByFieldValues(Map<String,Object> mapValue, Pagination<I> pageBean) throws IndexException{
		return findIndexByFieldValues(mapValue, null, null, pageBean);
	}
	@Override
	public List<I> findIndexByFieldValues(Map<String,Object> mapValue) throws IndexException{
		return findIndexByFieldValues(mapValue, null, null);
	}
	/**
	 * 将document转化为业务对象
	 * 
	 * @param document
	 *            lucene查询结果的docuement
	 * @return
	 */
	@SuppressWarnings("unused")
	private I getObjectFromDocument(Document document) {
		return getConverter().getOriginObj(document);
	}
	
	
	/**
	 * 将document转化为业务对象
	 * 
	 * @param document
	 *            lucene查询结果的docuement
	 * @return
	 */
	private List<I> getObjectFromDocument(List<Document> document) {
		return getConverter().getOriginObj(document);
	}
	/**
	 * 设置索引数据的基本路径
	 * 
	 * @param baseIndexPath 基本路径
	 */
	@Value("#{properties['lucene.indexDirPath']}")
	protected void setBaseIndexPath(String baseIndexPath) {
		this.baseIndexPath = baseIndexPath;
	}

	/**
	 * 获取此业务对象的索引数据路径
	 * 
	 * @return
	 */
	protected File getIndexPathFile() {
		if(indexPathFile == null){
			indexPath = baseIndexPath
			+ "/"
			+ getConverter().generatePojo().getClass()
					.getSimpleName();
			indexPathFile = new File(indexPath);
		}
		
		return indexPathFile;
	}

	public IndexConverter<I> getConverter() {
		return converter;
	}

	public void setConverter(IndexConverter<I> converter) {
		this.converter = converter;
	}
}
