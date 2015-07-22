/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtec.validationgen.test;

/**
 *
 * @author rikikun
 */
public class TestService {
    
    public static void main(String...args){
        System.out.println("agbc number(5)".split(" ")[1].toUpperCase().contains("R("));
        System.out.println("number".toUpperCase().contains("NsUMBER") ? "NUMBER":"VARCHAR2");
    }
    
}
