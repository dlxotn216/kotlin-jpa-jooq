## 프로젝트 환경
JOOQ, JPA, Kotlin, Spring Boot 2.4.3  
데이터를 저장하는 부분은 JPA를 적극 활용하고 페이지네이션과 같은 복잡한 쿼리는 JOOQ로 하는 구조이다.  
JOOQ의 code generation은 db로 붙어서 하거나, Entity를 참조해서 하거나, Schema가 기재 된 sql을 보고 하거나 인데  
아무래도 JPA Entity에 맞는 SQL을 직접 관리하는 것이 필수라는 것을 이전 프로젝트에서 느꼈기 때문에 JOOQ도 해당 파일을 참조하는 것이 제일 좋다고 생각이 들었다.  
별도의 접속 계정관리를 어떻게 부여해 줄 지나 프로젝트가 CI/CD에서 빌드 될 때도 네트워크 환경에 따른 접속 문제가 벌써부터 예상 됐기 때문이다.  

다만 이렇게 구성을 하게 되면 인메모리 DB를 사용한 로컬에서 통합테스트는 좀 힘들지 않을까 싶었다. H2에 맞는 문법도 알아야 하니   
근데 진행 해 보니 JOOQ가 요구하는 테이블 생성 관련 스크립트에서 아래와 같은 구문은 파싱 에러가 났다. 특정 DBMS에 맞는 문법은 인식 못하는 듯..
```sql
CREATE OR REPLACE TABLE USR_USER
(
    USER_KEY BIGINT       NOT NULL PRIMARY KEY,
    USER_ID  VARCHAR(256) NOT NULL UNIQUE,
    EMAIL    VARCHAR(256) NOT NULL UNIQUE,
    NAME     VARCHAR(512) NOT NULL
) COLLATE = 'utf8mb4_general_ci'
  ENGINE = InnoDB;

CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1;
```   
따라서 결국 JOOQ가 코드 생성에 필요한 스크립트를 실제 DB Schema와 별도로 관리해야 한다는 것이다.  
그러면 뭐 H2는 로컬에서 잘 사용하고 실제 DB는 별도 파일로 잘 관리하면 되지 않을까 싶기도 하고.  

아니면 Docker로 실 DB와 동일한 Maria나 Postgre를 사용해보자 싶었다.  
오래남ㄴ에 설정 하려니 별 삽질이 다 있었다. 


### Docker로 설정 된 mariadb 적용하기
docker run -p 3306:3306  --name local-mariadb -e MYSQL_ROOT_PASSWORD="0216" -d mariadb --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --lower_case_table_names=1

IntelliJ 상에서 접속도 잘 됐고 아래와 같은 명령어로 터미널에서 접속도 잘 됐다.   
>  docker exec -it local-mariadb mysql -u root -p0216

그런데 계속 아래와 같은 오류 메시지가 나온다.  
>  Caused by: java.sql.SQLException: Access denied for user 'root'@'172.17.0.1' (using password: YES)
Current charset is UTF-8. If password has been set using other charset, consider using option 'passwordCharacterEncoding'

이 문제로 두시간 넘게 삽질을 한 것 같다.  

결론은 아래와 같은 설정에서 비밀번호 부분을 ""로 잘 감싸주어야 했다.  
url의 경우 datasource에도 기재 해주어야 아래와 같은 오류를 맞이하지 않을 수 있었다.
> If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
	If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active).
```yaml
spring:
  datasource:
    hikari:
      max-lifetime: 420000
      connection-timeout: 10000
      validation-timeout: 10000
      idle-timeout: 30000
      username: root
      password: "0216"    
      driver-class-name: org.mariadb.jdbc.Driver
      maximum-pool-size: 50
      jdbc-url: jdbc:mariadb://127.0.0.1:3306/DEMO
    url: jdbc:mariadb://127.0.0.1:3306/DEMO
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      org.hibernate.envers.audit_table_suffix: _HIS
      org.hibernate.envers.modified_flag_suffix: _CHANGED
      hibernate.jdbc.time_zone: UTC
      hibernate.format_sql: true
      hibernate.jdbc.batch_size: 100
      hibernate.jdbc.order_inserts: true
      hibernate.query.in_clause_parameter_padding: true
    open-in-view: false
server:
  port: 8090
```

근데 h2를 사용할 땐 서버가 내려가면 알아서 데이터들이 비워졌기에 좋았는데  
도커를 사용하자니 컨테이너를 지우고 다시 시작해야했다. 뭐 컨테이너가 내려갈 때 다 소멸시킬 수 있나 옵션이 있긴 할 것 같지만...

아무래도 local에선 h2를 잘 사용하고 JOOQ를 이용해서 자동 생성하기 위한 스크립트를 잘 관리해주면 될 것 같기도 하다.  

근데 JOOQ의 경우해서 빌드 후에 컬럼네임이 바뀐건 validation이 되진 않는다. JPA는 validate 단계가 있긴 한데..  
좀 더 찾아봐야겠다 이 부분은. 