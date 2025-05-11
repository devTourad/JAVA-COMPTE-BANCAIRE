package Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDateTime date;
    private TypeTransaction type;
    private double montant;
    private Compte compte;
    
    public Transaction(TypeTransaction type, double montant, Compte compte) {
        this.date = LocalDateTime.now();
        this.type = type;
        this.montant = montant;
        this.compte = compte;
    }
    
    // Getters
    public LocalDateTime getDate() { return date; }
    public TypeTransaction getType() { return type; }
    public double getMontant() { return montant; }
    public Compte getCompte() { return compte; }
    
    @Override
    public String toString() {
        return "Transaction{" +
               "date=" + date +
               ", type=" + type +
               ", montant=" + montant +
               ", compte=" + compte.getNumero() +
               '}';
    }
}