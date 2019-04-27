package com.baidu.spark.util.mapper.unit;

import static com.baidu.spark.TestUtils.assertReflectionEquals;
import static com.baidu.spark.TestUtils.newCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dozer.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.baidu.spark.util.mapper.ExcludePathCallback;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperImpl;

/**
 * Spark映射器测试用例.
 * 
 * @author GuoLin
 *
 */
public class SparkMapperTest {
	
	private SparkMapper mapper;
	
	@Before
	public void before() {
		mapper = new SparkMapperImpl();
	}
	
	@Test
	public void clone_smoke() {
		Date date1 = new Date();
		Date date2 = new Date();
		
		Mock parent1 = new Mock();
		parent1.setPPrimativeInt(10);
		parent1.setPInteger(11);
		parent1.setPPrimativeLong(100l);
		parent1.setPLong(1000l);
		parent1.setPString("Parent1");
		parent1.setPPrimativeShort(Short.parseShort("1"));
		parent1.setPShort(Short.valueOf("2"));
		parent1.setPPrimativeBoolean(true);
		parent1.setPBoolean(false);
		parent1.setPDate(date1);
		parent1.setType(MockType.A);
		
		Mock child1 = new Mock();
		child1.setPPrimativeInt(10);
		child1.setPInteger(11);
		child1.setPPrimativeLong(100l);
		child1.setPLong(1000l);
		child1.setPString("Testing");
		child1.setPPrimativeShort(Short.parseShort("1"));
		child1.setPShort(Short.valueOf("2"));
		child1.setPPrimativeBoolean(true);
		child1.setPBoolean(false);
		child1.setPDate(date2);
		child1.setType(MockType.B);
		child1.setParent(parent1);
		child1.setPStringArray(new String[] { "a", "b", "c" });
		child1.setPPrimativeIntegerArray(new int[] { 1, 2, 3 });
		HashSet<String> hashSet = new HashSet<String>();
		hashSet.add("hash set");
		child1.setPStringSet(hashSet);
		List<String> list = new ArrayList<String>();
		list.add("list");
		child1.setPStringList(list);
		child1.setPStringSet(hashSet);
		SortedSet<String> sortedSet = new TreeSet<String>();
		sortedSet.add("sorted set");
		child1.setPStringSortedSet(sortedSet);
		
		Mock newMock = mapper.clone(child1);
		assertNotSame(child1.getPPrimativeIntegerArray(), newMock.getPPrimativeIntegerArray());
		assertNotSame(child1.getPStringArray(), newMock.getPStringArray());
		assertReflectionEquals(child1, newMock);
	}
	
	@Test
	public void clone_primitiveWrapperProperty() {
		PrimitiveWrapperMock mock = new PrimitiveWrapperMock();
		mock.setB(true);
		mock.setBy(new Byte((byte)1));
		mock.setC((char)2);
		mock.setD((double)109.32);
		mock.setF((float)2993.1231);
		mock.setI(100);
		mock.setL(100L);
		mock.setS("Am I a primitive wrapper?");
		
		PrimitiveWrapperMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
	}

	@Test
	public void clone_primitiveProperty() {
		PrimitiveMock mock = new PrimitiveMock();
		mock.setB(true);
		mock.setBy((byte)1);
		mock.setC((char)2);
		mock.setD((double)109.32);
		mock.setF((float)2993.1231);
		mock.setI(100);
		mock.setL(100L);
		PrimitiveMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
	}

	@Test
	public void clone_otherBasicObjectProperty() throws Exception {
		Date now = new Date();
		BasicPropertyMock mock = new BasicPropertyMock();
		mock.setBigDecimal(new BigDecimal("100.23123"));
		mock.setBigInteger(new BigInteger("100203"));
		mock.setClazz(String.class);
		mock.setDate(now);
		mock.setFile(new File("C:/"));
		mock.setSqlDate(new java.sql.Date(now.getTime()));
		mock.setSqlTime(new java.sql.Time(now.getTime()));
		mock.setSqlTimestamp(new java.sql.Timestamp(now.getTime()));
		mock.setUrl(new URL("http://www.baidu.com"));
		BasicPropertyMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
	}

	@Test
	public void clone_enumProperty() {
		EnumPropertyMock mock = new EnumPropertyMock();
		mock.setType(EnumPropertyMock.EnumType.A);
		EnumPropertyMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
	}

	@Test
	public void clone_referenceProperty() {
		Reference1Mock mock1 = new Reference1Mock();
		mock1.setId("This is reference1");
		Reference2Mock mock2 = new Reference2Mock();
		mock2.setId("This is reference2");
		mock1.setR2(mock2);
		Reference1Mock newMock1 = mapper.clone(mock1);
		assertReflectionEquals(mock1, newMock1);
		assertReflectionEquals(newMock1.getR2(), mock1.getR2());
		assertNotSame(newMock1.getR2(), mock1.getR2());
	}

	@Test
	public void clone_cyclicReference_direct() {
		Reference1Mock mock1 = new Reference1Mock();
		mock1.setId("This is reference1");
		mock1.setR1(mock1);
		Reference1Mock newMock1 = mapper.clone(mock1);
		assertReflectionEquals(mock1, newMock1);
		assertSame(newMock1, newMock1.getR1());
	}

	@Test
	public void clone_cyclicReference_far() {
		Reference1Mock mock1 = new Reference1Mock();
		mock1.setId("This is reference1");
		Reference2Mock mock2 = new Reference2Mock();
		mock2.setId("This is reference2");
		mock1.setR2(mock2);
		mock2.setR1(mock1);
		Reference1Mock newMock1 = mapper.clone(mock1);
		assertReflectionEquals(mock1, newMock1);
		assertReflectionEquals(newMock1.getR2(), mock1.getR2());
		assertNotSame(newMock1.getR2(), mock1.getR2());
		assertSame(newMock1.getR2().getR1(), newMock1);
	}

	@Test
	@Ignore("需要实现，这里陈小惠报了一个bug!")
	public void clone_cyclicReference_far_inSameArray() {
		
	}

	@Test
	@Ignore("需要实现!")
	public void clone_HashSetWithOverridedHashCode() {
		
	}

	@Test
	public void clone_primitiveArray() {
		PrimitiveArrayMock mock = new PrimitiveArrayMock();
		mock.setB(new boolean[] { true, false });
		mock.setBy(new byte[] { (byte)1, (byte)2 });
		mock.setC(new char[] { (char)49, (char)50 });
		mock.setD(new double[] { 1.23, 3.232 });
		mock.setF(new float[] { (float)23.23, (float)213.2323 });
		mock.setI(new int[] { 3, 4, 5 });
		mock.setL(new long[] { 6, 7, 8 });
		PrimitiveArrayMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
		assertNotSame(mock.getB(), newMock.getB());
		assertNotSame(mock.getBy(), newMock.getBy());
		assertNotSame(mock.getC(), newMock.getC());
		assertNotSame(mock.getD(), newMock.getD());
		assertNotSame(mock.getF(), newMock.getF());
		assertNotSame(mock.getI(), newMock.getI());
		assertNotSame(mock.getL(), newMock.getL());
	}

	@Test
	public void clone_primitiveWrapperArray() {
		PrimitiveWrapperArrayMock mock = new PrimitiveWrapperArrayMock();
		mock.setB(new Boolean[] { true, false });
		mock.setBy(new Byte[] { (byte)1, (byte)2 });
		mock.setC(new Character[] { (char)49, (char)50 });
		mock.setD(new Double[] { 1.23, 3.232 });
		mock.setF(new Float[] { (float)23.23, (float)213.2323 });
		mock.setI(new Integer[] { 3, 4, 5 });
		mock.setL(new Long[] { 6L, 7L, 8L });
		PrimitiveWrapperArrayMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
		assertNotSame(mock.getB(), newMock.getB());
		assertNotSame(mock.getBy(), newMock.getBy());
		assertNotSame(mock.getC(), newMock.getC());
		assertNotSame(mock.getD(), newMock.getD());
		assertNotSame(mock.getF(), newMock.getF());
		assertNotSame(mock.getI(), newMock.getI());
		assertNotSame(mock.getL(), newMock.getL());
	}
	
	@Test
	public void clone_basicObjectArray() throws Exception {
		Date now = new Date();
		Date later = new Date(now.getTime() + 123213);
		BasicArrayPropertyMock mock = new BasicArrayPropertyMock();
		mock.setBigDecimal(new BigDecimal[] { new BigDecimal("100.23123"), new BigDecimal("345345.23") });
		mock.setBigInteger(new BigInteger[] { new BigInteger("100203"), new BigInteger("1234324") });
		mock.setClazz(new Class<?>[] { String.class, Integer.class });
		mock.setDate(new Date[] { now, later });
		mock.setFile(new File[] { new File("C:/"), new File("D:/") });
		mock.setSqlDate(new java.sql.Date[] { new java.sql.Date(now.getTime()), new java.sql.Date(later.getTime()) });
		mock.setSqlTime(new java.sql.Time[] { new java.sql.Time(now.getTime()), new java.sql.Time(later.getTime()) });
		mock.setSqlTimestamp(new java.sql.Timestamp[] { new java.sql.Timestamp(now.getTime()), new java.sql.Timestamp(later.getTime()) });
		mock.setUrl(new URL[] { new URL("http://www.baidu.com"), new URL("http://www.sina.com") });
		BasicArrayPropertyMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
	}
	
	@Test
	public void clone_referenceArray() {
		ReferenceArrayMock mock = new ReferenceArrayMock();
		mock.setId("base mock");
		Reference1Mock ref1 = new Reference1Mock();
		ref1.setId("ref1");
		Reference1Mock ref2 = new Reference1Mock();
		ref2.setId("ref2");
		mock.setRefs(new Reference1Mock[] { ref1, ref2 });
		ReferenceArrayMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
		assertNotSame(mock.getRefs(), newMock.getRefs());
		assertNotSame(mock.getRefs()[0], newMock.getRefs()[0]);
		assertNotSame(mock.getRefs()[1], newMock.getRefs()[1]);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void clone_basicObjectCollection() throws Exception {
		BasicCollectionMock mock = new BasicCollectionMock();
		mock.setCollection(newCollection(ArrayList.class, "arraylist1", "arraylist2"));
		mock.setSet(newCollection(HashSet.class, 1, 1));
		mock.setHashSet(newCollection(HashSet.class, 1L, 1L));
		mock.setSortedSet(newCollection(TreeSet.class, new Date(), new Date(1000000L)));
		mock.setLinkedHashSet(newCollection(LinkedHashSet.class, new URL("http://www.baidu.com"), new URL("http://www.sina.com")));
		mock.setTreeSet(newCollection(TreeSet.class, new BigDecimal("100"), new BigDecimal("200")));
		mock.setList(newCollection(ArrayList.class, true, false));
		mock.setArrayList(newCollection(ArrayList.class, new BigInteger("2323"), new BigInteger("123123")));
		mock.setLinkedList(newCollection(LinkedList.class, Double.valueOf("123.233"), Double.valueOf("34434.2323322")));
		BasicCollectionMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
		assertNotSame(mock.getCollection(), newMock.getCollection());
		assertNotSame(mock.getSet(), newMock.getSet());
		assertNotSame(mock.getHashSet(), newMock.getHashSet());
		assertNotSame(mock.getLinkedHashSet(), newMock.getLinkedHashSet());
		assertNotSame(mock.getSortedSet(), newMock.getSortedSet());
		assertNotSame(mock.getTreeSet(), newMock.getTreeSet());
		assertNotSame(mock.getList(), newMock.getList());
		assertNotSame(mock.getArrayList(), newMock.getArrayList());
		assertNotSame(mock.getLinkedList(), newMock.getLinkedList());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void clone_referenceCollection() {
		Reference1Mock ref1 = new Reference1Mock();
		ref1.setId("ref1");
		Reference1Mock ref2 = new Reference1Mock();
		ref2.setId("ref2");
		ReferenceCollectionMock mock = new ReferenceCollectionMock();
		mock.setRefs(newCollection(ArrayList.class, ref1, ref2));
		ReferenceCollectionMock newMock = mapper.clone(mock);
		assertReflectionEquals(mock, newMock);
		assertNotSame(mock.getRefs(), newMock.getRefs());
		assertNotSame(mock.getRefs().get(0), newMock.getRefs().get(0));
		assertNotSame(mock.getRefs().get(1), newMock.getRefs().get(1));
	}

	@Test
	@Ignore
	public void clone_hashmap() {
		
	}
	
	@Test
	public void clone_hasGetterNoSetter() {
		HasGetterNoSetterMock mock = new HasGetterNoSetterMock();
		mock.name = "has getter no setter";
		HasGetterNoSetterMock newMock = mapper.clone(mock);
		assertNull(newMock.getName());
	}
	
	@Test
	public void clone_hasSetterNoGetter() {
		HasSetterNoGetterMock mock = new HasSetterNoGetterMock();
		mock.name = "has setter no getter";
		HasSetterNoGetterMock newMock = mapper.clone(mock);
		assertNull(newMock.name);
	}
	
	/**
	 * 校验广度优先,原因如下.
	 * <p>
	 * <strong>深度优先</strong>路径大概为：mock.a - mock.b - mock.b.a - mock.b.b - <em>mock.b.ref</em> - mock.b.z - <strike>mock.ref</strike> - mock.z
	 * 由于其中mock.b.ref == mock.ref，所以第二次mock.ref不会被执行！因此必须使用广度优先以支持用户的路径输入
	 * </p>
	 * <p>
	 * <strong>广度优先</strong>路径大概为：mock.a - mock.b - <em>mock.ref</em> - mock.z - mock.b.a - mock.b.b - <strike>mock.b.ref</strike> - mock.b.z
	 * 由于mock.ref == mock.b.ref，因此第二次mock.b.ref不会被执行
	 * </p>
	 * <p>
	 * <strong>校验准则</strong>：mock.a - mock.b - mock.z - mock.b.a
	 * </p>
	 * @see BreadthFirstMock
	 * @see BreadthFirstRefMock
	 */
	@Test
	public void clone_breadthFirst() {
		BreadthFirstMock mock = new BreadthFirstMock();
		mock.a = "Before";
		mock.z = "After";
		
		BreadthFirstRefMock relatedMock = new BreadthFirstRefMock();
		relatedMock.a = "related mock";
		mock.b = relatedMock;
		
		BreadthFirstMock newMock = mapper.clone(mock);
		assertEquals(mock.getA(), newMock.getA());
		assertNotSame(mock.getB(), newMock.getB());
		assertEquals(mock.getB().getA(), newMock.getB().getA());
	}

	@Test
	public void clone_callback_excludePathCallback_smoke() {
		PathMock mock = new PathMock();
		mock.setA("a");
		mock.setB("b");
		
		PathRefMock ref = new PathRefMock();
		ref.setC(100L);
		ref.setD(299);
		mock.setRef(ref);
		
		PathRefMock parent = new PathRefMock();
		parent.setC(1000L);
		parent.setD(2990);
		ref.setParent(parent);
		
		PathMock newMock = mapper.clone(mock, new ExcludePathCallback("a", "ref.c", "ref.parent.d"));
		assertNull(newMock.getA());
		assertNull(newMock.getRef().getC());
		assertNull(newMock.getRef().getParent().getD());
		assertEquals(newMock.getB(), mock.getB());
		assertEquals(newMock.getRef().getD(), mock.getRef().getD());
		assertEquals(newMock.getRef().getParent().getC(), mock.getRef().getParent().getC());
	}
	
	@Test
	public void clone_callback_excludePathCallback_cycleReference() {
		String sameRef = "same ref";
		
		PathMock mock = new PathMock();
		mock.setSameRef(sameRef);
		
		PathRefMock ref = new PathRefMock();
		ref.setSameRef(sameRef);
		mock.setRef(ref);
		
		PathMock newMock = mapper.clone(mock, new ExcludePathCallback("sameRef"));
		assertNotNull(newMock);
		assertNull(newMock.getSameRef());
		assertEquals(sameRef, newMock.getRef().getSameRef());
	}

	@Test
	public void clone_callback_includePathCallback_smoke() {
		// 需要更多的测试，例如：a.ref就会失败
		// hibernate各种情况有待测试
		PathMock mock = new PathMock();
		mock.setA("a");
		mock.setB("b");
		
		PathRefMock ref = new PathRefMock();
		ref.setC(100L);
		ref.setD(299);
		mock.setRef(ref);
		
		PathRefMock parent = new PathRefMock();
		parent.setC(1000L);
		parent.setD(2990);
		ref.setParent(parent);
		
		PathMock newMock = mapper.clone(mock, new IncludePathCallback("a", "ref.c", "ref.parent.d"));
		assertNull(newMock.getB());
		assertNull(newMock.getRef().getD());
		assertNull(newMock.getRef().getParent().getC());
		assertEquals(newMock.getA(), mock.getA());
		assertEquals(newMock.getRef().getC(), mock.getRef().getC());
		assertEquals(newMock.getRef().getParent().getD(), mock.getRef().getParent().getD());
	}

	@Test
	@Ignore
	public void clone_feature_mapNull() {
		
	}

	
	public static class PrimitiveWrapperMock {
		private String s;
		private Integer i;
		private Long l;
		private Double d;
		private Float f;
		private Character c;
		private Boolean b;
		private Byte by;
		public String getS() {
			return s;
		}
		public void setS(String s) {
			this.s = s;
		}
		public Integer getI() {
			return i;
		}
		public void setI(Integer i) {
			this.i = i;
		}
		public Long getL() {
			return l;
		}
		public void setL(Long l) {
			this.l = l;
		}
		public Double getD() {
			return d;
		}
		public void setD(Double d) {
			this.d = d;
		}
		public Float getF() {
			return f;
		}
		public void setF(Float f) {
			this.f = f;
		}
		public Character getC() {
			return c;
		}
		public void setC(Character c) {
			this.c = c;
		}
		public Boolean getB() {
			return b;
		}
		public void setB(Boolean b) {
			this.b = b;
		}
		public Byte getBy() {
			return by;
		}
		public void setBy(Byte by) {
			this.by = by;
		}
	}
	
	public static class PrimitiveMock {
		private int i;
		private long l;
		private double d;
		private float f;
		private char c;
		private boolean b;
		private byte by;
		public int getI() {
			return i;
		}
		public void setI(int i) {
			this.i = i;
		}
		public long getL() {
			return l;
		}
		public void setL(long l) {
			this.l = l;
		}
		public double getD() {
			return d;
		}
		public void setD(double d) {
			this.d = d;
		}
		public float getF() {
			return f;
		}
		public void setF(float f) {
			this.f = f;
		}
		public char getC() {
			return c;
		}
		public void setC(char c) {
			this.c = c;
		}
		public boolean isB() {
			return b;
		}
		public void setB(boolean b) {
			this.b = b;
		}
		public byte getBy() {
			return by;
		}
		public void setBy(byte by) {
			this.by = by;
		}
	}
	
	public static class PrimitiveArrayMock {
		private int[] i;
		private long[] l;
		private double[] d;
		private float[] f;
		private char[] c;
		private boolean[] b;
		private byte[] by;
		public int[] getI() {
			return i;
		}
		public void setI(int[] i) {
			this.i = i;
		}
		public long[] getL() {
			return l;
		}
		public void setL(long[] l) {
			this.l = l;
		}
		public double[] getD() {
			return d;
		}
		public void setD(double[] d) {
			this.d = d;
		}
		public float[] getF() {
			return f;
		}
		public void setF(float[] f) {
			this.f = f;
		}
		public char[] getC() {
			return c;
		}
		public void setC(char[] c) {
			this.c = c;
		}
		public boolean[] getB() {
			return b;
		}
		public void setB(boolean[] b) {
			this.b = b;
		}
		public byte[] getBy() {
			return by;
		}
		public void setBy(byte[] by) {
			this.by = by;
		}
	}
	
	public static class PrimitiveWrapperArrayMock {
		private Integer[] i;
		private Long[] l;
		private Double[] d;
		private Float[] f;
		private Character[] c;
		private Boolean[] b;
		private Byte[] by;
		public Integer[] getI() {
			return i;
		}
		public void setI(Integer[] i) {
			this.i = i;
		}
		public Long[] getL() {
			return l;
		}
		public void setL(Long[] l) {
			this.l = l;
		}
		public Double[] getD() {
			return d;
		}
		public void setD(Double[] d) {
			this.d = d;
		}
		public Float[] getF() {
			return f;
		}
		public void setF(Float[] f) {
			this.f = f;
		}
		public Character[] getC() {
			return c;
		}
		public void setC(Character[] c) {
			this.c = c;
		}
		public Boolean[] getB() {
			return b;
		}
		public void setB(Boolean[] b) {
			this.b = b;
		}
		public Byte[] getBy() {
			return by;
		}
		public void setBy(Byte[] by) {
			this.by = by;
		}
	}
	
	public static class BasicPropertyMock {
		private Date date;
		private BigDecimal bigDecimal;
		private BigInteger bigInteger;
		private Class<?> clazz;
		private File file;
		private URL url;
		private java.sql.Date sqlDate;
		private java.sql.Time sqlTime;
		private java.sql.Timestamp sqlTimestamp;
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public BigDecimal getBigDecimal() {
			return bigDecimal;
		}
		public void setBigDecimal(BigDecimal bigDecimal) {
			this.bigDecimal = bigDecimal;
		}
		public BigInteger getBigInteger() {
			return bigInteger;
		}
		public void setBigInteger(BigInteger bigInteger) {
			this.bigInteger = bigInteger;
		}
		public Class<?> getClazz() {
			return clazz;
		}
		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}
		public File getFile() {
			return file;
		}
		public void setFile(File file) {
			this.file = file;
		}
		public URL getUrl() {
			return url;
		}
		public void setUrl(URL url) {
			this.url = url;
		}
		public java.sql.Date getSqlDate() {
			return sqlDate;
		}
		public void setSqlDate(java.sql.Date sqlDate) {
			this.sqlDate = sqlDate;
		}
		public java.sql.Time getSqlTime() {
			return sqlTime;
		}
		public void setSqlTime(java.sql.Time sqlTime) {
			this.sqlTime = sqlTime;
		}
		public java.sql.Timestamp getSqlTimestamp() {
			return sqlTimestamp;
		}
		public void setSqlTimestamp(java.sql.Timestamp sqlTimestamp) {
			this.sqlTimestamp = sqlTimestamp;
		}
	}

	public static class BasicArrayPropertyMock {
		private Date[] date;
		private BigDecimal[] bigDecimal;
		private BigInteger[] bigInteger;
		private Class<?>[] clazz;
		private File[] file;
		private URL[] url;
		private java.sql.Date[] sqlDate;
		private java.sql.Time[] sqlTime;
		private java.sql.Timestamp[] sqlTimestamp;
		public Date[] getDate() {
			return date;
		}
		public void setDate(Date[] date) {
			this.date = date;
		}
		public BigDecimal[] getBigDecimal() {
			return bigDecimal;
		}
		public void setBigDecimal(BigDecimal[] bigDecimal) {
			this.bigDecimal = bigDecimal;
		}
		public BigInteger[] getBigInteger() {
			return bigInteger;
		}
		public void setBigInteger(BigInteger[] bigInteger) {
			this.bigInteger = bigInteger;
		}
		public Class<?>[] getClazz() {
			return clazz;
		}
		public void setClazz(Class<?>[] clazz) {
			this.clazz = clazz;
		}
		public File[] getFile() {
			return file;
		}
		public void setFile(File[] file) {
			this.file = file;
		}
		public URL[] getUrl() {
			return url;
		}
		public void setUrl(URL[] url) {
			this.url = url;
		}
		public java.sql.Date[] getSqlDate() {
			return sqlDate;
		}
		public void setSqlDate(java.sql.Date[] sqlDate) {
			this.sqlDate = sqlDate;
		}
		public java.sql.Time[] getSqlTime() {
			return sqlTime;
		}
		public void setSqlTime(java.sql.Time[] sqlTime) {
			this.sqlTime = sqlTime;
		}
		public java.sql.Timestamp[] getSqlTimestamp() {
			return sqlTimestamp;
		}
		public void setSqlTimestamp(java.sql.Timestamp[] sqlTimestamp) {
			this.sqlTimestamp = sqlTimestamp;
		}
	}

	public static class EnumPropertyMock {
		private EnumType type;
		public EnumType getType() {
			return type;
		}
		public void setType(EnumType type) {
			this.type = type;
		}
		public static enum EnumType {
			A, B, C
		}
	}
	
	public static class Reference1Mock {
		private String id;
		private Reference2Mock r2;
		private Reference1Mock r1;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Reference2Mock getR2() {
			return r2;
		}
		public void setR2(Reference2Mock r2) {
			this.r2 = r2;
		}
		public Reference1Mock getR1() {
			return r1;
		}
		public void setR1(Reference1Mock r1) {
			this.r1 = r1;
		}
	}

	public static class Reference2Mock {
		private String id;
		private Reference1Mock r1;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Reference1Mock getR1() {
			return r1;
		}
		public void setR1(Reference1Mock r1) {
			this.r1 = r1;
		}
	}

	public static class ReferenceArrayMock {
		private String id;
		private Reference1Mock[] refs;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Reference1Mock[] getRefs() {
			return refs;
		}
		public void setRefs(Reference1Mock[] refs) {
			this.refs = refs;
		}
	}

	public static class BasicCollectionMock {
		private Collection<?> collection;
		private Set<?> set;
		private HashSet<?> hashSet;
		private LinkedHashSet<?> linkedHashSet;
		private SortedSet<?> sortedSet;
		private TreeSet<?> treeSet;
		private List<?> list;
		private ArrayList<?> arrayList;
		private LinkedList<?> linkedList;
		public Collection<?> getCollection() {
			return collection;
		}
		public void setCollection(Collection<?> collection) {
			this.collection = collection;
		}
		public Set<?> getSet() {
			return set;
		}
		public void setSet(Set<?> set) {
			this.set = set;
		}
		public HashSet<?> getHashSet() {
			return hashSet;
		}
		public void setHashSet(HashSet<?> hashSet) {
			this.hashSet = hashSet;
		}
		public LinkedHashSet<?> getLinkedHashSet() {
			return linkedHashSet;
		}
		public void setLinkedHashSet(LinkedHashSet<?> linkedHashSet) {
			this.linkedHashSet = linkedHashSet;
		}
		public SortedSet<?> getSortedSet() {
			return sortedSet;
		}
		public void setSortedSet(SortedSet<?> sortedSet) {
			this.sortedSet = sortedSet;
		}
		public TreeSet<?> getTreeSet() {
			return treeSet;
		}
		public void setTreeSet(TreeSet<?> treeSet) {
			this.treeSet = treeSet;
		}
		public List<?> getList() {
			return list;
		}
		public void setList(List<?> list) {
			this.list = list;
		}
		public ArrayList<?> getArrayList() {
			return arrayList;
		}
		public void setArrayList(ArrayList<?> arrayList) {
			this.arrayList = arrayList;
		}
		public LinkedList<?> getLinkedList() {
			return linkedList;
		}
		public void setLinkedList(LinkedList<?> linkedList) {
			this.linkedList = linkedList;
		}
	}
	
	public static class ReferenceCollectionMock {
		private List<Reference1Mock> refs;
		public List<Reference1Mock> getRefs() {
			return refs;
		}
		public void setRefs(List<Reference1Mock> refs) {
			this.refs = refs;
		}
	}
	
	public static class BreadthFirstMock {
		static int processedProperties = 0;
		private String a;
		private BreadthFirstRefMock b;
		private String z;
		public String getA() {
			return a;
		}
		public void setA(String a) {
			processedProperties++;
			this.a = a;
		}
		public BreadthFirstRefMock getB() {
			return b;
		}
		public void setB(BreadthFirstRefMock b) {
			processedProperties++;
			this.b = b;
		}
		public String getZ() {
			return z;
		}
		public void setZ(String z) {
			processedProperties++;
			this.z = z;
		}
	}
	
	public static class BreadthFirstRefMock {
		private String a;
		public String getA() {
			return a;
		}
		public void setA(String a) {
			// 检测BreadthFirstMock中已经处理过了所有的方法
			int nonStaticProperties = ReflectionUtils.getPropertyDescriptors(BreadthFirstMock.class).length - 1;
			if (BreadthFirstMock.processedProperties < nonStaticProperties) {
				throw new AssertionError("Not breadth-first. It's depth-first!");
			}
			this.a = a;
		}
	}

	public static class HasGetterNoSetterMock {
		private String name;
		public String getName() {
			return name;
		}
	}
	
	public static class HasSetterNoGetterMock {
		private String name;
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class PathMock {
		private String a;
		private String b;
		private PathRefMock ref;
		private Object sameRef;
		public String getA() {
			return a;
		}
		public void setA(String a) {
			this.a = a;
		}
		public String getB() {
			return b;
		}
		public void setB(String b) {
			this.b = b;
		}
		public PathRefMock getRef() {
			return ref;
		}
		public void setRef(PathRefMock ref) {
			this.ref = ref;
		}
		public Object getSameRef() {
			return sameRef;
		}
		public void setSameRef(Object sameRef) {
			this.sameRef = sameRef;
		}
	}
	
	public static class PathRefMock {
		private Long c;
		private Integer d;
		private PathRefMock parent;
		private Object sameRef;
		public Long getC() {
			return c;
		}
		public void setC(Long c) {
			this.c = c;
		}
		public Integer getD() {
			return d;
		}
		public void setD(Integer d) {
			this.d = d;
		}
		public PathRefMock getParent() {
			return parent;
		}
		public void setParent(PathRefMock parent) {
			this.parent = parent;
		}
		public Object getSameRef() {
			return sameRef;
		}
		public void setSameRef(Object sameRef) {
			this.sameRef = sameRef;
		}
		
	}
	
	public static class Mock {
		private int pPrimativeInt;
		private Integer pInteger;
		private String pString;
		private long pPrimativeLong;
		private Long pLong;
		private short pPrimativeShort;
		private Short pShort;
		private boolean pPrimativeBoolean;
		private Boolean pBoolean;
		private Date pDate;
		private MockType type;
		
		// Object
		private Mock parent;
		
		// Iterable
		private String[] pStringArray;
		private int[] pPrimativeIntegerArray;
		private List<String> pStringList;
		private Set<String> pStringSet;
		private SortedSet<String> pStringSortedSet;
		private HashSet<String> pStringHashSet;
		
		public int getPPrimativeInt() {
			return pPrimativeInt;
		}
		public void setPPrimativeInt(int primativeInt) {
			pPrimativeInt = primativeInt;
		}
		public Integer getPInteger() {
			return pInteger;
		}
		public void setPInteger(Integer integer) {
			pInteger = integer;
		}
		public String getPString() {
			return pString;
		}
		public void setPString(String string) {
			pString = string;
		}
		public long getPPrimativeLong() {
			return pPrimativeLong;
		}
		public void setPPrimativeLong(long primativeLong) {
			pPrimativeLong = primativeLong;
		}
		public Long getPLong() {
			return pLong;
		}
		public void setPLong(Long long1) {
			pLong = long1;
		}
		public short getPPrimativeShort() {
			return pPrimativeShort;
		}
		public void setPPrimativeShort(short primativeShort) {
			pPrimativeShort = primativeShort;
		}
		public Short getPShort() {
			return pShort;
		}
		public void setPShort(Short short1) {
			pShort = short1;
		}
		public boolean isPPrimativeBoolean() {
			return pPrimativeBoolean;
		}
		public void setPPrimativeBoolean(boolean primativeBoolean) {
			pPrimativeBoolean = primativeBoolean;
		}
		public Boolean getPBoolean() {
			return pBoolean;
		}
		public void setPBoolean(Boolean boolean1) {
			pBoolean = boolean1;
		}
		public Date getPDate() {
			return pDate;
		}
		public void setPDate(Date date) {
			pDate = date;
		}
		public MockType getType() {
			return type;
		}
		public void setType(MockType type) {
			this.type = type;
		}
		public Mock getParent() {
			return parent;
		}
		public void setParent(Mock parent) {
			this.parent = parent;
		}
		public String[] getPStringArray() {
			return pStringArray;
		}
		public void setPStringArray(String[] stringArray) {
			pStringArray = stringArray;
		}
		public int[] getPPrimativeIntegerArray() {
			return pPrimativeIntegerArray;
		}
		public void setPPrimativeIntegerArray(int[] primativeIntegerArray) {
			pPrimativeIntegerArray = primativeIntegerArray;
		}
		public List<String> getPStringList() {
			return pStringList;
		}
		public void setPStringList(List<String> stringList) {
			pStringList = stringList;
		}
		public Set<String> getPStringSet() {
			return pStringSet;
		}
		public void setPStringSet(Set<String> stringSet) {
			pStringSet = stringSet;
		}
		public SortedSet<String> getPStringSortedSet() {
			return pStringSortedSet;
		}
		public void setPStringSortedSet(SortedSet<String> stringSortedSet) {
			pStringSortedSet = stringSortedSet;
		}
		public HashSet<String> getPStringHashSet() {
			return pStringHashSet;
		}
		public void setPStringHashSet(HashSet<String> stringHashSet) {
			pStringHashSet = stringHashSet;
		}
	}
	
	private static enum MockType {
		A, B
	}
	
}
