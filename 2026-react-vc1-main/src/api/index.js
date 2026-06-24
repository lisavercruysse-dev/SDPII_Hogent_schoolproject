import axiosRoot from "axios";
import { JWT_TOKEN_KEY } from "../contexts/auth";

const DEFAULT_API_URL = "http://localhost:3000/api";
const baseUrl = (import.meta.env.VITE_API_URL || DEFAULT_API_URL).replace(
  /\/+$/,
  "",
);

export const axios = axiosRoot.create({
  baseURL: baseUrl,
});

axios.interceptors.request.use((config) => {
  const token = localStorage.getItem(JWT_TOKEN_KEY);

  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }

  return config;
});

export async function getById(url) {
  const { data } = await axios.get(url);
  return data;
}

export async function getAll(url) {
  const { data } = await axios.get(url);
  return Array.isArray(data) ? data : (data?.items || []);
}

export const post = async (url, { arg }) => {
  const { data } = await axios.post(url, arg);
  return data;
};

export const save = async (url, { arg: body }) => {
  await axios.post(url, body);
};

export const updateById = async (url, { arg: body }) => {
  await axios.put(url, body);
};

export async function deleteResource(url) {
  const { data } = await axios.delete(url);
  return data;
}
