import { useEffect, useState } from "react";
import api from "../api/axios";

const BudgetTest = () => {
  const [results, setResults] = useState([]);

  const testData = async () => {
    try {
      const months = [
        { month: 9, year: 2025 },  // past month
        { month: 10, year: 2025 }, // current month
        { month: 11, year: 2025 }, // future month
      ];

      // Recurring Income & Expense
      const recurringIncome = { amount: 20000, category: "Salary", date: "2025-10-01", recurring: true, frequency: "MONTHLY" };
      const recurringExpense = { amount: 5000, category: "Rent", date: "2025-10-01", recurring: true, frequency: "MONTHLY" };

      // One-time Expenses
      const oneTimeExpenses = [
        { amount: 1500, category: "Food", date: "2025-10-05", recurring: false },
        { amount: 800, category: "Entertainment", date: "2025-10-10", recurring: false }
      ];

      // Post recurring data only once (backend should handle month logic)
      await api.post("/income", recurringIncome);
      await api.post("/expenses", recurringExpense);

      // Post one-time expenses
      for (let e of oneTimeExpenses) {
        await api.post("/expenses", e);
      }

      // Set a total budget for each month
      for (let m of months) {
        await api.post(`/budget?month=${m.month}&year=${m.year}&amount=25000`);
      }

      // Fetch and check budget status for each month
      const monthResults = [];
      for (let m of months) {
        const res = await api.get(`/budget/check?month=${m.month}&year=${m.year}`);
        monthResults.push({
          month: m.month,
          year: m.year,
          status: res.data.status,
          remaining: res.data.remaining,
          message: res.data.message
        });
      }

      setResults(monthResults);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    testData();
  }, []);

  return (
    <div style={{ padding: "20px" }}>
      <h2>Budget Test Results</h2>
      {results.map((r) => (
        <div key={`${r.month}-${r.year}`} style={{ border: "1px solid #ccc", margin: "10px", padding: "10px" }}>
          <p><strong>Month/Year:</strong> {r.month}/{r.year}</p>
          <p><strong>Status:</strong> {r.status}</p>
          <p><strong>Remaining:</strong> ₹{r.remaining}</p>
          <p><strong>Message:</strong> {r.message}</p>
        </div>
      ))}
    </div>
  );
};

export default BudgetTest;
