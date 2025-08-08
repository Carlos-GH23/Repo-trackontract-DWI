import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

const clientesMock = [
  {
    nombreEmpresa: "Tech Solutions",
    razonSocial: "Tech Solutions S.A. de C.V.",
    nombreRepresentante: "Carlos",
    apellidoRepresentante: "Ramírez",
    correoRepresentante: "carlos@techsolutions.com",
    telefonoRepresentante: "5551234567",
    estado: "Activo",
  },
  {
    nombreEmpresa: "EcoServicios",
    razonSocial: "EcoServicios MX",
    nombreRepresentante: "Lucía",
    apellidoRepresentante: "Torres",
    correoRepresentante: "lucia@ecoservicios.mx",
    telefonoRepresentante: "5559876543",
    estado: "Inactivo",
  },
];

export default function EditarCliente() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    nombreEmpresa: "",
    razonSocial: "",
    nombreRepresentante: "",
    apellidoRepresentante: "",
    correoRepresentante: "",
    telefonoRepresentante: "",
    estado: "Activo",
  });

  useEffect(() => {
    const cliente = clientesMock[id];
    if (cliente) {
      setFormData(cliente);
    } else {
      console.warn("Cliente no encontrado");
    }
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

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Cliente actualizado:", formData);
    navigate("/admin/clientes");
  };

  return (
    <div className="min-h-screen bg-white p-6">
      <div className="flex justify-between items-center mb-8">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate("/admin/clientes")}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <svg
              className="w-6 h-6 text-gray-700"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
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
              <h1 className="text-2xl font-bold text-gray-900">Editar Cliente</h1>
              <p className="text-sm text-gray-600">
                Modifica la información del cliente seleccionado
              </p>
            </div>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="max-w-6xl mx-auto space-y-6">
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
              Información del Cliente
            </h3>
          </div>

          <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4">
            <div className="w-full">
              <label className="block text-sm font-medium text-gray-700 mb-2">Nombre de la empresa</label>
              <input
                name="nombreEmpresa"
                value={formData.nombreEmpresa}
                onChange={handleChange}
                placeholder="Ej. Tech Solutions"
                className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
              />
            </div>

            <div className="w-full">
              <label className="block text-sm font-medium text-gray-700 mb-2">Razón Social</label>
              <input
                name="razonSocial"
                value={formData.razonSocial}
                onChange={handleChange}
                placeholder="Ej. Tech Solutions S.A. de C.V."
                className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
              />
            </div>

            <div className="w-full">
              <label className="block text-sm font-medium text-gray-700 mb-2">Nombre del representante</label>
              <input
                name="nombreRepresentante"
                value={formData.nombreRepresentante}
                onChange={handleChange}
                placeholder="Ej. Carlos"
                className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
              />
            </div>

            <div className="w-full">
              <label className="block text-sm font-medium text-gray-700 mb-2">Apellido del representante</label>
              <input
                name="apellidoRepresentante"
                value={formData.apellidoRepresentante}
                onChange={handleChange}
                placeholder="Ej. Ramírez"
                className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
              />
            </div>

            <div className="w-full">
              <label className="block text-sm font-medium text-gray-700 mb-2">Correo del representante</label>
              <input
                name="correoRepresentante"
                value={formData.correoRepresentante}
                onChange={handleChange}
                placeholder="carlos@techsolutions.com"
                type="email"
                className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
              />
            </div>

            <div className="w-full">
              <label className="block text-sm font-medium text-gray-700 mb-2">Teléfono del representante</label>
              <input
                name="telefonoRepresentante"
                value={formData.telefonoRepresentante}
                onChange={handleChange}
                placeholder="5551234567"
                type="tel"
                className="w-full min-w-[400px] px-5 py-3 border border-gray-300 rounded-lg"
              />
            </div>
          </div>

          <div className="px-6 pb-6">
            <label className="block text-sm font-medium text-gray-700 mb-3">Estado del Cliente</label>
            <div className="flex items-center space-x-3">
              <button
                type="button"
                onClick={toggleEstado}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${
                  formData.estado === "Activo" ? "bg-green-600" : "bg-gray-300"
                }`}
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
            onClick={() => navigate("/admin/clientes")}
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
