import { Settings, Shield, Lock, Eye, EyeOff, Plus } from "lucide-react"
import NavbarClient from "../components/NavbarClient"
import SidebarClient from "../components/SidebarClient"
import { useState } from "react"

const Profile = () => {
    const [isCollapsed, setIsCollapsed] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errors, setErrors] = useState({});

    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
        validatePassword(e.target.value);
    };

    const handleConfirmPasswordChange = (e) => {
        setConfirmPassword(e.target.value);
        validateConfirmPassword(e.target.value);
    };

    const validatePassword = (value) => {
        const newErrors = { ...errors };
        
        if (value.length < 8) {
            newErrors.password = "La contraseña debe tener al menos 8 caracteres";
        } else if (!/(?=.*[a-z])/.test(value)) {
            newErrors.password = "Debe incluir al menos una minúscula";
        } else if (!/(?=.*[A-Z])/.test(value)) {
            newErrors.password = "Debe incluir al menos una mayúscula";
        } else if (!/(?=.*\d)/.test(value)) {
            newErrors.password = "Debe incluir al menos un número";
        } else if (!/(?=.*[!@#$%^&*])/.test(value)) {
            newErrors.password = "Debe incluir al menos un símbolo especial";
        } else {
            delete newErrors.password;
        }
        
        setErrors(newErrors);
    };

    const validateConfirmPassword = (value) => {
        const newErrors = { ...errors };
        
        if (value !== password) {
            newErrors.confirmPassword = "Las contraseñas no coinciden";
        } else {
            delete newErrors.confirmPassword;
        }
        
        setErrors(newErrors);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        
        if (Object.keys(errors).length === 0 && password && confirmPassword) {
            // Aquí iría la lógica para cambiar la contraseña
            console.log("Cambiando contraseña...");
            alert("Contraseña cambiada exitosamente");
            
            // Limpiar formulario
            setPassword("");
            setConfirmPassword("");
            setErrors({});
        } else {
            alert("Por favor, corrija los errores antes de continuar");
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 flex">
            {/* Sidebar */}
            <SidebarClient isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />

            {/* Main Content */}
            <div className="flex-1 flex flex-col">
                {/* Navbar */}
                <NavbarClient />
                
                {/* Content */}
                <div className="flex-1 p-6">
                    {/* Header */}
                    <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
                                <Settings className="w-6 h-6 text-gray-600" />
                            </div>
                            <div>
                                <h1 className="text-xl font-semibold text-gray-800">Establecer nueva contraseña</h1>
                                <p className="text-sm text-gray-600">Panel de Cliente - Establecer nueva contraseña</p>
                            </div>
                        </div>
                    </div>

                    {/* Informational Text */}
                    <div className="text-center mb-6">
                        <p className="text-red-500 text-lg">Establezca la nueva contraseña para su perfil</p>
                    </div>

                    {/* Main Content Card */}
                    <div className="max-w-4xl mx-auto">
                        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
                            {/* Card Header */}
                            <div className="flex items-center space-x-3 mb-8">
                                <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                                    <Shield className="w-6 h-6 text-green-600" />
                                </div>
                                <h2 className="text-2xl font-semibold text-gray-800">Configuración de Acceso</h2>
                            </div>

                            <form onSubmit={handleSubmit}>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                                    {/* Password Field */}
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Contraseña *
                                        </label>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                                <Lock className="h-5 w-5 text-gray-400" />
                                            </div>
                                            <input
                                                type={showPassword ? "text" : "password"}
                                                value={password}
                                                onChange={handlePasswordChange}
                                                className={`block w-full pl-10 pr-10 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                                                    errors.password ? "border-red-300" : "border-gray-300"
                                                }`}
                                                placeholder="********"
                                            />
                                            <button
                                                type="button"
                                                onClick={() => setShowPassword(!showPassword)}
                                                className="absolute inset-y-0 right-0 pr-3 flex items-center"
                                            >
                                                {showPassword ? (
                                                    <EyeOff className="h-5 w-5 text-gray-400" />
                                                ) : (
                                                    <Eye className="h-5 w-5 text-gray-400" />
                                                )}
                                            </button>
                                        </div>
                                        <p className="mt-2 text-sm text-gray-600">
                                            La contraseña debe tener al menos 8 caracteres e incluir: mayúscula, minúscula, número y símbolo especial
                                        </p>
                                        {errors.password && (
                                            <p className="mt-1 text-sm text-red-600">{errors.password}</p>
                                        )}
                                    </div>

                                    {/* Confirm Password Field */}
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Repita la Contraseña *
                                        </label>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                                <Lock className="h-5 w-5 text-gray-400" />
                                            </div>
                                            <input
                                                type={showConfirmPassword ? "text" : "password"}
                                                value={confirmPassword}
                                                onChange={handleConfirmPasswordChange}
                                                className={`block w-full pl-10 pr-10 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                                                    errors.confirmPassword ? "border-red-300" : "border-gray-300"
                                                }`}
                                                placeholder="********"
                                            />
                                            <button
                                                type="button"
                                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                                className="absolute inset-y-0 right-0 pr-3 flex items-center"
                                            >
                                                {showConfirmPassword ? (
                                                    <EyeOff className="h-5 w-5 text-gray-400" />
                                                ) : (
                                                    <Eye className="h-5 w-5 text-gray-400" />
                                                )}
                                            </button>
                                        </div>
                                        <p className="mt-2 text-sm text-gray-600">
                                            La contraseña debe tener al menos 8 caracteres e incluir: mayúscula, minúscula, número y símbolo especial
                                        </p>
                                        {errors.confirmPassword && (
                                            <p className="mt-1 text-sm text-red-600">{errors.confirmPassword}</p>
                                        )}
                                    </div>
                                </div>

                                {/* Submit Button */}
                                <div className="mt-8 flex justify-end">
                                    <button
                                        type="submit"
                                        className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-lg text-sm font-medium transition-colors flex items-center space-x-2"
                                    >
                                        <Plus className="w-5 h-5" />
                                        <span>Confirmar</span>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Profile;

