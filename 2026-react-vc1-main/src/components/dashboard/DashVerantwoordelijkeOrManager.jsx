import { useAuth } from "../../contexts/auth";
import DashManager from "./DashManager";
import DashVerantwoordelijke from "./DashVerantwoordelijke";

export default function DashVerantwoordelijkeOrManager() {
  const { user } = useAuth();
  const normalizedRole = (user?.jobTitel ?? "").trim().toLowerCase();
  const isVerantwoordelijke = normalizedRole === "verantwoordelijke";
  const isManager = normalizedRole === "manager";

  // TODO refactor berekenenen afwezigheden en beschikbare werknemers

  return (
    <div>
      {isVerantwoordelijke && <DashVerantwoordelijke />}
      {isManager && <DashManager />}
    </div>
  );
}
