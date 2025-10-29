# lib-notifications

Firefly Notifications Library - A hexagonal architecture implementation for multi-channel notifications.

## Overview

This library provides a clean, testable notification system for email, SMS, and push notifications using **Hexagonal Architecture** (Ports and Adapters pattern). The core domain logic is completely isolated from infrastructure concerns, making it easy to swap providers, test in isolation, and maintain over time.

### Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                         │
│          (EmailService, SMSService, PushService)            │
└────────────────────┬────────────────────────────────────────┘
                     │ depends on
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Domain Layer (Ports)                     │
│        EmailProvider │ SMSProvider │ PushProvider           │
│              (interfaces + DTOs)                            │
└─────────┬──────────────────────┬────────────────────┬───────┘
          │                      │                    │
     implemented by          implemented by      implemented by
          │                      │                    │
          ▼                      ▼                    ▼
┌──────────────────┐  ┌──────────────────┐  ┌─────────────────┐
│ Infrastructure   │  │ Infrastructure   │  │ Infrastructure  │
│   (Adapters)     │  │   (Adapters)     │  │   (Adapters)    │
├──────────────────┤  ├──────────────────┤  ├─────────────────┤
│ SendGrid Adapter │  │ Twilio Adapter   │  │ Firebase Adapter│
│ Resend Adapter   │  │                  │  │                 │
└──────────────────┘  └──────────────────┘  └─────────────────┘
```

### Components

#### Core Module (`lib-notifications-core`)

**Domain Layer (Ports & DTOs):**
- `EmailProvider`, `SMSProvider`, `PushProvider` - Port interfaces defining contracts
- Request/Response DTOs for each notification type
- No infrastructure dependencies

**Application Layer (Services):**
- `EmailService`, `SMSService`, `PushService` - Service interfaces and implementations
- Depend only on port interfaces, never on concrete adapters
- Contain business logic and orchestration

#### Adapter Modules (Infrastructure)

- **`lib-notifications-sendgrid`** - SendGrid email adapter
- **`lib-notifications-resend`** - Resend email adapter  
- **`lib-notifications-twilio`** - Twilio SMS adapter
- **`lib-notifications-firebase`** - Firebase Cloud Messaging push adapter

Each adapter implements the corresponding port interface and handles provider-specific integration details.

## Installation

Add the core library to your Spring Boot application:

```xml path=null start=null
<dependency>
  <groupId>com.firefly</groupId>
  <artifactId>lib-notifications-core</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then add one or more adapter libraries based on your notification needs:

```xml path=null start=null
<!-- For email via SendGrid -->
<dependency>
  <groupId>com.firefly</groupId>
  <artifactId>lib-notifications-sendgrid</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- For email via Resend -->
<dependency>
  <groupId>com.firefly</groupId>
  <artifactId>lib-notifications-resend</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- For SMS via Twilio -->
<dependency>
  <groupId>com.firefly</groupId>
  <artifactId>lib-notifications-twilio</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- For push notifications via Firebase -->
<dependency>
  <groupId>com.firefly</groupId>
  <artifactId>lib-notifications-firebase</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

See individual adapter READMEs for configuration details.

## Usage

Inject the service interfaces from the core module. Spring automatically wires the adapter implementations you've added to your classpath.

### Email Example

```java path=null start=null
@Service
public class UserNotificationService {
    
    @Autowired
    private EmailService emailService;
    
    public void sendWelcomeEmail(String userEmail) {
        EmailRequestDTO request = EmailRequestDTO.builder()
            .from("noreply@example.com")
            .to(List.of(userEmail))
            .subject("Welcome to Firefly")
            .html("<h1>Welcome!</h1><p>Thanks for signing up.</p>")
            .text("Welcome! Thanks for signing up.")
            .build();
        
        emailService.sendEmail(request)
            .subscribe(response -> {
                if (response.isSuccess()) {
                    log.info("Email sent successfully: {}", response.getMessageId());
                } else {
                    log.error("Failed to send email: {}", response.getError());
                }
            });
    }
}
```

### SMS Example

```java path=null start=null
@Autowired
private SMSService smsService;

public void sendVerificationCode(String phoneNumber, String code) {
    SMSRequestDTO request = SMSRequestDTO.builder()
        .phoneNumber(phoneNumber)
        .message("Your verification code is: " + code)
        .build();
    
    smsService.sendSMS(request)
        .subscribe(response -> {
            if (response.isSuccess()) {
                log.info("SMS sent: {}", response.getMessageId());
            }
        });
}
```

### Push Notification Example

```java path=null start=null
@Autowired
private PushService pushService;

public void sendPushNotification(String deviceToken, String title, String body) {
    PushNotificationRequest request = PushNotificationRequest.builder()
        .token(deviceToken)
        .title(title)
        .body(body)
        .data(Map.of("type", "alert", "priority", "high"))
        .build();
    
    pushService.sendPush(request)
        .subscribe(response -> {
            if (response.isSuccess()) {
                log.info("Push sent: {}", response.getMessageId());
            }
        });
}
```

## Benefits of Hexagonal Architecture

1. **Provider Independence**: Switch from SendGrid to Resend without changing business logic
2. **Testability**: Mock port interfaces for unit tests without touching real providers
3. **Maintainability**: Clear separation between domain logic and infrastructure
4. **Flexibility**: Add new providers by simply implementing the port interface
5. **Domain Focus**: Business logic in services depends only on abstract interfaces

## Testing

Test your notification logic without real providers:

```java path=null start=null
@Test
public void testEmailNotification() {
    // Mock the port interface
    EmailProvider mockProvider = mock(EmailProvider.class);
    when(mockProvider.sendEmail(any()))
        .thenReturn(Mono.just(EmailResponseDTO.success("test-id")));
    
    EmailServiceImpl service = new EmailServiceImpl();
    service.emailProvider = mockProvider;
    
    // Test without touching real email infrastructure
    StepVerifier.create(service.sendEmail(someRequest))
        .expectNextMatches(response -> response.isSuccess())
        .verifyComplete();
}
```

## Documentation

For detailed architecture documentation, see [ARCHITECTURE.md](ARCHITECTURE.md).

For adapter-specific configuration and usage:
- [SendGrid Adapter](../lib-notifications-sendgrid/README.md)
- [Resend Adapter](../lib-notifications-resend/README.md)
- [Twilio Adapter](../lib-notifications-twilio/README.md)
- [Firebase Adapter](../lib-notifications-firebase/README.md)
