package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.jdbc.support.JdbcUtils
import java.sql.*
import javax.sql.DataSource

/**
 * repository 추상화
 */
class MemberRepositoryV5(
    private val jdbcTemplate: JdbcTemplate
) : MemberRepository {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values(?, ?)"
        jdbcTemplate.update(sql, member.memberId, member.money)

        return member
    }

    override fun findById(memberId: String): Member {
        val sql = "select * from member where member_id = ?"
        return jdbcTemplate.queryForObject(sql, memberRowMapper, memberId)!!
    }

    private val memberRowMapper = RowMapper<Member> { rs, rowNum ->
        Member(
            rs.getString("member_id"),
            rs.getInt("money")
        )
    }

    override fun update(memberId: String, money: Int) {
        val sql = "update member set money=? where member_id=?"
        jdbcTemplate.update(sql, money, memberId)
    }

    override fun delete(memberId: String) {
        val sql = "delete from member where member_id = ?"
        jdbcTemplate.update(sql, memberId)
    }
}