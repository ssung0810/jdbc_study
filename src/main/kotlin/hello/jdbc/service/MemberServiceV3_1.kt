package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV1
import hello.jdbc.repository.MemberRepositoryV2
import hello.jdbc.repository.MemberRepositoryV3
import org.slf4j.LoggerFactory
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.AbstractPlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.lang.Exception
import java.sql.Connection
import javax.sql.DataSource
/**
 * 트랜잭션 - 트랜잭션 매니저
 */
class MemberServiceV3_1(
//    private val dataSource: DataSource,
    private val memberRepository: MemberRepositoryV3,
    private val transactionManager: PlatformTransactionManager
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun accountTransfer(fromId: String, toId: String, money: Int) {
        val status = transactionManager.getTransaction(DefaultTransactionDefinition())

        try {
            val fromMember = memberRepository.findById(fromId)
            val toMember = memberRepository.findById(toId)

            memberRepository.update(fromId, fromMember.money?.minus(money) ?: 0)
            validation(toMember)
            memberRepository.update(toId, toMember.money?.plus(money) ?: 0)

            transactionManager.commit(status)
        } catch (e: Exception) {
            transactionManager.rollback(status)
            throw IllegalStateException(e)
        }
    }

    private fun validation(toMember: Member) {
        if (toMember.memberId.equals("ex")) {
            throw IllegalStateException("이체중 예외 발생")
        }
    }
}

