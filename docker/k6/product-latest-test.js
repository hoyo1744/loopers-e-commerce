import http from 'k6/http';
import { check, sleep } from 'k6';

// ===== 실행 옵션 (원하는 대로 조절) =====
export const options = {
  vus: 1,              // 동시 사용자 100명
  duration: '1m',       // 1분간 실행 (웹 대시보드를 위해 충분히 길게)
  // 실행 종료 요약에 어떤 통계를 보여줄지
  summaryTrendStats: ['min','avg','med','p(90)','p(95)','p(99)','p(99.9)','max'],
  thresholds: {
    http_req_failed: ['rate<0.01'],                 // 에러율 < 1%
    http_req_duration: ['p(99)<500'],               // p95 < 500ms
  },
};

// ===== 환경변수로 엔드포인트/파라미터 조절 =====
// 예) BASE_URL=http://localhost:8080  BRAND_ID=1  PAGE=0  SIZE=20
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const BRAND_ID = __ENV.BRAND_ID || '1';
const PAGE = __ENV.PAGE || '0';
const SIZE = __ENV.SIZE || '20';

// API 경로
const PATH = '/api/v1/products';

export default function () {
  const url = `${BASE_URL}${PATH}?brandId=${BRAND_ID}&sort=latest&page=${PAGE}&size=${SIZE}`;
  const res = http.get(url, { tags: { endpoint: 'products_latest' } });

  // HTTP 200 확인
  const ok = check(res, {
    'status is 200': (r) => r.status === 200,
    'json present': (r) => {
      try { JSON.parse(r.body); return true; } catch (_) { return false; }
    },
  });

  if (ok) {
    const body = JSON.parse(res.body);
    // 응답 구조에 맞게 경로 조정 (예: { data: { products: [...] } })
    const products = body?.data?.products ?? body?.data?.products?.products ?? body?.data?.products;
    // products가 배열인지 체크
    check(products, {
      'has products array': (p) => Array.isArray(p),
      [`has ${SIZE} items`]: (p) => Array.isArray(p) && p.length === Number(SIZE),
    });

    if (Array.isArray(products) && products.length > 0) {
      // createdAt 내림차순 정렬 검증
      const times = products.map((p) => new Date(p.createdAt).getTime());
      const sorted = [...times].sort((a, b) => b - a);
      check(null, {
        'createdAt desc sorted': () => JSON.stringify(times) === JSON.stringify(sorted),
      });
    }
  }

  sleep(1);
}

