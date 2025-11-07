package monaldichristiansmtp;

import java.io.*;
import java.net.Socket;

public class SMTPClient {
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private SMTPResponse lastResponse;

    public SMTPClient(String server) throws IOException {
        socket = new Socket(server, 25);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        lastResponse = readServerResponse();
        System.out.println("Connesso al server: " + lastResponse);
    }

    private SMTPResponse readServerResponse() throws IOException {
        StringBuilder rawResponse = new StringBuilder();
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Connessione chiusa per errore nella connessione");
        }
        rawResponse.append(line).append("\n");

        while (line.length() >= 4 && line.charAt(3) == '-') {
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Connessione chiusa per errore nella connessione");
            }
            rawResponse.append(line).append("\n");
        }

        SMTPResponse parsed = SMTPResponseParser.parseMultiLine(rawResponse.toString());
        if (parsed == null) {
            return new SMTPResponse(-1, "", rawResponse.toString());
        }
        return parsed;
    }

    public SMTPResponse sendCommand(String command) throws IOException {
        System.out.println("Comando Inviato: " + command);
        writer.write(command + "\r\n");
        writer.flush();
        lastResponse = readServerResponse();
        System.out.println("Risposta: " + lastResponse.getMessage());
        return lastResponse;
    }

    public SMTPResponse helo(String server) throws IOException {
        return sendCommand("HELO " + server);
    }

    public SMTPResponse from(String email) throws IOException {
        return sendCommand("MAIL FROM:<" + email + ">");
    }

    public SMTPResponse to(String email) throws IOException {
        return sendCommand("RCPT TO:<" + email + ">");
    }

    public SMTPResponse data(String from, String to, String subject, String message) throws IOException {
        sendCommand("DATA");

        StringBuilder sb = new StringBuilder();
        sb.append("From: ").append(from).append("\r\n");
        sb.append("To: ").append(to).append("\r\n");
        sb.append("Subject: ").append(subject).append("\r\n");
        sb.append("\r\n");
        sb.append(message).append("\r\n");
        sb.append(".\r\n");

        writer.write(sb.toString());
        writer.flush();

        return readServerResponse();
    }

    public SMTPResponse quit() throws IOException {
        return sendCommand("QUIT");
    }

    public void closeConnection() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    public SMTPResponse getLastResponse() {
        return lastResponse;
    }

    public String getResponse() {
        return lastResponse.getMessage();
    }

}
