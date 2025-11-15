package ru.agma.transport.controllers;

import ru.agma.transport.models.ContactRequest;
import ru.agma.transport.services.ContactService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final ContactService contactService;

    public HomeController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Главная страница
    @GetMapping("/")
    public String home(Model model) {
        // ВАЖНО: Всегда создаем новый объект для формы
        if (!model.containsAttribute("contactRequest")) {
            model.addAttribute("contactRequest", new ContactRequest());
        }

        model.addAttribute("pageTitle", "АГМА - Грузоперевозки по России");
        model.addAttribute("companyName", "АГМА");
        return "index";
    }

    @GetMapping("/russia")
    public String russia(Model model) {
        model.addAttribute("pageTitle", "Перевозки по России | АГМА");
        return "russia";
    }

    @GetMapping("/import")
    public String importPage(Model model) {
        model.addAttribute("pageTitle", "Импортные перевозки | АГМА");
        return "import";
    }

    @GetMapping("/export")
    public String export(Model model) {
        model.addAttribute("pageTitle", "Экспортные перевозки | АГМА");
        return "export";
    }

    @GetMapping("/crossdock")
    public String crossDocking(Model model) {
        model.addAttribute("pageTitle", "Кросс-докинг | АГМА");
        return "crossdock";
    }

    @GetMapping("/containers")
    public String containers(Model model) {
        model.addAttribute("pageTitle", "КТК по УрФО | АГМА");
        return "containers";
    }

    @GetMapping("/requisites")
    public String requisites(Model model) {
        model.addAttribute("pageTitle", "Реквизиты компаний | АГМА");
        return "requisites";
    }

    // Обработка формы обратной связи
    @PostMapping("/contact")
    public String handleContactForm(
            @ModelAttribute ContactRequest contactRequest,
            RedirectAttributes redirectAttributes) {

        try {
            // Обрабатываем заявку через сервис (отправка email + логирование)
            contactService.processContactRequest(contactRequest);

            // Добавляем сообщение об успехе
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Спасибо, " + contactRequest.getName() + "! Ваша заявка принята. Мы свяжемся с вами в течение 30 минут.");

        } catch (Exception e) {
            // Обрабатываем ошибки
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Произошла ошибка при отправке заявки. Пожалуйста, позвоните нам по телефону.");
        }

        // Добавляем новый объект для формы
        redirectAttributes.addFlashAttribute("contactRequest", new ContactRequest());

        // Перенаправляем на главную страницу (исправлено с /index на /)
        return "redirect:/";
    }
}