
# 인덱스를 이용한 성능 개선

## 좋아요 순 상품 목록 조회

**쿼리**
```sql
select p1_0.id,p1_0.name,p1_0.price,p1_0.created_at,p1_0.brand_id,b1_0.name,count(l1_0.like_id)
from product p1_0
         join brand b1_0 on p1_0.brand_id=b1_0.brand_id
         left join likes l1_0 on l1_0.product_id=p1_0.id
group by p1_0.id,p1_0.name,p1_0.price,p1_0.created_at,p1_0.brand_id,b1_0.name 
order by count(l1_0.like_id) 
desc limit 0, 20
```

**실행 시간**
```
[2025-08-15 00:13:13] 20 rows retrieved starting from 1 in 59 s 726 ms (execution: 59 s 334 ms, fetching: 392 ms)
```

**실행 계획**

```
-> Limit: 20 row(s)  (actual time=60729..60729 rows=20 loops=1)
    -> Sort: `count(l1_0.like_id)` DESC, limit input to 20 row(s) per chunk  (actual time=60729..60729 rows=20 loops=1)
        -> Table scan on <temporary>  (actual time=60540..60681 rows=1e+6 loops=1)
            -> Aggregate using temporary table  (actual time=60540..60540 rows=1e+6 loops=1)
                -> Left hash join (l1_0.product_id = p1_0.id)  (cost=932e+9 rows=9.32e+12) (actual time=3323..7142 rows=23.5e+6 loops=1)
                    -> Inner hash join (p1_0.brand_id = b1_0.brand_id)  (cost=403270 rows=397898) (actual time=0.189..342 rows=1e+6 loops=1)
                        -> Table scan on p1_0  (cost=3829 rows=994746) (actual time=0.092..276 rows=1e+6 loops=1)
                        -> Hash
                            -> Table scan on b1_0  (cost=1.4 rows=4) (actual time=0.078..0.0792 rows=4 loops=1)
                    -> Hash
                        -> Covering index scan on l1_0 using uk_likes_user_product  (cost=587 rows=23.4e+6) (actual time=1.63..2029 rows=23.5e+6 loops=1)

```

**실행 계획 분석**
- product p1_0와 brand b1_0 스캔시, 인덱스가 사용되지 않음   --> product.brand_id 인덱스 추가 -> 옵티마이저가 Hash Join 대신 Index Nested Loop Join을 사용하도록 유도
- Inner hash join은 조인 전략 중 1가지에며, 적절한 인덱스가 없으니 옵티마이저가 해시로 메모리 조인을 시도한것
- Covering index scan on l1_0 using uk_likes_user_product: liks 테이블의 유니크키는 user_id, product_id이고 조인키는 like.product_id = product.product_id 이기 때문에 
user_id가 없어 product_id 기준으로 탐색 불가 -> 따라서 모든 범위 인덱스 스캔을 진행
--> likes.product_id 인덱스 또는 likes(product_id, userId) 인덱스 추가
- Left hash join (l1_0.product_id = p1_0.id)  : likes와 product가 해시 조인을 수행한 결과가 2350만 행 조회
- Aggregate using temporary table : 조인결과가 크고 정렬/집계에 맞는 인덱스가 없어 임시 테이블로 집계, 60초 사용
- Sort: `count(l1_0.like_id)` DESC, : 집계 이후 임시테이블 대상으로 정렬 시도
- Group By 수행시 인덱스 순서대로 처리하지 못하면 MySQL은 임세 테이블 + filesort로 집계/정렬 수행
- ORDER BY COUNT(*) DESC 는 그룹 수가 확정된 뒤에야 정렬할 수 있어, 집계 → 그 결과를 다시 정렬의 두 단계를 반드시 거침.

```
-> Sort: `count(l1_0.like_id)` DESC, limit input to 20 row(s) per chunk  (actual time=60729..60729 rows=20 loops=1)
        -> Table scan on <temporary>  (actual time=60540..60681 rows=1e+6 loops=1)
            -> Aggregate using temporary table  (actual time=60540..60540 rows=1e+6 loops=1)
                -> Left hash join (l1_0.product_id = p1_0.id)  (cost=932e+9 rows=9.32e+12) (actual time=3323..7142 rows=23.5e+6 loops=1)
                    -> Inner hash join (p1_0.brand_id = b1_0.brand_id)  (cost=403270 rows=397898) (actual time=0.189..342 rows=1e+6 loops=1)
                        -> Table scan on p1_0  (cost=3829 rows=994746) (actual time=0.092..276 rows=1e+6 loops=1)
                        -> Hash
                            -> Table scan on b1_0  (cost=1.4 rows=4) (actual time=0.078..0.0792 rows=4 loops=1)
                    -> Hash
                        -> Covering index scan on l1_0 using uk_likes_user_product  (cost=587 rows=23.4e+6) (actual time=1.63..2029 rows=23.5e+6 loops=1)
```

**병목 지점**
- 집계 함수에서 임시 테이블 생성
- 임시 테이블 대상의 스캔 및 정렬

**개선 포인트**
- 집계 전 데이터양 축소(선 브랜드 필터)
- 인덱스 추가
```sql
CREATE INDEX idx_product_brand_id ON product(brand_id);
CREATE INDEX idx_likes_product_id ON likes(product_id);
```
- 테이블 비정규화로 집계 함수 사용 제거
- view 테이블 사용


**개선 결과**
- likes.product_id 인덱스 추가로 Nested loop left join으로 변경됨
- Covering index lookup on l1_0 using idx_likes_product_id (product_id=p1_0.id) : likes.product_id 인덱스 사용으로 전 범위 스캔이 아닌 인덱스 탐색을 수행함 
- 집계함수 컬럼이 인덱스와 일치하지 않아 집계 함수에서 병목이 심함
```
-> Limit: 20 row(s)  (actual time=56704..56704 rows=20 loops=1)
    -> Sort: `count(l1_0.like_id)` DESC, limit input to 20 row(s) per chunk  (actual time=56704..56704 rows=20 loops=1)
        -> Table scan on <temporary>  (actual time=56495..56655 rows=1e+6 loops=1)
            -> Aggregate using temporary table  (actual time=56495..56495 rows=1e+6 loops=1)
                -> Nested loop left join  (cost=3.91e+6 rows=23.6e+6) (actual time=279..4694 rows=23.5e+6 loops=1)
                    -> Inner hash join (b1_0.brand_id = p1_0.brand_id)  (cost=506665 rows=994746) (actual time=278..425 rows=1e+6 loops=1)
                        -> Table scan on b1_0  (cost=0.00403 rows=4) (actual time=0.058..0.0652 rows=4 loops=1)
                        -> Hash
                            -> Table scan on p1_0  (cost=104762 rows=994746) (actual time=0.989..200 rows=1e+6 loops=1)
                    -> Covering index lookup on l1_0 using idx_likes_product_id (product_id=p1_0.id)  (cost=1.05 rows=23.7) (actual time=0.00211..0.00345 rows=23.5 loops=1e+6)

```

**쿼리 구조 개선**
- 집계 함수에서 병목이 심하기 떄문에 서브쿼리로 집계 전용 쿼리와 조회 쿼리 분리 후 사용
- 쿼리 실행 결과 1.7초
>20 rows retrieved starting from 1 in 2 s 63 ms (execution: 1 s 729 ms, fetching: 334 ms)
- 정렬 및 검색 조건 마다 쿼리가 달라질수 있어 구현이 까다롭고 복잡함
```
SELECT p.*, b.name, agg.cnt
FROM (
         SELECT product_id, COUNT(*) AS cnt
         FROM likes
         GROUP BY product_id
         ORDER BY cnt DESC
         LIMIT 20
     ) agg
         JOIN product p ON p.id = agg.product_id
         JOIN brand b ON b.brand_id = p.brand_id;

```

**결론**
- 인덱스 추가 전 : 58초 -> 인덱스 추가후 : 56초로 집계 함수 병목이 커 큰 성능 향상은 없었습니다.
- 집계 함수 병목을 제거하기 위해서 쿼리 구조 변경을 했을때 1.7초까지 단축되었습니다.
- 쿼리 구조 변경시 정렬 및 필터 조건마다 쿼리가 달라질 수 있어 구현의 복잡성이 높아집니다.

---

## 가격 순 상품 목록 조회

**쿼리**
```sql
select p1_0.id,p1_0.name,p1_0.price,p1_0.created_at,p1_0.brand_id,b1_0.name,count(l1_0.like_id)
from product p1_0
         join brand b1_0 on p1_0.brand_id=b1_0.brand_id
         left join likes l1_0 on l1_0.product_id=p1_0.id
group by p1_0.id,p1_0.name,p1_0.price,p1_0.created_at,p1_0.brand_id,b1_0.name
order by p1_0.price limit 0,20
```

**실행 시간**
```
 20 rows retrieved starting from 1 in 1 m 0 s 807 ms (execution: 1 m 0 s 441 ms, fetching: 366 ms)
```
**실행 계획**
```
-> Limit: 20 row(s)  (actual time=62385..62385 rows=20 loops=1)
    -> Sort: p1_0.price, limit input to 20 row(s) per chunk  (actual time=62385..62385 rows=20 loops=1)
        -> Table scan on <temporary>  (actual time=62184..62334 rows=1e+6 loops=1)
            -> Aggregate using temporary table  (actual time=62184..62184 rows=1e+6 loops=1)
                -> Left hash join (l1_0.product_id = p1_0.id)  (cost=932e+9 rows=9.32e+12) (actual time=3501..7352 rows=23.5e+6 loops=1)
                    -> Inner hash join (p1_0.brand_id = b1_0.brand_id)  (cost=403270 rows=397898) (actual time=0.225..245 rows=1e+6 loops=1)
                        -> Table scan on p1_0  (cost=3829 rows=994746) (actual time=0.091..181 rows=1e+6 loops=1)
                        -> Hash
                            -> Table scan on b1_0  (cost=1.4 rows=4) (actual time=0.0805..0.0826 rows=4 loops=1)
                    -> Hash
                        -> Covering index scan on l1_0 using uk_likes_user_product  (cost=587 rows=23.4e+6) (actual time=1.3..2152 rows=23.5e+6 loops=1)

```

**실행 계획 분석**
- 좋아요 순 상품 목록 조회와 동일한 실행 계획으로 보이며 집계 함수에서 가장 큰 병목 현상이 나타났습니다.

**병목 지점**
- 집계 함수에서 60초 사용

**개선 포인트**
- 인덱스로는 한계가 있으며, 쿼리 구조 개선 또는 비정규화가 필요

**결론**
- 인덱스 추가 전 : 60초 -> 인덱스 추가후 : 56초로 집계 함수 병목이 커 큰 성능 향상은 없었습니다.
- 쿼리 구조 변경시 정렬 및 필터 조건마다 쿼리가 달라질 수 있어 구현의 복잡성이 높아집니다.

---

### 최신순 상품 목록 조회

**쿼리**
```sql
select p1_0.id,p1_0.name,p1_0.price,p1_0.created_at,p1_0.brand_id,b1_0.name,count(l1_0.like_id)
from product p1_0
         join brand b1_0 on p1_0.brand_id=b1_0.brand_id
         left join likes l1_0 on l1_0.product_id=p1_0.id
group by p1_0.id,p1_0.name,p1_0.price,p1_0.created_at,p1_0.brand_id,b1_0.name
order by p1_0.created_at desc limit 0,20
```

**실행 시간**
```
1 row retrieved starting from 1 in 59 s 12 ms (execution: 58 s 674 ms, fetching: 338 ms)
```
**실행 계획**
```
-> Limit: 20 row(s)  (actual time=58649..58649 rows=20 loops=1)
    -> Sort: p1_0.created_at DESC, limit input to 20 row(s) per chunk  (actual time=58649..58649 rows=20 loops=1)
        -> Table scan on <temporary>  (actual time=58436..58595 rows=1e+6 loops=1)
            -> Aggregate using temporary table  (actual time=58436..58436 rows=1e+6 loops=1)
                -> Nested loop left join  (cost=3.9e+6 rows=23.5e+6) (actual time=284..5025 rows=23.5e+6 loops=1)
                    -> Inner hash join (b1_0.brand_id = p1_0.brand_id)  (cost=506665 rows=994746) (actual time=284..451 rows=1e+6 loops=1)
                        -> Table scan on b1_0  (cost=0.00403 rows=4) (actual time=0.0566..0.0653 rows=4 loops=1)
                        -> Hash
                            -> Table scan on p1_0  (cost=104762 rows=994746) (actual time=0.423..204 rows=1e+6 loops=1)
                    -> Covering index lookup on l1_0 using idx_likes_product_id (product_id=p1_0.id)  (cost=1.05 rows=23.6) (actual time=0.00235..0.00373 rows=23.5 loops=1e+6)

```

**실행 계획 분석**
- 좋아요 순 상품 목록 조회와 동일한 실행 계획으로 보이며 집계 함수에서 가장 큰 병목 현상이 나타났습니다.

**병목 지점**
- 집계 함수에서 58초 사용

**개선 포인트**
- 인덱스로는 한계가 있으며, 쿼리 구조 개선 또는 비정규화가 필요

**결론**
- 인덱스 추가 전 : 60초 -> 인덱스 추가후 : 56초로 집계 함수 병목이 커 큰 성능 향상은 없었습니다.
- 쿼리 구조 변경시 정렬 및 필터 조건마다 쿼리가 달라질 수 있어 구현의 복잡성이 높아집니다.

## 결과
전반적으로 인덱스 추가 여부와 관계 없이 뚜렷한 성능 향상을 보이지 않았습니다.
그 이유는 병목의 주된 원인은 집계 함수에서 발생되었기 때문입니다.
집계 함수 결과가 너무 크고, group by 절에 나열된 모든 컬럼에 대해 인덱스를 순서대로 걸어줄 수 없기 때문에 
MV, 반정규화 방법으로 성능 개선을 시도합니다.

