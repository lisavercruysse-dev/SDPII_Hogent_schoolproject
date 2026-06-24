import React from "react";
import { FileText, ArrowRight, Check } from "lucide-react";

export default function NotificationCard({
  notification,
  onMarkAsRead,
  onView,
}) {
  return (
    <div
      data-cy="notification-card"
      className={`p-5 border rounded-lg mb-4 flex flex-col sm:flex-row justify-between transition-colors ${
        notification.isRead
          ? "bg-white border-gray-200"
          : "bg-[#fdf3f3] border-[#fad4d4]"
      }`}
    >
      <div className="flex items-start gap-4">
        <FileText
          className={`w-5 h-5 mt-0.5 shrink-0 ${
            notification.isRead ? "text-gray-400" : "text-[#b64949]"
          }`}
          strokeWidth={1.5}
        />
        <div className="flex flex-col">
          <h3 className="text-sm font-medium text-gray-900">
            {notification.title}
          </h3>
          <p className="text-sm text-gray-600 mt-1.5">
            {notification.description}
          </p>
          <p className="text-xs text-gray-400 mt-1.5">{notification.time}</p>
        </div>
      </div>

      <div className="flex flex-col justify-between items-end mt-4 sm:mt-0 sm:min-w-[180px]">
        <div className="h-6 w-full flex justify-end mb-4 sm:mb-0">
          {!notification.isRead && (
            <button
              onClick={() => onMarkAsRead(notification.id)}
              className="flex items-center gap-1.5 text-xs font-medium text-gray-600 hover:text-gray-900 transition-colors"
            >
              <Check className="w-3.5 h-3.5" strokeWidth={2.5} />
              Markeren als gelezen
            </button>
          )}
        </div>

        <div className="flex items-center justify-end gap-2 w-full">
          <button
            onClick={() => onView(notification.id)}
            className="flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-md text-sm text-gray-700 hover:bg-gray-50 transition-colors font-medium"
          >
            Bekijk <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
