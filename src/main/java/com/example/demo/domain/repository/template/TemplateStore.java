package com.example.demo.domain.repository.template;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.template.Template;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TemplateStore {

    private Long id = 0L;
    Map<Long, Template> map = new ConcurrentHashMap<>();

    public void save(Template template){
        if(template.getId() != null){
            map.replace(template.getId(), template);
        }
        else{
            template.setId(id);
            map.put(id++, template);
        }
    }

    public List<Template> findByMemberId(String memberId){
        return map.values().stream().filter(template -> template.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public Template findByTemplateId(Long templateId) {
        return map.get(templateId);
    }

}
