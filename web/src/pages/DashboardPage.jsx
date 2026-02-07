import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userService } from '../services/api';
import './Dashboard.css';

export default function DashboardPage() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          navigate('/login');
          return;
        }

        const response = await userService.getProfile();
        if (response.data.success) {
          setUser(response.data.data);
        } else {
          setError('Failed to load profile');
          navigate('/login');
        }
      } catch (err) {
        setError(err.response?.data?.message || 'Unauthorized');
        navigate('/login');
      } finally {
        setLoading(false);
      }
    };

    fetchUserProfile();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  if (loading) {
    return <div className="dashboard-container">Loading...</div>;
  }

  if (error) {
    return <div className="dashboard-container error">{error}</div>;
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Dashboard</h1>
        <button className="logout-btn" onClick={handleLogout}>Logout</button>
      </div>

      {user && (
        <div className="profile-card">
          <h2>User Profile</h2>
          <div className="profile-info">
            <div className="info-group">
              <label>Username:</label>
              <p>{user.username}</p>
            </div>
            <div className="info-group">
              <label>Email:</label>
              <p>{user.email}</p>
            </div>
            <div className="info-group">
              <label>Role:</label>
              <p><span className={`role-badge ${user.role.toLowerCase()}`}>{user.role}</span></p>
            </div>
            <div className="info-group">
              <label>Status:</label>
              <p><span className={`status-badge ${user.isActive ? 'active' : 'inactive'}`}>{user.isActive ? 'Active' : 'Inactive'}</span></p>
            </div>
            <div className="info-group">
              <label>User ID:</label>
              <p>{user.id}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
