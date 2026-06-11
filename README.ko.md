<h1 align="center">KursMapper</h1>

<p align="center">
  <a href="https://central.sonatype.com/artifact/kr.urbansoft.kursmapper/kurs-mapper-annotation"><img src="https://img.shields.io/maven-central/v/kr.urbansoft.kursmapper/kurs-mapper-annotation?color=blue&style=flat-square" alt="Maven Central"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square" alt="License: MIT"></a>
</p>

*다른 언어로도 이 문서의 내용을 읽을 수 있습니다: [English](README.md), [🇰🇷 한국어](README.ko.md)*

> **코드를 매퍼에 맞추지 마세요. 매퍼가 코드에 맞춰야 합니다.**

매퍼 라이브러리를 사용하다 보면 여러 가지 한계에 봉착하곤 합니다. 매퍼 인터페이스에 각종 복잡한 어노테이션을 추가해야 하거나, 도메인 모델에 어노테이션을 추가해야 하는 경우도 있죠. 어떤 경우에는 라이브러리의 한계 때문에 해당 객체의 매핑 코드를 전부 수동으로 작성하기도 합니다. 아주 가끔은 매퍼 인터페이스를 만들어 놓은 사실을 잊어버리고 또 다시 만들기도 합니다.

**KursMapper**는 이런 기존의 문제들을 해결할 수 있는 새로운 방법을 제시하고자 합니다.

---

## 📄 목차

- [KursMapper를 선택해야 하는 이유](#-kursmapper를-선택해야-하는-이유)
- [설치](#-설치)
- [빠른 시작](#-빠른-시작)
- [컨텍스트 기반 구동](#-컨텍스트-기반-구동)
- [매퍼 및 매핑 함수 작성 규칙](#-매퍼-및-매핑-함수-작성-규칙)
- [자동 생성된 매핑 함수 수정 방법](#-자동-생성된-매핑-함수-수정-방법)
- [샌드박스에 대하여](#-샌드박스에-대하여)
- [KursMapper의 철학](#-kursmapper의-철학)
- [피드백](#-피드백)

---

## 💡 KursMapper를 선택해야 하는 이유

> **"라이브러리와 더 이상 싸우지 마세요. 자동화에 갇히지 않는 유연함을 제공합니다."**

* 🚫 **어노테이션 지옥 탈출**  
  단 한 개의 필수 어노테이션과 두 개의 선택적 어노테이션만이 존재합니다. **진짜입니다.**


* 🛠️ **추가 DSL 제로**  
  대부분의 설정과 모든 매핑 구현은 순수 코틀린을 이용합니다. 라이브러리의 한계에 맞춰 코드를 바꾸는 대신, 라이브러리가 어떻게 동작해야 하는지 가르치세요.


* 🎯 **자동 생성은 확실할 때만!**  
  자동화를 강제하지 않습니다. 자동화가 어려울 땐 그냥 구현해 버리는 게 더 빠르니까요!


* 🧭 **가이드 기반 개발**  
  자동 생성이 불가능할 때 단순히 에러만 출력하지 않습니다.  
  어떤 매퍼를 어디에 어떤 이름으로 만들어야 하는지 단계별 가이드를 제공합니다.


* ⚡ **코틀린과 IDE 자동완성의 시너지**  
  객체에 코틀린 확장함수로 매핑 컨텍스트를 등록하므로 IDE 자동완성에 매우 친화적입니다.


* 🔄 **매핑 코드 재사용성 극대화**  
  모든 매핑을 그래프로 처리하여 컨텍스트에 자동 등록하기 때문에 프로퍼티별 매핑도 추가 정의 없이 바로 사용할 수 있습니다.


* 🔓 **벤더 락인 효과 최소화**  
  **KursMapper**가 추구하는 매퍼 구조는 **KursMapper** 없이도 독립적으로 기능합니다.

---

## 📦 설치

`build.gradle.kts`에 아래 내용을 추가하세요:

```kotlin
plugins {
  id("com.google.devtools.ksp") version "2.3.7"
}

dependencies {
  implementation("kr.urbansoft.kursmapper:kurs-mapper-annotation:0.1.0-alpha")
  ksp("kr.urbansoft.kursmapper:kurs-mapper-processor:0.1.0-alpha")
}
```

---

## 🚀 빠른 시작

매퍼가 위치할 패키지를 하나 정하세요.

```kotlin
package kr.urbansoft.example.adapter.infra.persistence.mapper // 예시입니다.
```

해당 패키지에 아래와 같은 예제 인터페이스를 만드세요.

```kotlin
package kr.urbansoft.example.adapter.infra.persistence.mapper

@Suppress("unused", "EXTENSION_SHADOWED_BY_MEMBER") // IDE 구문 강조를 이용하기 위한 선택적 어노테이션. 취향에 따라 삭제해도 됩니다.
@KursContext(contextName = "persistenceMapper", rootPackageName = "kr.urbansoft", guideLanguage = GuideLanguage.KO_KR) // 최소 설정입니다.
// contextName: 이 패키지에 모일 매퍼들을 지칭하는 이름입니다. KursMapper는 철저하게 컨텍스트 기반 하에 동작합니다.
// rootPackageName: 여러분 코드베이스의 root 패키지 이름을 입력해주세요.
// guideLanguage: 한국어 README를 위해 가이드 언어를 한국어로 설정했습니다. 이 설정은 선택 항목이며, 기본값은 EN_US입니다.
interface PersistenceMapperConfig {
  // @KursContext interface 내에 정의한 확장함수 정의가 곧 매핑 목적 정의입니다.
  fun MeetingRoom.asMeetingRoomRecord(): MeetingRoomRecord // MeetingRoom -> MeetingRoomRecord 목적 정의
  
  fun MeetingRoomRecord.asMeetingRoom(): MeetingRoom // MeetingRoomRecord -> MeetingRoom 목적 정의
  
  fun User.asUserRecord(): UserRecord // User -> UserRecord 목적 정의
  
  fun UserRecord.asUser(): User // UserRecord -> User 목적 정의
}
```
> 💡 목적 정의  
> **KursMapper**에게 어떤 변환이 필요한지 알려주는 설정입니다.  
> 코틀린 확장함수 문법을 이용하여 리시버에는 원본 객체를, 반환 타입은 대상 객체를 가리킵니다.  
> 목적 정의에 사용한 함수명은 설정 인터페이스 내에서의 구분을 위해서만 쓰입니다.

프로젝트를 빌드하세요. 뭐라구요? 벌써 빌드하는 게 맞냐구요?  
네! 맞습니다. 사용에 필요한 설정이 이미 다 끝났습니다.  

---

### ❔ 매퍼 호출 방법

**KursMapper**의 매핑 함수는 기본적으로 아래와 같이 호출합니다.

DI(의존성 주입)는 필요하지 않습니다.

매퍼가 어디있는지 찾을 필요가 없으며, IDE 자동완성을 통해 사용 가능한 매핑 함수를 쉽게 확인할 수 있습니다.

```kotlin
source // 다른 객체로 변환하고 싶은 원본 객체
  .someContextMapper() // 설정에 정의한 contextName
  .asTarget() // 변환을 호출하는 최종 매핑 함수
```

#### 📛 왜 `toTarget()`이 아니라 `asTarget()` 인가요?

**KursMapper**는 호출부에서 타겟 타입을 즉시 인지할 수 있도록 최종 변환 함수명의 기본값을 `as[대상 클래스 이름]` 구조로 생성합니다.

* **접두어 `as`를 사용하는 이유**  
코틀린/자바의 최상위 메서드인 `toString()`과의 네이밍 충돌 가능성을 원천 차단하기 위함입니다. 내부 구조상 최종 함수명이 `toString`이 될 경우 매핑이 정상적으로 동작하지 않을 수 있으므로, 안전한 `as`를 기본값으로 채택했습니다.


* **이름 커스터마이징**  
프로젝트 컨벤션에 따라 이 접두어나 네이밍 규칙을 바꾸고 싶다면 [KursMapper - All Settings](README.all-settings.ko.md)를 참고하여 변경할 수 있습니다.

---

### 🛠️ 실전 사용 예제

위에서 설정한대로 프로젝트를 빌드하면 아마 빌드 에러가 발생할 것입니다. 걱정하지 마세요. **정상입니다.**  
KursMapper는 부족한 매핑을 발견하면 단순히 에러만 출력하는 것이 아니라, 무엇을 어떻게 구현해야 하는지 가이드와 함께 알려줍니다.  
빌드 메시지를 보기 전에, 먼저 이번 예제에서 사용할 객체들을 살펴보겠습니다.

보시다시피 단순 DTO <-> DTO 예제가 아닙니다.  
실제 프로젝트에서 흔히 만날 수 있는 중첩 객체, 컬렉션, 커스텀 타입 등이 포함된 예제를 처음부터 끝까지 따라가며 **KursMapper**가 어떤 방식으로 여러분을 돕는지 확인해 보겠습니다.

```kotlin
@JvmInline value class UserId(val value: UUID)

enum class Trait {
  LUSTFUL,
  CHASTE,
  GLUTTONOUS,
  TEMPERATE,
  GREEDY,
  GENEROUS,
}

data class User(val id: UserId, val age: Int, val name: String, val createAt: Instant, val traits: Set<Trait>)

class UserRecord(val id: UUID, val age: Int, val name: String, val createAt: LocalDateTime, val traits: List<String>)

@JvmInline value class MeetingRoomId(val value: Long)

data class MeetingRoom(val id: MeetingRoomId, val name: String, val reservation: MeetingRoomReservation)

data class MeetingRoomReservation(val datetime: Instant, val userIds: List<UserId>)

class MeetingRoomRecord(val id: Long, val name: String, val datetime: LocalDateTime, val userIds: List<UUID>)
```

빌드 메시지입니다.  
메시지 내용에도 있듯이 가이드는 항상 `/build/generated/ksp/main/resources/KursMapperGuide.txt` 파일에도 함께 저장되니 참고해주세요.

```text
[KursMapper]
Unresolved mapping functions detected.
Please refer to the KursMapper guide and resolve them.
You can find the guide in the build console
or at '/build/generated/ksp/main/resources/KursMapperGuide.txt'

======================== [KursMapper 가이드] ========================

함수 구현이 필요합니다. 아래 내용을 참고해서 구현해주세요.

1. 매퍼를 생성합니다.
    - 매퍼가 위치할 패키지: kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin
    - 파일명: StringMapper.kt
    - 아래 내용의 매퍼 파일을 생성해주세요.
        @JvmInline
        value class StringMapper(val source: String)
    - 참고) source FQCN: kotlin.String

2. 매퍼에 함수를 추가한 후, 매핑 로직을 구현해주세요.
    - 매퍼 FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin.StringMapper
    - 아래 내용의 함수를 추가해주세요.
        fun asTrait(): Trait = TODO("여기에 매핑 로직을 구현해주세요.")
    - 참고) source FQCN: kotlin.String
    - 참고) target FQCN: kr.urbansoft.example.domain.model.example.Trait

3. 구현이 완료되면 다시 빌드해주세요.

=====================================================================

======================== [KursMapper 가이드] ========================

함수 구현이 필요합니다. 아래 내용을 참고해서 구현해주세요.

...하략: 추가적으로 5 건의 가이드가 더 있습니다...
```

**KursMapper**는 자동화를 강제하거나, 현재 시점에서 추론 불가능 한 코드를 자동 생성하려고 노력하지 않습니다.  
대신 **어떤 것이 부족한지, 그리고 그것을 채우려면 어떻게 해야 하는지 최대한 상세하게 알리려고 노력합니다.**  
가이드 대로 한 번 구현해 봅시다.

```kotlin
package kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin

// import 생략

@JvmInline
value class StringMapper(val source: String) {
  fun asTrait(): Trait = Trait.valueOf(source)
}
```

구현 중간에 `StringMapper` 클래스에 `Redeclaration` 충돌이 발생해도 걱정하지 말고 구현하세요.  
다시 빌드하면 충돌이 자동으로 해결됩니다.

구현이 완료되었다면 이제 다시 빌드합니다.

이제 5건의 가이드가 남았습니다.

```text
======================== [KursMapper 가이드] ========================

함수 구현이 필요합니다. 아래 내용을 참고해서 구현해주세요.

1. 매퍼를 생성합니다.
    - 매퍼가 위치할 패키지: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - 파일명: TraitMapper.kt
    - 아래 내용의 매퍼 파일을 생성해주세요.
        @JvmInline
        value class TraitMapper(val source: Trait)
    - 참고) source FQCN: kr.urbansoft.example.domain.model.example.Trait

2. 매퍼에 함수를 추가한 후, 매핑 로직을 구현해주세요.
    - 매퍼 FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.TraitMapper
    - 아래 내용의 함수를 추가해주세요.
        fun asString(): String = TODO("여기에 매핑 로직을 구현해주세요.")
    - 참고) source FQCN: kr.urbansoft.example.domain.model.example.Trait
    - 참고) target FQCN: kotlin.String

3. 구현이 완료되면 다시 빌드해주세요.

=====================================================================
```

가이드에 따라 구현한 뒤, 다시 빌드합니다.

```kotlin
@JvmInline
value class TraitMapper(val source: Trait) {
  fun asString(): String = source.name
}
```

세 번째 가이드입니다.

```text
======================== [KursMapper 가이드] ========================

함수 구현이 필요합니다. 아래 내용을 참고해서 구현해주세요.

1. 매퍼를 생성합니다.
    - 매퍼가 위치할 패키지: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - 파일명: MeetingRoomRecordMapper.kt
    - 아래 내용의 매퍼 파일을 생성해주세요.
        @JvmInline
        value class MeetingRoomRecordMapper(val source: MeetingRoomRecord)
    - 참고) source FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.MeetingRoomRecord

2. 매퍼에 함수를 추가한 후, 매핑 로직을 구현해주세요.
    - 매퍼 FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.MeetingRoomRecordMapper
    - 아래 내용의 함수를 추가해주세요.
        fun asMeetingRoom(): MeetingRoom { return MeetingRoom(id = source.id.persistenceMapper().asMeetingRoomId(), name = source.name, reservation = TODO("Implementation required")) }
    - 참고) source FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.MeetingRoomRecord
    - 참고) target FQCN: kr.urbansoft.example.domain.model.example.MeetingRoom

3. 구현이 완료되면 다시 빌드해주세요.

=====================================================================
```

그런데 구현하려고 보니 `MeetingRoomRecord.datetime`은 `java.time.LocalDateTime`이고, `MeetingRoom.reservation.datetime`은 `kotlin.time.Instant`여서 `TimeZone`이 필요합니다!  
아무 걱정 없이 `asMeetingRoom` 메서드에 인자를 추가하세요.

```kotlin
@JvmInline
value class MeetingRoomRecordMapper(val source: MeetingRoomRecord) {
  fun asMeetingRoom(timeZone: TimeZone): MeetingRoom {
    return MeetingRoom(
      id = source.id.persistenceMapper().asMeetingRoomId(),
      name = source.name,
      reservation =
        MeetingRoomReservation(
          datetime = source.datetime.toKotlinLocalDateTime().toInstant(timeZone), // 인자를 받아서 처리할 수 있습니다!
          userIds = source.userIds, // 이 부분은 아직 컴파일 오류가 발생합니다!
        ),
    )
  }
}
```

그리고 이제 `userIds`를 구현해야 하는데 둘 다 `List` 입니다. 이럴 때는 아주 간단하게 아래처럼 구현할 수 있습니다.

```kotlin
@JvmInline
value class MeetingRoomRecordMapper(val source: MeetingRoomRecord) {
  fun asMeetingRoom(timeZone: TimeZone): MeetingRoom {
    return MeetingRoom(
      id = source.id.persistenceMapper().asMeetingRoomId(),
      name = source.name,
      reservation =
        MeetingRoomReservation(
          datetime = source.datetime.toKotlinLocalDateTime().toInstant(timeZone),
          userIds = source.userIds.map { it.persistenceMapper().asUserId() }, // KursMapper가 자동 생성한 UUID -> UserId 매핑 함수를 이용합니다!
        ),
    )
  }
}
```

네 번째 가이드입니다.

```text
======================== [KursMapper 가이드] ========================

함수 구현이 필요합니다. 아래 내용을 참고해서 구현해주세요.

1. 매퍼를 생성합니다.
    - 매퍼가 위치할 패키지: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - 파일명: MeetingRoomMapper.kt
    - 아래 내용의 매퍼 파일을 생성해주세요.
        @JvmInline
        value class MeetingRoomMapper(val source: MeetingRoom)
    - 참고) source FQCN: kr.urbansoft.example.domain.model.example.MeetingRoom

2. 매퍼에 함수를 추가한 후, 매핑 로직을 구현해주세요.
    - 매퍼 FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.MeetingRoomMapper
    - 아래 내용의 함수를 추가해주세요.
        fun asMeetingRoomRecord(): MeetingRoomRecord { return MeetingRoomRecord(id = source.id.persistenceMapper().asLong(), name = source.name, datetime = TODO("Implementation required"), userIds = TODO("Implementation required")) }
    - 참고) source FQCN: kr.urbansoft.example.domain.model.example.MeetingRoom
    - 참고) target FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.MeetingRoomRecord

3. 구현이 완료되면 다시 빌드해주세요.

=====================================================================
```

```kotlin
@JvmInline
value class MeetingRoomMapper(val source: MeetingRoom) {
  fun asMeetingRoomRecord(timeZone: TimeZone): MeetingRoomRecord {
    return MeetingRoomRecord(
      id = source.id.persistenceMapper().asLong(),
      name = source.name,
      datetime = source.reservation.datetime.toLocalDateTime(timeZone).toJavaLocalDateTime(),
      userIds = source.reservation.userIds.map { it.persistenceMapper().asJavaUUID() } // 이번에도 KursMapper가 자동 생성한 UserId -> UUID 매핑 함수를 이용합니다!
    )
  }
}
```

다섯 번째 가이드입니다.

```text
======================== [KursMapper 가이드] ========================

함수 구현이 필요합니다. 아래 내용을 참고해서 구현해주세요.

1. 매퍼를 생성합니다.
    - 매퍼가 위치할 패키지: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - 파일명: UserRecordMapper.kt
    - 아래 내용의 매퍼 파일을 생성해주세요.
        @JvmInline
        value class UserRecordMapper(val source: UserRecord)
    - 참고) source FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord

2. 매퍼에 함수를 추가한 후, 매핑 로직을 구현해주세요.
    - 매퍼 FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.UserRecordMapper
    - 아래 내용의 함수를 추가해주세요.
        fun asUser(): User { return User(id = source.id.persistenceMapper().asUserId(), age = source.age, name = source.name, createAt = TODO("Implementation required"), traits = source.traits.persistenceMapper().asTraitSet()) }
    - 참고) source FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord
    - 참고) target FQCN: kr.urbansoft.example.domain.model.example.User

3. 구현이 완료되면 다시 빌드해주세요.

=====================================================================
```

```kotlin
@JvmInline
value class UserRecordMapper(val source: UserRecord) {
  fun asUser(timeZone: TimeZone): User {
    return User(
      id = source.id.persistenceMapper().asUserId(),
      age = source.age,
      name = source.name,
      createAt = source.createAt.toKotlinLocalDateTime().toInstant(timeZone), // 이번에도 인자를 받아서 처리합시다.
      traits =
        source.traits
          .persistenceMapper()
          .asTraitSet(), // KursMapper가 여러분이 가이드에 따라 구현했던 String -> Trait 매핑 함수를 이용하는 List<String> -> Set<Trait> 매핑 함수를 자동으로 만들었습니다!
    )
  }
}

```

이제 마지막 하나 남은 가이드도 해결한 뒤, 빌드하면 드디어 빌드에 성공하면서 인터페이스에 정의했던 목적 정의가 모두 완성되었습니다.  

```text
======================== [KursMapper 가이드] ========================

함수 구현이 필요합니다. 아래 내용을 참고해서 구현해주세요.

1. 매퍼를 생성합니다.
    - 매퍼가 위치할 패키지: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - 파일명: UserMapper.kt
    - 아래 내용의 매퍼 파일을 생성해주세요.
        @JvmInline
        value class UserMapper(val source: User)
    - 참고) source FQCN: kr.urbansoft.example.domain.model.example.User

2. 매퍼에 함수를 추가한 후, 매핑 로직을 구현해주세요.
    - 매퍼 FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.UserMapper
    - 아래 내용의 함수를 추가해주세요.
        fun asUserRecord(): UserRecord { return UserRecord(id = source.id.persistenceMapper().asJavaUUID(), age = source.age, name = source.name, createAt = TODO("Implementation required"), traits = source.traits.persistenceMapper().asStringList()) }
    - 참고) source FQCN: kr.urbansoft.example.domain.model.example.User
    - 참고) target FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord

3. 구현이 완료되면 다시 빌드해주세요.

=====================================================================
```

```kotlin
@JvmInline
value class UserMapper(val source: User) {
  fun asUserRecord(timeZone: TimeZone): UserRecord {
    return UserRecord(
      id = source.id.persistenceMapper().asJavaUUID(),
      age = source.age,
      name = source.name,
      createAt = source.createAt.toLocalDateTime(timeZone).toJavaLocalDateTime(),
      traits = source.traits.persistenceMapper().asStringList(),
    )
  }
}
```

이렇게 **KursMapper**의 사용자가 되어 주셔서 감사합니다!

**KursMapper**는 이렇게 **작은 단위의 매핑 구현을 조합**하여 큰 단위의 매핑을 완성해 나가기 때문에, **사용하면 할 수록 수동 구현은 줄어들고 자동 연결이 늘어나는 구조**를 가지고 있습니다.  
가이드를 열심히 준비했으니, 여러분께 많은 도움이 되길 바랍니다.

---

### 🔎 자동 생성 결과를 믿어도 되나요?

**KursMapper**는 런타임 리플렉션이나 숨겨진 매핑 엔진으로 동작하지 않습니다.

생성된 매핑 함수는 `/build/generated/ksp` 아래에 평범한 코틀린 코드로 생성되며, IDE에서 직접 열어보고 디버깅할 수 있습니다.

자동 생성된 구현이 마음에 들지 않는다면 같은 매퍼에 멤버 함수를 직접 작성해 덮어쓸 수 있습니다.

즉, **KursMapper**의 자동 생성은 블랙박스가 아니라 검토하고 교체할 수 있는 초안에 가깝습니다.

---

## 🌐 컨텍스트 기반 구동

KursMapper는 철저하게 **컨텍스트 단위**로 격리되어 구동됩니다. 설정 인터페이스에서 지정한 `contextName`이 곧 매퍼를 여는 열쇠가 됩니다.

* **자유로운 컨텍스트 설계**  
  프로젝트의 크기에 따라 영속성(Persistence) 계층 전체를 하나로 묶을 수도 있고, 도메인별로 세세하게 분리할 수도 있습니다. 여러분의 자유입니다!


* **컨텍스트 간의 독립성**  
  KursMapper는 **서로 다른 컨텍스트에 있는 매핑 함수를 자동으로 연결하지 않습니다.** 컨텍스트는 상호 독립적이어야 합니다. 물론 필요한 경우 여러분이 직접 다른 컨텍스트의 함수를 수동으로 호출하여 조합하는 것은 자유입니다.


* **⚠️ 주의 - 패키지 구조 제약**  
  컨텍스트 설정 인터페이스가 위치한 패키지와 그 하위 패키지에는 **또 다른 설정 인터페이스를 중복 정의하면 안 됩니다.** 이름 충돌 및 컴파일 에러의 원인이 됩니다.

#### ❌ 잘못된 구조 (컨텍스트 중첩)

```text
infra/mapper/
  ├── PersistenceMapperConfig.kt  <- contextName = "persistenceMapper"
  └── user/
      └── UserMapperConfig.kt     <- 하위 패키지에 다른 컨텍스트 정의 시 문제가 발생할 수 있습니다.
```

#### ⭕ 올바른 구조 (컨텍스트 분리)

```text
infra/mapper/
  ├── persistence/
  │   └── PersistenceMapperConfig.kt  <- contextName = "persistenceMapper"
  └── external/
      └── ExternalApiMapperConfig.kt  <- contextName = "externalApiMapper"
```

---

## ✏️ 매퍼 및 매핑 함수 작성 규칙

**KursMapper**는 체계적이고 예측 가능한 매퍼 관리를 위해 일관되고 엄격한 작성 규칙을 사용합니다.

직접 매핑 함수를 구현하려면 아래 규칙을 **모두 준수**해야 하며, 규칙을 벗어난 매퍼와 매핑 함수는 **의도적으로 무시됩니다.**

> **🛑 잠깐!! 뒤로 가기나 창을 닫지 마세요!**
>
> 이 규칙들을 여러분께 일일이 외우라고 강요하는 게 아닙니다.  
> 규칙을 모르더라도 지킬 수 있는 멋진 방법이 준비되어 있으니, 마음 편하게 구조만 슥 훑어보세요!
> * 빠르게 알고 싶다면 [🌱 샌드박스에 대하여](#-샌드박스에-대하여)를 참조하세요.

---

### 📂 패키지 규칙

**KursMapper**는 한 패키지에 너무 많은 매퍼 파일이 쌓이는 문제를 줄이기 위해 아래와 같은 패키지 정책을 가지고 있습니다.

```kotlin
enum class MapperSubPackageCreationMode {
  AUTO, // 기본값
  FLAT,
  MANUAL,
}
```

* **`AUTO` (기본값)**: 원본 객체가 속한 패키지명의 마지막 조각을 하위 패키지로 자동 생성합니다.
    * 예: `kotlin.String` $\rightarrow$ `kr.urbansoft.example.adapter.infra.mapper.persistence.kotlin.StringMapper`
    * 예: `kr.urbansoft.user.User` $\rightarrow$ `kr.urbansoft.example.adapter.infra.mapper.persistence.user.UserMapper`


* **`FLAT`**: 하위 패키지를 만들지 않고, 모든 매퍼 클래스를 설정 인터페이스와 동일한 패키지에 평평하게 몰아넣습니다.


* **`MANUAL`**: 원하는 패키지명을 직접 지정합니다.

---

### 📄 파일 및 클래스명 규칙

**KursMapper**는 변환하고자 하는 원본 객체를 기준으로 매퍼 클래스 이름을 결정합니다.

특히 코틀린의 강력한 널 안정성을 십분 활용하기 위해, **같은 타입이어도 널 가능성이 다르면 완전히 다른 객체**로 취급하여 정교하게 분리합니다.

* 예: 원본이 `String`이면 $\rightarrow$ `StringMapper`
* 예: 원본이 `String?`이면 $\rightarrow$ `NullableStringMapper`

#### 🧩 클래스명 조립 템플릿

클래스 이름은 아래 구조로 조립되며, 원하는 경우 커스텀할 수 있습니다.

$$\text{[Prefix]} + \text{[Nullable]} + \text{[매퍼명]} + \text{[Suffix]} + \text{[글로벌 Suffix]}$$

- prefix: 기본값은 빈 문자열입니다.
- 널 가능성: 원본 클래스가 `Nullable`한 경우 `Nullable`이 붙습니다.
- 매퍼명: 기본값은 원본 클래스명입니다.
- suffix: 기본값은 빈 문자열입니다.
- 글로벌 suffix: 기본값은 `Mapper`입니다.

> **💡 권장 사항**  
> 매퍼가 위치할 파일명은 클래스명과 동일하게 만들고, **파일 하나당 매퍼 클래스 하나**를 두는 것을 권장합니다.
> 
> 단, 컴파일러가 패키지와 클래스명만 맞으면 인식하므로 강제 사항은 아닙니다.

---

### 🔒 value class 및 원본 변수 규칙

**KursMapper**는 구조 특성상 매퍼 클래스수가 많아질 수 있습니다. 이를 최소 비용으로 유지하기 위해 모든 매퍼는 value class여야 합니다.

* **변수명 제한**  
매퍼가 가지는 단 하나의 프로퍼티는 원본 객체여야 하며, 변수명은 기본적으로 `source`여야 합니다.


* **접근 제어자**  
**KursMapper**가 자동 생성하는 매핑 코드에서 원본 데이터에 접근할 수 있어야 하므로, 이 프로퍼티는 반드시 `public`이어야 합니다.

```kotlin
@JvmInline
value class UserMapper(val source: User)
```

---

### 🏷️ 매핑 함수명 규칙

매핑 함수명 역시 예측 가능한 규칙으로 조립됩니다.

$$\text{[동사]} + \text{[Prefix]} + \text{[Nullable]} + \text{[매핑 함수명]} + \text{[Suffix]}$$

- 동사: 기본값은 `as`입니다.
- prefix: 기본값은 빈 문자열입니다.
- 널 가능성: 대상 클래스가 `Nullable`한 경우 `Nullable`이 붙습니다.
- 매핑 함수명: 기본값은 대상 클래스명입니다.
- suffix: 기본값은 빈 문자열입니다.

---

### 🛠️ 매핑 함수 구현

이제 매퍼를 어디에, 어떤 이름으로 만들지가 모두 정해졌습니다. 매핑 함수의 내용은 코틀린으로 자유롭게 구현하세요!

> 각 항목의 세부적인 이름 규칙이나 접두사/접미사를 바꾸는 설정 방법은 [KursMapper - All Settings](README.all-settings.ko.md)를 참고해 주세요.

> 규칙이 너무 많고 엄격해서 항상 모두 지키기 어렵습니다.  
> 서두에 말한 것과 같이 [샌드박스에 대하여](#-샌드박스에-대하여)를 참고하면 규칙을 몰라도 지킬 수 있으니 걱정하지 마세요.

#### 🧪 규칙이 모두 적용된 최종 매퍼 예시

```kotlin
package kr.urbansoft.example.adapter.infra.mapper.exampleMapper.user

@JvmInline
value class NullableUserMapper(val source: User?) {
  fun asNullableUserDto(): UserDto? = source?.let { it.exampleMapper().asUserDto() }
}
```

---

## 🔄 자동 생성된 매핑 함수 수정 방법

### 📂 자동 생성된 매퍼의 위치 및 규칙

자동 생성된 코드는 모두 `/build/generated/ksp` 디렉토리 밑에 생성됩니다.  
자동 생성되는 코드 또한 위의 [매퍼 및 매핑 함수 작성 규칙](#-매퍼-및-매핑-함수-작성-규칙)을 따르며, 개발자가 직접 작성한 코드와 구분하기 위해 파일명에 `ByKurs` 접미사가 추가됩니다.

* 예: UserMapperByKurs.kt
 
---

### 🔍 자동 생성된 매퍼를 쉽게 찾는 방법

자동 생성된 매퍼를 직접 찾는 것은 번거로울 수 있습니다.  
규칙에 대해 잘 안다면 생각보다 손쉽게 원하는 매퍼 파일을 찾을 수도 있지만, 불편한 건 사실입니다.

이럴 때는 찾고 싶은 매핑 함수를 사용하는 임시 코드를 작성한 뒤에 IDE의 `Go to Declaration` 기능을 사용해보세요.  
즉시 해당 매핑 함수를 열어볼 수 있습니다.

```kotlin
val source: User
source.persistenceMapper().asUserRecord() // persistenceMapper 혹은 asUserRecord에 커서를 두고 Go to Declaration 기능을 사용하세요!
```

---

### 📜 자동 생성된 매퍼의 내용

**KursMapper**는 모든 매핑을 작은 단위의 매핑 함수가 모인 그래프로 처리하기 때문에  
자동 생성된 코드가 복잡하지 않고 사람이 직접 짠 매핑 코드와 크게 다르지 않습니다.

```kotlin
// Generated by KursMapper. DO NOT EDIT DIRECTLY.
package kr.urbansoft.example.adapter.infra.persistence.mapper.example

import kotlin.String
import kr.urbansoft.example.adapter.infra.persistence.mapper.example.persistenceMapper
import kr.urbansoft.example.domain.model.example.Trait

public fun Trait.persistenceMapper(): TraitMapper = TraitMapper(this)

public fun TraitMapper.asNullableString(): String? = source.persistenceMapper().asString()
```
> 💡 [빠른 시작](#-빠른-시작)에서 직접 구현했던 `source.persistenceMapper().asString()`, 즉 `Trait` $\rightarrow$ `String` 매핑 함수를 자동으로 찾아서 `TraitMapper.asNullableString()` 함수의 내용이 자동 생성된 걸 눈치채셨나요?

**KursMapper**는 코틀린의 함수 해석 규칙을 이용합니다.  
같은 이름의 멤버 함수와 확장 함수가 동시에 존재할 경우, 코틀린은 항상 멤버 함수를 우선 호출합니다.  
따라서 사용자가 직접 구현한 매핑 함수가 존재하면 자동 생성된 확장 함수는 자연스럽게 가려집니다.

---

### 🛠️ 자동 생성된 매핑 함수 수정하기

이제 자동 생성 되었지만 구현이 맘에 들지 않는 매핑 함수를 직접 수정할 준비가 되었습니다.  
패키지와 클래스명, 매핑함수명까지 모두 알게 되었으니까요.  

이제 `/build/generated/ksp` 디렉토리에서 벗어나 설정 인터페이스가 있는 패키지에서 하위 패키지를 만들고, 매퍼 파일이 없다면 매퍼 파일과 클래스를 추가하고, 수정하고 싶은 매핑 함수를 복사해 와서 구현만 원하는 대로 고쳐주면 됩니다.   

> 아무 정보 없이 새로운 매핑 함수를 구현할 때보다는 쉽겠지만, 여전히 규칙을 위반해 **KursMapper**에게 무시당할 수 있습니다.  
> 진짜 중요하고 시급한 건 **KursMapper**의 규칙 같은 게 아니고 새로운 구현을 1초라도 빨리 테스트 해 보는 것이잖아요?  
> [샌드박스에 대하여](#-샌드박스에-대하여)를 참고하면 가장 빠르게 내 구현을 **KursMapper**에게 주입하는 방법을 확인할 수 있습니다.

---

## 🌱 샌드박스에 대하여

샌드박스는 설정 인터페이스에 정의하는 임시 매퍼 및 매핑 함수를 의미합니다.

### ⚙️ 기본 설정 방법

```kotlin
@Suppress("unused", "EXTENSION_SHADOWED_BY_MEMBER")
@KursContext(contextName = "persistenceMapper", rootPackageName = "kr.urbansoft", guideLanguage = GuideLanguage.KO_KR)
interface PersistenceMapperConfig {
  fun MeetingRoom.asMeetingRoomRecord(): MeetingRoomRecord
  
  fun MeetingRoomRecord.asMeetingRoom(): MeetingRoom
  
  fun User.asUserRecord(): UserRecord
  
  fun UserRecord.asUser(): User
  
  // 샌드박스 설정
  // value class를 쓰지 않아도 됩니다.
  class SandboxExample(val value: Int) { // 클래스명, 변수명을 원하는 대로 정할 수 있습니다.
    fun test(): String = value.toString() // 매핑 함수명도 원하는 대로 정할 수 있습니다.
  }
}
```

샌드박스 설정에서 중요한 것은 원본 객체를 가리키는 클래스 파라미터의 타입, 그리고 대상 객체를 가리키는 매핑 함수의 반환 타입뿐입니다.  
다른 규칙은 이곳에서는 신경쓰지 않아도 됩니다.

> **💡 주의**  
> value class를 사용하지 않아도 되지만, 클래스 파라미터에는 파라미터를 **한 개만** 정의해주세요.  
> 두 개 이상의 파라미터를 정의하는 경우에는 **KursMapper**가 해당 클래스를 샌드박스 매퍼로 인식하지 못합니다.

샌드박스에 매퍼와 매핑함수를 추가했다면 빌드를 한 후, 어떻게 **KursMapper**에 적용되었는지 살펴봅시다.

```kotlin
// Generated by KursMapper. DO NOT EDIT DIRECTLY.
package kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin

import kotlin.Int
import kotlin.String
import kotlin.jvm.JvmInline
import kr.urbansoft.example.adapter.infra.persistence.mapper.PersistenceMapperConfig

@JvmInline
public value class IntMapper(
  public val source: Int,
)

public fun Int.persistenceMapper(): IntMapper = IntMapper(this)

public fun IntMapper.asString(): String = PersistenceMapperConfig.SandboxExample(source).test() // 샌드박스를 통해 생성된 매핑 함수
```

**KursMapper**가 샌드박스 설정을 발견하면, 원본 객체 $\rightarrow$ 대상 객체의 매핑 함수를 **규칙대로 생성**합니다.  
그리고 여러분이 방금 정의한 샌드박스의 매핑 함수를 호출하도록 내용을 구현합니다.

이를 통해 여러분의 샌드박스 구현이 즉시 **KursMapper**의 매핑 함수 그래프에 편입되고 **마치 정식 구현된 것처럼 동작**합니다.  
따라서 여러분은 **KursMapper**의 규칙에 휘둘리지 않으면서도 원하는 매핑 함수를 **KursMapper**에 넣었다 뺐다 하면서 **KursMapper**가 어떻게 동작하는지 마음껏 테스트 할 수 있습니다.

---

### 🔝 매핑 함수 우선순위

같은 source $\rightarrow$ target 매핑 함수에 대해 목적 정의, 샌드박스, 사용자 직접 구현이 동시에 존재할 수 있습니다.

사용자 직접 구현이 존재하면 그것이 우선 적용되고, 없다면 샌드박스 구현이 적용되며, 둘 다 없다면 목적 정의를 기반으로 자동 생성이 시도됩니다.

또한 각 매핑 함수의 인자 구성은 병합되지 않습니다.  
최종적으로 우선 적용된 매핑 함수의 인자 구성이 그대로 사용됩니다.

예를 들어 목적 정의에 인자가 없더라도 샌드박스 함수가 `timeZone: TimeZone` 인자를 가진다면, 샌드박스가 적용된 정식 매핑 함수도 `timeZone` 인자를 요구합니다.

반대로 사용자 직접 구현이 존재한다면, 목적 정의의 인자 구성과 관계없이 사용자 직접 구현의 시그니처가 최종적으로 사용됩니다.

---

### 🚀 샌드박스 매핑 함수를 정식 매핑 함수로 승격하기

샌드박스를 통해 충분히 만족할 만한 구현을 테스트한 뒤, 설정 인터페이스 밖의 정식 매퍼로 옮기고 싶다면 승격 가이드를 사용할 수 있습니다.  
샌드박스가 편하긴 하지만 설정 인터페이스 하나에 모든 매핑 함수를 몰아 넣고 싶지는 않으시겠죠?

#### 🔍 샌드박스 승격 가이드 찾기

샌드박스 승격 가이드는 아래 경로에서 찾을 수 있습니다.  
> /build/generated/ksp/main/resources/promoteSandboxGuide.txt

*하나 이상의 샌드박스 매퍼를 정의한 경우에만 이 파일이 생성됩니다.*

#### 📋 가이드 확인 및 승격하기

가이드 내용을 먼저 확인해 봅시다.

```text
======================== [KursMapper 가이드] ========================

샌드박스 함수를 정식 매핑 함수로 승격하려면 아래 내용을 참고해서 승격해주세요.

1. 매퍼를 생성합니다.
    - 매퍼가 위치할 패키지: kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin
    - 파일명: IntMapper.kt
    - 아래 내용의 매퍼 파일을 생성해주세요.
        @JvmInline
        value class IntMapper(val source: Int)
    - 참고) source FQCN: kotlin.Int

2. 매퍼에 함수를 추가해주세요.
    - 매퍼 FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin.IntMapper
    - 아래 내용의 함수를 추가해주세요.
        fun asString(): String { return PersistenceMapperConfig.SandboxExample(source).test() }
    - 참고) source FQCN: kotlin.Int
    - 참고) target FQCN: kotlin.String

3. IDE의 Go to Declaration 기능을 이용하여 샌드박스에 정의했던 매핑 코드를 복사해서 정식 매핑 함수 내용으로 교체하세요.
    fun asString(): String {
      TODO("이 곳에 '{ return PersistenceMapperConfig.SandboxExample(source).test() }' 대신 붙여넣으세요.")
    }

4. 아래 샌드박스 함수를 삭제해주세요. 샌드박스 매퍼 클래스는 삭제하지 않아도 됩니다.
    @JvmInline
    value class IntMapper(val source: Int) {
      fun asString(): String
    }
    - 중요) 정식 매핑 함수로 승격한 후에도 기존 샌드박스 매핑 함수를 삭제하지 않으면 삭제 가이드와 함께 컴파일 에러가 발생합니다.
    - 중요) 이것은 어떤 매핑 함수가 현재 적용되고 있는지에 대한 미래 혼란을 방지하기 위함입니다.
    - 참고) source FQCN: kotlin.Int
    - 참고) target FQCN: kotlin.String
    - 참고) KursMapper는 샌드박스 정의에 사용한 클래스명, 변수명, 함수명은 사용하지 않으므로, 실제 설정 인터페이스에 정의된 내용과 위 내용이 다를 수 있습니다.

5. 샌드박스를 승격한 뒤 다시 빌드해주세요.

=====================================================================
```

이 가이드는 여러분이 애써 고민하지 않아도 샌드박스에 있는 매핑 함수를 어디에, 어떻게 넣어야 하는지 모두 알려주기 때문에,  
[빠른 시작](#-빠른-시작)부터 함께해 온 여러분이라면 이 가이드를 따라하는 데 어려움이 있지 않을 거라고 믿습니다.

---

### 🧪 샌드박스 실전 예제

보다 실전적인 샌드박스 예제를 함께 봅시다.

먼저 [빠른 시작](#-빠른-시작)에서 만들었던 아래의 매퍼를 봐 주세요.

```kotlin
@JvmInline
value class UserMapper(val source: User) {
  fun asUserRecord(timeZone: TimeZone): UserRecord {
    return UserRecord(
      id = source.id.persistenceMapper().asJavaUUID(),
      age = source.age,
      name = source.name,
      createAt = source.createAt.toLocalDateTime(timeZone).toJavaLocalDateTime(),
      traits = source.traits.persistenceMapper().asStringList(),
    )
  }
}
```

혹시 이 매퍼에서 불편한 점을 찾으셨나요?

그렇습니다. `User.createAt: Instant` $\rightarrow$ `UserRecord.createAt: java.time.LocalDateTime` 변환은 사실 더 작은 매핑 함수로 추출할 수 있다는 점입니다.

현재 예제에서처럼 코틀린 datetime과 영속성 ORM을 위한 java.time을 함께 쓰는 코드베이스에서는 `Instant` $\rightarrow$ `java.time.LocalDateTime` 매핑은 숨쉬듯이 발생할 수 있는 매핑 함수입니다.  
그런데 이것을 단지 **KursMapper**가 자동으로 연결해 주지 않는다고 해서 포기하고 매번 구현하는 것이 정말 옳은 일일까요?  

이 실전 예제에서는 이 매핑을 샌드박스를 통해 구현하고 그 구현이 어떻게 적용되는지 확인해 보겠습니다.

---

#### 💻 샌드박스 매퍼 정의

먼저, 지금 머리 속에 있는 매핑 함수를 샌드박스에 정의한 다음 빌드합니다.

```kotlin
class InstantMapper(val source: Instant) {
  fun asJavaLocalDateTime(timeZone: TimeZone): java.time.LocalDateTime {
    return source.toLocalDateTime(timeZone).toJavaLocalDateTime()
  }
}
```
```kotlin
// Generated by KursMapper. DO NOT EDIT DIRECTLY.
package kr.urbansoft.example.adapter.infra.persistence.mapper.time

import java.time.LocalDateTime
import kotlin.jvm.JvmInline
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kr.urbansoft.example.adapter.infra.persistence.mapper.PersistenceMapperConfig
import kr.urbansoft.example.adapter.infra.persistence.mapper.time.asJavaLocalDateTime
import kr.urbansoft.example.adapter.infra.persistence.mapper.time.persistenceMapper

@JvmInline
public value class InstantMapper(
  public val source: Instant,
)

public fun Instant.persistenceMapper(): InstantMapper = InstantMapper(this)

public fun InstantMapper.asJavaLocalDateTime(timeZone: TimeZone): LocalDateTime = PersistenceMapperConfig.InstantMapper(source).asJavaLocalDateTime(timeZone = timeZone)

public fun InstantMapper.asJavaNullableLocalDateTime(timeZone: TimeZone): LocalDateTime? = source.persistenceMapper().asJavaLocalDateTime(timeZone = timeZone)
```

잘 적용되었습니다. **KursMapper**가 똑똑하게 `Instant` $\rightarrow$ `java.time.LocalDateTime?`를 자동 구현해 놓은 것도 보이네요.

그런데 저는 아직도 불편합니다.  
이 매핑 함수는 더 쪼갤 수 있습니다.  
즉 `kotlin.time.Instant` $\rightarrow$ `kotlinx.datetime.LocalDateTime`과 `kotlinx.datetime.LocalDateTime` $\rightarrow$ `java.time.LocalDateTime`으로 말이죠!

추가로 샌드박스 매퍼를 정의한 후 빌드합니다.

```kotlin
class InstantMapper(val source: Instant) {
  fun asJavaLocalDateTime(timeZone: TimeZone): java.time.LocalDateTime {
    return source.toLocalDateTime(timeZone).toJavaLocalDateTime()
  }
  
  fun asLocalDateTime(timeZone: TimeZone): LocalDateTime = source.toLocalDateTime(timeZone)
}

class LocalDateTimeMapper(val source: LocalDateTime) {
  fun asJavaLocalDateTime(): java.time.LocalDateTime = source.toJavaLocalDateTime()
}
```

흠. 이제 충분히 쪼개진 것 같지만 처음 정의했던 `InstantMapper.asJavaLocalDateTime` 함수의 구현을 조금 만져서 방금 정의한 `InstantMapper.asLocalDateTime`를 사용하도록 하면 더 좋을 것 같습니다.

```kotlin
class InstantMapper(val source: Instant) {
  fun asJavaLocalDateTime(timeZone: TimeZone): java.time.LocalDateTime {
    return source.persistenceMapper().asLocalDateTime(timeZone).toJavaLocalDateTime()
  }

  fun asLocalDateTime(timeZone: TimeZone): LocalDateTime = source.toLocalDateTime(timeZone)
}

class LocalDateTimeMapper(val source: LocalDateTime) {
  fun asJavaLocalDateTime(): java.time.LocalDateTime = source.toJavaLocalDateTime()
}
```

좋습니다. 방금 `persistenceMapper` 컨텍스트에 코틀린 Instant를 자바 LocalDateTime으로 변환하는 능력을 부여하셨습니다!  
앞으로는 **KursMapper**가 `kotlin.time.Instant` $\rightarrow$ `java.time.LocalDateTime` 변환을 발견하면 다시 가르치지 않아도 알아서 잘 변환할 것입니다.

이 상태에서 다시 한 번 `UserMapper`를 살펴보죠.

```kotlin
@JvmInline
value class UserMapper(val source: User) {
  fun asUserRecord(timeZone: TimeZone): UserRecord {
    return UserRecord(
      id = source.id.persistenceMapper().asJavaUUID(),
      age = source.age,
      name = source.name,
      createAt = source.createAt.toLocalDateTime(timeZone).toJavaLocalDateTime(),
      traits = source.traits.persistenceMapper().asStringList(),
    )
  }
}
```

여기서 방금 전 샌드박스에 정의한 매핑함수를 사용하도록 직접 코딩할 수도 있습니다.

```kotlin
@JvmInline
value class UserMapper(val source: User) {
  fun asUserRecord(timeZone: TimeZone): UserRecord {
    return UserRecord(
      id = source.id.persistenceMapper().asJavaUUID(),
      age = source.age,
      name = source.name,
      createAt = source.createAt.persistenceMapper().asJavaLocalDateTime(timeZone),
      traits = source.traits.persistenceMapper().asStringList(),
    )
  }
}
```

이렇게 말이죠. 하지만 이번에는 **KursMapper**의 자동 연결을 믿어 봅시다. 과감하게 `asUserRecord` 매핑함수를 주석처리 한 뒤 빌드해 주세요.

```kotlin
@JvmInline
value class UserMapper(val source: User) {
  //fun asUserRecord(timeZone: TimeZone): UserRecord {
  //  return UserRecord(
  //    id = source.id.persistenceMapper().asJavaUUID(),
  //    age = source.age,
  //    name = source.name,
  //    createAt = source.createAt.persistenceMapper().asJavaLocalDateTime(timeZone),
  //    traits = source.traits.persistenceMapper().asStringList(),
  //  )
  //}
}
```

앗! 가이드가 떴습니다. 그런데 내용이 지금까지 봤던 것과는 조금 다릅니다. 

```text
======================== [KursMapper 가이드] ========================

함수에 인자가 부족합니다. 아래 내용을 참고해서 인자를 추가해주세요.

1. 설정 인터페이스 파일을 엽니다.
   - 설정 인터페이스 FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.PersistenceMapperConfig

2. 목적 정의 함수의 인자를 최종 인자로 변경하거나, 최종 인자가 적용된 함수로 대체해주세요.
   - 목적 정의 함수
        fun User.asUserRecord(): UserRecord
   - 참고) 최종 인자: timeZone: TimeZone
        fun User.asUserRecord(timeZone: TimeZone): UserRecord
   - 참고) source FQCN: kr.urbansoft.example.domain.model.example.User
   - 참고) target FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord
   - 참고) 최종 인자 FQCN
      · timeZone: kotlinx.datetime.TimeZone
   - 참고) KursMapper는 목적 정의에 사용한 함수명은 사용하지 않으므로, 실제 설정 인터페이스에 정의된 함수명은 위 함수명과 다를 수 있습니다.
   - 참고) 인자 부족 문제는 이 매핑 함수가 다른 매핑 함수를 호출하기 위해 필요한 인자가 부족할 때 발생합니다.

3. 목적 정의 함수를 변경한 뒤 다시 빌드해주세요.

=====================================================================
```

이것은 함수 인자 부족 가이드입니다.  
기존의 `fun User.asUserRecord(): UserRecord` 목적 정의에 `timeZone: TimeZone` 인자를 추가해 달라고 요청하고 있습니다.  

>**KursMapper**는 작은 매핑 함수가 추가적인 인자를 요구하면, 그것을 사용하는 큰 매핑 함수로 인자를 자동으로 전파합니다.  
>그런데 인자를 전파할 큰 매핑 함수가 여러분이 정의한 목적 정의인 경우에는 전파에 실패하고 함수 인자 부족 가이드를 띄웁니다.  
>이 동작은 코드의 주인은 여러분이라는 **KursMapper**의 철학 때문입니다.  
>여러분이 직접 작성한 코드는 **KursMapper**에게 절대적이며 감히 그 의도를 해석해서 수정 적용할 수 없는 대상입니다.

설정 인터페이스로 이동해서 시키는 대로 한 뒤, 빌드해 봅시다.

```kotlin
@Suppress("unused", "EXTENSION_SHADOWED_BY_MEMBER")
@KursContext(contextName = "persistenceMapper", rootPackageName = "kr.urbansoft", guideLanguage = GuideLanguage.KO_KR)
interface PersistenceMapperConfig {
  // 목적 정의 생략

  fun User.asUserRecord(timeZone: TimeZone): UserRecord

  // 샌드박스 생략
}
```

이제 빌드에 성공했습니다.
과연 어떻게 구현되었는지 확인해 보죠.

```kotlin
// Generated by KursMapper. DO NOT EDIT DIRECTLY.
package kr.urbansoft.example.adapter.infra.persistence.mapper.example

import kotlinx.datetime.TimeZone
import kr.urbansoft.example.adapter.infra.persistence.mapper.collections.asStringList
import kr.urbansoft.example.adapter.infra.persistence.mapper.collections.persistenceMapper
import kr.urbansoft.example.adapter.infra.persistence.mapper.time.asJavaLocalDateTime
import kr.urbansoft.example.adapter.infra.persistence.mapper.time.persistenceMapper
import kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord
import kr.urbansoft.example.domain.model.example.User

public fun User.persistenceMapper(): UserMapper = UserMapper(this)

public fun UserMapper.asNullableUserRecord(timeZone: TimeZone): UserRecord? = source.persistenceMapper().asUserRecord(timeZone = timeZone)

public fun UserMapper.asUserRecord(timeZone: TimeZone): UserRecord =
  UserRecord(
    id = source.id.persistenceMapper().asJavaUUID(),
    age = source.age,
    name = source.name,
    createAt = source.createAt.persistenceMapper().asJavaLocalDateTime(timeZone = timeZone),
    traits = source.traits.persistenceMapper().asStringList(),
  )
```

**KursMapper**가 여러분이 부여한 변환 능력을 사용해서 수동으로 구현해야만 했던 `UserMapper.asUserRecord` 매핑 함수를 자동으로 구현했습니다!  

**KursMapper**에게 새로운 능력을 가르쳐 주셔서 고맙습니다!  
이런 방식으로 변환 능력을 가르치면 가르칠수록 **KursMapper**는 여러분을 더욱 충실하게 돕는 똑똑한 조력자가 될 것입니다.

샌드박스에 작성한 이 기능이 마음에 든다면 [샌드박스 매핑 함수를 정식 매핑 함수로 승격하기](#-샌드박스-매핑-함수를-정식-매핑-함수로-승격하기)를 참고해서 정식 매퍼로 승격시켜 보세요.

---

## 💭 KursMapper의 철학

여기까지 오셨다면 아마 눈치채셨을 것입니다.  
KursMapper는 기존 매퍼 라이브러리들과 조금 다른 관점에서 출발했습니다.

기존의 수많은 매퍼 라이브러리들은 훌륭하지만, 때로는 개발자를 주객전도의 늪에 빠뜨리곤 합니다.

매핑 라이브러리의 한계 때문에 멀쩡한 도메인 엔티티에 기본 생성자를 억지로 뚫거나 불필요한 Setter를 열어두어야 했고, 가독성도 떨어지는 복잡한 어노테이션 속성들을 구글링하며 공부해야 했습니다. 심지어 복잡한 매핑이 막힐 때는 라이브러리가 제공하는 독자적인 내부 DSL이나 표현식을 배우느라 정작 중요한 기능을 만드는 시간보다 라이브러리와 싸우는 시간이 더 많아지기도 합니다.

> **"코드를 매퍼에 맞추지 마세요. 매퍼가 코드에 맞춰야 합니다."**

**KursMapper**는 이러한 문제의식에서 출발했습니다.

우리가 가장 잘하는 것은 **코틀린 코드를 짜는 것**입니다. 라이브러리가 모든 것을 마법처럼 알아서 해주는 '블랙박스'가 되기보다는, 개발자가 매핑 코드를 제어하는 주도권을 온전히 가지게 하고 싶었습니다.

자동화는 오직 명확하고 확실할 때만 안전하게 지원하고, 복잡하고 까다로운 비즈니스 매핑은 개발자가 코틀린 코드로 직접 조절하되, **KursMapper**는 그 과정에서 **"어디가 비어있고 무엇을 채워야 하는지"를 알려주는 가장 친절한 조력자** 역할을 수행하도록 설계했습니다.

KursMapper가 여러분의 코드베이스에서 매핑으로 인한 스트레스를 덜어내고, 순수 코틀린 개발의 즐거움을 지켜드릴 수 있기를 바랍니다.

## 💬 피드백

**KursMapper**를 사용해 보다가 막히는 부분이나 궁금한 점이 있다면 언제든지 Issue를 남겨주세요.

여러분의 의견을 듣고 싶습니다.