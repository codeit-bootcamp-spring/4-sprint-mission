# sprint-mission-4
## 1. @RequestBody

- 역할
    - HTTP 요청의 body 데이터를 자바 객체로 변환할 대 사용
- POST, PUT, PATCH 등 body가 있는 요청에 주로 사용
- 특징
    - body에 담긴 JSON, XML, plain text 등 다양한 포맷을 객체로 변환
    - 파라미터 1개에만 사용 가능
    - 기본 생성자 필요
    - @Valid와 함께 사용 가능 (입력값 검증)
- 비교
    - @RequestParam : 쿼리스트링 / 폼 데이터
    - @RequestBody : body 데이터
- 사용법
    
    ```java
    @PostMapping("/api")
    public void method(@RequestBody MyDto dto) {
        // dto에 body 데이터가 매핑됨
    }
    ```
    

## 2. @ModelAttribute

- 역할
    - 쿼리 파라미터, form-data 등 요청 값을 자바 객체(DTO)로 자동 매핑
- GET 요청, 폼 전송(POST) 등에서 여러 파라미터를 객체로 받을 때 주로 사용
- 특징
    - k-v 형태의 요청 데이터를 객체 필드에 자동 할당
    - 파라미터가 많을 때 코드 간결화에 유리
    - 기본 생성자 필요
- 사용법
    
    ```java
    @GetMapping("/search")
    public String search(@ModelAttribute SearchDto dto) {
        // /search?keyword=abc&sort=desc → dto에 값 자동 매핑
    }
    ```
