

const NavbarAbo = () => {
    return (
        
        <header className="h-14 bg-[var(--color-crema)] flex items-center justify-between px-6 shadow-sm border-b border-[var(--color-marron)]">
            <h2 className="text-[var(--color-cafe)] font-semibold text-lg">
                Panel del Administrador
            </h2>

            <div className="flex items-center gap-4">
                <span className="text-sm text-[var(--color-gris-texto)]">Rol del Usuario</span>

            </div>
        </header>           
    );
};



export default NavbarAbo;
