import React, { useState } from "react"
import { useNavigate } from "react-router-dom"

export default function RegistroCliente() {
    const [clienteActivo, setClienteActivo] = useState(true)
    const navigate = useNavigate()

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="max-w-6xl mx-auto"> {/* Contenedor más ancho */}
                <div className="flex items-center mb-8 justify-between">
                    <div className="flex items-center space-x-4">
                        <button
                            onClick={() => navigate("/admin/clientes")}
                            className="mr-2 p-2 hover:bg-gray-100 rounded-lg transition-colors duration-200"
                        >
                            <svg className="w-6 h-6 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                            </svg>
                        </button>
                        <div className="bg-blue-100 p-3 rounded-lg">
                            <svg className="w-7 h-7 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M17 20h5v-2a4 4 0 00-3-3.87M9 20H4v-2a4 4 0 013-3.87m9-9a4 4 0 11-8 0 4 4 0 018 0zm6 4a4 4 0 11-8 0 4 4 0 018 0zM9 7a4 4 0 11-8 0 4 4 0 018 0z"
                                />
                            </svg>
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">Registro de Cliente</h1>
                            <p className="text-sm text-gray-600">Panel de Administración - Gestión de Clientes</p>
                        </div>
                    </div>
                </div>

                <form className="space-y-6">
                    <div className="bg-white shadow-lg rounded-xl border border-gray-200">
                        <div className="px-6 py-4 border-b border-gray-200">
                            <h3 className="text-lg font-semibold text-gray-800 flex items-center">
                                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path
                                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                    />
                                </svg>
                                Datos del Cliente
                            </h3>
                        </div>

                        {/* Inputs en 2 columnas, pero anchos */}
                        <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4">
                            <div className="w-full">
                                <input placeholder="Nombre de la empresa *" className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg" />
                            </div>
                            <div className="w-full">
                                <input placeholder="Razón social *" className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg" />
                            </div>
                            <div className="w-full">
                                <input placeholder="Nombre del representante *" className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg" />
                            </div>
                            <div className="w-full">
                                <input placeholder="Apellido del representante *" className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg" />
                            </div>
                            <div className="w-full">
                                <input placeholder="Correo del representante *" type="email" className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg" />
                            </div>
                            <div className="w-full">
                                <input placeholder="Teléfono del representante *" type="tel" className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg" />
                            </div>
                        </div>
                    </div>

                    <div className="bg-white shadow-lg rounded-xl border border-gray-200">
                        <div className="px-6 py-4 border-b border-gray-200">
                            <h3 className="text-lg font-semibold text-gray-800 flex items-center">
                                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path
                                        d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                    />
                                    <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} />
                                </svg>
                                Estado del Cliente
                            </h3>
                        </div>

                        <div className="p-6">
                            <div className="flex items-center space-x-3">
                                <button
                                    type="button"
                                    onClick={() => setClienteActivo(!clienteActivo)}
                                    className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${clienteActivo ? "bg-green-600" : "bg-gray-300"
                                        }`}
                                >
                                    <span
                                        className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${clienteActivo ? "translate-x-6" : "translate-x-1"
                                            }`}
                                    />
                                </button>
                                <label className="text-sm font-medium text-gray-700">
                                    {clienteActivo ? "Habilitado" : "Inhabilitado"}
                                </label>
                            </div>
                        </div>
                    </div>

                    <div className="flex justify-end space-x-4">
                        <button
                            type="button"
                            onClick={() => navigate("/admin/clientes")}
                            className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors duration-200"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
                        >
                            Registrar Cliente
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}
