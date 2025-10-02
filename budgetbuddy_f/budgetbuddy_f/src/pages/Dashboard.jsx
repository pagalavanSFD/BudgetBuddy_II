import { useEffect, useState, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import api from "../api/axios";
import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";
import "./Dashboard.css";

const Dashboard = () => {
  const { token, logout } = useContext(AuthContext);
  const [email, setEmail] = useState("");
  const [expenses, setExpenses] = useState([]);
  const [income, setIncome] = useState([]);
  const [totalIncome, setTotalIncome] = useState(0);
  const [totalExpenses, setTotalExpenses] = useState(0);
  const [balance, setBalance] = useState(0);
  const [categories, setCategories] = useState([]); // Dynamic categories
  const [categorySpent, setCategorySpent] = useState({});
  const [recurringDeposits, setRecurringDeposits] = useState([]);

// Fetch recurring deposits
const fetchRecurringDeposits = async () => {
  try {
    const res = await api.get("/recurring-deposit", {
      headers: { Authorization: `Bearer ${token}` },
    });
    setRecurringDeposits(res.data);
  } catch (err) {
    console.error("Error fetching recurring deposits:", err);
  }
};

useEffect(() => {
  if (token) {
    fetchData();
    fetchRecurringDeposits();
  }
}, [token]);

  // Fetch user email from JWT
  useEffect(() => {
    if (!token) return;
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      setEmail(payload.sub);
    } catch (err) {
      console.error("Invalid token");
    }
  }, [token]);

  // Fetch income and expenses
  const fetchData = async () => {
    try {
      const expRes = await api.get("/expenses", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const incRes = await api.get("/income", {
        headers: { Authorization: `Bearer ${token}` },
      });

      setExpenses(expRes.data);
      setIncome(incRes.data);

      // Calculate totals
      const totalInc = incRes.data.reduce((sum, i) => sum + i.amount, 0);
      const totalExp = expRes.data.reduce((sum, e) => sum + e.amount, 0);
      setTotalIncome(totalInc);
      setTotalExpenses(totalExp);
      setBalance(totalInc - totalExp);

      // Dynamic categories
      const cats = [...new Set(expRes.data.map((e) => e.category))];
      setCategories(cats);

      // Calculate category spent
      const catSpent = {};
      cats.forEach((c) => {
        catSpent[c] = expRes.data
          .filter((e) => e.category === c)
          .reduce((sum, e) => sum + e.amount, 0);
      });
      setCategorySpent(catSpent);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    if (token) fetchData();
  }, [token]);

  return (
    <div className="dashboard-container">
      <Navbar />
      <Sidebar />

      <div className="page-content">
        <h2>Welcome, {email}</h2>
        <div className="summary-cards">
          <div className="card">
            <h3>Total Income</h3>
            <p>₹{totalIncome}</p>
          </div>
          <div className="card">
            <h3>Total Expenses</h3>
            <p>₹{totalExpenses}</p>
          </div>
          <div className="card">
            <h3>Remaining Balance</h3>
            <p>₹{balance}</p>
          </div>
        </div>

        <h3>Category-wise Spending</h3>
        {categories.length === 0 && <p>No expenses yet.</p>}
        {categories.map((cat) => {
          const spent = categorySpent[cat] || 0;
          const percent = totalIncome ? Math.min((spent / totalIncome) * 100, 100) : 0;
          return (
            <div key={cat} className="category-bar">
              <span>{cat}: ₹{spent}</span>
              <div className="progress-container">
                <div
                  className={`progress ${percent > 100 ? "over" : ""}`}
                  style={{ width: `${percent}%` }}
                ></div>
              </div>
            </div>
          );
        })}

        <h3>Upcoming Recurring Deposits</h3>
        {recurringDeposits.length === 0 && <p>No recurring deposits scheduled.</p>}
        {recurringDeposits.map((d) => (
        <div key={d.id} className="recurring-card">
            <span>{d.goal.goalName} - ₹{d.amount} ({d.frequency})</span>
            <span>Next: {d.nextDepositDate}</span>
        </div>
        ))}


        <button className="logout-btn" onClick={logout}>
          Logout
        </button>
      </div>
    </div>
  );
};

export default Dashboard;
