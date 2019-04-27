package com.baidu.spark.index.engine.helper;

import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.ListProperty;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: SYSTEM
 * Date: 11-3-11
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */
public class ListFieldComparatorSource extends FieldComparatorSource{
	private ListProperty property;

	public ListFieldComparatorSource(ListProperty property){
		super();
		Assert.notNull(property);
		this.property = property;
	}

	@Override
	public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
		return new ListFieldComparator(this.property);
	}
}
