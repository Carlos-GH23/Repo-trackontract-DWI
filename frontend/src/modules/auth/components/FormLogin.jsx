import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import styles from "../styles/form-login.module.css";
import { showErrorToast } from "../../../kernel/alerts";

const FormLogin = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  // 🔹 Estados para intentos y bloqueo
  const [attempts, setAttempts] = useState(0);
  const [isBlocked, setIsBlocked] = useState(false);
  const [timeLeft, setTimeLeft] = useState(0);

  const navigate = useNavigate();

  // Revisar si hay bloqueo guardado
  useEffect(() => {
    const blockedUntil = localStorage.getItem("blockedUntil");
    if (blockedUntil) {
      const diff = Math.floor((new Date(blockedUntil) - new Date()) / 1000);
      if (diff > 0) {
        setIsBlocked(true);
        setTimeLeft(diff);
      } else {
        localStorage.removeItem("blockedUntil");
      }
    }
  }, []);

  // Temporizador para desbloquear
  useEffect(() => {
    if (isBlocked && timeLeft > 0) {
      const timer = setInterval(() => {
        setTimeLeft((prev) => {
          if (prev <= 1) {
            setIsBlocked(false);
            localStorage.removeItem("blockedUntil");
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
      return () => clearInterval(timer);
    }
  }, [isBlocked, timeLeft]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (isBlocked) {
        showErrorToast({
          title: "Error",
          text: `Cuenta bloqueada. Intenta nuevamente en ${Math.ceil(timeLeft / 60)} minutos`,
          timer: 4000
        });
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) throw new Error("Credenciales incorrectas");

      const data = await response.json();

      const { token, user } = data;

      if (!token || typeof token !== 'string') {
        throw new Error("Token no encontrado o inválido en la respuesta del servidor");
      }

      if (!user || typeof user !== 'object') {
        throw new Error("Información de usuario no encontrada en la respuesta del servidor");
      }

      // Guarda token
      localStorage.setItem("accessToken", token);

      // Guarda información del usuario
      localStorage.setItem("userId", user.id);
      localStorage.setItem("user", user.name);
      localStorage.setItem("email", user.email);
      localStorage.setItem("role", user.role);

      // Decodifica token
      jwtDecode(token);

      // Redirecciona según rol
      if (user.role === "ADMIN") {
        navigate("/admin/contratos");
      } else if (user.role === "ABOGADO") {
        navigate("/abogado/profile");
      } else if (user.role === "CLIENT") {
        navigate("/empresa/profile");
      } else {
        alert("Rol no reconocido: " + user.role);
      }
    } catch (err) {
      const newAttempts = attempts + 1;
      setAttempts(newAttempts);

      if (newAttempts < 3) {
        showErrorToast({
          title: "Error",
          text: `Credenciales incorrectas. Te quedan ${3 - newAttempts} intento(s).`,
          timer: 4000
        });
      } else {
        // Bloquear por 30 min
        const unblockTime = new Date(Date.now() + 30 * 60 * 1000);
        localStorage.setItem("blockedUntil", unblockTime.toISOString());
        setIsBlocked(true);
        setTimeLeft(30 * 60);
        setAttempts(0);
          showErrorToast({
          title: "Error",
          text: `Cuenta bloqueada, intenta de nuevo después de 30 minutos.`,
          timer: 4000
        });
      }
    }
  };

  const togglePasswordVisibility = () => setShowPassword(!showPassword);

  return (
    <div className={styles.container}>
      {/* 🔹 Bloqueo con círculo */}
      {isBlocked && (
        <div style={{
          position: "fixed", top: 0, left: 0, width: "100%", height: "100%",
          background: "rgba(0,0,0,0.5)", display: "flex", flexDirection: "column",
          alignItems: "center", justifyContent: "center", color: "#fff", fontSize: "18px", zIndex: 9999
        }}>
          <div style={{
            width: "80px", height: "80px", border: "6px solid #ccc",
            borderTop: "6px solid red", borderRadius: "50%",
            animation: "spin 1s linear infinite"
          }}></div>
          <p style={{ marginTop: "20px" }}>
            Cuenta bloqueada. Espera {Math.ceil(timeLeft / 60)} minutos
          </p>
        </div>
      )}

      <div className={styles.leftPanel}>
        <div className={styles.logoContainer}>
          <div className={styles.logoWrapper}>
            <div className={styles.shield}>
              <svg xmlns="http://www.w3.org/2000/svg"width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
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

      <div className={styles.rightPanel}>
        <form className={styles.form} onSubmit={handleSubmit}>
          <h2 className={styles.title}>Bienvenido a TrackOntract</h2>
          <p className={styles.subtitle}>Inicia sesión con tu cuenta</p>

          <div className={styles.formGroup}>
            <label htmlFor="email" className={styles.label}>
              Correo electrónico
            </label>
            <input
              type="email"
              id="email"
              className={styles.input}
              placeholder="Correo electrónico"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="password" className={styles.label}>
              Contraseña
            </label>
            <div className={styles.passwordInputGroup}>
              <input
                type={showPassword ? "text" : "password"}
                id="password"
                className={styles.input}
                placeholder="Contraseña"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <button type="button" className={styles.passwordToggle} onClick={togglePasswordVisibility}>
                {showPassword ? (
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                    viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                    <line x1="1" y1="1" x2="23" y2="23" />
                  </svg>
                ) : (
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                    viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                )}
              </button>
            </div>
          </div>

          <button type="submit" className={styles.submitButton}>
            Iniciar Sesión
          </button>

          <div className={styles.linksContainer}>
            <Link to="/forgot-password" className={styles.link}>
              ¿Olvidaste tu contraseña?
            </Link>
          </div>
        </form>
      </div>
    </div>
  )
}

export default FormLogin;
