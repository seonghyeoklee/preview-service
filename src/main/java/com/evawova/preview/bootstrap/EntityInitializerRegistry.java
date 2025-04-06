package com.evawova.preview.bootstrap;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 엔티티 초기화 클래스들을 등록하고 관리하는 레지스트리
 */
@Component
public class EntityInitializerRegistry {

    private final List<EntityInitializer> initializers;

    public EntityInitializerRegistry(List<EntityInitializer> initializers) {
        this.initializers = initializers;
    }

    /**
     * 모든 초기화 클래스를 의존성과 우선순위를 고려하여 정렬한 리스트 반환
     * 
     * @return 정렬된 초기화 클래스 리스트
     */
    public List<EntityInitializer> getAllInitializers() {
        return initializers.stream()
                .sorted(Comparator
                        // 먼저 의존성에 따라 정렬
                        .comparing((EntityInitializer initializer) -> initializers.stream()
                                .anyMatch(other -> initializer.dependsOn(other.getEntityName())) ? 1 : 0)
                        // 그 다음 우선순위에 따라 정렬
                        .thenComparing(EntityInitializer::getOrder))
                .collect(Collectors.toList());
    }
}