// src/kernel/validation.js

export function validateEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

  if (!email) {
    return "El correo electrónico es obligatorio."
  }

  if (!emailRegex.test(email)) {
    return "Por favor, introduce un correo electrónico válido."
  }

  return "" // Sin errores
}

export function validatePassword(password) {
  const passwordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,}$/

  if (!password) {
    return "La contraseña es obligatoria."
  }

  if (!passwordRegex.test(password)) {
    return "La contraseña debe tener mínimo 6 caracteres en total, un número y un carácter especial."
  }

  return ""
}

