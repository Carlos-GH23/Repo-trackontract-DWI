import React, { useState, useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"

const contratosMock = [
  {
    nombre: "Contrato A",
    cliente: "Empresa X",
    categoria: "Software",
    vencimiento: "2025-12-31",
    descripcion: "Contrato para el desarrollo de un sistema de gestión.",
    estado: "Activo",
  },
  {
    nombre: "Contrato B",
    cliente: "Corporación Y",
    categoria: "Consultoría",
    vencimiento: "2025-08-15",
    descripcion: "Asesoría técnica en procesos internos.",
    estado: "Vencido",
  },
  {
    nombre: "Contrato C",
    cliente: "Startup Z",
    categoria: "Soporte",
    vencimiento: "2026-01-10",
    descripcion: "Mantenimiento técnico mensual.",
    estado: "Activo",
  },
]

export default function EditarContrato() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    nombre: "",
    cliente: "",
    categoria: "",
    vencimiento: "",
    descripcion: "",
    estado: "Activo",
  })

  useEffect(() => {
    const contrato = contratosMock[id]
    if (contrato) {
      setFormData(contrato)
    } else {
      console.warn("Contrato no encontrado")
    }
  }, [id])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const toggleEstado = () => {
    setFormData((prev) => ({
      ...prev,
      estado: prev.estado === "Activo" ? "Vencido" : "Activo",
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    console.log("Contrato actualizado:", formData)
    navigate("/admin/contratos")
  }

  return (
    <div className="min-h-screen bg-white p-6">
      <div className="max-w-4xl mx-auto">
        {/* Header con botón atrás */}
        <div className="flex items-center mb-8">
          <button
            onClick={() => navigate("/admin/contratos")}
            className="mr-4 p-2 hover:bg-gray-100 rounded-lg transition duration-200 flex items-center justify-center shadow-sm"
          >
            <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <div className="flex items-center">
            <div className="bg-blue-100 p-3 rounded-lg mr-4 shadow-sm">
              <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Editar Contrato</h1>
              <p className="text-sm text-gray-600">Panel de Administración - Gestión de Contratos</p>
            </div>
          </div>
        </div>

        {/* Mensaje informativo */}
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6 shadow">
          <p className="text-sm text-red-700">
            Modifica los campos necesarios para actualizar la información del contrato *
          </p>
        </div>

        {/* Formulario */}
        <form onSubmit={handleSubmit}>
          <div className="bg-white rounded-xl shadow-2xl border border-blue-200 overflow-hidden">
            <div className="bg-blue-50 px-6 py-4 border-b border-blue-200">
              <h3 className="text-lg font-semibold text-gray-800 flex items-center">
                <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center mr-3 shadow-sm">
                  <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
                Información del contrato
              </h3>
            </div>

            <div className="p-6 space-y-6">
              {/* Nombre */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">Nombre del Contrato *</label>
                <input
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleChange}
                  type="text"
                  placeholder="Contrato de Servicios Profesionales 2025"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
                />
              </div>

              {/* Cliente y Categoría como selects */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Cliente *</label>
                  <select
                    name="cliente"
                    value={formData.cliente}
                    onChange={handleChange}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm"
                  >
                    <option value="">Seleccione un cliente</option>
                    <option value="Empresa X">Empresa X</option>
                    <option value="Corporación Y">Corporación Y</option>
                    <option value="Startup Z">Startup Z</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Categoría *</label>
                  <select
                    name="categoria"
                    value={formData.categoria}
                    onChange={handleChange}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm"
                  >
                    <option value="">Seleccione una categoría</option>
                    <option value="Software">Software</option>
                    <option value="Consultoría">Consultoría</option>
                    <option value="Soporte">Soporte</option>
                  </select>
                </div>
              </div>

              {/* Fecha y estado */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Fecha de Vencimiento *</label>
                  <input
                    name="vencimiento"
                    type="date"
                    value={formData.vencimiento}
                    onChange={handleChange}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Estado del Contrato *</label>
                  <div className="flex items-center space-x-4 mt-1">
                    <div
                      onClick={toggleEstado}
                      className={`relative inline-flex h-6 w-11 items-center rounded-full cursor-pointer transition-colors duration-300 ${
                        formData.estado === "Activo" ? "bg-green-600" : "bg-gray-400"
                      }`}
                    >
                      <span
                        className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-300 ${
                          formData.estado === "Activo" ? "translate-x-6" : "translate-x-1"
                        }`}
                      />
                    </div>
                    <span className="text-sm font-medium text-gray-700">
                      {formData.estado === "Activo" ? "Activo" : "Vencido"}
                    </span>
                  </div>
                </div>
              </div>

              {/* Descripción */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Descripción del Contrato *</label>
                <textarea
                  name="descripcion"
                  value={formData.descripcion}
                  onChange={handleChange}
                  rows={5}
                  placeholder="Describe detalladamente el contrato..."
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none shadow-sm"
                />
              </div>
            </div>
          </div>

          {/* Botones */}
          <div className="flex justify-end space-x-4 mt-8">
            <button
              type="button"
              onClick={() => navigate("/admin/contratos")}
              className="px-8 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 font-medium shadow"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium flex items-center gap-2 shadow-md"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
              Guardar Cambios
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
