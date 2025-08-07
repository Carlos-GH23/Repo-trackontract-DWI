import "../../../../styles/navbarbo.styles.css"
import { FaUserShield } from "react-icons/fa"
import { useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'


const Navbar = () => {
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
        '/admin/abogados': {
            title: 'Abogados',
            subtitle: 'Gestión de Abogados',
        },
        '/admin/clientes': {
            title: 'Clientes',
            subtitle: 'Gestión de Clientes',
        },

        '/admin/contratos': {
            title: 'Contratos',
            subtitle: 'Gestión de contratos',
        },
        '/admin/categorias': {
            title: 'Categorias',
            subtitle: 'Gestión de Categorias',
        },
        '/admin/perfil': {
            title: 'Perfil',
            subtitle: 'Aquí podrás cambiar tu información personal y contraseña',
        },

    }

    const currentRoute = routeInfo[location.pathname] || {
        title: 'Bienvenido Admin',
        subtitle: 'Administrador - Hay muchas cosas que puedes hacer hoy',
    }


    return (
        <header className={`navbarbo transition-transform duration-300 ${hideNavbar ? "-translate-y-full" : "translate-y-0"}`}>
            <h2 className="icoNo">
                <FaUserShield size={50} className="mr-4 text-2xl" />

                <div className="nom">
                    <h1 className="h1">{currentRoute.title}</h1>
                    <p>{currentRoute.subtitle}</p>
                </div>
            </h2>
        </header>
    )
}




export default Navbar;
