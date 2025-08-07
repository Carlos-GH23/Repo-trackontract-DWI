import {useNavigate} from "react-router-dom";

const Navbar = () => {
    const navigate = useNavigate();
    const handleLogout = async () => {
        const token = localStorage.getItem("accessToken");

        if (token) {
            try {
                await fetch("http://localhost:8080/auth/logout", {
                    method: "POST",
                    headers: { Authorization: `Bearer ${token}` },
                });
            } catch (error) {
                console.error("Error al invalidar el token:", error);
            }
        }

        localStorage.clear();
        navigate("/");
    };


    return (
        <header className="h-14 bg-[var(--color-crema)] flex items-center justify-between px-6 shadow-sm border-b border-[var(--color-marron)]">
            <h2 className="text-[var(--color-cafe)] font-semibold text-lg">
                Panel del Administrador
            </h2>

            <div className="flex items-center gap-4">
                <span className="text-sm text-[var(--color-gris-texto)]">Rol del Usuario</span>

                <button
                onClick={handleLogout}
                    className="px-3 py-2 rounded-md bg-[var(--color-marron)] text-white font-medium hover:bg-[var(--color-cafe)] transition ">
                    Cerrar sesión
                </button>
            </div>
        </header>
    );
};



export default Navbar;
