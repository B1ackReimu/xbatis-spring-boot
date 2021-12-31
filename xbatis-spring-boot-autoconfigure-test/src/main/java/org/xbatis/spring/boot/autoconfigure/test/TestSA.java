package org.xbatis.spring.boot.autoconfigure.test;

import org.xbatis.spring.boot.ShardAlgorithm;

public class TestSA implements ShardAlgorithm {
    @Override
    public String shardSuffix(String... args) {
        return null;
    }
}
