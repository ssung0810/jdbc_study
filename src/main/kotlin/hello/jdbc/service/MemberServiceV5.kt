package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepository
import hello.jdbc.repository.MemberRepositoryV3
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * repository 추상화
 */
class MemberServiceV5(
    private val memberRepository: MemberRepository,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    fun accountTransfer(fromId: String, toId: String, money: Int) {
        try {
            val fromMember = memberRepository.findById(fromId)
            val toMember = memberRepository.findById(toId)

            memberRepository.update(fromId, fromMember.money?.minus(money) ?: 0)
            validation(toMember)
            memberRepository.update(toId, toMember.money?.plus(money) ?: 0)

        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    private fun validation(toMember: Member) {
        if (toMember.memberId.equals("ex")) {
            throw IllegalStateException("이체중 예외 발생")
        }
    }
}

