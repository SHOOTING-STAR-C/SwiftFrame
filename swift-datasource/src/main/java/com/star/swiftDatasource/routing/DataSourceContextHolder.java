package com.star.swiftDatasource.routing;

import com.star.swiftDatasource.constants.DataSourceEnum;

import java.util.Deque;
import java.util.ArrayDeque;

public class DataSourceContextHolder {
    // 使用栈支持嵌套切换
    private static final ThreadLocal<Deque<DataSourceEnum>> STACK =
        ThreadLocal.withInitial(ArrayDeque::new);

    public static void pushDataSource(DataSourceEnum ds) {
        STACK.get().push(ds);
    }

    public static DataSourceEnum getDataSource() {
        return STACK.get().peekFirst();
    }

    public static void popDataSource() {
        Deque<DataSourceEnum> stack = STACK.get();
        stack.pop();
        if (stack.isEmpty()) {
            STACK.remove();
        }
    }

    public static void clear() {
        STACK.remove();
    }
}
