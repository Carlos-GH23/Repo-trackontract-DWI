import "../../../styles/perfil.styles.css";
import { useEffect, useState } from "react";
import Swal from "sweetalert2";

const ProfileAbo = () => {
  const [profile, setProfile] = useState({
    id: null,
    name: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    status: true, // Agregar el status
    roleName: ""
  });

  const [editMode, setEditMode] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordData, setPasswordData] = useState({
    newPassword: "",
    confirmPassword: ""
  });
  const [passwordError, setPasswordError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) return;

    fetch("http://localhost:8080/users/me", {
      headers: { Authorization: `Bearer ${token}` }
    })
        .then(res => {
          if (!res.ok) throw new Error("Error al obtener perfil");
          return res.json();
        })
        .then(data => {
          setProfile({
            id: data.id || null,
            name: data.name || "",
            lastName: data.lastName || "",
            email: data.email || "",
            phoneNumber: data.phoneNumber || "",
            status: data.status || true, // Agregar el status
            roleName: data.roleName || ""
          });
        })
        .catch(err => {
          console.error(err);
          setErrorMsg("No se pudo cargar el perfil.");
        });
  }, []);

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

  const handleSave = () => {
    setErrorMsg(""); // limpiar errores

    if (!profile.phoneNumber || profile.phoneNumber.trim() === "") {
      setErrorMsg("El número de teléfono no puede estar vacío.");
      return;
    }

    const token = localStorage.getItem("accessToken");
    if (!token || !profile.id) return;

    fetch("http://localhost:8080/users/update", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({
        id: profile.id,
        name: profile.name,
        last_name: profile.lastName,
        email: profile.email,
        phoneNumber: profile.phoneNumber,
        status: profile.status
      })
    })
        .then(async res => {
          if (!res.ok) {
            const err = await res.json();
            throw new Error(err.text || "Error al actualizar");
          }
          return res.json();
        })
        .then(() => {
          setEditMode(false);
          Swal.fire({
            icon: "success",
            title: "¡Éxito!",
            text: "Perfil actualizado con éxito",
            timer: 2000,
            showConfirmButton: false
          });
        })
        .catch(err => {
          console.error(err);
          setErrorMsg(err.message);
        });
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
      const response = await fetch("http://localhost:8080/users/me/password/update", {
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

  return (
      <div className="datos">
        <h2 className="titulo">Perfil del Abogado</h2>

        <div className="contenido">
          <div className="info">
            <div className="campo">
              <label htmlFor="name">Nombre(s):</label>
              <input
                  type="text"
                  id="name"
                  name="name"
                  value={profile.name}
                  onChange={handleChange}
                  readOnly={!editMode}
              />
            </div>
            <div className="campo">
              <label htmlFor="lastName">Apellidos:</label>
              <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={profile.lastName}
                  onChange={handleChange}
                  readOnly={!editMode}
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
              />
            </div>
          </div>

          <div className="info1">
            <div className="campo">
              <label htmlFor="telefono">Teléfono:</label>
              <input
                  type="tel"
                  id="telefono"
                  name="phoneNumber"
                  value={profile.phoneNumber}
                  onChange={handleChange}
                  readOnly={!editMode}
              />
            </div>
            <div className="campo">
              <label htmlFor="rol">Rol:</label>
              <input type="text" id="rol" value={profile.roleName} readOnly />
            </div>
          </div>
        </div>

        {errorMsg && <p style={{ color: "red", textAlign: "center", margin: "10px 0" }}>{errorMsg}</p>}

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

export default ProfileAbo;
