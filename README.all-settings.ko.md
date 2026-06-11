<h1 align="center">KursMapper - All Settings</h1>

*다른 언어로도 이 문서의 내용을 읽을 수 있습니다: [English](README.md), [🇰🇷 한국어](README.ko.md)*

> **"KursMapper의 모든 설정을 한 곳에서 확인하세요."**
>
> 대부분의 프로젝트는 기본 설정만으로 충분합니다.
>
> 하지만 프로젝트의 구조나 팀의 코딩 규칙에 맞게 세밀하게 조정하고 싶다면 이 문서를 참고하세요.

---

## 📄 목차

* [전체 설정 예제](#-전체-설정-예제)
* [KursContext](#-kurscontext)
    * [contextName](#contextname)
    * [rootPackageName](#rootpackagename)
    * [mapperNameGlobalSuffix](#mappernameglobalsuffix)
    * [mapperSourceVariableName](#mappersourcevariablename)
    * [mappingFunctionNameVerb](#mappingfunctionnameverb)
    * [packageRules](#packagerules)
    * [guideLanguage](#guidelanguage)
* [KursPackageRule](#-kurspackagerule)
* [KursRule](#-kursrule)
* [설정 인터페이스 구성 요소](#-설정-인터페이스-구성-요소)
* [설정 우선순위](#-설정-우선순위)

---

# ⚙️ 전체 설정 예제

먼저 전체 설정이 어떤 모습인지 한 번 살펴봅시다.

```kotlin
@KursContext(
  contextName = "persistenceMapper",
  rootPackageName = "kr.urbansoft",
  mapperNameGlobalSuffix = "Mapper",
  mapperSourceVariableName = "source",
  mappingFunctionNameVerb = "as",
  packageRules =
    [
      KursPackageRule(
        packageName = "java",
        rule =
          KursRule(
            mapperSubPackageCreationMode = MapperSubPackageCreationMode.AUTO,
            mapperSubPackageName = "",
            mapperNamePrefix = "Java",
            mapperName = "",
            mapperNameSuffix = "",
            mappingFunctionNamePrefix = "Java",
            mappingFunctionName = "",
            mappingFunctionNameSuffix = "",
          ),
      )
    ],
  guideLanguage = GuideLanguage.KO_KR,
)
interface PersistenceMapperConfig {
  // 타입 규칙
  @KursRule(
    mapperSubPackageCreationMode = MapperSubPackageCreationMode.AUTO,
    mapperSubPackageName = "",
    mapperNamePrefix = "Java",
    mapperName = "",
    mapperNameSuffix = "",
    mappingFunctionNamePrefix = "Java",
    mappingFunctionName = "",
    mappingFunctionNameSuffix = "",
  )
  fun User.rule()

  // 목적 정의
  fun User.asUserRecord(timeZone: TimeZone)

  // 샌드박스
  class SandboxExample(val value: Instant) {
    fun asLocalDateTime(timeZone: TimeZone): LocalDateTime
  }
}

```

대부분의 프로젝트는 아래 설정만으로 충분합니다.

```kotlin
@KursContext(
  contextName = "persistenceMapper",
  rootPackageName = "kr.urbansoft",
)
```

나머지 설정은 이름 규칙을 변경하거나 특정 타입에 대한 규칙을 세밀하게 조정하고 싶을 때 사용합니다.

---

# 🏷️ KursContext

`@KursContext`는 **KursMapper**의 시작점입니다.

---

## contextName

```kotlin
contextName = "persistenceMapper"
```

컨텍스트 이름입니다.

자동 생성되는 확장 함수 이름에도 사용됩니다.

```kotlin
user.persistenceMapper().asUserRecord()
```

필수 설정입니다.

---

## rootPackageName

```kotlin
rootPackageName = "kr.urbansoft"
```

사용자 코드베이스의 루트 패키지입니다.

**KursMapper**는 이 값을 기준으로 사용자 코드를 탐색합니다.

필수 설정입니다.

---

## mapperNameGlobalSuffix

```kotlin
mapperNameGlobalSuffix = "Mapper"
```

모든 매퍼 클래스 이름 뒤에 붙는 suffix입니다.

기본값:

```text
Mapper
```

예시:

```text
UserMapper
StringMapper
MeetingRoomMapper
```



---

## mapperSourceVariableName

```kotlin
mapperSourceVariableName = "source"
```

매퍼 내부에서 원본 객체를 저장하는 프로퍼티 이름입니다.

기본값:

```kotlin
val source: User
```

생성된 코드도 이 이름을 기준으로 작성됩니다.

---

## mappingFunctionNameVerb

```kotlin
mappingFunctionNameVerb = "as"
```

매핑 함수의 동사 부분입니다.

기본값:

```text
as
```

예시:

```kotlin
asUser()
asUserRecord()
asString()
```



---

## packageRules

```kotlin
packageRules = [...]
```

특정 패키지에 대해 별도의 규칙을 적용합니다.

예를 들어 기본 설정은 Java 타입에 대해 다음 규칙을 적용합니다.

```kotlin
KursPackageRule(
  packageName = "java",
  rule = KursRule(
    mapperNamePrefix = "Java",
    mappingFunctionNamePrefix = "Java",
  ),
)
```

따라서:

```text
UUID
↓
JavaUUIDMapper

asJavaUUID()
```

와 같은 이름이 생성됩니다.

---

## guideLanguage

```kotlin
guideLanguage = GuideLanguage.KO_KR
```

가이드 파일 언어입니다.

지원 언어:

```kotlin
GuideLanguage.KO_KR
GuideLanguage.EN_US
```

기본값:

```kotlin
GuideLanguage.EN_US
```

---

# 📦 KursPackageRule

특정 패키지 전체에 적용되는 규칙입니다.

```kotlin
KursPackageRule(
  packageName = "java",
  rule = KursRule(...)
)
```

---

## packageName

규칙을 적용할 패키지입니다.

해당 패키지와 모든 하위 패키지에 적용됩니다.

예시:

```kotlin
packageName = "java"
```

적용 대상:

```text
java.util.UUID
java.time.LocalDateTime
java.time.Instant
...
```



---

## rule

적용할 규칙입니다.

`KursRule`을 사용합니다.

---

# 🎯 KursRule

특정 타입 또는 특정 패키지에 적용되는 규칙입니다.

---

## mapperSubPackageCreationMode

### AUTO

기본값입니다.

원본 객체 패키지의 마지막 세그먼트를 사용합니다.

예시:

```text
kotlin.String
↓
mapper.kotlin.StringMapper
```

### FLAT

서브 패키지를 생성하지 않습니다.

```text
mapper.StringMapper
```

### MANUAL

사용자가 직접 지정한 패키지를 사용합니다.

```kotlin
mapperSubPackageCreationMode = MapperSubPackageCreationMode.MANUAL
mapperSubPackageName = "common"
```

↓

```text
mapper.common.StringMapper
```

> **💡 주의**  
> mapperSubPackageName는 mapperSubPackageCreationMode가 MANUAL일 때만 값을 입력해야 합니다.  
> AUTO나 FLAT일 때 mapperSubPackageName에 값을 입력하면 빌드 오류가 발생합니다.

---

## mapperSubPackageName

`MANUAL` 모드에서 사용할 패키지명입니다.

```kotlin
mapperSubPackageName = "common"
```

> **💡 주의**  
> 점(`.`)은 사용할 수 없습니다.

---

## mapperNamePrefix

```kotlin
mapperNamePrefix = "Java"
```

원본 객체가 패키지 혹은 타입 규칙의 대상일 때 적용됩니다.

예시:

```text
UUID
↓
JavaUUIDMapper
```

---

## mapperName

```kotlin
mapperName = "Identifier"
```

원본 클래스명을 완전히 대체합니다.

예시:

```text
User
↓
IdentifierMapper
```

---

## mapperNameSuffix

```kotlin
mapperNameSuffix = "Entity"
```

예시:

```text
UserEntityMapper
```

---

## mappingFunctionNamePrefix

```kotlin
mappingFunctionNamePrefix = "Java"
```

대상 객체가 패키지 혹은 타입 규칙의 대상일 때 적용됩니다.

예시:

```text
asJavaUUID()
```

---

## mappingFunctionName

대상 클래스명을 완전히 대체합니다.

```kotlin
mappingFunctionName = "Identifier"
```

↓

```text
asIdentifier()
```

---

## mappingFunctionNameSuffix

```kotlin
mappingFunctionNameSuffix = "Value"
```

↓

```text
asUserValue()
```

---

# 🧩 설정 인터페이스 구성 요소

지금까지는 어노테이션 설정에 대해 알아보았습니다.

하지만 실제 설정 인터페이스에는 어노테이션 외에도 다양한 요소를 정의할 수 있습니다.

```kotlin
interface PersistenceMapperConfig {
  // 타입 규칙
  @KursRule(...)
  fun User.rule()

  // 목적 정의
  fun User.asUserRecord(timeZone: TimeZone): UserRecord

  // 샌드박스
  class InstantMapper(val source: Instant) {
    fun asLocalDateTime(timeZone: TimeZone): LocalDateTime
  }
}
```

설정 인터페이스 내부에는 크게 세 가지 요소를 정의할 수 있습니다.

1. 타입 규칙
2. 목적 정의
3. 샌드박스

---

## 타입 규칙

타입 규칙은 특정 타입에 대한 이름 생성 규칙을 정의합니다.

```kotlin
@KursRule(
  mapperNamePrefix = "Java",
  mappingFunctionNamePrefix = "Java",
)
fun UUID.rule()
```

타입 규칙은 해당 타입이 원본 객체 또는 대상 객체로 등장할 때 적용됩니다.

```text
UUID
↓
JavaUUIDMapper

asUUID()
↓
asJavaUUID()
```

> **💡 주의**  
> 함수 이름이 반드시 `rule`이어야 타입 규칙으로 인식합니다.

---

## 목적 정의

목적 정의는 KursMapper가 구현해야 할 최종 매핑 함수를 선언하는 영역입니다.

```kotlin
fun User.asUserRecord(timeZone: TimeZone): UserRecord

fun UserRecord.asUser(): User
```

KursMapper는 목적 정의를 기준으로 매핑 그래프를 구성하고 필요한 매퍼를 생성합니다.

---

## 샌드박스

샌드박스는 임시 매핑 함수를 정의하는 영역입니다.

```kotlin
class InstantMapper(val source: Instant) {
  fun asLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return source.toLocalDateTime(timeZone)
  }
}
```

샌드박스에서 정의한 매핑 함수는 다른 매핑 함수를 구현할 때 자유롭게 사용할 수 있습니다.

또한 충분히 범용적인 매핑 함수가 되었다면 정식 매퍼로 승격할 수도 있습니다.

샌드박스에 대한 자세한 내용은 README의 "🌱 샌드박스에 대하여" 챕터를 참고하세요.

---

# 🔝 설정 우선순위

동일한 대상에 여러 규칙이 적용될 수 있습니다.

이 경우 우선순위는 다음과 같습니다.

```text
타입 규칙 (@KursRule)
↓
패키지 규칙 (@KursPackageRule)
↓
KursMapper 기본 규칙
```

즉, 타입 규칙이 존재하면 패키지 규칙은 무시됩니다.

---

## 💡 정말 이 설정들을 모두 외워야 하나요?

아니요.

대부분의 프로젝트는 아래 설정만으로 충분합니다.

```kotlin
@KursContext(
  contextName = "persistenceMapper",
  rootPackageName = "kr.urbansoft",
)
```

설정을 커스터마이징해야 하는 경우는 생각보다 많지 않습니다.

먼저 기본 설정으로 시작해 보세요.

정말 필요한 순간이 오면, 그때 다시 이 문서를 찾아오시면 됩니다. 😄