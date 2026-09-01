package team.themoment.readygsmserver.domain.chat.service;

/**
 * 스트림 수신 콜백. 구현체는 이 세 메서드를 같은 스레드에서 순서대로 호출한다.
 */
public interface StreamHandler {

    void onToken(String token);

    /**
     * @param finishReason {@code "stop"} 또는 {@code "length"}
     */
    void onComplete(String finishReason);

    void onError(Throwable e);
}
