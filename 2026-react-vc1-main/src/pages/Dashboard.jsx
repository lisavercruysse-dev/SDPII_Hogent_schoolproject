import DashWerknemer from "../components/dashboard/DashWerknemer";
import DashVerantwoordelijkeOrManager from "../components/dashboard/DashVerantwoordelijkeOrManager";
import { useAuth } from "../contexts/auth";

export default function Dashboard() {
  const { user } = useAuth();
  const normalizedRole = (user?.jobTitel ?? "").trim().toLowerCase();
  const isManagerOrVerantwoordelijke =
    normalizedRole === "manager" || normalizedRole === "verantwoordelijke";
  const isWerknemer = normalizedRole === "werknemer";

  return (
    <div>
      {isManagerOrVerantwoordelijke && <DashVerantwoordelijkeOrManager />}
      {isWerknemer && <DashWerknemer />}
    </div>
  );
}
