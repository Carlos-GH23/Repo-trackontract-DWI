import { useEffect, useState } from "react";
import Swal from "sweetalert2";
import "../../../styles/perfil.styles.css";

const ProfileCompa = () => {
    const [profile, setProfile] = useState({
        id: null,
        name: "",
        business_name: "",
        representative_name: "",
        representative_surnames: "",
        email: "",
        phone: "",
        status: true
    });

    const [editMode, setEditMode] = useState(false);
    const [errorMsg, setErrorMsg] = useState("");
    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [passwordData, setPasswordData] = useState({
        newPassword: "",
        confirmPassword: ""
    });
    const [passwordError, setPasswordError] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const ensureEmailAndFetch = async () => {
                const token = localStorage.getItem("accessToken");
                if (!token) return setErrorMsg("Falta token. Inicia sesión nuevamente.");

                    let userEmail = localStorage.getItem("email");
                if (!userEmail) {
                      try {
                            const me = await fetch("http://localhost:8080/users/me", {
                                  headers: { Authorization: `Bearer ${token}` }
                            });
                            if (me.ok) {
                                  const d = await me.json();
                                  if (d?.email) {
                                        userEmail = d.email;
                                        localStorage.setItem("email", userEmail);
                                      }
                                }
                          } catch {}
                    }
                fetchProfile();
              };
          ensureEmailAndFetch();
    }, []);

    const fetchProfile = async () => {
        try {
            setLoading(true);
            setErrorMsg("");

            const token = localStorage.getItem("accessToken");
            const userEmail = localStorage.getItem("email");



            if (!token || !userEmail) {
                const missingItems = [];
                if (!token) missingItems.push("Token de acceso");
                if (!userEmail) missingItems.push("Email del usuario");

                setErrorMsg(`Faltan datos de autenticación: ${missingItems.join(", ")}. Por favor, inicia sesión nuevamente.`);
                return;
            }



            const response = await fetch(`http://localhost:8080/clients/me?email=${encodeURIComponent(userEmail)}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
<<<<<<< HEAD
            
            
=======

            console.log("Respuesta del servidor:", response.status, response.statusText);

>>>>>>> 09f070b5832f0e7c1054cbe801528654b8131fd6
            if (!response.ok) {
                if (response.status === 403) {
                    throw new Error("Acceso denegado. Verifica que tengas permisos para acceder a este recurso.");
                } else if (response.status === 404) {
                    throw new Error("Perfil no encontrado. Verifica que el email sea correcto.");
                } else {
                    throw new Error(`Error del servidor: ${response.status} ${response.statusText}`);
                }
            }

            const data = await response.json();
            if (data.result) {
                setProfile({
                    id: data.result.id || null,
                    name: data.result.name || "",
                    business_name: data.result.business_name || "",
                    representative_name: data.result.representative_name || "",
                    representative_surnames: data.result.representative_surnames || "",
                    email: data.result.email || "",
                    phone: data.result.phone || "",
                    status: data.result.status ?? true
                });
            }
        } catch (error) {
            // Error al obtener perfil
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudo cargar el perfil.",
                timer: 2000,
                showConfirmButton: false
            });
        } finally {
            setLoading(false);
        }
    };

    const handleChange = e => {
        const { name, value } = e.target;
        setProfile(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handlePasswordChange = e => {
        const { name, value } = e.target;
        setPasswordData(prev => ({
            ...prev,
            [name]: value
        }));
        setPasswordError(""); // Limpiar error al escribir
    };

    const handleSave = async () => {
        setErrorMsg(""); // limpiar errores

        if (!profile.representative_name || profile.representative_name.trim() === "") {
            setErrorMsg("El nombre del representante no puede estar vacío.");
            return;
        }
        if (!profile.phone || profile.phone.trim() === "") {
            setErrorMsg("El número de teléfono no puede estar vacío.");
            return;
        }
        if (!profile.email || profile.email.trim() === "") {
            setErrorMsg("El correo electrónico no puede estar vacío.");
            return;
        }

        try {
            const token = localStorage.getItem("accessToken");
            const userEmail = localStorage.getItem("email");

            if (!token || !userEmail) {
                setErrorMsg("No se pudo obtener la información de autenticación");
                return;
            }

            const response = await fetch(`http://localhost:8080/clients/me/profile?email=${encodeURIComponent(userEmail)}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    id: profile.id,
                    name: profile.name, // Solo lectura
                    business_name: profile.business_name, // Solo lectura
                    representative_name: profile.representative_name, // Editable
                    representative_surnames: profile.representative_surnames, // Solo lectura
                    email: profile.email, // Editable
                    phone: profile.phone, // Editable
                    status: profile.status
                })
            });

            if (response.status === 401 || response.status === 403) {
                   Swal.fire({ icon: "warning", title: "Sesión expirada", text: "Vuelve a iniciar sesión." })
                     .then(() => { localStorage.clear(); window.location.href = "/"; });
                   return;
                 }

            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.text || "Error al actualizar");
            }

            setEditMode(false);
            Swal.fire({
                icon: "success",
                title: "¡Éxito!",
                text: "Perfil actualizado con éxito",
                timer: 2000,
                showConfirmButton: false
            });
        } catch (error) {
            // Error al actualizar perfil
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudo actualizar el perfil.",
                timer: 2000,
                showConfirmButton: false
            });
        }
    };

    const handlePasswordChangeSubmit = async () => {
        setPasswordError("");

        // Validaciones
        if (!passwordData.newPassword || !passwordData.confirmPassword) {
            setPasswordError("Todos los campos son obligatorios.");
            return;
        }

        if (passwordData.newPassword !== passwordData.confirmPassword) {
            setPasswordError("Las contraseñas nuevas no coinciden.");
            return;
        }

        if (passwordData.newPassword.length < 8) {
            setPasswordError("La nueva contraseña debe tener al menos 8 caracteres.");
            return;
        }

        try {
            const token = localStorage.getItem("accessToken");
            const userEmail = localStorage.getItem("email");

            if (!token || !userEmail) {
                setPasswordError("No se pudo obtener la información de autenticación");
                return;
            }

            const response = await fetch(`http://localhost:8080/clients/me/password?email=${encodeURIComponent(userEmail)}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    newPassword: passwordData.newPassword,
                    confirmPassword: passwordData.confirmPassword
                })
            });

            if (response.status === 401 || response.status === 403) {
                   Swal.fire({ icon: "warning", title: "Sesión expirada", text: "Vuelve a iniciar sesión." })
                     .then(() => { localStorage.clear(); window.location.href = "/"; });
                   return;
                 }

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.text || "Error al cambiar la contraseña");
            }

            // Éxito - Cerrar sesión automáticamente
            Swal.fire({
                icon: "success",
                title: "¡Contraseña actualizada!",
                text: "Tu contraseña ha sido cambiada exitosamente. Por seguridad, tu sesión será cerrada.",
                confirmButtonText: "Entendido"
            }).then(() => {
                // Limpiar localStorage
                localStorage.clear();

                // Redirigir al login
                window.location.href = "/";
            });

        } catch (error) {
            setPasswordError(error.message);
        }
    };

    const openPasswordModal = () => {
        setShowPasswordModal(true);
        setPasswordData({
            newPassword: "",
            confirmPassword: ""
        });
        setPasswordError("");
    };

    const closePasswordModal = () => {
        setShowPasswordModal(false);
        setPasswordData({
            newPassword: "",
            confirmPassword: ""
        });
        setPasswordError("");
    };

    if (loading) {
        return (
            <div className="datos">
                <div style={{ textAlign: "center", padding: "50px" }}>
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                    <p>Cargando perfil...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="datos">
            <h2 className="titulo">Perfil de la Empresa</h2>

            <div className="contenido">
                <div className="info">
                    <div className="campo">
                        <label htmlFor="name">Nombre de la Empresa:</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={profile.name}
                            readOnly
                            style={{ backgroundColor: "#f5f5f5", color: "#666" }}
                        />
                    </div>
                    <div className="campo">
                        <label htmlFor="business_name">Nombre del Negocio:</label>
                        <input
                            type="text"
                            id="business_name"
                            name="business_name"
                            value={profile.business_name}
                            readOnly
                            style={{ backgroundColor: "#f5f5f5", color: "#666" }}
                        />
                    </div>
                    <div className="campo">
                        <label htmlFor="email">Correo Electrónico:</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={profile.email}
                            onChange={handleChange}
                            readOnly={!editMode}
                            style={!editMode ? { backgroundColor: "#f5f5f5", color: "#666" } : {}}
                        />
                    </div>
                </div>

                <div className="info1">
                    <div className="campo">
                        <label htmlFor="representative_name">Nombre del Representante:</label>
                        <input
                            type="text"
                            id="representative_name"
                            name="representative_name"
                            value={profile.representative_name}
                            onChange={handleChange}
                            readOnly={!editMode}
                            style={!editMode ? { backgroundColor: "#f5f5f5", color: "#666" } : {}}
                        />
                    </div>
                    <div className="campo">
                        <label htmlFor="representative_surnames">Apellidos del Representante:</label>
                        <input
                            type="text"
                            id="representative_surnames"
                            name="representative_surnames"
                            value={profile.representative_surnames}
                            readOnly
                            style={{ backgroundColor: "#f5f5f5", color: "#666" }}
                        />
                    </div>
                    <div className="campo">
                        <label htmlFor="phone">Teléfono:</label>
                        <input
                            type="tel"
                            id="phone"
                            name="phone"
                            value={profile.phone}
                            onChange={handleChange}
                            readOnly={!editMode}
                            style={!editMode ? { backgroundColor: "#f5f5f5", color: "#666" } : {}}
                        />
                    </div>
                </div>
            </div>

            {errorMsg && (
                <div style={{ textAlign: "center", margin: "10px 0" }}>
                    <p style={{ color: "red", marginBottom: "10px" }}>{errorMsg}</p>
                    <button
                        onClick={fetchProfile}
                        className="btn"
                        style={{ backgroundColor: "#007bff", color: "white" }}
                    >
                        Reintentar
                    </button>
                </div>
            )}

            <div className="boton-container">
                {editMode ? (
                    <>
                        <button className="btn" onClick={handleSave}>
                            Guardar
                        </button>
                        <button className="btn" onClick={() => setEditMode(false)}>
                            Cancelar
                        </button>
                    </>
                ) : (
                    <>
                        <button className="btn" onClick={() => setEditMode(true)}>
                            Editar Perfil
                        </button>
                        <button className="btn" onClick={openPasswordModal} style={{ marginLeft: "10px", backgroundColor: "#28a745" }}>
                            Nueva Contraseña
                        </button>
                    </>
                )}
            </div>

            {/* Modal de Cambio de Contraseña */}
            {showPasswordModal && (
                <div className="password-modal-overlay" onClick={closePasswordModal}>
                    <div className="password-modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Nueva Contraseña</h3>

                        <div className="fila-contrasenas">
                            <div className="campo">
                                <label htmlFor="newPassword">Nueva Contraseña:</label>
                                <input
                                    type="password"
                                    id="newPassword"
                                    name="newPassword"
                                    value={passwordData.newPassword}
                                    onChange={handlePasswordChange}
                                    placeholder="Mínimo 8 caracteres"
                                />
                            </div>
                            <div className="campo">
                                <label htmlFor="confirmPassword">Confirmar Nueva Contraseña:</label>
                                <input
                                    type="password"
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    value={passwordData.confirmPassword}
                                    onChange={handlePasswordChange}
                                    placeholder="Repite la nueva contraseña"
                                />
                            </div>
                        </div>

                        {passwordError && (
                            <p style={{ color: "red", textAlign: "center", margin: "10px 0", fontSize: "14px" }}>
                                {passwordError}
                            </p>
                        )}

                        <div className="boton-container1">
                            <button className="btn" onClick={handlePasswordChangeSubmit}>
                                Actualizar Contraseña
                            </button>
                            <button className="btn" onClick={closePasswordModal} style={{ marginLeft: "10px", backgroundColor: "#6c757d" }}>
                                Cancelar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ProfileCompa;