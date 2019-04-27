package com.baidu.spark.util.poi;

/**
 * 单元格数据及属性的bean
 * @author 阿蹲
 */
public class CellData {
	/**
	 * 单元格文字内容
	 */
	private String cellcon;
	/**
	 * 背景颜色
	 */
	private short bgColor;
	/**
	 * 单元格注释
	 */
	private String remark;
	
	
	public CellData(String cellcon,int bgColor,String remark){
		this.cellcon=cellcon;
		this.bgColor=(short)bgColor;
		this.remark=remark;
	}
	
	public CellData(String cellcon,int bgColor){
		this.cellcon=cellcon;
		this.bgColor=(short)bgColor;
	}
	
	public CellData(String cellcon){
		this.cellcon=cellcon;
		this.bgColor=0;
	}
	
	
	public short getBGcolor() {
		return bgColor;
	}
	public String getCellcon() {
		return cellcon;
	}
	public String getRemark(){
		return remark==null ? "" : remark;
	}
}
