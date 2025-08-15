import React, { useState } from 'react';
import { validateRecoveryToken } from '../adapters/auth.controller';
import styles from '../styles/form-login.module.css';
import { showErrorToast, showSuccessToast } from '../../../kernel/alerts.js';

const VerifyTokenComponent = ({ email, token, setToken, setStep, setUser }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setToken(e.target.value.toUpperCase().slice(0, 5)); // Limitar a 5 caracteres
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (token.length !== 5) {
      setError('El token debe tener 5 caracteres');
      return;
    }

    setIsLoading(true);
    try {
      const response = await validateRecoveryToken(token); // CORRECTO
      if (response.type !== 'SUCCESS') throw new Error(response.text);

      showSuccessToast({ title: 'Token válido', text: response.text, timer: 2000 });
      setUser(response.result.user);
      setStep(3);
    } catch (e) {
      showErrorToast({ title: 'Error', text: e.message, timer: 3000 });
      setError(e.message);
    } finally {
      setIsLoading(false);
    }
  };


  return (
      <form onSubmit={handleSubmit}>
        <div className={styles.formGroup}>
          <label htmlFor="token" className={styles.label}>Código de verificación</label>
          <input
              type="text"
              id="token"
              value={token}
              onChange={handleChange}
              className={styles.input}
              placeholder="Ingrese el código"
              required
          />
          {error && <p className="text-red-600 text-sm mt-1">{error}</p>}
        </div>

        <button type="submit" className={styles.submitButton} disabled={isLoading}>
          {isLoading ? 'Verificando...' : 'Verificar código'}
        </button>
      </form>
  );
};

export default VerifyTokenComponent;
