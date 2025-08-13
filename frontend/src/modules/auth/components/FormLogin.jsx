import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import {jwtDecode} from "jwt-decode";
import styles from "../styles/form-login.module.css";
import { showErrorToast } from "../../../kernel/alerts";

const FormLogin = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) throw new Error("Error al iniciar sesión");

      const data = await response.json();
      
      // El backend devuelve { token, user }
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
      localStorage.setItem("user", user.name);
      localStorage.setItem("email", user.email);
      localStorage.setItem("role", user.role);

      // Decodifica token para verificar
      const decoded = jwtDecode(token);

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
      showErrorToast({
        title: "Error",
        text: err.message || "Credenciales incorrectas o cuenta bloqueada",
        timer: 4000
      });
    }
  };

  const togglePasswordVisibility = () => setShowPassword(!showPassword);

  return (
    <div className={styles.container}>
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
                />
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
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                    <line x1="1" y1="1" x2="23" y2="23" />
                  </svg>
                ) : (
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
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

export default FormLogin