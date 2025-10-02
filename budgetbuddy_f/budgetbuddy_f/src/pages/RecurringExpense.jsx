// src/pages/RecurringExpense.jsx
import React, { useState, useEffect, useContext } from "react";
import api from "../api/axios";
import Sidebar from "../components/Sidebar";
import { AuthContext } from "../context/AuthContext";
import "./RecurringExpense.css";

const RecurringExpense = () => {
  const { token } = useContext(AuthContext);
  const [expenses, setExpenses] = useState([]);
  const [form, setForm] = useState({
    title: "",
    category: "",
    amount: "",
    frequency: "MONTHLY",
    startDate: ""
  });

  // Fetch recurring expenses
  const fetchExpenses = async () => {
    if (!token) return;
    try {
      const res = await api.get("/recurring-expense");
      setExpenses(res.data);
    } catch (err) {
      console.error("Error fetching recurring expenses:", err.response?.data || err.message);
    }
  };

  useEffect(() => {
    if (token) fetchExpenses();
  }, [token]);

  // Form handlers
  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!token) return;
    try {
      const res = await api.post("/recurring-expense", form);
      setExpenses([...expenses, res.data]);
      setForm({ title: "", category: "", amount: "", frequency: "MONTHLY", startDate: "" });
    } catch (err) {
      console.error("Error adding recurring expense:", err.response?.data || err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!token) return;
    try {
      await api.delete(`/recurring-expense/${id}`);
      setExpenses(expenses.filter(exp => exp.id !== id));
    } catch (err) {
      console.error("Error deleting recurring expense:", err.response?.data || err.message);
    }
  };

  return (
    <div className="recurring-expenses-page">
      <Sidebar />
      <div className="page-content">
        <h2>Recurring Expenses</h2>

        <form className="recurring-expense-form" onSubmit={handleSubmit}>
          <input name="title" value={form.title} onChange={handleChange} placeholder="Title" required />
          <input name="category" value={form.category} onChange={handleChange} placeholder="Category" />
          <input type="number" step="0.01" name="amount" value={form.amount} onChange={handleChange} placeholder="Amount" required />
          <select name="frequency" value={form.frequency} onChange={handleChange}>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
            <option value="YEARLY">Yearly</option>
          </select>
          <input type="date" name="startDate" value={form.startDate} onChange={handleChange} required />
          <button type="submit">Add Recurring Expense</button>
        </form>

        <div className="recurring-expense-list">
          {expenses.map(exp => (
            <div key={exp.id} className="expense-card">
              <h4>{exp.title}</h4>
              <p>Category: {exp.category}</p>
              <p>Amount: ₹{exp.amount}</p>
              <p>Frequency: {exp.frequency}</p>
              <p>Start Date: {exp.startDate}</p>
              <button onClick={() => handleDelete(exp.id)}>Delete</button>
            </div>
          ))}
          {expenses.length === 0 && <p>No recurring expenses found.</p>}
        </div>
      </div>
    </div>
  );
};

export default RecurringExpense;
