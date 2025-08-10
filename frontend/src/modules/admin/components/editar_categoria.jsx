import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";


export default function EditarCategoria() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    description: "",
    status: true,
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCategoria = async () => {
      try {
        setLoading(true);
        setError(null);
        const token = localStorage.getItem("accessToken");
        const res = await fetch(`http://localhost:8080/categories/id/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error("Error al cargar la categoría");

        const data = await res.json();

        setFormData({
          name: data.result.name || "",
          description: data.result.description || "",
          status: data.result.status ?? true,
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchCategoria();
  }, [id]);


  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const toggleEstado = async () => {
    try {
      setError(null);
      const token = localStorage.getItem("accessToken");
      const newStatus = !formData.status;

      const body = {
        id: Number(id),
        name: formData.name,
        description: formData.description,
        status: newStatus,
      };

      const res = await fetch("http://localhost:8080/categories/change-status", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(body),
      });

      if (!res.ok) {
        throw new Error("Error al cambiar el estado");
      }

      setFormData((prev) => ({
        ...prev,
        status: newStatus,
      }));
    } catch (err) {
      setError(err.message);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    const result = await Swal.fire({
      title: "¿Confirmas guardar los cambios?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Sí, guardar",
      cancelButtonText: "Cancelar",
    });

    if (!result.isConfirmed) {
      return;
    }

    try {
      const token = localStorage.getItem("accessToken");
      const res = await fetch("http://localhost:8080/categories/update", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          id: Number(id),
          name: formData.name,
          description: formData.description,
          status: formData.status,
        }),
      });

      if (!res.ok) {
        throw new Error("Error al actualizar la categoría");
      }

      await Swal.fire("¡Guardado!", "La categoría se actualizó correctamente.", "success");
      navigate("/admin/categorias");
    } catch (err) {
      Swal.fire("Error", err.message, "error");
    }
  };

  if (loading) return <p>Cargando categoría...</p>;
  if (error) return <p className="text-red-600">Error: {error}</p>;

  return (
      <div className="min-h-screen bg-white p-6">
        <div className="flex justify-between items-center mb-8">
          <div className="flex items-center space-x-4">
            <button
                onClick={() => navigate("/admin/categorias")}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <svg
                  className="w-6 h-6 text-gray-700"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
              >
                <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M15 19l-7-7 7-7"
                />
              </svg>
            </button>
            <div className="flex items-center space-x-3">
              <div className="bg-blue-100 p-3 rounded-lg">
                <svg
                    className="w-7 h-7 text-blue-600"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                >
                  <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M3 7a2 2 0 012-2h5l2 2h7a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2V7z"
                  />
                </svg>
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Editar Categoría</h1>
                <p className="text-sm text-gray-600">
                  Modifica la información y estado de la categoría seleccionada
                </p>
              </div>
            </div>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="max-w-3xl mx-auto space-y-6">
          <div className="bg-white shadow-lg rounded-xl border border-gray-200">
            <div className="px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-800 flex items-center">
                <svg
                    className="w-5 h-5 mr-2"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                >
                  <path
                      d="M7 3h5l2 2h7a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2V7z"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                  />
                </svg>
                Información de la Categoría
              </h3>
            </div>

            <div className="p-6 grid grid-cols-1 gap-6">
              <div>
                <label
                    htmlFor="name"
                    className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Nombre de la Categoría
                </label>
                <input
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Ej. Servicios de Consultoría"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    required
                />
              </div>
              <div>
                <label
                    htmlFor="description"
                    className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Descripción
                </label>
                <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    rows={4}
                    placeholder="Ej. Asesoría especializada..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg resize-none"
                    required
                />
              </div>
            </div>

            <div className="px-6 pb-6">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Estado de la Categoría
              </label>
              <div className="flex items-center space-x-3">
                <button
                    type="button"
                    onClick={toggleEstado}
                    className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${
                        formData.status ? "bg-green-600" : "bg-gray-300"
                    }`}
                    aria-pressed={formData.status}
                    aria-label="Toggle estado"
                >
                <span
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                        formData.status ? "translate-x-6" : "translate-x-1"
                    }`}
                />
                </button>
                <span className="text-sm font-medium text-gray-700">
                {formData.status ? "Habilitada" : "Inhabilitada"}
              </span>
              </div>
            </div>
          </div>

          {error && <p className="text-red-600">{error}</p>}

          <div className="flex justify-end gap-4">
            <button
                type="button"
                onClick={() => navigate("/admin/categorias")}
                className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors duration-200"
            >
              Cancelar
            </button>
            <button
                type="submit"
                className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
            >
              Guardar Cambios
            </button>
          </div>
        </form>
      </div>
  );
}
