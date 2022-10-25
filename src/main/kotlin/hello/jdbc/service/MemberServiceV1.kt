package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV1

class MemberServiceV1(
    private val memberRepository: MemberRepositoryV1
) {
    fun accountTransfer(fromId: String, toId: String, money: Int) {
        val fromMember = memberRepository.findById(fromId)
        val toMember = memberRepository.findById(toId)

        memberRepository.update(fromId, fromMember.money?.minus(money) ?: 0)
        validation(toMember)
        memberRepository.update(toId, toMember.money?.plus(money) ?: 0)
    }

    private fun validation(toMember: Member) {
        if (toMember.memberId.equals("ex")) {
            throw IllegalStateException("이체중 예외 발생")
        }
    }
}