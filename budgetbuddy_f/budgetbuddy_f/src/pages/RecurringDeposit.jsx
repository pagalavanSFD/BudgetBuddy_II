// src/pages/RecurringDeposit.jsx
import { useState, useEffect, useContext } from "react";
import Sidebar from "../components/Sidebar";
import api from "../api/axios";
import { AuthContext } from "../context/AuthContext";
import "./RecurringDeposit.css";

const RecurringDeposit = () => {
  const { token } = useContext(AuthContext);
  const [deposits, setDeposits] = useState([]);
  const [goals, setGoals] = useState([]);
  const [form, setForm] = useState({
    goalId: "",
    amount: "",
    frequency: "WEEKLY",
    nextDepositDate: ""
  });

  const fetchDeposits = async () => {
    if (!token) return;
    try {
      const res = await api.get("/recurring-deposit");
      setDeposits(res.data);
    } catch (err) {
      console.error("Error fetching deposits:", err.response?.data || err.message);
    }
  };

  const fetchGoals = async () => {
    if (!token) return;
    try {
      const res = await api.get("/savings-goal");
      setGoals(res.data);
    } catch (err) {
      console.error("Error fetching goals:", err.response?.data || err.message);
    }
  };

  useEffect(() => {
    if (token) {
      fetchDeposits();
      fetchGoals();
    }
  }, [token]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!token) return;

    const selectedGoal = goals.find(g => g.id === parseInt(form.goalId));
    if (!selectedGoal) return;

    const payload = {
      goal: selectedGoal,
      amount: parseFloat(form.amount),
      frequency: form.frequency,
      nextDepositDate: form.nextDepositDate
    };

    try {
      await api.post("/recurring-deposit", payload);
      setForm({ goalId: "", amount: "", frequency: "WEEKLY", nextDepositDate: "" });
      fetchDeposits();
    } catch (err) {
      console.error("Error creating deposit:", err.response?.data || err.message);
    }
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`/recurring-deposit/${id}`);
      fetchDeposits();
    } catch (err) {
      console.error("Error deleting deposit:", err.response?.data || err.message);
    }
  };

  const handleProcessNow = async () => {
    try {
      await api.post("/recurring-deposit/process");
      alert("Recurring deposits processed!");
      fetchDeposits();
    } catch (err) {
      console.error("Error processing deposits:", err.response?.data || err.message);
      alert("Error processing deposits");
    }
  };

  return (
    <div>
      <Sidebar />
      <div className="page-content">
        <h2>Recurring Deposits</h2>

        <form className="deposit-form" onSubmit={handleSubmit}>
          <select name="goalId" value={form.goalId} onChange={handleChange} required>
            <option value="">Select Goal</option>
            {goals.map(g => <option key={g.id} value={g.id}>{g.goalName}</option>)}
          </select>

          <input type="number" name="amount" value={form.amount} onChange={handleChange} placeholder="Amount" required />

          <select name="frequency" value={form.frequency} onChange={handleChange}>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
          </select>

          <input type="date" name="nextDepositDate" value={form.nextDepositDate} onChange={handleChange} required />

          <button type="submit">Create Recurring Deposit</button>
        </form>

        <button className="process-btn" onClick={handleProcessNow}>Process Deposits Now</button>

        <div className="deposits-list">
          {deposits.map(d => (
            <div key={d.id} className="deposit-card">
              <h4>{d.goal.goalName}</h4>
              <p>Amount: ₹{d.amount}</p>
              <p>Frequency: {d.frequency}</p>
              <p>Next Deposit: {d.nextDepositDate}</p>
              <button onClick={() => handleDelete(d.id)}>Delete</button>
            </div>
          ))}
          {deposits.length === 0 && <p>No recurring deposits yet.</p>}
        </div>
      </div>
    </div>
  );
};

export default RecurringDeposit;
