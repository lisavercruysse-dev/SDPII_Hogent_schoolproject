import React from "react";
import { useAuth } from "../../contexts/auth";
import AbsenceOverview from "../../pages/AbsenceOverview";
import ManagerAbsenceOverview from "../../pages/ManagerAbsenceOverview";

export default function AbsenceWrapper() {
  const { user } = useAuth();

  const managerRoles = ["manager", "supervisor", "verantwoordelijke"];
  const isManager = user && managerRoles.includes(user.jobTitel?.toLowerCase());

  if (isManager) {
    return <ManagerAbsenceOverview />;
  }

  return <AbsenceOverview />;
}
