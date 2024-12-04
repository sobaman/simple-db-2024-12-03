package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sql {

    private final Connection conn;

    private StringBuilder sb = new StringBuilder();
    private List<Object> params = new ArrayList<>();

    public Sql(Connection conn) {
        this.conn = conn;
    }


    //todo param 저장해서 나중에 바인딩
    public Sql append(String sqlBit, Object... param) {
        sb.append(sqlBit).append(" ");
        params.addAll(Arrays.asList(param));
        return this;
    }

    public long insert() {
        String sql = sb.toString();

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindParameters(pst, params);
            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Don't find Key");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL : " + sql + " Error : " + e.getMessage(), e);
        }
    }



    private void bindParameters(PreparedStatement pst, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            pst.setObject(i + 1, params.get(i));
        }
    }


    public int update() {

        String sql = sb.toString();

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            bindParameters(pst, params);
            return pst.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL : " + sql + " Error : " + e.getMessage(), e);
        }
    }
}