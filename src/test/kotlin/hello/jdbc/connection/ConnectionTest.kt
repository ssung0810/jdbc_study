package hello.jdbc.connection

import com.zaxxer.hikari.HikariDataSource
import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.sql.DriverManager

class ConnectionTest {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun driverManagerTest() {
        val con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD)
        val con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD)
        logger.info("connection=${con1} / getClass=${con1.javaClass}")
        logger.info("connection=${con2} / getClass=${con2.javaClass}")
    }

    @Test
    fun driverManagerDataSourceTest() {
        val dataSource = DriverManagerDataSource(URL, USERNAME, PASSWORD)
        val con1 = dataSource.connection
        val con2 = dataSource.connection
        logger.info("connection=${con1} / getClass=${con1.javaClass}")
        logger.info("connection=${con2} / getClass=${con2.javaClass}")
    }

    @Test
    fun dataSourceConnectionPool() {
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = URL
        dataSource.username = USERNAME
        dataSource.password = PASSWORD
        dataSource.maximumPoolSize = 10
        dataSource.poolName = "MyPool"

        val con1 = dataSource.connection
        val con2 = dataSource.connection
        logger.info("connection=${con1} / getClass=${con1.javaClass}")
        logger.info("connection=${con2} / getClass=${con2.javaClass}")
        Thread.sleep(1000)
    }
}