package controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import model.*;
import service.*;
import view.*;

/**
 * Controlador principal que coordina las interacciones entre la vista y los
 * servicios
 *
 * Responsabilidades: - Gestionar eventos de la interfaz de usuario - Validar
 * datos antes de enviarlos al servicio - Coordinar el flujo entre la vista y la
 * capa de negocio - Mostrar mensajes de feedback al usuario
 *
 * Patrón: Controller en arquitectura MVC
 */
import java.util.logging.Logger;

public class MainController {

    private static final Logger logger = Logger.getLogger(MainController.class.getName());
    private final MainFrame view;           // Referencia a la vista principal
    private final PeliculaService service;  // Referencia al servicio de negocio
    private final AtomicBoolean saving = new AtomicBoolean(false);  // Control de concurrencia

    /**
     * Constructor del controlador principal
     *
     * @param view Vista principal de la aplicación
     * @param service Servicio de gestión de películas
     */
    public MainController(MainFrame view, PeliculaService service) {
        this.view = view;
        this.service = service;
        bind(); // Configura los listeners de eventos
        init(); // Inicializacion adicional
    }

    /**
     * Inicialización del controlador
     */
    private void init() {
        // Mostrar el panel de agregar por defecto al iniciar
        view.mostrarPanel("AGREGAR");
        view.getFormPanel().clear();
    }

    /**
     * Configura los listeners para los eventos de la interfaz Vincula los
     * componentes de UI con sus respectivos manejadores
     */
    private void bind() {
        // Navegación entre paneles

        // Botón "Agregar" - Mostrar panel de agregar película
        view.getBtnAgregar().addActionListener(e -> {
            view.mostrarPanel("AGREGAR");
            view.getFormPanel().clear();
        });

        // Botón "Modificar" - Mostrar panel de modificar película
        view.getBtnModificar().addActionListener(e -> {
            view.mostrarPanel("MODIFICAR");
            view.getModificarPanel().limpiarFormulario();
        });

        // Botón "Eliminar" - Mostrar panel de eliminar película
        view.getBtnEliminar().addActionListener(e -> {
            view.mostrarPanel("ELIMINAR");
            view.getEliminarPanel().limpiarFormulario();
        });

        // Botón "Listar" - Mostrar panel de listar películas
        view.getBtnListar().addActionListener(e -> {
            view.mostrarPanel("LISTAR");
            onListarTodas(); // Cargar todas las películas al entrar
        });

        // Agregar película
        view.getFormPanel().getBtnGuardar().addActionListener(e -> onSave());

        // Modificar película
        ModificarPeliculaPanel modPanel = view.getModificarPanel();

        // Botón "Buscar" en el panel de modificar
        modPanel.getBtnBuscar().addActionListener(e -> onBuscarModificar());

        // Botón "Guardar Cambios" en el panel de modificar
        modPanel.getBtnGuardar().addActionListener(e -> onModificar());

        // Botón "Limpiar Formulario" en el panel de modificar
        modPanel.getBtnLimpiar().addActionListener(e -> {
            modPanel.limpiarFormulario();
            JOptionPane.showMessageDialog(view, "Formulario limpiado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Botón "Limpiar Búsqueda" en el panel de modificar
        modPanel.getBtnLimpiarBusqueda().addActionListener(e -> {
            modPanel.limpiarBusqueda();
            System.out.println("Búsqueda limpiada en panel MODIFICAR");
        });

        // Eliminar película
        EliminarPeliculaPanel delPanel = view.getEliminarPanel();

        // Botón "Buscar por ID" en el panel de eliminar
        delPanel.getBtnBuscar().addActionListener(e -> onBuscarEliminar());

        // Botón "Eliminar Película" en el panel de eliminar
        delPanel.getBtnEliminar().addActionListener(e -> onEliminar());

        // Botón "Limpiar Formulario" en el panel de eliminar
        delPanel.getBtnLimpiar().addActionListener(e -> {
            delPanel.limpiarFormulario();
            JOptionPane.showMessageDialog(view, "Formulario limpiado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Botón "Limpiar Búsqueda" en el panel de eliminar
        delPanel.getBtnLimpiarBusqueda().addActionListener(e -> {
            delPanel.limpiarBusqueda();
            System.out.println("Búsqueda limpiada en panel ELIMINAR");
        });

        // Listar películas
        ListarPeliculasPanel listarPanel = view.getListarPanel();

        // Botón "Listar Todas"
        listarPanel.getBtnListarTodas().addActionListener(e -> onListarTodas());

        // Botón "Aplicar Filtros"
        listarPanel.getBtnFiltrar().addActionListener(e -> onAplicarFiltros());

        // Botón "Limpiar Filtros"
        listarPanel.getBtnLimpiarFiltros().addActionListener(e -> {
            listarPanel.limpiarFiltros();
            onListarTodas();
        });

        listarPanel.getTable().getRowSorter().addRowSorterListener(e -> {
            if (e.getType() == javax.swing.event.RowSorterEvent.Type.SORT_ORDER_CHANGED) {
                System.out.println("Ordenamiento cambiado");
                // Actualizar contador después del ordenamiento
                listarPanel.actualizarContador(listarPanel.getTable().getRowCount());
            }
        });

    }

    /**
     * Maneja el evento de guardado de una película
     *
     * Flujo: 1. Obtiene datos del formulario 2. Convierte y valida los datos 3.
     * Invoca el servicio para persistir 4. Muestra feedback al usuario 5.
     * Limpia el formulario en caso de éxito
     */
    private void onSave() {
        if (saving.get()) {
            return;
        }
        saving.set(true);

        PeliculaFormPanel form = view.getFormPanel();
        try {
            System.out.println("Iniciando proceso de guardado de película...");

            // Validar campos obligatorios
            if (form.getTitulo().isEmpty() || form.getDirector().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Título y Director son campos obligatorios.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Convierte el género de String a Enum
            Genero genero = Genero.valueOf(form.getGenero());

            // Crea el objeto Pelicula con los datos del formulario
            Pelicula p = new Pelicula(
                    form.getTitulo(),
                    form.getDirector(),
                    form.getAnio(),
                    form.getDuracion(),
                    genero
            );

            // Persiste la película mediante el servicio
            int id = service.add(p);

            // Muestra mensaje de éxito con el ID generado
            JOptionPane.showMessageDialog(view, "Película guardada (ID: " + id + ")", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // Limpia el formulario para nueva entrada
            form.clear();

        } catch (IllegalArgumentException ex) { // valueOf falló
            // Error de conversión de género
            JOptionPane.showMessageDialog(view, "Género inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Libera el flag de guardado
            saving.set(false);
        }
    }

    private void onBuscarModificar() {
        ModificarPeliculaPanel panel = view.getModificarPanel();
        String busqueda = panel.getTextoBusqueda();

        if (busqueda.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingrese un título para buscar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<Pelicula> resultados = service.findByTitle(busqueda);

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(view, "No se encontraron películas con ese título.",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (resultados.size() == 1) {
                // Un solo resultado, cargar directamente
                panel.cargarPelicula(resultados.get(0));
                JOptionPane.showMessageDialog(view, "Película encontrada y cargada.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Múltiples resultados, mostrar selección
                String[] opciones = resultados.stream()
                        .map(p -> p.getId() + " - " + p.getTitulo() + " (" + p.getAnio() + ")")
                        .toArray(String[]::new);

                String seleccion = (String) JOptionPane.showInputDialog(view,
                        "Seleccione la película a modificar:",
                        "Múltiples resultados",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]);

                if (seleccion != null) {
                    int idSeleccionado = Integer.parseInt(seleccion.split(" - ")[0]);
                    Pelicula seleccionada = resultados.stream()
                            .filter(p -> p.getId() == idSeleccionado)
                            .findFirst()
                            .orElse(null);

                    if (seleccionada != null) {
                        panel.cargarPelicula(seleccionada);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onModificar() {
        ModificarPeliculaPanel panel = view.getModificarPanel();

        try {
            // Validar campos obligatorios
            if (panel.getTitulo().isEmpty() || panel.getDirector().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Título y Director son campos obligatorios.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (panel.getPeliculaId() == null) {
                JOptionPane.showMessageDialog(view, "No hay película cargada para modificar.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Genero genero = Genero.valueOf(panel.getGenero());

            Pelicula p = new Pelicula(
                    panel.getPeliculaId(),
                    panel.getTitulo(),
                    panel.getDirector(),
                    panel.getAnio(),
                    panel.getDuracion(),
                    genero
            );

            int confirmacion = JOptionPane.showConfirmDialog(view,
                    """
                    ¿Está seguro de que desea modificar esta película?
                    ID: """ + p.getId() + "\n"
                    + "Título: " + p.getTitulo(),
                    "Confirmar modificación",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                service.update(p);
                JOptionPane.showMessageDialog(view, "Película modificada exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                panel.limpiarFormulario();
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, "Género inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onBuscarEliminar() {
        EliminarPeliculaPanel panel = view.getEliminarPanel();
        String idTexto = panel.getTextoBusquedaId();

        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingrese un ID para buscar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            Pelicula pelicula = service.findById(id);
            panel.cargarPelicula(pelicula);
            JOptionPane.showMessageDialog(view, "Película encontrada.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "ID debe ser un número válido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEliminar() {
        EliminarPeliculaPanel panel = view.getEliminarPanel();

        if (panel.getPeliculaId() == null) {
            JOptionPane.showMessageDialog(view, "No hay película cargada para eliminar. Busque una película primero.", "Sin película cargada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int confirmacion = JOptionPane.showConfirmDialog(view,
                    """
                \u00bfEsta seguro de que desea ELIMINAR permanentemente esta película?
                ID: """ + panel.getPeliculaId() + "\n"
                    + "Título: " + panel.getTitulo() + "\n\n"
                    + "Esta acción no se puede deshacer.",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                service.delete(panel.getPeliculaId());
                JOptionPane.showMessageDialog(view, "Película eliminada exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                panel.limpiarFormulario();
                System.out.println("Película eliminada: ID= " + panel.getPeliculaId());
            } else {
                System.out.println("Eliminación cancelada por el usuario.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onListarTodas() {
        try {
            System.out.println("Iniciando carga de todas las películas...");
            List<Pelicula> peliculas = service.findAll();
            System.out.println("Películas obtenidas del service: " + peliculas.size());

            // DEBUG: Imprimir cada película
            for (Pelicula p : peliculas) {
                System.out.println("- " + p.getId() + ": " + p.getTitulo() + " (" + p.getAnio() + ") - " + p.getGenero());
            }

            view.getListarPanel().cargarPeliculas(peliculas);
            System.out.println("Cargadas " + peliculas.size() + " películas en la tabla");
        } catch (Exception ex) {
            logger.severe("Error al cargar películas: " + ex.getMessage());
            JOptionPane.showMessageDialog(view, "Error al cargar películas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAplicarFiltros() {
        ListarPeliculasPanel panel = view.getListarPanel();

        try {
            String genero = panel.getGeneroSeleccionado();
            int anioDesde = panel.getAnioDesde();
            int anioHasta = panel.getAnioHasta();

            System.out.println("Aplicando filtros - Género: " + genero + ", Rango: " + anioDesde + "-" + anioHasta);

            // Validar rango de años
            if (anioDesde > anioHasta) {
                JOptionPane.showMessageDialog(view,
                        "El año 'desde' no puede ser mayor que el año 'hasta'.",
                        "Error en filtros", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Pelicula> peliculasFiltradas = service.findWithFilters(genero, anioDesde, anioHasta);
            panel.cargarPeliculas(peliculasFiltradas);

            System.out.println("Filtros aplicados exitosamente. Resultados: " + peliculasFiltradas.size());

        } catch (Exception ex) {
            System.err.println("Error al aplicar filtros: " + ex.getMessage());
            JOptionPane.showMessageDialog(view, "Error al aplicar filtros: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
