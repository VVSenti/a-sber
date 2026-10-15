package org.learning.streams;

import java.time.LocalDateTime;

public record Transaction(String clientId, String category, LocalDateTime timestamp) {

}
