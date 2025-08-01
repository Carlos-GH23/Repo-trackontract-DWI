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
import LayoutAbo from "../modules/abogado/components/layout/LayoutAbo";
import Profile from "../modules/abogado/views/Profile";

const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

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

        {/* Endpoints protegidos para el abogado */}
        <Route path="/abogado" element={<LayoutAbo/>}>
          <Route path="profile" element={<Profile/>}/>
        </Route>



      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;