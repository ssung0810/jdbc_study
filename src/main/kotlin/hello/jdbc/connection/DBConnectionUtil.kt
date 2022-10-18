package hello.jdbc.connection

import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DBConnectionUtil {
    companion object {
        private val logger = LoggerFactory.getLogger(this.javaClass)

        fun getConnection(): Connection {
            try {
                val connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)
                logger.error("get connection=${connection}, class=${connection.javaClass}")
                return connection
            } catch (e: SQLException) {
                throw IllegalStateException(e)
            }
        }
    }
}