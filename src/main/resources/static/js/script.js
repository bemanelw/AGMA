
document.addEventListener('DOMContentLoaded', function() {
    initSmoothScroll();
    initFormValidation();
    initPhoneMask();
    handleFormScroll();
});

// ------------------------------------
// Плавная прокрутка при нажатии на якоря
// ------------------------------------
function initSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// ------------------------------------
// Клиентская валидация обязательных полей формы
// ------------------------------------
function initFormValidation() {
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function(e) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;

            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    isValid = false;
                    field.classList.add('is-invalid');
                } else {
                    field.classList.remove('is-invalid');
                }
            });

            if (!isValid) {
                e.preventDefault();
                alert('Пожалуйста, заполните все обязательные поля');
            }
        });
    }
}

// ------------------------------------
// Маска для поля телефона (+7 (XXX) XXX-XX-XX)
// ------------------------------------
function initPhoneMask() {
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');

            if (value.startsWith('7') || value.startsWith('8')) {
                value = value.substring(1);
            }

            let formatted = '+7 (';
            if (value.length > 0) formatted += value.substring(0, 3);
            if (value.length > 3) formatted += ') ' + value.substring(3, 6);
            if (value.length > 6) formatted += '-' + value.substring(6, 8);
            if (value.length > 8) formatted += '-' + value.substring(8, 10);

            e.target.value = formatted;
        });
    }
}

// ------------------------------------
// Автопрокрутка к форме после отправки
// ------------------------------------
function handleFormScroll() {
    const formBlock = document.getElementById('contact-form-block');
    const successMsg = document.getElementById('successMsg');
    const errorMsg = document.getElementById('errorMsg');

    // Если на странице есть сообщение от контроллера — плавно скроллим к форме
    if (successMsg || errorMsg) {
        formBlock.scrollIntoView({ behavior: 'smooth', block: 'center' });
        setTimeout(() => {
            (successMsg || errorMsg).style.opacity = '1';
            (successMsg || errorMsg).style.transition = 'opacity 0.6s ease';
        }, 250);
    }
}
