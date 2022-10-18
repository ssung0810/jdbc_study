package hello.jdbc.connection

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DBConnectionUtilTest {

    @Test
    fun connection() {
        val connection = DBConnectionUtil.getConnection()
        assertThat(connection).isNotNull
    }
}