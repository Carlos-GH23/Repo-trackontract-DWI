import { handleRequest } from "../../../config/http-client.gateway.js";

export const login = async (email, password) => {
    try {
        const response = await handleRequest('post', '/users/login', {email, password})

        if (response.type !== 'SUCCESS' || response.status === 'ERROR')
            throw new Error(response.text)

        const { token, user } = response.result;
        localStorage.setItem('token', token)
        localStorage.setItem('user', user.name)
        localStorage.setItem('role', user.role)
        localStorage.setItem('email', user.email)

        return response
    } catch (e) {
        throw new Error(e.message)
    } 
}

export const sendPasswordRecoveryEmail = async (email) => {
    try {
        const response = await handleRequest('post', '/users/send-email', { email })

        if (response.type !== 'SUCCESS' || response.status === 'ERROR')
            throw new Error(response.text)

        return response
    } catch (e) {
        throw new Error(e.message)
    }
}

export const validateRecoveryToken = async (token) => {
    try {
        const response = await handleRequest('post', '/users/validate-recovery-token', { token })

        if (response.type !== 'SUCCESS' || response.status === 'ERROR')
            throw new Error(response.text)

        return response
    } catch (e) {
        throw new Error(e.message)
    }
}

export const resetPassword = async (email, nuevaPassword, confirmarPassword) => {
    try {
        const response = await handleRequest('post', '/users/restaurar-password', {
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
