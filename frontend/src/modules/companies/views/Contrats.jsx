import { useState, useEffect } from "react";
import Swal from 'sweetalert2';
import '../styles/contra.module.css';

const Contrats = () => {
    const [contratos, setContratos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [filtroNombre, setFiltroNombre] = useState("");

    // Obtener contratos del cliente desde el backend
    useEffect(() => {
        fetchContratos();
    }, []);

    const fetchContratos = async () => {
        try {
            setCargando(true);
            const token = localStorage.getItem("accessToken");
            const userEmail = localStorage.getItem("email");
            
            if (!token) {
                throw new Error("No hay token de autenticación");
            }

            if (!userEmail) {
                throw new Error("No se pudo identificar el email del usuario");
            }

            // Obtener contratos del cliente autenticado por email
            const response = await fetch(`http://localhost:8080/contracts/by-user-email?email=${encodeURIComponent(userEmail)}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al cargar contratos");
            }

            const data = await response.json();
            
            if (data.result && Array.isArray(data.result)) {
                // Filtrar solo contratos aceptados (status = true)
                const contratosAceptados = data.result.filter(contrato => {
                    return contrato.status === true;
                });
                
                setContratos(contratosAceptados);
            } else {
                setContratos([]);
            }
        } catch (error) {
            // Error al obtener contratos
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

    const descargarPDF = async (contrato) => {
        try {
            // Mostrar spinner
            Swal.fire({
                title: 'Generando PDF...',
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

            // Llamar al endpoint real del backend
            const response = await fetch(`http://localhost:8080/contracts/${contrato.id}/pdf`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al generar PDF");
            }

            // Obtener el PDF como blob
            const pdfBlob = await response.blob();
            
            // Crear URL del blob
            const url = window.URL.createObjectURL(pdfBlob);
            
            // Crear enlace de descarga
            const link = document.createElement('a');
            link.href = url;
            link.download = `contrato_${contrato.id}.pdf`;
            
            // Simular clic para descargar
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            
            // Liberar URL
            window.URL.revokeObjectURL(url);

            // Mostrar mensaje de éxito
            Swal.fire({
                icon: 'success',
                title: 'PDF Generado',
                text: 'El PDF se ha descargado correctamente',
                confirmButtonColor: '#7F56D9',
            });

        } catch (error) {
            // Error al generar PDF
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudo generar el PDF',
                confirmButtonColor: '#7F56D9',
            });
        }
    };

    const mostrarDetalleContrato = (contrato) => {
        Swal.fire({
            title: contrato.name,
            html: `
                <div class="text-left space-y-3">
                    <div class="flex items-center justify-between">
                        <span class="font-semibold text-gray-700">Estado de Aprobación:</span>
                        <span class="px-2 py-1 text-xs rounded-full font-medium ${contrato.status ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}">
                            ${contrato.status ? '✅ Aceptado y Notificado' : '⏳ Pendiente de Aprobación'}
                        </span>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Categoría:</span>
                        <p class="text-gray-600 mt-1">${contrato.category_id?.name || "N/A"}</p>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Abogado Asignado:</span>
                        <p class="text-gray-600 mt-1">${contrato.abogado_id?.name && contrato.abogado_id?.lastName ? 
                            `${contrato.abogado_id.name} ${contrato.abogado_id.lastName}` : "N/A"}</p>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Fecha de Aprobación:</span>
                        <p class="text-gray-600 mt-1">${contrato.updated_at ? new Date(contrato.updated_at).toLocaleDateString() : "N/A"}</p>
                    </div>
                    <div>
                        <span class="font-semibold text-gray-700">Fecha de Vencimiento:</span>
                        <p class="text-gray-600 mt-1">${contrato.due_date ? new Date(contrato.due_date).toLocaleDateString() : "N/A"}</p>
                    </div>
                                         <div>
                         <span class="font-semibold text-gray-700">Descripción:</span>
                         <p class="text-gray-600 mt-1">${contrato.description || "Sin descripción"}</p>
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
                <span className="ml-3 text-gray-600">Cargando contratos...</span>
            </div>
        );
    }

    // Filtrado local por nombre
    const contratosFiltrados = contratos.filter((contrato) =>
        contrato.name?.toLowerCase().includes(filtroNombre.toLowerCase()) ||
        contrato.category_id?.name?.toLowerCase().includes(filtroNombre.toLowerCase())
    );

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-gray-800">Mis Contratos</h1>
                    <p className="text-gray-600 mt-1">Contratos pendientes y aceptados por nuestro equipo legal</p>
                </div>
                <input 
                    type="text" 
                    placeholder="Buscar contrato..." 
                    value={filtroNombre}
                    onChange={(e) => setFiltroNombre(e.target.value)}
                    className="border border-gray-300 rounded-md p-2"
                />
            </div>

            {contratosFiltrados.length === 0 ? (
                <div className="text-center py-12">
                    <div className="max-w-md mx-auto">
                        <div className="text-gray-400 mb-4">
                            <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                        </div>
                        <h3 className="text-lg font-semibold text-gray-700 mb-2">No tienes contratos</h3>
                        <p className="text-gray-500">
                            Cuando se cree un contrato para tu empresa, aparecerá aquí. 
                            Los contratos estarán en estado "Pendiente" hasta que un abogado los revise y apruebe.
                        </p>
                    </div>
                </div>
            ) : (
                <div className="grid grid-cols-1 gap-6">
                    {contratosFiltrados.map((contrato) => (
                        <div
                            key={contrato.id}
                            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                        >
                            <div className="p-6">
                                <div className="flex justify-between items-start mb-4">
                                    <h2 className="text-xl font-semibold text-gray-800">{contrato.name}</h2>
                                    <div className="flex flex-col items-end gap-2">
                                        <span className="px-2 py-1 text-xs rounded-full font-medium bg-green-100 text-green-800">
                                            ✅ Aceptado y Notificado
                                        </span>
                                        {contrato.updated_at && (
                                            <span className="text-xs text-gray-500">
                                                Aprobado: {new Date(contrato.updated_at).toLocaleDateString()}
                                            </span>
                                        )}
                                    </div>
                                </div>

                                <div className="space-y-2 text-sm text-gray-700 mb-4">
                                    <p><strong>Categoría:</strong> {contrato.category_id?.name || "N/A"}</p>
                                    <p><strong>Abogado Asignado:</strong> {contrato.abogado_id?.name && contrato.abogado_id?.lastName ? 
                                        `${contrato.abogado_id.name} ${contrato.abogado_id.lastName}` : "N/A"}</p>
                                    <p><strong>Vencimiento:</strong> {contrato.due_date ? new Date(contrato.due_date).toLocaleDateString() : "N/A"}</p>
                                    <p><strong>Descripción:</strong> {contrato.description || "Sin descripción"}</p>
                                </div>

                                <div className="flex justify-end gap-4">
                                    <button 
                                        className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
                                        onClick={() => mostrarDetalleContrato(contrato)}
                                    >
                                        Ver Detalle
                                    </button>
                                    <button 
                                        className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg"
                                        onClick={() => descargarPDF(contrato)}
                                    >
                                        Descargar PDF
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Contrats;