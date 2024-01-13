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
        config.setUsername("3d6fp2mzezmhjzalqke3");
        config.setPassword("pscale_pw_M7h1N9iSYHci4QLF31NPI2ieyqlzohZ2XxFUCxmBSA2");
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