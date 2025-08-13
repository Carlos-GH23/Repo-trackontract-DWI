import { Link, useLocation } from "react-router-dom";
import {FaUsers,FaFileContract,FaCity ,FaChevronLeft,FaChevronRight,FaUserCircle,FaSignOutAlt} from "react-icons/fa";
import { useNavigate } from "react-router-dom";

const SidebarCompa = ({ isCollapsed, setIsCollapsed }) => {
    const location = useLocation();
    const navigate = useNavigate();

    const links = [
        { to: "/empresa/contract", label: "Contratos", icon: <FaFileContract /> },
        { to: "/empresa/profile", label: "Perfil", icon: <FaUserCircle /> }
    ];

    const handleLogout = async () => {
        const accessToken = localStorage.getItem("access_token");

        try {
            await fetch("http://localhost:8080/auth/logout", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${accessToken}`
                }
            });
        } catch (error) {
            console.error("Error al cerrar sesión:", error);
        }

        localStorage.removeItem("access_token");
        localStorage.removeItem("refresh_token");
        localStorage.removeItem("role");

        navigate("/");
    };

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

            <nav className="flex flex-col gap-3 p-2">
                {links.map(({ to, label, icon }) => (
                    <Link key={to} to={to} className={`flex items-center gap-2 p-4 rounded-md transition bg-white/10 hover:bg-white/20 ${location.pathname === to ? "bg-white/20" : ""}`}>
                        {icon}
                        {!isCollapsed && <span>{label}</span>}
                    </Link>
                ))}
            </nav>

            <div className="mt-auto p-1">
                <div className="mt-auto p-1">
                    <button
                        onClick={handleLogout}
                        className={`w-full text-left flex items-center gap-2 p-4 rounded-md transition bg-white/10 hover:bg-white/20 ${
                            location.pathname === logoutLink.to ? "bg-white/20" : ""
                        }`}
                    >
                        {logoutLink.icon}
                        {!isCollapsed && <span>{logoutLink.label}</span>}
                    </button>
                </div>

            </div>
        </div>
    );
};

export default SidebarCompa;