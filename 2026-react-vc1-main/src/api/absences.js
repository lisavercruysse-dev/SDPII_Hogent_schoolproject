import { axios } from "./index";

export async function getMyAbsences() {
  const { data } = await axios.get("/afwezigheden/me");
  return data;
}

export async function createAbsence(absenceData) {
  const { data } = await axios.post("/afwezigheden", absenceData);
  return data;
}

export async function cancelAbsence(id) {
  const { data } = await axios.patch(`/afwezigheden/${id}/cancel`);
  return data;
}

export async function getAllAbsences() {
  const { data } = await axios.get("/afwezigheden/all");
  return data;
}

export async function approveAbsence(id) {
  const { data } = await axios.patch(`/afwezigheden/${id}/approve`);
  return data;
}

export async function rejectAbsence(id) {
  const { data } = await axios.patch(`/afwezigheden/${id}/reject`);
  return data;
}
