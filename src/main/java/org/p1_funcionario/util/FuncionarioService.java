
package org.p1_funcionario.util;

import org.p1_funcionario.model.Funcionario;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final List<Funcionario> funcionarios;

    public FuncionarioService() throws IOException {
        this(new FuncionarioRepository());
    }

    public FuncionarioService(FuncionarioRepository repository)
            throws IOException {

        this.repository = Objects.requireNonNull(repository);
        this.funcionarios = new ArrayList<>(repository.carregar());
    }

    public void cadastrar(Funcionario funcionario)
            throws IOException {

        Validacoes.validarFuncionario(funcionario);

        boolean existe = funcionarios.stream()
                .anyMatch(f -> f.getMatricula().equals(
                        funcionario.getMatricula()));

        if (existe) {
            throw new IllegalArgumentException(
                    "Já existe um funcionário com essa matrícula."
            );
        }

        funcionarios.add(funcionario);

        try {
            repository.salvar(funcionarios);
        } catch (IOException e) {
            funcionarios.remove(funcionario);
            throw e;
        }
    }

    public Optional<Funcionario> consultarPorMatricula(
            String matricula) {

        return funcionarios.stream()
                .filter(f -> f.getMatricula().equals(matricula))
                .findFirst();
    }

    public boolean excluir(String matricula) throws IOException {

        Optional<Funcionario> encontrado =
                consultarPorMatricula(matricula);

        if (encontrado.isEmpty()) {
            return false;
        }

        Funcionario removido = encontrado.get();
        funcionarios.remove(removido);

        try {
            repository.salvar(funcionarios);
        } catch (IOException e) {
            funcionarios.add(removido);
            throw e;
        }

        return true;
    }

    public List<Funcionario> listarTodos() {

        return funcionarios.stream()
                .sorted(Comparator.comparing(
                        Funcionario::getNome,
                        String.CASE_INSENSITIVE_ORDER
                ))
                .toList();
    }

    public List<Funcionario> filtrarPorCargo(String cargo) {

        if (cargo == null || cargo.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe um cargo para pesquisar."
            );
        }

        String termo = cargo.trim().toLowerCase(Locale.ROOT);

        return funcionarios.stream()
                .filter(f -> f.getCargo()
                        .toLowerCase(Locale.ROOT).contains(termo))
                .toList();
    }

    public List<Funcionario> filtrarPorFaixaSalarial(
            BigDecimal minimo, BigDecimal maximo) {

        if (minimo == null || maximo == null
                || minimo.signum() < 0
                || maximo.compareTo(minimo) < 0) {

            throw new IllegalArgumentException(
                    "Informe uma faixa salarial válida."
            );
        }

        return funcionarios.stream()
                .filter(f -> f.getSalario()
                        .compareTo(minimo) >= 0)
                .filter(f -> f.getSalario()
                        .compareTo(maximo) <= 0)
                .toList();
    }

    public Map<String, BigDecimal>
    calcularMediaSalarialPorCargo() {

        Map<String, List<Funcionario>> grupos =
                funcionarios.stream()
                        .collect(Collectors.groupingBy(
                                Funcionario::getCargo,
                                TreeMap::new,
                                Collectors.toList()
                        ));

        Map<String, BigDecimal> resultado =
                new LinkedHashMap<>();

        grupos.forEach((cargo, grupo) -> {

            BigDecimal soma = grupo.stream()
                    .map(Funcionario::getSalario)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal media = soma.divide(
                    BigDecimal.valueOf(grupo.size()),
                    2,
                    RoundingMode.HALF_UP
            );

            resultado.put(cargo, media);
        });

        return resultado;
    }

    public Map<String, List<Funcionario>> agruparPorCidade() {

        return funcionarios.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getEndereco().getCidade(),
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    public int quantidade() {
        return funcionarios.size();
    }

    public void atualizar(Funcionario atualizado) throws IOException {

        Validacoes.validarFuncionario(atualizado);

        int indice = -1;

        for (int i = 0; i < funcionarios.size(); i++) {
            if (funcionarios.get(i).getMatricula()
                    .equals(atualizado.getMatricula())) {
                indice = i;
                break;
            }
        }

        if (indice == -1) {
            throw new IllegalArgumentException(
                    "Funcionário não encontrado para atualização."
            );
        }

        Funcionario anterior = funcionarios.set(indice, atualizado);

        try {
            repository.salvar(funcionarios);
        } catch (IOException e) {
            funcionarios.set(indice, anterior);
            throw e;
        }
    }

}
