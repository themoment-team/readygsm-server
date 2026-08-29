package team.themoment.readygsmserver.domain.chat.client;

/**
 * 진행 중인 스트림을 끊기 위한 손잡이.
 *
 * <p>클라이언트가 탭을 닫아도 upstream 호출이 계속되면 비용이 나가므로,
 * SSE 연결이 끊긴 시점에 {@link #cancel()}로 구독을 해제한다.
 */
@FunctionalInterface
public interface StreamSubscription {

    void cancel();
}
