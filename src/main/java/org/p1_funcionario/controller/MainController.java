
package org.p1_funcionario.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.p1_funcionario.model.Endereco;
import org.p1_funcionario.model.Funcionario;
import org.p1_funcionario.util.FuncionarioService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    // Campos do cadastro
    @FXML private TextField matriculaField;
    @FXML private TextField nomeField;
    @FXML private TextField cpfField;
    @FXML private TextField cargoField;
    @FXML private TextField salarioField;

    @FXML private DatePicker nascimentoPicker;
    @FXML private DatePicker contratacaoPicker;

    @FXML private TextField logradouroField;
    @FXML private TextField numeroField;
    @FXML private TextField complementoField;
    @FXML private TextField bairroField;
    @FXML private TextField cidadeField;
    @FXML private TextField estadoField;
    @FXML private TextField cepField;

    // Consulta e mensagens
    @FXML private TextField consultaField;
    @FXML private Label statusLabel;

    // Tabela de funcionários
    @FXML private TableView<Funcionario> tabela;
    @FXML private TableColumn<Funcionario, String> colMatricula;
    @FXML private TableColumn<Funcionario, String> colNome;
    @FXML private TableColumn<Funcionario, String> colCpf;
    @FXML private TableColumn<Funcionario, String> colCargo;
    @FXML private TableColumn<Funcionario, String> colCidade;
    @FXML private TableColumn<Funcionario, BigDecimal> colSalario;

    // Relatórios
    @FXML private TextField cargoRelatorioField;
    @FXML private TextField salarioMinField;
    @FXML private TextField salarioMaxField;
    @FXML private TextArea relatorioArea;

    private FuncionarioService service;

    private final ObservableList<Funcionario> dadosTabela =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            service = new FuncionarioService();
            configurarTabela();
            atualizarTabela(service.listarTodos());
            status("Sistema carregado com sucesso.");
        } catch (Exception e) {
            status("Erro ao carregar funcionários: " + e.getMessage());
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Erro de inicialização",
                    "Não foi possível carregar os funcionários.",
                    e.getMessage()
            );
        }
    }

    private void configurarTabela() {
        colMatricula.setCellValueFactory(
                new PropertyValueFactory<>("matricula"));
        colNome.setCellValueFactory(
                new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(
                new PropertyValueFactory<>("cpf"));
        colCargo.setCellValueFactory(
                new PropertyValueFactory<>("cargo"));
        colSalario.setCellValueFactory(
                new PropertyValueFactory<>("salario"));

        colCidade.setCellValueFactory(cellData -> {
            Funcionario f = cellData.getValue();

            String cidade = "";
            if (f.getEndereco() != null
                    && f.getEndereco().getCidade() != null) {
                cidade = f.getEndereco().getCidade();
            }

            return new javafx.beans.property.SimpleStringProperty(cidade);
        });

        tabela.setItems(dadosTabela);

        tabela.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, selecionado) -> {
                    if (selecionado != null) {
                        preencherFormulario(selecionado);
                    }
                });
    }

    @FXML
    private void cadastrarFuncionario() {
        try {
            Funcionario funcionario = lerFormulario();

            service.cadastrar(funcionario);

            atualizarTabela(service.listarTodos());
            limparCampos();
            status("Funcionário cadastrado com sucesso.");

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Cadastro",
                    "Cadastro realizado.",
                    "O funcionário foi cadastrado com sucesso."
            );

        } catch (Exception e) {
            tratarErro("Não foi possível cadastrar o funcionário.", e);
        }
    }

    @FXML
    private void atualizarFuncionario() {
        try {
            Funcionario funcionario = lerFormulario();

            service.atualizar(funcionario);

            atualizarTabela(service.listarTodos());
            limparCampos();
            status("Funcionário atualizado com sucesso.");

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Atualização",
                    "Atualização realizada.",
                    "Os dados foram atualizados com sucesso."
            );

        } catch (Exception e) {
            tratarErro("Não foi possível atualizar o funcionário.", e);
        }
    }

    @FXML
    private void excluirSelecionado() {
        Funcionario selecionado =
                tabela.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Exclusão",
                    "Nenhum funcionário selecionado.",
                    "Selecione um funcionário na tabela."
            );
            return;
        }

        try {
            service.excluir(selecionado.getMatricula());

            atualizarTabela(service.listarTodos());
            limparCampos();
            status("Funcionário excluído com sucesso.");

        } catch (Exception e) {
            tratarErro("Não foi possível excluir o funcionário.", e);
        }
    }

    @FXML
    private void consultarFuncionario() {
        try {
            String matricula = consultaField.getText().trim();

            if (matricula.isEmpty()) {
                throw new IllegalArgumentException(
                        "Informe a matrícula para consultar."
                );
            }

            java.util.Optional<Funcionario> resultado =
                    service.consultarPorMatricula(matricula);

            if (resultado.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Consulta",
                        "Funcionário não encontrado.",
                        "Não existe funcionário com essa matrícula."
                );
                return;
            }

            Funcionario funcionario = resultado.get();

            preencherFormulario(funcionario);
            tabela.getSelectionModel().select(funcionario);
            status("Funcionário encontrado.");

        } catch (Exception e) {
            tratarErro("Não foi possível realizar a consulta.", e);
        }
    }

    @FXML
    private void listarFuncionarios() {
        try {
            atualizarTabela(service.listarTodos());
            status("Lista de funcionários atualizada.");
        } catch (Exception e) {
            tratarErro("Não foi possível listar os funcionários.", e);
        }
    }

    @FXML
    private void filtrarPorCargo() {
        try {
            String cargo = cargoRelatorioField.getText().trim();

            if (cargo.isEmpty()) {
                throw new IllegalArgumentException(
                        "Informe o cargo para filtrar."
                );
            }

            List<Funcionario> filtrados =
                    service.filtrarPorCargo(cargo);

            atualizarTabela(filtrados);
            status("Filtro por cargo aplicado.");

        } catch (Exception e) {
            tratarErro("Não foi possível filtrar por cargo.", e);
        }
    }

    @FXML
    private void filtrarPorSalario() {
        try {
            BigDecimal minimo =
                    converterSalario(salarioMinField.getText());

            BigDecimal maximo =
                    converterSalario(salarioMaxField.getText());

            if (minimo.compareTo(maximo) > 0) {
                throw new IllegalArgumentException(
                        "O salário mínimo não pode ser maior que o máximo."
                );
            }

            List<Funcionario> filtrados =
                    service.filtrarPorFaixaSalarial(minimo, maximo);

            atualizarTabela(filtrados);
            status("Filtro salarial aplicado.");

        } catch (Exception e) {
            tratarErro("Não foi possível filtrar por salário.", e);
        }
    }

    @FXML
    private void gerarRelatorioMedias() {
        try {
            Map<String, BigDecimal> medias =
                    service.calcularMediaSalarialPorCargo();

            if (medias.isEmpty()) {
                relatorioArea.setText(
                        "Não há funcionários cadastrados para gerar o relatório."
                );
                return;
            }

            String texto = medias.entrySet()
                    .stream()
                    .map(entry -> entry.getKey()
                            + ": R$ "
                            + entry.getValue().toPlainString())
                    .collect(Collectors.joining("\n"));

            relatorioArea.setText(
                    "MÉDIA SALARIAL POR CARGO\n\n" + texto
            );

        } catch (Exception e) {
            tratarErro("Não foi possível gerar o relatório.", e);
        }
    }

    @FXML
    private void gerarRelatorioCidades() {
        try {
            Map<String, List<Funcionario>> grupos =
                    service.agruparPorCidade();

            if (grupos.isEmpty()) {
                relatorioArea.setText(
                        "Não há funcionários cadastrados para gerar o relatório."
                );
                return;
            }

            String texto = grupos.entrySet()
                    .stream()
                    .map(entry -> entry.getKey()
                            + ": "
                            + entry.getValue().size()
                            + " funcionário(s)")
                    .collect(Collectors.joining("\n"));

            relatorioArea.setText(
                    "FUNCIONÁRIOS POR CIDADE\n\n" + texto);

            relatorioArea.setText(
                    "FUNCIONÁRIOS POR CIDADE\n\n" + texto
            );

        } catch (Exception e) {
            tratarErro("Não foi possível gerar o relatório.", e);
        }
    }

    @FXML
    private void limparRelatorio() {
        relatorioArea.clear();
        status("Relatório limpo.");
    }

    @FXML
    private void limparFormulario() {
        limparCampos();
        tabela.getSelectionModel().clearSelection();
        status("Formulário limpo.");
    }

    private Funcionario lerFormulario() {
        String matricula = matriculaField.getText().trim();
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String cargo = cargoField.getText().trim();

        BigDecimal salario = converterSalario(
                salarioField.getText()
        );

        if (nascimentoPicker.getValue() == null) {
            throw new IllegalArgumentException(
                    "Informe a data de nascimento."
            );
        }

        if (contratacaoPicker.getValue() == null) {
            throw new IllegalArgumentException(
                    "Informe a data de contratação."
            );
        }

        Endereco endereco = new Endereco(
                logradouroField.getText().trim(),
                numeroField.getText().trim(),
                complementoField.getText().trim(),
                bairroField.getText().trim(),
                cidadeField.getText().trim(),
                estadoField.getText().trim(),
                cepField.getText().trim()
        );

        return new Funcionario(
                matricula,
                nome,
                cpf,
                nascimentoPicker.getValue(),
                cargo,
                salario,
                contratacaoPicker.getValue(),
                endereco
        );
    }

    private void preencherFormulario(Funcionario f) {
        matriculaField.setText(f.getMatricula());

        // Impede a alteração da matrícula durante a edição.
        matriculaField.setEditable(false);

        nomeField.setText(f.getNome());
        cpfField.setText(f.getCpf());
        cargoField.setText(f.getCargo());
        salarioField.setText(f.getSalario().toPlainString());

        nascimentoPicker.setValue(f.getDataNascimento());
        contratacaoPicker.setValue(f.getDataContratacao());

        Endereco e = f.getEndereco();

        if (e != null) {
            logradouroField.setText(e.getLogradouro());
            numeroField.setText(e.getNumero());
            complementoField.setText(e.getComplemento());
            bairroField.setText(e.getBairro());
            cidadeField.setText(e.getCidade());
            estadoField.setText(e.getEstado());
            cepField.setText(e.getCep());
        } else {
            logradouroField.clear();
            numeroField.clear();
            complementoField.clear();
            bairroField.clear();
            cidadeField.clear();
            estadoField.clear();
            cepField.clear();
        }
    }

    private void limparCampos() {
        matriculaField.clear();

        // Libera a matrícula para um novo cadastro.
        matriculaField.setEditable(true);

        nomeField.clear();
        cpfField.clear();
        cargoField.clear();
        salarioField.clear();

        nascimentoPicker.setValue(null);
        contratacaoPicker.setValue(null);

        logradouroField.clear();
        numeroField.clear();
        complementoField.clear();
        bairroField.clear();
        cidadeField.clear();
        estadoField.clear();
        cepField.clear();

        consultaField.clear();
        tabela.getSelectionModel().clearSelection();
    }

    private void atualizarTabela(List<Funcionario> funcionarios) {
        dadosTabela.setAll(funcionarios);
    }

    private BigDecimal converterSalario(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Informe o salário."
            );
        }

        String valor = texto.trim()
                .replace("R$", "")
                .replace(" ", "");

        // Aceita formatos como 1500,50 ou 1.500,50.
        if (valor.contains(",")) {
            valor = valor.replace(".", "").replace(",", ".");
        }

        try {
            BigDecimal salario = new BigDecimal(valor);

            if (salario.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "O salário não pode ser negativo."
                );
            }

            return salario;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Salário inválido. Exemplo: 1500,50."
            );
        }
    }

    private void tratarErro(String mensagem, Exception e) {
        String detalhe = e.getMessage();

        if (detalhe == null || detalhe.isBlank()) {
            detalhe = e.getClass().getSimpleName();
        }

        status(mensagem + " " + detalhe);

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Erro",
                mensagem,
                detalhe
        );
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String cabecalho,
            String conteudo) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }

    private void status(String mensagem) {
        if (statusLabel != null) {
            statusLabel.setText(mensagem);
        }
    }
}
