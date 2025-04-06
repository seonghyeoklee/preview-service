package com.evawova.preview.bootstrap;

/**
 * 엔티티 초기화를 위한 인터페이스
 * 각 엔티티별 초기화 클래스는 이 인터페이스를 구현해야 합니다.
 */
public interface EntityInitializer {

    /**
     * 엔티티 초기화 실행
     */
    void initialize();

    /**
     * 초기화할 엔티티의 이름 반환
     * 
     * @return 엔티티 이름
     */
    String getEntityName();

    /**
     * 초기화 우선순위 반환. 낮은 값이 더 높은 우선순위를 의미합니다.
     * 
     * @return 우선순위 값
     */
    default int getOrder() {
        return 100; // 기본 우선순위
    }

    /**
     * 이 초기화기가 다른 초기화기 이후에 실행되어야 하는지 확인
     * 
     * @param entityName 의존하는 엔티티 이름
     * @return 의존성 여부
     */
    default boolean dependsOn(String entityName) {
        return false;
    }
}