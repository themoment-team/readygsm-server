package team.themoment.readygsmserver.domain.chat.service;

/**
 * 스트림 수신 콜백. 구현체는 이 메서드들을 같은 스레드에서 순서대로 호출한다.
 *
 * <p>끝맺는 방법이 셋이다. 무엇을 부르느냐에 따라 클라이언트가 받는 신호가 달라진다.
 * <ul>
 *   <li>{@link #onComplete} {@code event: done}</li>
 *   <li>{@link #onError} {@code event: error}</li>
 *   <li>{@link #onAbort} 아무것도 보내지 않고 연결만 끊는다</li>
 * </ul>
 */
public interface StreamHandler {

    void onToken(String token);

    /**
     * @param finishReason {@code "stop"} 또는 {@code "length"}
     */
    void onComplete(String finishReason);

    void onError(Throwable e);

    /**
     * 아무 이벤트도 보내지 않고 연결을 끊는다.
     *
     * <p>프로세스가 죽거나 네트워크가 끊긴 상황에 해당한다. 클라이언트는 완료 신호 없이
     * 스트림이 닫힌 것만 보게 되므로 이를 실패로 판단해야 한다.
     */
    void onAbort();
}
