import React, { useState, useEffect } from "react";
import { FaEye, FaEdit, FaTrash } from "react-icons/fa";
import { Link } from "react-router-dom";
import Swal from "sweetalert2";

export default function CategoriasInactivas() {
    const [categorias, setCategorias] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchCategoriasInactivas = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch("http://localhost:8080/categories/all/status/false", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) throw new Error("Error al cargar categorías inactivas");

            const data = await response.json();
            setCategorias(data || []);
        } catch (error) {
            // Error al obtener categorías inactivas
            setCategorias([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCategoriasInactivas();
    }, []);


    if (loading) return <p>Cargando categorías inactivas...</p>;

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="flex justify-between items-center mb-8">
                <div className="flex items-center space-x-4">
                    <div className="bg-blue-100 p-3 rounded-lg">{/* Puedes agregar un icono aquí */}</div>
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">Categorías Inactivas</h1>
                        <p className="text-sm text-gray-600">Panel de Administración - Gestión de Categorías Inactivas</p>
                    </div>
                </div>


            </div>

            {categorias.length === 0 ? (
                <p className="text-center text-gray-500 text-lg">No hay categorías inactivas registradas.</p>
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
                                    <span className="text-xs px-2 py-1 rounded-full font-medium bg-gray-200 text-gray-700">
                    Inactivo
                  </span>
                                </div>

                                <p className="text-sm text-gray-600 mb-4">{categoria.description}</p>

                                <div className="flex gap-3">





                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
