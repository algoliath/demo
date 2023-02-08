package com.example.demo.domain.column.form;

import com.example.demo.domain.column.Column;
import com.example.demo.domain.columnTable.SpreadSheetTable;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class ColumnUpdateForms {

    private List<ColumnUpdateForm> columnUpdateFormList = new ArrayList<>();

    public ColumnUpdateForms(){
    }

    public ColumnUpdateForms(List<Column> columns) {
        for(Column column: columns){
            columnUpdateFormList.add(new ColumnUpdateForm(column));
        }
    }

    public boolean contains(String name) {
        return columnUpdateFormList.stream()
                .filter(columnUpdateForm -> columnUpdateForm.getName().equals(name))
                .findAny().isPresent();
    }

    public Optional<ColumnUpdateForm> getColumnUpdateForm(String name) {
        return columnUpdateFormList.stream()
                .filter(columnUpdateForm -> columnUpdateForm.getName().equals(name))
                .findAny();
    }

    public void addForm(ColumnUpdateForm columnUpdateForm) {
        columnUpdateFormList.add(columnUpdateForm);
    }

    public void deleteForm(ColumnUpdateForm columnUpdateForm) {
        columnUpdateFormList = columnUpdateFormList.stream().filter(columnUpdateForm1 -> columnUpdateForm1.equals(columnUpdateForm)).collect(Collectors.toList());
    }

//    public void setCellRange(SpreadSheetTable spreadSheetTable){
//        columnUpdateFormList.stream().iterator().forEachRemaining(columnUpdateForm -> columnUpdateForm.setRange(spreadSheetTable.getColumnRange(columnUpdateForm.getName())));
//    }


    public boolean hasDuplicateColumns() {
        Set<String> set = new HashSet<>();
        for(ColumnUpdateForm columnUpdateForm: columnUpdateFormList){
            if(!set.add(columnUpdateForm.getName())){
                return false;
            }
        }
        return true;
    }

    public Set<String> getDuplicateColumns(){
        Set<String> set = new HashSet<>();
        Set<String> duplicateSet = new HashSet<>();
        for(ColumnUpdateForm columnUpdateForm: columnUpdateFormList){
            String columnName = columnUpdateForm.getName();
            if(!set.add(columnName)){
                duplicateSet.add(columnName);
            }
        }
        return duplicateSet;
    }

}
