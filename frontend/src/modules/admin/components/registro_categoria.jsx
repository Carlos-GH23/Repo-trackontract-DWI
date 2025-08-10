import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

export default function RegistroCategoria() {
  const [nombre, setNombre] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [categoriaHabilitada, setCategoriaHabilitada] = useState(true);
  const navigate = useNavigate();

  const volverACategorias = () => {
    navigate("/admin/categorias");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const result = await Swal.fire({
      title: "¿Confirmas crear esta categoría?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Sí, crear",
      cancelButtonText: "Cancelar",
    });

    if (!result.isConfirmed) {
      return;
    }

    const nuevaCategoria = {
      name: nombre,
      description: descripcion,
      status: true,
    };

    const token = localStorage.getItem("accessToken");

    function parseBackendError(errorResponse) {
      if (typeof errorResponse === "object" && errorResponse !== null) {
        if ("text" in errorResponse && typeof errorResponse.text === "string") {
          return errorResponse.text;
        }
        return Object.values(errorResponse).join("\n");
      }
      return String(errorResponse);
    }


    try {
      const responseSave = await fetch("http://localhost:8080/categories/save", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(nuevaCategoria),
      });

      if (!responseSave.ok) {
        const contentType = responseSave.headers.get("content-type");
        let errorMessage = "Error desconocido";

        if (contentType && contentType.includes("application/json")) {
          const errorJson = await responseSave.json();
          errorMessage = parseBackendError(errorJson);
        } else {
          const errorText = await responseSave.text();
          errorMessage = errorText || errorMessage;
        }

        throw new Error(errorMessage);
      }


      const data = await responseSave.json();
      const createdCategory = data.result || data;

      if (categoriaHabilitada === false) {
        const responseStatus = await fetch("http://localhost:8080/categories/change-status", {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            id: createdCategory.id,
            name: createdCategory.name,
            description: createdCategory.description,
            status: categoriaHabilitada,
          }),
        });

        if (!responseStatus.ok) {
          const errorText = await responseStatus.text();
          throw new Error("Error al cambiar estado: " + errorText);
        }
      }

      Swal.fire("¡Creado!", "La categoría fue creada exitosamente.", "success");
      volverACategorias();
    } catch (error) {
      console.error(error);
      Swal.fire("Error", error.message, "error");
    }
  };

  return (
      <div className="min-h-screen bg-white p-6">
        <div className="max-w-4xl mx-auto">
          {/* ENCABEZADO */}
          <div className="flex items-center mb-6">
            <button
                onClick={volverACategorias}
                className="mr-4 p-2 hover:bg-gray-100 rounded-lg transition duration-200 shadow-sm"
            >
              <svg
                  className="w-4 h-4 text-gray-700"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
              >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <h1 className="text-2xl font-bold text-gray-900">Registro de Nueva Categoría</h1>
          </div>

          {/* FORMULARIO */}
          <form className="space-y-6" onSubmit={handleSubmit}>
            {/* Datos de la categoría */}
            <div className="bg-white shadow-xl rounded-xl border border-blue-200">
              <div className="px-6 py-4 border-b border-blue-100">
                <h3 className="text-lg font-semibold text-gray-800">Datos de la Categoría</h3>
              </div>
              <div className="p-6 grid grid-cols-1 gap-4">
                <input
                    value={nombre}
                    onChange={(e) => setNombre(e.target.value)}
                    placeholder="Nombre de la Categoría *"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                />
                <textarea
                    value={descripcion}
                    onChange={(e) => setDescripcion(e.target.value)}
                    rows={4}
                    placeholder="Descripción de la categoría *"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg resize-none shadow-sm focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            {/* Estado de la categoría */}
            <div className="bg-white shadow-xl rounded-xl border border-blue-200">
              <div className="px-6 py-4 border-b border-blue-100">
                <h3 className="text-lg font-semibold text-gray-800">Estado de la Categoría</h3>
              </div>
              <div className="p-6">
                <div className="flex items-center space-x-3">
                  <button
                      type="button"
                      onClick={() => setCategoriaHabilitada(!categoriaHabilitada)}
                      className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                          categoriaHabilitada ? "bg-green-600" : "bg-gray-300"
                      }`}
                  >
                  <span
                      className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                          categoriaHabilitada ? "translate-x-6" : "translate-x-1"
                      }`}
                  />
                  </button>
                  <label className="text-sm font-medium text-gray-700">
                    {categoriaHabilitada ? "Habilitada" : "Inhabilitada"}
                  </label>
                </div>
              </div>
            </div>

            {/* Botones */}
            <div className="flex justify-end space-x-4">
              <button
                  type="button"
                  onClick={volverACategorias}
                  className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100"
              >
                Cancelar
              </button>
              <button
                  type="submit"
                  className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
              >
                Guardar Categoría
              </button>
            </div>
          </form>
        </div>
      </div>
  );
}
