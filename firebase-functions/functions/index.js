const { onDocumentCreated, onDocumentUpdated } = require("firebase-functions/v2/firestore");
const { getMessaging } = require("firebase-admin/messaging");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * Robô 1 (O Carteiro de Notificação Push - opcional)
 * Acionado quando uma nova consulta é criada na coleção 'appointments'.
 * Envia uma notificação PUSH para o app do dentista.
 */
exports.sendAppointmentNotification = onDocumentCreated("appointments/{appointmentId}", async (event) => {
  const newAppointment = event.data.data();
  console.log("Robô 1: Nova consulta detectada para:", newAppointment.patientName);

  const payload = {
    notification: {
      title: "Nova Consulta Agendada!",
      body: `Paciente: ${newAppointment.patientName} em ${newAppointment.date} às ${newAppointment.time}.`,
      sound: "default",
    },
  };

  const topic = "all";

  try {
    const response = await getMessaging().sendToTopic(topic, payload);
    console.log("Robô 1: Notificação push enviada com sucesso:", response);
  } catch (error) {
    console.error("Robô 1: Erro ao enviar notificação push:", error);
  }
});


/**
 * Robô 2 (O Detetive de E-mails)
 * Acionado quando um documento na coleção 'mail' é ATUALIZADO.
 * Verifica se o envio de e-mail deu erro e atualiza o agendamento correspondente.
 */
exports.updateAppointmentOnEmailStatus = onDocumentUpdated("mail/{mailId}", async (event) => {
  // Pega os dados do "documento-carta" DEPOIS da atualização.
  const mailData = event.data.after.data();

  // Verifica se o campo 'delivery' existe e se o 'state' é 'ERROR'.
  if (mailData.delivery && mailData.delivery.state === 'ERROR') {
    const patientEmail = mailData.to;
    console.log(`Robô 2: Erro de e-mail detectado para: ${patientEmail}`);
    console.log(`Motivo: ${mailData.delivery.error}`);

    const db = admin.firestore();
    const appointmentsRef = db.collection('appointments');

    // Procura na coleção 'appointments' por um agendamento com aquele e-mail.
    // Usamos um limite de 1 para o caso de haver múltiplos, pegamos o mais recente.
    const querySnapshot = await appointmentsRef.where('patientEmail', '==', patientEmail).limit(1).get();

    if (querySnapshot.empty) {
      console.log(`Robô 2: Nenhum agendamento encontrado para o e-mail: ${patientEmail}`);
      return;
    }

    // Atualiza o campo 'emailStatus' do agendamento encontrado.
    querySnapshot.forEach(doc => {
      console.log(`Robô 2: Atualizando o agendamento ${doc.id} para status de e-mail 'ERROR'`);
      // Usamos .update() para modificar apenas um campo.
      doc.ref.update({ emailStatus: 'ERROR' });
    });
  }

  return null; // Finaliza a função.
});