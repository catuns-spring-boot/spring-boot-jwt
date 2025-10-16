package xyz.catuns.spring.jwt.domain.utils;

import com.github.slugify.Slugify;

@FunctionalInterface
public interface Slugifier {

    String slug(String name);

}
