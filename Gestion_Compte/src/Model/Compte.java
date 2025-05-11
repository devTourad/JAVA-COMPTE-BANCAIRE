package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Compte implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String numero;
    protected double solde;
    protected Client proprietaire;
    protected List<Transaction> historique;
    
    public Compte(String numero, Client proprietaire) {
        this.numero = numero;
        this.proprietaire = proprietaire;
        this.solde = 0.0;
        this.historique = new ArrayList<>();
    }
    
    // Méthodes abstraites à implémenter dans les classes filles
    public abstract boolean debiter(double montant);
    public abstract boolean crediter(double montant);
    
    // Méthode pour ajouter une transaction à l'historique
    protected void ajouterTransaction(Transaction transaction) {
        historique.add(transaction);
    }
    
    // Getters
    public String getNumero() { return numero; }
    public double getSolde() { return solde; }
    public List<Transaction> getHistorique() { return historique; }
    public Client getProprietaire() { return proprietaire; }
    
    // Setters
    public void setNumero(String numero) { this.numero = numero; }
}