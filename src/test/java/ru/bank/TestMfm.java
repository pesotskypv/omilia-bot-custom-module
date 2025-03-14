package ru.bank;

import ru.bank.bot.Mfm;

public class TestMfm {
    public static void main(String[] args) throws Exception {
        Mfm.sendCustomMessage("00009269075781"
                , "test"
                , null);
    }
}
