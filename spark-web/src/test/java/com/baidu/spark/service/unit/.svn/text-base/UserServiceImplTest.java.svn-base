package com.baidu.spark.service.unit;

import static com.baidu.spark.TestUtils.sameString;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import com.baidu.spark.dao.UserDao;
import com.baidu.spark.model.User;
import com.baidu.spark.service.impl.UserServiceImpl;

/**
 * 用户服务实现测试用例.
 * <p>
 * jMock的使用可以参见：{@linkplain http://www.jmock.org/cookbook.html}
 * </p>
 * 
 * @author GuoLin
 *
 */
public class UserServiceImplTest {

    private Mockery context = new JUnit4Mockery();
    
    private UserServiceImpl impl = new UserServiceImpl();
    
    private Criteria criteria;
    
    private UserDao userDao;
    
    @Before
    public void before() throws Exception {
        userDao = context.mock(UserDao.class);
        criteria = context.mock(Criteria.class);
        impl.setUserDao(userDao);
    }
    
    @Test
    public void saveUser_smoke() throws Exception {
        final User user = new User();
        user.setUsername("guolin");
        context.checking(new Expectations() {
            {
                oneOf(userDao).save(user);
            } 
        });
        impl.saveUser(user);
    }

    @Test
    public void getUserById_smoke() throws Exception {
        final User user = new User();
        context.checking(new Expectations() {
            {
                oneOf(userDao).get(1L);
                will(returnValue(user));
            } 
        });
        User result = impl.getUserById(1L);
        assertSame(user, result);
    }
    
    @Test
    public void saveUser_usernameIsNull() throws Exception {
        final User user = new User();
        
        context.checking(new Expectations() {
            {
                never(userDao).save(with(any(User.class)));
            } 
        });
        
        try {
            impl.saveUser(user);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void checkConflicts_smoke() throws Exception {
        final User exists = new User();
        exists.setId(12L);
        exists.setUsername("guolin");
        final User detached = new User();
        detached.setUsername("guolin");
        
        context.checking(new Expectations() {
            {
                oneOf(userDao).createCriteria();
                will(returnValue(criteria));
                oneOf(criteria).add(with(sameString(Restrictions.eq("username", detached.getUsername()))));
                oneOf(criteria).add(with(sameString(Restrictions.eq("locked", detached.getLocked()))));
                oneOf(criteria).list();
                will(returnValue(Arrays.asList(exists)));
            }
        });
        
        boolean conflicts = impl.checkConflicts(detached);
        assertTrue(conflicts);
    }
    
    @Test
    public void checkConflicts_detachedUserIdNotNull() throws Exception {
        final User exists = new User();
        exists.setId(12L);
        exists.setUsername("guolin");
        final User detached = new User();
        detached.setId(2L);
        detached.setUsername("guolin");
        
        context.checking(new Expectations() {
            {
                oneOf(userDao).createCriteria();
                will(returnValue(criteria));
                oneOf(criteria).add(with(sameString(Restrictions.eq("username", detached.getUsername()))));
                oneOf(criteria).add(with(sameString(Restrictions.eq("locked", detached.getLocked()))));
                oneOf(criteria).add(with(sameString(Restrictions.ne("id", detached.getId()))));
                oneOf(criteria).list();
                will(returnValue(Arrays.asList(exists)));
            }
        });
        
        boolean conflicts = impl.checkConflicts(detached);
        assertTrue(conflicts);
    }
    
}
