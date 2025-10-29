import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";

function ProductDetail() {
  const { productId } = useParams();
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [page, setPage] = useState(0);  // 현재 리뷰 페이지 번호
  const [size, setSize] = useState(5);  // 한 페이지당 리뷰 개수
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // 상품 상세 + 리뷰 동시 조회
  useEffect(() => {
    fetchProduct(page, size);
  }, [productId, page, size]);

  const fetchProduct = async (pageToFetch = 0, pageSize = size) => {
    setLoading(true);
    setError("");
    try {
      const res = await axios.get(`http://localhost:8080/products/${productId}`, {
        params: { page: pageToFetch, size: pageSize },
        withCredentials: true,
      });

      const data = res.data;
      setProduct(data);
    } catch (e) {
      console.error(e);
      setError("상품 정보를 불러오는 중 오류가 발생했습니다.");
    }
    setLoading(false);
  };

  const handleQuantityChange = (operation) => {
    setQuantity((prevQuantity) => {
      const newQuantity =
        operation === "increase" ? prevQuantity + 1 : prevQuantity - 1;
      return newQuantity > 0 ? newQuantity : 1;
    });
  };

  const handleAddToCart = () => {
    axios
      .post(
        `http://localhost:8080/cartitem/${productId}?quantity=${quantity}`,
        {},
        { withCredentials: true }
      )
      .then(() => {
        alert("장바구니에 추가되었습니다.");
      })
      .catch((err) => {
        if (err.response && err.response.status === 401) {
          alert("로그인이 필요합니다.");
          navigate("/login");
          return;
        }
        console.error("장바구니 추가 중 오류:", err);
        alert("장바구니에 추가하는 중 오류가 발생했습니다.");
      });
  };

  const handleOrder = () => {
    navigate(`/order/${productId}?quantity=${quantity}`);
  };

  const goToPage = (p) => {
    if (p < 0) return;
    setPage(p);
  };

  return (
    <div>
      {loading && <p>로딩 중...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {product ? (
        <div>
          <h2>{product.name}</h2>

          {product.imageUrl && (
            <div style={{ margin: "1rem 0" }}>
              <img
                src={
                  product.imageUrl.startsWith("http")
                    ? product.imageUrl
                    : `http://localhost:8080${product.imageUrl}`
                }
                alt={product.name}
                style={{
                  maxWidth: "300px",
                  borderRadius: "8px",
                  border: "1px solid #ccc",
                }}
              />
            </div>
          )}

          <p>설명: {product.description}</p>
          <p>가격: ₩{product.price}</p>

          <div
            style={{
              marginTop: "1rem",
              display: "flex",
              alignItems: "center",
              gap: "1rem",
            }}
          >
            <button
              onClick={() => handleQuantityChange("decrease")}
              style={{ backgroundColor: "#ddd", border: "none", padding: "0.5rem", cursor: "pointer" }}
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
              }}
            />
            <button
              onClick={() => handleQuantityChange("increase")}
              style={{ backgroundColor: "#ddd", border: "none", padding: "0.5rem", cursor: "pointer" }}
            >
              +
            </button>
          </div>

          <div style={{ marginTop: "1rem" }}>
            <button
              onClick={handleAddToCart}
              style={{
                marginRight: "8px",
                backgroundColor: "#4CAF50",
                color: "#fff",
                border: "none",
                padding: "0.5rem 1rem",
                cursor: "pointer",
                borderRadius: "4px",
              }}
            >
              장바구니에 추가
            </button>
            <button
              onClick={handleOrder}
              style={{
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
          </div>

          <h3 style={{ marginTop: "2rem" }}>리뷰</h3>

          {product.reviews && product.reviews.content && product.reviews.content.length > 0 ? (
            <>
              <ul>
                {product.reviews.content.slice().reverse().map((review) => (
                  <li key={review.id} style={{ marginBottom: "1rem" }}>
                    <strong>{review.reviewer}</strong> - ⭐ {review.rating}/5{" "}
                    ({new Date(review.reviewDate).toLocaleDateString("ko-KR")})
                    <br />
                    {review.reviewText}
                  </li>
                ))}
              </ul>

              {product.reviews.totalPages > 1 && (
                <div style={{ marginTop: "1rem" }}>
                  {[...Array(product.reviews.totalPages)].map((_, idx) => (
                    <button
                      key={idx}
                      onClick={() => goToPage(idx)}
                      disabled={idx === page}
                      style={{
                        margin: "0 0.25rem",
                        fontWeight: idx === page ? "bold" : "normal",
                        padding: "0.3rem 0.6rem",
                        border: "1px solid #ccc",
                        borderRadius: "4px",
                        backgroundColor: idx === page ? "#ddd" : "white",
                        cursor: "pointer",
                      }}
                    >
                      {idx + 1}
                    </button>
                  ))}
                </div>
              )}
            </>
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
