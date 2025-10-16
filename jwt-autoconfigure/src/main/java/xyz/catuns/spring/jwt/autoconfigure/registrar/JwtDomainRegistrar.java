package xyz.catuns.spring.jwt.autoconfigure.registrar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;
import xyz.catuns.spring.jwt.autoconfigure.annotation.EnableJwtDomain;
import xyz.catuns.spring.jwt.domain.DomainMetadata;
import xyz.catuns.spring.jwt.domain.resolver.DomainMetadataResolver;

import java.util.Set;

@Slf4j
public class JwtDomainRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(EnableJwtDomain.class.getName())
        );

        if (attributes == null) {
            log.warn("@EnableJwtDomain annotation not found on {}",
                    annotationMetadata.getClassName());
            return;
        }

        // Extract domain configuration
        DomainMetadata dm = DomainMetadataResolver.resolve(attributes);
        String packageName = determineBasePackage(annotationMetadata);

        // Check if domain is configured
        if (dm.hasDomain()) {
            log.debug("Domain configuration detected");;

            registerEntityScanPackages(registry, dm);
//            registerRepositoryScanPackages(registry, dm);
        }

        registerDomainMetadata(registry, dm);
    }

    private String determineBasePackage(AnnotationMetadata annotationMetadata) {
        return ClassUtils.getPackageName(annotationMetadata.getClassName());
    }

    private static void registerDomainMetadata(BeanDefinitionRegistry registry, DomainMetadata dm) {
        GenericBeanDefinition beanDefinition = DomainMetadataResolver.asBeanDefinition(dm);
        registry.registerBeanDefinition("jwtDomainMetadata", beanDefinition);
        log.debug("DomainMetadata registered: {}", dm);
    }

    private void registerEntityScanPackages(BeanDefinitionRegistry registry, DomainMetadata dm) {
        Set<String> entityScanPackages = DomainMetadataResolver.determineEntityScanPackages(dm);
        EntityScanPackages.register(registry, entityScanPackages);
        log.debug("Registered entity scan packages: {}", entityScanPackages);
    }

    private void registerRepositoryScanPackages(BeanDefinitionRegistry registry, DomainMetadata dm) {
        Set<String> scanPackages = DomainMetadataResolver.determineRepositoryScanPackages(dm);
        // Create a new scanner for JPA repositories
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
        scanner.setIncludeAnnotationConfig(false);

        // Configure scanner filters for Spring Data JPA repositories
        scanner.addIncludeFilter(new AnnotationTypeFilter(org.springframework.stereotype.Repository.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(org.springframework.data.repository.Repository.class));

        // Actually perform the scanning
        for (String basePackage : scanPackages) {
            scanner.scan(basePackage);
        }
//
        log.debug("Registered repository scan packages: {}", scanPackages);

    }


}
