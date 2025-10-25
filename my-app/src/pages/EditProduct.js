import React, { useState, useEffect, useRef, useContext } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import { AuthContext } from "../contexts/AuthContext";

const categoryOptions = [
  { value: "ELECTRONICS", label: "전자제품" },
  { value: "CLOTHING", label: "의류" },
  { value: "FOOD", label: "식품" },
  { value: "FURNITURE", label: "가구" },
  { value: "TOYS", label: "장난감" },
];

function EditProduct() {
  const { userRole, loading } = useContext(AuthContext);
  const { productId } = useParams();
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

    const fetchProduct = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/products/${productId}`, { withCredentials: true });
        setFormData(res.data);
      } catch (err) {
        setError("상품 정보를 불러오는 중 오류가 발생했습니다.");
      }
    };
    fetchProduct();
  }, [userRole, navigate, productId]);

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
      const response = await axios.put(
        `http://localhost:8080/products/${productId}`,
        formData,
        { withCredentials: true }
      );
      if (response.status === 200) {
        setSuccess("상품이 성공적으로 수정되었습니다.");
        navigate("/products");
      }
    } catch (err) {
      setError("상품 수정 중 오류가 발생했습니다.");
    }
  };

  if (userRole === "ANONYMOUS" || userRole === "ROLE_USER") {
    // 로그인하지 않은 사용자에게는 아무것도 렌더링하지 않음
    return null;
  }
  
  return (
    <div>
      <h2>상품 수정</h2>
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
          <button type="submit">수정</button>
          <button type="button" onClick={() => navigate("/products")}>
            취소
          </button>
        </div>
      </form>
    </div>
  );
}

export default EditProduct;
