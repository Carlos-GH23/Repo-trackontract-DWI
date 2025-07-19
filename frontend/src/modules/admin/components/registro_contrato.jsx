import { useState } from "react"
import { useNavigate } from "react-router-dom"

export default function RegistroContrato() {
  const [habilitado, setHabilitado] = useState(true)
  const navigate = useNavigate()

  const volverAContratos = () => {
    navigate("/admin/contratos")
  }

  return (
    <div className="min-h-screen bg-white p-6">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center mb-8">
          <button
            onClick={volverAContratos}
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
              <h1 className="text-2xl font-bold text-gray-900">Registro de Nuevo Contrato</h1>
              <p className="text-sm text-gray-600">Panel de Administración - Gestión de Contratos</p>
            </div>
          </div>
        </div>

        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6 shadow">
          <p className="text-sm text-red-700">
            Completa todos los campos para registrar un nuevo contrato en el sistema *
          </p>
        </div>

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
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Nombre del Contrato *</label>
                <input
                  type="text"
                  placeholder="Contrato de Servicios Profesionales 2025"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Cliente *</label>
                <select
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm"
                >
                  <option>Seleccione un cliente</option>
                  <option>Empresa ABC S.A. de C.V.</option>
                  <option>TechCorp Solutions</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Categoría *</label>
                <select
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm"
                >
                  <option>Seleccione una categoría</option>
                  <option>Servicios Profesionales</option>
                  <option>Mantenimiento</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Fecha de Vencimiento *</label>
                <input
                  type="date"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">Estado del Contrato *</label>
              <div className="flex items-center space-x-4">
                <div
                  onClick={() => setHabilitado(!habilitado)}
                  className={`relative inline-flex h-6 w-11 items-center rounded-full cursor-pointer transition-colors duration-300 ${habilitado ? "bg-green-600" : "bg-gray-400"}`}
                >
                  <span
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-300 ${habilitado ? "translate-x-6" : "translate-x-1"}`}
                  />
                </div>
                <div className="flex items-center">
                  <span className="text-sm font-medium text-gray-700">
                    {habilitado ? "Habilitado" : "Inhabilitado"}
                  </span>
                </div>
              </div>
              
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Descripción del Contrato *</label>
              <textarea
                rows={5}
                placeholder="Describe detalladamente el contrato..."
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none shadow-sm"
              />
            </div>
          </div>
        </div>

        <div className="flex justify-end space-x-4 mt-8">
          <button
            onClick={volverAContratos}
            className="px-8 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 font-medium shadow"
          >
            Cancelar
          </button>
          <button className="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium flex items-center gap-2 shadow-md">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            Registrar Contrato
          </button>
        </div>
      </div>
    </div>
  )
}
