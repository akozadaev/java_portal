package ru.akozadaev.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	/**
	 * Настраивает правила HTTP-безопасности для публичных API-эндпоинтов.
	 *
	 * @param http билдер HTTP-безопасности Spring Security
	 * @return настроенная цепочка фильтров безопасности
	 * @throws Exception если Spring Security не сможет собрать цепочку фильтров
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/api/v1/applications").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/applications", "/api/v1/applications/{id}").permitAll()
						.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
						.anyRequest().authenticated())
				.build();
	}
}
