package hello.jdbc.repository

import hello.jdbc.connection.DBConnectionUtil
import hello.jdbc.domain.Member
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class MemberRepositoryV0 {
    fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values(?, ?)"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null

        try {
            con = getConnection()
            pstmt = con.prepareStatement(sql)
            pstmt.setString(1, member.memberId)
            pstmt.setInt(2, member.money)
            pstmt.executeUpdate()
            return member
        } catch (e: SQLException) {
            throw e
        } finally {
            close(con, pstmt, null)
        }

    }

    private fun close(con: Connection?, stmt: Statement?, rs: ResultSet?) {
        if (stmt != null) {
            try {
                stmt.close()
            } catch (e: SQLException) {
            }
        }

        if (con != null) {
            try {
                con.close()
            } catch (e: SQLException) {
            }
        }
    }

    private fun getConnection(): Connection {
        return DBConnectionUtil.getConnection()
    }
}