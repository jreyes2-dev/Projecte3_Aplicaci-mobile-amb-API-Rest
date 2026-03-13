package cat.copernic.backendProjecte3.config;

/**
 *
 * @author bharr
 */
// En tu backend (Spring Boot)
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esto le dice a Spring Boot: "Si alguien pide /uploads/loquesea, búscalo en la carpeta uploads de mi PC"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
