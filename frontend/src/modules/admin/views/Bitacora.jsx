import React, { useEffect, useState } from "react";
import { FaBook } from "react-icons/fa";

export default function Bitacora() {
  const [logs, setLogs] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [logsPerPage, setLogsPerPage] = useState(10);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [sortConfig, setSortConfig] = useState({ key: "id", direction: "desc" });

  const token = localStorage.getItem("accessToken");

  useEffect(() => {
    setLoading(true);
    fetch("http://localhost:8080/audit-logs/latest", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
        .then((res) => {
          if (!res.ok) throw new Error("Error al obtener bitácora");
          return res.json();
        })
        .then((data) => setLogs(data))
        .catch((err) => setError(err.message))
        .finally(() => setLoading(false));
  }, [token]);

  // Filtrar logs según búsqueda
  const filteredLogs = logs.filter(
      (log) =>
          log.usuario.toLowerCase().includes(searchTerm.toLowerCase()) ||
          log.rol.toLowerCase().includes(searchTerm.toLowerCase()) ||
          log.method.toLowerCase().includes(searchTerm.toLowerCase()) ||
          log.path.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Ordenar logs
  const sortedLogs = [...filteredLogs].sort((a, b) => {
    if (!sortConfig.key) return 0;
    const aVal = a[sortConfig.key] ?? "";
    const bVal = b[sortConfig.key] ?? "";
    if (aVal < bVal) return sortConfig.direction === "asc" ? -1 : 1;
    if (aVal > bVal) return sortConfig.direction === "asc" ? 1 : -1;
    return 0;
  });

  // Paginación
  const indexOfLastLog = currentPage * logsPerPage;
  const indexOfFirstLog = indexOfLastLog - logsPerPage;
  const currentLogs = sortedLogs.slice(indexOfFirstLog, indexOfLastLog);
  const totalPages = Math.ceil(sortedLogs.length / logsPerPage);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  const requestSort = (key) => {
    let direction = "asc";
    if (sortConfig.key === key && sortConfig.direction === "asc") direction = "desc";
    setSortConfig({ key, direction });
  };

  if (loading) return <p className="text-center mt-10">Cargando bitácora...</p>;
  if (error) return <p className="text-center mt-10 text-red-500">{error}</p>;

  return (
      <div className="min-h-screen bg-white p-6">
        {/* Header */}
        <div className="flex items-center space-x-4 mb-4">
          <div className="bg-blue-100 p-3 rounded-lg">
            <FaBook className="w-7 h-7 text-blue-600" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Bitácora</h1>
            <p className="text-sm text-gray-600">
              Registro de actividades por usuario, petición y rol
            </p>
          </div>
        </div>

        {/* Filtro y selector de logs */}
        <div className="flex justify-between mb-4 flex-wrap gap-2">
          <input
              type="text"
              placeholder="Buscar..."
              className="border rounded px-2 py-1 flex-1 min-w-[200px]"
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setCurrentPage(1);
              }}
          />
          <div className="flex items-center space-x-2">
            <label>Mostrar:</label>
            <select
                value={logsPerPage}
                onChange={(e) => {
                  setLogsPerPage(Number(e.target.value));
                  setCurrentPage(1);
                }}
                className="border rounded px-2 py-1"
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={25}>25</option>
              <option value={50}>50</option>
            </select>
            <span>registros por página</span>
          </div>
        </div>

        {/* Tabla */}
        <div className="overflow-x-auto">
          <table className="w-full border border-black border-collapse rounded-lg overflow-hidden">
            <thead className="bg-gray-100">
            <tr>
              {["usuario", "method", "rol", "fecha"].map((col) => (
                  <th
                      key={col}
                      className="px-4 py-2 text-left border border-black cursor-pointer select-none"
                      onClick={() => requestSort(col)}
                  >
                    {col.toUpperCase()}
                    {sortConfig.key === col ? (sortConfig.direction === "asc" ? " ▲" : " ▼") : ""}
                  </th>
              ))}
            </tr>
            </thead>
            <tbody>
            {currentLogs.map((log) => (
                <tr key={log.id} className="hover:bg-gray-50">
                  <td className="px-4 py-2 border border-black">{log.usuario}</td>
                  <td className="px-4 py-2 border border-black">
                    {log.method} <span className="text-gray-500">{log.path}</span>
                  </td>
                  <td className="px-4 py-2 border border-black">{log.rol}</td>
                  <td className="px-4 py-2 border border-black">{log.fecha}</td>
                </tr>
            ))}
            {currentLogs.length === 0 && (
                <tr>
                  <td colSpan="4" className="text-center py-4">
                    No se encontraron registros
                  </td>
                </tr>
            )}
            </tbody>
          </table>
        </div>

        {/* Paginador */}
        <div className="flex justify-center mt-4 space-x-2 flex-wrap">
          <button
              onClick={() => currentPage > 1 && setCurrentPage(currentPage - 1)}
              className="px-3 py-1 border rounded bg-white hover:bg-gray-200"
              disabled={currentPage === 1}
          >
            Anterior
          </button>
          {Array.from({ length: totalPages }, (_, i) => (
              <button
                  key={i + 1}
                  onClick={() => paginate(i + 1)}
                  className={`px-3 py-1 border rounded ${
                      currentPage === i + 1 ? "bg-blue-600 text-white" : "bg-white"
                  }`}
              >
                {i + 1}
              </button>
          ))}
          <button
              onClick={() => currentPage < totalPages && setCurrentPage(currentPage + 1)}
              className="px-3 py-1 border rounded bg-white hover:bg-gray-200"
              disabled={currentPage === totalPages}
          >
            Siguiente
          </button>
        </div>
      </div>
  );
}
