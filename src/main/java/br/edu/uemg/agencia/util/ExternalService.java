package br.edu.uemg.agencia.util;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalService {

    private static final String API_URL = "https://economia.awesomeapi.com.br/last/";

    public static Double getCotacao(String moeda) {
        String codigo = moeda.trim().toUpperCase();
        String par = codigo + "-BRL";
        System.out.println(">> Buscando cotação para: " + par);

        try {
            URL url = new URL(API_URL + par);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() != 200) return null;

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) jsonBuilder.append(line);
            br.close();

            String json = jsonBuilder.toString();
            Pattern p = Pattern.compile("\"bid\"\\s*:\\s*\"([\\d\\.]+)\"");
            Matcher m = p.matcher(json);

            if (m.find()) return Double.parseDouble(m.group(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void abrirNoMapa(String destino) {
        if (destino == null || destino.trim().isEmpty()) return;
        try {
            String query = URLEncoder.encode(destino, StandardCharsets.UTF_8.toString());
            String mapUrl = "http://googleusercontent.com/maps.google.com/?q=" + query;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(mapUrl));
            } else {
                JOptionPane.showMessageDialog(null, "Navegador não suportado.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao abrir mapa: " + e.getMessage());
        }
    }

    public static void abrirHtml(File arquivo) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(arquivo.toURI());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getEnderecoViaCep(String cep) {
        String cepLimpo = cep.replace("-", "").replace(".", "").trim();
        if (cepLimpo.length() != 8) return null;

        String urlStr = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);

            if (conn.getResponseCode() != 200) return null;

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) json.append(line);
            br.close();

            String raw = json.toString();
            if (raw.contains("\"erro\": true")) return null;

            String logradouro = extractJson(raw, "logradouro");
            String bairro = extractJson(raw, "bairro");
            String localidade = extractJson(raw, "localidade");
            String uf = extractJson(raw, "uf");

            return new String[]{logradouro, bairro, localidade, uf};
        } catch (Exception e) {
            System.err.println("Erro ViaCEP: " + e.getMessage());
        }
        return null;
    }

    private static String extractJson(String json, String key) {
        String search = "\"" + key + "\": \"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}