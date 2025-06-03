package com.refactoring.conferUi.Utils;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardReaderListener implements Runnable {
    private final CardTerminal terminal;
    private final Consumer<String> onEmployeeIdRead;
    private volatile boolean running = true;

    public CardReaderListener(CardTerminal terminal, Consumer<String> onEmployeeIdRead) {
        this.terminal = terminal;
        this.onEmployeeIdRead = onEmployeeIdRead;
    }

    @Override
    public void run() {
        try {
            while (running) {
                terminal.waitForCardPresent(1000);
                if (!running) break;

                if (!terminal.isCardPresent()) {
                    continue;
                }

                try {
                    Card card = terminal.connect("*");
                    CardChannel channel = card.getBasicChannel();

                    if (MifareClassicReader.authenticate(card, 4) && MifareClassicReader.authenticate(card, 5)) {
                        byte[] block4 = MifareClassicReader.readBlock(channel, 4);
                        byte[] block5 = MifareClassicReader.readBlock(channel, 5);

                        byte[] combined = new byte[block4.length + block5.length];
                        System.arraycopy(block4, 0, combined, 0, block4.length);
                        System.arraycopy(block5, 0, combined, block4.length, block5.length);

                        String text = new String(combined, StandardCharsets.US_ASCII).trim();

                        Pattern p = Pattern.compile("\\d+");
                        Matcher m = p.matcher(text);
                        if (m.find()) {
                            String employeeId = m.group();
                            onEmployeeIdRead.accept(employeeId);
                        }
                    } else {
                       AlertUtils.showInfoAlert("Tente Novamente!", "Cartão retirado rapido demais!");
                    }
                    card.disconnect(false);

                } catch (CardException e) {
                    System.out.println("Erro ao acessar o cartão: " + e.getMessage());
                }

                terminal.waitForCardAbsent(1000); // aguarda até 2s

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        running = false;
    }
}