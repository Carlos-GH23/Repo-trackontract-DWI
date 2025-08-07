import { useState } from "react"
import { Link } from "react-router-dom"
import { FaEye, FaEdit, FaTrash } from "react-icons/fa"

const contratos = [
    {
        nombre: "Contrato A",
        cliente: "Empresa X",
        categoria: "Software",
        vencimiento: "2025-12-31",
        descripcion: "Contrato para el desarrollo de un sistema de gestión.",
        estado: "Activo",
    },
    {
        nombre: "Contrato B",
        cliente: "Corporación Y",
        categoria: "Consultoría",
        vencimiento: "2025-08-15",
        descripcion: "Asesoría técnica en procesos internos.",
        estado: "Vencido",
    },
    {
        nombre: "Contrato C",
        cliente: "Startup Z",
        categoria: "Soporte",
        vencimiento: "2026-01-10",
        descripcion: "Mantenimiento técnico mensual.",
        estado: "Activo",
    },
]

export default function Contratos() {
    const [mostrarRegistro, setMostrarRegistro] = useState(false)

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex items-center mb-8">
                <div className="flex items-center">
                    <div className="bg-blue-100 p-3 rounded-lg mr-4">
                        <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                    </div>
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">Visualización de Contratos</h1>
                        <p className="text-sm text-gray-600">Panel de Administración - Gestión de Contratos</p>
                    </div>
                </div>
            </div>

            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Contratos Registrados</h1>
                <Link
                    to="/admin/contratos/add"
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors duration-200 flex items-center gap-2"
                >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                    </svg>
                    Registrar Contrato
                </Link>
            </div>

            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                {contratos.map((contrato, idx) => (
                    <div
                        key={idx}
                        className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                    >
                        <div className="p-6">
                            <div className="flex justify-between items-start mb-2">
                                <h2 className="text-lg font-semibold text-gray-800">{contrato.nombre}</h2>
                                <span
                                    className={`text-xs px-2 py-1 rounded-full font-medium ${contrato.estado === "Activo"
                                        ? "bg-green-100 text-green-800"
                                        : "bg-red-100 text-red-800"
                                        }`}
                                >
                                    {contrato.estado}
                                </span>
                            </div>

                            <p className="text-sm text-gray-600 mb-1"><strong>Cliente:</strong> {contrato.cliente}</p>
                            <p className="text-sm text-gray-600 mb-1"><strong>Categoría:</strong> {contrato.categoria}</p>
                            <p className="text-sm text-gray-600 mb-1"><strong>Vencimiento:</strong> {contrato.vencimiento}</p>
                            <p className="text-sm text-gray-600 mb-3"><strong>Descripción:</strong> {contrato.descripcion}</p>

                            <div className="flex gap-3">
                                <button
                                    title="Ver Detalle"
                                    className="p-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg"
                                >
                                    <FaEye />
                                </button>
                                <Link
                                    to={`/admin/contratos/edit/${idx}`}
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
