package ru.bank.bot;

import com.omilia.diamant.dialog.components.fields.ApiField;
import ru.bank.bot.doatm.Office;
import ru.bank.bot.utils.OutputMap;

import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static ru.bank.bot.doatm.Actions.getOfficeById;
import static ru.bank.bot.utils.Utils.toDiamantLog;


public class Siebel {
    public static Map<String, ApiField> getCardLocations(String siebelId, DialogData dialogData) {
        OutputMap output = new OutputMap();
        Set<String> cardOffices = null;
        try {
            cardOffices = SoapRequest.getCardLocationsOfficeCode(siebelId);
        } catch (SOAPException e) {
            output.add("BEgetCardLocationsStatus", "error");
            return output.get();
        }

        if (cardOffices.size() == 0) {
            output.add("BEgetCardLocationsStatus", "no cards");
            return output.get();
        }

        int counter = 1;
        for (String entry : cardOffices) {
            try {
                Office office = getOfficeById(entry);
                output.add("BEcardLocation" + counter, office.getClearAddress());
                output.add("BEcardLocationSchedule" + counter, office.getSchedule());
                counter++;
            } catch (IOException e) {
                toDiamantLog(dialogData, "Ошибка при запросе к servicepoint-api", e);
                output.add("BEgetCardLocationsStatus", "error");
                return output.get();
            }
        }

        if (output.get().size() == 0) {
            output.add("BEgetCardLocationsStatus", "error");
        } else {
            output.add("BEgetCardLocationsStatus", "ok");
        }
        return output.get();
    }
}
