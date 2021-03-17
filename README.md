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

### Mockito를 통해 Kotlin class를 spy, mock 할 수 없는 경우
코틀린의 기본 class는 모두 final이나 Mockito는 상속 가능한 기본 생성자를 가진 클래스를 요구한다.  
이 때 /src/test/resources 디렉터리 하위에 /mockito-extensions 디렉터리를 생성하고  
org.mockito.plugins.MockMaker 파일에 mock-maker-inline 한 줄 적어주면 spy, mock를 통해 생성이 가능해진다.    


### JPA에서 연관 Entity에 대해 손쉽게 수정하기
만약 아래와 같이 Study와 User의 관계를 담당하는 StudyUser 엔티티가 Set으로 정의가 되어있다면
```kotlin
@OneToMany(fetch = FetchType.LAZY, mappedBy = "study", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
val studyUsers: MutableSet<StudyUser> = mutableSetOf()

class StudyUser(
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "USER_KEY")
    val user: User,

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "STUDY_KEY")
    val study: Study
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Study
        return id == other.id
    }

    override fun hashCode() = id.hashCode()}
```

연관관계에 있는 Entity에 대한 추가 및 삭제(Soft delete)를 단순히 Set에 add를 하는 것으로 손쉽게 해결 할 수 있을 것 같다.  
```kotlin
// (before)
val before = mutableSetOf(
    StudyUser(study = 1, user = 1, deleted = false, reason = "Initial Input"),
    StudyUser(study = 1, user = 2, deleted = false, reason = "Initial Input")
)

/*
// request
{
    name: "Updated study",
    studyUsers: [
        {study: 1, user: 1, deleted: false},
        {study: 1, user: 2, deleted: true},
        {study: 1, user: 3, deleted: false}
    ],
    reason: "Delete user 2 and add User 3"
}
*/

// (after)
val after = mutableSetOf(
    StudyUser(study = 1, user = 1, deleted = false, reason = "Initial Input"),
    StudyUser(study = 1, user = 2, deleted = true, reason = "Delete user 2 and add User 3"),
    StudyUser(study = 1, user = 3, deleted = false, reason = "Delete user 2 and add User 3")
)
```

#### 실패 1
Study에선 아래와 같이 단순 add 연산을 했었다.  
```kotlin
fun addStudyUser(studyUser: StudyUser) {
    studyUsers.add(studyUser)
}
```
아래와 같은 테스트도 잘 통과 했다.  
```kotlin
@Test
fun `should update when deleted study users added`() {
// given
val study = Study(1L, "TEST_STD", "Test study")
val studyUser1 = StudyUser(User(1L, "taesu1", "taesu1@crscube.co.kr", "Taesu1"), study)
val studyUser2 = StudyUser(User(2L, "taesu2", "taesu2@crscube.co.kr", "Taesu2"), study)
study.addStudyUser(studyUser1)
study.addStudyUser(studyUser2)
val retrievedUser1 = StudyUser(User(2L, "taesu2", "taesu2@crscube.co.kr", "Taesu2"), study, studyUser1.audit)
val retrievedUser2 = StudyUser(User(1L, "taesu1", "taesu1@crscube.co.kr", "Taesu1"), study, studyUser2.audit)

// when
retrievedUser1.delete("delete user")
retrievedUser2.delete("delete user")
study.addStudyUser(retrievedUser1)
study.addStudyUser(retrievedUser2)

// then
assertThat(study.studyUsers.all { it.deleted }).isTrue
assertThat(study.studyUsers.all { it.reason == "delete user" }).isTrue
assertThat(study.studyUsers.size).isEqualTo(2)
}
```

하지만 위 테스트는 문제가 있다. Set은 중복 된 키가 있으면 업데이트하지 않고 무시한다. 그렇기에 아래 연산은 무시된다.    
```kotlin
study.addStudyUser(retrievedUser1)
study.addStudyUser(retrievedUser2)
```  
그럼 결과는 어떻게 검증이 됐을까?  
기존의 studyUser1, studyUser2의 audit 객체를 retrievedUser1, retrievedUser2 그대로 넣기 때문에    
then에서 검증한 대로 "delete user"라는 변경사유를 통해 삭제된 Object가 잘 들어간 듯 보이기만한다.  
실제 set안에 변경 된 object가 들어간게 아니다.        
   

따라서 아래와 같이 addStudyUser의 메서드를 수정하고 
```kotlin
fun addStudyUser(studyUser: StudyUser) {
    if (!studyUsers.add(studyUser)) {
        studyUsers.remove(studyUser)
        studyUsers.add(studyUser)
    }
}
```
아래와 같이 테스트도 보완한다.
```kotlin
val retrievedUser1 = StudyUser(User(2L, "taesu2", "taesu2@crscube.co.kr", "Taesu2"), study, Audit(
        deleted = studyUser1.audit.deleted,
        reason = studyUser1.audit.reason
))
val retrievedUser2 = StudyUser(User(1L, "taesu1", "taesu1@crscube.co.kr", "Taesu1"), study, Audit(
        deleted = studyUser1.audit.deleted,
        reason = studyUser1.audit.reason
))
```

#### 실패 2
자바의 자료구조 상으론 업데이트가 잘 됐다. JPA에선 문제가 없을까?  
실제로는 아래와 같엔 예외가 떨어진다.  
> EntityExistsException: A different object with the same identifier value

분면 CascadeType을 MERGE도 설정 해줬으나 이런 문제가 생긴다.  
기존 로직에선 Transactional내에서 조회한 Entity를 별도로 save 해주지 않았는데 거기서 문제가 생긴 것 같다.
AbstractSaveEventListener의 아래 로직에서 나는데 IdentityColumn을 사용하면 에러가 안나긴 한다.  
다만 userKey, studyKey가 unique 조건을 맺어야 하기에... 역시 이후에 DuplicatedKey 에러가 날 것이다.  
```java
if ( !useIdentityColumn ) {
    key = source.generateEntityKey( id, persister );
    final PersistenceContext persistenceContext = source.getPersistenceContextInternal();
    Object old = persistenceContext.getEntity( key );
    if ( old != null ) {
        if ( persistenceContext.getEntry( old ).getStatus() == Status.DELETED ) {
            source.forceFlush( persistenceContext.getEntry( old ) );
        }
        else {
            throw new NonUniqueObjectException( id, persister.getEntityName() );
        }
    }
    persister.setIdentifier( entity, id, source );
}
```
아래와 같이 save를 명시적으로 호출 해주었다.  
save 내에선 new 여부에 따라 persist or merge를 호출 해 준다.  
```java
studyRepository.save(study)

@Transactional
public <S extends T> S save(S entity) {
    Assert.notNull(entity, "Entity must not be null.");
    if (this.entityInformation.isNew(entity)) {
        this.em.persist(entity);
        return entity;
    } else {
        return this.em.merge(entity);
    }
}
```

#### 실패 3
저장이 잘 됐으니 끝났을까 싶지만 특정 비즈니스를 만족시키지 못하는 경우가 있다.  
Audit의 특성 상 추적이 필요한 필드에 데이터의 변경이 있어야만 쌓여야 한다. Enver를 사용하기에 추적이 필요한 컬럼외의 reason만 바뀌어도 revision이 쌓인다...  

예를 들어 아래와 같은 상황에선 추적이 필요한 필드(deleted)가 안바뀌었기에 revision이 쌓이면 안된다.  
```kotlin
// before
StudyUser(study = 1, user = 1, deleted = false, reason = "initial")
// request
/*
{
  study: 1, 
  user: 1,
  delete: false,
  reason: "change"
}
*/
// after
StudyUser(study = 1, user = 1, deleted = false, reason = "initial")
``` 

결국 요청이 들어온 데이터를 기반으로 해서 기존에 저장된 Entity를 조회하고  
삭제 됐다면 deleted, reason을 바꿔주고 그렇지 안하면 변경하지 않고  
새로운 등록 요청이면 객체를 생성해서 추가해주는 그런 처리가 결과적으로 필요하다.  

Hibernate entity의 equals and hashcode를 오버라이딩 해서 Proxy 객체와의 동등성을 맞춰주는 것이 좋다 하는  
글을 봐서 몇몇 시도를 해보았는데... 손 안대고 코풀기는 힘들 것 같다.     


### JOOQ 만 사용해보기
Entity를 kotlin data class로 선언하고 변경에 대한 비교 기준이 되는 필드만 constructor에 선언해주면  
실제 변경사항이 있을 때 업데이트 치는 로직을 구현하기 쉽지 않을까 싶었다.    
```kotlin
data class RoleEntity(
    var key: Long = -1L,
    val id: String,
    var name: String,               // 변경 감지 대상 필드
    var deleted: Boolean = false    // 변경 감지 대상 필드
) {
    var audit: Audit = Audit()
    val reason: String get() = audit.reason
    val createdBy: Long get() = audit.createdBy
    val createdAt: LocalDateTime get() = audit.createdAt
    val modifiedBy: Long get() = audit.modifiedBy
    val modifiedAt: LocalDateTime get() = audit.modifiedAt
}
```
이러면 name, deleted외의 변경사항이 있어도 equal 비교에서 같음으로 나타나니 말이다.  
물론 컬렉션 연산에서 문제가 당연 생길 수 있을 것이기도 하다.  

아래와 같이 Repository를 구성하
```kotlin
@Component
class RoleQuery(val dslContext: DSLContext) {

    @Transactional
    fun save(role: RoleEntity): RoleEntity {
        return if (role.key <= -1L) {
            create(role)
        } else {
            update(role)
        }
    }

    @Transactional
    fun create(role: RoleEntity): RoleEntity {
        val nextVal = ROLE_SEQ.nextval()
        val roleKey = dslContext.select(nextVal).fetchOne(nextVal)
        role.key = roleKey!!

        return role.apply {
            // case 3
            dslContext.batchInsert(toRecord(roleKey, this), toHistoryRecord(roleKey, this)).execute()
        }
    }
    /*
    // case 1
    // val MR = MST_ROLE
    // dslContext.insertInto(MR)
    //         .set(MR.ROLE_KEY, roleKey)
    //         .set(MR.ROLE_ID, role.id)
    //         .set(MR.NAME, role.name)
    //         .set(MR.DELETED, role.deleted)
    //         .set(MR.REASON, role.reason)
    //         .set(MR.CREATED_BY, role.createdBy)
    //         .set(MR.CREATED_AT, role.createdAt)
    //         .set(MR.MODIFIED_BY, role.modifiedBy)
    //         .set(MR.MODIFIED_AT, role.modifiedAt)
    //         .execute()
    //
    // val MR_HIS = MST_ROLE_HIS
    // dslContext.insertInto(MR_HIS)
    //         .set(MR_HIS.ROLE_KEY, roleKey)
    //         .set(MR_HIS.REV, 1L)
    //         .set(MR_HIS.REVTYPE, 1L)
    //         .set(MR_HIS.ROLE_ID, role.id)
    //         .set(MR_HIS.NAME, role.name)
    //         .set(MR_HIS.DELETED, role.deleted)
    //         .set(MR_HIS.REASON, role.reason)
    //         .set(MR_HIS.CREATED_BY, role.createdBy)
    //         .set(MR_HIS.CREATED_AT, role.createdAt)
    //         .set(MR_HIS.MODIFIED_BY, role.modifiedBy)
    //         .set(MR_HIS.MODIFIED_AT, role.modifiedAt)
    //         .execute()

    // case 2
    // dslContext.insertInto(MST_ROLE).set(toRecord(roleKey, role)).execute()
    // dslContext.insertInto(MST_ROLE_HIS).set(toHistoryRecord(roleKey, role)).execute()
     */

    @Transactional
    fun update(role: RoleEntity): RoleEntity {
        return role.apply {
            val updated = dslContext.executeUpdate(toRecord(this.key, this))
            if (updated == 1) {
                dslContext.executeInsert(toHistoryRecord(this.key, this))
            }
        }
    }

    @Transactional(readOnly = true)
    fun select(roleKey: Long): RoleEntity? {
        val record = dslContext.selectFrom(MST_ROLE)
            .where(MST_ROLE.ROLE_KEY.eq(roleKey)).fetchOne()
        return record?.toEntity()
    }

    fun MstRoleRecord?.toEntity(): RoleEntity? {
        return if (this == null) {
            null
        } else {
            RoleEntity(
                key = roleKey!!,
                id = roleId!!,
                name = name ?: "",
                deleted = deleted!!
            ).apply {
                this.audit = Audit(
                    reason = reason,
                    createdBy = createdBy,
                    createdAt = createdAt,
                    modifiedBy = modifiedBy,
                    modifiedAt = modifiedAt,
                )
            }
        }
    }

    private fun toRecord(roleKey: Long?, role: RoleEntity) = MstRoleRecord(
        roleKey = roleKey!!,
        roleId = role.id,
        name = role.name,
        deleted = role.deleted,
        reason = role.reason,
        createdBy = role.createdBy,
        createdAt = role.createdAt,
        modifiedBy = role.modifiedBy,
        modifiedAt = role.modifiedAt
    )

    private fun toHistoryRecord(roleKey: Long?, role: RoleEntity): MstRoleHisRecord {
        val rev = Math.abs(Random.nextInt()).toLong()
        dslContext.executeInsert(
            RevinfoRecord(rev = rev,
                          revtstmp = role.modifiedAt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()))

        return MstRoleHisRecord(
            roleKey = roleKey!!,
            roleId = role.id,
            name = role.name,
            deleted = role.deleted,
            reason = role.reason,
            createdBy = role.createdBy,
            createdAt = role.createdAt,
            modifiedBy = role.modifiedBy,
            modifiedAt = role.modifiedAt,
            rev = rev,
            revtype = 1L
        )
    }
}
```

처음엔 Repository 영역 내에서 save시 key로 현재 저장된 Entity를 조회하여 요청 Entity와의 다른 점이 있는 지 비교하려고 했는데
```kotlin
// service에서 변환 된 요청 Entity
val requestToEntity = response.toEntity()

// 조회 한 Entity
val selectedEntity = query.select()

if(requestToEntity != selectedEntity) {
    // do update
}
```

이럴 경우 requestToEntity에 변경 될 수 없는 프로퍼티(roleId)가 공백으로 들어가야 하는 것이 싫었다.  
Repository 내에선 name만 업데이트를 칠 것이기에 문제는 없었으나 정합성이 깨진 Entity가 돌아다닐 수 있기에...  
그렇다고 RequestDto를 Repository로 들여보내기도 싫었고...  
 
 그래서 Service에서 Entity를 조회하고 미리 값을 채워주었다.
 ```kotlin
// service에서 변환 된 요청 Entity
val requestToEntity = query.select().apply{
  name = response.name,
  deleted = response.deleted,
  audit = Audit(
                createdBy = 1L,
                createdAt = now,
                modifiedBy = 1L,
                modifiedAt = now,
                reason = request.reason
            )
}


// 조회 한 Entity
val selectedEntity = query.select()
if (requestToEntity != selectedEntity) {
    // do update
}
``` 
이러다보니 select를 두 번 하는 꼴이 된다.  
결과적으로는 아래와 같이 서비스에서 변경이 있는 지 비교하는게 나을 것 같았다.    
```kotlin
@Component
class RoleUpdateService(val roleQuery: RoleQuery) {
    @Transactional
    fun update(roleKey: Long, request: RoleUpdateRequest): RoleRetrieveResponse {
        val previous = roleQuery.select(roleKey) ?: throwResourceNotFound()

        val now = LocalDateTime.now()
        val role = previous.copy(
            key = roleKey,
            name = request.name,
            deleted = request.deleted).apply {
            audit = Audit(
                createdBy = 1L,
                createdAt = now,
                modifiedBy = 1L,
                modifiedAt = now,
                reason = request.reason
            )
        }
        // 변경이 있는 경우에만 수정한다.
        if (previous != role) {
            roleQuery.update(role)
        }

        return RoleRetrieveResponse(
            key = role.key,
            id = role.id,
            name = role.name,
            deleted = role.deleted)
    }

}
```
아래와 같이 로그도 보기쉽게 나오고 사용성은 좋은 것 같다.  
<img src="https://github.com/dlxotn216/image/blob/master/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202021-03-17%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%201.36.50.png?raw=true" />

다만 JPA를 사용한다면 Request/Response -> Entity로 총 두 번의 변환이 있지만  
JOOQ를 사용한다면 Request/Response -> Entity -> Record로 총 세번의 변환이 발생한다는 점이 단점같다.  
그에 따라서 프로퍼티 누락 등의 실수할 수 있는 구간이 늘어나기도 하고 컬럼 변경 등의 Schema 변경에 따라 영향이 가는 위치가 늘어날테니...    

Entity 뿐아니라 Enver에서 해주는 Revision에 대한 것도 신경써줘야 하니... 그것 또한 문제가 아닐 수 없다.  
아래와 같이 Entity <-> Record에 대한 부분을 함수/메서드로 만들고 단위테스트를 잘 채우면 될까 싶기도 하지만  
필드가 추가 됐을 때 테스트마저 누락되면 역시 실수는 분면 발생할 것이라 완전하지는 않은 듯.  
JPA라면 update 메서드에 인자를 추가하면 컴파일 시점에 알 수 있을 테니까...
```kotlin
fun MstRoleRecord?.toEntity(): RoleEntity? {
    return if (this == null) {
        null
    } else {
        RoleEntity(
            key = roleKey!!,
            id = roleId!!,
            name = name ?: "",
            deleted = deleted!!
        ).apply {
            this.audit = Audit(
                reason = reason,
                createdBy = createdBy,
                createdAt = createdAt,
                modifiedBy = modifiedBy,
                modifiedAt = modifiedAt,
            )
        }
    }
}

private fun toRecord(roleKey: Long?, role: RoleEntity) = MstRoleRecord(
    roleKey = roleKey!!,
    roleId = role.id,
    name = role.name,
    deleted = role.deleted,
    reason = role.reason,
    createdBy = role.createdBy,
    createdAt = role.createdAt,
    modifiedBy = role.modifiedBy,
    modifiedAt = role.modifiedAt
)

private fun toHistoryRecord(roleKey: Long?, role: RoleEntity): MstRoleHisRecord {
    val rev = Math.abs(Random.nextInt()).toLong()
    dslContext.executeInsert(
        RevinfoRecord(rev = rev,
                      revtstmp = role.modifiedAt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()))

    return MstRoleHisRecord(
        roleKey = roleKey!!,
        roleId = role.id,
        name = role.name,
        deleted = role.deleted,
        reason = role.reason,
        createdBy = role.createdBy,
        createdAt = role.createdAt,
        modifiedBy = role.modifiedBy,
        modifiedAt = role.modifiedAt,
        rev = rev,
        revtype = 1L
    )
}
```

### JOOQ를 사용한 Insert 방법
다양한 케이스가 존재 하는 듯 하다.   
batch insert도 가능하고 DBMS에 따라 merge into나 on duplicated 관련 처리도 가능하다. 
case 1
```kotlin
val MR = MST_ROLE
dslContext.insertInto(MR)
        .set(MR.ROLE_KEY, roleKey)
        .set(MR.ROLE_ID, role.id)
        .set(MR.NAME, role.name)
        .set(MR.DELETED, role.deleted)
        .set(MR.REASON, role.reason)
        .set(MR.CREATED_BY, role.createdBy)
        .set(MR.CREATED_AT, role.createdAt)
        .set(MR.MODIFIED_BY, role.modifiedBy)
        .set(MR.MODIFIED_AT, role.modifiedAt)
        .execute()

val MR_HIS = MST_ROLE_HIS
dslContext.insertInto(MR_HIS)
        .set(MR_HIS.ROLE_KEY, roleKey)
        .set(MR_HIS.REV, 1L)
        .set(MR_HIS.REVTYPE, 1L)
        .set(MR_HIS.ROLE_ID, role.id)
        .set(MR_HIS.NAME, role.name)
        .set(MR_HIS.DELETED, role.deleted)
        .set(MR_HIS.REASON, role.reason)
        .set(MR_HIS.CREATED_BY, role.createdBy)
        .set(MR_HIS.CREATED_AT, role.createdAt)
        .set(MR_HIS.MODIFIED_BY, role.modifiedBy)
        .set(MR_HIS.MODIFIED_AT, role.modifiedAt)
        .execute()
```

case 2
```kotlin
dslContext.insertInto(MST_ROLE).set(toRecord(roleKey, role)).execute()
dslContext.insertInto(MST_ROLE_HIS).set(toHistoryRecord(roleKey, role)).execute()
```

case 3
```kotlin
role.apply {
    dslContext.batchInsert(toRecord(roleKey, this), toHistoryRecord(roleKey, this)).execute()    
}
```

### JOOQ로 변경비교 두번째...
아래와 같이 update 구문에 조건절을 추가하고 update 된 행이 있으면 revision을 insert 한다.
```kotlin
val updated = dslContext.update(mr)
                .set(mr.NAME, role.name)
                .set(mr.DELETED, role.deleted)
                .set(mr.REASON, role.reason)
                .where(
                    mr.ROLE_KEY.eq(role.key)
                        .and(
                            nullif(mr.NAME, "").ne(role.name ?: "")
                                .or(mr.DELETED.ne(role.deleted))
                        )
                ).execute()

if (updated > 0) {
    dslContext.executeInsert(toHistoryRecord(role.key, role))
}
```

### JOOQ로 변경비교 세번째...
아래와 같이 최초 조회 된 old와 request로 부터 변경될 field가 설정 된 roleEntity를 같이 Repository에 넘긴다.  
```kotlin
class Service {
    fun update(roleKey: Long, request: RoleUpdateRequest): RoleRetrieveResponse {
        val now = LocalDateTime.now()
        val old = roleQuery.select(roleKey) ?: throwResourceNotFound()
        val role = old.copy(
            key = roleKey,
            name = request.name,
            deleted = request.deleted
        ).apply {
            audit = Audit(
                createdBy = 1L,
                createdAt = now,
                modifiedBy = 1L,
                modifiedAt = now,
                reason = request.reason
            )
        }
    
        return with(roleQuery.update(old, role)) {
            RoleRetrieveResponse(
                key = key,
                id = id,
                name = name,
                deleted = deleted)
        }
    }
}
```
repository에서 동등을 비교하며 업데이트를 수행하고 revision을 쌓는다. 
```kotlin
class Repository {
    fun update(old: RoleEntity, role: RoleEntity): RoleEntity {
        if (old == role) {
            return old
        }
    
        val mr = MST_ROLE
        return role.apply {
            val updated = dslContext.update(mr)
                .set(mr.NAME, name)
                .set(mr.DELETED, deleted)
                .set(mr.REASON, reason)
                .where(mr.ROLE_KEY.eq(key)).execute()
    
            if (updated > 0) {
                dslContext.executeInsert(toHistoryRecord(key, role))
            }
        }
    }
}
```

update에 old를 넘기는 부분이 좀 맘에 안들긴 하지만 Select를 줄일 수 있어 제일 나은 듯 하다.  
변경이 없으면 아예 update 쿼리 자체를 날리지 않을 수 있으니.  
JOOQ를 사용한다면 이 방식으로 쭈욱 나가지 않을까 싶은데 Entity <-> Record 변환 부분이 참 맘에 걸린다.  
Revision Entity도 신경 써줘야 하니...  
(Enver의 스키마에 맞추다보니 rev, revtype을 같이 넣어줘야 해서 생긴 변환이니 이건 무시해도 좋을 듯 하다.)