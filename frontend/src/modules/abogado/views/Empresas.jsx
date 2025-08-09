import { useState } from "react";

const empresasRe = [
    {
        cliente: "Comercializadora ABC",
        categoria: "Consultoría",
        descripcion: "Contrato para asesoría en optimización de procesos internos.",
        responsable: "Juan Pérez",
    },
    {
        cliente: "Empresa ABC",
        categoria: "Consultoría",
        descripcion: "Contrato para asesoría en optimización de procesos internos.",
        responsable: "Juan Pérez",
    },
    {
        cliente: "SA de CV",
        categoria: "Consultoría",
        descripcion: "Contrato para asesoría en optimización de procesos internos.",
        responsable: "Juan Pérez",
    },
];

const Empresas = () => {
    const [empresas, setEmpresas] = useState(empresasRe);

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Empresas Establecidas</h1>
                <input type="text" placeholder="Buscar empresa..." className="border border-gray-300 rounded-md p-2"/>
            </div>

            <div className="grid grid-cols-1 gap-6">
                {empresas.map((empresas, idx) => (
                    <div
                        key={idx}
                        className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                    >
                        <div className="p-6">
                            <div className="flex justify-between items-start mb-4">
                                <h2 className="text-xl font-semibold text-gray-800">{empresas.cliente}</h2>
                            </div>

                            <div className="space-y-2 text-sm text-gray-700">
                                <p><strong>Categoría:</strong> {empresas.categoria}</p>
                                <p><strong>Descripción:</strong> {empresas.descripcion}</p>
                                <p><strong>Responsable:</strong> {empresas.responsable}</p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default Empresas;