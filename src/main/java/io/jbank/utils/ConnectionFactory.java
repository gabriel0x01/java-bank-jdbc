package io.jbank.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public Connection abrirConexao() {
        try {
            Connection connection = DriverManager.getConnection();

            return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fecharConexao(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}