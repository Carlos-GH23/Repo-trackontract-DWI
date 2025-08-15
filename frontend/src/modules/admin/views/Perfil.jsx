import { useEffect, useState } from "react";
import Swal from "sweetalert2";
import "../../../styles/perfil.styles.css";

const ProfileAdmin = () => {
  const [profile, setProfile] = useState({
    id: null,
    name: "",
    lastName: "",
    email: "",
    phoneNumber: ""
  });

  const [emailOriginal, setEmailOriginal] = useState("");
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) return;

    fetch("http://localhost:8080/users/me", {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
        .then(res => {
          if (!res.ok) throw new Error("Error al obtener perfil");
          return res.json();
        })
        .then(data => {
          setProfile({
            id: data.id,
            name: data.name,
            lastName: data.lastName,
            email: data.email,
            phoneNumber: data.phoneNumber
          });
          setEmailOriginal(data.email); // Guardamos el email original para comparar luego
        })
        .catch(err => {
          // Error al obtener perfil
          Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se pudo cargar el perfil, inicia sesión nuevamente.",
            confirmButtonText: "Aceptar"
          }).then(() => {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            window.location.href = "/";
          });
        })
        .finally(() => setLoading(false));
  }, []);

  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setMessage(null);

    const token = localStorage.getItem("accessToken");
    if (!token) {
      Swal.fire({
        icon: "error",
        title: "No autorizado",
        text: "Por favor inicia sesión.",
        confirmButtonText: "Aceptar"
      }).then(() => {
        window.location.href = "/";
      });
      return;
    }

    fetch("http://localhost:8080/users/me", {
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
        phoneNumber: profile.phoneNumber
      })
    })
        .then(async (res) => {
          const body = await res.json();

          if (res.status === 401 || res.status === 403) {
            Swal.fire({
              icon: "warning",
              title: "Sesión expirada",
              text: "Tu sesión ha expirado o tus datos han cambiado. Vuelve a iniciar sesión.",
              confirmButtonText: "Aceptar"
            }).then(() => {
              localStorage.removeItem("accessToken");
              localStorage.removeItem("refreshToken");
              window.location.href = "/";
            });
            return;
          }

          if (res.ok) {
            if (profile.email !== emailOriginal) {
              Swal.fire({
                icon: "success",
                title: "Perfil actualizado",
                text: "Has cambiado tu correo, por seguridad debes iniciar sesión nuevamente.",
                confirmButtonText: "Aceptar"
              }).then(() => {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/";
              });
            } else {
              Swal.fire({
                icon: "success",
                title: "Perfil actualizado",
                text: "Cambios guardados correctamente.",
                confirmButtonText: "Aceptar"
              });
              setEmailOriginal(profile.email); // Actualizamos el email original
            }
          } else {
            setMessage(body.text || "Error al actualizar perfil");
          }
        })
        .catch(() => setMessage("Error de conexión"));
  };

  if (loading) return <p>Cargando...</p>;

  return (
      <div className="datos">
        <h2 className="titulo">Editar Perfil</h2>

        {message && <p className="mensaje">{message}</p>}

        <form onSubmit={handleSubmit} className="contenido">
          <div className="info">
            <div className="campo">
              <label htmlFor="name">Nombre(s):</label>
              <input
                  type="text"
                  id="name"
                  name="name"
                  value={profile.name}
                  onChange={handleChange}
              />
            </div>
            <div className="campo">
              <label htmlFor="lastName">Apellido(s):</label>
              <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={profile.lastName}
                  onChange={handleChange}
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
              />
            </div>
          </div>

          <div className="info1">
            <div className="campo">
              <label htmlFor="phoneNumber">Teléfono:</label>
              <input
                  type="tel"
                  id="phoneNumber"
                  name="phoneNumber"
                  value={profile.phoneNumber}
                  onChange={handleChange}
              />
            </div>
          </div>

          <div className="boton-container">
            <button type="submit" className="btn">
              Guardar cambios
            </button>
          </div>
        </form>
      </div>
  );
};

export default ProfileAdmin;
