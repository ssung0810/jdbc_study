package hello.jdbc.service

import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV1
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.DriverManagerDataSource

/**
 * 기본 동작, 트랜잭션이 없어서 문제 발생
 */
internal class MemberServiceV1Test {
    private val MEMBER_A = "memberA"
    private val MEMBER_B = "memberB"
    private val MEMBER_EX = "ex"

    private var memberRepository: MemberRepositoryV1? = null
    private var memberService: MemberServiceV1? = null

    @BeforeEach
    fun beforeEach() {
        val dataSource = DriverManagerDataSource(URL, USERNAME, PASSWORD)
        memberRepository = MemberRepositoryV1(dataSource)
        memberService = MemberServiceV1(memberRepository!!)
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

        assertThat(findMemberA.money).isEqualTo(8000)
        assertThat(findMemberB.money).isEqualTo(10000)
    }
}