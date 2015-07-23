/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtec.validationgen.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author rikikun
 */
public class IoService {

    public void writeExcel(XSSFWorkbook workBook, String fileName) throws IOException {
        try (FileOutputStream outPut = new FileOutputStream(new File(fileName));) {
            workBook.write(outPut);
        }
    }

    public XSSFWorkbook readExcel(String fileName) throws IOException {
        FileInputStream file = new FileInputStream(new File(fileName));
        return new XSSFWorkbook(file);
    }

    public void writeFile(StringBuilder data, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);) {
            printWriter.write(data.toString());
        } catch (IOException ex) {
            Logger.getLogger(IoService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, String> readFunction(String namePath) throws FileNotFoundException, IOException {
        Map<String, String> map = new HashMap<String, String>();
        BufferedReader input = new BufferedReader(new FileReader(namePath));
        String data = "";
        while ((data = input.readLine()) != null) {
            String[] temp=data.split("#");
            map.put(temp[0].trim(), temp[1].trim());
        }
        return map;
    }

    public String readSql(String sqlName) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(sqlName));
        String sql = "";
        sql += input.readLine();
        return sql;
    }

    public String readFile(String fileName) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        String data = "", temp = "";
        while ((temp = input.readLine()) != null) {
            data += temp;
            data += "\n";
        }
        return data;
    }

    public Boolean checkFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public String getConfigFile(String fileName) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        String sql = "";
        sql += input.readLine();
        return sql;
    }
}
