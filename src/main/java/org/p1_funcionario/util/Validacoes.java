package org.p1_funcionario.util;

import org.p1_funcionario.model.Endereco;
import org.p1_funcionario.model.Funcionario;

import java.time.LocalDate;
import java.time.Period;

public final class Validacoes {

    private Validacoes() {
    }

    public static boolean matriculaValida(String matricula) {
        return matricula != null
                && matricula.matches("\\d{6}");
    }

    public static boolean cpfValido(String cpf) {

        if (cpf == null) {
            return false;
        }

        String s = cpf.replaceAll("\\D", "");

        if (!s.matches("\\d{11}")
                || s.chars().distinct().count() == 1) {
            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += (s.charAt(i) - '0') * (10 - i);
        }

        int digito1 = 11 - soma % 11;

        if (digito1 > 9) {
            digito1 = 0;
        }

        if (digito1 != s.charAt(9) - '0') {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += (s.charAt(i) - '0') * (11 - i);
        }

        int digito2 = 11 - soma % 11;

        if (digito2 > 9) {
            digito2 = 0;
        }

        return digito2 == s.charAt(10) - '0';
    }

    public static boolean maiorDeIdade(LocalDate nascimento) {

        return nascimento != null
                && !nascimento.isAfter(LocalDate.now())
                && Period.between(
                nascimento, LocalDate.now()
        ).getYears() >= 18;
    }

    public static boolean cepValido(String cep) {

        return cep != null
                && cep.replaceAll("\\D", "").matches("\\d{8}");
    }

    public static void validarFuncionario(Funcionario f) {

        if (f == null) {
            throw new IllegalArgumentException(
                    "Funcionário não informado."
            );
        }

        if (!matriculaValida(f.getMatricula())) {
            throw new IllegalArgumentException(
                    "A matrícula deve conter 6 dígitos."
            );
        }

        if (f.getNome() == null
                || f.getNome().trim().length() < 3) {
            throw new IllegalArgumentException(
                    "O nome deve ter pelo menos 3 caracteres."
            );
        }

        if (!cpfValido(f.getCpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        if (!maiorDeIdade(f.getDataNascimento())) {
            throw new IllegalArgumentException(
                    "O funcionário deve ter pelo menos 18 anos."
            );
        }

        if (f.getCargo() == null || f.getCargo().isBlank()) {
            throw new IllegalArgumentException(
                    "O cargo é obrigatório."
            );
        }

        if (f.getSalario() == null
                || f.getSalario().signum() <= 0) {
            throw new IllegalArgumentException(
                    "O salário deve ser maior que zero."
            );
        }

        if (f.getDataContratacao() == null
                || f.getDataContratacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Data de contratação inválida."
            );
        }

        Endereco e = f.getEndereco();

        if (e == null) {
            throw new IllegalArgumentException(
                    "O endereço é obrigatório."
            );
        }

        if (vazio(e.getLogradouro())
                || vazio(e.getNumero())
                || vazio(e.getBairro())
                || vazio(e.getCidade())
                || vazio(e.getEstado())) {
            throw new IllegalArgumentException(
                    "Preencha os campos obrigatórios do endereço."
            );
        }

        if (!cepValido(e.getCep())) {
            throw new IllegalArgumentException(
                    "O CEP deve conter 8 dígitos."
            );
        }
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}