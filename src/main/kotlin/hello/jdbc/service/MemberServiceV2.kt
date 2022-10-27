package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV1
import hello.jdbc.repository.MemberRepositoryV2
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.sql.Connection
import javax.sql.DataSource

class MemberServiceV2(
    private val memberRepository: MemberRepositoryV2,
    private val dataSource: DataSource
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun accountTransfer(fromId: String, toId: String, money: Int) {
        val con = dataSource.connection

        try {
            con.autoCommit = false

            val fromMember = memberRepository.findById(con, fromId)
            val toMember = memberRepository.findById(con, toId)

            memberRepository.update(con, fromId, fromMember.money?.minus(money) ?: 0)
            validation(toMember)
            memberRepository.update(con, toId, toMember.money?.plus(money) ?: 0)

            con.commit()
        } catch (e: Exception) {
            con.rollback()
            throw IllegalStateException(e)
        } finally {
            release(con)
        }
    }

    private fun release(con: Connection) {
        if (con != null) {
            try {
                con.autoCommit = true
                con.close()
            } catch (e: Exception) {
                logger.error(e.message)
            }
        }
    }

    private fun validation(toMember: Member) {
        if (toMember.memberId.equals("ex")) {
            throw IllegalStateException("이체중 예외 발생")
        }
    }
}