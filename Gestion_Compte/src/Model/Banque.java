package Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import javax.swing.JOptionPane;

public class Banque {
    private Map<Integer, Client> clients;
    private Map<String, Compte> comptes;
    private static final String DATA_DIR = "data";
    private static final String SAVE_FILE = DATA_DIR + "/banque_data.ser";
    
    public Banque() {
        this.clients = new HashMap<>();
        this.comptes = new HashMap<>();
        createDataDirectory();
    }
    
    private void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            if (!dataDir.mkdir()) {
                System.err.println("Impossible de créer le dossier data");
            }
        }
    }
    
    public void ajouterClient(Client client) {
        clients.put(client.getId(), client);
        sauvegarderDonnees();
    }
    
    public void creerCompte(Compte compte) {
        comptes.put(compte.getNumero(), compte);
        sauvegarderDonnees();
    }
    
    public boolean effectuerTransfert(String numeroCompteSource, String numeroCompteDestination, double montant) {
        Compte source = comptes.get(numeroCompteSource);
        Compte destination = comptes.get(numeroCompteDestination);
        
        if (source != null && destination != null) {
            if (source.debiter(montant)) {
                destination.crediter(montant);
                return true;
            }
        }
        return false;
    }
    
    public List<Transaction> getHistoriqueCompte(String numeroCompte) {
        Compte compte = comptes.get(numeroCompte);
        return compte != null ? compte.getHistorique() : new ArrayList<>();
    }
    
    public Collection<Client> getClients() {
        return clients.values();
    }
    
    public List<Compte> getComptesClient(int clientId) {
        List<Compte> comptesClient = new ArrayList<>();
        for (Compte compte : comptes.values()) {
            if (compte.getProprietaire().getId() == clientId) {
                comptesClient.add(compte);
            }
        }
        return comptesClient;
    }
    
    public Collection<Compte> getComptes() {
        return comptes.values();
    }
    
    public void sauvegarderDonnees() {
        createDataDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(clients);
            oos.writeObject(comptes);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la sauvegarde des données : " + e.getMessage(),
                "Erreur de sauvegarde",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void chargerDonnees() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                clients = (Map<Integer, Client>) ois.readObject();
                comptes = (Map<String, Compte>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors du chargement des données : " + e.getMessage(),
                    "Erreur de chargement",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void supprimerClient(int clientId) {
        List<Compte> comptesClient = getComptesClient(clientId);
        for (Compte compte : comptesClient) {
            comptes.remove(compte.getNumero());
        }
        clients.remove(clientId);
        sauvegarderDonnees();
    }
    
    public void supprimerCompte(String numeroCompte) {
        comptes.remove(numeroCompte);
        sauvegarderDonnees();
    }
    
    public void modifierClient(int id, String nouveauNom, String nouveauPrenom, String nouveauEmail) {
        Client client = clients.get(id);
        if (client != null) {
            client.setNom(nouveauNom);
            client.setPrenom(nouveauPrenom);
            client.setEmail(nouveauEmail);
            sauvegarderDonnees();
        }
    }
    
    public void modifierCompte(String ancienNumero, String nouveauNumero, double nouveauParametre) {
        Compte compte = comptes.remove(ancienNumero);
        if (compte != null) {
            compte.setNumero(nouveauNumero);
            if (compte instanceof CompteCourant) {
                ((CompteCourant) compte).setDecouvertAutorise(nouveauParametre);
            } else if (compte instanceof CompteEpargne) {
                ((CompteEpargne) compte).setTauxInteret(nouveauParametre);
            }
            comptes.put(nouveauNumero, compte);
            sauvegarderDonnees();
        }
    }
}