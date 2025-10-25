import React, { useContext } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { FaShoppingCart } from "react-icons/fa";
import axios from "axios";
import { AuthContext } from "../contexts/AuthContext";

function Layout({ children }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, setUser, setUserRole } = useContext(AuthContext);

  const handleLogout = () => {
    axios
      .post(`http://localhost:8080/auth/logout`, {}, { withCredentials: true })
      .then(() => {
        console.log("로그아웃 성공");
        setUser(null);
        setUserRole("ANONYMOUS");
        localStorage.removeItem("token");
        alert("로그아웃 되었습니다.");
        navigate("/", { replace: true });
      })
      .catch((err) => {
        console.error("로그아웃 중 오류 발생:", err);
        alert("로그아웃 중 오류가 발생했습니다.");
      });
  };

  const handleLogin = () => {
    navigate("/login", { state: { from: location } });
  };

  const handleRegister = () => {
    navigate("/register");
  };

  const handleTitleClick = () => {
    navigate("/");
  };

  const handleMyPage = () => {
    navigate("/mypage");
  };

  const handleCart = () => {
    navigate("/cart");
  };

  return (
    <div style={{ padding: "2rem", position: "relative" }}>
      <header
        style={{
          position: "relative",
          paddingBottom: "1rem",
          marginBottom: "1rem",
          borderBottom: "2px solid #ddd",
        }}
      >
        <div
          style={{
            position: "absolute",
            top: 10,
            right: 10,
            display: "flex",
            alignItems: "center",
            gap: "0.5rem",
          }}
        >
          {user ? (
            <>
              <button onClick={handleCart} style={{ background: "none", border: "none", cursor: "pointer" }}>
                <FaShoppingCart size={24} />
              </button>
              <p
                style={{ margin: 0, cursor: "pointer", textDecoration: "underline" }}
                onClick={handleMyPage}
              >
                {user.name}님
              </p>
              <button onClick={handleLogout}>로그아웃</button>
            </>
          ) : (
            <>
              <button onClick={handleLogin}>로그인</button>
              <button onClick={handleRegister}>회원가입</button>
            </>
          )}
        </div>

        <h1 onClick={handleTitleClick} style={{ cursor: "pointer", textAlign: "center", margin: 0 }}>
          쇼핑몰 프로젝트
        </h1>
      </header>

      <main>{children}</main>
    </div>
  );
}

export default Layout;
