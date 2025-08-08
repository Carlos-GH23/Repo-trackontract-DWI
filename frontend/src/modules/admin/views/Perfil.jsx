import "../../../styles/navbarbo.styles.css"

const ProfileAdmin = () => {
  return (
    <>
      <div className="datos">
        <h2 className="titulo">Datos del Admin</h2>

        <div className="contenido">
          <div className="info">
            <div className="campo">
              <label htmlFor="name">Nombre(s):</label>
              <input type="text" id="name" name="name" placeholder="Carlos Galan Hernandez" />
            </div>
            <div className="campo">
              <label htmlFor="email">Correo Electrónico:</label>
              <input type="email" id="email" name="email" placeholder="admin@utez.edu.mx" />
            </div>
          </div>

          <div className="info1">
            <div className="campo">
              <label htmlFor="telefono">Teléfono:</label>
              <input type="tel" id="telefono" name="telefono" placeholder="7771234567" />
            </div>
            <div className="campo">
              <label htmlFor="cedula">Cédula Profesional:</label>
              <input type="text" id="cedula" name="cedula" placeholder="12345678" />
            </div>
          </div>
        </div>
        <div className="boton-container">
          <button className="btn">Actualizar</button>
        </div>
      </div>
      <div className="datos">
        <h2 className="titulo">Cambio de contraseña</h2>

        <div className="fila-contrasenas">
          <div className="campo">
            <label htmlFor="password">Contraseña:</label>
            <input type="password" id="password" name="password" placeholder="********" />
          </div>

          <div className="campo">
            <label htmlFor="password1">Contraseña actual:</label>
            <input type="password" id="password1" name="password1" placeholder="********" />
          </div>

          <div className="campo">
            <label htmlFor="password2">Contraseña nueva:</label>
            <input type="password" id="password2" name="password2" placeholder="Ingresa tu nueva contraseña" />
          </div>
        </div>

        <div className="boton-container1">
          <button className="btn">Guardar Contraseña</button>
        </div>
      </div>

    </>
  );
}

export default ProfileAdmin;
