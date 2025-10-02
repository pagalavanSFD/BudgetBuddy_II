import { useState, useEffect } from "react";
import Sidebar from "../components/Sidebar";
import api from "../api/axios";
import "./SavingsGoals.css";

const SavingsGoals = () => {
  const [goals, setGoals] = useState([]);
  const [form, setForm] = useState({ goalName: "", targetAmount: "", deadline: "" });
  const [depositAmounts, setDepositAmounts] = useState({});

  // Fetch all savings goals
  const fetchGoals = async () => {
    try {
      const res = await api.get("/savings-goal");
      setGoals(res.data);
    } catch (err) {
      console.error("Error fetching goals:", err);
    }
  };

  useEffect(() => {
    fetchGoals();
  }, []);

  // Handle form input changes
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // Add a new savings goal
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post("/savings-goal", form);
      setForm({ goalName: "", targetAmount: "", deadline: "" });
      fetchGoals();
    } catch (err) {
      console.error("Error adding goal:", err);
    }
  };

  // Delete a goal
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this goal?")) return;
    try {
      await api.delete(`/savings-goal/${id}`);
      fetchGoals();
    } catch (err) {
      console.error("Error deleting goal:", err);
    }
  };

  // Deposit money into a goal
const handleDeposit = async (id) => {
  const amount = parseFloat(depositAmounts[id]);
  if (!amount || amount <= 0) return;

  const token = localStorage.getItem("token");
  console.log("JWT Token:", token);  // <-- log token
  console.log("Depositing amount:", amount, "to goal ID:", id);

  try {
    const res = await api.post(`/savings-goal/${id}/deposit?amount=${amount}`);
    console.log("Deposit response:", res.data);  // <-- log response
    setDepositAmounts({ ...depositAmounts, [id]: "" });
    fetchGoals();
  } catch (err) {
    console.error("Error depositing:", err.response ? err.response.data : err);
  }
};

  return (
    <div className="savings-page">
      <Sidebar />
      <div className="page-content">
        <h2>Savings Goals</h2>

        {/* Add Goal Form */}
        <form className="goal-form" onSubmit={handleSubmit}>
          <input
            name="goalName"
            value={form.goalName}
            onChange={handleChange}
            placeholder="Goal Name"
            required
          />
          <input
            name="targetAmount"
            value={form.targetAmount}
            onChange={handleChange}
            type="number"
            placeholder="Target Amount"
            required
          />
          <input
            name="deadline"
            value={form.deadline}
            onChange={handleChange}
            type="date"
            required
          />
          <button type="submit">Add Goal</button>
        </form>

        {/* Goals List */}
        <div className="goals-list">
          {goals.map((goal) => {
            const progress = goal.currentAmount
              ? Math.min((goal.currentAmount / goal.targetAmount) * 100, 100)
              : 0;

            return (
              <div key={goal.id} className="goal-card">
                <h4>{goal.goalName}</h4>
                <p>Target: ₹{goal.targetAmount}</p>
                <p>Saved: ₹{goal.currentAmount || 0}</p>
                <p>Deadline: {goal.deadline}</p>

                <div className="progress-bar-container">
                  <div
                    className="progress-bar"
                    style={{ width: `${progress}%`, background: progress === 100 ? "#4caf50" : "#2196f3" }}
                  ></div>
                </div>
                <p>{progress.toFixed(1)}% completed</p>

                <div className="goal-actions">
                  <input
                    type="number"
                    placeholder="Add money"
                    value={depositAmounts[goal.id] || ""}
                    onChange={(e) =>
                      setDepositAmounts({ ...depositAmounts, [goal.id]: e.target.value })
                    }
                    disabled={progress === 100}
                  />
                  <button
                    onClick={() => handleDeposit(goal.id)}
                    disabled={progress === 100 || !depositAmounts[goal.id] || depositAmounts[goal.id] <= 0}
                  >
                    Deposit
                  </button>
                  <button onClick={() => handleDelete(goal.id)}>Delete</button>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default SavingsGoals;
