import { FaEye, FaEdit, FaTrash } from "react-icons/fa"
import { Link } from "react-router-dom"

const abogados = [
  {
    nombre: "Luis",
    apellidos: "Ramírez Salgado",
    correo: "luis.ramirez@bufetejuridico.com",
    telefono: "5512345678",
    estado: "Activo",
  },
  {
    nombre: "Carla",
    apellidos: "Hernández Soto",
    correo: "carla.hernandez@legalmex.com",
    telefono: "5543219876",
    estado: "Activo",
  },
  {
    nombre: "Jorge",
    apellidos: "Mendoza Torres",
    correo: "jorge.mendoza@defensoresmx.com",
    telefono: "5523456789",
    estado: "Inactivo",
  },
  {
    nombre: "Paola",
    apellidos: "Luna Rivera",
    correo: "paola.luna@lexjuris.com",
    telefono: "5534567890",
    estado: "Activo",
  },
]

export default function Usuarios() {
  return (
    <div className="min-h-screen bg-white p-6">
      <div className="flex justify-between items-center mb-8">
        <div className="flex items-center space-x-4">
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
            <h1 className="text-2xl font-bold text-gray-900">Abogados Registrados</h1>
            <p className="text-sm text-gray-600">
              Panel de Administración - Gestión de Abogados
            </p>
          </div>
        </div>

        <Link
          to="/admin/abogados/add"
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors duration-200 flex items-center gap-2 shadow-md"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Registrar Abogado
        </Link>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {abogados.map((abogado, idx) => (
          <div
            key={idx}
            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
          >
            <div className="p-6">
              <div className="flex justify-between items-start mb-3">
                <h2 className="text-lg font-semibold text-gray-800">
                  {abogado.nombre} {abogado.apellidos}
                </h2>
                <span
                  className={`text-xs px-2 py-1 rounded-full font-medium ${
                    abogado.estado === "Activo"
                      ? "bg-green-100 text-green-800"
                      : "bg-red-100 text-red-800"
                  }`}
                >
                  {abogado.estado}
                </span>
              </div>

              <div className="space-y-2 mb-4">
                <div className="flex items-center text-sm text-gray-600">
                  <svg
                    className="w-4 h-4 mr-2 text-blue-500"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                    />
                  </svg>
                  {abogado.correo}
                </div>
                <div className="flex items-center text-sm text-gray-600">
                  <svg
                    className="w-4 h-4 mr-2 text-green-500"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                    />
                  </svg>
                  {abogado.telefono}
                </div>
              </div>

              <div className="flex gap-3">
                <button
                  title="Ver Detalles"
                  className="p-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg"
                >
                  <FaEye />
                </button>
                <Link
                  to={`/admin/abogados/edit/${idx}`}
                  className="p-2 bg-yellow-400 hover:bg-yellow-500 text-white rounded-lg"
                  title="Editar"
                >
                  <FaEdit />
                </Link>
                <button
                  title="Eliminar"
                  className="p-2 bg-red-500 hover:bg-red-600 text-white rounded-lg"
                >
                  <FaTrash />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
