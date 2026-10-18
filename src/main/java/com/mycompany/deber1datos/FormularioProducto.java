/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.deber1datos;

import javax.swing.*;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class FormularioProducto extends JFrame {
    
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnNuevo; 
    
    private JComboBox<String> cbProductos;
    private JButton btnEditar;
    private JButton btnEliminar;
    
    private List<Producto> listaProductos; 
    private int idProductoEdicion = -1; 

    public FormularioProducto() {
        setTitle("TESQUIMSA - CRUD con ORM");
        setSize(450, 420); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // --- CAMPOS DE TEXTO ---
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 20, 100, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(130, 20, 200, 25);
        add(txtNombre);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(30, 60, 100, 25);
        add(lblPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(130, 60, 200, 25);
        add(txtPrecio);

        JLabel lblStock = new JLabel("Stock:");
        lblStock.setBounds(30, 100, 100, 25);
        add(lblStock);

        txtStock = new JTextField();
        txtStock.setBounds(130, 100, 200, 25);
        add(txtStock);

        // --- BOTONES CREAR / ACTUALIZAR / LIMPIAR ---
        btnGuardar = new JButton("Guardar (Crear)");
        btnGuardar.setBounds(30, 140, 140, 30);
        add(btnGuardar);
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(180, 140, 140, 30);
        btnActualizar.setEnabled(false); 
        add(btnActualizar);

        btnNuevo = new JButton("Nuevo / Limpiar Cajas");
        btnNuevo.setBounds(30, 180, 290, 25);
        add(btnNuevo);

        // --- SECCIÓN LEER / ELIMINAR / EDITAR ---
        JLabel lblEliminar = new JLabel("Lista (Leer):");
        lblEliminar.setBounds(30, 230, 100, 25);
        add(lblEliminar);

        cbProductos = new JComboBox<>();
        cbProductos.setBounds(130, 230, 200, 25);
        add(cbProductos);

        btnEditar = new JButton("Editar");
        btnEditar.setBounds(30, 270, 140, 30);
        add(btnEditar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(180, 270, 140, 30);
        add(btnEliminar);

        // --- ACCIONES DE LOS BOTONES ---
        btnGuardar.addActionListener(e -> crearProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnEditar.addActionListener(e -> prepararEdicion());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnNuevo.addActionListener(e -> limpiarCajas()); 

        cargarProductosEnCombo();
    }

    private SessionFactory getFactory() {
        Configuration config = new Configuration();
        config.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
        config.setProperty("hibernate.connection.url", "jdbc:sqlite:tesquimsa.db");
        config.setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        config.setProperty("hibernate.show_sql", "false");
        config.setProperty("hibernate.hbm2ddl.auto", "update");
        return config.addAnnotatedClass(Producto.class).buildSessionFactory();
    }

    // 1. CREAR (Create)
    private void crearProducto() {
        try {
            String nombre = txtNombre.getText().trim();
            // Reemplazamos comas por puntos y limpiamos espacios
            double precio = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
            int stock = Integer.parseInt(txtStock.getText().trim());

            if(nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
                return;
            }

            try (Session session = getFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                Producto nuevo = new Producto(nombre, precio, stock);
                session.persist(nuevo);
                tx.commit();
                JOptionPane.showMessageDialog(this, "✅ ¡Producto Creado!");
                limpiarCajas();
                cargarProductosEnCombo(); 
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: En Precio y Stock solo debes escribir números (Ej: 12.50 o 12,50). No letras ni símbolos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage());
        }
    }

    // 2. LEER (Read)
    private void cargarProductosEnCombo() {
        cbProductos.removeAllItems(); 
        try (Session session = getFactory().openSession()) {
            listaProductos = session.createQuery("from Producto", Producto.class).list();
            for (Producto p : listaProductos) {
                cbProductos.addItem("ID " + p.getId() + " - " + p.getNombre() + " ($" + p.getPrecio() + ")");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 3. ACTUALIZAR (Update) - Preparar
    private void prepararEdicion() {
        int index = cbProductos.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la lista primero.");
            return;
        }
        Producto p = listaProductos.get(index);
        txtNombre.setText(p.getNombre());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        
        idProductoEdicion = p.getId(); 
        btnGuardar.setEnabled(false); 
        btnActualizar.setEnabled(true); 
    }

    // 3. ACTUALIZAR (Update) - Guardar cambios
    private void actualizarProducto() {
        try {
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
            int stock = Integer.parseInt(txtStock.getText().trim());

            try (Session session = getFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                Producto p = session.get(Producto.class, idProductoEdicion);
                if (p != null) {
                    p.setNombre(nombre);
                    p.setPrecio(precio);
                    p.setStock(stock);
                    session.merge(p); 
                    tx.commit();
                    JOptionPane.showMessageDialog(this, "🔄 ¡Producto Actualizado!");
                    limpiarCajas();
                    cargarProductosEnCombo();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Revisa que el Precio y el Stock sean números válidos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar.");
        }
    }

    // 4. BORRAR (Delete)
    private void eliminarProducto() {
        int index = cbProductos.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la lista.");
            return;
        }
        Producto productoABorrar = listaProductos.get(index);

        try (Session session = getFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Producto p = session.get(Producto.class, productoABorrar.getId());
            if (p != null) {
                session.remove(p);
                tx.commit();
                JOptionPane.showMessageDialog(this, "🗑️ ¡Producto Eliminado!");
                limpiarCajas();
                cargarProductosEnCombo(); 
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar.");
        }
    }

    // Limpiar todo para el siguiente producto
    private void limpiarCajas() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        idProductoEdicion = -1;
        btnGuardar.setEnabled(true);    
        btnActualizar.setEnabled(false); 
        cbProductos.setSelectedIndex(-1); 
    }
}