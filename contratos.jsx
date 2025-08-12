import { Settings, Download, Eye, Calendar } from "lucide-react"
import NavbarClient from "../components/NavbarClient"
import SidebarClient from "../components/SidebarClient"
import { useState, useEffect } from "react"

const contractsData = [
    {
        id: 123,
        type: "Contrato de arrendamiento de casa",
        company: "SVC Inmobiliaria",
        lawyer: "Iván Buenavista",
        status: "Activo",
        date: "2024-01-15",
        fileUrl: "/contracts/contract-123.pdf"
    },
    {
        id: 124,
        type: "Contrato de servicios profesionales",
        company: "TechCorp",
        lawyer: "María González",
        status: "Pendiente",
        date: "2024-01-20",
        fileUrl: "/contracts/contract-124.pdf"
    },
    {
        id: 125,
        type: "Contrato de compraventa",
        company: "GlobalTrade",
        lawyer: "Carlos Mendoza",
        status: "Activo",
        date: "2024-01-25",
        fileUrl: "/contracts/contract-125.pdf"
    },
    {
        id: 126,
        type: "Contrato de trabajo",
        company: "InnovateLab",
        lawyer: "Ana Rodríguez",
        status: "Finalizado",
        date: "2024-01-30",
        fileUrl: "/contracts/contract-126.pdf"
    },
]

const ContractClient = () => {
    const [isCollapsed, setIsCollapsed] = useState(false);
    const [contracts, setContracts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filterStatus, setFilterStatus] = useState("todos");

    useEffect(() => {
        // Simular carga de datos desde API
        const loadContracts = async () => {
            try {
                // Simular delay de API
                await new Promise(resolve => setTimeout(resolve, 1000));
                setContracts(contractsData);
                setLoading(false);
            } catch (error) {
                console.error("Error cargando contratos:", error);
                setLoading(false);
            }
        };

        loadContracts();
    }, []);

    const handleDownload = async (contract) => {
        try {
            // Simular descarga
            console.log(`Descargando contrato ${contract.id}: ${contract.type}`);
            
            // Aquí iría la lógica real de descarga
            // Por ejemplo: window.open(contract.fileUrl, '_blank');
            
            // Simular delay de descarga
            await new Promise(resolve => setTimeout(resolve, 500));
            
            alert(`Contrato ${contract.id} descargado exitosamente`);
        } catch (error) {
            console.error("Error descargando contrato:", error);
            alert("Error al descargar el contrato");
        }
    };

    const handleViewContract = (contract) => {
        // Aquí iría la lógica para ver el contrato
        console.log(`Viendo contrato ${contract.id}: ${contract.type}`);
        alert(`Viendo contrato: ${contract.type}`);
    };

    const getStatusColor = (status) => {
        switch (status) {
            case "Activo": return "bg-green-100 text-green-800";
            case "Pendiente": return "bg-yellow-100 text-yellow-800";
            case "Finalizado": return "bg-blue-100 text-blue-800";
            default: return "bg-gray-100 text-gray-800";
        }
    };

    const filteredContracts = filterStatus === "todos" 
        ? contracts 
        : contracts.filter(contract => contract.status === filterStatus);

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-100 flex">
                <SidebarClient isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
                <div className="flex-1 flex flex-col">
                    <NavbarClient />
                    <div className="flex-1 flex items-center justify-center">
                        <div className="text-center">
                            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
                            <p className="text-gray-600">Cargando contratos...</p>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-100 flex">
            {/* Sidebar */}
            <SidebarClient isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />

            {/* Main Content */}
            <div className="flex-1 flex flex-col">
                {/* Navbar */}
                <NavbarClient />
                
                {/* Content */}
                <div className="flex-1 p-6">
                    {/* Header */}
                    <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center space-x-3">
                                <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
                                    <Settings className="w-6 h-6 text-gray-600" />
                                </div>
                                <div>
                                    <h1 className="text-xl font-semibold text-gray-800">Historial de Contratos</h1>
                                    <p className="text-sm text-red-500">Usted podrá visualizar todos los contratos que tiene asignados</p>
                                </div>
                            </div>
                            
                            {/* Filter */}
                            <div className="flex items-center space-x-2">
                                <label className="text-sm text-gray-600">Filtrar por:</label>
                                <select 
                                    value={filterStatus} 
                                    onChange={(e) => setFilterStatus(e.target.value)}
                                    className="border border-gray-300 rounded-md px-3 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    <option value="todos">Todos</option>
                                    <option value="Activo">Activos</option>
                                    <option value="Pendiente">Pendientes</option>
                                    <option value="Finalizado">Finalizados</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    {/* Contract Cards */}
                    <div className="space-y-4">
                        {filteredContracts.length === 0 ? (
                            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8 text-center">
                                <p className="text-gray-500">No se encontraron contratos con el filtro seleccionado.</p>
                            </div>
                        ) : (
                            filteredContracts.map((contract) => (
                                <div key={contract.id} className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
                                    <div className="flex items-center justify-between">
                                        <div className="flex-1">
                                            <div className="flex items-center justify-between mb-4">
                                                <div className="flex items-center space-x-3">
                                                    <span className="text-sm font-medium text-gray-600">No. {contract.id}</span>
                                                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(contract.status)}`}>
                                                        {contract.status}
                                                    </span>
                                                </div>
                                                <h2 className="text-lg font-semibold text-gray-800 text-center flex-1">{contract.type}</h2>
                                                <div className="flex items-center space-x-2 text-sm text-gray-500">
                                                    <Calendar className="w-4 h-4" />
                                                    <span>{new Date(contract.date).toLocaleDateString('es-ES')}</span>
                                                </div>
                                            </div>

                                            <div className="flex items-center justify-between">
                                                <span className="text-sm text-gray-600">
                                                    Nombre empresa: <span className="font-medium">{contract.company}</span>
                                                </span>
                                                <span className="text-sm text-gray-600">
                                                    Abogado Asignado: <span className="font-medium">{contract.lawyer}</span>
                                                </span>
                                            </div>
                                        </div>

                                        <div className="ml-6 flex space-x-2">
                                            <button 
                                                onClick={() => handleViewContract(contract)}
                                                className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors flex items-center space-x-2"
                                            >
                                                <Eye className="w-4 h-4" />
                                                <span>Ver</span>
                                            </button>
                                            <button 
                                                onClick={() => handleDownload(contract)}
                                                className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors flex items-center space-x-2"
                                            >
                                                <Download className="w-4 h-4" />
                                                <span>Descargar</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ContractClient;
