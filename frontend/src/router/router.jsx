// src/routes/AppRouter.jsx
import { BrowserRouter, Routes, Route } from "react-router-dom"
import Login from "../modules/auth/views/Login"
import PasswordRecoveryForm from "../modules/auth/views/PasswordRecoveryForm"
import LayoutAdmin from "../modules/admin/components/layout/layoutAdmin"
import LayoutAbo from "../modules/abogado/components/layout/LayoutAbo"
import Contratos from "../modules/admin/views/Contratos"
import RegistroContrato from "../modules/admin/components/registro_contrato"
import Categorias from "../modules/admin/views/Categorias"
import RegistroCategoria from "../modules/admin/components/registro_categoria"
import Usuarios from "../modules/admin/views/Usuarios"
import RegistroUsuario from "../modules/admin/components/registro_usuario"
import EditarUsuario from "../modules/admin/components/editar_usuario"
import EditarCategoria from "../modules/admin/components/editar_categoria"
import ProfileAbo from "../modules/abogado/views/Profile"
import Contract from "../modules/abogado/views/Contract"
import Empresas from "../modules/abogado/views/Empresas"
import PrivateRoute from "./PrivateRoute"

const AppRouter = () => {
  return (
      <BrowserRouter>
        <Routes>
          {/* Rutas públicas */}
          <Route path="/" element={<Login />} />
          <Route path="/forgot-password" element={<PasswordRecoveryForm />} />

          {/* Rutas protegidas para ADMIN */}
          <Route element={<PrivateRoute allowedRoles={["ADMIN"]} />}>
            <Route path="/admin" element={<LayoutAdmin />}>
              <Route path="contratos" element={<Contratos />} />
              <Route path="contratos/add" element={<RegistroContrato />} />
              <Route path="categorias" element={<Categorias />} />
              <Route path="categorias/add" element={<RegistroCategoria />} />
              <Route path="categorias/edit/:id" element={<EditarCategoria />} />
              <Route path="usuarios" element={<Usuarios />} />
              <Route path="usuarios/add" element={<RegistroUsuario />} />
              <Route path="usuarios/edit/:id" element={<EditarUsuario />} />
            </Route>
          </Route>

          {/* Rutas protegidas para ABOGADO */}
          <Route element={<PrivateRoute allowedRoles={["ABOGADO"]} />}>
            <Route path="/abogado" element={<LayoutAbo />}>
              <Route path="profile" element={<ProfileAbo />} />
              <Route path="contract" element={<Contract />} />
              <Route path="empresas" element={<Empresas />} />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
  )
}

export default AppRouter
