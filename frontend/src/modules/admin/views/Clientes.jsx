import { useState, useEffect, useRef } from "react";
import { FaEye, FaEdit, FaTrash } from "react-icons/fa";
import { Link } from "react-router-dom";
import Swal from "sweetalert2";

export default function Clientes() {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [clienteSeleccionado, setClienteSeleccionado] = useState(null);

  const [filtroStatus, setFiltroStatus] = useState(null); // null = todos, true=activos, false=inactivos
  const [filtroNombre, setFiltroNombre] = useState("");

  const fetchClientes = async (statusFilter = filtroStatus) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("accessToken");
      let url = "";

      if (statusFilter !== null) {
        url = `http://localhost:8080/clients/all/status/${statusFilter}`;
      } else {
        url = "http://localhost:8080/clients/all";
      }

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error("Error al cargar clientes");
      }

      const data = await response.json();

      if (Array.isArray(data)) {
        setClientes(data);
      } else if (data.result && Array.isArray(data.result)) {
        setClientes(data.result);
      } else {
        setClientes([]);
      }
    } catch (error) {
      console.error("Error al obtener clientes:", error);
      setClientes([]);
    } finally {
      setLoading(false);
    }
  };

  async function eliminarCliente(id) {
    const token = localStorage.getItem("accessToken");

    const confirm = await Swal.fire({
      title: "¿Estás seguro de eliminar este cliente?",
      text: "Esta acción no se puede deshacer.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
    });

    if (!confirm.isConfirmed) return;

    try {
      const response = await fetch(`http://localhost:8080/clients/delete/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Error al eliminar cliente");
      }

      Swal.fire("Eliminado", "El cliente fue eliminado correctamente.", "success");
      setClientes((prev) => prev.filter((cliente) => cliente.id !== id));
    } catch (error) {
      console.error("Error eliminando cliente:", error);
      Swal.fire("Error", error.message, "error");
    }
  }

  useEffect(() => {
    fetchClientes();
  }, [filtroStatus]);

  if (loading) return <p className="p-6">Cargando clientes...</p>;

  // Filtrado local por nombre (sin llamar a la API)
  const clientesFiltrados = clientes.filter((cliente) =>
      cliente.name.toLowerCase().includes(filtroNombre.toLowerCase())
  );

  return (
      <div className="min-h-screen bg-white p-6 relative">
        {/* Header */}
        <div className="flex justify-between items-center mb-6">
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
            <svg
                className="w-4 h-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            Registrar Cliente
          </Link>
        </div>

        {/* Filtros */}
        <div className="mb-6 flex flex-col sm:flex-row gap-4 sm:items-center sm:justify-between">
          <div className="flex gap-3">
            <button
                onClick={() => setFiltroStatus(null)}
                className={`px-4 py-2 rounded-lg font-semibold ${
                    filtroStatus === null ? "bg-blue-600 text-white" : "bg-gray-200 text-gray-800"
                }`}
            >
              Todos
            </button>
            <button
                onClick={() => setFiltroStatus(true)}
                className={`px-4 py-2 rounded-lg font-semibold ${
                    filtroStatus === true ? "bg-green-600 text-white" : "bg-gray-200 text-gray-800"
                }`}
            >
              Activos
            </button>
            <button
                onClick={() => setFiltroStatus(false)}
                className={`px-4 py-2 rounded-lg font-semibold ${
                    filtroStatus === false ? "bg-red-600 text-white" : "bg-gray-200 text-gray-800"
                }`}
            >
              Inactivos
            </button>
          </div>

          {/* Input de búsqueda */}
          <input
              type="text"
              placeholder="Buscar por nombre..."
              value={filtroNombre}
              onChange={(e) => setFiltroNombre(e.target.value)}
              className="border border-gray-300 rounded-lg px-4 py-2 w-full max-w-xs focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Lista de clientes */}
        {clientesFiltrados.length === 0 ? (
            <p className="text-center text-gray-500 text-lg">No hay clientes registrados.</p>
        ) : (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {clientesFiltrados.map((cliente) => (
                  <div
                      key={cliente.id}
                      className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                  >
                    <div className="p-6">
                      <div className="flex justify-between items-start mb-3">
                        <h2 className="text-lg font-semibold text-gray-800">{cliente.name}</h2>
                        <span
                            className={`text-xs px-2 py-1 rounded-full font-medium ${
                                cliente.status ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                            }`}
                        >
                    {cliente.status ? "Activo" : "Inactivo"}
                  </span>
                      </div>

                      <div className="space-y-2 mb-4 text-sm text-gray-600">
                        <p>
                          <strong>Razón Social:</strong> {cliente.business_name}
                        </p>
                        <p>
                          <strong>Representante:</strong> {cliente.representative_name} {cliente.representative_surnames}
                        </p>
                        <p>
                          <strong>Correo:</strong> {cliente.email}
                        </p>
                        <p>
                          <strong>Teléfono:</strong> {cliente.phone}
                        </p>
                      </div>

                      <div className="flex gap-3">
                        <button
                            title="Ver Detalles"
                            className="p-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg"
                            onClick={() => setClienteSeleccionado(cliente)}
                        >
                          <FaEye />
                        </button>

                        <Link
                            to={`/admin/clientes/edit/${cliente.id}`}
                            className="p-2 bg-yellow-400 hover:bg-yellow-500 text-white rounded-lg"
                            title="Editar"
                        >
                          <FaEdit />
                        </Link>

                        <button
                            title="Eliminar"
                            className="p-2 bg-red-500 hover:bg-red-600 text-white rounded-lg"
                            onClick={() => eliminarCliente(cliente.id)}
                        >
                          <FaTrash />
                        </button>
                      </div>
                    </div>
                  </div>
              ))}
            </div>
        )}

        {/* Modal con fondo blur */}
        {clienteSeleccionado && (
            <div
                className="fixed inset-0 flex items-center justify-center z-50"
                style={{
                  backgroundColor: "rgba(0,0,0,0.3)",
                  backdropFilter: "blur(8px)",
                  WebkitBackdropFilter: "blur(8px)",
                }}
                onClick={() => setClienteSeleccionado(null)}
            >
              <div
                  className="bg-white rounded-xl shadow-xl max-w-md w-full p-6 relative animate-fadeIn"
                  onClick={(e) => e.stopPropagation()}
              >
                <h2 className="text-2xl font-bold mb-4 text-gray-900">{clienteSeleccionado.name}</h2>
                <p className="mb-2">
                  <strong>Razón Social:</strong> {clienteSeleccionado.business_name}
                </p>
                <p className="mb-2">
                  <strong>Representante:</strong> {clienteSeleccionado.representative_name} {clienteSeleccionado.representative_surnames}
                </p>
                <p className="mb-2">
                  <strong>Correo:</strong> {clienteSeleccionado.email}
                </p>
                <p className="mb-2">
                  <strong>Teléfono:</strong> {clienteSeleccionado.phone}
                </p>
                <p className="mb-2">
                  <strong>Estado:</strong> {clienteSeleccionado.status ? "Activo" : "Inactivo"}
                </p>

                <button
                    className="mt-6 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
                    onClick={() => setClienteSeleccionado(null)}
                >
                  Cerrar
                </button>
              </div>
            </div>
        )}

        <style>
          {`
          @keyframes fadeIn {
            from {opacity: 0;}
            to {opacity: 1;}
          }
          .animate-fadeIn {
            animation: fadeIn 0.25s ease-in-out;
          }
        `}
        </style>
      </div>
  );
}
