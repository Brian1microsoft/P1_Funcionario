# Relatório breve de implementação

O projeto implementa uma aplicação JavaFX para cadastro e gerenciamento de funcionários. A interface possui formulário de dados pessoais e endereço, tabela de listagem, consulta e exclusão, além de área de exibição de relatórios.

O modelo de domínio é formado por `Funcionario` e `Endereco`. A relação entre eles é de composição: cada funcionário possui um endereço. O salário é armazenado em `BigDecimal`, e as datas em `LocalDate`.

A persistência é realizada no arquivo `funcionarios_cadastrados.txt`, com campos separados por ponto-e-vírgula. Os registros são carregados quando a aplicação inicia e regravados após operações de cadastro e exclusão.

As validações implementadas incluem matrícula numérica de seis dígitos e única, nome mínimo de três caracteres, CPF com verificação dos dígitos, idade mínima de 18 anos, salário positivo, datas não futuras e CEP com oito dígitos.

Os relatórios usam Stream API para filtrar por cargo e faixa salarial, calcular média salarial agrupada por cargo e agrupar funcionários por cidade. Foram usados `filter`, `map`, `collect`, `groupingBy`, `mapping` e `reducing`.
