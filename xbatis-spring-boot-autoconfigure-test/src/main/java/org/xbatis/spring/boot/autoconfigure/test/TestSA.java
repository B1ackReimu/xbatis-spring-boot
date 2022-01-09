package org.xbatis.spring.boot.autoconfigure.test;

import org.xbatis.spring.boot.autoconfigure.ShardAlgorithm;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSA implements ShardAlgorithm {
    @Override
    public String shardSuffix(Map<String,Object> args) {
        return "g1";
    }

    public static void main(String[] args) {
        String regex = "(\\s+(from|join)\\s+)([\\w,]+)";
        String sql = "select * from blog,test,kkk where join abc";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);
        String regex2 = ",*[\\w]+,*";
        Pattern pattern1 = Pattern.compile(regex2);
        while (matcher.find()){
            String group1 = matcher.group(3);
            //System.out.println(group1);
            Matcher matcher1 = pattern1.matcher(group1);
            while (matcher1.find()){
                String group = matcher1.group();
                System.out.println(group);
            }
        }
    }
}
