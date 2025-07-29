import React, { useState, useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"

const usuariosMock = [
  {
    nombre: "Carlos",
    apellidos: "Galán Hernández",
    correo: "202213nt164@utsz.edu.mx",
    telefono: "7776432657",
    estado: "Activo",
  },
  {
    nombre: "María",
    apellidos: "González López",
    correo: "maria.gonzalez@utsz.edu.mx",
    telefono: "7771234567",
    estado: "Activo",
  },
  {
    nombre: "Juan",
    apellidos: "Pérez Martínez",
    correo: "juan.perez@utsz.edu.mx",
    telefono: "7779876543",
    estado: "Inactivo",
  },
  {
    nombre: "Ana",
    apellidos: "Rodríguez Silva",
    correo: "ana.rodriguez@utsz.edu.mx",
    telefono: "7775551234",
    estado: "Activo",
  },
]

export default function EditarUsuario() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    nombre: "",
    apellidos: "",
    correo: "",
    telefono: "",
    estado: "Activo",
  })

  useEffect(() => {
    const user = usuariosMock[id]
    if (user) {
      setFormData(user)
    } else {
      console.warn("Usuario no encontrado")
    }
  }, [id])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const toggleEstado = () => {
    setFormData((prev) => ({
      ...prev,
      estado: prev.estado === "Activo" ? "Inactivo" : "Activo",
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    console.log("Datos actualizados:", formData)
    navigate("/admin/usuarios")
  }

  return (
    <div className="min-h-screen bg-white p-6">
      <div className="flex justify-between items-center mb-8">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate("/admin/usuarios")}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <svg
              className="w-6 h-6 text-gray-700"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M15 19l-7-7 7-7"
              />
            </svg>
          </button>
          <div className="flex items-center space-x-3">
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
              <h1 className="text-2xl font-bold text-gray-900">Editar Usuario</h1>
              <p className="text-sm text-gray-600">
                Modifica la información y estado del usuario seleccionado
              </p>
            </div>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="max-w-3xl mx-auto space-y-6">
        <div className="bg-white shadow-lg rounded-xl border border-gray-200">
          <div className="px-6 py-4 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-800 flex items-center">
              <svg
                className="w-5 h-5 mr-2"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                />
              </svg>
              Información del Usuario
            </h3>
          </div>

          <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nombre
              </label>
              <input
                name="nombre"
                value={formData.nombre}
                onChange={handleChange}
                placeholder="Ej. Carlos"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Apellidos
              </label>
              <input
                name="apellidos"
                value={formData.apellidos}
                onChange={handleChange}
                placeholder="Ej. Galán Hernández"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Correo electrónico
              </label>
              <input
                name="correo"
                value={formData.correo}
                onChange={handleChange}
                placeholder="usuario@ejemplo.com"
                type="email"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Teléfono
              </label>
              <input
                name="telefono"
                value={formData.telefono}
                onChange={handleChange}
                placeholder="7770000000"
                type="tel"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              />
            </div>
          </div>

          <div className="px-6 pb-6">
            <label className="block text-sm font-medium text-gray-700 mb-3">
              Estado del Usuario
            </label>
            <div className="flex items-center space-x-3">
              <button
                type="button"
                onClick={toggleEstado}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${
                  formData.estado === "Activo" ? "bg-green-600" : "bg-gray-300"
                }`}
              >
                <span
                  className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                    formData.estado === "Activo"
                      ? "translate-x-6"
                      : "translate-x-1"
                  }`}
                />
              </button>
              <span className="text-sm font-medium text-gray-700">
                {formData.estado === "Activo" ? "Habilitado" : "Inhabilitado"}
              </span>
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={() => navigate("/admin/usuarios")}
            className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors duration-200"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
          >
            Guardar Cambios
          </button>
        </div>
      </form>
    </div>
  )
}
