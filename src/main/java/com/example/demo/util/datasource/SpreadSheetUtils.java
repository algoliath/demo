package com.example.demo.util.datasource;

import org.springframework.stereotype.Component;

@Component
public class SpreadSheetUtils {

    public static String getSpreadSheetURL(String fileId){
        return "https://docs.google.com/spreadsheets/d/" + fileId + "/edit#gid=0";
    }

    public static String getEmbeddableSpreadSheetURL(String fileId){
        return "https://docs.google.com/spreadsheets/d/" + fileId + "/edit?usp=sharing&amp;widget=true&amp;headers=false";
    }

}
