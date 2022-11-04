package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.jdbc.support.JdbcUtils
import java.sql.*
import javax.sql.DataSource

/**
 * 트랜잭션 - 트랜잭션 매니저
 * DataSourceUtils.getConnection()
 * DataSourceUtils.getConnection()
 */
class MemberRepositoryV3(
    private val dataSource: DataSource
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values(?, ?)"

        var pstmt: PreparedStatement? = null
        var con: Connection? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setString(1, member.memberId)
            pstmt.setInt(2, member.money!!)
            pstmt.executeUpdate()
            return member
        } catch (e: SQLException) {
            throw e
        } finally {
            JdbcUtils.closeStatement(pstmt)
        }
    }

    fun findById(memberId: String): Member {
        val sql = "select * from member where member_id = ?"

        var pstmt: PreparedStatement? = null
        var rs: ResultSet? = null
        var con: Connection? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setString(1, memberId)

            rs = pstmt.executeQuery()
            if (rs.next()) {
                val member = Member()
                member.memberId = rs.getString("member_id")
                member.money = rs.getInt("money")
                return member
            } else {
                throw NoSuchElementException("member not found member_id=${memberId}")
            }
        } catch (e: SQLException) {
            throw e
        } finally {
            JdbcUtils.closeStatement(pstmt)
            JdbcUtils.closeResultSet(rs)
        }
    }

    fun update(memberId: String, money: Int) {
        val sql = "update member set money=? where member_id=?"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setInt(1, money)
            pstmt.setString(2, memberId)

            val resultSize = pstmt.executeUpdate()
        } catch (e: SQLException) {
            throw e
        } finally {
            close(con, pstmt, null)
        }
    }

    fun delete(memberId: String) {
        val sql = "delete from member where member_id = ?"

        var pstmt: PreparedStatement? = null
        var con: Connection? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setString(1, memberId)

            val resultSize = pstmt.executeUpdate()
        } catch (e: SQLException) {
            throw e
        } finally {
            close(con, pstmt, null)
        }
    }

    private fun close(con: Connection?, stmt: Statement?, rs: ResultSet?) {
        JdbcUtils.closeStatement(stmt)
        JdbcUtils.closeResultSet(rs)
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con, dataSource!!)
    }

    private fun getConnection(): Connection {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        val con = DataSourceUtils.getConnection(dataSource!!)
        logger.info("connection=${con} / class=${con.javaClass}")
        return con
    }
}