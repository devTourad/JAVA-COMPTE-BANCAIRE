package Model;


import java.io.Serializable;

public class CompteEpargne extends Compte {
    private static final long serialVersionUID = 1L;
    private double tauxInteret;
    
    public CompteEpargne(String numero, Client proprietaire, double tauxInteret) {
        super(numero, proprietaire);
        this.tauxInteret = tauxInteret;
    }
    
    @Override
    public boolean debiter(double montant) {
        if (solde >= montant) {
            solde -= montant;
            ajouterTransaction(new Transaction(TypeTransaction.DEBIT, montant, this));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean crediter(double montant) {
        solde += montant;
        ajouterTransaction(new Transaction(TypeTransaction.CREDIT, montant, this));
        return true;
    }
    
    public void calculerInterets() {
        double interets = solde * tauxInteret;
        crediter(interets);
    }
    
    public double getTauxInteret() { return tauxInteret; }
    public void setTauxInteret(double tauxInteret) { 
        this.tauxInteret = tauxInteret; 
    }
}