import { useState, useEffect } from "react";
import Swal from 'sweetalert2';

const RechazosContratos = () => {
    const [rechazos, setRechazos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [rechazoSeleccionado, setRechazoSeleccionado] = useState(null);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [adminComments, setAdminComments] = useState("");
    const [finalDecision, setFinalDecision] = useState("");

    // Obtener rechazos pendientes desde el backend
    useEffect(() => {
        fetchRechazos();
    }, []);

    const fetchRechazos = async () => {
        try {
            setCargando(true);
            const token = localStorage.getItem("accessToken");
            
            if (!token) {
                throw new Error("No hay token de autenticación");
            }

            const response = await fetch(`http://localhost:8080/contract-rejections/pending`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al cargar rechazos");
            }

            const data = await response.json();
            
            if (data.result && Array.isArray(data.result)) {
                setRechazos(data.result);
            } else {
                setRechazos([]);
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudieron cargar los rechazos',
                confirmButtonColor: '#7F56D9',
            });
        } finally {
            setCargando(false);
        }
    };

    const abrirModalRevisar = (rechazo) => {
        setRechazoSeleccionado(rechazo);
        setAdminComments("");
        setFinalDecision("");
        setMostrarModal(true);
    };

    const cerrarModal = () => {
        setMostrarModal(false);
        setRechazoSeleccionado(null);
        setAdminComments("");
        setFinalDecision("");
    };

    const revisarRechazo = async () => {
        try {
            if (!finalDecision) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Campo Requerido',
                    text: 'Debe seleccionar una decisión final',
                    confirmButtonColor: '#7F56D9',
                });
                return;
            }

            const token = localStorage.getItem("accessToken");
            const adminId = localStorage.getItem("userId");
            
            if (!token || !adminId) {
                throw new Error("No hay token de autenticación o ID de usuario");
            }

            const response = await fetch(`http://localhost:8080/contract-rejections/${rechazoSeleccionado.id}/review`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    adminId: parseInt(adminId),
                    adminComments: adminComments.trim() || null,
                    finalDecision: finalDecision
                }),
            });

            if (!response.ok) {
                throw new Error("Error al revisar el rechazo");
            }

            const data = await response.json();
            
            Swal.fire({
                icon: 'success',
                title: 'Revisión Completada',
                text: data.message || 'Rechazo revisado exitosamente',
                confirmButtonColor: '#7F56D9',
            });

            cerrarModal();
            fetchRechazos(); // Recargar la lista

        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudo revisar el rechazo',
                confirmButtonColor: '#7F56D9',
            });
        }
    };

    const mostrarDetalleRechazo = (rechazo) => {
        Swal.fire({
            title: `Rechazo - ${rechazo.contractName}`,
            html: `
                <div class="text-left space-y-3">
                    <div>
                        <span class="font-semibold text-gray-700">Contrato:</span>
                        <p class="text-gray-600 mt-1">${rechazo.contractName}</p>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Cliente:</span>
                        <p class="text-gray-600 mt-1">${rechazo.clientName}</p>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Categoría:</span>
                        <p class="text-gray-600 mt-1">${rechazo.categoryName}</p>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Abogado que Rechazó:</span>
                        <p class="text-gray-600 mt-1">${rechazo.abogadoName}</p>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Fecha de Rechazo:</span>
                        <p class="text-gray-600 mt-1">${new Date(rechazo.rejectionDate).toLocaleDateString()}</p>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Motivo del Rechazo:</span>
                        <p class="text-gray-600 mt-1">${rechazo.rejectionReason}</p>
                    </div>
                </div>
            `,
            icon: "info",
            confirmButtonText: "Cerrar",
            confirmButtonColor: "#7F56D9",
            width: "500px",
            customClass: {
                popup: "rounded-xl",
                title: "text-xl font-bold text-gray-800",
                htmlContainer: "text-left"
            }
        });
    };

    if (cargando) {
        return (
            <div className="flex justify-center items-center py-12">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                <span className="ml-3 text-gray-600">Cargando rechazos...</span>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Rechazos de Contratos</h1>
                <p className="text-gray-600 mt-1">Revisar y tomar decisiones sobre contratos rechazados por abogados</p>
            </div>

            {rechazos.length === 0 ? (
                <div className="text-center py-12">
                    <div className="max-w-md mx-auto">
                        <div className="text-gray-400 mb-4">
                            <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                        </div>
                        <h3 className="text-lg font-semibold text-gray-700 mb-2">No hay rechazos pendientes</h3>
                        <p className="text-gray-500">
                            Todos los contratos rechazados han sido revisados. 
                            Los abogados recibirán notificaciones sobre las decisiones tomadas.
                        </p>
                    </div>
                </div>
            ) : (
                <div className="grid grid-cols-1 gap-6">
                    {rechazos.map((rechazo) => (
                        <div
                            key={rechazo.id}
                            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-red-200"
                        >
                            <div className="p-6">
                                <div className="flex justify-between items-start mb-4">
                                    <h2 className="text-xl font-semibold text-gray-800">{rechazo.contractName}</h2>
                                    <div className="flex flex-col items-end gap-2">
                                        <span className="px-2 py-1 text-xs rounded-full font-medium bg-red-100 text-red-800">
                                            🚨 Rechazado por Abogado
                                        </span>
                                        <span className="text-xs text-gray-500">
                                            {new Date(rechazo.rejectionDate).toLocaleDateString()}
                                        </span>
                                    </div>
                                </div>

                                <div className="space-y-2 text-sm text-gray-700 mb-4">
                                    <p><strong>Cliente:</strong> {rechazo.clientName}</p>
                                    <p><strong>Categoría:</strong> {rechazo.categoryName}</p>
                                    <p><strong>Abogado:</strong> {rechazo.abogadoName}</p>
                                    <p><strong>Motivo:</strong> {rechazo.rejectionReason}</p>
                                </div>

                                <div className="flex justify-end gap-4">
                                    <button 
                                        className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
                                        onClick={() => mostrarDetalleRechazo(rechazo)}
                                    >
                                        Ver Detalle
                                    </button>
                                    <button 
                                        className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg"
                                        onClick={() => abrirModalRevisar(rechazo)}
                                    >
                                        Revisar y Decidir
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Modal de Revisión */}
            {mostrarModal && rechazoSeleccionado && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-xl p-6 max-w-md w-full mx-4">
                        <h3 className="text-lg font-semibold text-gray-800 mb-4">
                            Revisar Rechazo - {rechazoSeleccionado.contractName}
                        </h3>
                        
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Decisión Final *
                                </label>
                                <select
                                    value={finalDecision}
                                    onChange={(e) => setFinalDecision(e.target.value)}
                                    className="w-full border border-gray-300 rounded-md p-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                >
                                    <option value="">Seleccionar decisión</option>
                                    <option value="APPROVED">Aprobar Contrato</option>
                                    <option value="REJECTED">Rechazar Definitivamente</option>
                                    <option value="NEEDS_REVISION">Solicitar Revisiones</option>
                                </select>
                            </div>
                            
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Comentarios del Administrador
                                </label>
                                <textarea
                                    value={adminComments}
                                    onChange={(e) => setAdminComments(e.target.value)}
                                    rows={4}
                                    className="w-full border border-gray-300 rounded-md p-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="Comentarios opcionales sobre la decisión..."
                                />
                            </div>
                        </div>
                        
                        <div className="flex justify-end gap-3 mt-6">
                            <button
                                onClick={cerrarModal}
                                className="px-4 py-2 bg-gray-300 hover:bg-gray-400 text-gray-700 rounded-lg"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={revisarRechazo}
                                className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg"
                            >
                                Confirmar Decisión
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default RechazosContratos;
