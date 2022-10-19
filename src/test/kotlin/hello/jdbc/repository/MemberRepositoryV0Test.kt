package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.NoSuchElementException

internal class MemberRepositoryV0Test {

    private val repository = MemberRepositoryV0()

    @Test
    fun crud() {
        // save
        val member = Member("memberV1", 10000)
        repository.save(member)

        // find
        val findMember = repository.findById(member.memberId!!)
        assertThat(findMember).isEqualTo(member)

        // update
        repository.update(member.memberId!!, 20000)
        val updateMember = repository.findById(member.memberId!!)
        assertThat(updateMember.money).isEqualTo(20000)

        // delete
        repository.delete(member.memberId!!)
        assertThatThrownBy { repository.findById(member.memberId!!) }.isInstanceOf(NoSuchElementException::class.java)
    }
}