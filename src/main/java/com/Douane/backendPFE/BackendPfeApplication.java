package com.Douane.backendPFE;

import com.Douane.backendPFE.models.user.ERole;
import com.Douane.backendPFE.models.user.Role;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.repositories.userR.RoleRepository;
import com.Douane.backendPFE.repositories.userR.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BackendPfeApplication {


	@Autowired
	private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(BackendPfeApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return mapper;
	}
	@Bean
	CommandLineRunner run(RoleRepository roleRpository , UserRepository userRepository){
		return args -> {

			if (roleRpository.count()<1) {

				roleRpository.save(new Role( null, ERole.ROLE_ADMIN));
				roleRpository.save(new Role(null,ERole.ROLE_CITOYEN));
				roleRpository.save(new Role(null,ERole.ROLE_INSPECTATEURBR));
				roleRpository.save(new Role(null,ERole.ROLE_CHEF_BUREAU));
			}
			if(!userRepository.existsByEmail("Admin")){
				UserModel user = new UserModel(
						"Admin",

						encoder.encode("password")
				);

				Role adminRole = roleRpository.findRoleByName(ERole.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				//Role userRole = roleRpository.findRoleByName(ERole.ROLE_CITOYEN)
					//	.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				user.getRoles().add(adminRole);
				//user.getRoles().add(userRole);
				userRepository.save(user);
			}
		};
	}



}
