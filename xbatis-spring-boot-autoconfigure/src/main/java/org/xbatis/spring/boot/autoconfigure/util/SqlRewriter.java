package org.xbatis.spring.boot.autoconfigure.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbatis.spring.boot.autoconfigure.DataSourceSelector;
import org.xbatis.spring.boot.autoconfigure.XbatisDataSourceConfig;
import org.xbatis.spring.boot.autoconfigure.XbatisRouter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRewriter {

    private final Pattern PATTERN_FROM;
    private final XbatisRouter xbatisRouter;
    private final ConcurrentHashMap<String, String> classMap = new ConcurrentHashMap<>();

    public SqlRewriter(XbatisRouter xbatisRouter) {
        this.xbatisRouter = xbatisRouter;
    }

    {
        PATTERN_FROM = Pattern.compile("(\\s+(from|join)\\s+)([\\w,]+)");
    }

    public String rewrite(String statementId, String sql) {
        sql = sql.toLowerCase();
        sql = sql.replaceAll("\\s+", " ");
        String className = classMap.get(statementId);
        if (Objects.isNull(className)) {
            synchronized (this) {
                if (Objects.isNull(classMap.get(statementId))) {
                    className = statementId.substring(0, statementId.lastIndexOf(".")).intern();
                    classMap.put(statementId, className);
                }
            }
        }
        XbatisDataSourceConfig.XbatisNamespace xbatisNamespace = xbatisRouter.getXbatisNamespace(className);
        HashMap<String, char[][]> replaceMode = getReplaceMode(sql);
        Set<String> tableNames = replaceMode.keySet();
        for (String tableName : tableNames) {
            XbatisDataSourceConfig.XbatisNamespace.XbatisTable xbatisTable = xbatisNamespace.getTable(tableName);
            String tableSuffix = xbatisTable.getTableSuffix(DataSourceSelector.getLocalShareValue()).intern();
            char[][] replaceFix = replaceMode.get(tableName);
            for (char[] fix : replaceFix) {
                String origin = (fix[0] + tableName + fix[1]).intern();
                String replace = (fix[0] + tableName + tableSuffix + fix[1]).intern();
                sql = sql.replaceAll(origin, replace);
            }
        }
        return sql;
    }

    private HashMap<String, char[][]> getReplaceMode(String originSql) {
        HashMap<String, HashSet<Integer>> tableModeMap = new HashMap<>();
        Matcher matcher = PATTERN_FROM.matcher(originSql);
        while (matcher.find()) {
            String fromTable = matcher.group(3).intern();
            String[] tableNames = fromTable.split(",");
            if (tableNames.length == 1) {
                appendReplaceMode(tableModeMap, tableNames[0].intern(), SqlTableMode.ReplaceMode.BTB.getMode());
            } else {
                for (int i = 0; i < tableNames.length; i++) {
                    String tableName = tableNames[i].intern();
                    if (i == 0) {
                        appendReplaceMode(tableModeMap, tableName, SqlTableMode.ReplaceMode.BTC.getMode());
                    } else if (i == tableNames.length - 1) {
                        appendReplaceMode(tableModeMap, tableName, SqlTableMode.ReplaceMode.CTB.getMode());
                    } else {
                        appendReplaceMode(tableModeMap, tableName, SqlTableMode.ReplaceMode.CTC.getMode());
                    }
                }
            }
        }
        HashMap<String, char[][]> resultMap = new HashMap<>();
        tableModeMap.forEach((k, v) -> {
            int sum = 0;
            for (Integer integer : v) {
                sum += integer;
            }
            resultMap.put(k, SqlTableMode.getReplaceFix(sum));
        });
        return resultMap;
    }

    private void appendReplaceMode(HashMap<String, HashSet<Integer>> tableModeMap, String tableName, int mode) {
        HashSet<Integer> replaceModeSet;
        if (Objects.isNull(tableModeMap.get(tableName))) {
            replaceModeSet = new HashSet<Integer>() {{
                add(0);
            }};
            tableModeMap.put(tableName, replaceModeSet);
        } else {
            replaceModeSet = tableModeMap.get(tableName);
        }
        replaceModeSet.add(mode);
    }

}
