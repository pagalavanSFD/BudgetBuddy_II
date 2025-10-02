import { useState, useEffect } from "react";
import Sidebar from "../components/Sidebar";
import api from "../api/axios";
import "./Budget.css";

const Budget = () => {
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [totalBudget, setTotalBudget] = useState("");
  const [categories, setCategories] = useState([
    "Food",
    "Rent",
    "Entertainment",
    "Utilities",
    "Other",
  ]);
  const [categoryBudget, setCategoryBudget] = useState({});
  const [budgetStatus, setBudgetStatus] = useState(null);

  // Fetch budget status from backend
  const fetchBudgetStatus = async () => {
    try {
      const res = await api.get(`/budget/check?month=${month}&year=${year}`);
      setBudgetStatus(res.data);
      setTotalBudget(res.data.remaining.add(res.data.totalExpenses).toString());
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchBudgetStatus();
  }, [month, year]);

  // Set total budget
  const handleSetBudget = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/budget?month=${month}&year=${year}&amount=${totalBudget}`);
      fetchBudgetStatus();
    } catch (err) {
      console.error(err);
    }
  };

  // Add new category
  const addCategory = () => {
    const name = prompt("Enter new category name:");
    if (name && !categories.includes(name)) {
      setCategories([...categories, name]);
    }
  };

  // Remove a category
  const removeCategory = (cat) => {
    if (window.confirm(`Remove category "${cat}"?`)) {
      setCategories(categories.filter((c) => c !== cat));
      const newCatBudget = { ...categoryBudget };
      delete newCatBudget[cat];
      setCategoryBudget(newCatBudget);
    }
  };

  return (
    <div>
      <Sidebar />
      <div className="page-content">
        <h2>Monthly Budget</h2>

        <form className="budget-form" onSubmit={handleSetBudget}>
          <input
            type="number"
            placeholder="Month (1-12)"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
            required
          />
          <input
            type="number"
            placeholder="Year"
            value={year}
            onChange={(e) => setYear(e.target.value)}
            required
          />
          <input
            type="number"
            placeholder="Total Monthly Budget"
            value={totalBudget}
            onChange={(e) => setTotalBudget(e.target.value)}
            required
          />

          <h3>Category Budgets</h3>
          {categories.map((cat) => (
            <div key={cat} className="category-input">
              <input
                type="number"
                placeholder={`${cat} Budget`}
                value={categoryBudget[cat] || ""}
                onChange={(e) =>
                  setCategoryBudget({
                    ...categoryBudget,
                    [cat]: parseFloat(e.target.value),
                  })
                }
              />
              <button type="button" onClick={() => removeCategory(cat)}>
                x
              </button>
            </div>
          ))}

          <button type="button" onClick={addCategory}>
            + Add Category
          </button>
          <button type="submit">Set Budget</button>
        </form>

        {budgetStatus && (
          <div className={`budget-status ${budgetStatus.status}`}>
            <h3>Budget Overview</h3>
            <p>Total Income: ₹{budgetStatus.totalIncome}</p>
            <p>Total Expenses: ₹{budgetStatus.totalExpenses}</p>
            <p>Recurring Expenses: ₹{budgetStatus.totalRecurringExpenses}</p>
            <p>Recurring Deposits: ₹{budgetStatus.totalRecurringDeposits}</p>
            <p>Remaining Balance: ₹{budgetStatus.remaining}</p>
            {budgetStatus.status === "exceeded" && (
              <p style={{ color: "red" }}>Budget Exceeded!</p>
            )}
            {budgetStatus.status === "almost" && (
              <p style={{ color: "orange" }}>Almost reached budget!</p>
            )}
            {budgetStatus.status === "ok" && (
              <p style={{ color: "green" }}>Budget OK</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Budget;
