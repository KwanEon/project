import React, { createContext, useState, useEffect } from "react";
import axios from "axios";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);      // 유저 정보
  const [userRole, setUserRole] = useState(null);  // 권한 정보, 예: 'ANONYMOUS', 'USER', 'ADMIN'
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/auth/user`, { withCredentials: true });
        if (res.data) {
          setUser(res.data);
          const roleRes = await axios.get(`http://localhost:8080/auth/role`, { withCredentials: true });
          setUserRole(roleRes.data); // 예: 'ROLE_USER', 'ROLE_ADMIN'
        } else {
          setUser(null);
          setUserRole("ANONYMOUS");
        }
      } catch (error) {
        setUser(null);
        setUserRole("ANONYMOUS");
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  return (
    <AuthContext.Provider value={{ user, userRole, loading, setUser, setUserRole }}>
      {children}
    </AuthContext.Provider>
  );
}
