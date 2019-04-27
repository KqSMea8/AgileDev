package com.baidu.spark.util.poi;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.util.Assert;

/**
 * 存放Excel数据的bean
 * @author 阿蹲
 */
public class ExcelData {
	
	//颜色,用于背景颜色,字体等
	public static final int COLOR_YELLOW = 13;
	
	//存放Excel数据的List
	private List<List<CellData>> sheetData=new ArrayList<List<CellData>>();
	
	//存放未回车的行.在执行confirmRow方法之前,数据不会进入sheetData,暂时先存放rowData中.
	private List<CellData> rowData=new ArrayList<CellData>();
	
	//合并单元格数据
	private List<int[]> regionArea=new ArrayList<int[]>();

	/**
	 * 根据输入的CellData列表,添加一行单元格数据
	 * @param rowData CellData对象的列表
	 */
	public void addRow(List<CellData> rowData){
		Assert.notNull(rowData);
		
		this.sheetData.add(rowData);
	}
	
	/**
	 * 根据输入的字符串列表,添加一行单元格数据
	 * @param rowCon 字符串列表.每一个字符串为一个单元格的文本内容.
	 */
	public void addRowString(ArrayList<String> rowCon){
		Assert.notNull(rowCon);
		
		ArrayList<CellData> rowTemp=new ArrayList<CellData>();
		for(String cellCon:rowCon)
			rowTemp.add(new CellData(cellCon));
		addRow(rowTemp);
	}
	
	/**
	 * 根据输入的字符串列表,和颜色标识,添加一行单元格数据
	 * @param rowCon rowCon 字符串列表.每一个字符串为一个单元格的文本内容.
	 * @param bgColor 单元格背景颜色颜色标识.
	 */
	public void addRowString(ArrayList<String> rowCon, int bgColor){
		Assert.notNull(rowCon);

		ArrayList<CellData> rowTemp=new ArrayList<CellData>();
		for(String cellCon : rowCon)
			rowTemp.add(new CellData(cellCon, bgColor));
		addRow(rowTemp);
	}
	
	/**
	 * 添加一个单元格.
	 * @param cellData CellData对象
	 */
	public void addCellData(CellData cellData){
		Assert.notNull(cellData);
		rowData.add(cellData);
	}
	
	/**
	 * 添加一个单元格
	 * @param cellCon Object类型,直接toString后,作为内容.
	 */
	public void addCellData(Object cellCon){
		addCellData(new CellData(ObjectUtils.toString(cellCon)));
	}
	
	/**
	 * 添加一个单元格
	 * @param cellCon 文本内容
	 * @param bgColor 单元格背景颜色标识
	 */
	public void addCellData(String cellCon,int bgColor){
		addCellData(new CellData(cellCon,bgColor));
	}
	
	/**
	 * 添加一个单元格
	 * @param cellCon 文本内容
	 * @param bgColor 单元格背景颜色标识
	 * @param remark 单元格的comment
	 */
	public void addCellData(String cellCon,int bgColor,String remark){
		addCellData(new CellData(cellCon,bgColor,remark));
	}
	
	/**
	 * 逐个添加单元格的时候,已经添加完本行的所有单元格，初始化下一行。类似于回车
	 */
	public void confirmRow(){
		sheetData.add(rowData);
		rowData = new ArrayList<CellData>();
	}
	
	/**
	 * 获取所有单元格
	 * @return CellData对象列表的列表
	 */
	public List<List<CellData>> getSheetData() {
		return sheetData;
	}
	
	/**
	 * 获取第y行x列的单元格
	 * @param indexRow 使用的行索引,索引从1开始
	 * @param indexCell  使用的列索引,索引从1开始
	 * @return 返回指定位置单元格的CellData对象.如果此位置的单元格如果不存在,则返回null.
	 */
	public CellData getCellData( int x, int y ) {
		Assert.isTrue( x > 0 );
		Assert.isTrue( y > 0 );
		
		int actualX = x - 1;
		int actualY = y - 1;
		
		List<CellData> currentRowData =  sheetData.get(actualY);
		
		if (null == currentRowData){
			return null;
		}else{
			return currentRowData.get(actualX);
		}
	}
	
	/**
	 *  获取一行单元格的CellData对象列表
	 * @param i 单元格的行号索引.索引从1开始.
	 * @return  单元格CellData对象的列表.如果行不存在,则返回一个空列表.
	 */
	public List<CellData> getRowData(int y) {
		Assert.isTrue( y > 0 );
		
		int actualY = y - 1;

		List<CellData> currentRowData = sheetData.get( actualY );
		if ( null == currentRowData ){
			currentRowData = new ArrayList<CellData>();
		}
		
		return currentRowData;
	}
	
	/**
	 * 获取一行单元格的文本信息
	 * @param i 单元格的行号索引.索引从1开始.
	 * @return 单元格文本信息的列表.如果行不存在,则返回一个空列表.
	 */
	public List<String> getRowStringData(int y){
		Assert.isTrue( y > 0 );
		
		int actualY = y - 1;

		List<String> rowStringData = new ArrayList<String>();
		List<CellData> currentRowData = sheetData.get( actualY );
		if ( null == currentRowData ){
			return rowStringData;
		}
		for ( CellData cellData : currentRowData ) {
			rowStringData.add(cellData.getCellcon());
		}
		return rowStringData;
	}
	
	/**
	 * 添加一个合并单元格信息
	 * @param regionArea 合并单元格信息.长度为4的int数组
	 */
	public void addRegionArea(int[] regionArea) {
		if (isArea(regionArea)) {
			this.regionArea.add(regionArea);
		}
	}
	
	/**
	 * 添加一组合并单元格信息
	 * @param regionAreas 合并单元格信息的数组. 合并单元格信息本身也是一个数组,长度为4的int数组
	 */
	public void addRegionArea( int[][] regionAreas ) {
		Assert.notNull( regionAreas );
		for (int i=0;i<regionAreas.length;i++)
			if (isArea(regionAreas[i])) {
				this.regionArea.add(regionAreas[i]);
			}
	}
	
	/**
	 * 通过4个int(起止位置)来添加一个合并单元格信息
	 * @param startRow 起始行号(包含).索引从1开始
	 * @param startCol 起始列号(包含).索引从1开始
	 * @param endRow 终止行号(包含).索引从1开始
	 * @param endCol 终止列号(包含).索引从1开始
	 */
	public void addRegionArea(int startRow,int startCol,int endRow,int endCol){
		addRegionArea( new int[]{ startRow, startCol, endRow, endCol } );
	}
	
	/**
	 * 获取所有的合并单元格信息
	 * @return 合并单元格信息的列表.每个合并单元格信息是一个长度为4的int数组
	 */
	public List<int[]> getRegionArea() 	{
		return regionArea;
	}
	
	/**
	 * 获取一个合并单元格信息
	 * @param i 合并单元格信息的序号
	 * @return 合并单元格信息.合并单元格信息是一个长度为4的int数组
	 */
	public int[] getRegionArea(int i) 	{
		return regionArea.get(i);
	}
	
	/**
	 * 判断一个数组是否可以作为合并单元格信息.
	 * 要求长度为4，且4个数字均大于零.且第三个数大于第一个数,第四个数大于第二个数.
	 * @param regionArea 待检测的int数组
	 * @return true:满足所有条件 false:某些条件不满足
	 */
	private boolean isArea(int[] regionArea){
		Assert.notNull( regionArea );
		
		if (regionArea.length!=4)
			return false;
		for(int index : regionArea)
			if (index<=0)
				return false;
		if (regionArea[2]<regionArea[0] || regionArea[3]<regionArea[1])
			return false;
		return true;
	}
}