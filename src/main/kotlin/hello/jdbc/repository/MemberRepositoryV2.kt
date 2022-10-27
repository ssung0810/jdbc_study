package hello.jdbc.repository

import hello.jdbc.domain.Member
import org.slf4j.LoggerFactory
import org.springframework.jdbc.support.JdbcUtils
import java.sql.*
import javax.sql.DataSource

/**
 * JDBC - Connection
 */
class MemberRepositoryV2(
    private val dataSource: DataSource? = null
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

    fun findById(con: Connection, memberId: String): Member {
        val sql = "select * from member where member_id = ?"

        var pstmt: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
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

    fun update(con: Connection, memberId: String, money: Int) {
        val sql = "update member set money=? where member_id=?"

        var pstmt: PreparedStatement? = null

        try {
            pstmt = con.prepareStatement(sql)
            pstmt.setInt(1, money)
            pstmt.setString(2, memberId)

            val resultSize = pstmt.executeUpdate()
        } catch (e: SQLException) {
            throw e
        } finally {
            JdbcUtils.closeStatement(pstmt)
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
        JdbcUtils.closeConnection(con)
        JdbcUtils.closeStatement(stmt)
        JdbcUtils.closeResultSet(rs)
    }

    private fun getConnection(): Connection {
        val con = dataSource!!.connection
        logger.info("connection=${con} / class=${con.javaClass}")
        return con
    }
}