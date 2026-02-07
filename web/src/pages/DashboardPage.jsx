import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userService } from '../services/api';
import './Dashboard.css';

export default function DashboardPage() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [editMode, setEditMode] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // First, get user from localStorage (set during login)
    const storedUser = localStorage.getItem('user');
    const token = localStorage.getItem('token');

    if (!token) {
      navigate('/login');
      return;
    }

    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (e) {
        console.error('Failed to parse stored user:', e);
      }
    }

    // Try to fetch fresh user profile from API
    const fetchUserProfile = async () => {
      setLoading(true);
      try {
        const response = await userService.getProfile();
        if (response.data.success) {
          setUser(response.data.data);
          localStorage.setItem('user', JSON.stringify(response.data.data));
        } else {
          setError('Failed to load profile');
        }
      } catch (err) {
        console.error('Profile fetch error:', err);
        // Don't redirect if we have cached user data
        if (!storedUser) {
          setError('Failed to load profile. Please try logging in again.');
          setTimeout(() => navigate('/login'), 2000);
        }
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

  const formatDate = (dateString) => {
    if (!dateString) return 'Not available';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
  };

  if (loading) {
    return <div className="dashboard-container"><div className="loading-spinner">Loading...</div></div>;
  }

  if (error) {
    return <div className="dashboard-container error">{error}</div>;
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <div className="header-content">
          <h1>Welcome, {user?.username}! 👋</h1>
          <p className="header-subtitle">Manage your account and preferences</p>
        </div>
        <button className="logout-btn" onClick={handleLogout}>Logout</button>
      </div>

      {user && (
        <div className="dashboard-content">
          {/* Profile Card */}
          <div className="card profile-card">
            <div className="card-header">
              <h2>👤 User Profile</h2>
              <button 
                className="edit-btn"
                onClick={() => setEditMode(!editMode)}
              >
                {editMode ? 'Cancel' : '✏️ Edit'}
              </button>
            </div>
            
            <div className="profile-info">
              <div className="info-group">
                <label>Username</label>
                <p>{user.username}</p>
              </div>
              <div className="info-group">
                <label>Email Address</label>
                <p>{user.email}</p>
              </div>
              <div className="info-group">
                <label>User ID</label>
                <p className="mono">{user.id}</p>
              </div>
              <div className="info-group">
                <label>Role</label>
                <p><span className={`role-badge ${user.role.toLowerCase()}`}>{user.role}</span></p>
              </div>
              <div className="info-group">
                <label>Account Status</label>
                <p><span className={`status-badge ${user.isActive ? 'active' : 'inactive'}`}>
                  {user.isActive ? '🟢 Active' : '🔴 Inactive'}
                </span></p>
              </div>
              <div className="info-group">
                <label>Member Since</label>
                <p>{formatDate(user.createdAt)}</p>
              </div>
            </div>
          </div>

          {/* Account Info Cards */}
          <div className="cards-grid">
            <div className="info-card">
              <h3>🔐 Security</h3>
              <p>Password last changed: <strong>30 days ago</strong></p>
              <button className="action-btn">Change Password</button>
            </div>

            <div className="info-card">
              <h3>📧 Notifications</h3>
              <p>Email notifications: <strong>Enabled</strong></p>
              <button className="action-btn">Manage</button>
            </div>

            <div className="info-card">
              <h3>🌐 Preferences</h3>
              <p>Language: <strong>English</strong></p>
              <button className="action-btn">Settings</button>
            </div>

            <div className="info-card">
              <h3>📱 Connected Apps</h3>
              <p>Active sessions: <strong>1</strong></p>
              <button className="action-btn">View All</button>
            </div>
          </div>

          {/* Activity Section */}
          <div className="card activity-card">
            <h2>📊 Account Activity</h2>
            <div className="activity-timeline">
              <div className="activity-item">
                <span className="activity-time">Today</span>
                <p>Logged in from Windows</p>
              </div>
              <div className="activity-item">
                <span className="activity-time">Yesterday</span>
                <p>Account created and verified</p>
              </div>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="quick-actions">
            <button className="action-primary">Update Profile</button>
            <button className="action-secondary">Download Data</button>
            <button className="action-danger">Delete Account</button>
          </div>
        </div>
      )}
    </div>
  );
}
