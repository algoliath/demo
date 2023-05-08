package com.example.demo.domain.data.vo.template;

import com.example.demo.domain.sql.model.SQLOperator;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false, of = {"name"})
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class QueryColumnUpdateForm {

    private SQLOperator operator;
    private String operand;
    private String name;

    public QueryColumnUpdateForm(String name){
        this.name = name;
    }

}
