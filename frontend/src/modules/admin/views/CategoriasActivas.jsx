import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";

export default function CategoriasActivas() {
    const [categorias, setCategorias] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchCategoriasActivas = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch("http://localhost:8080/categories/all/status/true", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                // Error al cargar categorías activas
                setCategorias([]);
                return;
            }

            const data = await response.json();
            // Respuesta API categorías activas procesada
            if (data.result && Array.isArray(data.result)) {
                setCategorias(data.result);
            } else {
                setCategorias([]);
            }
        } catch (error) {
            // Error al cargar categorías activas
            setCategorias([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCategoriasActivas();
    }, []);

    if (loading) return <p>Cargando categorías activas...</p>;

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-8">
                <div className="flex items-center space-x-4">
                    <div className="bg-green-100 p-3 rounded-lg">{}</div>
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">Categorías Activas</h1>
                        <p className="text-sm text-gray-600">Panel de Administración - Gestión de Categorías Activas</p>
                    </div>
                </div>
            </div>

            {categorias.length === 0 ? (
                <p className="text-center text-gray-500 text-lg">No hay categorías activas registradas.</p>
            ) : (
                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {categorias.map((categoria) => (
                        <div
                            key={categoria.id}
                            className="bg-white shadow-lg hover:shadow-xl transition-shadow duration-300 rounded-xl border border-gray-200"
                        >
                            <div className="p-6">
                                <div className="flex justify-between items-start mb-2">
                                    <h2 className="text-lg font-semibold text-gray-800">{categoria.name}</h2>
                                    <span className="text-xs px-2 py-1 rounded-full font-medium bg-green-100 text-green-800">
                    Activo
                  </span>
                                </div>

                                <p className="text-sm text-gray-600 mb-4">{categoria.description}</p>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
