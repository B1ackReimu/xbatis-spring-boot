package org.xbatis.spring.boot.autoconfigure;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbatis.spring.boot.autoconfigure.util.SqlRewriter;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Component
public class XbatisInterceptor implements Interceptor {

    public static final ThreadLocal<String> STATEMENT_ID = new ThreadLocal<>();


    private SqlRewriter sqlRewriter;

    @Autowired
    public XbatisInterceptor(SqlRewriter sqlRewriter) {
        this.sqlRewriter = sqlRewriter;
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
        System.out.println("origin: " + sql);
        String rewrite = sqlRewriter.rewrite(STATEMENT_ID.get(), sql);
        field.set(boundSql, rewrite);
        System.out.println("replace: " + rewrite);
        STATEMENT_ID.remove();
        return invocation.proceed();
    }

}
