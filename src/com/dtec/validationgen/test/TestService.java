/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtec.validationgen.test;

import com.dtec.validationgen.service.IoService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author rikikun
 */
public class TestService {
    String abc="as";
    
    public static void main(String...args) throws IOException{
        System.out.println("agbc number(5)".split(" ")[1].toUpperCase().contains("R("));
        System.out.println("number".toUpperCase().contains("NsUMBER") ? "NUMBER":"VARCHAR2");
        TestService f=new TestService();
        f.changeString(f.abc);
        System.out.println(f.abc);
        
        XSSFWorkbook book = new IoService().readExcel("work around.xlsx");
            Map<String, String> mapFunction = new HashMap<String, String>();
            Sheet bookSheet = book.getSheetAt(0);
            Iterator<Row> rowIterator = bookSheet.rowIterator();
            while(rowIterator.hasNext()){
                Row row=rowIterator.next();
                System.out.println(row.getCell(0).getStringCellValue());
                if(row.getCell(0).getStringCellValue().trim()==null||row.getCell(0).getStringCellValue().trim()==""){
                    break;
                }
            }
    }
    
    public void changeString(String abc){
        abc="aggg";
    }
    
}
