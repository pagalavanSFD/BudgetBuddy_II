import { useState, useEffect } from "react";
import api from "../api/axios";
import "./Expense.css";

const Expense = () => {
  const [expenses, setExpenses] = useState([]);
  const [form, setForm] = useState({ description: "", amount: "", category: "" });
  const [editingId, setEditingId] = useState(null);

  const fetchExpenses = async () => {
    const res = await api.get("/expenses");
    setExpenses(res.data);
  };

  useEffect(() => {
    fetchExpenses();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await api.put(`/expenses/${editingId}`, form);
        setEditingId(null);
      } else {
        await api.post("/expenses", form);
      }
      setForm({ description: "", amount: "", category: "" });
      fetchExpenses();
    } catch (err) {
      console.error(err);
    }
  };

  const handleEdit = (expense) => {
    setForm({ description: expense.description, amount: expense.amount, category: expense.category });
    setEditingId(expense.id);
  };

  const handleDelete = async (id) => {
    await api.delete(`/expenses/${id}`);
    fetchExpenses();
  };

  return (
    <div className="expense-container">
      <h2>Expenses</h2>
      <form className="expense-form" onSubmit={handleSubmit}>
        <input type="text" name="description" placeholder="Description" value={form.description} onChange={handleChange} required />
        <input type="number" name="amount" placeholder="Amount" value={form.amount} onChange={handleChange} required />
        <input type="text" name="category" placeholder="Category" value={form.category} onChange={handleChange} required />
        <button type="submit">{editingId ? "Update" : "Add"} Expense</button>
      </form>

      <ul className="expense-list">
        {expenses.map((exp) => (
          <li key={exp.id}>
            {exp.description} | {exp.category} : ${exp.amount.toFixed(2)}
            <button onClick={() => handleEdit(exp)}>Edit</button>
            <button onClick={() => handleDelete(exp.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Expense;
