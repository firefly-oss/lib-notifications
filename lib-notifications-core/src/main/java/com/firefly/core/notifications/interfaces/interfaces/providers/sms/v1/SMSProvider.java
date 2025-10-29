/*
 * Copyright 2025 Firefly Software Solutions Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.firefly.core.notifications.interfaces.interfaces.providers.sms.v1;

import com.firefly.core.notifications.interfaces.dtos.sms.v1.SMSRequestDTO;
import com.firefly.core.notifications.interfaces.dtos.sms.v1.SMSResponseDTO;

/**
 * Port (outbound interface) for sending SMS notifications.
 * <p>
 * In hexagonal architecture, this interface represents an output port that defines
 * the contract for SMS delivery. Concrete implementations (adapters) provide the
 * actual infrastructure integration (e.g., Twilio, AWS SNS, Vonage).
 * <p>
 * The core domain and application layers depend only on this interface, never on
 * specific implementations, ensuring clean separation of concerns and testability.
 */
public interface SMSProvider {

    /**
     * Send an SMS using the provider's infrastructure.
     *
     * @param request SMS request containing recipient phone number and message text
     * @return Response containing delivery status and message ID
     */
    SMSResponseDTO sendSMS(SMSRequestDTO request);
}