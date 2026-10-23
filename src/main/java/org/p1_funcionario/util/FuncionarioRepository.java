
package org.p1_funcionario.util;

import org.p1_funcionario.model.Endereco;
import org.p1_funcionario.model.Funcionario;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioRepository {

    private final Path arquivo;

    public FuncionarioRepository() {
        this(Paths.get("funcionarios_cadastrados.txt"));
    }

    public FuncionarioRepository(Path arquivo) {
        this.arquivo = arquivo;
    }

    public List<Funcionario> carregar() throws IOException {

        List<Funcionario> funcionarios = new ArrayList<>();

        if (!Files.exists(arquivo)) {
            Path pai = arquivo.toAbsolutePath().getParent();

            if (pai != null) {
                Files.createDirectories(pai);
            }

            Files.createFile(arquivo);
            return funcionarios;
        }

        int numeroLinha = 0;

        for (String linha :
                Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {

            numeroLinha++;

            if (linha.isBlank()) {
                continue;
            }

            String[] c = linha.split(";", -1);

            if (c.length != 14) {
                System.err.println(
                        "Linha " + numeroLinha
                                + " ignorada: formato incompatível."
                );
                continue;
            }

            try {
                Endereco endereco = new Endereco(
                        c[7], c[8], c[9], c[10],
                        c[11], c[12], c[13]
                );

                Funcionario funcionario = new Funcionario(
                        c[0],
                        c[1],
                        c[2],
                        LocalDate.parse(c[3]),
                        c[4],
                        new BigDecimal(c[5]),
                        LocalDate.parse(c[6]),
                        endereco
                );

                funcionarios.add(funcionario);

            } catch (RuntimeException e) {
                System.err.println(
                        "Linha " + numeroLinha
                                + " contém dados inválidos e foi ignorada."
                );
            }
        }

        return funcionarios;
    }

    public void salvar(List<Funcionario> funcionarios)
            throws IOException {

        Path pai = arquivo.toAbsolutePath().getParent();

        if (pai != null) {
            Files.createDirectories(pai);
        }

        List<String> linhas = new ArrayList<>();

        for (Funcionario f : funcionarios) {

            Endereco e = f.getEndereco();

            linhas.add(String.join(";",
                    campo(f.getMatricula()),
                    campo(f.getNome()),
                    campo(f.getCpf()),
                    f.getDataNascimento().toString(),
                    campo(f.getCargo()),
                    f.getSalario().toPlainString(),
                    f.getDataContratacao().toString(),
                    campo(e.getLogradouro()),
                    campo(e.getNumero()),
                    campo(e.getComplemento()),
                    campo(e.getBairro()),
                    campo(e.getCidade()),
                    campo(e.getEstado()),
                    campo(e.getCep())
            ));
        }

        Files.write(
                arquivo,
                linhas,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private String campo(String valor) {

        if (valor == null) {
            return "";
        }

        return valor.trim()
                .replace(";", ",")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    public Path getArquivo() {
        return arquivo;
    }
}
