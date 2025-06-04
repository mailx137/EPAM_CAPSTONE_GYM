package com.gym.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.BlockingQueue;

public class ConnectionProxyFactory {
    public static Connection wrap(Connection realConnection, BlockingQueue<Connection> pool) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[] { Connection.class },
                new PooledConnectionHandler(realConnection, pool));
    }

    private static class PooledConnectionHandler implements InvocationHandler {
        private final Connection realConnection;
        private final BlockingQueue<Connection> pool;
        private boolean isClosed = false;

        public PooledConnectionHandler(Connection realConnection, BlockingQueue<Connection> pool) {
            this.realConnection = realConnection;
            this.pool = pool;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("close".equals(method.getName())) {
                if (!isClosed) {
                    isClosed = true;
                    pool.offer(realConnection);
                }
                return null;
            } else if ("isClosed".equals(method.getName())) {
                return isClosed;
            }

            return method.invoke(realConnection, args);
        }
    }
}