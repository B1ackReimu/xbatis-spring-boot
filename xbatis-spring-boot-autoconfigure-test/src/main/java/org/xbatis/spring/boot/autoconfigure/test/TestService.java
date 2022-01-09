package org.xbatis.spring.boot.autoconfigure.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xbatis.spring.boot.autoconfigure.test.entity.Blog;
import org.xbatis.spring.boot.autoconfigure.test.mapper.TestMapper;

import javax.annotation.PostConstruct;

@Service
public class TestService {

    private TestMapper testMapper;

    @Autowired
    public TestService(TestMapper testMapper) {
        this.testMapper = testMapper;
    }

    @PostConstruct
    public void test(){
        Blog blog = testMapper.selectBlog(1);
        System.out.println(blog);
    }
}
