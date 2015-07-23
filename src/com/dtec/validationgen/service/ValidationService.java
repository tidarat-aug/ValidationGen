/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtec.validationgen.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Row;
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
    private String keyParameter = "";
    private String keyFields = "";
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
            addBeforeBegin();
            addProcedureFunction();
            addInBeginAndBottom();
            keyFields="";
            keyParameter="";
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
                String keyNow = splitKey[0].trim();
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
            keyFields += "v_" + logkeys[0] + " := v_cs1_rec." + logkeys[0] + ";\n";
            keyParameter += "v_" + logkeys[0] + ",";
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

    public String addContent() {
        try {
            XSSFWorkbook book = ioService.readExcel(templatePath);
            Map<String, String> mapFunction = new HashMap<String, String>();

            Sheet bookSheet = book.getSheetAt(0);
            mapFunction = ioService.readFunction("mapping.korn");
            Iterator<Row> rowIterator = bookSheet.rowIterator();
            String fieldName = "", validateString = "", resultCommand = "", comment = "", callfunction = "", keyFunction = "";
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getCell(0).getStringCellValue().trim() == null || row.getCell(0).getStringCellValue().trim() == "") {
                    break;
                }
                callfunction = "PROCEDURE P_validate";
                fieldName = row.getCell(1).getStringCellValue().trim();
                validateString = row.getCell(10).getStringCellValue();
                String[] validateStrings = validateString.split("\\n");
                List<String> params = new ArrayList<>();
                for (String keyValidation : validateStrings) {
                    keyFunction = getKeyMap(mapFunction, keyValidation);
                    callfunction += "_" + keyFunction;
                    comment += "--" + keyValidation + "\n";
                    if (keyFunction.equals("expectvalue")) {
                        params.add(keyValidation.substring(keyValidation.indexOf("'") + 1, keyValidation.lastIndexOf("'")));
                    } else if (keyFunction.equals("expectvalues")) {
                        params.add(keyValidation.substring(keyValidation.indexOf("'") + 1, keyValidation.indexOf("'", keyValidation.indexOf("'") + 1)));
                        params.add(keyValidation.substring(keyValidation.indexOf("'", keyValidation.indexOf("'", keyValidation.indexOf("'") + 1) + 1) + 1, keyValidation.lastIndexOf("'")));
                    } else if (keyFunction.equals("intemplate")) {
                        params.add(keyValidation.substring(keyValidation.indexOf("'") + 1, keyValidation.lastIndexOf("'")));
                    } else if (keyFunction.equals("length")) {
                        params.add(keyValidation.substring(keyValidation.indexOf("'") + 1, keyValidation.lastIndexOf("'")));
                    }
                }
                callfunction += "(v_cs1_rec." + fieldName + ",'" + fieldName + "'";
                if(params.size()<=0){
                    callfunction+=");";
                }else if(params.size()==1){
                    callfunction+=",'"+params.get(0)+"');";
                }else if(params.size()==2){
                    callfunction+=",'"+params.get(0)+"'"+",'"+params.get(1)+"');";
                }
                    
                resultCommand += "--" + fieldName + "\n" + comment + "BEGIN\n" + callfunction + "\nEND;\n\n";
                comment = "";
            }

            return resultCommand + "\n\n";
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Error Gen Content",
                    "Can't update Content",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, null, ex);
            return "{content}";
        }

    }

    public String getKeyMap(Map<String, String> mapFunction, String word) {
        if(word.contains("null")){
            return mapFunction.get("null");
        }else if(word.contains("'CCYYMMDD'")){
            return mapFunction.get("'CCYYMMDD'");
        }else if(word.contains("'CCYY'")){
            return mapFunction.get("'CCYY'");
        }else if(word.contains("'CCMMDD'")){
            return mapFunction.get("'CCMMDD'");
        }else if(word.contains("'HHMMSS'")){
            return mapFunction.get("'HHMMSS'");
        }else if(word.contains("'MM'")){
            return mapFunction.get("'MM'");
        }else if(word.contains("or")){
            return mapFunction.get("or");
        }else if(word.contains("in")){
            return mapFunction.get("in");
        }else if(word.contains("digits")){
            return mapFunction.get("digits");
        }else if(word.contains("value must be")){
            return mapFunction.get("value must be");
        }
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
