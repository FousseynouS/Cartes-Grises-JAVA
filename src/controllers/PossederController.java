package controllers;

import database.DatabaseConnection;
import models.Posseder;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PossederController {

    // Récupérer toutes les relations Posseder
    public List<Posseder> getAllPosseder() {
        List<Posseder> posseders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM POSSEDER")) {

            while (rs.next()) {
                posseders.add(new Posseder(
                        rs.getInt("id_proprietaire"),
                        rs.getInt("id_vehicule"),
                        rs.getDate("date_debut_propriete"),
                        rs.getDate("date_fin_propriete")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posseders;
    }



    // Dans PossederController.java

// Méthode pour récupérer le nom d'un propriétaire par son ID
public String getProprietaireNameById(int idProprietaire) {
    String query = "SELECT nom, prenom FROM PROPRIETAIRE WHERE id_proprietaire = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, idProprietaire);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nom") + " " + rs.getString("prenom");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "Propriétaire inconnu";
}

// Méthode pour récupérer le nom d'un véhicule par son ID
public String getVehiculeNameById(int idVehicule) {
    String query = "SELECT matricule FROM VEHICULE WHERE id_vehicule = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, idVehicule);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("matricule");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "Véhicule inconnu";
}





    // Ajouter une relation Posseder
    public void addPosseder(int idProprietaire, int idVehicule, Date dateDebutPropriete, Date dateFinPropriete) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier si la relation existe déjà
            if (existsPosseder(conn, idProprietaire, idVehicule)) {
                showAlert("Erreur", "Cette relation existe déjà.");
                return;
            }

            // Insérer la nouvelle relation
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO POSSEDER (id_proprietaire, id_vehicule, date_debut_propriete, date_fin_propriete) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, idProprietaire);
                ps.setInt(2, idVehicule);
                ps.setDate(3, new java.sql.Date(dateDebutPropriete.getTime()));
                ps.setDate(4, dateFinPropriete != null ? new java.sql.Date(dateFinPropriete.getTime()) : null);
                ps.executeUpdate();
                showAlert("Succès", "La relation a été ajoutée avec succès !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'ajout de la relation.");
        }
    }

    // Supprimer une relation Posseder
    public void deletePosseder(int idProprietaire, int idVehicule) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Demander confirmation à l'utilisateur
            int confirmation = showConfirmation("Confirmation de suppression",
                    "Êtes-vous sûr de vouloir supprimer cette relation ?");

            if (confirmation == JOptionPane.YES_OPTION) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?")) {
                    ps.setInt(1, idProprietaire);
                    ps.setInt(2, idVehicule);
                    ps.executeUpdate();
                    showAlert("Succès", "La relation a été supprimée avec succès !");
                }
            } else {
                showAlert("Annulé", "La suppression a été annulée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la suppression de la relation.");
        }
    }

    
     public void updatePosseder(int idProprietaire, int idVehicule, Date newDateDebut, Date newDateFin) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE POSSEDER SET date_debut_propriete = ?, date_fin_propriete = ? " +
                 "WHERE id_proprietaire = ? AND id_vehicule = ?")) {
    
            // Définir les paramètres de la requête
            ps.setDate(1, newDateDebut);
            ps.setDate(2, newDateFin);
            ps.setInt(3, idProprietaire);
            ps.setInt(4, idVehicule);
    
            // Exécuter la mise à jour
            int rowsAffected = ps.executeUpdate();
    
            // Vérifier si la mise à jour a réussi
            if (rowsAffected == 0) {
                JOptionPane.showMessageDialog(null, "Erreur : La relation n'a pas été trouvée.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "La relation a été mise à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la mise à jour de la relation : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Vérifier si une relation existe déjà
    private boolean existsPosseder(Connection conn, int idProprietaire, int idVehicule) throws SQLException {
        String query = "SELECT COUNT(*) FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProprietaire);
            ps.setInt(2, idVehicule);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Afficher une alerte
    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Afficher une boîte de confirmation
    private int showConfirmation(String title, String message) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }
}
