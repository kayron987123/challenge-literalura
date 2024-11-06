package com.alura.literalura;

import com.alura.literalura.principal.Principal;
import com.alura.literalura.repository.LibroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	private LibroRepository libroRepository;

	public LiteraluraApplication(LibroRepository libroRepository) {
		this.libroRepository = libroRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(libroRepository);
		principal.mostrarMenu();
	}
}
