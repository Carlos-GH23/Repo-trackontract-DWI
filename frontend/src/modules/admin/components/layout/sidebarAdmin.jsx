import { Link, useLocation } from "react-router-dom";
import { FaUsers, FaFileContract, FaTags, FaChevronLeft, FaChevronRight, FaUserCircle, FaHistory, FaUserTie, FaAddressBook, FaSignOutAlt, FaClipboardList } from "react-icons/fa";

const sidebarAdmin = ({ isCollapsed, setIsCollapsed }) => {
    const location = useLocation();

    const links = [
        { to: "/admin/abogados", label: "Gestión de Abogados", icon: <FaUserTie /> },
        { to: "/admin/clientes", label: "Gestión de Clientes", icon: <FaAddressBook /> },
        { to: "/admin/contratos", label: "Gestión de Contratos", icon: <FaFileContract /> },
        { to: "/admin/categorias", label: "Gestión de Categorias", icon: <FaTags /> },
        { to: "/admin/bitacora", label: "Bitácora", icon: <FaClipboardList /> },
        { to: "/admin/perfil", label: "Perfil", icon: <FaUserCircle /> },
        

    ];

    const logoutLink = {
        to: "/",
        label: "Cerrar Sesión",
        icon: <FaSignOutAlt />
    };

    return (
        <div className={`bg-gradient-to-br from-[var(--color-degradado-inicio)] to-[var(--color-degradado-fin)] text-white transition-all duration-300 ${isCollapsed ? "w-16" : "w-64"} flex flex-col`}>
            <div className="flex items-center justify-between px-4 h-14 shadow-sm border-b border-white">
                {!isCollapsed && <h1 className="text-lg font-semibold">TrackOntrack</h1>}
                <button className="text-white focus:outline-none" onClick={() => setIsCollapsed(!isCollapsed)}>
                    {isCollapsed ? <FaChevronRight /> : <FaChevronLeft />}
                </button>
            </div>

            <nav className="flex flex-col gap-2 p-2">
                {links.map(({ to, label, icon }) => (
                    <Link key={to} to={to} className={`flex items-center gap-2 p-4 rounded-md transition bg-white/10 hover:bg-white/20 ${location.pathname === to ? "bg-white/20" : ""}`}>
                        {icon}
                        {!isCollapsed && <span>{label}</span>}
                    </Link>
                ))}
            </nav>

            <div className="mt-auto p-2">
                <Link to={logoutLink.to}
                    className={`flex items-center gap-2 p-4 rounded-md transition bg-white/10 hover:bg-white/20 ${location.pathname === logoutLink.to ? "bg-white/20" : ""
                        }`}
                >
                    {logoutLink.icon}
                    {!isCollapsed && <span>{logoutLink.label}</span>}
                </Link>
            </div>
        </div>
    );
};

export default sidebarAdmin;
