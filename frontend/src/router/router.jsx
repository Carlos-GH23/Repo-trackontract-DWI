import { BrowserRouter, Routes, Route } from "react-router";
import Login from "../modules/auth/views/Login";



const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

        

        
  
      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;