package views;

import controllers.PossederController;
import models.Posseder;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
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

            // Récupérer les noms du propriétaire et du véhicule
            String nomProprietaire = controller.getProprietaireNameById(posseder.getIdProprietaire());
            String nomVehicule = controller.getVehiculeNameById(posseder.getIdVehicule());

            // Afficher les détails de la propriété
            JLabel possederLabel = new JLabel(
                    "Propriétaire: " + nomProprietaire +
                            ", Véhicule: " + nomVehicule +
                            ", Début: " + posseder.getDateDebutPropriete() +
                            ", Fin: " + (posseder.getDateFinPropriete() != null ? posseder.getDateFinPropriete() : "En cours"));

            // Afficher l'état actuel de la propriété
            JLabel etatLabel = new JLabel(posseder.estProprietaireActuel() ? " (Actuel)" : " (Terminé)");
            etatLabel.setForeground(posseder.estProprietaireActuel() ? Color.GREEN : Color.RED);

            // Bouton pour modifier la relation
            JButton modifyButton = new JButton("Modifier");
            modifyButton.addActionListener(e -> {
                JTextField dateDebutField = new JTextField(posseder.getDateDebutPropriete().toString());
                JTextField dateFinField = new JTextField(posseder.getDateFinPropriete() != null ? posseder.getDateFinPropriete().toString() : "");

                Object[] message = {
                        "Date de début (YYYY-MM-DD):", dateDebutField,
                        "Date de fin (YYYY-MM-DD, facultatif):", dateFinField
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Modifier la relation", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        Date newDateDebut = Date.valueOf(dateDebutField.getText());
                        Date newDateFin = dateFinField.getText().isEmpty() ? null : Date.valueOf(dateFinField.getText());

                        // Validation des dates
                        if (newDateFin != null && newDateDebut.after(newDateFin)) {
                            JOptionPane.showMessageDialog(this, "La date de début doit être antérieure à la date de fin.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        controller.updatePosseder(posseder.getIdProprietaire(), posseder.getIdVehicule(), newDateDebut, newDateFin);
                        refreshView();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la relation : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Bouton pour supprimer la relation
            JButton deleteButton = new JButton("Supprimer");
            deleteButton.addActionListener(e -> {
                int confirmation = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer cette relation ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    controller.deletePosseder(posseder.getIdProprietaire(), posseder.getIdVehicule());
                    refreshView();
                }
            });

            possederPanel.add(possederLabel);
            possederPanel.add(etatLabel);
            possederPanel.add(modifyButton);
            possederPanel.add(deleteButton);
            panel.add(possederPanel);
        }

        // Panel pour les boutons Ajouter et Retour
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton pour ajouter une nouvelle relation
        JButton addButton = new JButton("Ajouter une relation");
        addButton.addActionListener(e -> {
            JTextField idProprietaireField = new JTextField();
            JTextField idVehiculeField = new JTextField();
            JTextField dateDebutField = new JTextField();
            JTextField dateFinField = new JTextField();

            Object[] message = {
                    "ID Propriétaire:", idProprietaireField,
                    "ID Véhicule:", idVehiculeField,
                    "Date de début (YYYY-MM-DD):", dateDebutField,
                    "Date de fin (YYYY-MM-DD, facultatif):", dateFinField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ajouter une relation", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int idProprietaire = Integer.parseInt(idProprietaireField.getText());
                    int idVehicule = Integer.parseInt(idVehiculeField.getText());
                    Date dateDebut = Date.valueOf(dateDebutField.getText());
                    Date dateFin = dateFinField.getText().isEmpty() ? null : Date.valueOf(dateFinField.getText());

                    // Validation des dates
                    if (dateFin != null && dateDebut.after(dateFin)) {
                        JOptionPane.showMessageDialog(this, "La date de début doit être antérieure à la date de fin.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    controller.addPosseder(idProprietaire, idVehicule, dateDebut, dateFin);
                    refreshView();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la relation : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Bouton Retour
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> dispose());

        // Ajouter les boutons au panel
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel);

        // Ajouter un JScrollPane pour gérer le défilement
        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);

        setVisible(true);
    }

    // Rafraîchir la vue
    private void refreshView() {
        dispose();
        new PossederView();
    }

    public static void main(String[] args) {
        new PossederView();
    }
}
