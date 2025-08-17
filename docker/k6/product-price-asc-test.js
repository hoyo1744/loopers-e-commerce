import http from 'k6/http';
import { check, sleep } from 'k6';

// ===== 실행 옵션 =====
export const options = {
  vus: 1,              // 동시 사용자 수
  duration: '1m',        // 실행 시간
  // 실행 종료 요약에 어떤 통계를 보여줄지
  summaryTrendStats: ['min','avg','med','p(90)','p(95)','p(99)','p(99.9)','max'],
  thresholds: {
    http_req_failed: ['rate<0.01'],       // 실패율 1% 미만
    http_req_duration: ['p(99)<500'],     // 95% 요청이 500ms 미만
  },
};

// ===== 환경변수 =====
// 예: BASE_URL=http://localhost:8080 BRAND_ID=1 PAGE=0 SIZE=20
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const BRAND_ID = __ENV.BRAND_ID || '1';
const PAGE = __ENV.PAGE || '0';
const SIZE = __ENV.SIZE || '20';

// API 경로
const PATH = '/api/v1/products';

export default function () {
  const url = `${BASE_URL}${PATH}?brandId=${BRAND_ID}&sort=price_asc&page=${PAGE}&size=${SIZE}`;
  const res = http.get(url, { tags: { endpoint: 'products_price_asc' } });

  // 응답 상태 코드와 JSON 파싱 가능 여부 체크
  const ok = check(res, {
    'status is 200': (r) => r.status === 200,
    'json present': (r) => {
      try { JSON.parse(r.body); return true; } catch (_) { return false; }
    },
  });

  if (ok) {
    const body = JSON.parse(res.body);
    const products = body?.data?.products ?? body?.data?.products?.products ?? body?.data?.products;

    // 배열 여부 및 개수 체크
    check(products, {
      'has products array': (p) => Array.isArray(p),
      [`has ${SIZE} items`]: (p) => Array.isArray(p) && p.length === Number(SIZE),
    });

    if (Array.isArray(products) && products.length > 0) {
      // price 오름차순 정렬 검증
      const prices = products.map((p) => p.price ?? 0);
      const sorted = [...prices].sort((a, b) => a - b);

      check(null, {
        'price asc sorted': () => JSON.stringify(prices) === JSON.stringify(sorted),
      });
    }
  }

  sleep(1);
}

