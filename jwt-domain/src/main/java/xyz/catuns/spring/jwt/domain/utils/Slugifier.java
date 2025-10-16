package xyz.catuns.spring.jwt.domain.utils;

@FunctionalInterface
public interface Slugifier {

    String slug(String name);

}
