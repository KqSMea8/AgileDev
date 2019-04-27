package com.baidu.spark.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 字符串的处理工具类
 * @author Adun
 * 2010-06-03
 */
public class StringUtils {

	//默认的分割字符
	public static final String DEFAULT_SPLIT_CHAR = "(\\,)|(，)|( )|(;)|(；)";
	
	//默认的连接字符
	public static final char  DEFAULT_COMBINE_CHAR = ',';
	
	private static JsonFactory jsonFactory = new JsonFactory();
	
	/**
	 * 返回一个字符串是否为null或者长度为0
	 * @param s
	 * @return
	 */
	public static Boolean isEmpty(String s){
		return null==s || 0==s.trim().length();
	}
	
	/**
	 * 返回一个字符串是否不为空.(不为null且长度不为0)
	 * @param s
	 * @return
	 */
	public static Boolean notEmpty(String s){
		return ! isEmpty(s);
	}

	/**
	 * 按照给定的char(严格匹配),进行源字符串的拆分.
	 * @param src
	 * @param separator
	 * @return
	 */
	public static List<String> split(String src,char separator){
		if (null == src){
			return new ArrayList<String>();
		}
		char[] chars = src.toCharArray();
		List<Integer> indexList = new ArrayList<Integer>(); 
		int size = 1;
		for(int i=0;i<chars.length;i++){
			char c = chars[i];
			if(c == separator){
				size ++;
				indexList.add(i);
			}
		}
		indexList.add(src.length());
		List<String> ret = new ArrayList<String>();
		int from = -1;
		for(int i=0;i<size;i++){
			String str = null;
			if(from == src.length()-1){
				str = "";
			}else{
				str =src.substring(from+1,indexList.get(i));
			}
			ret.add(str);
			from = indexList.get(i);
		}
		return ret;
	}
	
	/**
	 * 更换为html格式(替换\n为<br>,替换\t为四个空格)
	 * @param s
	 * @return
	 */
	public static String changeToHtmlForm(String s){
		if (null == s){
			return "";
		}
		return s.replaceAll("\n","<br>").replaceAll("\t","&nbsp;&nbsp;&nbsp;&nbsp;");
	}
	
	/**
	 * get the html form of the full trace of one Exception.
	 * @param e
	 * @return
	 */
	public static String getHtmlExceptionStackTrace(Exception e){
		if(e!=null){
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			writer.flush();
			writer.close();
			return changeToHtmlForm(sw.getBuffer().toString());
		}
		return "";
	}
	
	
	
	/**
	 * split one string into a list of substrings with default separator.
	 * @param s
	 * @return
	 */
	public static List<String> split(String s){
		try{
			String[] ss = splitToArray(s,DEFAULT_SPLIT_CHAR);
			List<String> list = new ArrayList<String>();
			for (String subs : ss){
				if (null != subs && subs.length() > 0){
					list.add(subs);
				}
			}
			return list;
		}catch(Exception e){
			return new ArrayList<String>();
		}
	}
	
	public static List<Long> splitAndParseLong(String s, String splitChar){
		String[] ss = splitToArray(s,DEFAULT_SPLIT_CHAR);
		List<Long> list = new ArrayList<Long>(ss.length);
		for (String subs : ss){
			 try{
				 list.add(Long.parseLong(subs));
			 }catch(NumberFormatException e){
			 }
		}
		return list;
	}
	
	/**
	 * 根据字符串和分隔符,将字符串分割为数组
	 * @param s
	 * @return
	 */
	public static String[] splitToArray(String s, String splitChar){
		try{
			return s.split(splitChar);
		}catch(Exception e){
			return new String[]{};
		}
	}
	
	/**
	 * 根据字符串,将字符串分割为数组
	 * @param s
	 * 使用默认分隔符.
	 * @return
	 */
	public static String[] splitToArray(String s){
		return splitToArray(s,DEFAULT_SPLIT_CHAR);
	}
	
	/**
	 * 将String转化为Integer格式.如果出现无法转换等异常,则返回0
	 */
	public static Integer parseInt(String s){
		Integer i;
		try{
			i = Integer.parseInt(s);
		}catch(Exception e){
			i = 0;
		}
		return i;
	}
	
	/**
	 * 将String转化为Long格式.如果出现无法转换等异常,则返回0
	 */
	public static Long parseLong(String s){
		Long i;
		try{
			i = Long.parseLong(s);
		}catch(Exception e){
			i = 0L;
		}
		return i;
	}
	
	/**
	 * 将字符串转为html格式:
	 * 转换\n为<br>
	 * 转换\t为4个&nbsp;
	 * 转换空格为&nbsp;
	 * @param s
	 * @return
	 */
	public static String transHtml(String s){
		return s.replaceAll("\n","<br>").replaceAll(" ","&nbsp;").replaceAll("\t","&nbsp;&nbsp;&nbsp;&nbsp;");
	}
	
	/**
	 * 将字符串转为html格式:
	 * 转换\n为<br>
	 * 转换\t为4个&nbsp;
	 * @param s
	 * @return
	 */
	public static String transHtmlWithoutSpace(String s){
		return s.replaceAll("\n","<br>").replaceAll("\t","&nbsp;&nbsp;&nbsp;&nbsp;");
	}
	
	/**
	 * 将抛出的异常的trace转化为字符串.行与行之间用\n隔开
	 * @param e
	 * @return
	 */
	public static String getExceptionStackTrace(Exception e){
		if(e!=null){
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			writer.flush();
			writer.close();
			return sw.getBuffer().toString();
		}
		return "";
	}
	
	/**
	 * remove the last character if the string is not empty
	 * @param s
	 * @return
	 */
	public static String removeLast(String s){
		if (notEmpty(s)){
			return s.substring(0, s.length()-1);
		}else{
			return s;
		}
	}
	
	/**
	 * combine the objects to a string with defined combiner-char
	 * @param objArray
	 * @param separator
	 * @return
	 */
	public static String combineToString(Object[] objArray, char combiner){
		StringBuffer sb = new StringBuffer();
		if (null != objArray){
			for (Object obj:objArray){
				if (null == obj){
					continue;
				}
				sb.append(obj.toString() + combiner);
			}
		}
		return removeLast(sb.toString());
	}
	
	/**
	 * combine the objects to a string with default combiner-char
	 * @param objArray
	 * @return
	 */
	public static String combineToString(Object[] objArray){
		return combineToString(objArray, DEFAULT_COMBINE_CHAR);
	}
	
	public static String toUnicode(String str){
        char[]arChar=str.toCharArray();
        int iValue=0;
        String uStr="";
        for(int i=0;i<arChar.length;i++){
            iValue=(int)str.charAt(i);           
            if(iValue<=256){
              // uStr+="&#x00"+Integer.toHexString(iValue)+";";
                uStr+="\\u00"+Integer.toHexString(iValue);
            }else{
              // uStr+="&#x"+Integer.toHexString(iValue)+";";
                uStr+="\\u"+Integer.toHexString(iValue);
            }
        }
        return uStr;
    }
	
	/**
	 * 返回一个对象的，经过转义的json字符串。如果失败,就返回默认字符串
	 * 
	 * @param o
	 *            要输出的对象
	 * @param defaultValue
	 *            默认字符串
	 */
	public static String getEscapedJsonString(Object o, String defaultValue) {
		if (o == null && defaultValue != null) {
			return defaultValue;
		} else {
			StringWriter writer = new StringWriter();
			try {
				JsonGenerator g = jsonFactory.createJsonGenerator(writer);
				// 需要传一个ObjectCodec，否则不能序列号Date等对象
				g.setCodec(new ObjectMapper());
				g.writeObject(o);
				g.close();
				return writer.toString();
			} catch (JsonProcessingException e) {
				return defaultValue;
			} catch (IOException e) {
				return defaultValue;
			}
		}
	}
	
	/**
	 * 返回一个对象的，经过转义的json字符串。如果失败,就返回空字符串
	 * 
	 * @param o
	 *            要输出的对象
	 */
	public static String getEscapedJsonString(Object o) {
		return getEscapedJsonString(o, "");
	}
	
	public static void main(String args[]){
		System.out.println(split("a,b,c",','));
	}
}
