package ru.agma.transport.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import ru.agma.transport.models.ContactRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final JavaMailSender mailSender;

    // Получаем из переменных окружения
    private final String companyEmail;
    private final String fromEmail;

    public ContactService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:chernyugovlev@gmail.com}") String companyEmail,
            @Value("${spring.mail.from:chernyugovlev@gmail.com}") String fromEmail
    ) {
        this.mailSender = mailSender;
        this.companyEmail = companyEmail;
        this.fromEmail = fromEmail;

        logger.info("📧 Email сервис инициализирован. From: {}, To: {}", fromEmail, companyEmail);
    }

    @Async
    public void processContactRequest(ContactRequest request) {
        logger.info("🔄 Обработка заявки от: {}", request.getName());

        // Логируем заявку
        logRequest(request);

        // Отправляем email
        sendEmailNotification(request);

        logger.info("✅ Заявка обработана для: {}", request.getName());
    }

    private void logRequest(ContactRequest request) {
        logger.info("=== НОВАЯ ЗАЯВКА АГМА ===");
        logger.info("Имя: {}", request.getName());
        logger.info("Телефон: {}", request.getPhone());
        logger.info("Email: {}", request.getEmail());
        logger.info("Маршрут: {} → {}", request.getRouteFrom(), request.getRouteTo());
        logger.info("Сообщение: {}", request.getMessage());
        logger.info("==========================");
    }

    private void sendEmailNotification(ContactRequest request) {
        logger.info("📤 Отправка email на: {}", companyEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Используем переменные из конфигурации
            helper.setFrom(fromEmail);
            helper.setTo(companyEmail);
            helper.setSubject("🚚 Новая заявка с сайта АГМА: " + request.getName());

            String emailContent = buildEmailContent(request);
            helper.setText(emailContent, true);

            mailSender.send(message);
            logger.info("✅ Email успешно отправлен для: {}", request.getName());

        } catch (MessagingException e) {
            logger.error("❌ Ошибка отправки email для: {}", request.getName(), e);

            // Fallback
            sendSimpleEmail(request);
        } catch (Exception e) {
            logger.error("💥 Неожиданная ошибка: {}", e.getMessage());
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
            .message-box {
                background: #fff3cd;
                border: 1px solid #ffeaa7;
                border-radius: 8px;
                padding: 15px;
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
                request.getName(),
                request.getPhone(),
                request.getPhone(),
                request.getEmail(),
                request.getEmail(),
                request.getRouteFrom(),
                request.getRouteTo(),
                request.getMessage() != null ? request.getMessage() : "✏️ Сообщение не указано",
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        );
    }

    // Fallback метод
    private void sendSimpleEmail(ContactRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(companyEmail);
            message.setSubject("Новая заявка с сайта АГМА: " + request.getName());
            message.setText(buildSimpleTextContent(request));

            mailSender.send(message);
            logger.info("✅ Простое email уведомление отправлено");
        } catch (Exception e) {
            logger.error("❌ Ошибка отправки простого email", e);
        }
    }

    private String buildSimpleTextContent(ContactRequest request) {
        return String.format("""
    ======= НОВАЯ ЗАЯВКА АГМА =======
    
    👤 КЛИЕНТ: %s
    
    📞 ТЕЛЕФОН: %s
    📧 EMAIL: %s
    
    🚛 МАРШРУТ: %s → %s
    
    💬 СООБЩЕНИЕ: %s
    
    ⏰ ВРЕМЯ: %s
    
    ================================
    """,
                request.getName(),
                request.getPhone(),
                request.getEmail(),
                request.getRouteFrom(),
                request.getRouteTo(),
                request.getMessage() != null ? request.getMessage() : "Не указано",
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        );
    }
}