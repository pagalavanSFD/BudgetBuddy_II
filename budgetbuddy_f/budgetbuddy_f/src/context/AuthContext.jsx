// src/context/AuthContext.jsx
import { createContext, useState } from "react";
import api from "../api/axios"; // ✅ use our configured axios instance

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem("token") || null);

  const signup = async (form) => {
    await api.post("/auth/signup", form); // ✅ uses baseURL
  };

  const login = async (form) => {
    try {
      const res = await api.post("/auth/login", form);

      if (res.data.token) {
        localStorage.setItem("token", res.data.token);
        setToken(res.data.token);
        return res.data; // maybe includes username/roles
      } else {
        return { error: res.data.error || "No token returned" };
      }
    } catch (err) {
      console.error("Login error:", err.response?.data || err.message);
      throw err;
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
