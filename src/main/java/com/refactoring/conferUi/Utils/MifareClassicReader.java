package com.refactoring.conferUi.Utils;

import javax.smartcardio.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MifareClassicReader {

    private static final byte[] DEFAULT_KEY = {
            (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF
    };

    public static String readEmployeeIdFromCard() throws Exception {
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();

        if (terminals.isEmpty()) {
            throw new Exception("Nenhum leitor NFC encontrado!");
        }

        CardTerminal terminal = terminals.get(0);
        terminal.waitForCardPresent(0);

        Card card = terminal.connect("*");
        CardChannel channel = card.getBasicChannel();

        // Autentica blocos 4 e 5
        if (!authenticate(card, 4) || !authenticate(card, 5)) {
            card.disconnect(false);
            throw new Exception("Falha na autenticação dos blocos 4 ou 5!");
        }

        byte[] block4 = readBlock(channel, 4);
        byte[] block5 = readBlock(channel, 5);

        byte[] combined = new byte[block4.length + block5.length];
        System.arraycopy(block4, 0, combined, 0, block4.length);
        System.arraycopy(block5, 0, combined, block4.length, block5.length);

        card.disconnect(false);

        String text = new String(combined, StandardCharsets.US_ASCII).trim();

        // Extrai somente números do texto
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group();
        }

        throw new Exception("Número não encontrado no cartão!");
    }

    public static boolean authenticate(Card card, int block) throws CardException {
        byte[] apdu = new byte[]{
                (byte)0xFF, (byte)0x86, 0x00, 0x00, 0x05,
                0x01, 0x00, (byte)block, 0x60, 0x00
        };

        CardChannel channel = card.getBasicChannel();
        ResponseAPDU response = channel.transmit(new CommandAPDU(apdu));
        return response.getSW1() == 0x90;
    }

    public static byte[] readBlock(CardChannel channel, int block) throws CardException {
        byte[] cmd = {(byte)0xFF, (byte)0xB0, 0x00, (byte)block, 0x10};
        ResponseAPDU response = channel.transmit(new CommandAPDU(cmd));

        if (response.getSW1() != 0x90) {
            throw new CardException("Erro ao ler bloco " + block);
        }

        return response.getData();
    }
}
