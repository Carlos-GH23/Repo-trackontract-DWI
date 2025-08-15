import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Swal from 'sweetalert2';
import "../styles/contratos.module.css";

const Contract = () => {
    const [contratos, setContratos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [mensaje, setMensaje] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [comentario, setComentario] = useState("");
    const [contratoRechazar, setContratoRechazar] = useState(null);
    const [filtroNombre, setFiltroNombre] = useState("");

    // Obtener contratos del backend
    useEffect(() => {
        fetchContratos();
    }, []);

    // Monitorear cambios en contratos
    useEffect(() => {
        // Estado de contratos actualizado
    }, [contratos]);

    const fetchContratos = async () => {
        try {
            setCargando(true);
            const token = localStorage.getItem("accessToken");
            const userId = localStorage.getItem("userId");
            
            if (!token) {
                throw new Error("No hay token de autenticación");
            }

            if (!userId) {
                throw new Error("No se pudo identificar al usuario");
            }

            // Obtener solo los contratos asignados al abogado autenticado
            const response = await fetch(`http://localhost:8080/contracts/by-abogado/${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al cargar contratos");
            }

            const data = await response.json();
            
            if (data.result && Array.isArray(data.result)) {
                setContratos(data.result);
            } else {
                setContratos([]);
            }
        } catch (error) {
            setMensaje("Error al cargar contratos: " + error.message);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudieron cargar los contratos',
                confirmButtonColor: '#7F56D9',
            });
        } finally {
            setCargando(false);
        }
    };


    const aceptarContrato = async (contrato) => {
        try {
            // Validar que el contrato no haya sido ya aceptado
            if (contrato.status) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Contrato ya aceptado',
                    text: 'Este contrato ya ha sido aceptado y no se puede modificar.',
                    confirmButtonColor: '#7F56D9',
                });
                return;
            }

            // Mostrar spinner circular infinito
            Swal.fire({
                title: 'Procesando...',
                html: '<div class="swal2-spinner"></div>',
                allowOutsideClick: false,
                showConfirmButton: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            const token = localStorage.getItem("accessToken");
            const userId = localStorage.getItem("userId");
            
            if (!token) {
                throw new Error("No hay token de autenticación");
            }

            if (!userId) {
                throw new Error("No se pudo identificar al usuario");
            }

            // Llamar al backend para aceptar el contrato
            const response = await fetch(`http://localhost:8080/contracts/${contrato.id}/accept?abogadoId=${userId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                let errorMessage = 'Error al aceptar el contrato';
                
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (jsonError) {
                    // Si no se puede parsear JSON, usar el status text
                    if (response.status === 403) {
                        errorMessage = 'No tienes permisos para realizar esta acción';
                    } else if (response.status === 404) {
                        errorMessage = 'Contrato no encontrado';
                    } else if (response.status === 500) {
                        errorMessage = 'Error interno del servidor';
                    } else {
                        errorMessage = `Error ${response.status}: ${response.statusText}`;
                    }
                }
                
                throw new Error(errorMessage);
            }

            // Actualizar la lista de contratos
            await fetchContratos();

            // Debug: verificar el estado del contrato
            try {
                const debugResponse = await fetch(`http://localhost:8080/contracts/${contrato.id}/debug`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                if (debugResponse.ok) {
                    const debugData = await debugResponse.json();
                }
            } catch (debugError) {
                // Error al debuggear contrato
            }

            // Mostrar éxito
            Swal.fire({
                title: '¡Contrato Aceptado!',
                html: '<strong>✅ Contrato aceptado exitosamente</strong><br><br>' +
                      'El contrato ha sido enviado al cliente por email y ya no se puede modificar.',
                icon: 'success',
                showConfirmButton: true,
                confirmButtonColor: '#7F56D9',
            });
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error.message || 'No se pudo aceptar el contrato',
                confirmButtonColor: '#7F56D9',
            });
        }
    };

    const abrirModalRechazo = (contrato) => {
        setContratoRechazar(contrato);
        setShowModal(true);
    };

    const rechazarContrato = async () => {
        try {
            // Validar que el contrato no haya sido ya aceptado
            if (contratoRechazar.status) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Contrato ya aceptado',
                    text: 'Este contrato ya ha sido aceptado y no se puede rechazar.',
                    confirmButtonColor: '#7F56D9',
                });
                return;
            }

            // Validación: comentario vacío
            if (!comentario.trim()) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Por favor, ingresa un motivo para rechazar el contrato.',
                    confirmButtonColor: '#7F56D9',
                });
                return;
            }

            const token = localStorage.getItem("accessToken");
            const userId = localStorage.getItem("userId");
            
            if (!token) {
                throw new Error("No hay token de autenticación");
            }

            if (!userId) {
                throw new Error("No se pudo identificar al usuario");
            }

            // Llamar al backend para rechazar el contrato
            const response = await fetch(`http://localhost:8080/contracts/${contratoRechazar.id}/reject?abogadoId=${userId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(comentario.trim())
            });

            if (!response.ok) {
                let errorMessage = 'Error al rechazar el contrato';
                
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (jsonError) {
                    // Si no se puede parsear JSON, usar el status text
                    if (response.status === 403) {
                        errorMessage = 'No tienes permisos para realizar esta acción';
                    } else if (response.status === 404) {
                        errorMessage = 'Contrato no encontrado';
                    } else if (response.status === 500) {
                        errorMessage = 'Error interno del servidor';
                    } else {
                        errorMessage = `Error ${response.status}: ${response.statusText}`;
                    }
                }
                
                throw new Error(errorMessage);
            }

            // Cerrar modal y limpiar estado
            setShowModal(false);
            setComentario("");
            setContratoRechazar(null);

            // Actualizar la lista de contratos
            await fetchContratos();

            // Debug: verificar el estado del contrato
            try {
                const debugResponse = await fetch(`http://localhost:8080/contracts/${contratoRechazar.id}/debug`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                if (debugResponse.ok) {
                    const debugData = await debugResponse.json();
                }
            } catch (debugError) {
                // Error al debuggear contrato
            }

            // Alerta de éxito
            Swal.fire({
                icon: 'success',
                title: 'Contrato rechazado',
                text: 'El contrato ha sido rechazado correctamente.',
                confirmButtonColor: '#7F56D9',
            });
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error.message || 'No se pudo rechazar el contrato',
                confirmButtonColor: '#7F56D9',
            });
        }
    };



    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Mis Contratos Asignados</h1>
                <div className="flex gap-2">
                    <button
                        onClick={async () => {
                            const token = localStorage.getItem("accessToken");
                            const userId = localStorage.getItem("userId");
                            try {
                                const response = await fetch(`http://localhost:8080/contracts/by-abogado/${userId}/debug`, {
                                    headers: { 'Authorization': `Bearer ${token}` }
                                });
                                if (response.ok) {
                                    const data = await response.json();
                                }
                            } catch (error) {
                                // Error en debug
                            }
                        }}
                        className="px-3 py-2 bg-blue-600 text-white rounded-md text-sm hover:bg-blue-700"
                    >
                        Debug
                    </button>
                    <input
                        type="text"
                        placeholder="Buscar contrato..."
                        value={filtroNombre}
                        onChange={(e) => setFiltroNombre(e.target.value)}
                        className="border border-gray-300 rounded-md p-2"
                    />
                </div>
            </div>

            {cargando ? (
                <div className="flex justify-center items-center py-12">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                    <span className="ml-3 text-gray-600">Cargando contratos...</span>
                </div>
            ) : contratos.length === 0 ? (
                <div className="text-center py-12">
                    <p className="text-gray-500 text-lg">No tienes contratos asignados actualmente</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 gap-6 w-full">
                    {contratos
                        .filter(contrato => 
                            contrato.name?.toLowerCase().includes(filtroNombre.toLowerCase()) ||
                            contrato.client_id?.name?.toLowerCase().includes(filtroNombre.toLowerCase())
                        )
                        .map((contrato) => (
                        <div
                            key={contrato.id}
                            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200 max-w-full"
                        >
                            <div className="p-6 max-w-full">
                                <div className="flex justify-between items-start mb-4">
                                    <h2 className="text-xl font-semibold text-gray-800">{contrato.name}</h2>
                                    <div className="flex flex-col items-end gap-2">
                                        <span className={`text-sm font-medium px-2 py-1 rounded-full ${
                                            contrato.status ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                                        }`}>
                                            {contrato.status ? "Aceptado y Notificado al Cliente" : "Pendiente de Aprobación"}
                                        </span>
                                    </div>
                                </div>

                                <div className="space-y-2 text-sm text-gray-700">
                                    <p><strong>Cliente:</strong> {contrato.client_id?.name || "N/A"}</p>
                                    <p><strong>Categoría:</strong> {contrato.category_id?.name || "N/A"}</p>
                                    <p><strong>Vencimiento:</strong> {contrato.due_date ? new Date(contrato.due_date).toLocaleDateString() : "N/A"}</p>
                                    <div className="break-words">
                                        <strong>Descripción:</strong> 
                                        <p className="text-gray-600 mt-1 break-words overflow-hidden">
                                            {contrato.description || "Sin descripción"}
                                        </p>
                                    </div>
                                    
                                    {contrato.status && (
                                        <div className="mt-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                                            <p className="text-blue-800 text-sm">
                                                <strong>✅ Contrato Aceptado:</strong> Este contrato ha sido aceptado y se ha enviado una notificación por email al cliente. 
                                                No se pueden realizar más modificaciones.
                                            </p>
                                        </div>
                                    )}
                                </div>

                                <div className="flex justify-end gap-4 mt-6">
                                    <button
                                        className={`px-4 py-2 rounded-lg ${
                                            !contrato.status
                                                ? 'bg-green-600 hover:bg-green-700 text-white'
                                                : 'bg-gray-400 text-gray-200 cursor-not-allowed'
                                        }`}
                                        onClick={() => aceptarContrato(contrato)}
                                        disabled={contrato.status}
                                    >
                                        {contrato.status ? 'Aceptado' : 'Aceptar contrato'}
                                    </button>
                                    <button
                                        className={`px-4 py-2 rounded-lg ${
                                            !contrato.status
                                                ? 'bg-red-600 hover:bg-red-700 text-white'
                                                : 'bg-gray-400 text-gray-200 cursor-not-allowed'
                                        }`}
                                        onClick={() => abrirModalRechazo(contrato)}
                                        disabled={contrato.status}
                                    >
                                        {contrato.status ? 'No disponible' : 'Rechazar contrato'}
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Modal de rechazo */}
            {showModal && (
                <div className="fixed inset-0 bg-gray-300 bg-opacity-40 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-xl">
                        <h2 className="text-xl font-semibold mb-4">Motivo del rechazo</h2>
                        <textarea
                            value={comentario}
                            onChange={(e) => setComentario(e.target.value)}
                            placeholder="Describe por qué estás rechazando el contrato..."
                            className="w-full border border-gray-300 rounded-md p-2 mb-4"
                            rows={4}
                        />
                        <div className="flex justify-end gap-4">
                            <button
                                onClick={() => setShowModal(false)}
                                className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={rechazarContrato}
                                className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg"
                            >
                                Rechazar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Contract;
