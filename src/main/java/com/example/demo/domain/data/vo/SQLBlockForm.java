package com.example.demo.domain.data.vo;
import com.example.demo.domain.database.model.SQLBlockType;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SQLBlockForm {

    private Integer sqlBlockOrder;
    private Integer sqlDataOrder;

    private SQLBlockType SQLBlockType;

    private String templateName;
    private String operator;
    private String operand;

    private List<String> columns = new ArrayList<>();
    private List<Integer> indices = new ArrayList();

}
