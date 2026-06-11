<h1 align="center">KursMapper</h1>

<p align="center">
  <a href="https://central.sonatype.com/artifact/kr.urbansoft.kursmapper/kurs-mapper-annotation"><img src="https://img.shields.io/maven-central/v/kr.urbansoft.kursmapper/kurs-mapper-annotation?color=blue&style=flat-square" alt="Maven Central"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square" alt="License: MIT"></a>
</p>

*Read this in other languages: [English](README.md), [🇰🇷 한국어](README.ko.md)*

> **Don't make your code fit the mapper. The mapper should fit your code.**

When using a mapper library, you often run into various limitations. You may find yourself adding complex annotations to mapper interfaces, or even annotating your domain models. In some cases, the library's limitations force you to write the entire mapping logic by hand. And every once in a while, you might even forget that you already created a mapper interface and end up creating another one from scratch.

**KursMapper** aims to offer a new approach to solving these common problems.

---

## 📄 Table of Contents

- [Why Choose KursMapper?](#-why-choose-kursmapper)
- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Context-Driven Operation](#-context-driven-operation)
- [Mapper and Mapping Function Conventions](#-mapper-and-mapping-function-conventions)
- [How to Customize Generated Mapping Functions](#-how-to-customize-generated-mapping-functions)
- [About the Sandbox](#-about-the-sandbox)
- [The Philosophy of KursMapper](#-the-philosophy-of-kursmapper)
- [Feedback](#-feedback)

---

## 💡 Why Choose KursMapper?

> **"Stop fighting your library. Get the flexibility to stay in control, even when automation falls short."**

* 🚫 **Escape Annotation Hell**  
  There is only **one required annotation** and **two optional annotations**. **Seriously.**


* 🛠️ **Zero Additional DSLs**  
  Most configuration and all mapping implementations are written in plain Kotlin. Instead of changing your code to fit the library, teach the library how your code should work.


* 🎯 **Generate Only When It's Certain**  
  Automation is never forced. Sometimes it's simply faster to implement the mapping yourself than to force automation to handle it.


* 🧭 **Guide-Driven Development**  
  When automatic generation isn't possible, you don't just get an error message.
  You get step-by-step guidance on what mapper to create, where to put it, and what to name it.


* ⚡ **Built for Kotlin and IDE Autocompletion**  
  Mapping contexts are registered through Kotlin extension functions, making them highly discoverable through IDE autocompletion.


* 🔄 **Maximum Mapping Reusability**  
  Every mapping is processed as part of a graph and automatically registered within its context, allowing property-level mappings to be reused without additional configuration.


* 🔓 **Minimal Vendor Lock-In**  
  The mapper structure promoted by **KursMapper** works independently, even without **KursMapper** itself.

---

## 📦 Installation

Add the following to your `build.gradle.kts`:

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

## 🚀 Quick Start

Choose a package where your mappers will live.

```kotlin
package kr.urbansoft.example.adapter.infra.persistence.mapper // Example package
```

Create an interface like the following in that package.

```kotlin
package kr.urbansoft.example.adapter.infra.persistence.mapper

@Suppress("unused", "EXTENSION_SHADOWED_BY_MEMBER") // Optional annotation to take advantage of IDE syntax highlighting. Feel free to remove it if you prefer.
@KursContext(contextName = "persistenceMapper", rootPackageName = "kr.urbansoft") // Minimal configuration.
// contextName: The name used to identify the mappers collected in this package. KursMapper operates entirely around contexts.
// rootPackageName: The root package of your codebase.
interface PersistenceMapperConfig {
  // Extension functions defined inside a @KursContext interface represent mapping intents.
  fun MeetingRoom.asMeetingRoomRecord(): MeetingRoomRecord // Mapping intent: MeetingRoom -> MeetingRoomRecord

  fun MeetingRoomRecord.asMeetingRoom(): MeetingRoom // Mapping intent: MeetingRoomRecord -> MeetingRoom

  fun User.asUserRecord(): UserRecord // Mapping intent: User -> UserRecord

  fun UserRecord.asUser(): User // Mapping intent: UserRecord -> User
}
```

> 💡 Mapping Intent
>
> A mapping intent tells **KursMapper** what conversions your application needs.
>
> Using Kotlin extension function syntax, the receiver type represents the source object, while the return type represents the target object.
>
> The function names used for mapping intents exist only to distinguish definitions within the configuration interface.

Build your project. What? You're wondering if it's really time to build already?  
Yes, it is. You're already done with all the required configuration.

---

### ❔ How Do I Call a Mapper?

Mapping functions generated by **KursMapper** are typically called like this:

DI (Dependency Injection) is not required.

You don't need to locate a mapper manually, and IDE autocompletion makes it easy to discover available mapping functions.

```kotlin
source // The source object you want to convert
  .someContextMapper() // The contextName defined in your configuration
  .asTarget() // The final mapping function that performs the conversion
```

#### 📛 Why `asTarget()` Instead of `toTarget()`?

By default, **KursMapper** generates final mapping function names using the `as[TargetClassName]` pattern so that the target type is immediately recognizable at the call site.

* **Why use the `as` prefix?**  
  To completely avoid potential naming conflicts with top-level Kotlin/Java methods such as `toString()`. Because of how the internal mapping mechanism works, mappings may not behave correctly if a generated function ends up being named `toString`. For this reason, the safer `as` prefix is used by default.


* **Customizing the naming convention**  
  If your project follows a different naming convention and you would like to change this prefix or the generated naming pattern, see [KursMapper - All Settings](README.all-settings.md).

---

### 🛠️ Real-World Example

If you build the project using the configuration from the previous section, you'll probably get a build error.  
Don't worry — **that's expected.**  
When **KursMapper** detects missing mappings, it doesn't just throw an error and leave you guessing. Instead, it provides guidance on exactly what needs to be implemented and how to implement it.  
Before we look at the build output, let's first take a look at the objects we'll be using throughout this example.

As you can see, this is not a simple DTO-to-DTO example.  
We'll walk through a more realistic scenario from start to finish, including nested objects, collections, and custom types, to demonstrate how **KursMapper** helps you throughout the process.

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

Here is the build output.

As mentioned in the message itself, the guide is also written to the following file:

`/build/generated/ksp/main/resources/KursMapperGuide.txt`

Keep this location in mind, as it is always generated alongside the build message.

```text
[KursMapper]
Unresolved mapping functions detected.
Please refer to the KursMapper guide and resolve them.
You can find the guide in the build console
or at '/build/generated/ksp/main/resources/KursMapperGuide.txt'

======================== [KursMapper Guide] ========================

Mapping implementation is required. Please refer to the guide below.

1. Create a mapper.
    - Mapper Package: kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin
    - File Name: StringMapper.kt
    - Create a mapper file with the following content.
        @JvmInline
        value class StringMapper(val source: String)
    - Note) Source FQCN: kotlin.String

2. Add the following function to the mapper and implement the mapping logic.
    - Mapper FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin.StringMapper
    - Add the following function.
        fun asTrait(): Trait = TODO("Implement mapping logic here.")
    - Note) Source FQCN: kotlin.String
    - Note) Target FQCN: kr.urbansoft.example.domain.model.example.Trait

3. Rebuild the project after completing the implementation.

====================================================================

======================== [KursMapper Guide] ========================

Mapping implementation is required. Please refer to the guide below.

...truncated: 5 additional guide entries omitted...
```

**KursMapper** does not force automation, nor does it try to generate code that cannot be inferred at the current point in time.  
Instead, it tries to explain **what is missing and how to fill that gap in as much detail as possible.**  
Let's implement it once by following the guide.

```kotlin
package kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin

// import omitted

@JvmInline
value class StringMapper(val source: String) {
  fun asTrait(): Trait = Trait.valueOf(source)
}
```

If a `Redeclaration` conflict occurs in the `StringMapper` class while you're implementing it, don't worry — just keep going.  
The conflict will be resolved automatically when you build the project again.

Once the implementation is complete, build the project again.

Now there are 5 guide entries left.

```text
======================== [KursMapper Guide] ========================

Mapping implementation is required. Please refer to the guide below.

1. Create a mapper.
    - Mapper Package: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - File Name: TraitMapper.kt
    - Create a mapper file with the following content.
        @JvmInline
        value class TraitMapper(val source: Trait)
    - Note) Source FQCN: kr.urbansoft.example.domain.model.example.Trait

2. Add the following function to the mapper and implement the mapping logic.
    - Mapper FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.TraitMapper
    - Add the following function.
        fun asString(): String = TODO("Implement mapping logic here.")
    - Note) Source FQCN: kr.urbansoft.example.domain.model.example.Trait
    - Note) Target FQCN: kotlin.String

3. Rebuild the project after completing the implementation.

====================================================================
```

After implementing the guide, build the project again.

```kotlin
@JvmInline
value class TraitMapper(val source: Trait) {
  fun asString(): String = source.name
}
```

Here is the third guide.

```text
======================== [KursMapper Guide] ========================

Mapping implementation is required. Please refer to the guide below.

1. Create a mapper.
    - Mapper Package: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - File Name: MeetingRoomRecordMapper.kt
    - Create a mapper file with the following content.
        @JvmInline
        value class MeetingRoomRecordMapper(val source: MeetingRoomRecord)
    - Note) Source FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.MeetingRoomRecord

2. Add the following function to the mapper and implement the mapping logic.
    - Mapper FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.MeetingRoomRecordMapper
    - Add the following function.
        fun asMeetingRoom(): MeetingRoom { return MeetingRoom(id = source.id.persistenceMapper().asMeetingRoomId(), name = source.name, reservation = TODO("Implementation required")) }
    - Note) Source FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.MeetingRoomRecord
    - Note) Target FQCN: kr.urbansoft.example.domain.model.example.MeetingRoom

3. Rebuild the project after completing the implementation.

====================================================================
```

However, when you start implementing it, you'll notice that `MeetingRoomRecord.datetime` is a `java.time.LocalDateTime`, while `MeetingRoom.reservation.datetime` is a `kotlin.time.Instant`, which means a `TimeZone` is required!  
No problem.  
Simply add a parameter to the `asMeetingRoom` method and continue.

```kotlin
@JvmInline
value class MeetingRoomRecordMapper(val source: MeetingRoomRecord) {
  fun asMeetingRoom(timeZone: TimeZone): MeetingRoom {
    return MeetingRoom(
      id = source.id.persistenceMapper().asMeetingRoomId(),
      name = source.name,
      reservation =
        MeetingRoomReservation(
          datetime = source.datetime.toKotlinLocalDateTime().toInstant(timeZone), // You can accept a parameter and use it here!
          userIds = source.userIds, // This part still causes a compilation error!
        ),
    )
  }
}
```

Now it's time to implement `userIds`.  
Both sides use a `List`, so in this case the implementation is very straightforward:

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
          userIds = source.userIds.map { it.persistenceMapper().asUserId() }, // Use the UUID -> UserId mapping function generated automatically by KursMapper!
        ),
    )
  }
}
```

Here is the fourth guide.

```text
======================== [KursMapper Guide] ========================

Mapping implementation is required. Please refer to the guide below.

1. Create a mapper.
    - Mapper Package: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - File Name: MeetingRoomMapper.kt
    - Create a mapper file with the following content.
        @JvmInline
        value class MeetingRoomMapper(val source: MeetingRoom)
    - Note) Source FQCN: kr.urbansoft.example.domain.model.example.MeetingRoom

2. Add the following function to the mapper and implement the mapping logic.
    - Mapper FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.MeetingRoomMapper
    - Add the following function.
        fun asMeetingRoomRecord(): MeetingRoomRecord { return MeetingRoomRecord(id = source.id.persistenceMapper().asLong(), name = source.name, datetime = TODO("Implementation required"), userIds = TODO("Implementation required")) }
    - Note) Source FQCN: kr.urbansoft.example.domain.model.example.MeetingRoom
    - Note) Target FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.MeetingRoomRecord

3. Rebuild the project after completing the implementation.

====================================================================
```

```kotlin
@JvmInline
value class MeetingRoomMapper(val source: MeetingRoom) {
  fun asMeetingRoomRecord(timeZone: TimeZone): MeetingRoomRecord {
    return MeetingRoomRecord(
      id = source.id.persistenceMapper().asLong(),
      name = source.name,
      datetime = source.reservation.datetime.toLocalDateTime(timeZone).toJavaLocalDateTime(),
      userIds = source.reservation.userIds.map { it.persistenceMapper().asJavaUUID() } // Once again, use the UserId -> UUID mapping function generated automatically by KursMapper!
    )
  }
}
```

Here is the fifth guide.

```text
======================== [KursMapper Guide] ========================

Mapping implementation is required. Please refer to the guide below.

1. Create a mapper.
    - Mapper Package: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - File Name: UserRecordMapper.kt
    - Create a mapper file with the following content.
        @JvmInline
        value class UserRecordMapper(val source: UserRecord)
    - Note) Source FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord

2. Add the following function to the mapper and implement the mapping logic.
    - Mapper FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.UserRecordMapper
    - Add the following function.
        fun asUser(): User { return User(id = source.id.persistenceMapper().asUserId(), age = source.age, name = source.name, createAt = TODO("Implementation required"), traits = source.traits.persistenceMapper().asTraitSet()) }
    - Note) Source FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord
    - Note) Target FQCN: kr.urbansoft.example.domain.model.example.User

3. Rebuild the project after completing the implementation.

====================================================================
```

```kotlin
@JvmInline
value class UserRecordMapper(val source: UserRecord) {
  fun asUser(timeZone: TimeZone): User {
    return User(
      id = source.id.persistenceMapper().asUserId(),
      age = source.age,
      name = source.name,
      createAt = source.createAt.toKotlinLocalDateTime().toInstant(timeZone), // Let's handle this one by accepting a parameter as well.
      traits =
        source.traits
          .persistenceMapper()
          .asTraitSet(), // KursMapper has automatically generated a List<String> -> Set<Trait> mapping function using the String -> Trait mapping function you implemented by following the guide!
    )
  }
}

```

After resolving the final remaining guide, build the project once more.  
This time, the build succeeds, and all of the mapping intents you defined in the configuration interface have finally been fulfilled.

```text
======================== [KursMapper Guide] ========================

Mapping implementation is required. Please refer to the guide below.

1. Create a mapper.
    - Mapper Package: kr.urbansoft.example.adapter.infra.persistence.mapper.example
    - File Name: UserMapper.kt
    - Create a mapper file with the following content.
        @JvmInline
        value class UserMapper(val source: User)
    - Note) Source FQCN: kr.urbansoft.example.domain.model.example.User

2. Add the following function to the mapper and implement the mapping logic.
    - Mapper FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.example.UserMapper
    - Add the following function.
        fun asUserRecord(): UserRecord { return UserRecord(id = source.id.persistenceMapper().asJavaUUID(), age = source.age, name = source.name, createAt = TODO("Implementation required"), traits = source.traits.persistenceMapper().asStringList()) }
    - Note) Source FQCN: kr.urbansoft.example.domain.model.example.User
    - Note) Target FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord

3. Rebuild the project after completing the implementation.

====================================================================
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

Thank you for becoming a **KursMapper** user!

**KursMapper** builds larger mappings by composing **small, focused mapping implementations**.  
As a result, the more you use it, the less manual mapping code you'll need to write, while the amount of automatic wiring continues to grow.

We've put a lot of effort into making the guides as helpful as possible, and we hope they make your development experience smoother and more enjoyable.

---

### 🔎 Can I Trust the Generated Code?

**KursMapper** does not rely on runtime reflection or a hidden mapping engine.

Generated mapping functions are emitted as ordinary Kotlin source files under `/build/generated/ksp`, which means you can open, inspect, and debug them directly in your IDE.

If you're not satisfied with a generated implementation, you can simply add your own member function to the same mapper and override it.

In other words, **KursMapper's** generated code is not a black box. It's closer to a draft that you can review, modify, or replace whenever you want.

---

## 🌐 Context-Driven Operation

KursMapper operates with strict isolation at the **context level**. The `contextName` specified in your configuration interface acts as the key that opens a particular mapping context.

* **Flexible Context Design**  
  Depending on the size and structure of your project, you can group an entire persistence layer into a single context or split contexts more granularly by domain. The choice is entirely yours.


* **Context Isolation**  
  KursMapper **does not automatically connect mapping functions across different contexts.** Contexts are expected to remain independent. Of course, if needed, you're free to manually compose mappings by calling functions from another context yourself.


* **⚠️ Important — Package Structure Restriction**  
  Do not define another configuration interface inside the same package as a context configuration interface, or in any of its subpackages. Doing so may cause naming conflicts and compilation errors.

#### ❌ Incorrect Structure (Nested Contexts)

```text
infra/mapper/
  ├── PersistenceMapperConfig.kt  <- contextName = "persistenceMapper"
  └── user/
      └── UserMapperConfig.kt     <- Defining another context in a subpackage may cause problems.
```

#### ⭕ Correct Structure (Separated Contexts)

```text
infra/mapper/
  ├── persistence/
  │   └── PersistenceMapperConfig.kt  <- contextName = "persistenceMapper"
  └── external/
      └── ExternalApiMapperConfig.kt  <- contextName = "externalApiMapper"
```

---

## ✏️ Mapper and Mapping Function Conventions

To keep mapper management consistent, predictable, and easy to understand, **KursMapper** follows a strict set of conventions.

If you want to implement mapping functions manually, you must follow **all** of the rules below. Mappers and mapping functions that do not follow these conventions are **intentionally ignored**.

> **🛑 Wait! Don't hit Back or close this page!**
>
> We're not asking you to memorize all of these rules.
>
> In fact, there's a much better way to follow them without needing to remember them at all. Feel free to just skim through the structure for now!
>
> * If you'd like to jump straight to the good part, see [🌱 About the Sandbox](#-about-the-sandbox).

---

### 📂 Package Conventions

To prevent a single package from becoming overcrowded with mapper files, **KursMapper** provides the following package organization strategies:

```kotlin
enum class MapperSubPackageCreationMode {
  AUTO, // Default
  FLAT,
  MANUAL,
}
```

* **`AUTO` (default)**: Automatically creates a subpackage using the last segment of the source object's package name.
    * Example: `kotlin.String` → `kr.urbansoft.example.adapter.infra.mapper.persistence.kotlin.StringMapper`
    * Example: `kr.urbansoft.user.User` → `kr.urbansoft.example.adapter.infra.mapper.persistence.user.UserMapper`


* **`FLAT`**: Does not create subpackages. All mapper classes are placed directly in the same package as the configuration interface.


* **`MANUAL`**: Allows you to specify the package name explicitly.

---

### 📄 File and Class Naming Conventions

**KursMapper** determines mapper class names based on the source type being converted.

To take full advantage of Kotlin's strong null-safety system, **types with different nullability are treated as completely different source types**, even if they share the same underlying type.

* Example: If the source type is `String` → `StringMapper`
* Example: If the source type is `String?` → `NullableStringMapper`

#### 🧩 Class Name Composition Template

Class names are assembled using the following structure and can be customized if desired.

$$\text{[Prefix]} + \text{[Nullable]} + \text{[Mapper Name]} + \text{[Suffix]} + \text{[Global Suffix]}$$

* Prefix: Empty by default.
* Nullable: `Nullable` is added when the source type is nullable.
* Mapper Name: The source class name by default.
* Suffix: Empty by default.
* Global Suffix: `Mapper` by default.

> **💡 Recommendation**
>
> We recommend using the same name for the file and the mapper class, and placing **one mapper class per file**.
>
> This is not a strict requirement. As long as the package and class name match the expected conventions, the compiler will recognize the mapper correctly.

---

### 🔒 Value Class and Source Property Conventions

Because of its design, **KursMapper** may generate a large number of mapper classes. To keep this overhead as low as possible, every mapper must be implemented as a value class.

* **Property Name Restriction**  
A mapper must contain exactly one property representing the source object, and by default that property must be named `source`.
  

* **Visibility Requirement**  
Since mapping code generated by **KursMapper** needs direct access to the source data, this property must be declared as `public`.

```kotlin
@JvmInline
value class UserMapper(val source: User)
```

---

Mapping function names are also assembled using a predictable naming convention.

$$\text{[Verb]} + \text{[Prefix]} + \text{[Nullable]} + \text{[Mapping Function Name]} + \text{[Suffix]}$$

* Verb: `as` by default.
* Prefix: Empty by default.
* Nullable: `Nullable` is added when the target type is nullable.
* Mapping Function Name: The target class name by default.
* Suffix: Empty by default.

---

### 🛠️ Implementing Mapping Functions

Now that the package and class naming conventions are defined, you can implement the mapping function body freely in Kotlin.

> For details on each naming convention and how to customize prefixes and suffixes, see [KursMapper - All Settings](README.all-settings.md).

> These conventions are strict, and there are quite a few of them, so it can be difficult to follow all of them perfectly every time.
>
> As mentioned earlier, you don't need to worry. See [About the Sandbox](#-about-the-sandbox), and you'll be able to follow the conventions even without memorizing them.

#### 🧪 Final Mapper Example with All Conventions Applied

```kotlin
package kr.urbansoft.example.adapter.infra.mapper.exampleMapper.user

@JvmInline
value class NullableUserMapper(val source: User?) {
  fun asNullableUserDto(): UserDto? = source?.let { it.exampleMapper().asUserDto() }
}
```

---

## 🔄 Customizing Generated Mapping Functions

### 📂 Location and Naming Conventions of Generated Mappers

All generated code is placed under the `/build/generated/ksp` directory.  
Generated code follows the same conventions described in [Mapper and Mapping Function Conventions](#-mapper-and-mapping-function-conventions). To distinguish generated files from manually written ones, a `ByKurs` suffix is appended to the file name.

* Example: `UserMapperByKurs.kt`

---

### 🔍 Finding Generated Mappers Easily

Locating a generated mapper manually can be inconvenient.  
If you're familiar with the naming conventions, finding the correct file is usually straightforward, but it's still not the most comfortable workflow.

A simpler approach is to write a temporary call to the mapping function you're looking for and use your IDE's **Go to Declaration** feature.  
This will take you directly to the generated mapping function.

```kotlin
val source: User
source.persistenceMapper().asUserRecord() // Place the cursor on persistenceMapper or asUserRecord and use Go to Declaration.
```

---

### 📜 What Generated Mappers Look Like

Because **KursMapper** treats every mapping as part of a graph composed of small, focused mapping functions, the generated code remains straightforward and easy to follow.  
As a result, automatically generated mappings are not significantly different from code that a developer would write by hand.

```kotlin
// Generated by KursMapper. DO NOT EDIT DIRECTLY.
package kr.urbansoft.example.adapter.infra.persistence.mapper.example

import kotlin.String
import kr.urbansoft.example.adapter.infra.persistence.mapper.example.persistenceMapper
import kr.urbansoft.example.domain.model.example.Trait

public fun Trait.persistenceMapper(): TraitMapper = TraitMapper(this)

public fun TraitMapper.asNullableString(): String? = source.persistenceMapper().asString()
```
> 💡 Did you notice that **KursMapper** automatically discovered the `source.persistenceMapper().asString()` mapping you implemented in the [Quick Start](#-quick-start) section—that is, the `Trait` → `String` mapping—and used it to generate the body of `TraitMapper.asNullableString()` for you?

**KursMapper** takes advantage of Kotlin's function resolution rules.  
When a member function and an extension function with the same name are both available, Kotlin always prefers the member function.  
As a result, whenever you provide your own mapping implementation, the automatically generated extension function is naturally shadowed and no longer used.

---

### 🛠️ Customizing Generated Mapping Functions

At this point, you're ready to take control of any generated mapping function whose implementation you don't like.  
After all, you now know the package name, class name, and mapping function name.

Simply leave the `/build/generated/ksp` directory, create the appropriate subpackage under the package containing your configuration interface, add the mapper file and class if they don't already exist, copy the mapping function you want to customize, and modify its implementation however you like.

> This is certainly easier than implementing a brand-new mapping function from scratch with no guidance at all, but it's still possible to violate the conventions and have your implementation ignored by **KursMapper**.
> 
> More importantly, the truly urgent task isn't memorizing **KursMapper's** conventions—it's testing your new implementation as quickly as possible.
> 
> See [About the Sandbox](#-about-the-sandbox) to learn the fastest way to inject your implementation into **KursMapper**.

---

## 🌱 About the Sandbox

A sandbox is a temporary mapper or mapping function defined directly within a configuration interface.

### ⚙️ Basic Setup

```kotlin
@Suppress("unused", "EXTENSION_SHADOWED_BY_MEMBER")
@KursContext(contextName = "persistenceMapper", rootPackageName = "kr.urbansoft", guideLanguage = GuideLanguage.KO_KR)
interface PersistenceMapperConfig {
  fun MeetingRoom.asMeetingRoomRecord(): MeetingRoomRecord

  fun MeetingRoomRecord.asMeetingRoom(): MeetingRoom

  fun User.asUserRecord(): UserRecord

  fun UserRecord.asUser(): User

  // Sandbox configuration
  // A value class is not required here.
  class SandboxExample(val value: Int) { // You may choose any class name and property name.
    fun test(): String = value.toString() // Mapping function names are also completely up to you.
  }
}
```

When defining a sandbox mapper, the only things that matter are the type of the class parameter representing the source object and the return type of the mapping function representing the target object.

You don't need to worry about any of the other conventions here.

> **💡 Note**
>
> Although a value class is not required, the class constructor must contain **exactly one parameter**.
>
> If you define two or more constructor parameters, **KursMapper** will not recognize the class as a sandbox mapper.

After adding a mapper and mapping function to the sandbox, build the project and see how **KursMapper** applies it.

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

public fun IntMapper.asString(): String = PersistenceMapperConfig.SandboxExample(source).test() // Mapping function generated using the sandbox
```

When **KursMapper** discovers a sandbox definition, it generates a mapping function from the source type to the target type **according to the standard conventions**.

The generated function is then implemented to call the sandbox mapping function you just defined.

This allows your sandbox implementation to be immediately incorporated into **KursMapper's** mapping graph and behave **as if it were a fully implemented mapper**.

As a result, you can freely add, remove, and experiment with mapping functions in **KursMapper** without being constrained by its conventions, making it easy to test and understand how **KursMapper** behaves.

---

### 🔝 Mapping Function Priority

For the same source $\rightarrow$ target mapping, a mapping intent, a sandbox implementation, and a manually implemented mapping function may all exist at the same time.

If a manual implementation exists, it takes precedence.  
If no manual implementation exists, the sandbox implementation is used.  
If neither exists, **KursMapper** attempts to generate the mapping automatically based on the mapping intent.

Parameter lists are not merged between implementations.  
Instead, the parameter list of the highest-priority mapping function is used as-is.  
For example, even if the mapping intent defines no parameters, if the sandbox function declares a `timeZone: TimeZone` parameter, the generated production mapping function will also require a `timeZone` parameter.  
Likewise, if a manual implementation exists, its function signature becomes the final signature regardless of the parameter list defined by the mapping intent.

---

### 🚀 Promoting a Sandbox Mapping Function to a Production Mapper

Once you've tested a sandbox implementation and are satisfied with the result, you can move it into a production mapper outside the configuration interface by using the promotion guide.

Sandboxes are convenient, but you probably don't want to keep every mapping function inside a single configuration interface forever.

#### 🔍 Finding the Sandbox Promotion Guide

The sandbox promotion guide can be found at the following location:

> `/build/generated/ksp/main/resources/promoteSandboxGuide.txt`

*This file is generated only when one or more sandbox mappers are defined.*

#### 📋 Reviewing the Guide and Performing the Promotion

Let's take a look at the guide first.

```text
======================== [KursMapper Guide] ========================

To promote the sandbox function to a production mapping function, please follow the instructions below.

1. Create a mapper.
    - Mapper Package: kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin
    - File Name: IntMapper.kt
    - Create a mapper file with the following content.
        @JvmInline
        value class IntMapper(val source: Int)
    - Note) Source FQCN: kotlin.Int

2. Add the following function to the mapper.
    - Mapper FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.kotlin.IntMapper
    - Add the following function.
        fun asString(): String { return PersistenceMapperConfig.SandboxExample(source).test() }
    - Note) Source FQCN: kotlin.Int
    - Note) Target FQCN: kotlin.String

3. Use the IDE's Go to Declaration feature to navigate to the sandbox implementation, then copy its implementation and replace the body of the production mapping function.
    fun asString(): String {
      TODO("Paste the implementation here instead of '{ return PersistenceMapperConfig.SandboxExample(source).test() }'.")
    }

4. Remove the following sandbox function. Removing the sandbox mapper class is not required.
    @JvmInline
    value class IntMapper(val source: Int) {
      fun asString(): String
    }
    - Important) If the original sandbox function is not removed after promotion, a compilation error will be raised along with a removal guide.
    - Important) This is intended to prevent future confusion about which mapping function is currently being applied.
    - Note) Source FQCN: kotlin.Int
    - Note) Target FQCN: kotlin.String
    - Note) KursMapper does not use the class name, variable name, or function name defined in the sandbox declaration. Therefore, the actual declaration in the configuration interface may differ from the one shown above.

5. Rebuild the project after promoting the sandbox function.

====================================================================
```

This guide tells you exactly where and how to move your sandbox mapping functions, so you don't have to spend time figuring it out yourself.  
If you've been following along since [Quick Start](#-quick-start), you should have no trouble following the guide and completing the promotion process.

---

### 🧪 Real-World Sandbox Example

Let's take a look at a more practical sandbox example.

First, consider the mapper we created earlier in [Quick Start](#-quick-start).

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

Have you noticed anything inconvenient about this mapper?

That's right.  
The conversion from `User.createAt: Instant` to `UserRecord.createAt: java.time.LocalDateTime` could actually be extracted into a smaller, reusable mapping function.

In a codebase like the one used in this example—where Kotlin datetime types coexist with `java.time` types for persistence and ORM integration—an `Instant` → `java.time.LocalDateTime` conversion can easily become one of the most common mappings in the entire project.

So is it really the right choice to keep implementing it over and over again just because **KursMapper** doesn't automatically connect it for you?  

In this example, we'll implement this mapping through a sandbox and see how the implementation is applied throughout the mapping graph.

---

#### 💻 Defining a Sandbox Mapper

First, define the mapping function you have in mind inside the sandbox and build the project.

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

It has been applied successfully. You can also see that **KursMapper** has intelligently generated an `Instant` → `java.time.LocalDateTime?` mapping for you.

But I'm still not satisfied.  
This mapping function can be broken down even further: `kotlin.time.Instant` → `kotlinx.datetime.LocalDateTime`, and then `kotlinx.datetime.LocalDateTime` → `java.time.LocalDateTime`.

Let's define an additional sandbox mapper and build the project again.

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

Hmm. This looks granular enough now, but it would be better to slightly adjust the implementation of the original `InstantMapper.asJavaLocalDateTime` function so that it uses the `InstantMapper.asLocalDateTime` function we just defined.

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

Great. You've just taught the `persistenceMapper` context how to convert a Kotlin `Instant` into a Java `LocalDateTime`.

From now on, whenever **KursMapper** encounters a `kotlin.time.Instant` → `java.time.LocalDateTime` conversion, it will know how to handle it without being taught again.

Now let's take another look at `UserMapper`.

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

At this point, you could also update the implementation manually to use the mapping function you just defined in the sandbox.

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

Like this.

But this time, let's trust **KursMapper's** automatic wiring.

Go ahead and comment out the `asUserRecord` mapping function, then build the project.

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

Oops! A guide appeared.  
But this time, its contents are a little different from what we've seen so far.


```text
======================== [KursMapper Guide] ========================

The mapping intent is missing required arguments. Please refer to the guide below and update the arguments.

1. Open the configuration interface file.
   - Configuration Interface FQCN: kr.urbansoft.example.adapter.infra.persistence.mapper.PersistenceMapperConfig

2. Update the mapping intent to use the final argument list, or replace it with the function signature shown below.
   - Mapping Intent
        fun User.asUserRecord(): UserRecord
   - Note) Final Arguments: timeZone: TimeZone
        fun User.asUserRecord(timeZone: TimeZone): UserRecord
   - Note) Source FQCN: kr.urbansoft.example.domain.model.example.User
   - Note) Target FQCN: kr.urbansoft.example.adapter.infra.persistence.someorm.example.UserRecord
   - Note) Final Argument FQCNs
      · timeZone: kotlinx.datetime.TimeZone
   - Note) KursMapper identifies mapping intents by their source and target types, not by their function names. Therefore, the actual function name declared in the configuration interface may differ from the one shown above.
   - Note) This issue occurs when a mapping intent does not declare all arguments required by the mapping functions it uses.

3. After updating the mapping intent, rebuild the project.

====================================================================
```

This is a missing function parameter guide.  
It asks you to add a `timeZone: TimeZone` parameter to the existing `fun User.asUserRecord(): UserRecord` mapping intent.

> When a small mapping function requires additional parameters, **KursMapper** automatically propagates those parameters to the larger mapping functions that use it.
>
> However, if the larger mapping function is a mapping intent that you defined yourself, parameter propagation fails and **KursMapper** displays a missing function parameter guide instead.
>
> This behavior exists because of **KursMapper's** philosophy: you are the owner of your code.
>
> Code you write yourself is absolute to **KursMapper**. It is not something **KursMapper** should dare to interpret, modify, or apply changes to on your behalf.

Move to the configuration interface, follow the guide, and build the project again.

```kotlin
@Suppress("unused", "EXTENSION_SHADOWED_BY_MEMBER")
@KursContext(contextName = "persistenceMapper", rootPackageName = "kr.urbansoft", guideLanguage = GuideLanguage.KO_KR)
interface PersistenceMapperConfig {
  // Mapping intents omitted
  
  fun User.asUserRecord(timeZone: TimeZone): UserRecord
  
  // Sandbox definitions omitted
}
```

The build now succeeds.

Let's take a look at how it was implemented.


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

**KursMapper** has automatically generated the `UserMapper.asUserRecord` mapping function that previously had to be implemented by hand, using the conversion capability you taught it!

Thank you for teaching **KursMapper** a new conversion capability.  
The more conversion capabilities you teach it in this way, the more **KursMapper** can help you by automatically composing and reusing them throughout your codebase.

If you're happy with this sandbox implementation, consider promoting it to a production mapper by following [Promoting a Sandbox Mapping Function to a Production Mapper](#-Promoting-a-Sandbox-Mapping-Function-to-a-Production-Mapper).

---

## 💭 The Philosophy of KursMapper

If you've made it this far, you've probably already noticed:  
**KursMapper** starts from a somewhat different perspective than most existing mapper libraries.

Many mapper libraries are excellent tools, but they can sometimes lead developers into a situation where the tool starts dictating the design of the code rather than serving it.

Because of a mapper library's limitations, developers may find themselves adding no-argument constructors to perfectly valid domain entities, exposing unnecessary setters, or spending time studying complex annotation attributes that hurt readability. When a complicated mapping doesn't fit the library's assumptions, they may even have to learn a library-specific DSL or expression language—sometimes spending more time fighting the library than building the actual feature.

> **"Don't make your code fit the mapper. The mapper should fit your code."**

**KursMapper** was built around this idea.

What we do best is **write Kotlin code**. Rather than becoming a black box that magically handles everything, **KursMapper** is designed to keep developers in control of their mapping logic.

Automation is provided only when it is safe and unambiguous. For complex business mappings, developers remain free to express the logic directly in Kotlin, while **KursMapper** focuses on being the most helpful guide possible—showing **what is missing and what needs to be implemented next**.

We hope **KursMapper** helps reduce the friction and frustration often associated with object mapping, allowing you to focus on the parts of Kotlin development that matter most.

## 💬 Feedback

If you run into any issues or have questions while using **KursMapper**, feel free to open an issue at any time.

We'd love to hear your feedback.