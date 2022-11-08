package hello.jdbc.service

import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepository
import hello.jdbc.repository.MemberRepositoryV5
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

/**
 * 트랜잭션 - 트랜잭션 AOP
 */
@SpringBootTest
internal class MemberServiceV5Test(
    private val memberRepository: MemberRepository,
    private val memberService: MemberServiceV5
) {
    private val MEMBER_A = "memberA"
    private val MEMBER_B = "memberB"
    private val MEMBER_EX = "ex"

    @TestConfiguration
    class Config() {
        @Bean
        fun dataSource(): DataSource {
            return DriverManagerDataSource(URL, USERNAME, PASSWORD)
        }

        @Bean
        fun jdbcTemplate(): JdbcTemplate {
            return JdbcTemplate(dataSource())
        }

        @Bean
        fun memberRepository(): MemberRepository {
            return MemberRepositoryV5(jdbcTemplate())
        }

        @Bean
        fun memberService(): MemberServiceV5 {
            return MemberServiceV5(memberRepository())
        }
    }

    @AfterEach
    fun afterEach() {
        memberRepository!!.delete(MEMBER_A)
        memberRepository!!.delete(MEMBER_B)
        memberRepository!!.delete(MEMBER_EX)
    }

    @Test
    fun 정상_이체() {
        val memberA = Member(MEMBER_A, 10000)
        val memberB = Member(MEMBER_B, 10000)

        memberRepository!!.save(memberA)
        memberRepository!!.save(memberB)

        memberService!!.accountTransfer(memberA.memberId!!, memberB.memberId!!, 2000)

        val findMemberA = memberRepository!!.findById(memberA.memberId!!)
        val findMemberB = memberRepository!!.findById(memberB.memberId!!)

        assertThat(findMemberA.money).isEqualTo(8000)
        assertThat(findMemberB.money).isEqualTo(12000)
    }

    @Test
    fun 이체_에러() {
        val memberA = Member(MEMBER_A, 10000)
        val memberEX = Member(MEMBER_EX, 10000)

        memberRepository!!.save(memberA)
        memberRepository!!.save(memberEX)

        assertThatThrownBy { memberService!!.accountTransfer(memberA.memberId!!, memberEX.memberId!!, 2000) }
            .isInstanceOf(IllegalStateException::class.java)

        val findMemberA = memberRepository!!.findById(memberA.memberId!!)
        val findMemberB = memberRepository!!.findById(memberEX.memberId!!)

        assertThat(findMemberA.money).isEqualTo(10000)
        assertThat(findMemberB.money).isEqualTo(10000)
    }
}