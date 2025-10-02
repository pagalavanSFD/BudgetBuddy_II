import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import "./Navbar.css";

const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const location = useLocation();

  const links = [
    { path: "/", label: "Dashboard" },
    { path: "/income", label: "Income" },
    { path: "/expenses", label: "Expenses" },
    { path: "/budget", label: "Budget" },
    { path: "/savings-goals", label: "Savings Goals" },
    { path: "/recurring-deposit", label: "Recurring Deposits" }, // added
    { path: "/recurring-expense", label: "Recurring Expenses" }, // added
  ];

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <h1>BudgetBuddy</h1>
      </div>

      {/* Desktop links */}
      <div className={`nav-links ${isOpen ? "open" : ""}`}>
        {links.map((link) => (
          <Link
            key={link.path}
            to={link.path}
            onClick={() => setIsOpen(false)}
            className={location.pathname === link.path ? "active" : ""}
          >
            {link.label}
          </Link>
        ))}
      </div>

      {/* Hamburger button */}
      <button className="hamburger" onClick={() => setIsOpen(!isOpen)}>
        ☰
      </button>
    </nav>
  );
};

export default Navbar;
