import { useState } from "react";
import '../styles/contra.module.css';

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

const Contrats = () => {
    const [contratos, setContratos] = useState(initialContratos);

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Contratos Aceptados</h1>
                <input type="text" placeholder="Buscar contrato..." className="border border-gray-300 rounded-md p-2"/>
            </div>

            <div className="grid grid-cols-1 gap-6">
                {contratos.map((contratos, idx) => (
                    <div
                        key={idx}
                        className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                    >
                        <div className="p-6">
                            <div className="flex justify-between items-start mb-4">
                                <h2 className="text-xl font-semibold text-gray-800">{contratos.nombre}</h2>
                            </div>

                            <div className="space-y-2 text-sm text-gray-700">
                                <p><strong>Categoría:</strong> {contratos.cliente}</p>
                                <p><strong>Responsable:</strong> {contratos.categoria}</p>
                                <p><strong>Descripción:</strong> {contratos.vencimiento}</p>
                                <p><strong>Responsable:</strong> {contratos.descripcion}</p>
                            </div>
                            <div className="boton-container">
                                <button className="btn">Descargar</button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Contrats;