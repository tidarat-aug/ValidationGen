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
    private String keyParameter="";
    private String keyFields="";
    private IoService ioService = new IoService();
    private final Integer FIELD_NUM = 1;
    private final Integer FIELD_DES = 10;
    private StringBuilder dataBuffer = new StringBuilder();
    private JFrame parent;

    public ValidationService(JFrame parent) {
        this.parent = parent;
    }

    public void genarate() {
        try {
            XSSFWorkbook book = ioService.readExcel(templatePath);
            Sheet bookSheet = book.getSheetAt(0);
            Map<String, String> mapFunction = new HashMap<String, String>();
            mapFunction = ioService.readFunction("mapping.korn");
            addBeforeBegin();
            addProcedureFunction();
            addInBeginAndBottom();
            ioService.writeFile(dataBuffer, stagingName + "_result_korn.txt");
        } catch (IOException ex) {
            Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addBeforeBegin() throws IOException {
        String transformKey = "";
        String transformError = "";
        String transformErrorBegin = "";
        if (logField.contains(",")) {
            String[] logkeys = logField.split(",");
            for (String key : logkeys) {
                String[] splitKey = key.split("  ");
                String keyNow=splitKey[0].trim();
                keyParameter += "v_" + keyNow + ",";
                keyFields += "v_" + keyNow + " := v_cs1_rec." + keyNow + ";\n";
                transformKey += "v_" + key + ";\n";
                transformErrorBegin += "v_errdtl_rec." + keyNow + " := " + keyNow + ";\n";
                if (splitKey[1].toUpperCase().contains("NUMBER")) {
                    transformError += keyNow + " IN " + "NUMBER,";
                } else {
                    transformError += keyNow + " IN " + "VARCHAR2,";
                }
            }
        } else {
            String[] logkeys = logField.split("  ");
            keyFields += "v_" + logField + " := v_cs1_rec." + logField + ";\n";
            keyParameter += "v_" + logField + ",";
            transformKey = "v_" + logField + ";\n";
            transformError += logkeys[0] + " IN " + (logkeys[1].toUpperCase().contains("NUMBER") ? "NUMBER" : "VARCHAR2") + ",";
            transformErrorBegin = "v_errdtl_rec." + logkeys[0] + " := " + logkeys[0] + ";\n";
        }
        keyParameter = keyParameter.substring(0, keyParameter.length() - 1);
        dataBuffer.append(ioService.readFile("template.beforeBegin.korn")
                .replaceAll("\\{tableName\\}", stagingName)
                .replaceAll("\\{keyFields\\}", transformKey)
                .replaceAll("\\{keyError\\}", transformError.substring(0, transformError.length() - 1))
                .replaceAll("\\{keyErrorBegin\\}", transformErrorBegin));
    }

    private void addProcedureFunction() throws IOException {
        dataBuffer.append("\n");
        dataBuffer.append(ioService.readFile("template.plFunction.korn")
                .replaceAll("\\{keyParameter\\}", keyParameter)
                .replaceAll("\\{tableName\\}", stagingName));
    }

    public void addInBeginAndBottom() throws IOException {
        //{tableName},{keyFields},{content}
        dataBuffer.append(ioService.readFile("template.InBegin.korn")
                .replaceAll("\\{tableName\\}", stagingName)
                .replaceAll("\\{content\\}", addContent())
                .replaceAll("\\{keyFields\\}", keyFields));
    }
    
    public String addContent(){
        
        return "";
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
