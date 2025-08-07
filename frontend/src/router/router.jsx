import { BrowserRouter, Routes, Route } from "react-router";
import Login from "../modules/auth/views/Login";
import LayoutAdmin from "../modules/admin/components/layout/layoutAdmin"
import Contratos from "../modules/admin/views/Contratos"
import RegistroContrato from "../modules/admin/components/registro_contrato"
import Categorias from "../modules/admin/views/Categorias"
import RegistroCategoria from "../modules/admin/components/registro_categoria"
import Usuarios from "../modules/admin/views/Usuarios"
import RegistroUsuario from "../modules/admin/components/registro_usuario"
import EditarUsuario from "../modules/admin/components/editar_usuario"
import EditarCategoria from "../modules/admin/components/editar_categoria"
import PasswordRecoveryForm from "../modules/auth/views/PasswordRecoveryForm";
import LayoutAbo from "../modules/abogado/components/layout/LayoutAbo";
import ProfileAbo from "../modules/abogado/views/Profile";
import Contract from "../modules/abogado/views/Contract";
import Empresas from "../modules/abogado/views/Empresas";
import ContratoAcep from "../modules/abogado/components/contratoAcep";
import Clientes from "../modules/admin/views/Clientes"
import RegistroCliente from "../modules/admin/components/registro_cliente"
import EditarCliente from "../modules/admin/components/editar_cliente"
import EditarContrato from "../modules/admin/components/editar_contrato"
import ProfileAdmin from "../modules/admin/views/Perfil"

const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>

        {/* Rutas públicas */}
        <Route path="/" element={<Login />} />
        <Route path="/forgot-password" element={<PasswordRecoveryForm />} />

        {/* Rutas protegidas para el administrador */}
        <Route path="/admin" element={<LayoutAdmin />}>

          <Route path="contratos" element={<Contratos />} />
          <Route path="contratos/add" element={<RegistroContrato />} />
          <Route path="contratos/edit/:id" element={<EditarContrato />} />
          <Route path="categorias" element={<Categorias />} />
          <Route path="categorias/add" element={<RegistroCategoria />} />
          <Route path="categorias/edit/:id" element={<EditarCategoria />} />
          <Route path="clientes" element={<Clientes />} />
          <Route path="clientes/add" element={<RegistroCliente />} />
          <Route path="clientes/edit/:id" element={<EditarCliente />} />
          <Route path="abogados" element={<Usuarios />} />
          <Route path="abogados/add" element={<RegistroUsuario />} />
          <Route path="abogados/edit/:id" element={<EditarUsuario />} />
          <Route path="perfil" element={<ProfileAdmin />} />

        </Route>

        {/* Rutas protegidas para el abogado */}
        <Route path="/abogado" element={<LayoutAbo />}>
          <Route path="profile" element={<ProfileAbo />} />
          <Route path="contract" element={<Contract/>} />
          <Route path="empresas" element={<Empresas/>} />

        </Route>



      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;