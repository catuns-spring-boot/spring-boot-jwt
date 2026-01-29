package xyz.catuns.spring.jwt.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import xyz.catuns.spring.jwt.core.properties.JwtMetadata;
import xyz.catuns.spring.jwt.domain.properties.JwtDomainProperties;


@ConfigurationProperties(prefix = "jwt")
public class JwtProperties extends JwtMetadata {

    /*
     * Jwt Entity Domain Properties
     */
   @NestedConfigurationProperty
   private JwtDomainProperties entity = new JwtDomainProperties();


   public JwtDomainProperties getEntity() {
    return entity;
   }

   public void setEntity(JwtDomainProperties entity) {
    this.entity = entity;
   }

   
}
