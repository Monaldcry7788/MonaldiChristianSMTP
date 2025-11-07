package monaldichristiansmtp;

public class SMTPResponseParser {
    public static SMTPResponse parse(String rawResponse) {
        String[] lines = rawResponse.trim().split("\n");
        if (lines.length == 0) {
            return null;
        }

        String firstLine = lines[0];
        int code = extractCode(firstLine);
        String message = extractMessage(firstLine);

        return new SMTPResponse(code, message, rawResponse);
    }

    public static SMTPResponse parseMultiLine(String rawResponse) {
        String[] lines = rawResponse.trim().split("\n");
        if (lines.length == 0) {
            return null;
        }

        StringBuilder fullMessage = new StringBuilder();
        int code = -1;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i == 0) {
                code = extractCode(line);
            }
            fullMessage.append(extractMessage(line));
            if (i < lines.length - 1) {
                fullMessage.append(" ");
            }
        }

        return new SMTPResponse(code, fullMessage.toString(), rawResponse);
    }

    public static int extractCode(String line) {
        if (line == null || line.length() < 3) {
            return -1;
        }
        try {
            return Integer.parseInt(line.substring(0, 3));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String extractMessage(String line) {
        if (line == null || line.length() < 4) {
            return "";
        }
        return line.substring(4);
    }

    public static boolean isSuccessCode(int code) {
        return code >= 200 && code < 300;
    }

    public static boolean isErrorCode(int code) {
        return code >= 400;
    }

    public static String getResponseType(int code) {
        if (code >= 100 && code < 200) {
            return "Informational";
        } else if (code >= 200 && code < 300) {
            return "Success";
        } else if (code >= 300 && code < 400) {
            return "Intermediate";
        } else if (code >= 400 && code < 500) {
            return "Temporary Error";
        } else if (code >= 500 && code < 600) {
            return "Permanent Error";
        }
        return "Unknown";
    }
}