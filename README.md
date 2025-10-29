# lib-notifications

Firefly Notifications Library

A small hexagonal library exposing notification ports (email, SMS, push), DTOs, and simple application services that depend only on ports. Concrete providers live in separate adapter libraries.

- Core: ports, DTOs, and services under `lib-notifications-core`
- Adapters: `lib-notifications-twilio` (SMS), `lib-notifications-sendgrid` (email), `lib-notifications-resend` (email), `lib-notifications-firebase` (push)

## Install
Add as a dependency to your Spring Boot service and include the adapters you plan to use.

```xml path=null start=null
<dependency>
  <groupId>com.firefly</groupId>
  <artifactId>lib-notifications-core</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then add one or more adapters (see individual READMEs).

## Usage
Inject the service interfaces exposed by the core and call them; Spring will resolve the adapter beans you include on the classpath.

```java path=null start=null
@Autowired EmailService emailService;

emailService.sendEmail(EmailRequestDTO.builder()
    .from("noreply@example.com")
    .to("user@example.com")
    .subject("Hi")
    .text("Hello")
    .build());
```
