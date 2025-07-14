import React from 'react';
import { useNavigate } from 'react-router-dom';

function Main() {
  const navigate = useNavigate();

  const goToProducts = () => {
    navigate('/products');
  };

  return (
    <section style={{ padding: '2rem' }}>
      <h2>쇼핑몰 프로젝트 메인</h2>

      <button 
        onClick={goToProducts} 
        style={{ margin: '1rem 0', padding: '0.5rem 1rem', cursor: 'pointer' }}
      >
        상품 목록 보기
      </button>
    </section>
  );
}

export default Main;
