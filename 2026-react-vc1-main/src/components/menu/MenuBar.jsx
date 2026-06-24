import MenuItem from "./MenuItem";
import { LuLayoutDashboard } from "react-icons/lu";
import { IoCalendarOutline } from "react-icons/io5";
import { IoCalendarClearOutline } from "react-icons/io5";
import { IoNotificationsOutline } from "react-icons/io5";
import { NavLink } from "react-router";
import useSWR from "swr";

import img from "../../assets/delaware.png";
import { useAuth } from "../../contexts/auth";
import * as notificatiesApi from "../../api/notifications";

export default function MenuBar() {
  const { user } = useAuth();

  const { data: unreadData } = useSWR(
    user ? `notificaties-unread-count-${user.id}` : null,
    () => notificatiesApi.getUnreadCount(user.id),
    { refreshInterval: 30000 },
  );

  const unreadCount = unreadData?.count ?? 0;

  return (
    <div className="flex flex-col w-60 border border-[#E5E5E5] h-full bg-[#F9F9F9]">
      <div className="p-10 border-b border-[#E5E5E5]">
        <img className="h-11.5 w-38.5" src={img} />
      </div>
      <NavLink
        to="/dashboard"
        className="aria-[current=page]:bg-[#FADEDE] aria-[current=page]:text-[#CD1212]"
      >
        <MenuItem name="Dashboard" icon={LuLayoutDashboard} />
      </NavLink>
      <NavLink
        to="/planning"
        className="aria-[current=page]:bg-[#FADEDE] aria-[current=page]:text-[#CD1212]"
      >
        <MenuItem name="Planning" icon={IoCalendarOutline} />
      </NavLink>
      <NavLink
        to="/afwezigheden"
        className="aria-[current=page]:bg-[#FADEDE] aria-[current=page]:text-[#CD1212]"
      >
        <MenuItem name="Afwezigheden" icon={IoCalendarClearOutline} />
      </NavLink>
      <NavLink
        to="/meldingen"
        className="aria-[current=page]:bg-[#FADEDE] aria-[current=page]:text-[#CD1212]"
      >
        <MenuItem
          name="Meldingen"
          icon={IoNotificationsOutline}
          badgeCount={unreadCount}
        />
      </NavLink>
    </div>
  );
}
