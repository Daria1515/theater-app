package com.web.theater.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// Authorize requests
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/**", "/GetAvatarUserController").permitAll()//ко всем страницам разрешить доступ
				)
				.csrf(AbstractHttpConfigurer::disable)//отключаем проверку csrf для корректных ajax запросов
				// Configure OAuth2 login
				.oauth2Login(oauth2Login ->
						oauth2Login
								.loginPage("/")
								.defaultSuccessUrl("/reg_sig_google", true)//куда (контроллер) будет получен ответ от google в виде данных пользователя
				);
		return http.build();
	}
}
