package ru.agma.transport.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ContactRequest {
    @NotBlank(message = "Имя обязательно для заполнения")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String name;

    @NotBlank(message = "Телефон обязателен для заполнения")
    @Pattern(regexp = "^[\\d\\s\\-\\(\\)\\+]+$", message = "Некорректный формат телефона")
    private String phone;

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email обязателен для заполнения")
    private String email;

    @NotBlank(message = "Укажите пункт отправления")
    private String routeFrom;

    @NotBlank(message = "Укажите пункт назначения")
    private String routeTo;

    private String message;

    public ContactRequest() {
    }



    public ContactRequest(String name, String phone, String email, String routeFrom, String routeTo) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRouteFrom() {
        return routeFrom;
    }

    public void setRouteFrom(String routeFrom) {
        this.routeFrom = routeFrom;
    }

    public String getRouteTo() {
        return routeTo;
    }

    public void setRouteTo(String routeTo) {
        this.routeTo = routeTo;
    }
}
