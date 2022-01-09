package org.xbatis.spring.boot.autoconfigure.test.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xbatis.spring.boot.autoconfigure.annotation.Master;
import org.xbatis.spring.boot.autoconfigure.annotation.NameSpace;
import org.xbatis.spring.boot.autoconfigure.test.entity.Blog;

@Mapper
@NameSpace("ns1")
public interface TestMapper {

    @Master
    Blog selectBlog(@Param("id") int id);

}
