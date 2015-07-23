/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtec.validationgen.test;

import com.dtec.validationgen.service.IoService;
import java.io.IOException;
import java.util.Arrays;
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

    String abc = "as";

    public static void main(String... args) throws IOException {
        System.out.println("agbc number(5)".split(" ")[1].toUpperCase().contains("R("));
        System.out.println("number".toUpperCase().contains("NsUMBER") ? "NUMBER" : "VARCHAR2");
        TestService f = new TestService();
        f.changeString(f.abc);
        System.out.println(f.abc);

        XSSFWorkbook book = new IoService().readExcel("work around.xlsx");
        Map<String, String> mapFunction = new HashMap<String, String>();
        Sheet bookSheet = book.getSheetAt(0);
        Iterator<Row> rowIterator = bookSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            row.getCell(0).getStringCellValue();
            String fieldName = row.getCell(1).getStringCellValue().trim();
            String validateString = row.getCell(10).getStringCellValue();
            for (String keyValidation : validateString.split("\\n")) {
                System.out.println(keyValidation.trim());
            }
        }
        String keyValidation="'abc' or 'ddd'";
        System.out.println(keyValidation.indexOf("'") + 1 +" "+keyValidation.indexOf("'", keyValidation.indexOf("'")+1));
        String[]param={"","",""};
        param[0] = keyValidation.substring(keyValidation.indexOf("'") + 1, keyValidation.indexOf("'", keyValidation.indexOf("'")+1));
        param[1] = keyValidation.substring(keyValidation.indexOf("'", keyValidation.indexOf("'", keyValidation.indexOf("'")+1)+1) + 1, keyValidation.lastIndexOf("'"));
        
        System.out.println(Arrays.asList(param));
    }

    public void changeString(String abc) {
        abc = "aggg";
    }

}
