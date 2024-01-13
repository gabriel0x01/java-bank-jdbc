package io.jbank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import io.jbank.domain.cliente.Cliente;
import io.jbank.domain.cliente.DadosCadastroCliente;

public class ContaDAO {

    private Connection connection;

    ContaDAO(Connection connection) {
        this.connection = connection;
    }

    public void abrir(DadosAberturaConta dadosDaConta) {

        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email) VALUES (?, ?, ?, ?, ?)";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, conta.getTitular().getNome());
            preparedStatement.setString(4, conta.getTitular().getCpf());
            preparedStatement.setString(5, conta.getTitular().getEmail());

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Set<Conta> listar() {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        Set<Conta> contas = new HashSet<>();

        try {

            String sql = "SELECT * FROM conta";

            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                // também poderia pegar pelo índice da coluna, ou utilizar o tipo de dado exato.
                var numero = resultSet.getInt("numero");
                var saldo = resultSet.getBigDecimal("saldo");
                var nome = resultSet.getString("cliente_nome");
                var cpf = resultSet.getString("cliente_cpf");
                var email = resultSet.getString("cliente_email");

                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);

                var cliente = new Cliente(dadosCadastroCliente);

                contas.add(new Conta(numero, saldo, cliente));
            }

            resultSet.close();
            preparedStatement.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return contas;

    }

    public Conta listarPorNumero(Integer numero) {
        String sql = "SELECT * FROM conta WHERE numero = ?";

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Conta conta = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, numero);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Integer numeroRecuperado = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta = new Conta(numeroRecuperado, saldo, cliente);
            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return conta;

    }

    public void alterar(Integer numero, BigDecimal valor) {
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setBigDecimal(1, valor);
            preparedStatement.setInt(2, numero);

            preparedStatement.execute();
            connection.commit();
            preparedStatement.close();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
