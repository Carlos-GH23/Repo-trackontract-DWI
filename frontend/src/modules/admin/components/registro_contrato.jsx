import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import Swal from 'sweetalert2'

export default function RegistroContrato() {
  const [formData, setFormData] = useState({
    name: "",
    client_id: "",
    category_id: "",
    abogado_id: "", // Agregar campo para abogado
    due_date: "",
    description: "",
    status: true // Added status field
  })
  const [clientes, setClientes] = useState([])
  const [categorias, setCategorias] = useState([])
  const [abogados, setAbogados] = useState([]) // Agregar estado para abogados
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  // Cargar clientes, categorías y abogados al montar el componente
  useEffect(() => {
    fetchClientes()
    fetchCategorias()
    fetchAbogados() // Agregar llamada para obtener abogados
  }, [])

  // Función para obtener clientes
  const fetchClientes = async () => {
    try {
      const token = localStorage.getItem("accessToken")
      
      if (!token) {
        throw new Error("No hay token de autenticación")
      }

      const response = await fetch("http://localhost:8080/clients/all", {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      })

      if (!response.ok) {
        throw new Error("Error al obtener clientes")
      }

      const data = await response.json()
      if (data.result) {
        setClientes(data.result)
      } else {
        setClientes([])
      }
    } catch (err) {
      console.error("Error al obtener clientes:", err)
      setError("Error al cargar clientes: " + err.message)
    }
  }

  // Función para obtener categorías
  const fetchCategorias = async () => {
    try {
      const token = localStorage.getItem("accessToken")
      
      if (!token) {
        throw new Error("No hay token de autenticación")
      }

      const response = await fetch("http://localhost:8080/categories/all", {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      })

      if (!response.ok) {
        throw new Error("Error al obtener categorías")
      }

      const data = await response.json()
      if (data.result) {
        setCategorias(data.result)
      } else {
        setCategorias([])
      }
    } catch (err) {
      console.error("Error al obtener categorías:", err)
      setError("Error al cargar categorías: " + err.message)
    }
  }

  // Función para obtener abogados
  const fetchAbogados = async () => {
    try {
      const token = localStorage.getItem("accessToken")
      
      if (!token) {
        throw new Error("No hay token de autenticación")
      }

      const response = await fetch("http://localhost:8080/users/by-role/ABOGADO", {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      })

      if (!response.ok) {
        throw new Error("Error al obtener abogados")
      }

      const data = await response.json()
      if (data.result) {
        setAbogados(data.result)
      } else {
        setAbogados([])
      }
    } catch (err) {
      console.error("Error al obtener abogados:", err)
      setError("Error al cargar abogados: " + err.message)
    }
  }

  // Función para manejar cambios en el formulario
  const handleInputChange = (e) => {
    const { name, value } = e.target
    
    setFormData(prev => {
      const newState = {
        ...prev,
        [name]: value
      }
      return newState
    })
  }

  // Función para enviar el contrato
  const handleSubmit = async (e) => {
    e.preventDefault()
    
    // Validar campos requeridos
    if (!formData.name || !formData.client_id || !formData.category_id || !formData.due_date) {
      setError("Por favor completa todos los campos requeridos")
      return
    }

    // Confirmar antes de crear
    const result = await Swal.fire({
      title: "¿Confirmas crear este contrato?",
      text: `Se creará el contrato "${formData.name}"`,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3B82F6",
      cancelButtonColor: "#6B7280",
      confirmButtonText: "Sí, crear",
      cancelButtonText: "Cancelar",
    });

    if (!result.isConfirmed) return;

    try {
      setLoading(true)
      setError(null)
      
      const token = localStorage.getItem("accessToken")
      
      if (!token) {
        throw new Error("No hay token de autenticación")
      }

      // Preparar datos para enviar
      const contratoData = {
        name: formData.name,
        description: formData.description,
        due_date: formData.due_date,
        status: formData.status, // Use formData.status
        clientsDTO: {
          id: parseInt(formData.client_id)
        },
        categoriesDTO: {
          id: parseInt(formData.category_id)
        },
        abogadoDTO: {
          id: parseInt(formData.abogado_id)
        }
      }

      const response = await fetch("http://localhost:8080/contracts/save", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(contratoData)
      })

      if (!response.ok) {
        const errorData = await response.json()
        console.log("Error response body:", errorData)
        
        // Extraer el mensaje de error del backend
        let errorMessage = "Error al crear contrato"
        if (errorData.message) {
          errorMessage = errorData.message
        } else if (errorData.text) {
          errorMessage = errorData.text
        } else if (errorData.error) {
          errorMessage = errorData.error
        }
        
        throw new Error(errorMessage)
      }

      // Éxito - redirigir a la lista de contratos
      Swal.fire({
        title: '¡Éxito!',
        text: 'Contrato creado exitosamente',
        icon: 'success',
        confirmButtonText: 'Continuar',
        confirmButtonColor: '#3B82F6'
      }).then(() => {
        navigate("/admin/contratos")
      })
      
    } catch (err) {
      console.error("Error al crear contrato:", err)
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const volverAContratos = () => {
    navigate("/admin/contratos")
  }

  if (error && !clientes.length && !categorias.length) {
    return (
      <div className="min-h-screen bg-white p-6 flex items-center justify-center">
        <div className="text-center">
          <div className="text-red-600 text-xl mb-4">Error al cargar datos</div>
          <p className="text-gray-600 mb-4">{error}</p>
          <button 
            onClick={() => {
              fetchClientes()
              fetchCategorias()
            }}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
          >
            Reintentar
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-white p-6">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center mb-8">
          <button
            onClick={volverAContratos}
            className="mr-4 p-2 hover:bg-gray-100 rounded-lg transition duration-200 flex items-center justify-center shadow-sm"
          >
            <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <div className="flex items-center">
            <div className="bg-blue-100 p-3 rounded-lg mr-4 shadow-sm">
              <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Registro de Nuevo Contrato</h1>
              <p className="text-sm text-gray-600">Panel de Administración - Gestión de Contratos</p>
            </div>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6 shadow">
            <p className="text-sm text-red-700">{error}</p>
          </div>
        )}

        <div className="bg-white rounded-xl shadow-2xl border border-blue-200 overflow-hidden">
          <div className="bg-blue-50 px-6 py-4 border-b border-blue-200">
            <h3 className="text-lg font-semibold text-gray-800 flex items-center">
              <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center mr-3 shadow-sm">
                <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              Información del contrato
            </h3>
          </div>

          <form onSubmit={handleSubmit} className="p-6 space-y-6">
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">Nombre del Contrato *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                placeholder="Contrato de Servicios Profesionales 2025"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
                required
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Cliente *</label>
                <select
                  name="client_id"
                  value={formData.client_id}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm"
                  required
                >
                  <option value="">Seleccione un cliente</option>
                  {clientes.map(cliente => (
                    <option key={cliente.id} value={cliente.id}>
                      {cliente.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Categoría *</label>
                <select
                  name="category_id"
                  value={formData.category_id}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm"
                  required
                >
                  <option value="">Seleccione una categoría</option>
                  {categorias.map(categoria => (
                    <option key={categoria.id} value={categoria.id}>
                      {categoria.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Abogado *</label>
                <select
                  name="abogado_id"
                  value={formData.abogado_id}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm"
                  required
                >
                  <option value="">Seleccione un abogado</option>
                  {abogados.map(abogado => (
                    <option key={abogado.id} value={abogado.id}>
                      {abogado.name} {abogado.lastName}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Fecha de Vencimiento *</label>
                <input
                  type="date"
                  name="due_date"
                  value={formData.due_date}
                  onChange={handleInputChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Descripción</label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="Descripción detallada del contrato..."
                  rows="3"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
                />
              </div>
            </div>

            {/* Estado del Contrato */}
            <div className="bg-gray-50 p-4 rounded-lg border border-gray-200">
              <div className="flex items-center justify-between">
                <div className="flex items-center">
                  <div className="w-8 h-8 bg-gray-100 rounded-lg flex items-center justify-center mr-3 shadow-sm">
                    <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                  </div>
                  <span className="text-sm font-medium text-gray-700">Estado del Contrato</span>
                </div>
                <div className="flex items-center">
                  <span className={`text-sm font-medium mr-3 ${formData.status ? 'text-green-600' : 'text-gray-500'}`}>
                    {formData.status ? 'Habilitado' : 'Deshabilitado'}
                  </span>
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, status: !prev.status }))}
                    className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${
                      formData.status ? 'bg-green-600' : 'bg-gray-200'
                    }`}
                  >
                    <span
                      className={`inline-block h-4 w-4 transform rounded-full bg-white transition duration-200 ease-in-out ${
                        formData.status ? 'translate-x-6' : 'translate-x-1'
                      }`}
                    />
                  </button>
                </div>
              </div>
            </div>

            <div className="flex gap-4 pt-6">
              <button
                type="button"
                onClick={volverAContratos}
                className="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors duration-200 shadow-sm"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-6 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white rounded-lg transition-colors duration-200 shadow-sm flex items-center gap-2"
              >
                {loading ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    Creando...
                  </>
                ) : (
                  "Crear Contrato"
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
