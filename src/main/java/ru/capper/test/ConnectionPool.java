package ru.capper.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

    static final ComboPooledDataSource ds = new ComboPooledDataSource();
    static Properties prop = new Properties();

    static {
        try {
            String dp = System.getProperty("database.properties");

            InputStream is;
            if (dp == null || dp.isEmpty()) {
                is = ConnectionPool.class.getClassLoader().getResourceAsStream("database.properties");
            } else {
                is = new FileInputStream(dp);
            }

            prop.load(is);
            ds.setDriverClass(prop.getProperty("driverClass"));
            ds.setJdbcUrl(prop.getProperty("url"));
            ds.setUser(prop.getProperty("user"));
            ds.setPassword(prop.getProperty("password"));
            ds.setMinPoolSize(getIntProp("minPoolSize"));
            ds.setAcquireIncrement(getIntProp("acquireIncrement"));
            ds.setMaxPoolSize(getIntProp("maxPoolSize"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        } catch (PropertyVetoException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private static int getIntProp(final String propName) {
        try {
            return Integer.valueOf(prop.getProperty(propName));
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
            return -1;
        }
    }
}