# ✈️ Agência Viagens++

![Java](https://img.shields.io/badge/Java-17-orange)
![Swing](https://img.shields.io/badge/GUI-Swing%20%7C%20FlatLaf-blue)
![SQLite](https://img.shields.io/badge/Database-SQLite-green)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36)

> Sistema desktop completo para gestão de agências de turismo, desenvolvido com foco em performance, interface moderna e integridade de dados.

---

## 📖 Sobre o Projeto

O **Agência Viagens++** é uma aplicação desktop robusta desenvolvida em **Java 17**. O projeto moderniza a interface padrão do Swing utilizando a biblioteca **FlatLaf**, proporcionando um visual limpo e profissional (Dark/Light).

O sistema gerencia todo o ciclo de vida de uma agência, desde o cadastro de clientes e pacotes até a efetivação de reservas e pagamentos, com um painel administrativo para análise de desempenho.

---

## 🚀 Funcionalidades Principais

### 🔐 Controle de Acesso
- **Sistema de Login:** Autenticação segura com diferenciação de cargos.
- **Níveis de Permissão:**
  - **Admin:** Acesso total (relatórios, logs, gestão de usuários).
  - **Atendente:** Foco em vendas, reservas e clientes.

### 📦 Gestão
- **Clientes:** Cadastro completo com histórico.
- **Pacotes de Viagem:** Gerenciamento de destinos nacionais e internacionais, incluindo datas e cotas.

### 💰 Operacional e Financeiro
- **Reservas:** Motor de reservas com verificação de disponibilidade.
- **Simulação:** Cálculo prévio de valores para orçamentos.
- **Pagamentos:** Processamento via PIX e Cartão de Crédito/Débito.
- **Cancelamentos:** Gestão de cancelamento e exclusão lógica de reservas.

### 📊 Inteligência e Auditoria
- **Dashboard:** Gráficos e indicadores de desempenho em tempo real.
- **Log de Operações:** Rastreamento automático de todas as ações críticas no sistema (quem fez, o que fez, quando fez).

---

## 🛠 Tecnologias Utilizadas

* **Linguagem:** Java 17 (LTS)
* **Interface Gráfica:** Java Swing + [FlatLaf](https://www.formdev.com/flatlaf/) 
* **Banco de Dados:** SQLite (com driver JDBC)
* **Gerenciamento de Dependências:** Apache Maven
* **Relatórios/Gráficos:** JFreeChart (Sugerido para dashboard)

---

## 🗄 Estrutura do Banco de Dados

O sistema utiliza um banco **SQLite** (`agencia_viagens.db`) que é inicializado automaticamente na primeira execução.

**Tabelas Principais:**
* `usuario`: Credenciais e níveis de acesso.
* `cliente`: Dados pessoais e contato.
* `pacote`: Destinos, valores e estoque.
* `reserva`: Vínculo entre cliente e pacote.
* `pagamento`: Registros financeiros das reservas.
* `log_operacoes`: Auditoria do sistema.

> ⚙️ **Nota Técnica:** O banco possui **Triggers** configurados para atualizar automaticamente o campo `updated_at` e **Índices** para otimização de consultas frequentes.

---

## 🔑 Credenciais Padrão

Para o primeiro acesso, utilize as contas pré-configuradas:

| Cargo | Usuário | Senha |
| :--- | :--- | :--- |
| **Administrador** | `admin` | `admin123` |
| **Atendente** | `atendente` | `atendente123` |

---

## ▶️ Como Executar

### Pré-requisitos
* Java JDK 17 instalado.
* Maven instalado e configurado no PATH.

### Passo a passo

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/marowyck/agencia-viagens.git](https://github.com/marowyck/agencia-viagens.git)
   cd agencia-viagens
   ```

2. **Compile e empacote o projeto:**
   ```bash
   mvn clean package
   ```

3. **Execute a aplicação:**
   ```bash
   java -jar target/agencia-viagens.jar
   ```

---

Autores: Maria Olívia Cassucci, Matheus Vicente, Victor Gmeiner, Gustavo Amaral


