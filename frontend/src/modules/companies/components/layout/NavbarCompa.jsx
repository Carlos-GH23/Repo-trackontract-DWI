import "../../../../styles/navbarbo.styles.css"
import { FaBriefcase } from "react-icons/fa"
import { useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'

const NavbarCompa = () => {
    const location = useLocation()

    const [hideNavbar, setHideNavbar] = useState(false)
    const [lastScrollY, setLastScrollY] = useState(0)

    useEffect(() => {
        const handleScroll = () => {
            const currentScrollY = window.scrollY

            if (currentScrollY > lastScrollY && currentScrollY > 50) {
                // Scroll hacia abajo
                setHideNavbar(true)
            } else {
                // Scroll hacia arriba
                setHideNavbar(false)
            }

            setLastScrollY(currentScrollY)
        }

        window.addEventListener('scroll', handleScroll)
        return () => window.removeEventListener('scroll', handleScroll)
    }, [lastScrollY])

    const routeInfo = {
        '/empresa/profile': {
            title: 'Perfil',
            subtitle: 'Aquí podrás cambiar tu información personal y contraseña',
        },
        '/empresa/contract': {
            title: 'Contratos',
            subtitle: 'Puedes visualizar los contratos aceptados y descargarlos',
        }
    }

    const currentRoute = routeInfo[location.pathname] || {
        title: 'Bienvenido Cliente',
        subtitle: 'Cliente - Hay muchos contratos para visualizar',
    }

    return (
        <header className={`navbarbo transition-transform duration-300 ${hideNavbar ? "-translate-y-full" : "translate-y-0"}`}>
            <h2 className="icoNo">
                <FaBriefcase size={50} className="mr-4 text-2xl" />
                <div className="nom">
                    <h1 className="h1">{currentRoute.title}</h1>
                    <p>{currentRoute.subtitle}</p>
                </div>
            </h2>
        </header>
    );
};

export default NavbarCompa;