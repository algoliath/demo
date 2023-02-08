package com.example.demo.domain.source.messagesource;

import com.example.demo.util.Source;
import com.example.demo.util.message.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

@Slf4j
public class MessageSourceTest {

    @BeforeEach
    public void init(){

    }

    @Test
    public void errorMessageCode(){
        String code = "칼럼 {0}의 조건 {1}을 설정할 수 없습니다. 스프레드시트의 셀 범위 {2}를 확인해 주세요.";
        Object[] arguments = new Object[]{"column1", "uniqueness", "Sheet1!A0:D5"};
        String message = MessageConverter.convertMessage(code, arguments);
        Assertions.assertThat(message).isEqualTo("칼럼 column1의 조건 uniqueness을 설정할 수 없습니다. 스프레드시트의 셀 범위 Sheet1!A0:D5를 확인해 주세요.");
    }

    @Test
    public void errorMessageList(){
        Source<List<String>> source = new Source<>();
        Optional<List<String>> spreadSheetRange = source.get("spreadSheetRange");
        Assertions.assertThat(spreadSheetRange.isEmpty()).isTrue();
    }


}
