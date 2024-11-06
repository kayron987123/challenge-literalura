package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.DatosLibro;
import com.alura.literalura.model.Libro;
import com.alura.literalura.model.Resultados;
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

    public Principal(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public void mostrarMenu(){
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    --------------------------------------------------------------------
                    Opciones que se pueden realizar
                    
                    1. Buscar libro por Titulo
                    2. Listar libros buscados
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
            }
        }
    }

    private void buscarLibroPorTitulo(){
        System.out.println("Introduce el titulo del libro");
        var nombretitulo = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "/books/?search=%20" + nombretitulo.replace(" ", "%20"));
        Resultados resultados = conversor.convertirDatos(json, Resultados.class);
        for (DatosLibro datosLibro : resultados.libros()){
            var listaAutores = datosLibro.autor().stream().map(datosAutor -> new Autor(null, datosAutor.name())).toList();

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
        List<Libro> libros = libroRepository.getLibrosWhereIdiomasIsEn();
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




}
