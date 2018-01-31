package fmssapi.util;


import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-28 下午2:23
 */
public class ExcelUtil {

    /**
     * 该方法是一个标准的表格输出,包括表头和表体,如果不符合该格式的需要特别定制
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格属性列名数组
     * @param dataList
     *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     */
    public static void exportExcel(String title, String[] headers,
                                   List<List<String>> dataList, OutputStream out) {
        // 声明一个工作薄
        XSSFWorkbook workBook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workBook.createSheet();
        workBook.setSheetName(0,title);
        // 创建表格标题行 第一行
        XSSFRow titleRow = sheet.createRow(0);
        for(int i=0;i<headers.length;i++){
            titleRow.createCell(i).setCellValue(headers[i]);
        }
        //插入需导出的数据
        for(int i=0;i<dataList.size();i++){
            XSSFRow row = sheet.createRow(i+1);
            for(int j=0;j<dataList.get(i).size();j++){
                row.createCell(j).setCellValue(dataList.get(i).get(j));
            }
        }
        try {
            workBook.write(out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                workBook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
