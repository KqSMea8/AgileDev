package com.baidu.spark.index.engine.helper;

import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.ListProperty;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: SYSTEM
 * Date: 11-3-11
 * Time: 下午1:32
 * To change this template use File | Settings | File Templates.
 */
public class ListFieldComparator extends FieldComparator {
	private Map<String, Integer> indexMap = new HashMap<String, Integer>();
	private List<String> values = new ArrayList<String>();
	private List<String> currentReaderValues = new ArrayList<String>();
	private String field;
	private String bottom;

	public ListFieldComparator(ListProperty property){
		super();
		Assert.notNull(property);
		Assert.isTrue(ListProperty.TYPE.equals(property.getType()));
		setSortIndexMap(property);
		this.field = property.getId().toString();
	}

	private void setSortIndexMap(ListProperty property){
		List<String> listKey = property.getListKey();
		for (String key : listKey){
			indexMap.put(key, indexMap.size());
		}
	}

	@Override
	public int compare(int slot1, int slot2) {
		Integer index1 = indexMap.get(values.get(slot1));
		Integer index2 = indexMap.get(values.get(slot2));
		if (null == index1){
			return 1;
		}
		if (null == index2){
			return -1;
		}
		return index1 - index2;
	}

	@Override
	public void setBottom(int slot) {
		if (slot > this.currentReaderValues.size()){
			slot = this.currentReaderValues.size() - 1;
		}
		this.bottom = this.currentReaderValues.get(slot);
	}

	@Override
	public int compareBottom(int doc) throws IOException {
		return this.compare(this.values.size(), doc);
	}

	@Override
	public void copy(int slot, int doc) throws IOException {
		if (this.values.size() > slot){
			this.values.remove(slot);
		}
		this.values.add(slot, this.currentReaderValues.get(doc));
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase) throws IOException {
		this.currentReaderValues = Arrays.asList(FieldCache.DEFAULT.getStrings(reader, this.field));
	}

	@Override
	public Comparable value(int slot) {
		return slot;
	}
}
