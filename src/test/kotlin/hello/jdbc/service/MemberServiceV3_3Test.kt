package hello.jdbc.service

import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV3
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

/**
 * 트랜잭션 - 트랜잭션 AOP
 */
@SpringBootTest
internal class MemberServiceV3_3Test(
    private val memberRepository: MemberRepositoryV3,
    private val memberService: MemberServiceV3_3
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
        fun transactionManager(): PlatformTransactionManager {
            return DataSourceTransactionManager(dataSource())
        }

        @Bean
        fun memberRepository(): MemberRepositoryV3 {
            return MemberRepositoryV3(dataSource())
        }

        @Bean
        fun memberService(): MemberServiceV3_3 {
            return MemberServiceV3_3(memberRepository())
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