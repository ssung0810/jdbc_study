package hello.jdbc.connection

abstract class ConnectionConst {
    companion object {
        const val URL = "jdbc:mysql://localhost:3306/jdbc_study"
        const val USERNAME = "root"
        const val PASSWORD = "passw0rd"
    }
}
