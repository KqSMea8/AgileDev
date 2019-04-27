package com.baidu.spark.service.impl.helper.copyspace.transformer.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.baidu.spark.dao.SpaceSequenceDao;
import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceSequence;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.SpaceSequenceMetadata;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.DeserializeError;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.InvalidDataError;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.json.JsonAppender;
import com.baidu.spark.util.json.JsonUtils;

/**
 * spacesequence的转换器
 * @author zhangjing_pe
 *
 */
public class SpaceSequenceTransformer extends PojoTransformer<SpaceSequenceMetadata, SpaceSequence> {
	
	private SpaceSequenceDao spaceSequenceDao = null;

	@Override
	public SpaceSequenceMetadata exportMetadata(Space space) {
		SpaceSequence sequence = spaceSequenceDao.get(space.getId());
		SpaceSequenceMetadata metadata = new SpaceSequenceMetadata();
		List<SpaceSequence> list = new ArrayList<SpaceSequence>();
		list.add(sequence);
		metadata.setResultData(getJson(list));
		return metadata;
	}
	@Override
	public String getJson(List<SpaceSequence> sequences) {
		Assert.notNull(sequences);
		Assert.isTrue(sequences.size() == 1);
		SpaceSequence sequence = sequences.get(0);
		JsonAppender appender = new JsonAppender();
		appender.append("nextCardSeqNum", 1L).append(
				"nextCardTypeLocalId",
				sequence.getNextCardTypeLocalId() == null ? 1L : sequence
						.getNextCardTypeLocalId()).append(
				"nextCardPropertyLocalId",
				sequence.getNextCardPropertyLocalId() == null ? 1L : sequence
						.getNextCardPropertyLocalId()).append(
				"nextListValueLocalId",
				sequence.getNextListValueLocalId() == null ? 1L : sequence
						.getNextListValueLocalId());
		return appender.getJsonString();
	}
	
	@Override
	public SpaceSequenceMetadata getMetadata(String jsonData) {
		SpaceSequenceMetadata metadata = new SpaceSequenceMetadata();
		metadata.setResultData(jsonData);
		SpaceSequence sequence = JsonUtils.getObjectByJsonString(jsonData,SpaceSequence.class);
		metadata.addPojo(sequence);
		return metadata;
	}
	@Override
	protected void importPojos(Space space,List<SpaceSequence> spaceSequenceList,List<ImportOption<?>> importOptions){
		Assert.notNull(spaceSequenceList);
		Assert.isTrue(spaceSequenceList.size() == 1);
		SpaceSequence pojo = spaceSequenceList.get(0);
		SpaceSequence dbPojo = spaceSequenceDao.get(space.getId());
		dbPojo.setNextCardPropertyLocalId(pojo.getNextCardPropertyLocalId());
		dbPojo.setNextCardTypeLocalId(pojo.getNextCardTypeLocalId());
		dbPojo.setNextListValueLocalId(pojo.getNextListValueLocalId());
		spaceSequenceDao.save(dbPojo);	
	}

	@Override
	public List<ValidationResult> validateImportData(String jsonData) {
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		try{
			SpaceSequenceMetadata metadata = getMetadata(jsonData);
			if(metadata == null||metadata.getPojos()==null||metadata.getPojos().size()!=1){
				results.add(new InvalidDataError(SpaceSequence.class,MessageHolder.get("spacecopy.validation.error.invalidData.spaceSequence.notFound")));
				return results;
			}
			SpaceSequence sequence = metadata.getPojos().get(0);
			if (sequence.getId() != null
					|| sequence.getNextCardPropertyLocalId() == null
					|| sequence.getNextCardPropertyLocalId() <= 0L
					|| sequence.getNextCardTypeLocalId() == null
					|| sequence.getNextCardTypeLocalId() <= 0L
					|| sequence.getNextListValueLocalId() == null
					|| sequence.getNextListValueLocalId() <= 0) {
				results.add(new InvalidDataError(SpaceSequence.class));
			}
		}catch(SparkRuntimeException e){
			results.add(new DeserializeError(SpaceSequence.class,e));
		}
		return results;
	}
	@Override
	public boolean match(Metadata metadata){
		return (metadata instanceof SpaceSequenceMetadata);
	}

	
	@Autowired
	public void setSpaceSequenceDao(SpaceSequenceDao spaceSequenceDao) {
		this.spaceSequenceDao = spaceSequenceDao;
	}

}
