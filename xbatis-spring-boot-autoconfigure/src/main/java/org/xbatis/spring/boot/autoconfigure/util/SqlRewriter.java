package org.xbatis.spring.boot.autoconfigure.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbatis.spring.boot.autoconfigure.DataSourceSelector;
import org.xbatis.spring.boot.autoconfigure.XbatisDataSourceConfig;
import org.xbatis.spring.boot.autoconfigure.XbatisRouter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SqlRewriter {

    private final Pattern PATTERN_FROM;
    private final Pattern PATTERN_TABLE;
    private XbatisRouter xbatisRouter;
    private final ConcurrentHashMap<String, String> classMap = new ConcurrentHashMap<>();

    @Autowired
    public void setXbatisRouter(XbatisRouter xbatisRouter) {
        this.xbatisRouter = xbatisRouter;
    }

    {
        PATTERN_FROM = Pattern.compile("(\\s+(from|join)\\s+)([\\w,]+)");
        PATTERN_TABLE = Pattern.compile(",*\\w+,*");
    }

    public String rewrite(String statementId, String originSql) {
        originSql = originSql.toLowerCase();
        originSql = originSql.replaceAll("\\s+"," ");
        // <from|join<table>>
        ArrayList<ArrayList<String>> tableNames = new ArrayList<>();
        String className = classMap.get(statementId);
        if (Objects.isNull(className)) {
            synchronized (this) {
                if (Objects.isNull(classMap.get(statementId))) {
                    classMap.put(statementId, statementId.substring(0, statementId.lastIndexOf(".")));
                }
            }
        }
        Matcher matcher = PATTERN_FROM.matcher(originSql);
        while (matcher.find()){
            ArrayList<String> tables = new ArrayList<>();
            String fromTable = matcher.group(3).intern();
        }
        XbatisDataSourceConfig.XbatisNamespace xbatisNamespace = xbatisRouter.getXbatisNamespace(className);
        return null;
    }

}
