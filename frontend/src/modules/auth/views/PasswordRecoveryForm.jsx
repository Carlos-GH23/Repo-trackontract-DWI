import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import RequestEmailComponent from '../components/RequestEmailComponent';
import VerifyTokenComponent from '../components/VerifyTokenComponent';
import ChangePasswordComponent from '../components/ChangePasswordComponent';
import styles from '../styles/form-login.module.css';

const PasswordRecoveryForm = () => {
  const [step, setStep] = useState(1);
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [user, setUser] = useState(null);

  return (
      <div className={styles.container} style={{ backgroundColor: 'var(--white)' }}>

        {/* Panel izquierdo con logo */}
        <div className={styles.leftPanel}>
          <div className={styles.logoContainer}>
            <div className={styles.logoWrapper}>
              <div className={styles.shield}>
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
                  <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                </svg>
              </div>
              <div className={styles.scales}>
                <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" >
                  <path d="M16 11V3a1 1 0 0 0-1-1h-6a1 1 0 0 0-1 1v8" />
                  <path d="M8 21h8" />
                  <path d="M12 17v4" />
                  <path d="M4 15s2-1 4-1 4 1 4 1-2 1-4 1-4-1-4-1Z" />
                  <path d="M16 15s2-1 4-1 4 1 4 1-2 1-4 1-4-1-4-1Z" />
                </svg>
              </div>
            </div>
            <h1 className={styles.brandName}>TrackOntract</h1>
          </div>
        </div>

        {/* Panel derecho con formulario */}
        <div className={styles.rightPanel}>
          <div className={styles.form} style={{ maxWidth: '450px' }}>
            <h2 className={styles.title}>Restablecer Contraseña</h2>
            <p className={styles.subtitle}>
              {step === 1 && 'Ingresa tu correo para comenzar'}
              {step === 2 && 'Verifica tu identidad'}
              {step === 3 && 'Crea una nueva contraseña'}
            </p>

            {/* Indicador de pasos */}
            <div style={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              marginBottom: '2rem',
              gap: '0.5rem'
            }}>
              {[1, 2, 3].map((stepNumber) => (
                  <React.Fragment key={stepNumber}>
                    <div style={{
                      width: '30px',
                      height: '30px',
                      borderRadius: '50%',
                      backgroundColor: step >= stepNumber ? 'var(--primary)' : 'var(--blue)',
                      color: 'white',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '0.9rem',
                      fontWeight: '600'
                    }}>
                      {stepNumber}
                    </div>
                    {stepNumber < 3 && (
                        <div style={{
                          width: '30px',
                          height: '2px',
                          backgroundColor: step > stepNumber ? 'var(--primary)' : 'var(--blue)'
                        }}></div>
                    )}
                  </React.Fragment>
              ))}
            </div>

            {/* Componentes de cada paso */}
            {step === 1 && <RequestEmailComponent email={email} setEmail={setEmail} setStep={setStep} />}
            {step === 2 && <VerifyTokenComponent email={email} token={token} setToken={setToken} setStep={setStep} setUser={setUser} />}
            {step === 3 && <ChangePasswordComponent email={email} token={token} setStep={setStep} user={user} />}

            {/* Link de regreso */}
            <div style={{ marginTop: '1.5rem', textAlign: 'center', borderTop: '1px solid #eee', paddingTop: '1.5rem' }}>
              <Link to="/" style={{
                color: 'var(--primary)',
                textDecoration: 'none',
                fontWeight: '600',
                fontSize: '0.9rem',
                display: 'inline-flex',
                alignItems: 'center'
              }}>
                ← Volver al inicio de sesión
              </Link>
            </div>
          </div>
        </div>

      </div>
  );
};

export default PasswordRecoveryForm;
