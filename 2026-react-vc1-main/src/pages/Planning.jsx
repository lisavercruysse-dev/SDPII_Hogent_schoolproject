import { useState, useMemo, useEffect } from "react";
import { TaskList } from "../components/tasks/TaskList";
import { PlanningTimeline } from "../components/tasks/PlanningTimeline";
import TaskDetailsModal from "../components/tasks/TaskDetailsModal";
import { FilterBar } from "../components/FilterBar";
import { MemberRow } from "../components/tasks/MemberRow";
import { TimeLineLegend } from "../components/tasks/TimeLineLegend.jsx";
import { useAuth } from "../contexts/auth";
import { TimelineHourLabels } from "../components/tasks/TimelineHourLabels";
import EditTimeBlockModal from "../components/tasks/EditTimeBlockModal.jsx";
import DeleteTaskModal from "../components/tasks/DeleteTaskModal.jsx";
import TaskTemplateList from "../components/taskTemplates/TaskTemplateList.jsx";
import UncompletedTaskList from "../components/tasks/uncompletedTasks/UncompletedTaskList.jsx";
import useSWR from "swr";
import { getAll, updateById, deleteResource } from "../api/index.js";
import AsyncData from "../components/asyncData/AsyncData.jsx";
import useSWRMutation from "swr/mutation";

const toDateInputValue = (date) => date.toISOString().split("T")[0];

const getWeekStart = (dateInput) => {
  const date = new Date(dateInput);
  const day = date.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  date.setDate(date.getDate() + diff);
  date.setHours(0, 0, 0, 0);
  return date;
};

const isSameWeek = (dateString, anchorDateString) => {
  if (!dateString || !anchorDateString) return true;
  const date = new Date(dateString);
  const weekStart = getWeekStart(anchorDateString);
  const weekEnd = new Date(weekStart);
  weekEnd.setDate(weekEnd.getDate() + 6);
  weekEnd.setHours(23, 59, 59, 999);
  return date >= weekStart && date <= weekEnd;
};

const normalizeTask = (task) => ({
  ...task,
  memberId: task.memberId ?? task.werknemerId ?? null,
  startdatum: task.startdatum ?? task.datum ?? null,
  duurtijd: task.duurtijd ?? task.taakTemplate?.duurTijd ?? 0,
  omschrijving: task.omschrijving ?? task.taakTemplate?.omschrijving ?? "",
  type: task.type ?? task.taakTemplate?.type ?? "",
});

export default function Planning() {
  const { user } = useAuth();
  const normalizedRole = (user?.jobTitel ?? "").trim().toLowerCase();
  const isManager = normalizedRole === "manager";
  const isVerantwoordelijke = normalizedRole === "verantwoordelijke";
  const isWerknemer = normalizedRole === "werknemer";
  const isManagerOrVerantwoordelijke = isManager || isVerantwoordelijke;

  if (!user) {
    return null;
  }

  const tasksEndpoint = isManager
    ? "taken"
    : isVerantwoordelijke
      ? "werknemers/me/takenSupervisor"
      : "werknemers/me/taken";

  // Teams: manager ziet alle teams, verantwoordelijke enkel zijn eigen teams
  const teamsEndpoint = isManager
    ? "teams"
    : isVerantwoordelijke
      ? "teams/mine"
      : null;

  const {
    data: werknemerTasks = [],
    isLoading,
    error,
    mutate,
  } = useSWR(tasksEndpoint, getAll);

  const { data: sites = [] } = useSWR("sites", getAll);
  const { data: teams = [] } = useSWR(teamsEndpoint, getAll);
  console.log("Fetched teams:", teams);

  const { data: apiWerknemers = [] } = useSWR(
    isManager ? "werknemers" : null,
    getAll,
  );
  const { data: taakTemplates = [] } = useSWR(
    isManagerOrVerantwoordelijke ? "taakTemplates" : null,
    getAll,
  );
  
    // Genormaliseerde werknemerslijst met `teams: number[]` voor beide rollen
  const werknemersList = useMemo(() => {
    if (isManager) {
      return apiWerknemers;
    }

    const map = new Map();
    for (const team of teams) {
      for (const w of team.werknemers) {
        if (!map.has(w.id)) {
          map.set(w.id, { ...w, teams: [] });
        }
        map.get(w.id).teams.push(team.id);
      }
    }
    return Array.from(map.values());
  }, [isManager, apiWerknemers, teams]);

  const teamsForSite = (siteId) =>
    teams.filter((t) => t.siteId === Number(siteId));

  const [searchQuery, setSearchQuery] = useState("");
  const [selectedDate, setSelectedDate] = useState(
    toDateInputValue(new Date()),
  );
  const [selectedTask, setSelectedTask] = useState(null);

  const [selectedPlant, setSelectedPlant] = useState(null);
  const [selectedTeam, setSelectedTeam] = useState(null);

  const [modelType, setModelType] = useState("");
  const [selectedTaskBlock, setSelectedTaskBlock] = useState(null);
  const [taskToDelete, setTaskToDelete] = useState(null);

  // Verantwoordelijke: siteId en team afleiden uit eigen geladen teams
    useEffect(() => {
      if (!isVerantwoordelijke || teams.length === 0 || selectedPlant !== null) return;
      setSelectedPlant(teams[0].siteId);
      setSelectedTeam(teams[0].id);
    }, [teams, isVerantwoordelijke, selectedPlant]);
  
    // Manager: standaard plant uit sites, standaard team uit teams
    useEffect(() => {
      if (!isManager || sites.length === 0 || selectedPlant !== null) return;
      const firstSiteId = sites[0].id;
      setSelectedPlant(firstSiteId);
    }, [sites, isManager, selectedPlant]);
  
    useEffect(() => {
      if (!isManager || teams.length === 0 || selectedTeam !== null || selectedPlant === null) return;
      setSelectedTeam(teamsForSite(selectedPlant)[0]?.id ?? null);
    }, [teams, isManager, selectedTeam, selectedPlant]);

  const { trigger: onSubmit } = useSWRMutation(
    selectedTask ? `taken/${selectedTask.id}/status` : null,
    updateById,
  );

  const normalizedTasks = useMemo(
    () => werknemerTasks.map(normalizeTask),
    [werknemerTasks],
  );

  // Update team when plant changes
  function handlePlantChange(plantId) {
    const id = Number(plantId);
    setSelectedPlant(id);
    setSelectedTeam(teamsForSite(id)[0]?.id ?? null);
  }

  // Filtered members
  const filteredMembers = useMemo(
    () => werknemersList.filter((m) => m.teams?.includes(selectedTeam)),
    [selectedPlant, selectedTeam],
  );

  // Tasks per member for the selected day
  function memberTasks(memberId) {
    return normalizedTasks.filter((t) => {
      const taskDate = (t.startdatum || "").split("T")[0];
      return t.memberId === memberId && taskDate === selectedDate;
    });
  }

  const showModal = (task, requestedType) => {
    const modalType =
      requestedType === "complete" && task.status === "afgewerkt"
        ? "cancel"
        : requestedType;
    setSelectedTask(task);
    setModelType(modalType);
  };
  const closeModal = () => {
    setSelectedTask(null);
    setModelType("");
  };

  const showEditTimeBlockModal = (task) => {
    setSelectedTaskBlock(task);
  };
  const closeEditTimeBlockModal = () => {
    setSelectedTaskBlock(null);
  };

  const showDeleteTaskModal = (task) => {
    setTaskToDelete(task);
  };
  const closeDeleteTaskModal = () => {
    setTaskToDelete(null);
  };
const handleDeleteTask = async (taskId) => {
    try {
      await deleteResource(`taken/${taskId}`);
      
      mutate((prevTasks = []) => prevTasks.filter((t) => t.id !== taskId), false);
      
      closeDeleteTaskModal();
    } catch (err) {
      console.error("Fout bij het verwijderen van de taak:", err);
      alert("Kon de taak niet verwijderen. Controleer je console.");
    }
  };

  const filteredTasks = useMemo(() => {
    return normalizedTasks.filter((task) => {
      const matchesSearch = (
        task.taakTemplate?.omschrijving ||
        task.omschrijving ||
        ""
      )
        .toLowerCase()
        .includes(searchQuery.toLowerCase());

      const taskDate = task.datum || task.startdatum || "";
      const matchesDate = isWerknemer
        ? isSameWeek(taskDate, selectedDate)
        : selectedDate
          ? taskDate.split("T")[0] === selectedDate
          : true;
      const matchesMember = isWerknemer
        ? Number(task.memberId) === Number(user.id)
        : true;
      return matchesSearch && matchesDate && matchesMember;
    });
  }, [searchQuery, selectedDate, normalizedTasks, user.id, isWerknemer]);

  const uncompletedTasks = useMemo(() => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const seen = new Set();
    return normalizedTasks.filter((t) => {
      if (seen.has(t.id)) return false;
      const isPast = new Date(t.startdatum || t.datum) < today;

      const member = werknemersList.find((w) => w.id === t.memberId);
      const sameTeam = member?.teams?.includes(selectedTeam);

      if (isPast && sameTeam && t.status?.toLowerCase() !== "afgewerkt") {
        seen.add(t.id);
        return true;
      }
      return false;
    });
  }, [normalizedTasks, selectedTeam, werknemersList]);

  return (
    <div className="mx-16 mt-8">
      <p className="text-gray-800 text-xl p-4 md:text-2xl font-bold">
        Planning
      </p>

      {/* Filters */}
      <FilterBar
        plants={sites}
        plant={selectedPlant}
        onPlant={handlePlantChange}
        teams={teamsForSite(selectedPlant)}
        team={selectedTeam}
        onTeam={(id) => setSelectedTeam(Number(id))}
        selectedDate={selectedDate}
        setSelectedDate={setSelectedDate}
      />
      <TimeLineLegend />

      {/* Timeline werknemer */}
      {isWerknemer && (
        <PlanningTimeline tasks={filteredTasks} selectedDate={selectedDate} />
      )}

      {/* Member rows */}
      <TimelineHourLabels />
      {isManagerOrVerantwoordelijke && (
        <div className="divide-y divide-gray-100">
          {filteredMembers.length === 0 ? (
            <div className="py-10 text-center text-sm text-gray-400">
              Geen teamleden gevonden voor de geselecteerde filters.
            </div>
          ) : (
            filteredMembers.map((member) => (
              <MemberRow
                key={member.id}
                member={member}
                tasks={memberTasks(member.id)}
                onEdit={(task) => showEditTimeBlockModal(task)}
                onDelete={(task) => showDeleteTaskModal(task)}
              />
            ))
          )}
        </div>
      )}

      <AsyncData loading={isLoading} error={error}>
        {isWerknemer && (
          <TaskList
            tasks={filteredTasks}
            searchQuery={searchQuery}
            setSearchQuery={setSearchQuery}
            onTaskDetailsClick={(task) => showModal(task, "details")}
            onCompleted={(task) => showModal(task, "complete")}
            onCancel={(task) => showModal(task, "cancel")}
          />
        )}
      </AsyncData>

      {isManagerOrVerantwoordelijke && (
        <UncompletedTaskList
          tasks={uncompletedTasks}
          onAssign={showEditTimeBlockModal}
        />
      )}

      {isManagerOrVerantwoordelijke && (
        <TaskTemplateList
          taskTemplates={taakTemplates}
          onAssign={showEditTimeBlockModal}
        />
      )}

      <TaskDetailsModal
        isOpen={!!selectedTask}
        onClose={closeModal}
        task={selectedTask}
        type={modelType}
        onSubmit={onSubmit}
        mutate={mutate}
      />

      <EditTimeBlockModal
        isOpen={!!selectedTaskBlock}
        onClose={closeEditTimeBlockModal}
        task={selectedTaskBlock}
        werknemers={filteredMembers}
        onSaved={() => mutate()}
        tasks={normalizedTasks}
      />

      <DeleteTaskModal
        isOpen={!!taskToDelete}
        onClose={closeDeleteTaskModal}
        task={taskToDelete}
        onDelete={handleDeleteTask}
      />
    </div>
  );
}
