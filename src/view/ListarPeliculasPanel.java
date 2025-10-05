package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import model.Pelicula;

/**
 *
 * @author kabes
 */
public class ListarPeliculasPanel extends javax.swing.JPanel {

    // Componentes de filtros
    private final JComboBox<String> cmbGenero;
    private final JSpinner spnAnioDesde;
    private final JSpinner spnAnioHasta;
    private final JButton btnFiltrar;
    private final JButton btnLimpiarFiltros;
    private final JButton btnListarTodas;

    // Tabla y modelo
    private final JTable tablePeliculas;
    private final DefaultTableModel tableModel;

    // Componente para mostrar total
    private final JLabel lblTotal;
    // Referencia al sorter
    private TableRowSorter<DefaultTableModel> sorter;

    // Columnas de la tabla
    private final String[] columnNames = {"ID", "Título", "Director", "Año", "Duración (min)", "Género"};

    /**
     * Crea un nuevo formulario ListarPeliculasPanel
     */
    public ListarPeliculasPanel() {
        setLayout(new BorderLayout());

        // Inicializar TODOS los componentes primero
        String[] generos = {"TODOS", "ACCION", "COMEDIA", "DRAMA", "TERROR", "CIENCIA_FICCION", "ROMANCE", "AVENTURA"};
        cmbGenero = new JComboBox<>(generos);

        spnAnioDesde = new JSpinner(new SpinnerNumberModel(1900, 1900, 2030, 1));
        spnAnioHasta = new JSpinner(new SpinnerNumberModel(2030, 1900, 2030, 1));

        // Configurar formato solo para enteros
        JSpinner.NumberEditor editorDesde = new JSpinner.NumberEditor(spnAnioDesde, "#");
        JSpinner.NumberEditor editorHasta = new JSpinner.NumberEditor(spnAnioHasta, "#");
        spnAnioDesde.setEditor(editorDesde);
        spnAnioHasta.setEditor(editorHasta);

        btnFiltrar = new JButton("Aplicar Filtros");
        btnLimpiarFiltros = new JButton("Limpiar Filtros");
        btnListarTodas = new JButton("Listar Todas");

        // Configurar tabla con ordenamiento
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }

            // AGREGAR: Especificar tipos de columnas para ordenamiento correcto
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class; // ID
                    case 1:
                        return String.class;  // Título
                    case 2:
                        return String.class;  // Director
                    case 3:
                        return Integer.class; // Año
                    case 4:
                        return Integer.class; // Duración
                    case 5:
                        return String.class;  // Género
                    default:
                        return String.class;
                }
            }
        };

        tablePeliculas = new JTable(tableModel);
        // Crear y configurar el TableRowSorter explícitamente
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tablePeliculas.setRowSorter(sorter);
        // Guardar referencia al sorter como campo de la clase
        this.sorter = sorter;

        // OPCIONAL: Configurar ordenamiento personalizado para columnas específicas
        configurarOrdenamiento();
        // OPCIONAL: Configurar ordenamiento personalizado para columnas específicas
        configurarOrdenamiento();

        // Inicializar label
        lblTotal = new JLabel("0");

        // DESPUÉS inicializar componentes
        initComponents();
    }
    
    private void configurarOrdenamiento() {
        // Obtener el TableRowSorter
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter
                = (javax.swing.table.TableRowSorter<DefaultTableModel>) tablePeliculas.getRowSorter();

        // Configurar comparadores personalizados si es necesario
        java.util.Comparator<String> generoComparator = (s1, s2) -> {
            // Ordenamiento personalizado para géneros si lo deseas
            return s1.compareToIgnoreCase(s2);
        };

        // Aplicar comparador personalizado para la columna de género (índice 5)
        sorter.setComparator(5, generoComparator);

        // OPCIONAL: Establecer ordenamiento inicial (por ID ascendente)
        java.util.List<javax.swing.RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new javax.swing.RowSorter.SortKey(0, javax.swing.SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Filtro por género
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFiltros.add(new JLabel("Género:"), gbc);
        gbc.gridx = 1;
        panelFiltros.add(cmbGenero, gbc);

        // Filtro por rango de años
        gbc.gridx = 2;
        gbc.gridy = 0;
        panelFiltros.add(new JLabel("Año desde:"), gbc);
        gbc.gridx = 3;
        panelFiltros.add(spnAnioDesde, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        panelFiltros.add(new JLabel("hasta:"), gbc);
        gbc.gridx = 5;
        panelFiltros.add(spnAnioHasta, gbc);

        // Botones
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFiltros.add(btnFiltrar, gbc);
        gbc.gridx = 1;
        panelFiltros.add(btnLimpiarFiltros, gbc);
        gbc.gridx = 2;
        panelFiltros.add(btnListarTodas, gbc);

        // Panel principal
        add(panelFiltros, BorderLayout.NORTH);

        // Tabla con scroll
        JScrollPane scrollPane = new JScrollPane(tablePeliculas);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        add(scrollPane, BorderLayout.CENTER);

        // Panel de información - USAR EL CAMPO DE LA CLASE, NO CREAR UNO NUEVO
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.add(new JLabel("Total de películas mostradas: "));
        panelInfo.add(lblTotal); // CAMBIAR ESTA LÍNEA - usar el campo de la clase
        add(panelInfo, BorderLayout.SOUTH);

        // Configurar ancho de columnas después de que la tabla esté agregada
        tablePeliculas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablePeliculas.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablePeliculas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablePeliculas.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablePeliculas.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablePeliculas.getColumnModel().getColumn(5).setPreferredWidth(120);

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    /**
     * Carga las películas en la tabla manteniendo el ordenamiento
     */
    @SuppressWarnings("unchecked")
    public void cargarPeliculas(List<Pelicula> peliculas) {
        // Guardar el ordenamiento actual
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter
                = (javax.swing.table.TableRowSorter<DefaultTableModel>) tablePeliculas.getRowSorter();
        java.util.List<javax.swing.RowSorter.SortKey> sortKeys = (java.util.List<javax.swing.RowSorter.SortKey>) sorter.getSortKeys();

        tableModel.setRowCount(0); // Limpiar tabla

        for (Pelicula p : peliculas) {
            Object[] fila = {
                p.getId(), // Integer para ordenamiento numérico correcto
                p.getTitulo(),
                p.getDirector(),
                p.getAnio(), // Integer para ordenamiento numérico correcto  
                p.getDuracion(), // Integer para ordenamiento numérico correcto
                p.getGenero().toString()
            };
            tableModel.addRow(fila);
        }

        // Restaurar el ordenamiento
        sorter.setSortKeys(sortKeys);

        // Actualizar contador - usar el número de filas mostradas (después del filtro)
        actualizarContador(tablePeliculas.getRowCount());
        lblTotal.repaint();
    }


    public void actualizarContador(int total) {
        lblTotal.setText(String.valueOf(total));
        lblTotal.repaint();
    }

    /**
     * Limpia los filtros y resetea la tabla
     */
    public void limpiarFiltros() {
        cmbGenero.setSelectedIndex(0);
        spnAnioDesde.setValue(1900);
        spnAnioHasta.setValue(2030);
        tableModel.setRowCount(0);
        lblTotal.setText("0");
    }

    // AGREGAR: Método para obtener el número real de filas (considerando filtros de tabla)
    public int getRowCount() {
        return tablePeliculas.getRowCount();
    }

// AGREGAR: Método para limpiar ordenamiento
    public void limpiarOrdenamiento() {
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter
                = (javax.swing.table.TableRowSorter<DefaultTableModel>) tablePeliculas.getRowSorter();
        sorter.setSortKeys(null);
    }

    // Getters para los componentes
    public JButton getBtnFiltrar() {
        return btnFiltrar;
    }

    public JButton getBtnLimpiarFiltros() {
        return btnLimpiarFiltros;
    }

    public JButton getBtnListarTodas() {
        return btnListarTodas;
    }

    public String getGeneroSeleccionado() {
        return (String) cmbGenero.getSelectedItem();
    }

    public int getAnioDesde() {
        return (Integer) spnAnioDesde.getValue();
    }

    public int getAnioHasta() {
        return (Integer) spnAnioHasta.getValue();
    }

    public JTable getTable() {
        return tablePeliculas;
    }

}
