import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import axios from "axios";

function SignupPage() {
  const [formData, setFormData] = useState({
    username: "",
    name: "",
    password: "",
    email: "",
    phoneNumber: "",
  });

  const [validationErrors, setValidationErrors] = useState({});
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // 해당 필드의 오류 메시지 초기화
    setValidationErrors(prev => ({ ...prev, [name]: "" }));
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`${process.env.REACT_APP_API_BASE_URL}/register`, formData);
      alert("회원가입이 성공적으로 완료되었습니다! 이메일 인증을 완료해야 로그인할 수 있습니다.");
      navigate("/login");
    } catch (error) {
      const res = error.response;

      if (!res) {
        alert("서버에 연결할 수 없습니다.");
        return;
      }

      // 서버에서 반환된 유효성 검사 오류 처리
      if (res.data?.status === "validation_error" && res.data.errors) {
        setValidationErrors(res.data.errors);
        setError("");
        return;
      }

      // 일반적인 에러 메시지 처리
      const message = res.data?.message || res.data;
      if (typeof message === "string") {
        setError(message);
      } else {
        setError("회원가입 중 알 수 없는 오류가 발생했습니다.");
      }
    }
  };

  return (
    <div>
      <h1>회원가입</h1>
      <form onSubmit={handleSignup} noValidate>
        <div style={{ marginBottom: "1rem" }}>
          <label>아이디</label><br />
          <input
            name="username"
            value={formData.username}
            onChange={handleChange}
            placeholder="아이디"
          />
          {validationErrors.username && (
            <span style={{ color: "red", marginLeft: "1rem" }}>{validationErrors.username}</span>
          )}
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label>이름</label><br />
          <input
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="이름"
          />
          {validationErrors.name && (
            <span style={{ color: "red", marginLeft: "1rem" }}>{validationErrors.name}</span>
          )}
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label>비밀번호</label><br />
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="비밀번호"
          />
          {validationErrors.password && (
            <span style={{ color: "red", marginLeft: "1rem" }}>{validationErrors.password}</span>
          )}
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label>이메일</label><br />
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="이메일"
          />
          {validationErrors.email && (
            <span style={{ color: "red", marginLeft: "1rem" }}>{validationErrors.email}</span>
          )}
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label>휴대전화</label><br />
          <input
            name="phoneNumber"
            value={formData.phoneNumber}
            onChange={handleChange}
            placeholder="휴대전화"
          />
          {validationErrors.phoneNumber && (
            <span style={{ color: "red", marginLeft: "1rem" }}>{validationErrors.phoneNumber}</span>
          )}
        </div>

        <button type="submit">회원가입</button>

        {error && (
          <p style={{ color: "red", marginTop: "1rem" }}>{error}</p>
        )}
      </form>
    </div>
  );
}

export default SignupPage;
