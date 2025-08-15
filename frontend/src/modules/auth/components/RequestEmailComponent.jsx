import React, { useState } from 'react';
import styles from '../styles/form-login.module.css';
import { showErrorToast, showSuccessToast } from "../../../kernel/alerts.js";

export const sendPasswordRecoveryEmail = async (email) => {
  const res = await fetch("http://localhost:8080/auth/password/forgot", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email })
  });
  return await res.json();
};

const RequestEmailComponent = ({ email, setEmail, setStep }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email) {
      setError('Ingresa tu correo');
      return;
    }
    setError('');
    setIsLoading(true);
    try {
      const response = await sendPasswordRecoveryEmail(email);
      if (response.type !== 'SUCCESS') throw new Error(response.text);
      showSuccessToast({ title: 'Envío exitoso', text: response.text, timer: 3000 });
      setStep(2);
    } catch (e) {
      showErrorToast({ title: 'Error al enviar el correo', text: e.message, timer: 3000 });
      setError(e.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
      <form onSubmit={handleSubmit}>
        <div className={styles.formGroup}>
          <label htmlFor="email" className={styles.label}>Correo electrónico</label>
          <input
              type="email"
              id="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              className={styles.input}
              placeholder="tucorreo@ejemplo.com"
              required
          />
          {error && <p className="text-red-600 text-sm mt-1">{error}</p>}
        </div>
        <button type="submit" className={styles.submitButton} disabled={isLoading}>
          {isLoading ? 'Enviando...' : 'Enviar código de verificación'}
        </button>
      </form>
  );
};

export default RequestEmailComponent;
