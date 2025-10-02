import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import "./Sidebar.css";

const Sidebar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const location = useLocation();

  const links = [
    { path: "/", label: "Dashboard" },
    { path: "/income", label: "Income" },
    { path: "/expenses", label: "Expenses" },
    { path: "/budget", label: "Budget" },
    { path: "/savings-goals", label: "Savings Goals" },
    { path: "/recurring-deposit", label: "Recurring Deposits" },
    { path: "/recurring-expense", label: "Recurring Expenses" },
  ];

  return (
    <>
      {/* Sidebar */}
      <div className={`sidebar ${isOpen ? "open" : ""}`}>
        <h2>BudgetBuddy</h2>
        <nav>
          <ul>
            {links.map((link) => (
              <li key={link.path}>
                <Link
                  to={link.path}
                  onClick={() => setIsOpen(false)}
                  className={location.pathname === link.path ? "active" : ""}
                >
                  {link.label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
      </div>

      {/* Hamburger button for mobile */}
      <button className="hamburger" onClick={() => setIsOpen(!isOpen)}>
        ☰
      </button>

      {/* Overlay for mobile */}
      {isOpen && <div className="overlay" onClick={() => setIsOpen(false)}></div>}
    </>
  );
};

export default Sidebar;
