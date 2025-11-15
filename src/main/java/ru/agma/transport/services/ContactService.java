package ru.agma.transport.services;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import ru.agma.transport.models.ContactRequest;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sendgrid.Method;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Value("${SENDGRID_API_KEY:}")
    private String sendGridApiKey;

    @Value("${EMAIL_FROM:chernyugovlev@gmail.com}")
    private String fromEmail;

    @Value("${EMAIL_TO:serch-zuganov@yandex.ru}")
    private String toEmail;

    @Async
    public void processContactRequest(ContactRequest request) {
        logger.info("🔄 Обработка заявки от: {}", request.getName());

        try {
            logRequest(request);
            boolean emailSent = sendEmailNotification(request);

            if (emailSent) {
                logger.info("✅ Заявка успешно обработана для: {}", request.getName());
            } else {
                logger.warn("⚠️ Заявка обработана, но email не отправлен для: {}", request.getName());
            }

        } catch (Exception e) {
            logger.error("💥 Критическая ошибка при обработке заявки для: {}", request.getName(), e);
        }
    }

    private void logRequest(ContactRequest request) {
        logger.info("=== НОВАЯ ЗАЯВКА АГМА ===");
        logger.info("👤 Имя: {}", request.getName());
        logger.info("📞 Телефон: {}", request.getPhone());
        logger.info("📧 Email: {}", request.getEmail());
        logger.info("🚛 Маршрут: {} → {}", request.getRouteFrom(), request.getRouteTo());
        logger.info("💬 Сообщение: {}",
                request.getMessage() != null ? request.getMessage() : "Не указано");
        logger.info("⏰ Время: {}", getCurrentTime());
        logger.info("==========================");
    }

    private boolean sendEmailNotification(ContactRequest request) {
        logger.info("📧 СОДЕРЖАНИЕ ПИСЬМА:");
        logger.info("Кому: {}", toEmail);
        logger.info("Тема: Новая заявка с сайта: {}", request.getName());
        logger.info("Клиент: {}, Телефон: {}", request.getName(), request.getPhone());
        if (sendGridApiKey == null || sendGridApiKey.trim().isEmpty()) {
            logger.error("❌ SENDGRID_API_KEY не настроен");
            return false;
        }

        logger.info("📤 Отправка email через SendGrid на: {}", toEmail);

        try {
            Email from = new Email(fromEmail, "ТК АГМА");
            Email to = new Email(toEmail);
            String subject = "🚚 Новая заявка с сайта: " + request.getName();
            String htmlContent = buildEmailContent(request);
            Content content = new Content("text/html", htmlContent);

            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey.trim());
            Request sgRequest = new Request();

            sgRequest.setMethod(Method.POST);
            sgRequest.setEndpoint("mail/send");
            sgRequest.setBody(mail.build());

            Response response = sg.api(sgRequest);
            int statusCode = response.getStatusCode();

            logger.info("📨 SendGrid ответ: статус {}", statusCode);

            if (statusCode >= 200 && statusCode < 300) {
                logger.info("✅ Email успешно отправлен через SendGrid");
                return true;
            } else {
                logger.error("❌ Ошибка SendGrid: {} - {}", statusCode, response.getBody());
                return false;
            }

        } catch (Exception e) {
            logger.error("💥 Ошибка SendGrid: {}", e.getMessage());
            return false;
        }
    }

    private String buildEmailContent(ContactRequest request) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { 
                        font-family: 'Inter', Arial, sans-serif; 
                        background: #f2f2f2;
                        margin: 0;
                        padding: 0;
                        color: #181818;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background: #ffffff;
                        border-radius: 14px;
                        overflow: hidden;
                        box-shadow: 0 5px 20px rgba(0,0,0,0.08);
                    }
                    .header {
                        background: #181818;
                        color: #ffffff;
                        padding: 25px 30px;
                        text-align: center;
                        border-bottom: 3px solid #c0c0c0;
                    }
                    .header h2 {
                        margin: 0;
                        font-size: 24px;
                        font-weight: 700;
                        letter-spacing: -0.5px;
                    }
                    .content {
                        padding: 30px;
                    }
                    .field {
                        margin-bottom: 16px;
                        padding: 12px 16px;
                        background: #f8f9fa;
                        border-radius: 8px;
                        border-left: 4px solid #c0c0c0;
                    }
                    .label {
                        font-weight: 600;
                        color: #181818;
                        display: block;
                        margin-bottom: 4px;
                        font-size: 14px;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }
                    .value {
                        color: #333;
                        font-size: 16px;
                        font-weight: 500;
                    }
                    .value a {
                        color: #181818;
                        text-decoration: none;
                        font-weight: 600;
                    }
                    .value a:hover {
                        color: #c0c0c0;
                    }
                    .route {
                        background: #e9ecef;
                        padding: 15px;
                        border-radius: 10px;
                        text-align: center;
                        font-weight: 600;
                        font-size: 18px;
                        color: #181818;
                        margin: 20px 0;
                    }
                    .footer {
                        background: #181818;
                        color: #ddd;
                        padding: 20px;
                        text-align: center;
                        font-size: 14px;
                    }
                    .footer p {
                        margin: 5px 0;
                    }
                    .logo {
                        font-size: 20px;
                        font-weight: 700;
                        color: #c0c0c0;
                        margin-bottom: 10px;
                    }
                    .time {
                        text-align: center;
                        padding: 15px;
                        background: #f8f9fa;
                        border-radius: 8px;
                        margin: 20px 0;
                        font-size: 14px;
                        color: #666;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">ТК АГМА</div>
                        <h2>🚚 Новая заявка с сайта</h2>
                    </div>
                    
                    <div class="content">
                        <div class="field">
                            <span class="label">Клиент</span>
                            <div class="value">%s</div>
                        </div>
                        
                        <div class="field">
                            <span class="label">Контакты</span>
                            <div class="value">
                                📞 <a href="tel:%s">%s</a><br>
                                📧 <a href="mailto:%s">%s</a>
                            </div>
                        </div>
                        
                        <div class="route">
                            📍 %s → %s
                        </div>
                        
                        <div class="field">
                            <span class="label">Детали заявки</span>
                            <div class="value">%s</div>
                        </div>
                    </div>
                    
                    <div class="time">
                        <span class="label">Время получения</span><br>
                        %s
                    </div>
                    
                    <div class="footer">
                        <p>📧 Автоматическое уведомление с agma-transport.ru</p>
                        <p>Не отвечайте на это письмо</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                escapeHtml(request.getName()),
                request.getPhone(),
                request.getPhone(),
                request.getEmail(),
                request.getEmail(),
                escapeHtml(request.getRouteFrom()),
                escapeHtml(request.getRouteTo()),
                escapeHtml(request.getMessage() != null ? request.getMessage() : "✏️ Сообщение не указано"),
                getCurrentTime()
        );
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}