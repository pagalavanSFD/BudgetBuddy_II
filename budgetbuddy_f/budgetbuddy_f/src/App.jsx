import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Signup from "./pages/Signup";
import Login from "./pages/Login";
import ProtectedRoute from "./components/ProtrctedRoute";
import Dashboard from "./pages/Dashboard";
import Income from "./pages/Income";
import Expense from "./pages/Expense";
import Budget from "./pages/Budget";
import SavingsGoals from "./pages/SavingsGoals";
import RecurringDeposit from "./pages/RecurringDeposit";
import RecurringExpense from "./pages/RecurringExpense";


function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/income" element={<ProtectedRoute><Income /></ProtectedRoute>} />
          <Route path="/expenses" element={<ProtectedRoute><Expense /></ProtectedRoute>} />
          <Route path="/budget" element={<ProtectedRoute><Budget /></ProtectedRoute>} />
          <Route path="/savings-goals" element={<ProtectedRoute><SavingsGoals /></ProtectedRoute>} />
         <Route
          path="/recurring-deposit"
          element={
            <ProtectedRoute>
              <RecurringDeposit />
            </ProtectedRoute>
          }
        />
        {/* <Route
          path="/budget-test"
          element={
            <ProtectedRoute>
              <BudgetTest />
            </ProtectedRoute>
          }
        /> */}
        

<Route path="/recurring-expense" element={<ProtectedRoute><RecurringExpense /></ProtectedRoute>} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
