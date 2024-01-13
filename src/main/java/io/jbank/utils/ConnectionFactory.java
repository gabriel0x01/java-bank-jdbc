package io.jbank.utils;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionFactory {

    public Connection abrirConexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return createDataSource().getConnection();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private HikariDataSource createDataSource() throws ClassNotFoundException {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://aws.connect.psdb.cloud/jbank?sslMode=VERIFY_IDENTITY");
        config.setUsername("6np3l6z4ef98xva123s6");
        config.setPassword("pscale_pw_LrZIf95JyYwEdJV8cUauAz6Id9S0iQNAK5CjmDCF0Xf");
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }

    public void fecharConexao(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}