import React, { useState } from "react"
import { useNavigate } from "react-router-dom"

export default function RegistroCategoria() {
  const [categoriaHabilitada, setCategoriaHabilitada] = useState(true)
  const navigate = useNavigate()

  const volverACategorias = () => {
    navigate("/admin/categorias")
  }

  return (
    <div className="min-h-screen bg-white p-6">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center mb-6">
          <button
            onClick={volverACategorias}
            className="mr-4 p-2 hover:bg-gray-100 rounded-lg transition duration-200 shadow-sm"
          >
            <svg className="w-4 h-4 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-blue-100 text-blue-600 rounded-lg flex items-center justify-center shadow-sm">
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  d="M3 7a2 2 0 012-2h5l2 2h7a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2V7z"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                />
              </svg>
            </div>
            <h1 className="text-2xl font-bold text-gray-900">Registro de Nueva Categoría</h1>
          </div>
        </div>

        <form className="space-y-6">
          <div className="bg-white shadow-xl rounded-xl border border-blue-200">
            <div className="px-6 py-4 border-b border-blue-100">
              <h3 className="text-lg font-semibold text-gray-800 flex items-center">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path d="M7 3h5c.5 0 1 .2 1.4.6l7 7a2 2 0 010 2.8l-7 7a2 2 0 01-2.8 0l-7-7A2 2 0 013 12V7a4 4 0 014-4z" strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} />
                </svg>
                Datos de la Categoría
              </h3>
            </div>
            <div className="p-6 grid grid-cols-1 gap-4">
              <input placeholder="Nombre de la Categoría *" className="w-full px-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500" />
              <textarea
                rows={4}
                placeholder="Descripción de la categoría *"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg resize-none shadow-sm focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="bg-white shadow-xl rounded-xl border border-blue-200">
            <div className="px-6 py-4 border-b border-blue-100">
              <h3 className="text-lg font-semibold text-gray-800 flex items-center">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} />
                </svg>
                Estado de la Categoría
              </h3>
            </div>
            <div className="p-6">
              <div className="flex items-center space-x-3">
                <button
                  type="button"
                  onClick={() => setCategoriaHabilitada(!categoriaHabilitada)}
                  className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${
                    categoriaHabilitada ? "bg-green-600" : "bg-gray-300"
                  }`}
                >
                  <span
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                      categoriaHabilitada ? "translate-x-6" : "translate-x-1"
                    }`}
                  />
                </button>
                <label className="text-sm font-medium text-gray-700">
                  {categoriaHabilitada ? "Habilitada" : "Inhabilitada"}
                </label>
              </div>
            </div>
          </div>

          <div className="flex justify-end space-x-4">
            <button
              type="button"
              onClick={volverACategorias}
              className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200 shadow"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition duration-200 shadow-md"
            >
              Guardar Categoría
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
