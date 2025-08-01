import "../../../../styles/navbarbo.styles.css"
import {FaBriefcase } from "react-icons/fa";
import { useLocation } from 'react-router-dom';

const NavbarAbo = () => {

    const location = useLocation();

    // Mapeo de rutas a títulos
    const routeInfo = {
        '/abogado/profile': {
            title: 'Perfil',
            subtitle: 'Aquí podrás cambiar tu información personal y contraseña',
        },
        '/abogado/contract': {
            title: 'Contratos',
            subtitle: 'Visualiza y gestiona tus contratos legales de las empresas',
        },
        // Agrega más rutas según necesites
    };

    const currentRoute = routeInfo[location.pathname] || {
        title: 'Bienvenido Abogado',
        subtitle: 'Abogado - Hay muchas cosas que puedes hacer el día de hoy',
    };

    return (
        <header className="navbarbo">
            <h2 className="icoNo">
                <FaBriefcase  size={50} className="mr-4 text-2xl" />
                <div className="nom">
                    <h1 className="h1">{currentRoute.title}</h1>
                    <p>{currentRoute.subtitle}</p>
                </div>
            </h2>
        </header>          
    );
};

export default NavbarAbo;
