import Swal from 'sweetalert2';
import '../styles/sweet-alert-styles.css';

export function showAlert(title, message, type = 'success') {
    Swal.fire({
        title: title,
        text: message,
        icon: type,
    });
}

export function showAlertWithoutCancel(title, message, type = 'success') {
    Swal.fire({
        title: title,
        text: message,
        icon: type,
        showCancelButton: false,
    });
}

export function showConfirmation(title, message, type = 'warning', callback, cancelCallback) {
    const appElement = document.getElementById('app');
    Swal.fire({
        title: title,
        text: message,
        icon: type,
        showCancelButton: true,
        showCloseButton: false,
        confirmButtonText: 'Aceptar',
        cancelButtonText: 'Cancelar',
        didOpen: () => {
            if (appElement) {
                appElement.setAttribute('inert', '');
            }
        },
        willClose: () => {
            if (appElement) {
                appElement.removeAttribute('inert');
            }
        },
    }).then((result) => {
        if (result.isConfirmed) {
            callback();
        } else {
            cancelCallback && cancelCallback();
        }
    });
}

export function showConfirmationWithoutCancel({ title, message, type = 'warning', callback = () => {}, cancelCallback = () => {} }) {
    const DURATION = 30 * 60 * 1000; // 30 minutos en ms
    let timerInterval;

    Swal.fire({
        title: title,
        html: `
            <div style="display:flex;flex-direction:column;align-items:center;gap:15px;">
                <p>${message}</p>
                <div id="circular-progress" style="
                    width:100px;
                    height:100px;
                    border-radius:50%;
                    border: 8px solid #eee;
                    border-top: 8px solid #3085d6;
                    animation: spin ${DURATION}ms linear forwards;
                "></div>
                <p id="countdown-text"></p>
            </div>
        `,
        icon: type,
        showCancelButton: false,
        confirmButtonText: 'Aceptar',
        didOpen: () => {
            const content = Swal.getHtmlContainer();
            const countdownText = content.querySelector("#countdown-text");
            let timeLeft = DURATION / 1000; // segundos totales

            timerInterval = setInterval(() => {
                timeLeft--;
                const minutes = Math.floor(timeLeft / 60);
                const seconds = timeLeft % 60;
                countdownText.textContent = `Tiempo restante: ${minutes}:${seconds.toString().padStart(2, "0")}`;

                if (timeLeft <= 0) {
                    clearInterval(timerInterval);
                    Swal.close(); // cerrar automáticamente cuando acabe
                }
            }, 1000);
        },
        willClose: () => {
            clearInterval(timerInterval);
        }
    }).then((result) => {
        if (result.isConfirmed) {
            callback();
        } else {
            cancelCallback();
        }
    });
}

// Animación CSS para el giro circular
const style = document.createElement('style');
style.innerHTML = `
@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}`;
document.head.appendChild(style);


export function showConfirmationAsync(title, message, type = 'warning', callback, cancelCallback) {
    Swal.fire({
        title: title,
        text: message,
        icon: type,
        showCancelButton: true,
        confirmButtonText: 'Aceptar',
        cancelButtonText: 'Cancelar',
    }).then(async (result) => {
        if (result.isConfirmed) {
            await callback();
        } else {
            cancelCallback && await cancelCallback();
        }
    });
}

export function showConfirmationWithoutCancelAsync(title, message, type = 'warning', callback, cancelCallback = async () => {}) {
    Swal.fire({
        title: title,
        text: message,
        icon: type,
        showCancelButton: false,
        confirmButtonText: 'Aceptar',
    }).then(async (result) => {
        if (result.isConfirmed) {
            await callback();
        } else {
            cancelCallback && await cancelCallback();
        }
    });
}

// Toast config

const toast = (config) => {
    const { icon, title, text, onCloseCallback, timer } = config;

    Swal.mixin({
        toast: true,
        position: 'top-end',
        iconColor: 'teacher',
        customClass: {
            popup: 'colored-toast'
        },
        showConfirmButton: false,
        showCancelButton: false,
        timer,
        timerProgressBar: true,
        didClose: () => {
            onCloseCallback && onCloseCallback();
        }
    }).fire({
        icon,
        title,
        text
    });
}

export const showSuccessToast = (config) => {
    const { title, onCloseCallback, timer, text } = config;
    toast({
        icon: 'success',
        title,
        text: text || '',
        onCloseCallback,
        timer: timer || 3000
    });
}

export const showErrorToast = (config) => {
    const { title, onCloseCallback, timer, text } = config;
    toast({
        icon: 'error',
        title,
        text: text || '',
        onCloseCallback,
        timer: timer || 3000
    });
}

export const showWarningToast = (config) => {
    const { title, onCloseCallback, timer, text } = config;
    toast({
        icon: 'warning',
        title,
        text: text || '',
        onCloseCallback,
        timer: timer || 3000
    });
}

export const showInfoToast = (config) => {
    const { title, onCloseCallback, timer, text } = config;
    toast({
        icon: 'info',
        title,
        text: text || '',
        onCloseCallback,
        timer: timer || 3000
    });
}