package Model;

import Model.Client;
import Model.Compte;
import Model.Transaction;
import Model.TypeTransaction;

public class CompteCourant extends Compte {
    private static final long serialVersionUID = 1L;
    private double decouvertAutorise;
    
    public CompteCourant(String numero, Client proprietaire, double decouvertAutorise) {
        super(numero, proprietaire);
        this.decouvertAutorise = decouvertAutorise;
    }
    
    @Override
    public boolean debiter(double montant) {
        if (solde - montant >= -decouvertAutorise) {
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
    
    public double getDecouvertAutorise() { return decouvertAutorise; }
    public void setDecouvertAutorise(double decouvertAutorise) { 
        this.decouvertAutorise = decouvertAutorise; 
    }
}