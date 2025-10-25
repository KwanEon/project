import React, { useContext, useState, useEffect, useRef } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../contexts/AuthContext";

const categoryOptions = [
  { value: "ELECTRONICS", label: "전자제품" },
  { value: "CLOTHING", label: "의류" },
  { value: "FOOD", label: "식품" },
  { value: "FURNITURE", label: "가구" },
  { value: "TOYS", label: "장난감" },
];

function AddProduct() {
  const { user, userRole, loading } = useContext(AuthContext);
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    price: "",
    stock: "",
    description: "",
    category: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const hasRedirected = useRef(false);

  useEffect(() => {
    if (loading) return;

    if (!hasRedirected.current) {
      hasRedirected.current = true;
      if (userRole === "ANONYMOUS") {
        alert("로그인이 필요합니다.");
        navigate("/login");
        return;
      } else if (userRole !== "ROLE_ADMIN") {
        alert("접근 권한이 없습니다.");
        navigate("/products");
        return;
      }
    }
  }, [userRole, navigate, user, loading]);

  if (loading || userRole === "ANONYMOUS" || userRole !== "ROLE_ADMIN") {
    return null;
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    try {
      const response = await axios.post(
        `http://localhost:8080/products`,
        formData,
        { withCredentials: true }
      );
      if (response.status === 201) {
        setSuccess("상품이 성공적으로 추가되었습니다.");
        navigate("/products");
      }
    } catch (err) {
      setError("상품 추가 중 오류가 발생했습니다.");
    }
  };

  return (
    <div>
      <h2>상품 추가</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {success && <p style={{ color: "green" }}>{success}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>상품명</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>가격</label>
          <input
            type="number"
            name="price"
            value={formData.price}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>재고</label>
          <input
            type="number"
            name="stock"
            value={formData.stock}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>설명</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleInputChange}
          />
        </div>
        <div>
          <label>카테고리</label>
          <select
            name="category"
            value={formData.category}
            onChange={handleInputChange}
            required
          >
            <option value="" disabled>
              카테고리 선택
            </option>
            {categoryOptions.map(({ value, label }) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>
        <div>
          <button type="submit">추가</button>
          <button type="button" onClick={() => navigate("/products")}>
            취소
          </button>
        </div>
      </form>
    </div>
  );
}

export default AddProduct;
