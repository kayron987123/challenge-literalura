package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.DatosLibro;
import com.alura.literalura.model.Libro;
import com.alura.literalura.model.Resultados;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoApi;
import com.alura.literalura.service.ConvierteDatos;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class    Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi  consumoApi= new ConsumoApi();
    private final String URL_BASE = "https://gutendex.com";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu(){
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    --------------------------------------------------------------------
                    Opciones que se pueden realizar
                    
                    1. Buscar libro por Titulo
                    2. Listar libros buscados y encontrados
                    3. Listar autores
                    4. Listar autores vivos en un determinado anio
                    5. Mostrar cantidad de libros en ingles encontrados
                    6. Mostrar cantidad de libros en frances encontrados
                    7. Mostrar top 10 de libros mas descargados
                    8. Buscar autores por nombre
                    0. Salir
                    --------------------------------------------------------------------
                    Ingrese el numero de la opcion que desea realizar
                    --------------------------------------------------------------------
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion){
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    cantidadLibrosPorIdiomasEnIngles();
                    break;
                case 6:
                    cantidadLibrosPorIdiomasEnFrances();
                    break;
                case 7:
                    top10LibroMasDescargados();
                    break;
                case 8:
                    buscarAutoresPorNombre();
                    break;
                case 0:
                    System.out.println("Saliendo del programa");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }
    }

    private void buscarLibroPorTitulo(){
        System.out.println("Introduce el titulo del libro");
        var nombretitulo = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "/books/?search=%20" + nombretitulo.replace(" ", "%20"));
        Resultados resultados = conversor.convertirDatos(json, Resultados.class);
        for (DatosLibro datosLibro : resultados.libros()){
            var listaAutores = datosLibro.autor().stream().map(datosAutor -> new Autor(null, datosAutor.name(), datosAutor.birthYear(), datosAutor.deathYear())).toList();

            var libro = new Libro();
            libro.setTitulo(datosLibro.titulo());
            libro.setAutores(listaAutores);
            libro.setIdiomas(datosLibro.idiomas());
            libro.setNumeroDescarga(datosLibro.numeroDescargas());

            libroRepository.save(libro);

            System.out.println("""
            Se encontro este libro con el título ingresado:
            Título: %s
            Autor(es): %s
            Idiomas: %s
            Número de descargas: %d
            """.formatted(
                    datosLibro.titulo(),
                    listaAutores.stream().map(Autor::getName).collect(Collectors.joining(", ")),
                    String.join(", ", datosLibro.idiomas()),
                    datosLibro.numeroDescargas()
            ));
        }
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();
        for (Libro libro : libros){
            System.out.println("""
            Título: %s
            Autor(es): %s
            Idiomas: %s
            Número de descargas: %d
            """.formatted(
                    libro.getTitulo(),
                    libro.getAutores().stream().map(Autor::getName).collect(Collectors.joining(", ")),
                    String.join(", ", libro.getIdiomas()),
                    libro.getNumeroDescarga()
            ));
        }
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()){
            System.out.println("No se ha buscado ningun libro aun");
            return;
        }
        for (Autor autor : autores){
            System.out.println("""
            Nombre: %s
            Año de nacimiento: %d
            Año de defunción: %d
            """.formatted(
                    autor.getName(),
                    autor.getBirthYear(),
                    autor.getDeathYear()
            ));
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Introduce el anio para buscar autores vivos");
        var anioAutor = teclado.nextInt();
        List<Autor> autores = autorRepository.findByYearAlive(anioAutor);
        if (autores.isEmpty()){
            System.out.println("No se encontraron autores vivos en el anio ingresado");
            return;
        }
        for (Autor autor : autores){
            System.out.println("""
            Nombre: %s
            Año de nacimiento: %d
            Año de defunción: %d
            """.formatted(
                    autor.getName(),
                    autor.getBirthYear(),
                    autor.getDeathYear()
            ));
        }
    }

    private void cantidadLibrosPorIdiomasEnFrances() {
        var libros = libroRepository.getLibrosPorIdiomasEnFrances();
        var cantidadLibros = libros.size();
        System.out.println("La cantidad de libros en frances es: " + cantidadLibros);
    }

    private void cantidadLibrosPorIdiomasEnIngles() {
        var libros = libroRepository.getLibrosWhereIdiomasIsEn();
        var cantidadLibros = libros.size();
        System.out.println("La cantidad de libros en ingles es: " + cantidadLibros);
    }

    private void top10LibroMasDescargados() {
        List<Libro> libros = libroRepository.findTop10ByOrderByNumeroDescargaDesc();
        for (Libro libro : libros){
            System.out.println("""
            Título: %s
            Autor(es): %s
            Idiomas: %s
            Número de descargas: %d
            """.formatted(
                    libro.getTitulo(),
                    libro.getAutores().stream().map(Autor::getName).collect(Collectors.joining(", ")),
                    String.join(", ", libro.getIdiomas()),
                    libro.getNumeroDescarga()
            ));
        }
    }

    private void buscarAutoresPorNombre() {
        System.out.println("Introduce el autor del libro");
        var nombreAutor = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "/books/?search=" + nombreAutor.replace(" ", "%20"));
        Resultados resultados = conversor.convertirDatos(json, Resultados.class);
        for (DatosLibro datosLibro : resultados.libros()){
            var listaAutores = datosLibro.autor().stream().map(datosAutor -> new Autor(null, datosAutor.name(), datosAutor.birthYear(), datosAutor.deathYear())).toList();

            System.out.println("""
            Se encontro este libro con el autor ingresado:
            Título: %s
            Autor(es): %s
            Idiomas: %s
            Número de descargas: %d
            """.formatted(
                    datosLibro.titulo(),
                    listaAutores.stream().map(Autor::getName).collect(Collectors.joining(", ")),
                    String.join(", ", datosLibro.idiomas()),
                    datosLibro.numeroDescargas()
            ));
        }
    }
}
