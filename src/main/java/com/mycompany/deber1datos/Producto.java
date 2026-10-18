/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.deber1datos;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nombre;
    private double precio;
    private int stock;

    // Constructor vacío obligatorio para Hibernate
    public Producto() {}
    
    // Constructor para crear productos fácilmente
    public Producto(String nombre, double precio, int stock) { 
        this.nombre = nombre; 
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    @Override
    public String toString() {
        return "Producto [ID=" + id + ", Nombre=" + nombre + ", Precio=$" + precio + ", Stock=" + stock + "]";
    }
}