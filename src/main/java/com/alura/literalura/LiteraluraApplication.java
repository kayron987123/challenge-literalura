package com.alura.literalura;

import com.alura.literalura.principal.Principal;
import com.alura.literalura.repository.AutorRepository;
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
	private AutorRepository autorRepository;

	public LiteraluraApplication(LibroRepository libroRepository, AutorRepository autorRepository) {
		this.libroRepository = libroRepository;
		this.autorRepository = autorRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(libroRepository, autorRepository);
		principal.mostrarMenu();
	}
}
