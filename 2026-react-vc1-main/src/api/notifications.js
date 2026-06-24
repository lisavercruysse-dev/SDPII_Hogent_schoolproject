import { axios } from "./index";

export async function getNotifications(werknemerId, filter) {
  const params = filter ? { filter } : {};
  const { data } = await axios.get(
    `/api/notificaties/werknemer/${werknemerId}`,
    { params },
  );
  return data;
}

export async function getUnreadCount(werknemerId) {
  const { data } = await axios.get(
    `/api/notificaties/werknemer/${werknemerId}/unread-count`,
  );
  return data;
}

export async function getNotificationById(id) {
  const { data } = await axios.get(`/api/notificaties/${id}`);
  return data;
}

export async function markAsRead(id) {
  const { data } = await axios.patch(`/api/notificaties/${id}/read`);
  return data;
}
