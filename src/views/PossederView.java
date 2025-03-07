package views;

import controllers.PossederController;
import models.Posseder;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class PossederView extends JFrame {
    private PossederController controller;

    public PossederView() {
        controller = new PossederController();

        setTitle("Gestion des Propriétés");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Charger les relations Posseder
        List<Posseder> posseders = controller.getAllPosseder();
        for (Posseder posseder : posseders) {
            JPanel possederPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel possederLabel = new JLabel(
                    "Propriétaire ID: " + posseder.getIdProprietaire() +
                            ", Véhicule ID: " + posseder.getIdVehicule() +
                            ", Début: " + posseder.getDateDebutPropriete() +
                            ", Fin: " + (posseder.getDateFinPropriete() != null ? posseder.getDateFinPropriete() : "En cours")
            );

            JButton deleteButton = new JButton("Supprimer");
            deleteButton.addActionListener(e -> {
                controller.deletePosseder(posseder.getIdProprietaire(), posseder.getIdVehicule());
                refreshView();
            });

            possederPanel.add(possederLabel);
            possederPanel.add(deleteButton);
            panel.add(possederPanel);
        }

        // Panel pour les boutons "Ajouter" et "Retour"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton "Ajouter une relation"
        JButton addButton = new JButton("Ajouter une relation");
        addButton.addActionListener(e -> {
            JTextField proprietaireField = new JTextField();
            JTextField vehiculeField = new JTextField();
            JTextField dateDebutField = new JTextField();
            JTextField dateFinField = new JTextField();

            Object[] message = {
                    "ID Propriétaire:", proprietaireField,
                    "ID Véhicule:", vehiculeField,
                    "Date de début (AAAA-MM-JJ):", dateDebutField,
                    "Date de fin (AAAA-MM-JJ, optionnel):", dateFinField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ajouter une relation", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int idProprietaire = Integer.parseInt(proprietaireField.getText());
                    int idVehicule = Integer.parseInt(vehiculeField.getText());
                    Date dateDebut = java.sql.Date.valueOf(dateDebutField.getText());
                    Date dateFin = dateFinField.getText().isEmpty() ? null : java.sql.Date.valueOf(dateFinField.getText());

                    refreshView();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur de saisie : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Bouton "Retour"
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> dispose());

        // Ajouter les boutons au panel
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        // Ajouter le panel des boutons au panneau principal
        panel.add(buttonPanel);

        // Ajouter un JScrollPane pour la barre de défilement
        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);

        setVisible(true);
    }

    // Rafraîchir la vue
    private void refreshView() {
        dispose();
        new PossederView();
    }
}   