package hello.jdbc.repository

import hello.jdbc.connection.DBConnectionUtil
import hello.jdbc.domain.Member
import java.sql.*

class MemberRepositoryV0 {
    fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values(?, ?)"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null

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
            close(con, pstmt, null)
        }
    }

    fun findById(memberId: String): Member {
        val sql = "select * from member where member_id = ?"

        var con: Connection? = null
        var pstmt: PreparedStatement? = null
        var rs: ResultSet? = null

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
            close(con, pstmt, rs)
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

        var con: Connection? = null
        var pstmt: PreparedStatement? = null

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