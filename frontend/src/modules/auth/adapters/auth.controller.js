import { handleRequest } from "../../../config/http-client.gateway.js";

export const login = async (email, password) => {
    try {
        const response = await handleRequest('post', '/auth/login', {email, password})

        if (response.type !== 'SUCCESS' || response.status === 'ERROR')
            throw new Error(response.text)

        const { token, user } = response.result;
        
        // Guardar en localStorage para compatibilidad
        localStorage.setItem('token', token)
        localStorage.setItem('user', user.name)
        localStorage.setItem('role', user.role)
        localStorage.setItem('email', user.email)

        return response
    } catch (e) {
        throw new Error(e.message)
    } 
}

export const logout = async () => {
    try {
        const token = localStorage.getItem('token');
        if (token) {
            await handleRequest('post', '/auth/logout', {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
        }
    } catch (error) {
        console.error('Error en logout:', error);
    } finally {
        // Limpiar localStorage independientemente del resultado
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('role');
        localStorage.removeItem('email');
    }
};

export const sendPasswordRecoveryEmail = async (email) => {
    try {
        const response = await handleRequest('post', '/auth/send-email', { email })

        if (response.type !== 'SUCCESS' || response.status === 'ERROR')
            throw new Error(response.text)

        return response
    } catch (e) {
        throw new Error(e.message)
    }
}

export const validateRecoveryToken = async (token) => {
    try {
        const response = await handleRequest('post', '/auth/validate-recovery-token', { token })

        if (response.type !== 'SUCCESS' || response.status === 'ERROR')
            throw new Error(response.text)

        return response
    } catch (e) {
        throw new Error(e.message)
    }
}

export const resetPassword = async (email, nuevaPassword, confirmarPassword) => {
    try {
        const response = await handleRequest('post', '/auth/restaurar-password', {
            email,
            nuevaPassword,
            confirmarPassword
        })

        if (response.type !== 'SUCCESS' || response.status === 'ERROR')
            throw new Error(response.text)

        return response
    } catch (e) {
        throw new Error(e.message)
    }
}
