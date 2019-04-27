package com.baidu.spark.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.baidu.spark.annotations.BAVE;
import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.model.Discussion;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.service.DiscussionService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.SpringSecurityUtils;

/**
 * 讨论区服务的实现类
 * 
 * @author shixiaolei
 */
@Service
public class DiscussionServiceImpl implements DiscussionService {

	/** UserService */
	private UserService userService;
	/** RestTemplate */
	private RestTemplate template;
	/** ECMP颁发的认证token */
	private String token;
	/** 系统在ECMP中对应的sourceId */
	private String sourceId;

	private static final Long MSG_MAX_LENGTH = 65535L;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public Discussion getById(Long id, Card card) {
		Assert.notNull(id);
		Assert.notNull(card);
		
		Map resp = template.getForObject(appendTokenToUrl("/feeds/{id}"), HashMap.class, id);
		return generateDiscussion(resp);
	}

	@Override
	public Discussion deleteDiscussion(Long discussionId, Card card) {
		Assert.notNull(discussionId);
		Assert.notNull(card);
		Assert.notNull(card.getId());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", SpringSecurityUtils.getCurrentUserName());
		map.put("status", Discussion.STATE_REMOVE);

		template.put(appendTokenToUrl("/feeds/{id}"), map, discussionId);
		return getById(discussionId, card);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Discussion replyDiscussion(String msg, Long parentDiscussionId, Card card) {
		Assert.notNull(parentDiscussionId);
		Assert.notNull(card);
		Assert.notNull(msg);
		Assert.isTrue(msg.length() < MSG_MAX_LENGTH);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", SpringSecurityUtils.getCurrentUserName());
		map.put("msg", msg);
		map.put("parentId", parentDiscussionId);
		map.put("type", "discussion");
		if (card.getProject() != null) {
			map.put("extraMsg", "{projectId:" + card.getProject().getIcafeProjectId() + "}");
		}
		Map resp = template.postForObject(appendTokenToUrl("/feeds"), map,
				HashMap.class);
		return generateDiscussion(resp);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Discussion replyCard(String msg, Card card) {
		Assert.notNull(card);
		Assert.notNull(msg);
		Assert.isTrue(msg.length() < MSG_MAX_LENGTH);

		if (isFirstReply(card)) {
			initCardInBave(card);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", SpringSecurityUtils.getCurrentUserName());
		map.put("msg", msg);
		map.put("type", "discussion");
		if (card.getProject() != null) {
			map.put("extraMsg", "{projectId:" + card.getProject().getIcafeProjectId() + "}");
		}
		map.put("parentBizId", generateCardBizId(card));
		Map resp = template.postForObject(appendTokenToUrl("/feeds"), map,
				HashMap.class);
		return generateDiscussion(resp);
	}

	@Override
	public Discussion updateDiscussion(String msg, Long discussionId, Card card) {
		Assert.notNull(discussionId);
		Assert.notNull(card);
		Assert.notNull(card.getId());
		Assert.notNull(msg);
		Assert.isTrue(msg.length() < MSG_MAX_LENGTH);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", SpringSecurityUtils.getCurrentUserName());
		map.put("msg", msg);
		template.put(appendTokenToUrl("/feeds/{id}"), map, discussionId);

		return getById(discussionId, card);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Discussion> getAllDiscussionsInCard(Card card) {
		try {
			Map resp = template.getForObject(
					appendTokenToUrl("/{sourceId}/feeds/{bizId}/tree"),
					HashMap.class, sourceId, generateCardBizId(card));
			List<Map> children = (List<Map>) resp.get("children");
			return generateDiscussions(children);
		} catch (ResponseStatusException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatus())) {
				return Collections.<Discussion>emptyList();
			} else {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/**
	 * 检查此卡片是否是第一次被回复
	 * @param card
	 * @return
	 */
	private boolean isFirstReply(Card card) {
		return getAllDiscussionsInCard(card).isEmpty();
	}

	/**
	 * 如果卡片是第一次回复，那么往ECMP发送请求创建一个虚拟父节点代表该卡片.
	 * @param card
	 */
	private void initCardInBave(Card card) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("bizId", generateCardBizId(card));
		request.put("type", "card");
		request.put("title", card.getTitle());
		request.put("msg", card.getDetail());
		request.put("username", SpringSecurityUtils.getCurrentUserName());
		if (card.getProject() != null) {
			request.put("extraMsg", "{projectId:" + card.getProject().getIcafeProjectId() + "}");
		}
		template.postForObject(appendTokenToUrl("/feeds"), request, HashMap.class);
	}

	/**
	 * 为卡片上成一个BIZ ID
	 * 
	 * @param card
	 * @return
	 */
	private static String generateCardBizId(Card card) {
		return "spark-space" + card.getSpace().getId() + "-card" + card.getId();
	}

	/**
	 * 将token加到url后面
	 * 
	 * @param url
	 * @return
	 */
	private String appendTokenToUrl(String url) {
		return url + "?token=" + token;
	}
	
	/**
	 * 根据MAP返回Discussion
	 * 
	 * @param map
	 * @param card
	 * @return
	 */
	private Discussion generateDiscussion(Map<String, Object> map) {
		Discussion discussion = new Discussion();
		discussion.setId(NumberUtils.createLong(map.get("id").toString()));
		discussion.setContent(map.get("msg").toString());
		discussion.setStatus((Integer) map.get("status"));
		discussion.setReplyId(Long.parseLong(map.get("parentId").toString()));
		
		String username = (String) map.get("creator");
		User user = userService.getUserByUserName(username);
		if (user != null) {
			discussion.setUserId(user.getId());
			discussion.setUserName(user.getName());
		}
		Object createDate = map.get("createDate");
		Object editDate = map.get("editDate");
		if (editDate != null) {
			discussion.setLastModifyTime(new Date(NumberUtils.createLong(editDate.toString())));
		} else if (createDate != null) {
			discussion.setLastModifyTime(new Date(NumberUtils.createLong(createDate.toString())));
		} 
		// 如果parentBizId不为null，则它是卡片的回复
		if (map.get("parentBizId") != null) {
			map.put("parentId", null);
		}
		return discussion;
	}

	@SuppressWarnings("unchecked")
	private List<Discussion> generateDiscussions(List<Map> discussionMapList) {
		if(discussionMapList == null || discussionMapList.isEmpty()) {
			return Collections.<Discussion>emptyList();
		}
		List<Discussion> list = new LinkedList<Discussion>();
		for (Map discussionMap : discussionMapList) {
			try {
				Discussion dis = generateDiscussion(discussionMap);
				List<Map> children = (List<Map>) discussionMap.get("children");
				dis.setReplyList(generateDiscussions(children));
				list.add(dis);
			} catch (Exception e) {
				logger.debug("Discussion {id} is bad format", discussionMap.get("id"));
			}
		}
		return list;
	}

	@Autowired
	@BAVE
	public void setTemplate(RestTemplate template) {
		this.template = template;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Value("#{properties['discussion.rest.token']}")
	public void setToken(String token) {
		this.token = token;
	}

	@Value("#{properties['discussion.rest.sourceId']}")
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

}
