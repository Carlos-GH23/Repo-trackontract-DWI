import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { FaUsers, FaFileContract, FaTags, FaChevronLeft, FaChevronRight, FaUserCircle, FaHistory, FaUserTie, FaAddressBook, FaSignOutAlt, FaChevronDown, FaChevronUp, FaClipboardList } from "react-icons/fa";
import { useNavigate } from "react-router-dom";


const SidebarAdmin = ({ isCollapsed, setIsCollapsed }) => {
    const location = useLocation();
    const navigate = useNavigate();
    const [catOpen, setCatOpen] = useState(false);

    const links = [
        { to: "/admin/abogados", label: "Gestión de Abogados", icon: <FaUserTie /> },
        { to: "/admin/clientes", label: "Gestión de Clientes", icon: <FaAddressBook /> },
        { to: "/admin/contratos", label: "Gestión de Contratos", icon: <FaFileContract /> },
        { to: "/admin/categorias", label: "Gestión de Categorias", icon: <FaTags /> },
        { to: "/admin/bitacora", label: "Bitácora", icon: <FaClipboardList /> },
        { to: "/admin/perfil", label: "Perfil", icon: <FaUserCircle /> },
    ];

    const handleLogout = async () => {
        const accessToken = localStorage.getItem("access_token");
        try {
            await fetch("http://localhost:8080/auth/logout", {
                method: "POST",
                headers: { "Authorization": `Bearer ${accessToken}` }
            });
        } catch (error) {
            console.error("Error al cerrar sesión:", error);
        }
        localStorage.removeItem("access_token");
        localStorage.removeItem("refresh_token");
        localStorage.removeItem("role");
        navigate("/");
    };

    const logoutLink = { to: "/", label: "Cerrar Sesión", icon: <FaSignOutAlt /> };

    return (
        <div className={`bg-gradient-to-br from-[var(--color-degradado-inicio)] to-[var(--color-degradado-fin)] text-white transition-all duration-300 ${isCollapsed ? "w-16" : "w-64"} flex flex-col`}>
            <div className="flex items-center justify-between px-4 h-14 shadow-sm border-b border-white">
                {!isCollapsed && <h1 className="text-lg font-semibold">TrackOntrack</h1>}
                <button className="text-white focus:outline-none" onClick={() => setIsCollapsed(!isCollapsed)}>
                    {isCollapsed ? <FaChevronRight /> : <FaChevronLeft />}
                </button>
            </div>

            <nav className="flex flex-col gap-2 p-2">
                {links.map((link, i) => {
                    if (link.children) {
                        return (
                            <div key={i}>
                                <button
                                    onClick={() => setCatOpen(!catOpen)}
                                    className={`flex items-center justify-between w-full p-4 rounded-md transition bg-white/10 hover:bg-white/20 ${!isCollapsed ? '' : 'justify-center'}`}
                                >
                                    <div className="flex items-center gap-2">
                                        {link.icon}
                                        {!isCollapsed && <span>{link.label}</span>}
                                    </div>
                                    {!isCollapsed && (catOpen ? <FaChevronUp /> : <FaChevronDown />)}
                                </button>
                                {catOpen && !isCollapsed && (
                                    <div className="ml-6 flex flex-col gap-1">
                                        {link.children.map((child, idx) => (
                                            <Link
                                                key={idx}
                                                to={child.to}
                                                className={`p-2 rounded hover:bg-white/10 block ${location.pathname === child.to ? "bg-white/20" : ""}`}
                                            >
                                                {child.icon && <span className="mr-2">{child.icon}</span>}
                                                {child.label}
                                            </Link>
                                        ))}
                                    </div>
                                )}
                            </div>
                        );
                    }
                    return (
                        <Link
                            key={i}
                            to={link.to}
                            className={`flex items-center gap-2 p-4 rounded-md transition bg-white/10 hover:bg-white/20 ${location.pathname === link.to ? "bg-white/20" : ""}`}
                        >
                            {link.icon}
                            {!isCollapsed && <span>{link.label}</span>}
                        </Link>
                    );
                })}
            </nav>

            <div className="mt-auto p-2">
                <button
                    onClick={handleLogout}
                    className={`w-full text-left flex items-center gap-2 p-4 rounded-md transition bg-white/10 hover:bg-white/20 ${location.pathname === logoutLink.to ? "bg-white/20" : ""}`}
                >
                    {logoutLink.icon}
                    {!isCollapsed && <span>{logoutLink.label}</span>}
                </button>
            </div>
        </div>
    );
};

export default SidebarAdmin;
