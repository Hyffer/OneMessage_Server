package xyz.hyffer.onemessage_server.client_api.controller_ws;

import com.cisco.commons.networking.SSEClient;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import xyz.hyffer.onemessage_server.client_api.service.ClientPushService;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientPushControllerTest {

    @LocalServerPort
    private int port;

    @Resource
    ClientSseManager clientSseManager;

    @Resource
    ClientPushService pushService;

    BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

    @Test
    void notificationTest() {
        SSEClient sseClient = SSEClient.builder()
                .url("http://localhost:" + port + "/app/notification/full")
                .eventHandler((notification) -> {
                    System.out.println(notification);
                    blockingQueue.add(notification);
                })
                .build();
        sseClient.start();
        await().until(() -> clientSseManager.countAll() > 0);

        pushService.PushNotification(
                List.of(),
                List.of()
        );
        await().atMost(1, SECONDS)
                .untilAsserted(() -> JSONAssert.assertEquals(
                        "{\"contacts\":[],\"messages\":[]}",
                        blockingQueue.poll(),
                        true
                ));

        sseClient.shutdown();
    }

    @Test
    void notificationError() {
        SSEClient sseClient = SSEClient.builder()
                .url("http://localhost:" + port + "/app/notification/notfound")
                .eventHandler((notification) -> {})
                .build();
        sseClient.start();

        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertThat(sseClient.getStatus()).isEqualTo(SSEClient.SubscribeStatus.RECONNECTING));
        sseClient.shutdown();
    }
}
