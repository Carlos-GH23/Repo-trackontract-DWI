import { useState } from "react";
import { Outlet } from "react-router-dom";
import SidebarCompa from '../../components/layout/SidebarCompa';
import NavbarCompa from '../../components/layout/NavbarCompa';

const LayoutCompa = () => {
    const [isCollapsed, setIsCollapsed] = useState(false);

    return (
        <div className="flex h-screen bg-[var(--white)]">
            <SidebarCompa isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
            <div className="flex-1 flex flex-col">
                <NavbarCompa />
                <main className="p-4 overflow-y-auto">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default LayoutCompa;