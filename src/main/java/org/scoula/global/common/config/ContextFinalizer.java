package org.scoula.global.common.config;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@WebListener
public class ContextFinalizer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 컨텍스트 초기화 시 필요한 작업
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // MySQL 드라이버 해제
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // MySQL AbandonedConnectionCleanupThread 종료
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
