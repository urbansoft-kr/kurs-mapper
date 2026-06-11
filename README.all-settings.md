<h1 align="center">KursMapper - All Settings</h1>

*Read this in other languages: [English](README.md), [🇰🇷 한국어](README.ko.md)*

> **"Find every KursMapper setting in one place."**
>
> For most projects, the default settings are all you'll ever need.
>
> However, if you'd like to fine-tune KursMapper to better match your project's structure or your team's coding conventions, this document will guide you through the available options.

---

## 📄 Table of Contents

* [Complete Configuration Example](#-complete-configuration-example)
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
* [Configuration Interface Components](#-configuration-interface-components)
* [Configuration Priority](#-configuration-priority)

---

# ⚙️ Complete Configuration Example

Let's start by looking at a fully configured example.

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
  guideLanguage = GuideLanguage.EN_US,
)
interface PersistenceMapperConfig {
  // Type rule
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

  // Mapping intent
  fun User.asUserRecord(timeZone: TimeZone)

  // Sandbox
  class SandboxExample(val value: Instant) {
    fun asLocalDateTime(timeZone: TimeZone): LocalDateTime
  }
}

```

Most projects only need the following configuration:

```kotlin
@KursContext(
  contextName = "persistenceMapper",
  rootPackageName = "kr.urbansoft",
)
```

The remaining settings are used when you want to customize naming conventions or fine-tune rules for specific types.

---

# 🏷️ KursContext

`@KursContext` is the entry point of **KursMapper**.

---

## contextName

```kotlin
contextName = "persistenceMapper"
```

The name of the context.

It is also used when generating extension function names automatically.

```kotlin
user.persistenceMapper().asUserRecord()
```

This setting is required.

---

## rootPackageName

```kotlin
rootPackageName = "kr.urbansoft"
```

The root package of your codebase.

**KursMapper** uses this value as the starting point for discovering and analyzing your code.

This setting is required.

---

## mapperNameGlobalSuffix

```kotlin
mapperNameGlobalSuffix = "Mapper"
```

The suffix appended to all mapper class names.

Default:

```text
Mapper
```

Examples:

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

The name of the property that stores the source object inside a mapper.

Default:

```kotlin
val source: User
```

Generated code is also written using this name.

---

## mappingFunctionNameVerb

```kotlin
mappingFunctionNameVerb = "as"
```

The verb used in generated mapping function names.

Default:

```text
as
```

Examples:

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

Applies custom rules to specific packages.

For example, the default configuration applies the following rule to Java types:

```kotlin
KursPackageRule(
  packageName = "java",
  rule = KursRule(
    mapperNamePrefix = "Java",
    mappingFunctionNamePrefix = "Java",
  ),
)
```

As a result, names such as the following are generated:

```text
UUID
↓
JavaUUIDMapper

asJavaUUID()
```

---

## guideLanguage

```kotlin
guideLanguage = GuideLanguage.EN_US
```

The language used for generated guide files.

Supported languages:

```kotlin
GuideLanguage.KO_KR
GuideLanguage.EN_US
```

Default:

```kotlin
GuideLanguage.EN_US
```

---

# 📦 KursPackageRule

A rule that applies to an entire package.

```kotlin
KursPackageRule(
  packageName = "java",
  rule = KursRule(...)
)
```

---

## packageName

The package to which the rule should be applied.

The rule applies to the specified package and all of its subpackages.

Example:

```kotlin
packageName = "java"
```

Applies to:

```text
java.util.UUID
java.time.LocalDateTime
java.time.Instant
...
```

---

## rule

The rule to apply.

Uses a `KursRule`.

---

# 🎯 KursRule

A rule that applies to a specific type or package.

---

## mapperSubPackageCreationMode

### AUTO

The default mode.

Uses the last segment of the source object's package name.

Example:

```text
kotlin.String
↓
mapper.kotlin.StringMapper
```

### FLAT

Does not create subpackages.

```text
mapper.StringMapper
```

### MANUAL

Uses a package name specified explicitly by the user.

```kotlin
mapperSubPackageCreationMode = MapperSubPackageCreationMode.MANUAL
mapperSubPackageName = "common"
```

↓

```text
mapper.common.StringMapper
```

> **💡 Note**  
> `mapperSubPackageName` should only be specified when `mapperSubPackageCreationMode` is set to `MANUAL`.
>
> Providing a value for `mapperSubPackageName` when using `AUTO` or `FLAT` will result in a build error.


---

## mapperSubPackageName

The package name to use when `MANUAL` mode is selected.

```kotlin
mapperSubPackageName = "common"
```

> **💡 Note**  
> Dots (`.`) are not allowed.

---

## mapperNamePrefix

```kotlin
mapperNamePrefix = "Java"
```

Applied when the source type matches a package rule or type rule.

Example:

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

Completely replaces the source class name.

Example:

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

Example:

```text
UserEntityMapper
```

---

## mappingFunctionNamePrefix

```kotlin
mappingFunctionNamePrefix = "Java"
```

Applied when the target type matches a package rule or type rule.

Example:

```text
asJavaUUID()
```

---

## mappingFunctionName

Completely replaces the target class name.

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

# 🧩 Configuration Interface Components

So far, we've focused on annotation-based configuration.

However, a configuration interface can contain more than just annotations.

```kotlin id="kkxqca"
interface PersistenceMapperConfig {
  // Type rule
  @KursRule(...)
  fun User.rule()

  // Mapping intent
  fun User.asUserRecord(timeZone: TimeZone): UserRecord

  // Sandbox
  class InstantMapper(val source: Instant) {
    fun asLocalDateTime(timeZone: TimeZone): LocalDateTime
  }
}
```

A configuration interface can contain three types of components:

1. Type Rules
2. Mapping Intents
3. Sandboxes

---

## Type Rules

A type rule defines naming rules for a specific type.

```kotlin id="z6u3z9"
@KursRule(
  mapperNamePrefix = "Java",
  mappingFunctionNamePrefix = "Java",
)
fun UUID.rule()
```

A type rule is applied whenever the type appears as either a source type or a target type.

```text id="4bskn0"
UUID
↓
JavaUUIDMapper

asUUID()
↓
asJavaUUID()
```

> **💡 Note**  
> The function name must be `rule` in order to be recognized as a type rule.

---

## Mapping Intents

A mapping intent declares a final mapping function that KursMapper is expected to provide.

```kotlin id="w8vlcz"
fun User.asUserRecord(timeZone: TimeZone): UserRecord

fun UserRecord.asUser(): User
```

KursMapper builds its mapping graph from mapping intents and generates any required mappers automatically.

---

## Sandbox

A sandbox is a place to define temporary mapping functions.

```kotlin id="tch7yq"
class InstantMapper(val source: Instant) {
  fun asLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return source.toLocalDateTime(timeZone)
  }
}
```

Mapping functions defined in a sandbox can be freely used when implementing other mapping functions.

Once a sandbox mapping function proves to be generally useful, it can be promoted to a production mapper.

For more information about sandboxes, see the **"🌱 About Sandboxes"** chapter in the README.

---

# 🔝 Configuration Priority

Multiple rules may apply to the same target.

When that happens, the following priority order is used:

```text
Type Rule (@KursRule)
↓
Package Rule (@KursPackageRule)
↓
KursMapper Default Rules
```

In other words, if a type rule exists, the package rule is ignored.

---

## 💡 Do I Really Need to Memorize All of These Settings?

No.

For most projects, the following configuration is all you need:

```kotlin id="z2vjj4"
@KursContext(
  contextName = "persistenceMapper",
  rootPackageName = "kr.urbansoft",
)
```

The need to customize these settings is less common than you might think.

Start with the defaults first.

If the day comes when you need more control, this document will still be here waiting for you. 😄
