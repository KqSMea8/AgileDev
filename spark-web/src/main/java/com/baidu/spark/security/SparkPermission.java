package com.baidu.spark.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.acls.domain.AbstractPermission;
import org.springframework.security.acls.model.Permission;

 /**
 * spark中的权限分类的acl实现
 * @author zhangjing_pe
 *
 */
public class SparkPermission extends AbstractPermission {
	
	private static final long serialVersionUID = 9069932050219276534L;
	
	public static final Permission NONE = new SparkPermission(0,'N');
	
	/**10种权限定义*/
	public static final Permission READ = new SparkPermission(1 << 0, 'R'); // 1
	
	public static final Permission CREATE_CHILDREN = new SparkPermission(1 << 1, 'C'); // 2
    
	public static final Permission WRITE = new SparkPermission(1 << 2, 'W'); // 4
    
	public static final Permission DELETE = new SparkPermission(1 << 3, 'D'); // 8
    
	public static final Permission ADMIN = new SparkPermission(1 << 4, 'A'); // 16
    
    private static final List<Permission> permissions = new ArrayList<Permission>();
    
    static{
    	permissions.add(READ);
    	permissions.add(CREATE_CHILDREN);
    	permissions.add(WRITE);
    	permissions.add(DELETE);
    	permissions.add(ADMIN);
    }
    
    protected SparkPermission(int mask) {
        super(mask);
     }

     protected SparkPermission(int mask, char code) {
         super(mask, code);
     }
     
     public static List<Permission> getPermissions(){
    	 return permissions;
     }
}
     