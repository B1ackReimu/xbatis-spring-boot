package org.xbatis.spring.boot.autoconfigure.test.shardalgorithm;

import org.xbatis.spring.boot.autoconfigure.ShardAlgorithm;

import java.util.Map;

public class GroupSA implements ShardAlgorithm {

    @Override
    public String shardSuffix(Map<String, Object> args) {
        System.out.println("shardSuffix args:" + args);
        return "_0";
    }
}
