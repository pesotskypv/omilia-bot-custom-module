package ru.bank;

import com.google.gson.JsonObject;
import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.utils.OutputMap;
import ru.bank.bot.utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
    @Test
    public void outputMap2String() {
        OutputMap outputMap = new OutputMap();
        outputMap.add("aaa", "aaa");
        outputMap.add("bbb", "bbb");
        outputMap.get().put("ccc", ApiField.builder().name("bbb").status(FieldStatus.UNDEFINED).build());
        System.out.println(Utils.outputMap2String(outputMap.get()));
    }
}
