import { useEffect } from "react";
import { useAuth } from "../contexts/auth";
import { LuLogOut } from "react-icons/lu";

export default function Logout() {
  const { isAuthed, logout } = useAuth();

  useEffect(() => {
    logout();
  }, [logout]);

  return (
    <div className="flex items-center justify-center w-screen h-screen bg-[#F9F9F9]">
      <div className="flex items-center gap-3 border border-[#E5E5E5] rounded-xl bg-white px-6 py-4">
        <LuLogOut className="text-xl text-[#CD1212]" />
        {isAuthed ? (
          <span className="font-semibold text-[#4D4D4D]">Logging out...</span>
        ) : (
          <span className="font-semibold text-[#4D4D4D]">You were successfully logged out</span>
        )}
      </div>
    </div>
  );
}