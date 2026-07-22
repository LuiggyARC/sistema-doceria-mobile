# ⚡ PaiDégua HUB

<div align="center">

# 📱 Plataforma Android de Gestão Empresarial 360°

Sistema comercial robusto focado em pequenos e médios negócios:

**vendas • estoque • dashboards • relatórios • funcionários • pagamentos • segurança • nuvem**

</div>

---

# 📖 Sobre o Projeto

O **PaiDégua HUB** evoluiu de um aplicativo de nicho para uma plataforma de gestão completa, operando sob o modelo **SaaS (Software as a Service) Multiempresa**. 

O sistema oferece controle absoluto sobre a operação comercial, desde o registro do primeiro produto até o backup de segurança em nuvem, garantindo que o empreendedor tenha clareza total sobre seu lucro real.

---

# 🛡️ Segurança e Proteção de Dados (Últimas Atualizações)

Implementamos uma camada de segurança de nível bancário para proteger as informações comerciais:

- **🔒 Criptografia BCrypt**: Senhas de usuários e empresas não são mais lidas por humanos. Utilizamos hashing BCrypt para garantir que as credenciais sejam impossíveis de reverter.
- **🔐 EncryptedSharedPreferences**: Chaves sensíveis (como a Public Key do Mercado Pago) são criptografadas em nível de hardware pelo Android.
- **☁️ Sincronização Inteligente**: Novo sistema de "Nuvem de Dados" que monitora o que foi sincronizado.
    - ✅ **CloudDone**: Garantido no servidor.
    - ☁️ **CloudOff**: Pendente de backup local.
- **🛡️ Ofuscação de Código**: Integração total com **ProGuard/R8**, dificultando engenharia reversa e protegendo a propriedade intelectual da plataforma.

---

# ✨ Funcionalidades Principais

## 🏢 Suporte Multiempresa Real
Isolamento total de dados via `companyId`. Cada empresa opera em seu próprio ecossistema:
- Produtos, Vendas, Funcionários e Despesas são 100% independentes entre contas.

## 💳 Gestão de Pagamentos Integrada
- Suporte nativo para **Mercado Pago (Point)**, **Stone** e **Caixa (Azulzinha)**.
- **Trava de Segurança Bluetooth**: O app verifica obrigatoriamente se a maquininha está ativa e conectada antes de iniciar a venda no cartão.

## 👨‍💼 Gestão de Funcionários e Equipe
- Cadastro e controle de colaboradores.
- Estrutura preparada para futuras permissões de acesso (Dono vs. Operador).

## 📊 Inteligência de Negócio (BI)
- **Dashboards em Tempo Real**: Faturamento bruto, lucro líquido e progresso de metas mensais.
- **Relatórios Automatizados**: Gráficos de categorias, formas de pagamento e exportação em **PDF**.

---

# ⚙️ Estrutura Técnica

- **Linguagem**: Kotlin (Modern Android)
- **UI**: Jetpack Compose com Material Design 3
- **Banco de Dados**: Room Database (Versão 6) com criptografia preparada
- **Arquitetura**: MVVM (Model-View-ViewModel) + Repositories
- **Integração**: Retrofit para sincronização com API Django
- **Navegação**: Navigation Drawer (Menu Lateral) moderno com foco em agilidade

---

# 🚀 CI/CD (GitHub Actions)

O projeto conta com um pipeline de automação completo:
- **Build Automático**: Validação de cada alteração enviada.
- **Análise Estática (Lint)**: Varredura em busca de bugs e riscos de performance.
- **Explicação do Processo**: O CI agora detalha cada etapa do teste para a equipe.
- **Relatório de Riscos**: O GitHub gera um alerta automático sobre riscos de Hardware, Dados e Privacidade (LGPD).

---

# 📦 Estrutura do App

```text
PaiDégua HUB
├── 🔐 Autenticação Segura (BCrypt)
├── 🏢 Gestão Multiempresa
├── 🛒 PDV (Ponto de Venda)
├── 📜 Histórico de Vendas (Sync Status)
├── 📦 Estoque Inteligente (Alertas)
├── 👨‍💼 Gestão de Funcionários
├── 💸 Controle Financeiro & Despesas
├── 📊 Relatórios & Exportação PDF
├── 💳 Gateway de Pagamentos (Bluetooth)
└── ☁️ Backup & Sync em Nuvem
```

---

# 👨‍💻 Autor

Desenvolvido por **Luiggy Alberto**

Foco em soluções comerciais escaláveis e segurança mobile.

GitHub: [LuiggyARC](https://github.com/LuiggyARC)

---

<div align="center">
  <b>PaiDégua HUB: Onde a inteligência encontra o seu negócio.</b>
</div>
