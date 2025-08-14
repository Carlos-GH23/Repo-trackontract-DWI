import { useState, useEffect } from "react";
import { FaEye, FaEdit, FaTrash } from "react-icons/fa";
import { Link } from "react-router-dom";
import Swal from "sweetalert2";

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [usuarioSeleccionado, setUsuarioSeleccionado] = useState(null);
  const [filtroStatus, setFiltroStatus] = useState(null); // null = todos, true = activos, false = inactivos
  const [filtroNombre, setFiltroNombre] = useState("");

  const fetchUsuarios = async (statusFilter = filtroStatus) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("accessToken");
      let url = "";

      // Siempre filtrar por rol ABOGADO
      if (statusFilter !== null) {
        url = `http://localhost:8080/users/by-role/ABOGADO`;
      } else {
        url = "http://localhost:8080/users/by-role/ABOGADO";
      }

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error("Error al cargar usuarios");
      }

      const data = await response.json();

      // Filtrar por estado si se especifica
      let usuariosFiltrados = [];
      if (Array.isArray(data)) {
        usuariosFiltrados = data;
      } else if (data.result && Array.isArray(data.result)) {
        usuariosFiltrados = data.result;
      }

      // Aplicar filtro de estado localmente si es necesario
      if (statusFilter !== null) {
        usuariosFiltrados = usuariosFiltrados.filter(user => user.status === statusFilter);
      }

      setUsuarios(usuariosFiltrados);
    } catch (error) {
      console.error("Error al obtener usuarios:", error);
      setUsuarios([]);
    } finally {
      setLoading(false);
    }
  };

  async function eliminarUsuario(id) {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No tienes autorización para eliminar usuarios",
      });
      return;
    }

    const confirmResult = await Swal.fire({
      title: "¿Estás seguro?",
      text: "Esta acción no se puede deshacer. ¿Quieres eliminar al usuario?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
    });

    if (!confirmResult.isConfirmed) return;

    try {
      const response = await fetch(`http://localhost:8080/users/delete/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const text = await response.text();
        let json;
        try {
          json = JSON.parse(text);
        } catch {
          json = null;
        }

        return Swal.fire({
          icon: "error",
          title: "Error eliminando usuario",
          text: json?.text || `Status: ${response.status}`,
        });
      }

      Swal.fire({
        icon: "success",
        title: "Usuario eliminado",
        text: "El usuario fue eliminado correctamente",
      }).then(() => {
        // Actualiza la lista para reflejar que se eliminó sin recargar
        setUsuarios(prevUsuarios => prevUsuarios.filter(u => u.id !== id));
      });
    } catch (error) {
      console.error("Error eliminando usuario:", error);
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Ocurrió un error inesperado al eliminar el usuario",
      });
    }
  }




  useEffect(() => {
    fetchUsuarios();
  }, [filtroStatus]);

  if (loading) return <p className="p-6">Cargando usuarios...</p>;

  // Filtrado local por nombre
  const usuariosFiltrados = usuarios.filter((usuario) =>
      usuario.name?.toLowerCase().includes(filtroNombre.toLowerCase())
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
              <h1 className="text-2xl font-bold text-gray-900">Abogados Registrados</h1>
              <p className="text-sm text-gray-600">Panel de Administración - Gestión de Abogados</p>
            </div>
          </div>

          <Link
              to="/admin/abogados/add"
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
            Registrar Abogado
          </Link>
        </div>

        {/* Filtros */}
        <div className="mb-6 flex flex-col sm:flex-row gap-4 sm:items-center sm:justify-between">
          <div className="flex items-center gap-4">
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
            
            {/* Indicador de rol */}
            <div className="bg-blue-100 text-blue-800 px-3 py-2 rounded-lg text-sm font-medium">
              <span className="flex items-center gap-2">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                Solo Abogados
              </span>
            </div>
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

        {/* Lista de usuarios */}
        {usuariosFiltrados.length === 0 ? (
            <p className="text-center text-gray-500 text-lg">No hay abogados registrados.</p>
        ) : (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {usuariosFiltrados.map((usuario) => (
                  <div
                      key={usuario.id}
                      className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                  >
                    <div className="p-6">
                      <div className="flex justify-between items-start mb-3">
                        <h2 className="text-lg font-semibold text-gray-800">{usuario.name}</h2>
                        <span
                            className={`text-xs px-2 py-1 rounded-full font-medium ${
                                usuario.status ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                            }`}
                        >
                          {usuario.status ? "Activo" : "Inactivo"}
                        </span>
                      </div>

                      <div className="space-y-2 mb-4 text-sm text-gray-600">
                        <p>
                          <strong>Correo:</strong> {usuario.email}
                        </p>
                        <p>
                          <strong>Rol:</strong> {usuario.role}
                        </p>
                      </div>

                      <div className="flex gap-3">
                        <button
                            title="Ver Detalles"
                            className="p-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg"
                            onClick={() => setUsuarioSeleccionado(usuario)}
                        >
                          <FaEye />
                        </button>

                        <Link
                            to={`/admin/abogados/edit/${usuario.id}`}
                            className="p-2 bg-yellow-400 hover:bg-yellow-500 text-white rounded-lg"
                            title="Editar"
                        >
                          <FaEdit />
                        </Link>

                        <button
                            title="Eliminar"
                            className="p-2 bg-red-500 hover:bg-red-600 text-white rounded-lg"
                            onClick={() => eliminarUsuario(usuario.id)}
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
        {usuarioSeleccionado && (
            <div
                className="fixed inset-0 flex items-center justify-center z-50"
                style={{
                  backgroundColor: "rgba(0,0,0,0.3)",
                  backdropFilter: "blur(8px)",
                  WebkitBackdropFilter: "blur(8px)",
                }}
                onClick={() => setUsuarioSeleccionado(null)}
            >
              <div
                  className="bg-white rounded-xl shadow-xl max-w-md w-full p-6 relative animate-fadeIn"
                  onClick={(e) => e.stopPropagation()}
              >
                <h2 className="text-2xl font-bold mb-4 text-gray-900">{usuarioSeleccionado.name}</h2>
                <p className="mb-2">
                  <strong>Correo:</strong> {usuarioSeleccionado.email}
                </p>
                <p className="mb-2">
                  <strong>Rol:</strong> {usuarioSeleccionado.role}
                </p>
                <p className="mb-2">
                  <strong>Estado:</strong> {usuarioSeleccionado.status ? "Activo" : "Inactivo"}
                </p>

                <button
                    className="mt-6 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
                    onClick={() => setUsuarioSeleccionado(null)}
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
