package com.baidu.spark.util.mapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.spark.util.mapper.SparkMapperImpl.MappingConfig;

/**
 * 包含路径回调类.
 * 
 * @author GuoLin
 *
 */
public class IncludePathCallback implements MappingCallback {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/** 包含路径集合. */
	private Set<String> includePaths = new HashSet<String>();
	
	/**
	 * 构造器.
	 * @param includePaths 包含路径列表
	 */
	public IncludePathCallback(String... includePaths) {
		this.includePaths.add("");  // 根对象
		for (String path : includePaths) {
			addAllPartsOfPathToSet(path, this.includePaths);
		}
	}
	
	/**
	 * 允许给定批量前缀的构造器.
	 * <p>参数<code>includePathMap</code>的value为路径列表(与{@link #IncludePathCallback(String...)}的参数相同)，
	 * key为此路径列表中将会统一增加的前缀</p>
	 * 
	 * <p>以下是一个简单的例子, 其逻辑为包含<code>card.id</code>和<code>card.name</code>两个字段(从根开始):
	 * <pre>
	 * map.put("card", new String[] { "id", "name" });
	 * IncludePathCallback callback = new IncludePathCallback(map);</pre>
	 * </p>
	 * 
	 * <p>如果希望包含直接属性, 可以将key设置为空字符串<code>""</code>:
	 * <pre>
	 * map.put("", new String[] { "id", "name" });
	 * IncludePathCallback callback = new IncludePathCallback(map);</pre>
	 * </p>
	 * 
	 * <p>此构造器在需要复用原有includePaths数组时尤为有用, 例如:
	 * <pre>
	 * String[] cardTypePaths = new String[] { "id", "name" };
	 * CardType newCardType = mapper.clone(cardType, new IncludePathCallback(cardTypePaths));
	 * ...
	 * String[] cardPaths = new String[] { "id", "title", "detail" };
	 * map.put("", cardPaths);
	 * map.put("type", cardTypePaths);
	 * Card newCard = mapper.clone(card, map);</pre>
	 * 最终结果为：<code>id, title, detail, type.id, type.name</code>
	 * </p>
	 * 
	 * @param includePathMap 包含路径的Map，其key为前缀，value为路径列表
	 */
	public IncludePathCallback(Map<String, String[]> includePathMap) {
		if (includePathMap == null) {
			throw new IllegalArgumentException("Parameter includePathMap must not be null.");
		}
		
		this.includePaths.add("");  // 根对象
		
		// 拼装前缀
		for (Map.Entry<String, String[]> entry : includePathMap.entrySet()) {
			String prefix = entry.getKey();
			if (prefix == null) {
				continue;
			}
			String[] paths = entry.getValue();
			if (paths == null || paths.length == 0) {
				continue;
			}
			for (String path : paths) {
				String includePath = (prefix.length() == 0) ? path : new StringBuilder(prefix).append(".").append(path).toString();
				addAllPartsOfPathToSet(includePath, this.includePaths);
			}
		}
	}

	@Override
	public Object callback(Object sourceParent, Object destinationParent, Object source,
			Object destination, MappingConfig config) {
		
		if (!includePaths.contains(config.getPath())) {
			logger.debug("Ignore path: '{}'", config.getPath());
			return null;
		}
		
		return CONTINUE;
	}
	
	/**
	 * 将路径逐级保存到集合中.
	 * <p>
	 * 例如：给出<code>path = a.b.c</code>，将保存<code>a, a.b, a.b.c</code>三个值到目标集合中.
	 * </p>
	 * @param path 待处理的path
	 * @param paths path每段保存的目标集合
	 */
	private static void addAllPartsOfPathToSet(String path, Set<String> paths) {
		if (paths == null) {
			return;
		}
		paths.add(path);
		int loc = path.lastIndexOf('.');
		if (loc < 0) {
			return;
		}
		addAllPartsOfPathToSet(path.substring(0, loc), paths);
	}

}
