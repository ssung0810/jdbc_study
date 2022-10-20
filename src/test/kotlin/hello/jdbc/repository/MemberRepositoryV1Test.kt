package hello.jdbc.repository

import com.zaxxer.hikari.HikariDataSource
import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import hello.jdbc.domain.Member
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.DriverManagerDataSource

internal class MemberRepositoryV1Test {

    var repository: MemberRepositoryV1 = MemberRepositoryV1()

    @BeforeEach
    fun beforeEach() {
        val dataSourceD = DriverManagerDataSource(URL, USERNAME, PASSWORD)

        val dataSourceH = HikariDataSource()
        dataSourceH.jdbcUrl = URL
        dataSourceH.username = USERNAME
        dataSourceH.password = PASSWORD

        repository = MemberRepositoryV1(dataSourceH)
        Thread.sleep(1000)
    }

    @Test
    fun crud() {
        // save
        val member = Member("memberV1", 10000)
        repository.save(member)

        // find
        val findMember = repository.findById(member.memberId!!)
        Assertions.assertThat(findMember).isEqualTo(member)

        // update
        repository.update(member.memberId!!, 20000)
        val updateMember = repository.findById(member.memberId!!)
        Assertions.assertThat(updateMember.money).isEqualTo(20000)

        // delete
        repository.delete(member.memberId!!)
        Assertions.assertThatThrownBy { repository.findById(member.memberId!!) }.isInstanceOf(NoSuchElementException::class.java)
    }
}