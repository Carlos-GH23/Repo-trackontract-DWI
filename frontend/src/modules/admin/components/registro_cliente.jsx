import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

export default function RegistroCliente() {
    const [name, setName] = useState("");
    const [businessName, setBusinessName] = useState("");
    const [representativeName, setRepresentativeName] = useState("");
    const [representativeSurnames, setRepresentativeSurnames] = useState("");
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const [clienteActivo, setClienteActivo] = useState(true);
    const navigate = useNavigate();

    const volverAClientes = () => {
        navigate("/admin/clientes");
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const result = await Swal.fire({
            title: "¿Confirmas crear este cliente?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Sí, crear",
            cancelButtonText: "Cancelar",
        });

        if (!result.isConfirmed) {
            return;
        }

        const nuevoCliente = {
            name,
            business_name: businessName,
            representative_name: representativeName,
            representative_surnames: representativeSurnames,
            email,
            phone,
            status: true, // Siempre enviar true en el POST inicial
        };

        const token = localStorage.getItem("accessToken");

        try {
            const responseSave = await fetch("http://localhost:8080/clients/save", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(nuevoCliente),
            });

            if (!responseSave.ok) {
                const contentType = responseSave.headers.get("content-type");
                let errorMessage = "Error desconocido";

                if (contentType && contentType.includes("application/json")) {
                    const errorJson = await responseSave.json();
                    if (errorJson.text) {
                        errorMessage = errorJson.text;
                    } else {
                        errorMessage = Object.values(errorJson).join("\n");
                    }
                } else {
                    const errorText = await responseSave.text();
                    errorMessage = errorText || errorMessage;
                }

                throw new Error(errorMessage);
            }

            const data = await responseSave.json();
            const createdClient = data.result || data;

            // Si el cliente no debe estar activo, actualizar su status con PUT a /clients/change-status
            if (clienteActivo === false) {
                const responseStatus = await fetch("http://localhost:8080/clients/change-status", {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        id: createdClient.id,
                        name: createdClient.name,
                        business_name: createdClient.business_name,
                        representative_name: createdClient.representative_name,
                        representative_surnames: createdClient.representative_surnames,
                        email: createdClient.email,
                        phone: createdClient.phone,
                        status: clienteActivo, // Aquí sí envías false si es el caso
                    }),
                });

                if (!responseStatus.ok) {
                    const errorText = await responseStatus.text();
                    throw new Error("Error al cambiar estado: " + errorText);
                }
            }

            Swal.fire({
                title: "¡Cliente Creado!",
                html: `
                    <p>El cliente fue creado exitosamente.</p>
                    <p class="mt-2 text-sm text-gray-600">
                        <strong>Usuario automático creado:</strong><br/>
                        <strong>Email:</strong> ${email}<br/>
                        <strong>Contraseña:</strong> ${representativeName}123
                    </p>
                `,
                icon: "success",
                confirmButtonText: "Entendido",
                confirmButtonColor: "#3B82F6"
            });
            volverAClientes();
        } catch (error) {
            console.error(error);
            Swal.fire("Error", error.message, "error");
        }
    };

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="max-w-6xl mx-auto">
                <div className="flex items-center mb-8 justify-between">
                    <div className="flex items-center space-x-4">
                        <button
                            onClick={volverAClientes}
                            className="mr-2 p-2 hover:bg-gray-100 rounded-lg transition-colors duration-200"
                            aria-label="Volver a lista de clientes"
                        >
                            <svg
                                className="w-6 h-6 text-gray-800"
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                            >
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                            </svg>
                        </button>
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
                            <h1 className="text-2xl font-bold text-gray-900">Registro de Cliente</h1>
                            <p className="text-sm text-gray-600">Panel de Administración - Gestión de Clientes</p>
                        </div>
                    </div>
                </div>

                <form className="space-y-6" onSubmit={handleSubmit}>
                    <div className="bg-white shadow-lg rounded-xl border border-gray-200">
                        <div className="px-6 py-4 border-b border-gray-200">
                            <h3 className="text-lg font-semibold text-gray-800 flex items-center">
                                <svg
                                    className="w-5 h-5 mr-2"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
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

                        <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4">
                            <label className="flex flex-col">
                                <span className="mb-1 font-medium text-gray-700">Nombre de la empresa *</span>
                                <input
                                    placeholder="Nombre de la empresa"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
                                    required
                                />
                            </label>

                            <label className="flex flex-col">
                                <span className="mb-1 font-medium text-gray-700">Razón social *</span>
                                <input
                                    placeholder="Razón social"
                                    value={businessName}
                                    onChange={(e) => setBusinessName(e.target.value)}
                                    className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
                                    required
                                />
                            </label>

                            <label className="flex flex-col">
                                <span className="mb-1 font-medium text-gray-700">Nombre del representante *</span>
                                <input
                                    placeholder="Nombre del representante"
                                    value={representativeName}
                                    onChange={(e) => setRepresentativeName(e.target.value)}
                                    className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
                                    required
                                />
                            </label>

                            <label className="flex flex-col">
                                <span className="mb-1 font-medium text-gray-700">Apellido del representante *</span>
                                <input
                                    placeholder="Apellido del representante"
                                    value={representativeSurnames}
                                    onChange={(e) => setRepresentativeSurnames(e.target.value)}
                                    className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
                                    required
                                />
                            </label>

                            <label className="flex flex-col">
                                <span className="mb-1 font-medium text-gray-700">Correo del representante *</span>
                                <input
                                    type="email"
                                    placeholder="Correo del representante"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
                                    required
                                />
                            </label>

                            <label className="flex flex-col">
                                <span className="mb-1 font-medium text-gray-700">Teléfono</span>
                                <input
                                    type="text"
                                    placeholder="Teléfono"
                                    value={phone}
                                    maxLength="15"
                                    onChange={(e) => {
                                        const value = e.target.value;
                                        if (/^\d*$/.test(value)) {
                                            setPhone(value);
                                        }
                                    }}
                                    className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
                                    required
                                />
                            </label>
                        </div>
                    </div>

                    <div className="bg-white shadow-lg rounded-xl border border-gray-200">
                        <div className="px-6 py-4 border-b border-gray-200">
                            <h3 className="text-lg font-semibold text-gray-800 flex items-center">
                                <svg
                                    className="w-5 h-5 mr-2"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                    />
                                    <path
                                        d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                    />
                                </svg>
                                Estado del Cliente
                            </h3>
                        </div>

                        <div className="p-6">
                            <div className="flex items-center space-x-3">
                                <button
                                    type="button"
                                    onClick={() => setClienteActivo(!clienteActivo)}
                                    className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${
                                        clienteActivo ? "bg-green-600" : "bg-gray-300"
                                    }`}
                                >
                  <span
                      className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                          clienteActivo ? "translate-x-6" : "translate-x-1"
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
                            onClick={volverAClientes}
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
    );
}
