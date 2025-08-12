import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";

function ProductDetail() {
  const { productId } = useParams(); // URL에서 productId 추출
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1); // 수량 상태 추가
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchProduct = async () => {
      setLoading(true);
      setError("");
      try {
        const res = await axios.get(`${process.env.REACT_APP_API_BASE_URL}/products/${productId}`, {
          withCredentials: true,
        });
        setProduct(res.data);
      } catch (e) {
        setError("상품 정보를 불러오는 중 오류가 발생했습니다.");
      }
      setLoading(false);
    };

    fetchProduct();
  }, [productId]);

  // 수량 증감
  const handleQuantityChange = (operation) => {
    setQuantity((prevQuantity) => {
      const newQuantity = operation === "increase" ? prevQuantity + 1 : prevQuantity - 1;
      return newQuantity > 0 ? newQuantity : 1; // 최소 수량 1 보장
    });
  };

  // 장바구니에 추가
  const handleAddToCart = () => {
    axios
      .post(
        `${process.env.REACT_APP_API_BASE_URL}/cartitem/${productId}?quantity=${quantity}`, // 수량을 쿼리 파라미터로 전달
        {}, // POST 요청의 body는 비워둠
        { withCredentials: true }
      )
      .then(() => {
        alert("장바구니에 추가되었습니다.");
      })
      .catch((err) => {
        if (err.response && err.response.status === 401) {
          alert("로그인이 필요합니다.");
          navigate("/login"); // 로그인 페이지로 이동
          return;
        }
        console.error("장바구니 추가 중 오류:", err);
        alert("장바구니에 추가하는 중 오류가 발생했습니다.");
      });
  };

  // 개별 주문하기
  const handleOrder = () => {
    navigate(`/order/${productId}?quantity=${quantity}`);
  };

  return (
    <div>
      {loading && <p>로딩 중...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
      {product ? (
        <div>
          <h2>{product.name}</h2>

          {product.imageUrl && (  // 이미지 URL이 있는 경우에만 렌더링
            <div style = {{ margin: "1rem 0" }}>
              <img
                src={product.imageUrl.startsWith("http")  // 이미지 URL이 절대 경로인 경우
                  ? product.imageUrl  // 이미지 URL이 절대 경로인 경우 그대로 사용
                  : `http://localhost:8080${product.imageUrl}`} // 이미지 URL이 절대 경로가 아닌 경우 처리
                alt={product.name}
                style={{ maxWidth: "300px", borderRadius: "8px", border: "1px solid #ccc" }}
              />
            </div>
          )}

          <p>설명: {product.description}</p>
          <p>가격: ₩{product.price}</p>
          <div style={{ marginTop: "1rem", display: "flex", alignItems: "center", gap: "1rem" }}>
            <button
              onClick={() => handleQuantityChange("decrease")}
              style={{
                backgroundColor: "#ddd",
                border: "none",
                padding: "0.5rem",
                cursor: "pointer",
              }}
            >
              -
            </button>
            <input
              type="number"
              value={quantity}
              min="1"
              onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
              style={{
                width: "50px",
                textAlign: "center",
                padding: "0.3rem",
                borderRadius: "4px",
                border: "1px solid #ddd",
                appearance: "none",
                MozAppearance: "textfield", // Firefox
                WebkitAppearance: "none", // WebKit 기반 브라우저
                outline: "none",
              }}
            />
            <button
              onClick={() => handleQuantityChange("increase")}
              style={{
                backgroundColor: "#ddd",
                border: "none",
                padding: "0.5rem",
                cursor: "pointer",
              }}
            >
              +
            </button>
          </div>
          <button
            onClick={handleAddToCart}
            style={{
              marginTop: "1rem",
              backgroundColor: "#4CAF50",
              color: "#fff",
              border: "none",
              padding: "0.5rem 1rem",
              cursor: "pointer",
              borderRadius: "4px",
            }}
          >
            장바구니에 추가
          </button>{" "}
          <button
            onClick={handleOrder}
            style={{
              marginTop: "1rem",
              backgroundColor: "#2200ffff",
              color: "#fff",
              border: "none",
              padding: "0.5rem 1rem",
              cursor: "pointer",
              borderRadius: "4px",
            }}
          >
            주문하기
          </button>
          <h3>리뷰</h3>
          {product.reviews && product.reviews.length > 0 ? ( // reviews가 정의된 경우만 렌더링
            <ul>
              {product.reviews.map((review) => (
                <li key={review.id}>
                  <strong>{review.reviewer}</strong> -
                  {" "}
                  ⭐ {review.rating}/5    
                  {" ("}
                  {new Date(review.reviewDate).toLocaleDateString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" })}
                  {")"}
                  <br />
                  {review.reviewText}
                </li>
              ))}
            </ul>
          ) : (
            <p>아직 등록된 리뷰가 없습니다.</p>
          )}
        </div>
      ) : (
        !loading && <p>상품 정보를 찾을 수 없습니다.</p>
      )}
    </div>
  );
}

export { ProductDetail };
