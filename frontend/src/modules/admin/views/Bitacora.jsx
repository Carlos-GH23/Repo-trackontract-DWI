import React from "react";
import { FaBook } from "react-icons/fa";

export default function Bitacora() {
  const logs = [
    { usuario: "Carlos", metodo: "POST", ruta: "/admin/abogados", rol: "Administrador" },
    { usuario: "Ana", metodo: "PUT", ruta: "/abogado/categorias/45", rol: "Abogado" },
    { usuario: "Luis", metodo: "DELETE", ruta: "/cliente/contrato/12", rol: "Usuario" },
    { usuario: "María", metodo: "GET", ruta: "/cliente/contratos", rol: "Usuario" }
  ];

  return (
    <div className="min-h-screen bg-white p-6">
      {/* Header */}
      <div className="flex items-center space-x-4 mb-8">
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

      {/* Tabla */}
      <div className="overflow-x-auto">
        <table className="w-full border border-black border-collapse rounded-lg overflow-hidden">
          <thead className="bg-gray-100">
            <tr>
              <th className="px-4 py-2 text-left border border-black">Usuario</th>
              <th className="px-4 py-2 text-left border border-black">Petición</th>
              <th className="px-4 py-2 text-left border border-black">Rol</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log, i) => (
              <tr key={i} className="hover:bg-gray-50">
                <td className="px-4 py-2 border border-black">{log.usuario || "Anónimo"}</td>
                <td className="px-4 py-2 border border-black">
                  {log.metodo} <span className="text-gray-500">{log.ruta}</span>
                </td>
                <td className="px-4 py-2 border border-black">{log.rol || "Sin rol"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
