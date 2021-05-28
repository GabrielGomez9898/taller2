package com.example.taller2_gabrielg_juanmendez;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Usuario {
    String uid;
    String username;
    String name;
    String apellido;
    String contraseña;
    Long numeroIdentificacion;
    double latitud;
    double longitud;
    boolean disponible;


    public Usuario (String uid, String username, String name,String apellido, Long numeroIdentificacion, double latitud, double longitud,boolean disponible) {
        this.uid = uid;
        this.username = username;
        this.name = name;
        this.apellido = apellido;
        this.numeroIdentificacion = numeroIdentificacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.disponible = disponible;


    }
    /*
    public Usuario (String uid, String username, String name, String contraseña, Date fechaNacimiento, long telefono, String direccion) {
        this.uid = uid;
        this.username = username;
        this.name = name;
        this.contraseña = contraseña;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    public Usuario (String username, String name, String contraseñaAntigua, String contraseñaNueva,  long telefono, String direccion) {
        this.username = username;
        this.name = name;
        this.contraseñaAntigua = contraseñaAntigua;
        this.contraseñaNueva = contraseñaNueva;
        this.telefono = telefono;
        this.direccion = direccion;
    }*/
    public Usuario(){
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Long getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(Long numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("username",username);
        result.put("name",name);
        result.put("contraseña",contraseña);
        result.put("identficacion",numeroIdentificacion);
        result.put("latitud",latitud);
        result.put("longitud",longitud);
        return result;
    }
}

