DentalFlow

🦷DentalFlow é um aplicativo Android moderno para gerenciamento de agendamentos odontológicos, construído com as melhores práticas e tecnologias do ecossistema Android.

Ele oferece uma interface fluida e intuitiva para dentistas, enquanto automatiza a comunicação com pacientes através de um backend robusto no Firebase.

✨ Funcionalidades

📱 Aplicativo Android

•🔒 Autenticação Segura: Fluxo completo de registro e login para dentistas, utilizando Firebase Authentication.

•🗓️ Gerenciamento de Agenda: Visualize, crie, edite e exclua agendamentos em uma interface limpa e reativa.

•🚀 UI Moderna e Reativa: Interface construída 100% com Jetpack Compose, garantindo uma experiência de usuário fluida e moderna.

•🔍 Busca em Tempo Real: Filtre a lista de agendamentos instantaneamente para encontrar pacientes com facilidade.

•🔄 Pull-to-Refresh: Atualize a lista de agendamentos com um simples gesto, uma funcionalidade esperada em apps modernos.

•✨ Feedback Visual Inteligente:

  ◦Skeleton Loading: Exibe uma animação de "esqueleto" (shimmer) enquanto os dados carregam, melhorando a percepção de performance.
  
  ◦Alerta de Erro de E-mail: O card do agendamento muda de cor e exibe um ícone de alerta se o e-mail de confirmação para o paciente falhar.
  
•🔔 Notificações Push: Dentistas recebem notificações em tempo real sobre novos agendamentos, mesmo com o app em segundo plano, via Firebase Cloud Messaging.


☁️ Backend com Firebase

•🤖 sendAppointmentNotification (Cloud Function): Uma função serverless que é acionada sempre que um novo agendamento é criado e envia uma notificação push para o dentista.

•🕵️‍♂️ updateAppointmentOnEmailStatus (Cloud Function): Uma função inteligente que monitora o status de entrega de e-mails. Se um e-mail de confirmação falhar, ela atualiza o documento do agendamento no Firestore, ativando o alerta visual no app.

•Firestore: Utilizado como banco de dados NoSQL em tempo real para armazenar todos os dados de dentistas e agendamentos.

•Firebase Authentication: Gerencia todo o ciclo de vida de autenticação dos usuários.


💻 Tecnologias Utilizadas

•Linguagem: Kotlin

•UI: Jetpack Compose

•Arquitetura: MVVM (Model-View-ViewModel)

•Assincronia: Kotlin Coroutines & Flow

•Navegação: Jetpack Navigation for Compose•Backend: Firebase (Authentication, Firestore, Cloud Functions, Cloud Messaging)


📂 Estrutura do ProjetoO código é bem organizado, seguindo as melhores práticas de arquitetura limpa, com uma separação clara de responsabilidades.


.
├── 📱 app/                  # Código-fonte do aplicativo Android.

│   ├── data/               # Camada de dados (Models e Repositórios).

│   ├── navigation/         # Lógica de navegação (NavHost).

│   ├── services/# Serviços em segundo plano (Firebase Messaging).

│   ├── ui/                 # Camada de UI (Telas, Componentes e Temas).

│   └── viewmodel/          # Camada de lógica de negócios (ViewModels).

│
└── ☁️ firebase-functions/   # Código backend para as Firebase Cloud Functions (Node.js).

  └── functions/
  
   └── index.js        # Lógica das funções serverless.
      
