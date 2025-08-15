import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

export default function RegistroUsuario() {
  const [name, setName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirm, setPasswordConfirm] = useState("");
  const [usuarioActivo, setUsuarioActivo] = useState(true);
  const navigate = useNavigate();

  const volverAlListado = () => {
    navigate("/admin/abogados");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!name || !lastName || !email || !phoneNumber || !password || !passwordConfirm) {
      Swal.fire("Error", "Por favor completa todos los campos obligatorios.", "error");
      return;
    }

    if (password !== passwordConfirm) {
      Swal.fire("Error", "Las contraseñas no coinciden.", "error");
      return;
    }

    const result = await Swal.fire({
      title: "¿Confirmas crear este usuario?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Sí, crear",
      cancelButtonText: "Cancelar",
    });

    if (!result.isConfirmed) return;

    const nuevoUsuario = {
      name: name.trim(),
      last_name: lastName.trim(),
      email: email.trim(),
      phoneNumber: phoneNumber.trim(),
      password: password,
      status: usuarioActivo,
    };

    try {
      const token = localStorage.getItem("accessToken");
      const response = await fetch("http://localhost:8080/users/save", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(nuevoUsuario),
      });

      if (!response.ok) {
        let errorMsg = "Error al crear usuario";
        try {
          const errJson = await response.json();
          if (errJson.text) errorMsg = errJson.text;
          if (errJson.result) errorMsg = errJson.result;
        } catch (parseError) {
          // Si no se puede parsear como JSON, usar el texto plano
          errorMsg = "Error del servidor: " + response.status + " " + response.statusText;
        }
        throw new Error(errorMsg);
      }

      Swal.fire("¡Creado!", "El usuario fue registrado exitosamente.", "success");
      volverAlListado();
    } catch (error) {
      Swal.fire("Error", error.message, "error");
    }
  };

  return (
      <div className="min-h-screen bg-white p-6">
        <div className="max-w-4xl mx-auto">
          <div className="flex items-center mb-8 justify-between">
            <div className="flex items-center space-x-4">
              <button
                  onClick={volverAlListado}
                  className="mr-2 p-2 hover:bg-gray-100 rounded-lg transition-colors duration-200"
                  aria-label="Volver al listado de usuarios"
              >
                <svg
                    className="w-6 h-6 text-gray-800"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
              </button>
              <div className="bg-blue-100 p-3 rounded-lg">
                <svg
                    className="w-7 h-7 text-blue-600"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                >
                  <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M17 20h5v-2a4 4 0 00-3-3.87M9 20H4v-2a4 4 0 013-3.87m9-9a4 4 0 11-8 0 4 4 0 018 0zm6 4a4 4 0 11-8 0 4 4 0 018 0zM9 7a4 4 0 11-8 0 4 4 0 018 0z"
                  />
                </svg>
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Registro de Usuario</h1>
                <p className="text-sm text-gray-600">Panel de Administración - Gestión de Usuarios</p>

              </div>
            </div>
          </div>

          <form className="space-y-6" onSubmit={handleSubmit}>
            <div className="bg-white shadow-lg rounded-xl border border-gray-200">
              <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-800 flex items-center">Información Personal</h3>
              </div>
              <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label htmlFor="name" className="block text-sm font-medium text-gray-700">Nombre *</label>
                  <input
                      id="name"
                      name="name"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      placeholder="Ejemplo: Carlos"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                  />
                </div>
                <div>
                  <label htmlFor="last_name" className="block text-sm font-medium text-gray-700">Apellidos *</label>
                  <input
                      id="last_name"
                      name="last_name"
                      value={lastName}
                      onChange={(e) => setLastName(e.target.value)}
                      placeholder="Ejemplo: Galán Hernández"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                  />
                </div>
                <div>
                  <label htmlFor="email" className="block text-sm font-medium text-gray-700">Correo Electrónico *</label>
                  <input
                      id="email"
                      name="email"
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      placeholder="ejemplo@mail.com"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                  />
                </div>
                <div>
                  <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700">Teléfono *</label>
                  <input
                      id="phoneNumber"
                      name="phoneNumber"
                      type="tel"
                      value={phoneNumber}
                      onChange={(e) => setPhoneNumber(e.target.value)}
                      placeholder="55 1234 5678"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                  />
                </div>
              </div>
            </div>

            <div className="bg-white shadow-lg rounded-xl border border-gray-200">
              <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-800 flex items-center">Configuración de Acceso</h3>
              </div>
              <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label htmlFor="password" className="block text-sm font-medium text-gray-700">Contraseña *</label>
                  <input
                      id="password"
                      name="password"
                      type="password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      placeholder="********"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                  />
                </div>
                <div>
                  <label htmlFor="passwordConfirm" className="block text-sm font-medium text-gray-700">Confirmar Contraseña *</label>
                  <input
                      id="passwordConfirm"
                      name="passwordConfirm"
                      type="password"
                      value={passwordConfirm}
                      onChange={(e) => setPasswordConfirm(e.target.value)}
                      placeholder="********"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                  />
                </div>
              </div>

              <div className="p-6">
                <div className="flex items-center space-x-3">
                  <button
                      type="button"
                      onClick={() => setUsuarioActivo(!usuarioActivo)}
                      className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${
                          usuarioActivo ? "bg-green-600" : "bg-gray-300"
                      }`}
                      aria-pressed={usuarioActivo}
                      aria-label="Alternar estado activo usuario"
                  >
                  <span
                      className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                          usuarioActivo ? "translate-x-6" : "translate-x-1"
                      }`}
                  />
                  </button>
                  <label className="text-sm font-medium text-gray-700">
                    {usuarioActivo ? "Habilitado" : "Inhabilitado"}
                  </label>
                </div>
              </div>
            </div>

            <div className="flex justify-end space-x-4">
              <button
                  type="button"
                  onClick={volverAlListado}
                  className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors duration-200"
              >
                Cancelar
              </button>
              <button
                  type="submit"
                  className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
              >
                Registrar Usuario
              </button>
            </div>
          </form>
        </div>
      </div>
  );
}
