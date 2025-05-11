package InterfaceGUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import Model.Banque;
import Model.Client;
import Model.Compte;
import Model.CompteCourant;
import Model.CompteEpargne;
import Model.Transaction;
import Model.TypeTransaction;

public class MainWindow extends JFrame {
    private Banque banque;
    private JPanel mainPanel;
    private DefaultListModel<Client> clientListModel;
    private DefaultListModel<Transaction> transactionListModel;
    private DefaultListModel<Compte> compteListModel;
    private JList<Client> clientList;
    private JList<Transaction> transactionList;
    private JList<Compte> compteList;
    private static final Color THEME_COLOR = new Color(25, 83, 163); // Bleu foncé
    
    public MainWindow(Banque banque) {
        this.banque = banque;
        this.clientListModel = new DefaultListModel<>();
        this.transactionListModel = new DefaultListModel<>();
        this.compteListModel = new DefaultListModel<>();
        
        // Configuration de la fenêtre principale
        setTitle("Simulateur de Banque");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Appliquer le thème
        applyTheme();
        
        // Création du menu
        createMenuBar();
        
        // Création du panel principal
        mainPanel = new JPanel(new BorderLayout());
        createMainPanel();
        
        // Charger la liste initiale des clients
        refreshClientList();
        
        add(mainPanel);
    }
    
    private void applyTheme() {
        // Ajuster les couleurs pour une meilleure lisibilité avec le bleu foncé
        Color lighterBlue = new Color(51, 122, 183);  // Version plus claire pour certains éléments
        Color brightBackground = new Color(240, 244, 248);  // Fond très clair
        
        UIManager.put("Panel.background", brightBackground);
        UIManager.put("MenuBar.background", THEME_COLOR);
        UIManager.put("Menu.background", THEME_COLOR);
        UIManager.put("Menu.foreground", Color.WHITE);  // Texte blanc pour le menu
        UIManager.put("MenuItem.background", lighterBlue);
        UIManager.put("MenuItem.foreground", Color.WHITE);
        UIManager.put("TitledBorder.titleColor", THEME_COLOR);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(THEME_COLOR);
        
        // Menu Clients
        JMenu clientMenu = new JMenu("Clients");
        JMenuItem addClient = new JMenuItem("Nouveau Client");
        JMenuItem editClient = new JMenuItem("Modifier Client");
        JMenuItem deleteClient = new JMenuItem("Supprimer Client");
        addClient.addActionListener(e -> showNewClientDialog());
        editClient.addActionListener(e -> showEditClientDialog());
        deleteClient.addActionListener(e -> deleteSelectedClient());
        clientMenu.add(addClient);
        clientMenu.add(editClient);
        clientMenu.add(deleteClient);
        
        // Menu Comptes
        JMenu accountMenu = new JMenu("Comptes");
        JMenuItem addAccount = new JMenuItem("Nouveau Compte");
        JMenuItem editAccount = new JMenuItem("Modifier Compte");
        JMenuItem deleteAccount = new JMenuItem("Supprimer Compte");
        JMenuItem transfer = new JMenuItem("Effectuer un transfert");
        addAccount.addActionListener(e -> showNewAccountDialog());
        editAccount.addActionListener(e -> showEditAccountDialog());
        deleteAccount.addActionListener(e -> deleteSelectedAccount());
        transfer.addActionListener(e -> showTransferDialog());
        accountMenu.add(addAccount);
        accountMenu.add(editAccount);
        accountMenu.add(deleteAccount);
        accountMenu.add(transfer);
        
        menuBar.add(clientMenu);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);
    }
    
    private void createMainPanel() {
        // Panel de gauche pour la liste des clients
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Clients"));
        clientList = new JList<>(clientListModel);
        clientList.setCellRenderer(new ClientCellRenderer());
        clientList.addListSelectionListener(e -> {
            updateCompteList();
            updateTransactionList();
        });
        leftPanel.add(new JScrollPane(clientList), BorderLayout.CENTER);
        
        // Panel central pour les comptes du client
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Comptes"));
        compteList = new JList<>(compteListModel);
        compteList.setCellRenderer(new CompteCellRenderer());
        compteList.addListSelectionListener(e -> updateTransactionList());
        centerPanel.add(new JScrollPane(compteList), BorderLayout.CENTER);
        
        // Panel de droite pour l'historique
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Historique des transactions"));
        transactionList = new JList<>(transactionListModel);
        transactionList.setCellRenderer(new TransactionCellRenderer());
        rightPanel.add(new JScrollPane(transactionList), BorderLayout.CENTER);
        
        // Ajout des panels au panel principal
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        // Définir les tailles préférées
        leftPanel.setPreferredSize(new Dimension(200, 600));
        centerPanel.setPreferredSize(new Dimension(300, 600));
        rightPanel.setPreferredSize(new Dimension(300, 600));
    }
    
    private void showNewClientDialog() {
        JDialog dialog = new JDialog(this, "Nouveau Client", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));
        
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField emailField = new JTextField();
        
        dialog.add(new JLabel("Nom:"));
        dialog.add(nomField);
        dialog.add(new JLabel("Prénom:"));
        dialog.add(prenomField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        
        JPanel buttonPanel = new JPanel();
        JButton valider = new JButton("Valider");
        JButton annuler = new JButton("Annuler");
        
        valider.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || 
                prenomField.getText().trim().isEmpty() || 
                emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Veuillez remplir tous les champs", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Capitaliser chaque mot du nom et prénom
            String nom = capitalizeEachWord(nomField.getText().trim());
            String prenom = capitalizeEachWord(prenomField.getText().trim());
            
            // Capitaliser la première lettre de chaque partie de l'email
            String email = capitalizeEmailParts(emailField.getText().trim());
            
            // Créer le nouveau client
            Client client = new Client(
                generateClientId(),
                nom,
                prenom,
                email
            );
            
            banque.ajouterClient(client);
            banque.sauvegarderDonnees(); // Sauvegarder immédiatement
            refreshClientList();
            dialog.dispose();
            
            // Afficher un message de confirmation
            JOptionPane.showMessageDialog(this, 
                "Client créé avec succès !", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        annuler.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(valider);
        buttonPanel.add(annuler);
        dialog.add(buttonPanel);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showNewAccountDialog() {
        JDialog dialog = new JDialog(this, "Nouveau Compte", true);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));
        
        Client selectedClient = clientList.getSelectedValue();
        if (selectedClient == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un client d'abord", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JComboBox<String> typeCompte = new JComboBox<>(new String[]{"Compte Courant", "Compte Épargne"});
        JTextField numeroField = new JTextField();
        JTextField montantInitialField = new JTextField("0.0");
        
        dialog.add(new JLabel("Type de compte:"));
        dialog.add(typeCompte);
        dialog.add(new JLabel("Numéro:"));
        dialog.add(numeroField);
        dialog.add(new JLabel("Montant initial:"));
        dialog.add(montantInitialField);
        
        JButton valider = new JButton("Valider");
        valider.addActionListener(e -> {
            try {
                String numero = numeroField.getText().trim();
                double montantInitial = Double.parseDouble(montantInitialField.getText());
                
                if (numero.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez saisir un numéro de compte");
                    return;
                }
                
                Compte nouveauCompte;
                if (typeCompte.getSelectedIndex() == 0) {
                    nouveauCompte = new CompteCourant(numero, selectedClient, 1000);
                } else {
                    nouveauCompte = new CompteEpargne(numero, selectedClient, 0.02);
                }
                
                if (montantInitial > 0) {
                    nouveauCompte.crediter(montantInitial);
                }
                
                banque.creerCompte(nouveauCompte);
                banque.sauvegarderDonnees();
                updateCompteList();
                
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    "Compte créé avec succès !", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Montant initial invalide");
            }
        });
        
        dialog.add(valider);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showTransferDialog() {
        // Vérifier d'abord si un client est sélectionné
        Client selectedClient = clientList.getSelectedValue();
        if (selectedClient == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un client d'abord", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Récupérer les comptes du client sélectionné
        List<Compte> comptesClient = banque.getComptesClient(selectedClient.getId());
        if (comptesClient.size() < 2) {
            JOptionPane.showMessageDialog(this, 
                "Le client doit avoir au moins deux comptes pour effectuer un transfert", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Effectuer un transfert", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        
        // Créer les modèles pour les combobox avec uniquement les comptes du client
        DefaultComboBoxModel<Compte> sourceModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<Compte> targetModel = new DefaultComboBoxModel<>();
        
        // Remplir les modèles avec les comptes du client sélectionné
        for (Compte compte : comptesClient) {
            sourceModel.addElement(compte);
            targetModel.addElement(compte);
        }
        
        JComboBox<Compte> sourceAccount = new JComboBox<>(sourceModel);
        JComboBox<Compte> targetAccount = new JComboBox<>(targetModel);
        
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        sourceAccount.setRenderer(renderer);
        targetAccount.setRenderer(renderer);
        
        JTextField montantField = new JTextField();
        
        dialog.add(new JLabel("Compte source:"));
        dialog.add(sourceAccount);
        dialog.add(new JLabel("Compte destination:"));
        dialog.add(targetAccount);
        dialog.add(new JLabel("Montant:"));
        dialog.add(montantField);
        
        JButton valider = new JButton("Valider");
        valider.addActionListener(e -> {
            try {
                Compte source = (Compte) sourceAccount.getSelectedItem();
                Compte destination = (Compte) targetAccount.getSelectedItem();
                
                if (source == destination) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Les comptes source et destination doivent être différents",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double montant = Double.parseDouble(montantField.getText().trim());
                if (montant <= 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Le montant doit être supérieur à 0",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (banque.effectuerTransfert(source.getNumero(), destination.getNumero(), montant)) {
                    dialog.dispose();
                    updateCompteList();
                    JOptionPane.showMessageDialog(this, 
                        "Transfert effectué avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Transfert impossible : solde insuffisant",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Montant invalide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton annuler = new JButton("Annuler");
        annuler.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(valider);
        buttonPanel.add(annuler);
        dialog.add(buttonPanel);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // Classe pour le rendu des comptes dans les combobox
    private class ComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Compte) {
                Compte compte = (Compte) value;
                String type = compte instanceof CompteCourant ? "Courant" : "Épargne";
                String proprietaire = compte.getProprietaire().getNom() + " " + 
                                    compte.getProprietaire().getPrenom();
                setText(String.format("%s - %s : %.2f € (%s)", 
                    type, compte.getNumero(), compte.getSolde(), proprietaire));
            }
            return this;
        }
    }
    
    private int generateClientId() {
        // Générer un ID unique en vérifiant qu'il n'existe pas déjà
        int id;
        do {
            id = (int) (Math.random() * 10000);
        } while (clientExists(id));
        return id;
    }
    
    private boolean clientExists(int id) {
        for (Client client : banque.getClients()) {
            if (client.getId() == id) {
                return true;
            }
        }
        return false;
    }
    
    private void updateTransactionList() {
        transactionListModel.clear();
        Compte selectedCompte = compteList.getSelectedValue();
        if (selectedCompte != null) {
            for (Transaction transaction : selectedCompte.getHistorique()) {
                transactionListModel.addElement(transaction);
            }
        }
    }
    
    private void refreshClientList() {
        clientListModel.clear();
        for (Client client : banque.getClients()) {
            clientListModel.addElement(client);
        }
        // Forcer la mise à jour de l'affichage
        clientList.repaint();
        System.out.println("Nombre de clients : " + clientListModel.getSize()); // Debug
    }
    
    private void refreshAccountList() {
        updateTransactionList();
    }
    
    private void updateCompteList() {
        compteListModel.clear();
        Client selectedClient = clientList.getSelectedValue();
        if (selectedClient != null) {
            List<Compte> comptes = banque.getComptesClient(selectedClient.getId());
            for (Compte compte : comptes) {
                compteListModel.addElement(compte);
            }
        }
    }
    
    private class CompteCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Compte) {
                Compte compte = (Compte) value;
                String type = compte instanceof CompteCourant ? "Courant" : "Épargne";
                setText(String.format("%s - %s : %.2f €", type, compte.getNumero(), compte.getSolde()));
            }
            return this;
        }
    }
    
    private class TransactionCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Transaction) {
                Transaction trans = (Transaction) value;
                String type = trans.getType() == TypeTransaction.CREDIT ? "+" : "-";
                setText(String.format("%s %.2f € - %s", type, trans.getMontant(), 
                    trans.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            }
            return this;
        }
    }
    
    private class ClientCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Client) {
                Client client = (Client) value;
                setText(String.format("<html>%s %s<br><font size='2'>%s</font></html>", 
                    client.getNom(),
                    client.getPrenom(),
                    client.getEmail()));
            }
            return this;
        }
    }
    
    // Nouvelle méthode pour supprimer un client
    private void deleteSelectedClient() {
        Client selectedClient = clientList.getSelectedValue();
        if (selectedClient == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un client à supprimer", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce client et tous ses comptes ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            banque.supprimerClient(selectedClient.getId());
            refreshClientList();
            updateCompteList();
            updateTransactionList();
        }
    }
    
    // Nouvelle méthode pour supprimer un compte
    private void deleteSelectedAccount() {
        Compte selectedCompte = compteList.getSelectedValue();
        if (selectedCompte == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un compte à supprimer", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce compte ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            banque.supprimerCompte(selectedCompte.getNumero());
            updateCompteList();
            updateTransactionList();
        }
    }
    
    // Nouvelle méthode pour capitaliser chaque mot
    private String capitalizeEachWord(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(word.substring(0, 1).toUpperCase())
                      .append(word.substring(1).toLowerCase());
            }
        }
        
        return result.toString();
    }
    
    // Nouvelle méthode pour capitaliser les parties de l'email
    private String capitalizeEmailParts(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        
        // Capitaliser la première lettre de la partie locale
        String localPart = parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1);
        
        // Capitaliser chaque partie du domaine
        String[] domainParts = parts[1].split("\\.");
        StringBuilder domain = new StringBuilder();
        
        for (int i = 0; i < domainParts.length; i++) {
            if (i > 0) {
                domain.append(".");
            }
            String part = domainParts[i];
            if (!part.isEmpty()) {
                domain.append(part.substring(0, 1).toUpperCase())
                      .append(part.substring(1).toLowerCase());
            }
        }
        
        return localPart + "@" + domain.toString();
    }
    
    // Nouvelle méthode pour modifier un client
    private void showEditClientDialog() {
        Client selectedClient = clientList.getSelectedValue();
        if (selectedClient == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un client à modifier", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Modifier Client", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));
        
        JTextField nomField = new JTextField(selectedClient.getNom());
        JTextField prenomField = new JTextField(selectedClient.getPrenom());
        JTextField emailField = new JTextField(selectedClient.getEmail());
        
        dialog.add(new JLabel("Nom:"));
        dialog.add(nomField);
        dialog.add(new JLabel("Prénom:"));
        dialog.add(prenomField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        
        JPanel buttonPanel = new JPanel();
        JButton valider = new JButton("Valider");
        JButton annuler = new JButton("Annuler");
        
        valider.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || 
                prenomField.getText().trim().isEmpty() || 
                emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Veuillez remplir tous les champs", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String nom = capitalizeEachWord(nomField.getText().trim());
            String prenom = capitalizeEachWord(prenomField.getText().trim());
            String email = capitalizeEmailParts(emailField.getText().trim());
            
            banque.modifierClient(selectedClient.getId(), nom, prenom, email);
            refreshClientList();
            dialog.dispose();
            
            JOptionPane.showMessageDialog(this, 
                "Client modifié avec succès !", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        annuler.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(valider);
        buttonPanel.add(annuler);
        dialog.add(buttonPanel);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // Nouvelle méthode pour modifier un compte
    private void showEditAccountDialog() {
        Compte selectedCompte = compteList.getSelectedValue();
        if (selectedCompte == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un compte à modifier", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Modifier Compte", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        
        JTextField numeroField = new JTextField(selectedCompte.getNumero());
        JTextField parametreField = new JTextField();
        
        if (selectedCompte instanceof CompteCourant) {
            dialog.add(new JLabel("Découvert autorisé:"));
            parametreField.setText(String.valueOf(((CompteCourant) selectedCompte).getDecouvertAutorise()));
        } else {
            dialog.add(new JLabel("Taux d'intérêt:"));
            parametreField.setText(String.valueOf(((CompteEpargne) selectedCompte).getTauxInteret()));
        }
        
        dialog.add(new JLabel("Numéro:"));
        dialog.add(numeroField);
        dialog.add(parametreField);
        
        JButton valider = new JButton("Valider");
        valider.addActionListener(e -> {
            try {
                String nouveauNumero = numeroField.getText().trim();
                double nouveauParametre = Double.parseDouble(parametreField.getText().trim());
                
                if (nouveauNumero.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez saisir un numéro de compte");
                    return;
                }
                
                banque.modifierCompte(selectedCompte.getNumero(), nouveauNumero, nouveauParametre);
                updateCompteList();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this, 
                    "Compte modifié avec succès !", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valeur invalide");
            }
        });
        
        dialog.add(valider);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
} 