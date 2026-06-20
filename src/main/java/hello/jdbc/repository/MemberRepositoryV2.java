package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.JdbcUtils;

/**
 * JDBC - ConnectionParam
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member(member_id, money) VALUES (?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();

            return member;
        } catch(Exception e) {
            log.error("DB error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "SELECT * FROM member WHERE member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member(
                    rs.getString("member_id"),
                    rs.getInt("money")
                );

                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch(Exception e) {
            log.error("DB error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "SELECT * FROM member WHERE member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member(
                    rs.getString("member_id"),
                    rs.getInt("money")
                );

                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch(Exception e) {
            log.error("DB error", e);
            throw e;
        } finally {
            // connection 은 여기서 닫지 않는다.
            JdbcUtils.closeSilently(pstmt);
            JdbcUtils.closeSilently(rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "UPDATE member SET money = ? WHERE member_id = ?";

        Connection con  = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch(Exception e) {
            log.error("DB error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "UPDATE member SET money = ? WHERE member_id = ?";

        PreparedStatement pstmt = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch(Exception e) {
            log.error("DB error", e);
            throw e;
        } finally {
            JdbcUtils.closeSilently(pstmt);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "DELETE FROM member WHERE member_id = ?";

        Connection con  = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch(Exception e) {
            log.error("DB error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeSilently(con);
        JdbcUtils.closeSilently(stmt);
        JdbcUtils.closeSilently(rs);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }

}
