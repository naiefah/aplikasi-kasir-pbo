package model;

import java.util.Date;

public class Order {
    private int idOrder;
    private Date tanggal;
    private double total;

    public Order(int idOrder, Date tanggal, double total) {
        this.idOrder = idOrder;
        this.tanggal = tanggal;
        this.total = total;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public double getTotal() {
        return total;
    }
}