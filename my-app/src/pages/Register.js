import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import axios from "axios";

function SignupPage() {
  const [formData, setFormData] = useState({
    아이디: "",
    이름: "",
    비밀번호: "",
    이메일: "",
    휴대전화: "",
    주소: "",
  });
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleChange = (e) => { // 입력 필드의 변경을 처리하는 함수
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8080/register", formData, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      alert("회원가입이 성공적으로 완료되었습니다!");
      navigate("/login"); // 회원가입 후 로그인 페이지로 이동
    } catch (error) {
      if (!error.response) {
        alert("서버에 연결할 수 없습니다. 네트워크를 확인하세요.");
        return;
      }
      console.error("Error:", error.response);
      alert(error.response.data?.message || "회원가입 중 오류가 발생했습니다.");
    }
  };

  return (
    <div>
      <h1>Signup</h1>
      <form onSubmit={handleSignup}>
        {Object.keys(formData).map((key) => (
          <div key={key}>
            <input
              type="text"
              placeholder={key}
              name={key}
              value={formData[key]}
              onChange={handleChange}
              required
            />
          </div>
        ))}
        <button type="submit">Signup</button>
        {error && <p>{error}</p>}
      </form>
    </div>
  );
}

export default SignupPage;
