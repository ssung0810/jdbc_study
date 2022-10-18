package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MemberRepositoryV0Test {

    @Test
    fun crud() {
        val member = Member("memberV1", 10000)
        val memberRepositoryV0 = MemberRepositoryV0()

        memberRepositoryV0.save(member)
    }
}