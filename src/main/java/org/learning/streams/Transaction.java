package org.learning.streams;

import java.time.LocalDateTime;

record Transaction(String clientId, String category, LocalDateTime timestamp) {

}
