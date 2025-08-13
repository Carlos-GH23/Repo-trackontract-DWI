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

    const fetchContratos = async () => {
        try {
            setCargando(true);
            const token = localStorage.getItem("accessToken");
            
            if (!token) {
                throw new Error("No hay token de autenticación");
            }

            const response = await fetch("http://localhost:8080/contracts/all", {
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
            console.error("Error al obtener contratos:", error);
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
            if (!token) {
                throw new Error("No hay token de autenticación");
            }

            // Aquí podrías implementar la lógica para aceptar el contrato
            // Por ahora solo lo removemos de la lista
            const nuevos = contratos.filter(c => c.id !== contrato.id);
            setContratos(nuevos);

            // Mostrar éxito
            Swal.fire({
                title: '¡Éxito!',
                html: '<strong>Contrato aceptado</strong>',
                icon: 'success',
                showConfirmButton: true,
                confirmButtonColor: '#7F56D9',
            });
        } catch (error) {
            console.error("Error al aceptar contrato:", error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudo aceptar el contrato',
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
            if (!token) {
                throw new Error("No hay token de autenticación");
            }

            // Aquí podrías implementar la lógica para rechazar el contrato
            // Por ahora solo lo removemos de la lista
            const nuevos = contratos.filter(c => c.id !== contratoRechazar.id);
            setContratos(nuevos);
            setShowModal(false);
            setComentario("");
            setContratoRechazar(null);

            // Alerta de éxito
            Swal.fire({
                icon: 'success',
                title: 'Contrato rechazado',
                text: 'El contrato ha sido rechazado correctamente.',
                confirmButtonColor: '#7F56D9',
            });
        } catch (error) {
            console.error("Error al rechazar contrato:", error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudo rechazar el contrato',
                confirmButtonColor: '#7F56D9',
            });
        }
    };



    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Contratos Disponibles</h1>
                <input
                    type="text"
                    placeholder="Buscar contrato..."
                    value={filtroNombre}
                    onChange={(e) => setFiltroNombre(e.target.value)}
                    className="border border-gray-300 rounded-md p-2"
                />
            </div>

            {cargando ? (
                <div className="flex justify-center items-center py-12">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                    <span className="ml-3 text-gray-600">Cargando contratos...</span>
                </div>
            ) : contratos.length === 0 ? (
                <div className="text-center py-12">
                    <p className="text-gray-500 text-lg">No hay contratos disponibles</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 gap-6">
                    {contratos
                        .filter(contrato => 
                            contrato.name?.toLowerCase().includes(filtroNombre.toLowerCase()) ||
                            contrato.client_id?.name?.toLowerCase().includes(filtroNombre.toLowerCase())
                        )
                        .map((contrato) => (
                        <div
                            key={contrato.id}
                            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                        >
                            <div className="p-6">
                                <div className="flex justify-between items-start mb-4">
                                    <h2 className="text-xl font-semibold text-gray-800">{contrato.name}</h2>
                                    <span className={`text-sm font-medium px-2 py-1 rounded-full ${
                                        contrato.status ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                                    }`}>
                                        {contrato.status ? "Activo" : "Inactivo"}
                                    </span>
                                </div>

                                <div className="space-y-2 text-sm text-gray-700">
                                    <p><strong>Cliente:</strong> {contrato.client_id?.name || "N/A"}</p>
                                    <p><strong>Categoría:</strong> {contrato.category_id?.name || "N/A"}</p>
                                    <p><strong>Vencimiento:</strong> {contrato.due_date ? new Date(contrato.due_date).toLocaleDateString() : "N/A"}</p>
                                    <p><strong>Descripción:</strong> {contrato.description || "Sin descripción"}</p>
                                </div>

                                <div className="flex justify-end gap-4 mt-6">
                                    <button
                                        className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg"
                                        onClick={() => aceptarContrato(contrato)}
                                    >
                                        Aceptar contrato
                                    </button>
                                    <button
                                        className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg"
                                        onClick={() => abrirModalRechazo(contrato)}
                                    >
                                        Rechazar contrato
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
