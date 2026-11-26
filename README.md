# Programação Paralela e Distribuída em Java

Neste repositório, usamos a ordenação de vetores para aprender a dividir trabalho entre threads e entre computadores. Vamos praticar como executar tarefas ao mesmo tempo, esperar que elas terminem e trocar mensagens entre um cliente e seus servidores.

## Atividade 1 — Ordenação paralela

O programa divide um vetor em partes e usa várias threads para ordená-las na mesma máquina. Depois, combina as partes ordenadas até formar o vetor final. A pasta também tem uma versão sequencial para comparação.

[Código da atividade 1](atividade1/)

## Atividade 2 — Ordenação distribuída

A ideia é distribuir as partes do vetor entre vários servidores. Cada servidor usa threads para ordenar a parte que recebe e responde ao cliente com o resultado quando solicitado.

A atividade está em andamento. Já temos o atendimento de um cliente por vez e as mensagens de ordenação, junção de vetores e consulta de resultado. A inicialização do servidor e o programa cliente ficam para as próximas etapas.

[Código da atividade 2](atividade2/) · [Enunciado](enunciado_atividade.jpeg)
