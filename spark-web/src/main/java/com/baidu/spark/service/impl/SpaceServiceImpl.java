package com.baidu.spark.service.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.dao.SpaceDao;
import com.baidu.spark.dao.SpaceSequenceDao;
import com.baidu.spark.dao.SpacegroupDao;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.index.engine.CardIndexEngine;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceSequence;
import com.baidu.spark.model.space.Spacegroup;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.service.GroupService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserViewService;

/**
 * 空间服务实现类.
 * 
 * @author GuoLin
 *
 */
@Service
public class SpaceServiceImpl implements SpaceService {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private SpaceDao spaceDao;
	
	private SpaceSequenceDao spaceSeqDao ;
	
	private GroupService groupService;
	
	private PermissionService permissionService;
	
	private CardIndexEngine cardIndexEngine;
	
	private UserViewService userViewService;

	private SpacegroupDao spacegroupDao;

	@Override
	public Pagination<Space> getAllSpaces(Pagination<Space> page) {
		return spaceDao.findAll(page);
	}
	
	@Override
	public List<Space> getAllSpaces() {
		return spaceDao.findAll();
	}
	
	@Override
	public Space getSpaceByPrefixCode(String prefixCode){
		Assert.hasText(prefixCode);
		return spaceDao.findUniqueByProperty("prefixCode", prefixCode);
	}

	@Override
	public void saveSpace(Space space) {
		Assert.notNull(space);
		Assert.hasText(space.getPrefixCode());
		Assert.hasText(space.getName());
		Assert.notNull(space.getType());
		Assert.isNull(space.getId());
		spaceDao.save(space);
		
		SpaceSequence sequence = new SpaceSequence();
		sequence.setId(space.getId());
		sequence.setNextCardSeqNum(1L);
		sequence.setNextCardTypeLocalId(1L);
		sequence.setNextCardPropertyLocalId(1L);
		sequence.setNextListValueLocalId(1L);
		spaceSeqDao.save(sequence);
		//创建acl资源数据
		permissionService.createAcl(space);
	}
	
	@Override
	public void updateSpace(Space space) {
		Assert.notNull(space);
		Assert.hasText(space.getPrefixCode());
		Assert.hasText(space.getName());
		Assert.notNull(space.getType());
		Assert.notNull(space.getId());
		spaceDao.save(space);
	}
	
	@Override
	public void deteleSpace(Space space){
		Assert.notNull(space);
		spaceDao.bulkDeleteCards(space);
		//删除卡片的索引
		try {
			cardIndexEngine.deleteIndexByField("space", space.getId());
		} catch (IndexException e) {
			logger.error("index Exception--space:"+space.getId(), e);
		}
		userViewService.deleteAllInSpace(space);
		
		for(Spacegroup sp : space.getSpaceGroups()){
			sp.getSpaces().remove(space);
			spacegroupDao.update(sp);
		}
		groupService.deleteGroups(space);
		//删除空间对象
		permissionService.deletePermission(space);
		spaceDao.delete(space);
		
	}
	
	@Override
	public boolean checkConflicts(String prefixCode) {
		return checkConflicts(prefixCode, null);
	}
	
	@Override
	public boolean checkConflicts(Space space) {
		Assert.notNull(space);
		return checkConflicts(space.getPrefixCode(), space.getId());
	}
	
	/**
	 * 检测prefixCode是否已经存在.
	 * @param prefixCode 新空间的prefixCode
	 * @param id 已有空间的id，如果为空则不进行id检测
	 * @return 如果prefixCode冲突则返回true，否则返回false
	 */
	protected boolean checkConflicts(String prefixCode, Long id) {
		Assert.notNull(prefixCode);
		Criteria criteria = spaceDao.createCriteria();
		criteria.add(Restrictions.eq("prefixCode", prefixCode));
		if (id != null) {
			criteria.add(Restrictions.ne("id", id));
		}
		return criteria.list().size() != 0;
	}
	
	@Override
	public Space getSpace(Long spaceId){
		return spaceDao.get(spaceId);
	}
	
	public Long getNextSpaceSeq(Long spaceId){
		return spaceSeqDao.getCardSeqAndIncrease(spaceId);
	}
	
	@Autowired
	public void setSpaceDao(SpaceDao spaceDao) {
		this.spaceDao = spaceDao;
	}
	@Autowired
	public void setSpaceSeqDao(SpaceSequenceDao spaceSeqDao) {
		this.spaceSeqDao = spaceSeqDao;
	}
	@Autowired
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}
	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	@Autowired
	public void setUserViewService(UserViewService userViewService) {
		this.userViewService = userViewService;
	}
	@Autowired
	public void setCardIndexEngine(CardIndexEngine cardIndexEngine) {
		this.cardIndexEngine = cardIndexEngine;
	}
	@Autowired
	public void setSpacegroupDao(SpacegroupDao spacegroupDao) {
		this.spacegroupDao = spacegroupDao;
	}

}
