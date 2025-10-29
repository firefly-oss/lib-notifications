package com.firefly.core.notifications.core.services.sms.v1;

import com.firefly.core.notifications.interfaces.dtos.sms.v1.SMSRequestDTO;
import com.firefly.core.notifications.interfaces.dtos.sms.v1.SMSResponseDTO;
import com.firefly.core.notifications.interfaces.interfaces.providers.sms.v1.SMSProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SMSServiceImpl.class, SMSServiceImplTest.TestBeans.class})
class SMSServiceImplTest {

    @Configuration
    static class TestBeans {
        @Bean
        SMSProvider smsProvider() {
            return request -> SMSResponseDTO.success("sms-123");
        }
    }

    @Autowired
    private SMSService smsService;

    @Test
    void sendSMS_returnsSuccess() {
        SMSRequestDTO req = SMSRequestDTO.builder()
                .phoneNumber("+10000000000")
                .message("Hello")
                .build();

        SMSResponseDTO resp = smsService.sendSMS(req).block();
        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("SENT");
        assertThat(resp.getMessageId()).isEqualTo("sms-123");
    }
}
