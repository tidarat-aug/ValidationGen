/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtec.validationgen.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author rikikun
 */
public class ValidationService {

    private String templatePath;
    private String logField;
    private String stagingName;
    private IoService ioService = new IoService();
    private final Integer FIELD_NUM = 1;
    private final Integer FIELD_DES = 10;
    private StringBuilder dataBuffer = new StringBuilder();
    private JFrame parent;

    public ValidationService(JFrame parent) {
        this.parent=parent;
    }
    
    

    public void genarate() {
        try {
            XSSFWorkbook book = ioService.readExcel(templatePath);
            Sheet bookSheet = book.getSheetAt(0);
            Map<String, String> mapFunction = new HashMap<String, String>();
            mapFunction = ioService.readFunction("mapping.korn");
            updateBeforeBegin();
            updateInfunction();
            ioService.writeFile(dataBuffer, stagingName + "_result.txt");
        } catch (IOException ex) {
            Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String readColumnValidation() {
        return "";
    }

    private void updateBeforeBegin() throws IOException {
        dataBuffer.append(ioService.readFile("template.beforeBegin"));
        String transformKey = "";
        String transformError = "";
        String transformErrorBegin = "";
        if (logField.contains(",")) {
            String[] logkeys = logField.split(",");
            for (String key : logkeys) {
                String[] splitKey = key.split("  ");
                transformKey += "v_" + key + ";\n";
                transformErrorBegin += "v_errdtl_rec." + splitKey[0] + " := " + splitKey[0] + ";\n";
                if (splitKey[1].toUpperCase().contains("NUMBER")) {
                    transformError += splitKey[0] + " IN " + "NUMBER,";
                } else {
                    transformError += splitKey[0] + " IN " + "VARCHAR2,";
                }
            }
        } else {
            String[] logkeys = logField.split("  ");
            transformKey = "v_" + logField + ";\n";
            transformError += logkeys[0] + " IN " + (logkeys[1].toUpperCase().contains("NUMBER") ? "NUMBER" : "VARCHAR2") + ",";
            transformErrorBegin = "v_errdtl_rec." + logkeys[0] + " := " + logkeys[0] + ";\n";
        }

        dataBuffer = new StringBuilder(dataBuffer.toString()
                .replaceAll("\\{tableName\\}", stagingName)
                .replaceAll("\\{keyFields\\}", transformKey)
                .replaceAll("\\{keyError\\}", transformError.substring(0, transformError.length() - 1))
                .replaceAll("\\{keyErrorBegin\\}", transformErrorBegin));
    }

    public void updateInfunction() throws IOException {
        //{tableName},{keyFields},{content}
        StringBuilder inFunctionKeep = new StringBuilder();
        inFunctionKeep.append(ioService.readFile("template.InBegin"));
        String keyFields="";
        
        if (logField.contains(",")) {
            String[] logkeys = logField.split(",");
            for (String key : logkeys) {
                String[] splitKey = key.split("  ");
                keyFields+="v_"+splitKey[0]+" := v_cs1_rec."+splitKey[0]+";\n";
            }
        } else {
            
        }     
        
        inFunctionKeep = new StringBuilder(inFunctionKeep.toString()
                .replaceAll("\\{tableName\\}", stagingName)
                .replaceAll("\\{keyFields\\}", keyFields));

        dataBuffer.append(inFunctionKeep);
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getLogField() {
        return logField;
    }

    public void setLogField(String logField) {
        this.logField = logField;
    }

    public String getStagingName() {
        return stagingName;
    }

    public void setStagingName(String stagingName) {
        this.stagingName = stagingName;
    }

    public IoService getIoService() {
        return ioService;
    }

    public void setIoService(IoService ioService) {
        this.ioService = ioService;
    }

    public StringBuilder getDataBuffer() {
        return dataBuffer;
    }

    public void setDataBuffer(StringBuilder dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public JFrame getParent() {
        return parent;
    }

    public void setParent(JFrame parent) {
        this.parent = parent;
    }

    
}