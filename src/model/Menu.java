package model;

public class Menu {

    private int idMenu;
    private String namaMenu;
    private String kategori;
    private double harga;

    public Menu(int idMenu, String namaMenu, String kategori, double harga) {
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.kategori = kategori;
        this.harga = harga;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public String getKategori() {
        return kategori;
    }

    public double getHarga() {
        return harga;
    }
}