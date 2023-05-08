package com.example.demo.domain.data.vo.template.query;
import com.example.demo.domain.sql.model.SQLBlockType;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class QueryBlockForm {

    private String templateName;
    private SQLBlockType sqlBlockType;

    private Integer sqlBlockOrder;
    private Integer sqlDataOrder;
    private Integer queryOrder;

    private List<String> columns = new ArrayList<>();
    private List<String> operators = new ArrayList<>();
    private List<String> operands = new ArrayList<>();
    private List<Integer> indices = new ArrayList();

}
