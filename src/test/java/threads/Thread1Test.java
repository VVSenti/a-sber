package threads;

import org.junit.jupiter.api.Test;
import org.learning.future.task1.Order;
import org.learning.future.task1.OrderProcessor;
import org.learning.future.task1.Status;
import org.learning.future.task1.Task1Constants;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class Thread1Test {

    private final OrderProcessor processor = new OrderProcessor();

        @Test
        void shouldReturnEmptyListForEmptyInput() throws InterruptedException {
            var results = processor.process(Collections.emptyList());

            assertThat(results).isEmpty();
        }

        @Test
        void shouldReturnResultForEachOrder() throws InterruptedException {
            var orders = generateOrders(100);

            var results = processor.process(orders);

            assertThat(results).hasSize(100);
            assertThat(results)
                    .extracting(r -> r.getOrder().getNumber())
                    .containsExactlyInAnyOrderElementsOf(
                            orders.stream().map(Order::getNumber).toList()
                    );
        }

        @Test
        void shouldMarkAllOrdersAsDoneWhenTheyFitInDeadline() throws InterruptedException {
            var orders = generateOrders(100);

            var results = processor.process(orders);

            assertThat(results)
                    .allMatch(r -> r.getStatus() == Status.DONE);
        }

        @Test
        void shouldNotHaveDuplicateResults() throws InterruptedException {
            var orders = generateOrders(100);

            var results = processor.process(orders);

            var numbers = results.stream()
                    .map(r -> r.getOrder().getNumber())
                    .toList();

            assertThat(numbers).doesNotHaveDuplicates();
        }

        @Test
        void shouldRespectTotalDeadline() {
            var orders = generateOrders(100);

            assertTimeoutPreemptively(
                    java.time.Duration.ofSeconds(
                            Task1Constants.TOTAL_PROCESS_TIMEOUT_IN_SECONDS
                                    + Task1Constants.AWAIT_TERMINATION_IN_SECONDS
                    ),
                    () -> processor.process(orders)
            );
        }

    private static List<Order> generateOrders(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Order.builder()
                        .number(UUID.randomUUID())
                        .build())
                .toList();
    }
}