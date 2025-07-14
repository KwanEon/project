import React, { useEffect, useState, useContext } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../contexts/AuthContext";

const categoryOptions = [
  { value: "", label: "카테고리 선택" },
  { value: "ELECTRONICS", label: "전자제품" },
  { value: "CLOTHING", label: "의류" },
  { value: "FOOD", label: "식품" },
  { value: "FURNITURE", label: "가구" },
  { value: "TOYS", label: "장난감" },
];

function ProductList() {
  const { userRole } = useContext(AuthContext); // 사용자 정보와 역할을 가져옴
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [category, setCategory] = useState("");
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchProducts = async () => {   // 상품 목록을 가져오는 함수
    setLoading(true);   // 로딩 상태 시작
    setError(""); // 에러 메시지 초기화
    try {
      const params = {};
      if (category) params.category = category;
      if (keyword) params.keyword = keyword;

      const res = await axios.get("http://localhost:8080/products", {
        params, // 카테고리와 키워드를 쿼리 파라미터로 전달
        withCredentials: true, // 쿠키(세션) 포함 → 로그인 정보 전송
      });

      if (Array.isArray(res.data)) {  // 응답이 배열인 경우
        setProducts(res.data);
      } else if (Array.isArray(res.data.data)) {  // { data: [...] } 형태인 경우
        setProducts(res.data.data);
      } else {  // 응답이 배열이 아닌 경우
        setProducts([]);
        setError("상품 데이터가 배열 형식이 아닙니다.");
      }
    } catch (e) {
      setError("상품 목록을 불러오는 중 오류가 발생했습니다.");
      setProducts([]);
    }
    setLoading(false);  // 로딩 종료
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const handleAddProduct = () => navigate("/add-product");
  const handleEditProduct = (productId) => navigate(`/edit-product/${productId}`);

  const handleDeleteProduct = async (productId) => {
    const confirmDelete = window.confirm("정말로 이 상품을 삭제하시겠습니까?");
    if (!confirmDelete) return; // 사용자가 삭제를 취소한 경우
    try {
      await axios.delete(`http://localhost:8080/products/${productId}`, { withCredentials: true });
      fetchProducts();  // 삭제 후 상품 목록을 다시 불러옴
      alert("상품이 삭제되었습니다.");
    } catch (e) {
      alert("상품 삭제 중 오류가 발생했습니다.");
    }
  };

  const handleSearch = (e) => {   // 검색어와 카테고리로 상품 목록을 필터링
    e.preventDefault(); // 폼 제출 시 페이지 새로고침 방지
    fetchProducts();  // 검색어와 카테고리를 반영하여 상품 목록을 다시 불러옴
  };

  return (
    <div>
      <h2>상품 목록</h2>

      <form onSubmit={handleSearch}>
        <select value={category} onChange={(e) => setCategory(e.target.value)}>
          {categoryOptions.map(({ value, label }) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>

        <input
          type="text"
          placeholder="검색어 입력"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />

        <button type="submit">검색</button>
      </form>

      {loading && <p>로딩 중...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {userRole === "ROLE_ADMIN" && (
        <div style={{ marginBottom: "1rem" }}>
          <button onClick={handleAddProduct}>상품 추가</button>
        </div>
      )}

      <ul>
        {products.length === 0 ? (
          <li>상품이 없습니다.</li>
        ) : (
          products.map((product) => (
            <li key={product.id}>
              <strong
                style={{ cursor: "pointer", color: "blue", textDecoration: "underline" }}
                onClick={() => navigate(`/products/${product.id}`)}
              >
                {product.name}
              </strong>{" "}
              - {product.category} - ₩{product.price} - 재고: {product.stock}
              {userRole === "ROLE_ADMIN" && (
                <div>
                  <button onClick={() => handleEditProduct(product.id)}>수정</button>
                  <button onClick={() => handleDeleteProduct(product.id)}>삭제</button>
                </div>
              )}
            </li>
          ))
        )}
      </ul>
    </div>
  );
}

export { ProductList };
