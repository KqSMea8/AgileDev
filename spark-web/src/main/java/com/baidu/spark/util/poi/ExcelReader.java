package com.baidu.spark.util.poi;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.util.Assert;
import ch.qos.logback.classic.Logger;

/**
 * 读取Excel文件的数据.目前只支持单个sheet.
 * 目前只支持单个sheet的Excel文件读取
 * @author 阿蹲
 */
public class ExcelReader {
	
    private HSSFSheet sheet;		//TODO 同步问题
    
    /**
     * 使用完整的文件名生成一个ExcelReader
     * @param file 文件路径及文件名
     * @throws IOException 打开关闭文件,可能抛出读取文件的异常
     */
    public ExcelReader(String file) throws IOException {
    	FileInputStream is = null;
    	try{
    		is = new FileInputStream(file);
        	HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(is));
        	if (null == wb){
            	throw new IOException("read file error: the reading result is null");
            }
            this.sheet = wb.getSheetAt(0);
            if (null == this.sheet){
            	throw new IOException("read file error: the workbook does not contains any sheet");
            }
    	}finally{
    		is.close();
    	}
    }
    
    /**
     * 获取sheet中某行的元素个数
     * @param rowNumber 需要读取的行数
     * @return 所取行中,具有数据的单元格个数
     */
    public int getRowLength(int rowNumber){
    	Assert.notNull(sheet);
    	
    	try{
    		return sheet.getRow(rowNumber).getLastCellNum()+1;
    	}catch( RuntimeException e ){
    		return 0;
    	}
    }
    
    /**
     * 获取sheet中数据行的行数
     * @return sheet中的总行数
     */
    public int getRowCount(){
    	Assert.notNull(sheet);
    	
    	try{
    		return sheet.getLastRowNum()+1;
    	}catch( RuntimeException e ){
    		return 0;
    	}
    }
    
    /**
     * 获得sheet中某行的前n个数据
     * @param rowNumber 需要取数据的所在的行数.
     * @param length 所需要取的数据的个数
     * @return 读取到数据结果(每个单元格为一个String)的list.
     */
    public ArrayList<String> getRow(int rowNumber, int length){
    	Assert.notNull(sheet);
    	Assert.isTrue( rowNumber > 0 );
    	
    	//由于poi从0开始索引行号,故需要进行转化一下.
    	int actualRowNumber = rowNumber - 1;
    	
        ArrayList<String> list = new ArrayList<String>();
        for(int x = 0; x<length; x++) {
            list.add(getCell(x, actualRowNumber));
        }
        return list;
    }
    
    /**
     * 获得y行x列元素
     * 行、列索引号是从1开始计算
     * @param x 列序号
     * @param y 行序号
     * @return 单元格内容.如果异常或者空单元格,则返回空字符串.不会返回null.
     */
    public String getCell(int x, int y) {
    	Assert.notNull(sheet);
    	Assert.isTrue(x > 0);
    	Assert.isTrue(y > 0);
    	
    	//由于poi中实际使用的行/列序列,均为从0开始索引,故需要进行一下转化
    	int actualX = x - 1;
    	int actualY = y - 1;
    	
    	HSSFCell cell;
    	try{
        	cell =sheet.getRow( actualY ).createCell( actualX );
        }catch(Exception e){
        	cell=null;
        }
        return ObjectUtils.toString(cell);
    }
    
    /**
     * 从sheet中获取ExcelData对象
     * @return 已经填充了数据的ExcelData对象
     */
	public ExcelData getExcelData(){
		Assert.notNull(sheet);
		
		//灌入数据
		ExcelData ed = new ExcelData();
		for(int i=0;i<getRowCount();i++){
			ArrayList<String> temp=getRow(i, getRowLength(i));
			for (String cellContent:temp){
				ed.addCellData(cellContent);
			}
			ed.confirmRow();
		}
		
		//对于每一行,将最后一个不为空的单元格之后的内容全部给去掉.
		//对于通用的,这个最好不要.所以先注释掉了
//		for (int i=ed.getSheetData().size()-1;i>=0;i--){
//			boolean empty=true;
//			for (String t:ed.getRowStringData(i))
//				if (t!=null && t.length()>0)
//					empty=false;
//			if (empty==true){
//				ed.getSheetData().remove(i);
//			}else{
//				break;
//			}
//		}
		return ed;
	}
	
	
	private void logException( Exception e ){
		//TODO log this exception
	}
}

