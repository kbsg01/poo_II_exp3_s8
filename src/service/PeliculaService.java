package service;

import dao.PeliculaDAO;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import model.Pelicula;

/**
 * Servicio de aplicación para la gestión de películas
 *
 * Responsabilidades: - Implementar la lógica de negocio - Validar datos antes
 * de persistir - Coordinar operaciones con el DAO - Manejar excepciones de
 * negocio
 *
 */
public class PeliculaService {

    private final PeliculaDAO dao; // DAO para operaciones de persistencia

    /**
     * Constructor que inyecta el DAO
     *
     * @param dao Implementación de PeliculaDAO
     */
    public PeliculaService(PeliculaDAO dao) {
        this.dao = dao;
    }

    /**
     * Crea una nueva película en la base de datos con validaciones de negocio
     *
     * Validaciones implementadas: -Título no nulo y no vacío - Directo no nulo
     * y no vacío - Año dentro de rango válido (1900 - año actual +1) - Duración
     * dentro de rango válido (1-999 minutos) - Prevención de duplicados (título
     * + año)
     *
     * @param p Película a crear
     * @return int ID generado por la base de datos
     * @throws Exception Si falla validación o persistencia
     */
    public int add(Pelicula p) throws Exception {
        // Validaciones de negocio (no en la vista)
        if (p.getTitulo() == null || p.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (p.getTitulo().length() > 100) {
            throw new IllegalArgumentException("El título no puede exceder 100 caracteres");
        }
        if (p.getDirector() == null || p.getDirector().isBlank()) {
            throw new IllegalArgumentException("El director es obligatorio.");
        }
        int currentMax = Year.now().getValue() + 1;
        if (p.getAnio() < 1900 || p.getAnio() > currentMax) {
            throw new IllegalArgumentException("El año debe estar entre 1900 y " + currentMax + ".");
        }
        if (p.getDuracion() < 1 || p.getDuracion() > 999) {
            throw new IllegalArgumentException("La duración debe estar entre 1 y 999.");
        }

        try {
            // Delegar la persistencia al DAO
            int id = dao.create(p);
            p.setId(id);
            return id;
        } catch (java.sql.SQLIntegrityConstraintViolationException dup) {
            // Manejar violación del constraint única (título + año)
            throw new IllegalArgumentException("Ya existe una película con el mismo TÍTULO y AÑO.");
        }
    }

    /*
        Busca una película por ID
        @param id ID de la película a buscar

    */
    public Pelicula findById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Pelicula pelicula = dao.findById(id);
        if (pelicula == null) {
            throw new IllegalArgumentException("No se encontró la pelicula con ID: " + id);
        }
        return pelicula;
    } 

    public List<Pelicula> findAll() throws Exception {
        return dao.findAll();
    }

    /**
     * Filtra películas por género y rango de años
     *
     * @param genero Género a filtrar
     * @param anioDesde Año inicial del rango
     * @param anioHasta Año final del rango
     * @return Lista de películas filtradas
     * @throws SQLException Si ocurre un error en la consulta
     */
    public List<Pelicula> findWithFilters(String genero, int anioDesde, int anioHasta) throws SQLException {
        System.out.println("Aplicando filtros - Género: " + genero + ", Años: " + anioDesde + "-" + anioHasta);

        List<Pelicula> resultado = List.of(); 
        try {
            List<Pelicula> todasLasPeliculas = dao.findAll();
            System.out.println("Total películas en BD: " + todasLasPeliculas.size());
    
            resultado = todasLasPeliculas.stream()
                    .filter(p -> {
                        // Filtro por género (si no es "TODOS")
                        boolean generoMatch = "TODOS".equals(genero) || p.getGenero().toString().equals(genero);
    
                        // Filtro por rango de años
                        boolean anioMatch = p.getAnio() >= anioDesde && p.getAnio() <= anioHasta;
    
                        // DEBUG: Imprimir cada película que se evalúa
                        System.out.println("Película: " + p.getTitulo() + " - Género: " + p.getGenero()
                                + " (" + generoMatch + "), Año: " + p.getAnio() + " (" + anioMatch + ")");
    
                        return generoMatch && anioMatch;
                    })
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Películas filtradas: " + resultado.size());
        return resultado;
    }

    public List<Pelicula> findByTitle(String query) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda no puede estar vacío.");
        }
        return dao.findByTitleLike(query.trim());
    }

    public void update(Pelicula p) throws Exception {
        validateMovieData(p);
        try {
            validateMovieData(p);
            dao.update(p);
        } catch (java.sql.SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Ya existe otra película con el mismo Título y Año.");
        }
    }

    public void delete(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        // Verificar que la película existe antes de eliminar
        Pelicula pelicula = dao.findById(id);
        if (pelicula == null) {
            throw new IllegalArgumentException("No se encontró la película con ID: " + id);
        }

        dao.delete(id);
    }

    private void validateMovieData(Pelicula p
    ) throws IllegalArgumentException {
        // Validaciones similares a add()
        try {
            if (p.getId() == null || p.getId() <= 0) throw new IllegalArgumentException("ID de película inválido.");
            
            if (p.getTitulo() == null || p.getTitulo().isBlank()) throw new IllegalArgumentException("El titulo es obligatorio.");
            if (p.getDirector() == null || p.getDirector().isBlank()) throw new IllegalArgumentException("El director es obligatorio.");
            int currentMax = Year.now().getValue() + 1;
            if (p.getAnio() < 1900 || p.getAnio() > currentMax) throw new IllegalArgumentException("El año debe estar entre 1900 y " + currentMax + ".");
            if (p.getDuracion() < 1 || p.getDuracion() > 999) throw new IllegalArgumentException("La duración debe estar entre 1 y 999.");
        } catch (IllegalArgumentException exeption) {
            throw new IllegalArgumentException(exeption.getMessage());
        }
    } 
}
