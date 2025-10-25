import React, { useEffect, useRef } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function EmailVerify() {
  const navigate = useNavigate();
  const hasRedirected = useRef(false);

  useEffect(() => {
    const verifyEmail = async () => {
      const urlParams = new URLSearchParams(window.location.search);
      const token = urlParams.get("token");

      if (!token) {
        alert("유효하지 않은 인증 요청입니다.");
        return;
      }

      try {
        await axios.get(`${process.env.REACT_APP_API_BASE_URL}/auth/verify?token=${token}`, {
          withCredentials: true,
        });
        if (!hasRedirected.current) {
          hasRedirected.current = true;
          alert("이메일 인증이 완료되었습니다.");
          navigate("/login");
        }
      } catch (error) {
        if (!hasRedirected.current) {
          hasRedirected.current = true;
          console.error("인증 오류:", error);
          alert("인증에 실패했습니다. 다시 시도해주세요.");
          navigate("/login");
        }
      }
    };

    verifyEmail();
  }, [navigate]);

  return <div>이메일 인증 중입니다...</div>;
}

export default EmailVerify;
