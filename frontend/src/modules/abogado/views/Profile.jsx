import "../../../styles/perfil.styles.css";
import { useEffect, useState } from "react";

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
          alert("Perfil actualizado con éxito");
        })
        .catch(err => {
          console.error(err);
          setErrorMsg(err.message);
        });
  };

  return (
      <div className="datos">
        <h2 className="titulo">Datos del Admin</h2>

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
                  readOnly={editMode}
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
                  readOnly={editMode}
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
                  readOnly={editMode}
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
                  readOnly={editMode}
              />
            </div>
            <div className="campo">
              <label htmlFor="rol">Rol:</label>
              <input type="text" id="rol" value={profile.roleName} readOnly />
            </div>
          </div>
        </div>

        {errorMsg && <p style={{ color: "red" }}>{errorMsg}</p>}

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
              <button className="btn" onClick={() => setEditMode(true)}>
                Editar
              </button>
          )}
        </div>
      </div>
  );
};

export default ProfileAbo;
