package org.xbatis.spring.boot.autoconfigure;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Component
public class XbatisInterceptor implements Interceptor {

    public static final ThreadLocal<String> STATEMENT_ID = new ThreadLocal<>();

    private XbatisRouter xbatisRouter;

    @Autowired
    public void setXbatisRouter(XbatisRouter xbatisRouter) {
        this.xbatisRouter = xbatisRouter;
    }

    private Field field;

    {
        try {
            field = BoundSql.class.getDeclaredField("sql");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        System.out.println(sql);
        sql = sql.replace("id = 1", "id = 2");
        field.set(boundSql, sql);
        System.out.println(sql);
        return invocation.proceed();
    }

    private String replaceSql(String statementId, String originSql) {
        String s = originSql.replaceAll(" + ", " ");

        return null;
    }

    public static void main(String[] args) {
        String s = "select * from  a   where id =  5     ";
        System.out.println(s.replaceAll("\\s+"," "));
    }

}
