import { useState, useEffect } from "react";
import { FaBuilding, FaFileContract, FaCalendarAlt, FaUserTie } from "react-icons/fa";

const Empresas = () => {
    const [empresas, setEmpresas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [searchTerm, setSearchTerm] = useState("");

    useEffect(() => {
        fetchEmpresas();
    }, []);

    const fetchEmpresas = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem("accessToken");
            const userId = localStorage.getItem("userId");

            if (!token || !userId) {
                setError("No se pudo obtener la información de autenticación");
                return;
            }

            const response = await fetch(`http://localhost:8080/contracts/empresas-by-abogado/${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al cargar las empresas");
            }

            const data = await response.json();
            
            if (data.result && Array.isArray(data.result)) {
                setEmpresas(data.result);
            } else {
                setEmpresas([]);
            }
        } catch (error) {
            // Error al obtener empresas
            setEmpresas([]);
        } finally {
            setLoading(false);
        }
    };

    const getStatusColor = (status) => {
        if (status === true) return "bg-green-100 text-green-800";
        if (status === false) return "bg-red-100 text-red-800";
        return "bg-gray-100 text-gray-800";
    };

    const getStatusText = (status) => {
        if (status === true) return "Activo";
        if (status === false) return "Inactivo";
        return "Desconocido";
    };

    const getApprovalStatusColor = (approvalStatus) => {
        switch (approvalStatus) {
            case "ACEPTADO":
                return "bg-blue-100 text-blue-800";
            case "RECHAZADO":
                return "bg-red-100 text-red-800";
            case "PENDIENTE":
                return "bg-yellow-100 text-yellow-800";
            default:
                return "bg-gray-100 text-gray-800";
        }
    };

    const getApprovalStatusText = (approvalStatus) => {
        switch (approvalStatus) {
            case "ACEPTADO":
                return "Aceptado";
            case "RECHAZADO":
                return "Rechazado";
            case "PENDIENTE":
                return "Pendiente";
            default:
                return "Desconocido";
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return "No especificada";
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('es-ES', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        } catch (error) {
            return "Fecha inválida";
        }
    };

    const filteredEmpresas = empresas.filter(empresa =>
        empresa.clientName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        empresa.categoryName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        empresa.contractName?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) {
        return (
            <div className="min-h-screen bg-white p-6">
                <div className="flex justify-center items-center h-64">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen bg-white p-6">
                <div className="text-center text-red-600">
                    <p className="text-lg">{error}</p>
                    <button 
                        onClick={fetchEmpresas}
                        className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                        Reintentar
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-6">
                <div className="flex items-center space-x-4">
                    <div className="bg-blue-100 p-3 rounded-lg">
                        <FaBuilding className="w-7 h-7 text-blue-600" />
                    </div>
                    <div>
                        <h1 className="text-2xl font-bold text-gray-800">Empresas Asignadas</h1>
                        <p className="text-sm text-gray-600">Empresas asignadas a través de contratos</p>
                    </div>
                </div>
            </div>

            {/* Barra de búsqueda */}
            <div className="mb-6">
                <div className="relative">
                    <input
                        type="text"
                        placeholder="Buscar por empresa, categoría o contrato..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                    <FaBuilding className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                </div>
            </div>

            {/* Estadísticas */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
                    <div className="flex items-center">
                        <FaBuilding className="w-8 h-8 text-blue-600 mr-3" />
                        <div>
                            <p className="text-sm text-blue-600 font-medium">Total Empresas</p>
                            <p className="text-2xl font-bold text-blue-800">{empresas.length}</p>
                        </div>
                    </div>
                </div>
                <div className="bg-green-50 p-4 rounded-lg border border-green-200">
                    <div className="flex items-center">
                        <FaFileContract className="w-8 h-8 text-green-600 mr-3" />
                        <div>
                            <p className="text-sm text-green-600 font-medium">Contratos Activos</p>
                            <p className="text-2xl font-bold text-green-800">
                                {empresas.filter(e => e.contractStatus === true).length}
                            </p>
                        </div>
                    </div>
                </div>
                <div className="bg-yellow-50 p-4 rounded-lg border border-yellow-200">
                    <div className="flex items-center">
                        <FaUserTie className="w-8 h-8 text-yellow-600 mr-3" />
                        <div>
                            <p className="text-sm text-yellow-600 font-medium">Pendientes</p>
                            <p className="text-2xl font-bold text-yellow-800">
                                {empresas.filter(e => e.approvalStatus === "PENDIENTE").length}
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Lista de empresas */}
            {filteredEmpresas.length === 0 ? (
                <div className="text-center py-12">
                    <FaBuilding className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                    <h3 className="text-lg font-medium text-gray-900 mb-2">
                        {searchTerm ? "No se encontraron empresas" : "No hay empresas asignadas"}
                    </h3>
                    <p className="text-gray-500">
                        {searchTerm 
                            ? "Intenta con otros términos de búsqueda"
                            : "Aún no tienes empresas asignadas a través de contratos"
                        }
                    </p>
                </div>
            ) : (
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {filteredEmpresas.map((empresa, index) => (
                        <div
                            key={`${empresa.clientId}-${empresa.contractId}-${index}`}
                            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                        >
                            <div className="p-6">
                                {/* Header con nombre de empresa y estado */}
                                <div className="flex justify-between items-start mb-4">
                                    <div className="flex-1">
                                        <h2 className="text-xl font-semibold text-gray-800 mb-2">
                                            {empresa.clientName || "Empresa sin nombre"}
                                        </h2>
                                        <div className="flex flex-wrap gap-2">
                                            <span className={`text-xs px-2 py-1 rounded-full font-medium ${getStatusColor(empresa.contractStatus)}`}>
                                                {getStatusText(empresa.contractStatus)}
                                            </span>
                                            <span className={`text-xs px-2 py-1 rounded-full font-medium ${getApprovalStatusColor(empresa.approvalStatus)}`}>
                                                {getApprovalStatusText(empresa.approvalStatus)}
                                            </span>
                                        </div>
                                    </div>
                                </div>

                                {/* Información del contrato */}
                                <div className="space-y-3 mb-4">
                                    <div className="flex items-center space-x-2">
                                        <FaFileContract className="w-4 h-4 text-gray-500" />
                                        <span className="text-sm text-gray-600">
                                            <strong>Contrato:</strong> {empresa.contractName || "Sin nombre"}
                                        </span>
                                    </div>
                                    
                                    <div className="flex items-center space-x-2">
                                        <FaBuilding className="w-4 h-4 text-gray-500" />
                                        <span className="text-sm text-gray-600">
                                            <strong>Categoría:</strong> {empresa.categoryName || "Sin categoría"}
                                        </span>
                                    </div>
                                    
                                    <div className="flex items-center space-x-2">
                                        <FaCalendarAlt className="w-4 h-4 text-gray-500" />
                                        <span className="text-sm text-gray-600">
                                            <strong>Fecha límite:</strong> {formatDate(empresa.dueDate)}
                                        </span>
                                    </div>
                                </div>

                                {/* Descripción del contrato */}
                                {empresa.contractDescription && (
                                    <div className="mb-4">
                                        <p className="text-sm text-gray-700">
                                            <strong>Descripción:</strong> {empresa.contractDescription}
                                        </p>
                                    </div>
                                )}

                                {/* Información adicional */}
                                <div className="pt-4 border-t border-gray-200">
                                    <div className="flex justify-between text-xs text-gray-500">
                                        <span>ID Cliente: {empresa.clientId}</span>
                                        <span>ID Contrato: {empresa.contractId}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Empresas;