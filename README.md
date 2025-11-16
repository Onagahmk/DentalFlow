DentalFlow 

🦷DentalFlow é um aplicativo Android moderno para gerenciamento de consultas odontológicas. 
Ele fornece aos dentistas uma interface intuitiva para gerenciar seus horários e garante que os pacientes sejam mantidos informados por meio de e-mails automatizados. 
O backend é desenvolvido com Firebase, fornecendo sincronização de dados em tempo real, autenticação e funções serverless.
✨ Funcionalidades

📱 Aplicativo Android
•🔒 Autenticação Segura: Dentistas podem se registrar e fazer login em suas contas com segurança.
•🗓️ Gerenciamento de Agenda: Visualize uma lista clara e organizada de todas as consultas.
•➕ Criar Consultas: Adicione facilmente novas consultas para pacientes.
•✏️ Editar Consultas: Modifique os detalhes das consultas existentes conforme necessário.
•🔔 Notificações Push: Receba alertas instantâneos para consultas recém-agendadas.

☁️ Backend com Firebase
•🤖 Notificações Push Automatizadas: Uma Cloud Function (sendAppointmentNotification) envia automaticamente uma notificação para o dispositivo do dentista quando uma nova consulta é criada.
•🕵️‍♂️ Rastreamento de Status de E-mail: Uma Cloud Function (updateAppointmentOnEmailStatus) monitora o status de entrega do e-mail. Se um e-mail para um paciente falhar, ele atualiza o registro da consulta para refletir o erro, permitindo um acompanhamento rápido.

💻 Tecnologias Utilizadas
•UI: Jetpack Compose para uma UI moderna e declarativa.•Linguagem: Kotlin (incluindo Coroutines & Flow).
•Arquitetura: MVVM (Model-View-ViewModel).•Navegação: Jetpack Navigation para as transições de tela.
•Backend: Firebase◦Authentication: Para gerenciamento de usuários.
◦Firestore: Como banco de dados NoSQL para armazenar as consultas.
◦Cloud Functions: Para automação no lado do servidor.◦Cloud Messaging (FCM): Para as notificações push.

📂 Estrutura do ProjetoO repositório está organizado em duas partes principais:.

├── 📱 app/                  # Contém todo o código-fonte do aplicativo Android.

│   ├── src/main/java/

│   │   └── com/rodrigodecastro/dentalflow/

│   │       ├── navigation/   # Grafo de navegação e lógica.

│   │       ├── ui/           # Telas da UI (Composable functions).

│   │       ├── viewmodel/    # ViewModels para a lógica de negócios.

│   │       └── MainActivity.kt

│
└── ☁️ firebase-functions/   # Contém o código backend (Node.js) para as Firebase Cloud Functions.

    └── functions/
    
        └── index.js        # Arquivo principal com a lógica serverless.
        
        ☁️ Detalhes das Firebase Cloud Functions1.sendAppointmentNotification
        ◦Gatilho: Quando um novo documento é criado na coleção appointments.
        ◦Ação: Envia uma notificação push para o tópico all (assinado pelo app do dentista) com os detalhes da nova consulta.2.updateAppointmentOnEmailStatus
        ◦Gatilho: Quando um documento é atualizado na coleção mail (usada por uma extensão de envio de e-mails como a do SendGrid).
        ◦Ação: Verifica se o campo delivery.state do e-mail é ERROR. Se for, encontra a consulta correspondente na coleção appointments (pelo e-mail do paciente) e atualiza o campo emailStatus para ERROR.
