import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from 'sweetalert2';
import "../styles/contratos.module.css";

const initialContratos = [
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
];

const Contract = () => {
    const [contratos, setContratos] = useState(initialContratos);
    const [cargando, setCargando] = useState(false);
    const [mensaje, setMensaje] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [comentario, setComentario] = useState("");
    const [contratoRechazar, setContratoRechazar] = useState(null);

    const aceptarContrato = (idx) => {
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

        // Simula carga y después muestra éxito
        setTimeout(() => {
            const nuevos = contratos.filter((_, i) => i !== idx);
            setContratos(nuevos);
            setCargando(false);
            // Actualiza la alerta a "Contrato aceptado"
            Swal.update({
                title: '¡Éxito!',
                html: '<strong> Contrato aceptado</strong>',
                icon: 'success',
                showConfirmButton: true,
                
            });
        }, 2000);
    };


    const abrirModalRechazo = (idx) => {
        setContratoRechazar(idx);
        setShowModal(true);
    };

    const rechazarContrato = () => {
        const nuevos = contratos.filter((_, i) => i !== contratoRechazar);
        setContratos(nuevos);
        setShowModal(false);
        setComentario("");
        setContratoRechazar(null);

        Swal.fire({
            icon: 'success',
            title: 'Contrato rechazado',
            text: 'El contrato ha sido rechazado correctamente.',
            confirmButtonColor: '#7F56D9',
        });
    };


    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Contratos Pendientes</h1>
                <input
                    type="text"
                    placeholder="Buscar contrato..."
                    className="border border-gray-300 rounded-md p-2"
                />
            </div>

            <div className="grid grid-cols-1 gap-6">
                {contratos.map((contrato, idx) => (
                    <div
                        key={idx}
                        className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                    >
                        <div className="p-6">
                            <div className="flex justify-between items-start mb-4">
                                <h2 className="text-xl font-semibold text-gray-800">{contrato.nombre}</h2>
                            </div>

                            <div className="space-y-2 text-sm text-gray-700">
                                <p><strong>Cliente:</strong> {contrato.cliente}</p>
                                <p><strong>Categoría:</strong> {contrato.categoria}</p>
                                <p><strong>Vencimiento:</strong> {contrato.vencimiento}</p>
                                <p><strong>Descripción:</strong> {contrato.descripcion}</p>
                            </div>

                            <div className="flex justify-end gap-4 mt-6">
                                <button
                                    className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg"
                                    onClick={() => aceptarContrato(idx)}
                                >
                                    Aceptar contrato
                                </button>
                                <button
                                    className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg"
                                    onClick={() => abrirModalRechazo(idx)}
                                >
                                    Rechazar contrato
                                </button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

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
