import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

export default function EditarUsuario() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    nombre: "",
    apellidos: "",
    correo: "",
    telefono: "",
    estado: "Activo",
    password: "", // si quieres mostrar campo password para actualizar, podrías agregar
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    fetch(`http://localhost:8080/users/id/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
        .then(async (res) => {
          if (!res.ok) throw new Error("Usuario no encontrado");
          const data = await res.json();
          const usuario = data.result;
          setFormData({
            nombre: usuario.name || "",
            apellidos: usuario.last_name || usuario.lastName || "", // probamos varios nombres
            correo: usuario.email || "",
            telefono: usuario.phoneNumber || "",
            estado: usuario.status ? "Activo" : "Inactivo",
            password: "",
          });
          setLoading(false);
        })
        .catch((err) => {
          Swal.fire({
            icon: "error",
            title: "Error",
            text: err.message || "Error al cargar datos del usuario",
          });
          setLoading(false);
        });
  }, [id]);


  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const toggleEstado = () => {
    setFormData((prev) => ({
      ...prev,
      estado: prev.estado === "Activo" ? "Inactivo" : "Activo",
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validación simple
    if (
        !formData.nombre.trim() ||
        !formData.apellidos.trim() ||
        !formData.correo.trim() ||
        !formData.telefono.trim()
    ) {
      return Swal.fire({
        icon: "warning",
        title: "Campos incompletos",
        text: "Por favor, completa todos los campos requeridos.",
      });
    }

    const confirmResult = await Swal.fire({
      title: "¿Estás seguro?",
      text: "¿Quieres guardar los cambios realizados al usuario?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Sí, guardar",
      cancelButtonText: "Cancelar",
    });

    if (!confirmResult.isConfirmed) return;

    try {
      const token = localStorage.getItem("accessToken");
      const body = {
        id: Number(id),
        name: formData.nombre,
        last_name: formData.apellidos,
        email: formData.correo,
        phoneNumber: formData.telefono,
        password: formData.password, // si está vacío, el backend puede ignorar
        status: formData.estado === "Activo",
      };

      const response = await fetch("http://localhost:8080/users/update", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(body),
      });

      const text = await response.text();
      let json;
      try {
        json = JSON.parse(text);
      } catch {
        json = null;
      }

      if (!response.ok) {
        if (json && json.text) {
          return Swal.fire({
            icon: json.type === "WARNING" ? "warning" : "error",
            title: "Error",
            text: json.text,
          });
        }
        return Swal.fire({
          icon: "error",
          title: "Error desconocido",
          text: "Error desconocido al actualizar usuario",
        });
      }

      Swal.fire({
        icon: "success",
        title: "Usuario actualizado",
        text: json?.text || "El usuario se actualizó correctamente",
      }).then(() => {
        navigate("/admin/abogados");
      });
    } catch (error) {
        // Error al actualizar usuario
        Swal.fire("Error", "No se pudo actualizar el usuario", "error");
    }
  };

  if (loading) return <p className="p-6">Cargando datos del usuario...</p>;

  return (
      <div className="min-h-screen bg-white p-6">
        <div className="flex justify-between items-center mb-8">
          <div className="flex items-center space-x-4">
            <button
                onClick={() => navigate("/admin/abogados")}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                aria-label="Volver a lista de abogados"
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
                      d="M17 20h5v-2a4 4 0 00-3-3.87M9 20H4v-2a4 4 0 013-3.87m9-9a4 4 0 11-8 0 4 4 0 018 0zm6 4a4 4 0 11-8 0 4 4 0 018 0zM9 7a4 4 0 11-8 0 4 4 0 018 0z"
                  />
                </svg>
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Editar Abogado</h1>
                <p className="text-sm text-gray-600">
                  Modifica la información y estado del abogado seleccionado
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
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                  />
                </svg>
                Información del Abogado
              </h3>
            </div>

            <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label
                    htmlFor="nombre"
                    className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Nombre *
                </label>
                <input
                    id="nombre"
                    name="nombre"
                    value={formData.nombre}
                    onChange={handleChange}
                    placeholder="Ej. Carlos"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    required
                />
              </div>
              <div>
                <label
                    htmlFor="apellidos"
                    className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Apellidos *
                </label>
                <input
                    id="apellidos"
                    name="apellidos"
                    value={formData.apellidos}
                    onChange={handleChange}
                    placeholder="Ej. Galán Hernández"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    required
                />
              </div>
              <div>
                <label
                    htmlFor="correo"
                    className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Correo electrónico *
                </label>
                <input
                    id="correo"
                    name="correo"
                    value={formData.correo}
                    onChange={handleChange}
                    placeholder="abogado@firma.com"
                    type="email"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    required
                />
              </div>
              <div>
                <label
                    htmlFor="telefono"
                    className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Teléfono *
                </label>
                <input
                    id="telefono"
                    name="telefono"
                    value={formData.telefono}
                    onChange={handleChange}
                    placeholder="7770000000"
                    type="tel"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    required
                />
              </div>

              {/* Opcional: campo password si quieres actualizar contraseña */}
              {/* <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                Contraseña (dejar vacío para no cambiar)
              </label>
              <input
                id="password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="********"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              />
            </div> */}
            </div>

            <div className="px-6 pb-6">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Estado del Abogado
              </label>
              <div className="flex items-center space-x-3">
                <button
                    type="button"
                    onClick={toggleEstado}
                    className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${
                        formData.estado === "Activo" ? "bg-green-600" : "bg-gray-300"
                    }`}
                    aria-pressed={formData.estado === "Activo"}
                    aria-label="Toggle estado del abogado"
                >
                <span
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                        formData.estado === "Activo" ? "translate-x-6" : "translate-x-1"
                    }`}
                />
                </button>
                <span className="text-sm font-medium text-gray-700">
                {formData.estado === "Activo" ? "Habilitado" : "Inhabilitado"}
              </span>
              </div>
            </div>
          </div>

          <div className="flex justify-end gap-4">
            <button
                type="button"
                onClick={() => navigate("/admin/abogados")}
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
