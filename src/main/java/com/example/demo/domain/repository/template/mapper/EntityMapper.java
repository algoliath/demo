package com.example.demo.domain.repository.template.mapper;

import com.example.demo.domain.data.dto.EntityDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EntityMapper {

    void save(EntityDTO template);

    void delete(@Param("id") Long id);

    void update(EntityDTO template, @Param("id") Long id);

    void create(@Param("query") String query);

    void drop(@Param("query") String query);

    List<EntityDTO> findByMemberId(@Param("memberId") Long memberId);

    List<EntityDTO> findByName(@Param("name") String name);

    Optional<EntityDTO> findById(@Param("id") Long id);

    List<EntityDTO> findAll();

//    @Select("select template.id, template.name, file_name, file_domain, template.member_id from entity" +
//            " join template on entity.id = template.id" +
//            " where name like concat('%', #{name}, '%') and member_id = #{memberId}")
    List<EntityDTO> searchByCond(@Param("name") String name, @Param("memberId") Long memberId);
}
