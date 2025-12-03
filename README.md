# MotivaAI 🌟

**MotivaAI** é um aplicativo Android que auxilia pessoas em processo de recuperação de vícios, oferecendo suporte através de notificações motivacionais personalizadas geradas por IA, diário emocional e acompanhamento do progresso de abstinência.

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Arquitetura](#arquitetura)
- [Contribuindo](#contribuindo)
- [Licença](#licença)

## 🎯 Sobre o Projeto

O MotivaAI foi desenvolvido para oferecer suporte emocional e motivacional para pessoas que estão em processo de recuperação de diversos tipos de vícios. O aplicativo utiliza inteligência artificial (Google Gemini) para gerar mensagens personalizadas baseadas no perfil e necessidades do usuário.

### Principais Diferenciais

- 🤖 **IA Personalizada**: Mensagens motivacionais adaptadas ao perfil do usuário
- 📝 **Diário Emocional**: Registro e acompanhamento do estado emocional
- 📊 **Estatísticas de Progresso**: Visualização de dias de abstinência e milestones
- 🔔 **Notificações Inteligentes**: Lembretes motivacionais em horários configuráveis
- 📄 **Exportação PDF**: Geração de relatórios do diário para compartilhamento

## ✨ Funcionalidades

### RF01 - Contagem de Dias de Abstinência
- Registro da data de início da recuperação
- Cálculo automático de dias, semanas e meses de abstinência
- Visualização de marcos importantes (7 dias, 30 dias, 90 dias, etc.)

### RF02 - Notificações Motivacionais Agendadas
- Notificações diárias em horário configurável
- Mensagens personalizadas geradas por IA
- Configuração de horário preferencial pelo usuário

### RF04 - Personalização da IA
- Seleção do tipo de vício (álcool, tabaco, drogas, etc.)
- Escolha do tom das mensagens (encorajador, empático, direto)
- Geração de conteúdo adaptado ao perfil do usuário

### RF05 - Diário Emocional
- Registro de pensamentos e sentimentos
- Descrição de desafios enfrentados
- Histórico completo de entradas

### RF06 - Onboarding
- Processo de configuração inicial guiado
- Coleta de informações essenciais (data de início, tipo de vício, preferências)
- Interface amigável e intuitiva

### RF07 - Exportação para PDF
- Geração de relatório completo do diário
- Compatibilidade com Android 10+ (MediaStore)
- Opção de compartilhamento e backup

## 🛠 Tecnologias Utilizadas

### Core
- **Kotlin** - Linguagem de programação principal
- **Jetpack Compose** - Framework moderno de UI
- **Material Design 3** - Design system

### Android Jetpack
- **Room Database** - Persistência de dados local
- **WorkManager** - Agendamento de tarefas em background
- **ViewModel & LiveData** - Gerenciamento de estado e ciclo de vida
- **Navigation Component** - Navegação entre telas

### IA e APIs
- **Google AI (Gemini)** - Geração de mensagens motivacionais personalizadas

### Processamento de Dados
- **iText7** - Geração de documentos PDF
- **KSP** - Processamento de anotações Kotlin

### Build Tools
- **Gradle (Kotlin DSL)** - Sistema de build
- **Android Gradle Plugin** - Compilação Android

## 📦 Requisitos

### Ambiente de Desenvolvimento
- Android Studio Hedgehog | 2023.1.1 ou superior
- JDK 8 ou superior
- Gradle 8.0+

### Requisitos do Dispositivo
- Android 6.0 (API 23) ou superior
- Conexão com internet (para geração de mensagens IA)
- Permissões: Internet, Notificações, Armazenamento (opcional)

## 🚀 Instalação

### 1. Clone o Repositório

```bash
git clone https://github.com/Davizera-cmd/MotivaAI.git
cd MotivaAI
```

### 2. Configure a API Key do Google Gemini

1. Obtenha uma API key em [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Crie um arquivo `local.properties` na raiz do projeto (se não existir)
3. Adicione a seguinte linha:

```properties
GEMINI_API_KEY=sua_api_key_aqui
```

### 3. Sincronize e Execute

1. Abra o projeto no Android Studio
2. Aguarde a sincronização do Gradle
3. Conecte um dispositivo ou inicie um emulador
4. Clique em **Run** (▶️) ou pressione `Shift + F10`

## 📁 Estrutura do Projeto

```
MotivaAI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/motivaai/
│   │   │   │   ├── data/           # Camada de dados (Room, Repository)
│   │   │   │   ├── services/       # Serviços (IA, Notificações)
│   │   │   │   ├── ui/             # Camada de UI (Compose, ViewModels)
│   │   │   │   │   ├── screens/    # Telas do aplicativo
│   │   │   │   │   └── theme/      # Temas e estilos
│   │   │   │   ├── utils/          # Utilitários (PDF, Notificações)
│   │   │   │   ├── workers/        # Workers (WorkManager)
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── MotivaAiApplication.kt
│   │   │   ├── res/                # Recursos (layouts, strings, drawables)
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/            # Testes instrumentados
│   │   └── test/                   # Testes unitários
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🏗 Arquitetura

O projeto segue os princípios da **Arquitetura MVVM (Model-View-ViewModel)** recomendada pelo Google:

```
┌─────────────────┐
│  UI (Compose)   │  ← Telas e componentes visuais
└────────┬────────┘
         │
┌────────▼────────┐
│   ViewModel     │  ← Lógica de apresentação e estado
└────────┬────────┘
         │
┌────────▼────────┐
│   Repository    │  ← Camada de abstração de dados
└────────┬────────┘
         │
    ┌────▼────┐─────────┐
    │  Room   │ Gemini  │  ← Fontes de dados
    └─────────┴─────────┘
```

### Componentes Principais

- **MainActivity**: Activity principal que hospeda a navegação Compose
- **MotivaAiApplication**: Classe Application para inicialização global
- **MotivaAiRepository**: Gerenciamento centralizado de dados
- **MainViewModel**: ViewModel principal com estado da aplicação
- **MotivationalWorker**: Worker para agendamento de notificações
- **GeminiService**: Integração com a API do Google Gemini

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

### Diretrizes de Código

- Siga as convenções de código Kotlin
- Adicione testes para novas funcionalidades
- Documente funções e classes públicas
- Mantenha commits claros e descritivos

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 📞 Contato

**Desenvolvedor**: Davizera  
**GitHub**: [@Davizera-cmd](https://github.com/Davizera-cmd)

---

<div align="center">
  <p>Desenvolvido com ❤️ e Jetpack Compose</p>
  <p>🌟 Se este projeto foi útil, considere dar uma estrela!</p>
</div>
