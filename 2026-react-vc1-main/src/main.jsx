import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { createBrowserRouter, Navigate } from "react-router";
import { RouterProvider } from "react-router/dom";
import Layout from "./pages/Layout.jsx";
import Login from "./pages/Login.jsx";
import AbsenceWrapper from "./components/absences/AbsenceWrapper.jsx";
import Notifications from "./pages/Notifications.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import Planning from "./pages/Planning.jsx";
import Logout from "./pages/Logout.jsx";
import { AuthProvider } from "./contexts/Auth.context.jsx";

const router = createBrowserRouter([
  {
    path: "/login",
    Component: Login,
  },
  {
    path: "/logout",
    Component: Logout,
  },
  {
    Component: Layout,
    children: [
      {
        path: "/",
        element: <Navigate replace to="/dashboard" />,
      },
      {
        path: "/planning",
        Component: Planning,
      },
      {
        path: "/afwezigheden",
        Component: AbsenceWrapper,
      },
      {
        path: "/dashboard",
        Component: Dashboard,
      },
      {
        path: "/meldingen",
        Component: Notifications,
      },
    ],
  },
]);

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  </StrictMode>,
);
