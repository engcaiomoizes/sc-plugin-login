package br.com.caiomoizes.scLogin;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class Database {
    private Connection connection;

    public void setup(File dataFolder) throws SQLException {
        if (!dataFolder.exists()) dataFolder.mkdirs();

        // Conecta ao arquivo 'database.db' na pasta do plugin
        String url = "jdbc:sqlite:" + dataFolder.getPath() + "/database.db";
        connection = DriverManager.getConnection(url);

        // Cria a tabela se ela não existir
        try (PreparedStatement st = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS players (" +
                        "username TEXT PRIMARY KEY, " +
                        "password TEXT, " +
                        "is_premium BOOLEAN, " +
                        "uuid TEXT, " +
                        "last_ip TEXT)"
        )) {
            st.execute();
        }
    }

    public Boolean isPlayerPremium(String username) {
        try (PreparedStatement st = connection.prepareStatement(
                "SELECT is_premium FROM players WHERE username = ?"
        )) {
            st.setString(1, username.toLowerCase());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_premium");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPremiumStatus(String username, boolean isPremium, UUID uuid) {
        String uuidString = (uuid != null) ? uuid.toString() : null;

        String sql = "INSERT INTO players (username, is_premium, uuid) VALUES (?, ?, ?) " +
                "ON CONFLICT(username) DO UPDATE SET is_premium = excluded.is_premium, uuid = excluded.uuid";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username.toLowerCase());
            st.setBoolean(2, isPremium);
            st.setString(3, uuidString);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getSavedUUID(String username) {
        String sql = "SELECT uuid FROM players WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username.toLowerCase());
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String uuidString = rs.getString("uuid");
                if (uuidString != null && !uuidString.isEmpty())
                    return UUID.fromString(uuidString);
            }
        } catch (SQLException | IllegalArgumentException e) {
            SCLogin.getInstance().getLogger().warning("Erro ao carregar UUID de " + username);
        }
        return null;
    }

    public void savePlayer(String username, String password, boolean isPremium) {
        String sql = "INSERT OR REPLACE INTO players (username, password, is_premium) VALUES (?, ?, ?)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username.toLowerCase());
            st.setString(2, password);
            st.setBoolean(3, isPremium);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayer(String username) {
        if (!playerExists(username)) return;

        String sql = "DELETE FROM players WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username.toLowerCase());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(String username) {
        String sql = "SELECT 1 FROM players WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username.toLowerCase());
            return st.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getPassword(String username) {
        String sql = "SELECT password FROM players WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username.toLowerCase());
            ResultSet rs = st.executeQuery();
            return rs.next() ? rs.getString("password") : null;
        } catch (SQLException e) {
            return null;
        }
    }

    public void updatePassword(String username, String newPassword) {
        if (!playerExists(username)) return;

        String sql = "UPDATE players SET password = ? WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, newPassword);
            st.setString(2, username.toLowerCase());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
