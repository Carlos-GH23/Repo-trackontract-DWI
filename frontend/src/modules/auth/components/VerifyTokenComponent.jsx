import React, { useState } from 'react';
import styles from '../styles/form-login.module.css';
import { validateRecoveryToken } from '../adapters/auth.controller';
import {showErrorToast, showSuccessToast} from "../../../kernel/alerts.js";
import Loader from "../../../components/layout/Loader.jsx";

const VerifyTokenComponent = ({ email, token, setToken, setStep, setUser }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!token) {
      setError('El token es requerido');
      return;
    }

    setError('');
    setIsLoading(true);

    try {
      const response = await validateRecoveryToken(token);
      if (response.type !== 'SUCCESS') throw new Error(response.text);
      
      setUser(response.result);
      showSuccessToast({title: 'Token verificado', text: response.text, timer: 3000});
      setStep(3);
    } catch (e) {
      showErrorToast({title: 'Error al verificar el token', text: e.message, timer: 3000});
      setError(e.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Loader isLoading={isLoading} />
      <form onSubmit={handleSubmit}>
        <div className="mb-3 text-center">
          <p className="text-muted">
            Hemos enviado un código de 5 dígitos a <span className="fw-bold" style={{color: 'var(--primary)'}}>{email}</span>
          </p>
        </div>

        <div className={styles.formGroup}>
          <label htmlFor="token" className={styles.label}>Código de verificación</label>
          <input
            type="text"
            id="token"
            value={token}
            onChange={(e) => setToken(e.target.value)}
            className={`${styles.input} text-center fw-bold`}
            placeholder="_____"
            maxLength="5"
            required
            style={{letterSpacing: '5px', fontSize: '1.2rem'}}
          />
          {error && <p className="text-red-600 text-sm mt-1">{error}</p>}
        </div>

        <button 
          type="submit" 
          className={styles.submitButton}
          disabled={isLoading}
        >
          {isLoading ? 'Verificando...' : 'Verificar código'}
        </button>

        <div className="text-center mt-3">
          <button
            type="button"
            className="btn btn-link p-0"
            style={{color: 'var(--primary)', fontSize: '0.9rem'}}
            onClick={() => setStep(1)}
          >
            ↻ Reenviar código
          </button>
        </div>
      </form>
    </>
  );
};

export default VerifyTokenComponent;
