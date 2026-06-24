import React, { useState, useCallback } from "react";
import useSWR, { mutate as globalMutate } from "swr";
import NotificationCard from "../components/notifications/NotificationCard";
import AsyncData from "../components/asyncData/AsyncData";
import {
  Bell,
  Filter,
  CheckCheck,
  X,
  Calendar,
  MapPin,
  AlertCircle,
} from "lucide-react";
import { useAuth } from "../contexts/auth";
import * as notificatiesApi from "../api/notifications";
import { TYPE_LABELS } from "../api/notificationTypes";

function NotificationModal({ notificationId, onClose }) {
  const { data: notification, isLoading } = useSWR(
    notificationId ? `notificatie-${notificationId}` : null,
    () => notificatiesApi.getNotificationById(notificationId),
  );

  if (!notificationId) return null;

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-2xl overflow-hidden flex flex-col max-h-[90vh]">
        <div className="flex justify-between items-center p-5 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-900">
            Melding Details
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 overflow-y-auto">
          {isLoading || !notification ? (
            <div className="flex justify-center items-center py-10">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#b64949]"></div>
            </div>
          ) : (
            <div className="flex flex-col">
              <h3 className="text-base font-semibold text-gray-900 mb-6 border-b border-gray-100 pb-4">
                {notification.title}
              </h3>

              <div className="flex flex-col md:flex-row gap-8">
                <div className="flex-1">
                  <h4 className="text-xs font-semibold text-gray-900 mb-2 uppercase tracking-wider">
                    Omschrijving
                  </h4>
                  <p className="text-sm text-gray-600 leading-relaxed">
                    {notification.description}
                  </p>
                </div>

                <div className="w-full md:w-64 flex flex-col gap-5">
                  <div>
                    <div className="flex items-center gap-2 text-xs text-gray-500 mb-1">
                      <Calendar className="w-4 h-4" />
                      <span className="font-medium">Datum & Tijd</span>
                    </div>
                    <p className="text-sm text-gray-900">
                      {notification.time || "Geen tijd opgegeven"}
                    </p>
                  </div>

                  {notification.location && (
                    <div>
                      <div className="flex items-center gap-2 text-xs text-gray-500 mb-1">
                        <MapPin className="w-4 h-4" />
                        <span className="font-medium">Locatie</span>
                      </div>
                      <p className="text-sm text-gray-900">
                        {notification.location}
                      </p>
                    </div>
                  )}

                  {notification.priority && (
                    <div>
                      <div className="flex items-center gap-2 text-xs text-gray-500 mb-1">
                        <AlertCircle className="w-4 h-4" />
                        <span className="font-medium">Prioriteit</span>
                      </div>
                      <p className="text-sm text-gray-900">
                        {notification.priority}
                      </p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default function Notifications() {
  const { user } = useAuth();
  const [readFilter, setReadFilter] = useState("Alle");
  const [typeFilter, setTypeFilter] = useState("Alle types");

  const [selectedNotificationId, setSelectedNotificationId] = useState(null);

  const {
    data: notifications,
    isLoading,
    error,
    mutate,
  } = useSWR(user ? `notificaties-${user.id}` : null, () =>
    notificatiesApi.getNotifications(user.id),
  );

  const unreadCount = notifications
    ? notifications.filter((n) => !n.isRead).length
    : 0;

  const handleMarkAsRead = useCallback(
    async (id) => {
      mutate(
        (prev) => prev?.map((n) => (n.id === id ? { ...n, isRead: true } : n)),
        false,
      );

      globalMutate(
        `notificaties-unread-count-${user.id}`,
        (prev) => ({ count: Math.max(0, (prev?.count || 0) - 1) }),
        false,
      );

      try {
        await notificatiesApi.markAsRead(id);
        globalMutate(`notificaties-unread-count-${user.id}`);
      } catch (error) {
        mutate();
        globalMutate(`notificaties-unread-count-${user.id}`);
      }
    },
    [user?.id, mutate],
  );

  const handleMarkAllAsRead = useCallback(async () => {
    const unread = (notifications || []).filter((n) => !n.isRead);

    mutate((prev) => prev?.map((n) => ({ ...n, isRead: true })), false);

    globalMutate(`notificaties-unread-count-${user.id}`, { count: 0 }, false);

    try {
      await Promise.all(unread.map((n) => notificatiesApi.markAsRead(n.id)));
      globalMutate(`notificaties-unread-count-${user.id}`);
    } catch {
      mutate();
      globalMutate(`notificaties-unread-count-${user.id}`);
    }
  }, [notifications, mutate, user?.id]);

  const filteredNotifications = (notifications || []).filter((n) => {
    const matchesRead =
      readFilter === "Alle" ||
      (readFilter === "Ongelezen" && !n.isRead) ||
      (readFilter === "Gelezen" && n.isRead);
    const matchesType = typeFilter === "Alle types" || n.type === typeFilter;
    return matchesRead && matchesType;
  });

  return (
    <div className="p-4 md:p-8 w-full max-w-5xl mx-auto font-sans">
      <div className="mb-6">
        <h1 className="text-xl md:text-2xl font-medium text-gray-800">
          Meldingen
        </h1>
        <p className="text-sm text-gray-500 mt-1">
          Blijf up-to-date met al uw meldingen.
        </p>
      </div>

      {unreadCount > 0 && (
        <div className="flex items-center gap-3 p-4 bg-[#fdf3f3] border border-[#fad4d4] rounded-lg mb-6">
          <div className="bg-[#b64949] p-1.5 rounded-full">
            <Bell className="w-4 h-4 text-white" />
          </div>
          <span className="text-sm font-medium text-gray-800">
            Je hebt {unreadCount} ongelezen{" "}
            {unreadCount === 1 ? "melding" : "meldingen"}
          </span>
        </div>
      )}

      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between p-4 bg-white border border-gray-200 rounded-lg mb-6 gap-4">
        <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4 w-full sm:w-auto">
          <div className="flex items-center gap-2 text-gray-500">
            <Filter className="w-4 h-4" />
          </div>
          <select
            value={readFilter}
            onChange={(e) => setReadFilter(e.target.value)}
            className="w-full sm:w-40 px-3 py-2 bg-gray-50 border border-gray-200 rounded-md text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-[#4863d6] focus:border-transparent"
          >
            <option value="Alle">Alle statussen</option>
            <option value="Ongelezen">Ongelezen</option>
            <option value="Gelezen">Gelezen</option>
          </select>
          <select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            className="w-full sm:w-40 px-3 py-2 bg-gray-50 border border-gray-200 rounded-md text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-[#4863d6] focus:border-transparent"
          >
            <option value="Alle types">Alle types</option>
            {Object.entries(TYPE_LABELS).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>
        <button
          onClick={handleMarkAllAsRead}
          disabled={unreadCount === 0}
          className={`flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-md text-sm font-medium transition-colors w-full sm:w-auto justify-center ${unreadCount === 0 ? "opacity-50 cursor-not-allowed text-gray-400" : "text-gray-700 hover:bg-gray-50"}`}
        >
          <CheckCheck className="w-4 h-4" /> Alles als gelezen markeren
        </button>
      </div>

      <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden relative">
        <div className="p-5 border-b border-gray-100">
          <h2 className="text-sm font-medium text-gray-800">
            Meldingen ({filteredNotifications.length})
          </h2>
        </div>
        <div className="p-5">
          <AsyncData loading={isLoading} error={error}>
            {filteredNotifications.length === 0 ? (
              <p className="text-sm text-gray-500 text-center py-8">
                Er zijn geen meldingen die voldoen aan je filters.
              </p>
            ) : (
              filteredNotifications.map((notification) => (
                <NotificationCard
                  key={notification.id}
                  notification={notification}
                  onMarkAsRead={handleMarkAsRead}
                  onView={(id) => setSelectedNotificationId(id)}
                />
              ))
            )}
          </AsyncData>
        </div>
      </div>

      {selectedNotificationId && (
        <NotificationModal
          notificationId={selectedNotificationId}
          onClose={() => setSelectedNotificationId(null)}
        />
      )}
    </div>
  );
}
