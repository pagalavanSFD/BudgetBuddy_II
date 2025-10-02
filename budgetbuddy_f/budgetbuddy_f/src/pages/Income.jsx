import { useState, useEffect } from "react";
import api from "../api/axios";
import "./Income.css";

const Income = () => {
  const [incomes, setIncomes] = useState([]);
  const [form, setForm] = useState({ source: "", amount: "" });
  const [editingId, setEditingId] = useState(null);

  const fetchIncomes = async () => {
    try {
      const res = await api.get("/income");
      setIncomes(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchIncomes();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await api.put(`/income/${editingId}`, form);
        setEditingId(null);
      } else {
        await api.post("/income", form);
      }
      setForm({ source: "", amount: "" });
      fetchIncomes();
    } catch (err) {
      console.error(err);
    }
  };

  const handleEdit = (income) => {
    setForm({ source: income.source, amount: income.amount });
    setEditingId(income.id);
  };

  const handleDelete = async (id) => {
    await api.delete(`/income/${id}`);
    fetchIncomes();
  };

  return (
    <div className="income-container">
      <h2>Income</h2>
      <form className="income-form" onSubmit={handleSubmit}>
        <input
          type="text"
          name="source"
          placeholder="Source"
          value={form.source}
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="amount"
          placeholder="Amount"
          value={form.amount}
          onChange={handleChange}
          required
        />
        <button type="submit">{editingId ? "Update" : "Add"} Income</button>
      </form>

      <ul className="income-list">
        {incomes.map((inc) => (
          <li key={inc.id}>
            {inc.source}: ${inc.amount.toFixed(2)}{" "}
            <button onClick={() => handleEdit(inc)}>Edit</button>
            <button onClick={() => handleDelete(inc.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Income;
