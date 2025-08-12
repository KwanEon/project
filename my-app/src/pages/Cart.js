import React, { useEffect, useState, useRef, useContext } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import { AuthContext } from "../contexts/AuthContext";

function Cart() {
  const { userRole, loading } = useContext(AuthContext);
  const [cartItems, setCartItems] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const hasRedirected = useRef(false);

  useEffect(() => {
    if (loading) return;

    if (!hasRedirected.current) {
      hasRedirected.current = true;
      if (userRole === "ANONYMOUS") {
        alert("로그인이 필요합니다.");
        navigate("/login", { replace: true });
        return;
      }
    }

    // 로그인 상태일 때만 장바구니 조회
    if (userRole !== "ANONYMOUS") {
      axios
        .get(`${process.env.REACT_APP_API_BASE_URL}/cartitem`, { withCredentials: true })
        .then((response) => setCartItems(response.data))
        .catch((err) => {
          setError("장바구니를 불러오는 중 오류가 발생했습니다.");
          console.error(err);
        });
    }
  }, [userRole, navigate, loading]);

  if (userRole === "ANONYMOUS") {
    return null;
  }

  const handleQuantityChange = (cartId, operation) => {
    axios
      .patch(`${process.env.REACT_APP_API_BASE_URL}/cartitem/${cartId}?operation=${operation}`, {}, { withCredentials: true })
      .then(() => {
        setCartItems((prevItems) =>
          prevItems.map((item) =>
            item.id === cartId
              ? {
                  ...item,
                  quantity: operation === "increase" ? item.quantity + 1 : item.quantity - 1,
                }
              : item
          )
        );
      })
      .catch((err) => {
        setError("수량 변경 중 오류가 발생했습니다.");
        console.error(err);
      });
  };

  const handleRemoveItem = (productId) => {
    axios
      .delete(`${process.env.REACT_APP_API_BASE_URL}/cartitem/${productId}`, { withCredentials: true })
      .then(() => {
        setCartItems((prevItems) =>   // 현재 상태(cartItems)를 prevItems라는 이름으로 받아서
          prevItems.filter(
            (item) => item.productId !== productId));   // 이 조건을 만족하는 것만 남김
      })
      .catch((err) => {
        setError("상품 삭제 중 오류가 발생했습니다.");
        console.error(err);
      });
  };

  const handleClearCart = () => {
    axios
      .delete(`${process.env.REACT_APP_API_BASE_URL}/cartitem`, { withCredentials: true })
      .then(() => setCartItems([]))
      .catch((err) => {
        setError("장바구니를 비우는 중 오류가 발생했습니다.");
        console.error(err);
      });
  };

  const handleCheckout = () => {
    navigate("/order/cartitem");
  };

  return (
    <div>
      <h2>장바구니</h2>
      {cartItems.length === 0 ? (
        <p style={{ padding: "1rem", textAlign: "center" }}>장바구니가 비어 있습니다.</p>
      ) : (
        <>
          {cartItems.map((item) => (
            <div
              key={item.id}
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                marginBottom: "1rem",
                padding: "1rem",
                border: "1px solid #ddd",
                borderRadius: "8px",
              }}
            >
              <div>
                <h4>
                  <Link
                    to={`/products/${item.productId}`} // product.id → productId
                    style={{
                      textDecoration: "none",
                      color: "blue",
                      cursor: "pointer",
                    }}
                  >
                    {item.productName} {/* product.name → productName */}
                  </Link>
                </h4>
                <p>총 가격: {item.productPrice * item.quantity}원</p> {/* product.price → productPrice */}
              </div>
              <div>
                <button
                  onClick={() => handleQuantityChange(item.id, "decrease")}
                  style={{ marginRight: "0.5rem" }}
                  disabled={item.quantity <= 1}
                >
                  -
                </button>
                <span>{item.quantity}</span>
                <button
                  onClick={() => handleQuantityChange(item.id, "increase")}
                  style={{ marginLeft: "0.5rem" }}
                >
                  +
                </button>
                <button
                  onClick={() => handleRemoveItem(item.productId)} // product.id → productId
                  style={{
                    marginLeft: "1rem",
                    backgroundColor: "#ff4d4f",
                    color: "#fff",
                    border: "none",
                    padding: "0.5rem 1rem",
                    cursor: "pointer",
                  }}
                >
                  삭제
                </button>
              </div>
            </div>
          ))}


          <button
            onClick={handleClearCart}
            style={{
              marginTop: "1rem",
              backgroundColor: "#ff5722",
              color: "#fff",
              border: "none",
              padding: "0.7rem 2rem",
              cursor: "pointer",
              borderRadius: "4px",
            }}
          >
            장바구니 비우기
          </button>
          <button
            onClick={handleCheckout}
            style={{
              marginTop: "1rem",
              marginLeft: "1rem",
              backgroundColor: "#4CAF50",
              color: "#fff",
              border: "none",
              padding: "0.7rem 2rem",
              cursor: "pointer",
              borderRadius: "4px",
            }}
          >
            주문하기
          </button>
        </>
      )}
    </div>
  );
}

export default Cart;

