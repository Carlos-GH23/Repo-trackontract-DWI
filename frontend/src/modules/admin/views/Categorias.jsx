import { FaEye, FaEdit, FaTrash } from "react-icons/fa"
import { Link } from "react-router-dom"

const categorias = [
  {
    nombre: "Servicios de Consultoría",
    descripcion: "Asesoría especializada para procesos empresariales.",
    estado: "Activo",
  },
  {
    nombre: "Mantenimiento Técnico",
    descripcion: "Soporte y mantenimiento de infraestructura tecnológica.",
    estado: "Inactivo",
  },
  {
    nombre: "Desarrollo de Software",
    descripcion: "Construcción de soluciones a medida.",
    estado: "Activo",
  },
]

export default function Categorias() {
  return (
    <div className="min-h-screen bg-white p-6">
      <div className="flex justify-between items-center mb-8">
        <div className="flex items-center space-x-4">
          <div className="bg-blue-100 p-3 rounded-lg">
            <svg
              className="w-6 h-6 text-blue-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M3 7a2 2 0 012-2h5l2 2h7a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2V7z"
              />
            </svg>
          </div>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Categorías Registradas</h1>
            <p className="text-sm text-gray-600">Panel de Administración - Gestión de Categorías</p>
          </div>
        </div>

        <Link
          to="/admin/categorias/add"
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors duration-200 flex items-center gap-2 shadow-md"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Registrar Categoría
        </Link>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {categorias.map((categoria, idx) => (
          <div
            key={idx}
            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
          >
            <div className="p-6">
              <div className="flex justify-between items-start mb-2">
                <h2 className="text-lg font-semibold text-gray-800">{categoria.nombre}</h2>
                <span
                  className={`text-xs px-2 py-1 rounded-full font-medium ${
                    categoria.estado === "Activo"
                      ? "bg-green-100 text-green-800"
                      : "bg-gray-200 text-gray-700"
                  }`}
                >
                  {categoria.estado}
                </span>
              </div>

              <p className="text-sm text-gray-600 mb-4">{categoria.descripcion}</p>

              <div className="flex gap-3">
                <button
                  title="Ver Detalle"
                  className="p-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg"
                >
                  <FaEye />
                </button>

                <Link
                  to={`/admin/categorias/edit/${idx}`}
                  className="p-2 bg-yellow-400 hover:bg-yellow-500 text-white rounded-lg flex items-center justify-center"
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
