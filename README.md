# P1 Funcionário — JavaFX

Sistema de cadastro de funcionários feito em JavaFX/Maven, conforme os requisitos da atividade.

## Funcionalidades
- Cadastro de funcionário com endereço composto.
- Validação de matrícula (6 dígitos), CPF (dígitos verificadores), idade mínima de 18 anos, salário positivo e CEP com 8 dígitos.
- Consulta por matrícula, exclusão por seleção na tabela ou matrícula e listagem.
- Persistência em arquivo separado por `;`, carregado na inicialização e atualizado a cada inclusão/exclusão.
- Relatórios com Stream API: filtro por cargo, filtro por faixa salarial, média salarial por cargo (`groupingBy`, `mapping`, `reducing`) e agrupamento por cidade.
- Tratamento básico de erros de entrada e de leitura/escrita.

## Como executar
1. Instale JDK 21 (ou superior compatível com Maven) e Maven.
2. Abra a pasta do projeto no IntelliJ como projeto Maven.
3. Aguarde o Maven baixar JavaFX.
4. Execute `org.p1_funcionario.Launcher`, ou no terminal use `mvn clean javafx:run`.
5. O arquivo `funcionarios_cadastrados.txt` será criado na pasta de execução.

## Decisões de implementação
- `Funcionario` contém os dados pessoais e uma instância de `Endereco`, aplicando composição.
- `BigDecimal` é usado para salário para evitar imprecisão típica de `double`.
- `LocalDate` representa datas.
- Os campos de texto são codificados em Base64 ao gravar o CSV, para evitar que caracteres como `;` dentro de um texto quebrem a separação dos campos. O arquivo continua usando `;` como delimitador. Não edite os campos manualmente.
- Os relatórios usam Streams e exibem resultados na interface.

## Observação
A atividade pede que o código seja entregue no GitHub e em ZIP. Este ZIP contém o código-fonte e este README; não inclui dependências baixadas pelo Maven.
