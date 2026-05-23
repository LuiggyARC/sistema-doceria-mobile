# ⚡ PaiDégua Hub

<div align="center">

# 📱 MVP Android de Gestão Empresarial

Sistema Android desenvolvido para pequenos negócios com foco em:

**vendas • estoque • dashboards • relatórios • funcionários • pagamentos • gestão operacional**

</div>

---

# 📖 Sobre o Projeto

O **PaiDégua Hub** é um aplicativo Android criado para ajudar pequenos empreendedores a organizarem seus negócios de forma simples, moderna e acessível.

O projeto começou como um aplicativo de gestão para docerias, mas evoluiu para um sistema empresarial mais robusto preparado para:

- múltiplas empresas;
- autenticação real;
- dashboards financeiros;
- controle operacional;
- gerenciamento de funcionários;
- histórico de vendas;
- backup em nuvem;
- integração com pagamentos;
- futura sincronização online.

A proposta principal do sistema é funcionar como:

> “O gerente digital do pequeno empreendedor.”

---

# 🎯 Problema Resolvido

Muitos pequenos negócios:

- controlam vendas manualmente;
- misturam dinheiro pessoal e empresarial;
- não possuem visão do lucro real;
- não conseguem acompanhar estoque;
- não possuem relatórios financeiros;
- não têm controle operacional;
- dependem totalmente da memória do proprietário.

O PaiDégua Hub busca resolver esse cenário através de uma solução simples e visual.

---

# ✨ Diferenciais do Projeto

## 🎨 Personalização Inteligente

O sistema adapta automaticamente sua identidade visual com base na logo cadastrada pela empresa.

Ao carregar a logo:

- as cores principais do sistema mudam automaticamente;
- a interface se adapta à identidade visual da empresa;
- o aplicativo se aproxima de soluções white-label modernas.

---

## 📲 Interface Mobile Moderna

O aplicativo foi desenvolvido utilizando:

- Kotlin;
- Jetpack Compose;
- Material Design 3;
- arquitetura MVVM;
- Navigation Compose.

Mudanças recentes:

- navegação lateral moderna;
- integração total entre módulos;
- foco em experiência mobile;
- fluxo mais rápido para operação diária.

---

# 🔐 Autenticação e Registro

O sistema agora possui autenticação real.

## Recursos implementados

- login com e-mail e senha;
- cadastro de novas empresas;
- validação de credenciais;
- gerenciamento de sessão;
- persistência de login;
- estrutura preparada para sincronização futura em nuvem.

O usuário pode criar sua empresa diretamente pelo aplicativo.

---

# 🏢 Suporte Multiempresa Real

Todas as funcionalidades agora operam utilizando:

- `companyId`

Isso garante isolamento total entre empresas.

## Recursos afetados

- estoque;
- vendas;
- funcionários;
- despesas;
- fechamentos;
- relatórios.

Na prática:

- uma empresa não visualiza os dados da outra;
- cada login possui ambiente isolado;
- produtos, vendas e funcionários são independentes.

---

## 🔄 Filtragem Automática

Todos os ViewModels realizam filtragem automática utilizando a empresa logada.

Isso transforma o sistema em uma estrutura próxima de plataformas SaaS multiempresa.

---

## 🔐 SessionManager

Foi criado um gerenciador de sessão responsável por:

- manter login ativo;
- controlar usuário autenticado;
- persistir sessão localmente;
- preparar futura sincronização online.

---

# 🛒 Gestão de Vendas

- registro rápido de vendas;
- formas de pagamento;
- baixa automática no estoque;
- histórico operacional;
- fluxo preparado para integração de pagamentos.

---

# 📜 Histórico de Vendas

Foi adicionada uma nova tela:

## Histórico Vendas

Recursos:

- visualização de vendas anteriores;
- histórico operacional completo;
- consulta de movimentações;
- acompanhamento financeiro;
- análise de operações passadas.

A nova tela está integrada ao menu lateral.

---

# 👨‍💼 Gestão de Funcionários

Foi adicionada uma nova área:

## Funcionários

Preparada para:

- cadastro de funcionários;
- gerenciamento operacional;
- organização da equipe;
- expansão futura de permissões e acessos.

A funcionalidade já está integrada ao Drawer/Menu lateral.

---

# 📊 Dashboard Inteligente

O dashboard centraliza os principais indicadores do negócio:

- faturamento;
- lucro;
- vendas do dia;
- estoque baixo;
- metas;
- status do caixa;
- resumo operacional.

---

# 📦 Controle de Estoque

- cadastro de produtos;
- controle de estoque mínimo;
- atualização automática;
- alertas de estoque baixo;
- organização operacional.

---

# 💸 Controle Financeiro

## Despesas

- registro de despesas;
- categorias financeiras;
- histórico operacional;
- controle diário.

## Relatórios

- relatórios mensais;
- histórico financeiro;
- exportação PDF;
- resumo diário;
- taxas de cartão;
- resultado financeiro.

---

# ☁️ Backup em Nuvem

Foi adicionada uma nova seção:

## Backup em Nuvem

Localizada em:

1. Menu lateral
2. Ajustes
3. Backup em Nuvem

Objetivos:

- preparar sincronização online;
- segurança de dados;
- recuperação futura de informações;
- estrutura para ambiente SaaS.

---

# 💳 Integração de Pagamentos

Estrutura preparada para:

- Mercado Pago;
- Stone;
- Caixa;
- conexão Bluetooth;
- configuração de Public Key;
- fluxo de integração futura.

---

# 👨‍💼 Painel Administrativo

Foi adicionada uma nova tela administrativa.

## Painel Admin

Recursos:

- visualizar empresas cadastradas;
- ativar/desativar empresas;
- gerenciamento operacional;
- controle administrativo.

Acesso restrito para administradores.

---

# 🛡️ Segurança e Estrutura

## 🔒 Banco de Dados

O banco foi atualizado para:

- versão `v5`

Permitindo:

- suporte multiempresa;
- novas tabelas;
- estrutura escalável;
- futuras sincronizações.

---

## 🔐 Proteção de Código

Foi ativado:

- ProGuard;
- R8;
- regras de ofuscação.

Objetivos:

- dificultar engenharia reversa;
- proteger lógica de negócio;
- preparar publicação comercial.

---

## 🚀 Build de Release

O projeto agora possui:

- `minifyEnabled`;
- `shrinkResources`;
- otimizações de release.

Preparando o aplicativo para:

- deploy;
- publicação;
- distribuição comercial.

---

# 📲 Navegação do Aplicativo

## Como acessar as novas funcionalidades

1. Abra o Menu Lateral (Drawer) no canto esquerdo.
2. Acesse:
   - Histórico Vendas
   - Funcionários
3. Vá em:
   - Ajustes
   - Backup em Nuvem

Todas as funcionalidades já estão integradas à navegação lateral.

---

# 🧠 Regras de Negócio

- vendas reduzem estoque automaticamente;
- dados são isolados por empresa;
- sessões permanecem persistidas;
- despesas são categorizadas;
- relatórios financeiros são consolidados;
- histórico operacional é salvo localmente.

---

# 🛠️ Tecnologias Utilizadas

## 📱 Mobile

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3

## 🗄️ Banco de Dados

- Room Database
- SQLite
- Migrações Room

## 🌐 Integração

- Retrofit
- OkHttp
- Bluetooth Android

## 🏗️ Arquitetura

- MVVM
- ViewModels
- Repositories
- Navigation Compose
- SessionManager

## 🔒 Segurança

- ProGuard
- R8
- Release Optimization

---

# 📦 Estrutura Geral

```text
PaiDégua Hub
├── Login e Registro
├── Multiempresa
├── Dashboard
├── Histórico de Vendas
├── Funcionários
├── Vendas
├── Estoque
├── Despesas
├── Relatórios
├── Backup em Nuvem
├── Painel Admin
├── Configurações
├── SessionManager
├── Room Database
└── Integração de Pagamentos
```

---

# 🚀 Roadmap

## Próximas Melhorias

- sincronização online;
- autenticação em nuvem;
- multiusuário avançado;
- controle de permissões;
- analytics avançado;
- painel web administrativo;
- versão SaaS comercial.

---

# 💼 Aplicação Comercial

O sistema pode ser utilizado em:

- docerias;
- cafeterias;
- lanchonetes;
- pequenos mercados;
- lojas locais;
- negócios familiares;
- pequenos comércios.

---

# 🔒 Disponibilidade do Código

Este repositório possui foco educacional e de portfólio.

O projeto demonstra:

- arquitetura Android moderna;
- autenticação real;
- multiempresa;
- dashboards;
- relatórios;
- integração de pagamentos;
- segurança;
- preparação para deploy comercial.

---

# 👨‍💻 Autor

Desenvolvido por **Luiggy Alberto**

Foco em:

- Android;
- automação;
- análise de dados;
- sistemas comerciais;
- soluções para pequenos negócios.

GitHub:

https://github.com/LuiggyARC

---

# ⭐ Considerações Finais

O PaiDégua Hub evoluiu de um simples aplicativo de controle para um MVP Android empresarial com:

- autenticação real;
- suporte multiempresa;
- dashboards;
- relatórios;
- gestão de funcionários;
- histórico operacional;
- segurança;
- integração de pagamentos;
- preparação SaaS;
- arquitetura moderna.

O projeto hoje já demonstra características reais de um produto comercial em evolução.