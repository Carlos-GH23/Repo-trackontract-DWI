import React, { useState, useEffect } from "react";
import { FaEye, FaEdit, FaTrash } from "react-icons/fa";
import { Link } from "react-router-dom";
import Swal from "sweetalert2";

export default function Contratos() {
  const [contratos, setContratos] = useState([]);
  const [loading, setLoading] = useState(true);

  const [filtroStatus, setFiltroStatus] = useState(null); // null = todos, true=activos, false=inactivos
  const [filtroNombre, setFiltroNombre] = useState("");

  const fetchContratos = async (statusFilter = filtroStatus) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("accessToken");
      let url = "";

      if (statusFilter !== null) {
        url = `http://localhost:8080/contracts/all/status/${statusFilter}`;
      } else {
        url = "http://localhost:8080/contracts/all";
      }

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) throw new Error("Error al cargar contratos");

      const data = await response.json();

      if (Array.isArray(data)) {
        setContratos(data);
      } else if (data.result && Array.isArray(data.result)) {
        setContratos(data.result);
      } else {
        setContratos([]);
      }

    } catch (error) {
      console.error(error);
      Swal.fire("Error", error.message, "error");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchContratos();
  }, [filtroStatus]);

  const handleEliminar = async (id) => {
    const result = await Swal.fire({
      title: "¿Estás seguro?",
      text: "No podrás revertir esta acción",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
    });

    if (!result.isConfirmed) return;

    try {
      const token = localStorage.getItem("accessToken");
      const response = await fetch(`http://localhost:8080/contracts/delete/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Error al eliminar contrato");
      }

      Swal.fire("Eliminado", "El contrato ha sido eliminado.", "success");
      setContratos((prev) => prev.filter((cont) => cont.id !== id));
    } catch (error) {
      console.error(error);
      Swal.fire("Error", error.message, "error");
    }
  };

  const mostrarDetalleContrato = (contrato) => {
    Swal.fire({
      title: contrato.name,
      html: `
        <div class="text-left space-y-3">
          <div class="flex items-center justify-between">
            <span class="font-semibold text-gray-700">Estado:</span>
            <span class="px-2 py-1 text-xs rounded-full font-medium ${
              contrato.status ? "bg-green-100 text-green-800" : "bg-gray-200 text-gray-700"
            }">
              ${contrato.status ? "Activo" : "Inactivo"}
            </span>
          </div>
          <div>
            <span class="font-semibold text-gray-700">Cliente:</span>
            <p class="text-gray-600 mt-1">${contrato.client_id?.name || "N/A"}</p>
          </div>
          <div>
            <span class="font-semibold text-gray-700">Categoría:</span>
            <p class="text-gray-600 mt-1">${contrato.category_id?.name || "N/A"}</p>
          </div>
          <div>
            <span class="font-semibold text-gray-700">Fecha de Vencimiento:</span>
            <p class="text-gray-600 mt-1">${contrato.due_date ? new Date(contrato.due_date).toLocaleDateString() : "N/A"}</p>
          </div>
          <div>
            <span class="font-semibold text-gray-700">Descripción:</span>
            <p class="text-gray-600 mt-1">${contrato.description || "Sin descripción"}</p>
          </div>
        </div>
      `,
      icon: "info",
      confirmButtonText: "Cerrar",
      confirmButtonColor: "#3B82F6",
      width: "500px",
      customClass: {
        popup: "rounded-xl",
        title: "text-xl font-bold text-gray-800",
        htmlContainer: "text-left"
      }
    });
  };

  if (loading) return <p className="p-6">Cargando contratos...</p>;

  // Filtrado local por nombre
  const contratosFiltrados = contratos.filter((cont) =>
      cont.name.toLowerCase().includes(filtroNombre.toLowerCase())
  );

  return (
      <div className="min-h-screen bg-white p-6">
        <div className="flex justify-between items-center mb-6">
          <div className="flex items-center space-x-4">
            <div className="bg-blue-100 p-3 rounded-lg">{/* Icono si quieres */}</div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Contratos Registrados</h1>
              <p className="text-sm text-gray-600">Panel de Administración - Gestión de Contratos</p>
            </div>
          </div>

          <Link
              to="/admin/contratos/add"
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors duration-200 flex items-center gap-2 shadow-md"
          >
            Registrar Contrato
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

          <input
              type="text"
              placeholder="Buscar por nombre..."
              value={filtroNombre}
              onChange={(e) => setFiltroNombre(e.target.value)}
              className="border border-gray-300 rounded-lg px-4 py-2 w-full max-w-xs focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Lista de contratos */}
        {contratosFiltrados.length === 0 ? (
            <p className="text-center text-gray-500 text-lg">No hay contratos registrados.</p>
        ) : (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {contratosFiltrados.map((contrato) => (
                  <div
                      key={contrato.id}
                      className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                  >
                    <div className="p-6">
                      <div className="flex justify-between items-start mb-2">
                        <h2 className="text-lg font-semibold text-gray-800">{contrato.name}</h2>
                        <span
                            className={`text-xs px-2 py-1 rounded-full font-medium ${
                                contrato.status ? "bg-green-100 text-green-800" : "bg-gray-200 text-gray-700"
                            }`}
                        >
                          {contrato.status ? "Activo" : "Inactivo"}
                        </span>
                      </div>

                      <div className="text-sm text-gray-600 mb-4 space-y-1">
                        <p><strong>Cliente:</strong> {contrato.client_id?.name || "N/A"}</p>
                        <p><strong>Categoría:</strong> {contrato.category_id?.name || "N/A"}</p>
                        <p><strong>Vencimiento:</strong> {contrato.due_date ? new Date(contrato.due_date).toLocaleDateString() : "N/A"}</p>
                        <p><strong>Descripción:</strong> {contrato.description || "Sin descripción"}</p>
                      </div>

                      <div className="flex gap-3">
                        <button
                            onClick={() => mostrarDetalleContrato(contrato)}
                            className="p-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg flex items-center justify-center"
                            title="Ver Detalle"
                        >
                          <FaEye />
                        </button>

                        <Link
                            to={`/admin/contratos/edit/${contrato.id}`}
                            className="p-2 bg-yellow-400 hover:bg-yellow-500 text-white rounded-lg flex items-center justify-center"
                            title="Editar"
                        >
                          <FaEdit />
                        </Link>

                        <button
                            onClick={() => handleEliminar(contrato.id)}
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
        )}
      </div>
  );
}
