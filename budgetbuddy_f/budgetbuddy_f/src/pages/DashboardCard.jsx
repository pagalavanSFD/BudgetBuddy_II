// src/components/DashboardCard.jsx
import React from "react";
import "./DashboardCard.css";

const DashboardCard = ({ title, value, icon }) => {
  return (
    <div className="dashboard-card">
      {icon && <div className="dashboard-icon">{icon}</div>}
      <div className="dashboard-info">
        <h3>{value}</h3>
        <p>{title}</p>
      </div>
    </div>
  );
};

export default DashboardCard;
