package com.firefly.core.notifications.core.services.email.v1;

import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailRequestDTO;
import com.firefly.core.notifications.interfaces.dtos.email.v1.EmailResponseDTO;
import com.firefly.core.notifications.interfaces.enums.EmailStatusEnum;
import com.firefly.core.notifications.interfaces.interfaces.providers.email.v1.EmailProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmailServiceImpl.class, EmailServiceImplTest.TestBeans.class})
class EmailServiceImplTest {

    @Configuration
    static class TestBeans {
        @Bean
        EmailProvider emailProvider() {
            return request -> Mono.just(EmailResponseDTO.success("test-message-id"));
        }
    }

    @Autowired
    private EmailService emailService;

    @Test
    void sendEmail_returnsSuccess() {
        EmailRequestDTO req = EmailRequestDTO.builder()
                .from("noreply@example.com")
                .to("user@example.com")
                .subject("Hello")
                .text("Hi")
                .build();

        EmailResponseDTO resp = emailService.sendEmail(req).block();
        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo(EmailStatusEnum.SENT);
        assertThat(resp.getMessageId()).isEqualTo("test-message-id");
    }
}
