import { FaUserCircle } from "react-icons/fa";
import AsyncData from "./asyncData/AsyncData";
import { useAuth } from "../contexts/auth";
import { useState } from "react";
import { NavLink } from "react-router";

export default function UserLabel() {
    const { user } = useAuth();
    const [isOpen, setIsOpen] = useState(false);

    const toggleOpen = () => {
        setIsOpen(!isOpen);
    };

    return (
        <div className="w-60 mr-5 flex flex-col items-center gap-2 m-3 relative">
            <div className="border border-[#E5E5E5] py-3 flex gap-5 items-center w-60 justify-center rounded-3xl">
                <FaUserCircle className="text-4xl text-[#C4C4C4]" />
                <div>
                    <AsyncData>
                        {user && (
                            <button onClick={toggleOpen} className="cursor-pointer">
                                <p className="font-bold">{user.firstName} {user.lastName}</p>
                                <p className="text-[#4D4D4D]">{user.jobTitel}</p>
                            </button>
                        )}
                    </AsyncData>
                </div>
            </div>
            {isOpen && (
                <NavLink to='logout'>
                    <div className="absolute top-full mt-2 border border-[#E5E5E5] px-5 py-2 rounded-xl bg-white z-50 cursor-pointer">
                        Logout
                    </div>
                </NavLink>   
            )}
        </div>
    );
}