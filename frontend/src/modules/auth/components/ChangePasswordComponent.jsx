import React, { useState } from 'react';
import styles from '../styles/form-login.module.css';
import { resetPassword } from '../adapters/auth.controller';
import { useNavigate } from 'react-router-dom';
import { showErrorToast, showSuccessToast } from "../../../kernel/alerts.js";
import Loader from "../../../components/layout/Loader.jsx";

const ChangePasswordComponent = ({ email, token, setStep, user }) => {
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!newPassword || !confirmPassword) {
            setError('Ambas contraseñas son requeridas');
            return;
        }

        if (newPassword !== confirmPassword) {
            setError('Las contraseñas no coinciden');
            return;
        }

        if (newPassword.length < 6) {
            setError('La contraseña debe tener al menos 6 caracteres');
            return;
        }

        setError('');
        setIsLoading(true);

        try {
            // Aquí enviamos token + nueva contraseña
            const response = await resetPassword(token, newPassword);

            if (response.type !== 'SUCCESS') throw new Error(response.text);

            // Guardar JWT en localStorage para autenticación inmediata
            localStorage.setItem('token', response.result.token);

            showSuccessToast({
                title: 'Cambio Exitoso',
                text: response.text,
                timer: 2000
            });

            // Redirigir después de 2 segundos
            setTimeout(() => {
                navigate('/');
            }, 2000);
        } catch (e) {
            showErrorToast({
                title: 'Error al cambiar la contraseña',
                text: e.message,
                timer: 3000
            });
            setError(e.message);
        } finally {
            setIsLoading(false);
        }
    };

    const togglePasswordVisibility = () => setShowPassword(!showPassword);
    const toggleConfirmPasswordVisibility = () => setShowConfirmPassword(!showConfirmPassword);

    return (
        <>
            <Loader isLoading={isLoading} />
            <form onSubmit={handleSubmit}>
                <div className={styles.formGroup}>
                    <label htmlFor="newPassword" className={styles.label}>Nueva contraseña</label>
                    <div className={styles.passwordInputGroup}>
                        <input
                            type={showPassword ? 'text' : 'password'}
                            id="newPassword"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            className={styles.input}
                            placeholder="Ingresa tu nueva contraseña"
                            required
                        />
                        <button type="button" className={styles.passwordToggle} onClick={togglePasswordVisibility}>
                            {showPassword ? '🙈' : '👁️'}
                        </button>
                    </div>
                </div>

                <div className={styles.formGroup}>
                    <label htmlFor="confirmPassword" className={styles.label}>Confirmar contraseña</label>
                    <div className={styles.passwordInputGroup}>
                        <input
                            type={showConfirmPassword ? 'text' : 'password'}
                            id="confirmPassword"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            className={styles.input}
                            placeholder="Confirma tu nueva contraseña"
                            required
                        />
                        <button type="button" className={styles.passwordToggle} onClick={toggleConfirmPasswordVisibility}>
                            {showConfirmPassword ? '🙈' : '👁️'}
                        </button>
                        {error && <p className="text-red-600 text-sm mt-1">{error}</p>}
                    </div>
                </div>

                <button type="submit" className={styles.submitButton} disabled={isLoading}>
                    {isLoading ? 'Cambiando...' : 'Cambiar contraseña'}
                </button>
            </form>
        </>
    );
};

export default ChangePasswordComponent;
