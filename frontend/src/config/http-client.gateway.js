import AxiosClient from "./axios";
import swal from 'sweetalert2';

const errorMessages = {
    TOKEN_EXPIRED: {
        title: 'Código expirado',
        message: 'Solicita un nuevo código de recuperación de contraseña',
    },
    USER_NOT_FOUND: {
        title: 'Usuario no encontrado',
        message: 'El usuario no existe',
    },
    BAD_CREDENTIALS: {
        title: 'Credenciales incorrectas',
        message: 'Usuario y/o contraseña incorrectos',
    },
    SESSION_EXPIRED: {
        title: 'Sesión expirada',
        message: 'Es necesario iniciar sesión nuevamente',
    },
    ACCESS_DENIED: {
        title: 'Acceso denegado',
        message: 'No tienes permisos para acceder a esta ruta',
    },
    SERVER_ERROR: {
        title: 'Error en el servidor',
        message: 'Ha ocurrido un error en el servidor, vuelve a intentarlo más tarde',
    },
    UNEXPECTED_ERROR: {
        title: 'Error inesperado',
        message: 'Ha ocurrido un error inesperado, vuelve a intentarlo más tarde',
    }
};

AxiosClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) config.headers.Authorization = `Bearer ${token}`;
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

AxiosClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            const { status, data } = error.response;
            let errorMessage = Object.values(errorMessages).find(msg => msg.title === data.text);

            if (!errorMessage && data.text) {
                errorMessage = { title: "Error", message: data.text };
            }

            errorMessage = errorMessage || errorMessages.UNEXPECTED_ERROR;

            if (status === 401 || status === 403) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                localStorage.removeItem('roles');
                window.location.href = '/';
            }

            swal.fire({
                title: errorMessage.title,
                text: errorMessage.message,
                icon: "warning",
                confirmButtonText: "Aceptar",
            });

        } else {
            swal.fire({
                title: "Error de conexión",
                text: "No se pudo conectar al servidor",
                icon: "error",
                confirmButtonText: "Aceptar",
            });
        }
        return Promise.reject(error);
    }
);

const httpClient = {
    get: (endpoint) => AxiosClient.get(endpoint),
    post: (endpoint, payload, config) => AxiosClient.post(endpoint, payload, config),
    postBlob: (endpoint, payload) => AxiosClient.post(endpoint, payload, { responseType: 'blob' }),
    put: (endpoint, payload) => AxiosClient.put(endpoint, payload),
    patch: (endpoint, payload) => AxiosClient.patch(endpoint, payload),
    delete: (endpoint) => AxiosClient.delete(endpoint),
};

export const handleRequest = async (method, url, payload) => {
    try {
        const { status, data } = await httpClient[method](url, payload);
        return {
            result: status === 200 ? data.result : null,
            metadata: status === 200 ? data.metadata : null,
            type: data.type || 'SUCCESS',
            text: data.text || 'Operación exitosa'
        };
    } catch (error) {
        return {
            result: null,
            metadata: null,
            type: 'ERROR',
            text: error.response?.data?.text || `Error en solicitud ${method}`
        };
    }
};
  
  export default httpClient;