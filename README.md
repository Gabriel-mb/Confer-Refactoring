# 🏭 CONFER - Sistema de Gestão de Ferramentaria Industrial - EM DESENVOLVIMENTO

![Java](https://img.shields.io/badge/Java-17+-blue?logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-19+-orange?logo=openjdk)
![SpringBoot](https://img.shields.io/badge/Spring_Boot-3.1.6-brightgreen?logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blueviolet?logo=mysql)
![License](https://img.shields.io/badge/license-MIT-green)

Sistema completo para gestão de ferramentas, EPIs e consumíveis em ambientes industriais com controle de patrimônio e empréstimos.

## 📌 Funcionalidades Principais

### 🛡️ Módulo de EPIs
- Controle de fichas por funcionário

### 🔧 Gestão de Ferramentas
- Registro por código de patrimônio
- Histórico completo de empréstimos
- Status de disponibilidade em tempo real

### 📦 Controle de Consumíveis
- Registro de retiradas
- Controle de estoque

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- MySQL 8.0+
- Maven 3.8+

```bash
# Clonar repositório
git clone https://github.com/Gabriel-mb/Confer-Refactoring.git

# Configurar banco de dados
Edite o arquivo src/main/resources/application.properties

# Compilar e executar
mvn spring-boot:run
```
