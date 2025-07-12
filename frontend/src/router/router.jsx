import { BrowserRouter, Routes, Route } from "react-router";
import Login from "../modules/auth/views/Login";
import LayoutAdmin from "../modules/admin/components/layout/layoutAdmin"
import Contratos from "../modules/admin/views/Contratos"



const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

        <Route path="/admin" element={<LayoutAdmin />}>

          <Route path="contratos" element={<Contratos />} />


        </Route>




      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;