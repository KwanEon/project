import React, { useState, useContext } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { AuthContext } from "../contexts/AuthContext";
import axios from "axios";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { setUser, setUserRole } = useContext(AuthContext);

  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || "/"; // 로그인 후 리다이렉트할 경로

  const handleLogin = async (e) => {
    e.preventDefault();

    const params = new URLSearchParams();
    params.append("username", username);
    params.append("password", password);

    try {
      await axios.post(`${process.env.REACT_APP_API_BASE_URL}/auth/login`, params, {
        withCredentials: true,
      });

      // 로그인 성공 후 사용자 정보 요청
      const resUser = await axios.get(`${process.env.REACT_APP_API_BASE_URL}/auth/user`, { withCredentials: true });
      setUser(resUser.data);  // 사용자 정보 설정

      // 권한 정보도 별도 요청
      const resRole = await axios.get(`${process.env.REACT_APP_API_BASE_URL}/auth/role`, { withCredentials: true });
      setUserRole(resRole.data);  // 사용자 권한 설정

      alert("로그인 되었습니다.");
      
      if (from === "/login") {
        // 로그인 페이지에서 왔을 때는 홈으로 리다이렉트
        navigate("/", { replace: true });
      } else {
        navigate(from, { replace : true }); // 로그인 후 이전 페이지로 리다이렉트
      }
    } catch (err) {
      console.error("Login failed:", err);
      setError("아이디와 비밀번호를 확인해주세요.");
    }
  };

  return (
    <div>
      <h1>Login</h1>
      <form onSubmit={handleLogin}>
        <input
          type="text"
          placeholder="아이디"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
        {error && <p style={{ color: "red" }}>{error}</p>}
      </form>
    </div>
  );
}

export default Login;
