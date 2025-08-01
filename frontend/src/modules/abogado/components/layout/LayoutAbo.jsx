import { useState } from "react";
import { Outlet } from "react-router-dom";
import SidebarAbo from "./SidebarAbo";
import NavbarAbo from "./NavbarAbo";

const LayoutAbo = () => {
    const [isCollapsed, setIsCollapsed] = useState(false);

    return (
        <div className="flex h-screen bg-[var(--white)]">
            <SidebarAbo isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
            <div className="flex-1 flex flex-col">
                <NavbarAbo />
                <main className="p-4 overflow-y-auto">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default LayoutAbo;