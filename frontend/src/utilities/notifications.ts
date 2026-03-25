import { toast } from 'vue3-toastify';
import type { ToastOptions } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

export let showNotification = toast;

export function showSuccess(message: string, options?: ToastOptions) {
    showNotification(message, {
        type: 'success',
        autoClose: 2500,
        ...options
    });
}

export function showError(message: string, options?: ToastOptions) {
    showNotification(message, {
        type: 'error',
        autoClose: 10000,
        ...options
    });
}

export function showWarning(message: string, options?: ToastOptions) {
    showNotification(message, {
        type: 'warning',
        ...options
    });
}

export function showInfo(message: string, options?: ToastOptions) {
    showNotification(message, {
        type: 'info',
        ...options
    });
}