package io.jbank.domain.conta;

import io.jbank.utils.ConnectionFactory;
import io.jbank.domain.RegraDeNegocioException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Set;

public class ContaService {

    private ConnectionFactory connection;

    public ContaService() {
        this.connection = new ConnectionFactory();
    }

    public Set<Conta> listarContasAbertas() {
        Connection conn = connection.abrirConexao();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = connection.abrirConexao();
        new ContaDAO(conn).abrir(dadosDaConta);
    }

    private void alterar(Conta conta, BigDecimal valor) {
        Connection conn = connection.abrirConexao();
        new ContaDAO(conn).alterar(conta.getNumero(), valor);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        if (!conta.getAtiva()) {
            throw new RegraDeNegocioException("Conta inativa!");
        }

        BigDecimal novoValor = conta.getSaldo().subtract(valor);
        alterar(conta, novoValor);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        if (!conta.getAtiva()) {
            throw new RegraDeNegocioException("Conta inativa!");
        }

        BigDecimal novoValor = conta.getSaldo().add(valor);

        alterar(conta, novoValor);
    }

    public void realizarTransferencia(Integer numeroContaOrigem, Integer numeroContaDestino, BigDecimal valor) {
        this.realizarSaque(numeroContaOrigem, valor);
        this.realizarDeposito(numeroContaDestino, valor);
    }

    public void deletar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        Connection conn = connection.abrirConexao();
        new ContaDAO(conn).deletar(numeroDaConta);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        if (!conta.getAtiva()) {
            throw new RegraDeNegocioException("Conta já etá encerrada!");
        }

        Connection conn = connection.abrirConexao();
        new ContaDAO(conn).encerrar(numeroDaConta);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Connection conn = connection.abrirConexao();
        Conta conta = new ContaDAO(conn).listarPorNumero(numero);
        if (conta != null) {
            return conta;
        } else {
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
        }
    }

    public Set<Conta> listarContasInativas() {
        Connection conn = connection.abrirConexao();
        return new ContaDAO(conn).listarInativas();
    }

}
