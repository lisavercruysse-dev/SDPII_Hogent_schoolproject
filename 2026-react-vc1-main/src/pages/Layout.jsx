import { Outlet, ScrollRestoration } from "react-router";
import MenuBar from "../components/menu/MenuBar";
import UserLabel from '../components/UserLabel';

export default function Layout() {
  return (
    <div className="flex h-screen w-full overflow-hidden">
      <MenuBar />
      <div className="flex-1 overflow-y-auto bg-white">
        <div className="flex justify-end border-b border-[#E4E4E4] pr-5 pb-3 pt-2">
          <UserLabel />
        </div>
        <Outlet />
      </div>
      <ScrollRestoration />
    </div>
  );
}
