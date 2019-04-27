package com.baidu.spark.service.impl.helper.copyspace.transformer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.util.Assert;

import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.security.SparkPermission;
import com.baidu.spark.service.GroupService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.GroupMetadata;
import com.baidu.spark.service.impl.helper.copyspace.option.CheckboxImportOption;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.transformer.Transformer;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.DeserializeError;
import com.baidu.spark.util.json.CollectionAppender;
import com.baidu.spark.util.json.JsonAppender;
import com.baidu.spark.util.json.JsonUtils;

/**
 * group的转换器
 * 
 * @author zhangjing_pe
 * 
 */
public class GroupTransformer implements
		Transformer<GroupMetadata> {

	private GroupService groupService;

	private UserService userService;
	
	private PermissionService permissionService;

	private static final String JSON_ROOT_GROUP = "groupList";

	@Override
	public void importMetadata(Space space, String jsonData,List<ImportOption<?>> importOptions) {
		boolean importGoupInfo = true;
		boolean importUser = true;
		for(ImportOption<?> option:importOptions){
			if(option.getKey().equals(GroupMetadata.IMPORT_GROUP_INFO_KEY)&&!((CheckboxImportOption<?>)option).checked()){
				importGoupInfo = false;
			}
			if(option.getKey().equals(GroupMetadata.IMPORT_GROUP_USER_KEY)&&!((CheckboxImportOption<?>)option).checked()){
				importUser = false;
			}
		}
		//不导入直接返回
		if(!importGoupInfo){
			return;
		}
		GroupMetadata metadata = getMetadata(jsonData);
		List<Group> groups = metadata.getGroups();
		List<Integer> permissions = metadata.getPermission();
		
		
		
		for(int i=0;i<groups.size();i++){
			Group group = groups.get(i);
			Integer permission = permissions.get(i);
			
			group.setOwner(space);
			if(!importUser){
				group.setUsers(null);
			}
			groupService.saveGroup(group, space, permission);
		}
	}
	
	public String getJson(final Space space,List<Group> list) {
		JsonAppender appender = new JsonAppender();
		
		appender.appendList(JSON_ROOT_GROUP, new CollectionAppender<Group>(list) {
			@Override
			public String[] getNames() {
				return new String[] { "name", "exposed", "locked", "users","permission" };
			}

			@Override
			protected Object[] getValues(Group obj) {
				StringBuilder sb = new StringBuilder();
				if (CollectionUtils.isNotEmpty(obj.getUsers())) {
					for (User user : obj.getUsers()) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append(user.getUsername());
					}
				}
				
				Permission permission = permissionService.getMergedPermission(obj,space);
				return new Object[] { obj.getName(),
						obj.getExposed() == null ? true : obj.getExposed(),
						obj.getLocked() == null ? false : obj.getLocked(),
						sb.toString(),permission ==null? SparkPermission.NONE.getMask():permission.getMask()};
			}
		});
		return appender.getJsonString();
	}
	
	@Override
	public GroupMetadata getMetadata(String jsonData) {
		GroupMetadata metadata = new GroupMetadata();
		List<Group> groupList = new ArrayList<Group>();
		List<Integer> permissionList = new ArrayList<Integer>();
		Map<String, ArrayList<LinkedHashMap<String, String>>> groupListMap = JsonUtils
				.getObjectByJsonString(
						jsonData,
						new TypeReference<HashMap<String, ArrayList<LinkedHashMap<String, String>>>>() {
						});
		if (groupListMap == null || groupListMap.size() == 0) {
			return metadata;
		}
		List<LinkedHashMap<String, String>> mapList = groupListMap
				.get(JSON_ROOT_GROUP);
		if (CollectionUtils.isNotEmpty(mapList)) {
			for (LinkedHashMap<String, String> map : mapList) {
				Group group = new Group();
				group.setName(map.get("name"));
				group.setExposed(Boolean.parseBoolean(map.get("exposed")));
				group.setLocked(Boolean.parseBoolean(map.get("locked")));

				if (StringUtils.isNotBlank(map.get("users"))) {
					StringTokenizer st = new StringTokenizer(map.get("users"),
							",");
					while (st.hasMoreTokens()) {
						String username = st.nextToken();
						User user = userService.getUserByUserName(username);
						if (user != null) {
							group.addUser(user);
						}
					}
				}
				Integer permission = Integer.parseInt(map.get("permission"));
				
				groupList.add(group);
				permissionList.add(permission);
			}
		}
		metadata.setGroups(groupList);
		metadata.setPermission(permissionList);
		return metadata;
	}

	@Override
	public GroupMetadata exportMetadata(Space space) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		List<Group> groupList = groupService.getGroups(space);
		GroupMetadata metadata = new GroupMetadata();
		metadata.setResultData(getJson(space,groupList));
		return metadata;
	}

	@Override
	public boolean match(Metadata metadata) {
		if (metadata instanceof GroupMetadata) {
			return true;
		}
		return false;
	}

	@Override
	public List<ValidationResult> validateImportData(String jsonData) {
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		try {
			@SuppressWarnings("unused")
			GroupMetadata metadata = getMetadata(jsonData);
		} catch (Exception e) {
			results.add(new DeserializeError(Group.class,e));
		}
		return results;
	}

	@Autowired
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	
	

}
