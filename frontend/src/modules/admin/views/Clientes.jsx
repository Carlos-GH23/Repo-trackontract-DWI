import { FaEye, FaEdit, FaTrash } from "react-icons/fa";
import { Link } from "react-router-dom";

const clientes = [
  {
    nombreEmpresa: "Tech Solutions",
    razonSocial: "Tech Solutions S.A. de C.V.",
    nombreRepresentante: "Carlos",
    apellidoRepresentante: "Ramírez",
    correoRepresentante: "carlos@techsolutions.com",
    telefonoRepresentante: "5551234567",
    estado: "Activo",
  },
  {
    nombreEmpresa: "EcoServicios",
    razonSocial: "EcoServicios MX",
    nombreRepresentante: "Lucía",
    apellidoRepresentante: "Torres",
    correoRepresentante: "lucia@ecoservicios.mx",
    telefonoRepresentante: "5559876543",
    estado: "Inactivo",
  },
];

export default function Clientes() {
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
            <h1 className="text-2xl font-bold text-gray-900">Clientes Registrados</h1>
            <p className="text-sm text-gray-600">Panel de Administración - Gestión de Clientes</p>
          </div>
        </div>

        <Link
          to="/admin/clientes/add"
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors duration-200 flex items-center gap-2 shadow-md"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Registrar Cliente
        </Link>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {clientes.map((cliente, idx) => (
          <div
            key={idx}
            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
          >
            <div className="p-6">
              <div className="flex justify-between items-start mb-3">
                <h2 className="text-lg font-semibold text-gray-800">{cliente.nombreEmpresa}</h2>
                <span
                  className={`text-xs px-2 py-1 rounded-full font-medium ${
                    cliente.estado === "Activo"
                      ? "bg-green-100 text-green-800"
                      : "bg-red-100 text-red-800"
                  }`}
                >
                  {cliente.estado}
                </span>
              </div>

              <div className="space-y-2 mb-4 text-sm text-gray-600">
                <p>
                  <strong>Razón Social:</strong> {cliente.razonSocial}
                </p>
                <p>
                  <strong>Representante:</strong> {cliente.nombreRepresentante} {cliente.apellidoRepresentante}
                </p>
                <p>
                  <strong>Correo:</strong> {cliente.correoRepresentante}
                </p>
                <p>
                  <strong>Teléfono:</strong> {cliente.telefonoRepresentante}
                </p>
              </div>

              <div className="flex gap-3">
                <button
                  title="Ver Detalles"
                  className="p-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg"
                >
                  <FaEye />
                </button>
                <Link
                  to={`/admin/clientes/edit/${idx}`}
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
  );
}
